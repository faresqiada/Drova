package com.example.data.pickupproof

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.domain.model.PickupProofFailure
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Locale

/**
 * Performs only checks that can be supported locally and deterministically.
 * OCR can confirm receipt-like text and an order identifier; it cannot prove
 * that the physical food order is beside the receipt in the same frame.
 */
class PickupProofValidator(private val context: Context) {
    companion object {
        const val MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L
        const val MIN_FILE_SIZE_BYTES = 8L * 1024L
        const val MIN_IMAGE_WIDTH = 640
        const val MIN_IMAGE_HEIGHT = 480
        private val RECEIPT_HINTS = listOf(
            "invoice", "receipt", "order", "total", "egp", "فاتورة", "إيصال", "طلب", "الإجمالي", "جنيه", "ج.م"
        ).map { it.lowercase(Locale.ROOT) }
    }

    suspend fun validate(
        imageFile: File,
        orderId: String,
        orderNumber: String
    ): LocalValidationResult {
        if (!imageFile.exists() || !imageFile.isFile) {
            return invalid(PickupProofFailure.IMAGE_NOT_READABLE, "تعذر قراءة ملف الصورة.", "The image file could not be read.")
        }
        if (imageFile.length() < MIN_FILE_SIZE_BYTES) {
            return invalid(PickupProofFailure.IMAGE_TOO_SMALL, "الصورة صغيرة أو فارغة.", "The image is too small or empty.")
        }
        if (imageFile.length() > MAX_FILE_SIZE_BYTES) {
            return invalid(PickupProofFailure.IMAGE_TOO_LARGE, "حجم الصورة أكبر من الحد المسموح.", "The image exceeds the allowed size.")
        }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(imageFile.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return invalid(PickupProofFailure.IMAGE_NOT_READABLE, "الصورة تالفة أو غير قابلة للقراءة.", "The image is corrupt or unreadable.")
        }
        if (bounds.outWidth < MIN_IMAGE_WIDTH || bounds.outHeight < MIN_IMAGE_HEIGHT) {
            return invalid(PickupProofFailure.IMAGE_TOO_SMALL, "دقة الصورة غير كافية. التقط الصورة كاملة وبوضوح.", "Image resolution is insufficient. Capture the full receipt clearly.")
        }

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return try {
            val result = recognizer.process(InputImage.fromFilePath(context, Uri.fromFile(imageFile))).await()
            val normalizedText = normalize(result.text.orEmpty())
            val normalizedOrderNumber = normalize(orderNumber)
            val normalizedOrderId = normalize(orderId)
            val orderIdentifierMatched = (normalizedOrderNumber.isNotBlank() && normalizedText.contains(normalizedOrderNumber)) ||
                (normalizedOrderId.isNotBlank() && normalizedText.contains(normalizedOrderId))
            val hasReceiptText = RECEIPT_HINTS.any { normalizedText.contains(normalize(it)) }

            when {
                !hasReceiptText -> invalid(PickupProofFailure.RECEIPT_TEXT_NOT_FOUND, "لم يتم العثور على بيانات كافية في الفاتورة.", "No sufficient receipt text was found.")
                !orderIdentifierMatched -> invalid(PickupProofFailure.ORDER_IDENTIFIER_NOT_FOUND, "رقم الطلب غير مطابق.", "The order identifier does not match.")
                else -> LocalValidationResult.Valid(
                    width = bounds.outWidth,
                    height = bounds.outHeight,
                    fileSizeBytes = imageFile.length(),
                    hasReceiptText = true,
                    orderIdentifierMatched = true
                )
            }
        } catch (error: Exception) {
            invalid(PickupProofFailure.IMAGE_NOT_READABLE, "تعذر فحص نص الصورة. أعد التصوير بإضاءة ووضوح أفضل.", "The image text could not be checked. Retake it with better lighting and focus.")
        } finally {
            recognizer.close()
        }
    }

    private fun invalid(reason: PickupProofFailure, messageAr: String, messageEn: String) =
        LocalValidationResult.Invalid(reason, messageAr, messageEn)

    private fun normalize(value: String): String = value
        .lowercase(Locale.ROOT)
        .replace(Regex("[^\\p{L}\\p{N}]"), "")
}

sealed interface LocalValidationResult {
    data class Valid(
        val width: Int,
        val height: Int,
        val fileSizeBytes: Long,
        val hasReceiptText: Boolean,
        val orderIdentifierMatched: Boolean
    ) : LocalValidationResult

    data class Invalid(
        val reason: PickupProofFailure,
        val messageAr: String,
        val messageEn: String
    ) : LocalValidationResult
}
