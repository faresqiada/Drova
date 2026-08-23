package com.example.data.pickupproof

import android.content.Context
import android.net.Uri
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.PickupProof
import com.example.domain.model.PickupProofConfirmation
import com.example.domain.model.PickupProofFailure
import com.example.domain.model.PickupProofValidationStatus
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

/**
 * Camera proof flow: local image validation/OCR -> Storage upload -> Firestore transaction.
 * The Firestore transaction is the authoritative gate for PICKED_UP.
 */
class FirebasePickupProofService(
    context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val validator: PickupProofValidator = PickupProofValidator(context.applicationContext)
) : PickupProofService {
    override suspend fun confirmPickup(
        order: Order,
        captainId: String,
        imageFile: File
    ): PickupProofConfirmation {
        if (order.status != OrderStatus.CAPTAIN_ASSIGNED) {
            return failure(PickupProofFailure.ORDER_NOT_IN_ASSIGNED_STATE, "الطلب لم يعد في مرحلة التعيين.", "The order is no longer assigned for pickup.")
        }
        if (order.captainId != captainId) {
            return failure(PickupProofFailure.CAPTAIN_NOT_ASSIGNED, "هذا الطلب غير مسند إلى الكابتن الحالي.", "This order is not assigned to the current captain.")
        }

        val proofId = UUID.randomUUID().toString()
        val storagePath = "orders/${safePathSegment(order.id)}/pickup-proof/${safePathSegment(captainId)}/$proofId.jpg"
        val storageReference = storage.reference.child(storagePath)
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCustomMetadata("orderId", order.id)
            .setCustomMetadata("captainId", captainId)
            .setCustomMetadata("proofId", proofId)
            .build()

        val validation = try {
            validator.validate(imageFile, order.id, order.orderNumber)
        } catch (_: Exception) {
            return failure(PickupProofFailure.IMAGE_NOT_READABLE, "تعذر فحص الصورة.", "The image could not be validated.")
        }
        if (validation is LocalValidationResult.Invalid) {
            return PickupProofConfirmation.Failure(validation.reason, validation.messageAr, validation.messageEn)
        }
        validation as LocalValidationResult.Valid

        val proof = PickupProof(
                proofId = proofId,
                orderId = order.id,
                captainId = captainId,
                storagePath = storagePath,
                capturedAt = System.currentTimeMillis(),
                imageWidth = validation.width,
                imageHeight = validation.height,
                fileSizeBytes = validation.fileSizeBytes,
                contentType = "image/jpeg",
                hasReceiptText = validation.hasReceiptText,
                orderIdentifierMatched = validation.orderIdentifierMatched,
                validationStatus = PickupProofValidationStatus.VALIDATED,
                validationMessage = "Local OCR validation passed."
            )

        return try {
            storageReference.putFile(Uri.fromFile(imageFile), metadata).await()
            try {
                firestore.runTransaction { transaction ->
                    val orderReference = firestore.collection("orders").document(order.id)
                    val proofReference = orderReference.collection("pickupProofs").document(proofId)
                    val currentOrder = transaction.get(orderReference)
                    val currentStatus = currentOrder.getString("status")
                    val assignedCaptainId = currentOrder.getString("captainId")
                    val existingProofId = currentOrder.getString("pickupProofId")

                    if (currentStatus == OrderStatus.PICKED_UP.name || !existingProofId.isNullOrBlank()) {
                        throw PickupProofTransactionException(PickupProofFailure.DUPLICATE_CONFIRMATION)
                    }
                    if (currentStatus != OrderStatus.CAPTAIN_ASSIGNED.name) {
                        throw PickupProofTransactionException(PickupProofFailure.ORDER_NOT_IN_ASSIGNED_STATE)
                    }
                    if (assignedCaptainId != captainId) {
                        throw PickupProofTransactionException(PickupProofFailure.CAPTAIN_NOT_ASSIGNED)
                    }

                    transaction.set(proofReference, proof.toFirestoreMap())
                    transaction.update(
                        orderReference,
                        mapOf(
                            "status" to OrderStatus.PICKED_UP.name,
                            "pickupProofId" to proofId,
                            "pickupProof" to proof.toFirestoreMap(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    null
                }.await()
                PickupProofConfirmation.Success(proof)
            } catch (error: PickupProofTransactionException) {
                storageReference.deleteSafely()
                failure(error.reason, error.messageAr, error.messageEn)
            } catch (error: FirebaseFirestoreException) {
                storageReference.deleteSafely()
                failure(PickupProofFailure.SECURITY_RULES_REJECTED, "رفض Firebase العملية أو تعذر الاتصال به.", "Firebase rejected the operation or could not be reached.")
            } catch (error: Exception) {
                storageReference.deleteSafely()
                failure(PickupProofFailure.NETWORK_ERROR, "فشل تأكيد الاستلام. لم يتم تغيير حالة الطلب.", "Pickup confirmation failed. The order state was not changed.")
            }
        } catch (error: Exception) {
            failure(PickupProofFailure.UPLOAD_FAILED, "فشل رفع صورة الإثبات.", "The proof image upload failed.")
        }
    }

    private fun failure(reason: PickupProofFailure, messageAr: String, messageEn: String) =
        PickupProofConfirmation.Failure(reason, messageAr, messageEn)

    private fun safePathSegment(value: String): String = value
        .trim()
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .ifBlank { "unknown" }

    private fun PickupProof.toFirestoreMap(): Map<String, Any> = mapOf(
        "proofId" to proofId,
        "orderId" to orderId,
        "captainId" to captainId,
        "storagePath" to storagePath,
        "capturedAt" to capturedAt,
        "imageWidth" to imageWidth,
        "imageHeight" to imageHeight,
        "fileSizeBytes" to fileSizeBytes,
        "contentType" to contentType,
        "hasReceiptText" to hasReceiptText,
        "orderIdentifierMatched" to orderIdentifierMatched,
        "validationStatus" to validationStatus.name,
        "validationMessage" to validationMessage
    )

    private suspend fun StorageReference.deleteSafely() {
        try { delete().await() } catch (_: Exception) { }
    }

    private class PickupProofTransactionException(
        val reason: PickupProofFailure
    ) : IllegalStateException() {
        val messageAr: String = when (reason) {
            PickupProofFailure.DUPLICATE_CONFIRMATION -> "تم تأكيد استلام الطلب مسبقاً."
            PickupProofFailure.CAPTAIN_NOT_ASSIGNED -> "هذا الطلب غير مسند إلى الكابتن الحالي."
            else -> "لم يعد الطلب في الحالة المناسبة لتأكيد الاستلام."
        }
        val messageEn: String = when (reason) {
            PickupProofFailure.DUPLICATE_CONFIRMATION -> "Pickup was already confirmed."
            PickupProofFailure.CAPTAIN_NOT_ASSIGNED -> "The order is not assigned to the current captain."
            else -> "The order is no longer in the correct state for pickup confirmation."
        }
    }
}
