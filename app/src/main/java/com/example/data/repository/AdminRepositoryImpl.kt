package com.example.data.repository

import com.example.core.result.DrovaError
import com.example.core.result.DrovaResult
import com.example.domain.repository.AdminAssignmentPolicy
import com.example.domain.repository.AdminRecord
import com.example.domain.repository.AdminRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AdminRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : AdminRepository {
    private val _orders = MutableStateFlow<List<AdminRecord>>(emptyList())
    private val _restaurants = MutableStateFlow<List<AdminRecord>>(emptyList())
    private val _captains = MutableStateFlow<List<AdminRecord>>(emptyList())
    private val _users = MutableStateFlow<List<AdminRecord>>(emptyList())
    private val _assignmentRequests = MutableStateFlow<List<AdminRecord>>(emptyList())
    private val _roleRequests = MutableStateFlow<List<AdminRecord>>(emptyList())
    private val _lastError = MutableStateFlow<String?>(null)
    private val registrations = mutableListOf<ListenerRegistration>()

    override val orders: StateFlow<List<AdminRecord>> = _orders.asStateFlow()
    override val restaurants: StateFlow<List<AdminRecord>> = _restaurants.asStateFlow()
    override val captains: StateFlow<List<AdminRecord>> = _captains.asStateFlow()
    override val users: StateFlow<List<AdminRecord>> = _users.asStateFlow()
    override val assignmentRequests: StateFlow<List<AdminRecord>> = _assignmentRequests.asStateFlow()
    override val roleRequests: StateFlow<List<AdminRecord>> = _roleRequests.asStateFlow()
    override val lastError: StateFlow<String?> = _lastError.asStateFlow()

    @Synchronized
    override fun startRealtime() {
        stopRealtime()
        watch("orders") { _orders.value = it }
        watch("restaurants") { _restaurants.value = it }
        watch("captains") { _captains.value = it }
        watch("users") { _users.value = it }
        watch("captain_assignment_requests") { _assignmentRequests.value = it }
        watch("role_requests") { _roleRequests.value = it }
    }

    @Synchronized
    override fun stopRealtime() {
        registrations.forEach { it.remove() }
        registrations.clear()
    }

    override suspend fun pickupProofDownloadUrl(storagePath: String): DrovaResult<String> = try {
        if (storagePath.isBlank()) {
            return DrovaResult.Error(
                error = DrovaError.Storage.ReadError,
                messageAr = "مسار إثبات الاستلام غير متاح.",
                messageEn = "Pickup proof storage path is unavailable."
            )
        }
        DrovaResult.Success(storage.reference.child(storagePath).downloadUrl.await().toString())
    } catch (error: Exception) {
        DrovaResult.Error(
            error = DrovaError.Storage.ReadError,
            messageAr = "تعذر قراءة صورة إثبات الاستلام.",
            messageEn = "Could not read the pickup proof image.",
            cause = error
        )
    }

    override suspend fun approveAssignmentRequest(
        requestId: String,
        selectedCaptainIds: List<String>,
        adminUid: String
    ): DrovaResult<Unit> {
        if (requestId.isBlank() || adminUid.isBlank() || selectedCaptainIds.isEmpty() || selectedCaptainIds.size != selectedCaptainIds.distinct().size) {
            return assignmentError("اختيار الكباتن غير صالح.", "Captain selection is invalid.")
        }
        return try {
            firestore.runTransaction { transaction ->
                val requestReference = firestore.collection("captain_assignment_requests").document(requestId)
                val requestSnapshot = transaction.get(requestReference)
                val requestStatus = requestSnapshot.getString("status")
                val requestedCount = requestSnapshot.getLong("requestedCaptainCount")?.toInt()
                if (!AdminAssignmentPolicy.canApprove(requestStatus, requestedCount, selectedCaptainIds)) {
                    throw AssignmentTransactionException("الطلب أو اختيار الكباتن غير صالح.", "The request or captain selection is invalid.")
                }
                val restaurantId = requestSnapshot.getString("restaurantId")
                    ?: throw AssignmentTransactionException("المطعم المرتبط بالطلب غير متاح.", "Restaurant ID is missing.")
                val assignmentReads = selectedCaptainIds.map { captainId ->
                    val captainReference = firestore.collection("captains").document(captainId)
                    val assignmentReference = firestore.collection("assignments").document(AdminAssignmentPolicy.stableAssignmentId(requestId, captainId))
                    Triple(captainId, transaction.get(captainReference), transaction.get(assignmentReference))
                }
                assignmentReads.forEach { (captainId, captainSnapshot, assignmentSnapshot) ->
                    val approval = captainSnapshot.getString("approvalStatus")?.uppercase()
                    val suspended = captainSnapshot.getBoolean("suspended") == true
                    val enabled = captainSnapshot.getBoolean("enabledForReceivingOrders") == true
                    if (!captainSnapshot.exists() || !AdminAssignmentPolicy.isEligible(approval, suspended, enabled)) {
                        throw AssignmentTransactionException("الكابتن $captainId غير مؤهل حاليًا.", "Captain $captainId is not currently eligible.")
                    }
                    val existingRequestId = assignmentSnapshot.getString("requestId")
                    val existingStatus = assignmentSnapshot.getString("status")?.uppercase()
                    if (!existingRequestId.isNullOrBlank() && existingRequestId != requestId && existingStatus !in setOf("COMPLETED", "CANCELLED", "REJECTED")) {
                        throw AssignmentTransactionException("يوجد تعيين نشط لهذا الكابتن.", "The captain already has an active assignment.")
                    }
                }
                assignmentReads.forEach { (captainId, _, assignmentSnapshot) ->
                    if (assignmentSnapshot.getString("requestId") != requestId) {
                        val assignmentReference = firestore.collection("assignments").document(AdminAssignmentPolicy.stableAssignmentId(requestId, captainId))
                        transaction.set(assignmentReference, mapOf(
                            "assignmentId" to AdminAssignmentPolicy.stableAssignmentId(requestId, captainId),
                            "requestId" to requestId,
                            "restaurantId" to restaurantId,
                            "captainId" to captainId,
                            "status" to "ACTIVE",
                            "assignedByAdminUid" to adminUid,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        ))
                    }
                }
                transaction.update(requestReference, mapOf(
                    "status" to "APPROVED",
                    "selectedCaptainIds" to selectedCaptainIds,
                    "dedicatedCaptainIds" to selectedCaptainIds,
                    "approvedByAdminUid" to adminUid,
                    "approvedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
                null
            }.await()
            DrovaResult.Success(Unit)
        } catch (error: AssignmentTransactionException) {
            assignmentError(error.messageAr, error.messageEn)
        } catch (error: FirebaseFirestoreException) {
            assignmentError("رفض Firebase العملية أو تعذر الاتصال به.", "Firebase rejected the operation or could not be reached.", error)
        } catch (error: Exception) {
            assignmentError("فشل اعتماد طلب التعيين دون تغيير جزئي.", "Assignment approval failed without partial changes.", error)
        }
    }

    override suspend fun rejectAssignmentRequest(
        requestId: String,
        reason: String,
        adminUid: String
    ): DrovaResult<Unit> {
        if (requestId.isBlank() || adminUid.isBlank() || reason.trim().isBlank()) {
            return assignmentError("سبب الرفض مطلوب.", "A rejection reason is required.")
        }
        return try {
            firestore.runTransaction { transaction ->
                val requestReference = firestore.collection("captain_assignment_requests").document(requestId)
                val requestSnapshot = transaction.get(requestReference)
                if (requestSnapshot.getString("status")?.uppercase() != "PENDING") {
                    throw AssignmentTransactionException("طلب التعيين لم يعد معلقًا.", "The assignment request is no longer pending.")
                }
                transaction.update(requestReference, mapOf(
                    "status" to "REJECTED",
                    "rejectedByAdminUid" to adminUid,
                    "rejectedAt" to FieldValue.serverTimestamp(),
                    "rejectionReason" to reason.trim(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
                null
            }.await()
            DrovaResult.Success(Unit)
        } catch (error: AssignmentTransactionException) {
            assignmentError(error.messageAr, error.messageEn)
        } catch (error: FirebaseFirestoreException) {
            assignmentError("رفض Firebase العملية أو تعذر الاتصال به.", "Firebase rejected the operation or could not be reached.", error)
        } catch (error: Exception) {
            assignmentError("فشل رفض طلب التعيين دون تغيير جزئي.", "Assignment rejection failed without partial changes.", error)
        }
    }

    override suspend fun approveRoleRequest(requestId: String, adminUid: String): DrovaResult<Unit> {
        if (requestId.isBlank() || adminUid.isBlank()) {
            return roleRequestError("بيانات اعتماد الطلب غير مكتملة.", "Role request approval data is incomplete.")
        }
        return try {
            firestore.runTransaction { transaction ->
                val requestReference = firestore.collection("role_requests").document(requestId)
                val requestSnapshot = transaction.get(requestReference)
                if (!requestSnapshot.exists() || requestSnapshot.getString("status")?.uppercase() != "PENDING") {
                    throw RoleRequestTransactionException("طلب الاعتماد غير موجود أو تمت معالجته.", "The role request is missing or already processed.")
                }
                val requestedRole = requestSnapshot.getString("requestedRole")?.uppercase()
                if (requestedRole !in setOf("CAPTAIN", "RESTAURANT")) {
                    throw RoleRequestTransactionException("الدور المطلوب غير صالح.", "The requested role is invalid.")
                }
                val userId = requestSnapshot.getString("requesterUid")
                    ?: throw RoleRequestTransactionException("معرّف المستخدم غير موجود.", "The requester UID is missing.")
                val userReference = firestore.collection("users").document(userId)
                transaction.set(userReference, mapOf(
                    "role" to requestedRole,
                    "email" to (requestSnapshot.getString("email") ?: ""),
                    "full_name" to (requestSnapshot.getString("fullName") ?: ""),
                    "updatedAt" to FieldValue.serverTimestamp()
                ), com.google.firebase.firestore.SetOptions.merge())
                transaction.update(requestReference, mapOf(
                    "status" to "APPROVED",
                    "approvedByAdminUid" to adminUid,
                    "approvedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
                null
            }.await()
            DrovaResult.Success(Unit)
        } catch (error: RoleRequestTransactionException) {
            roleRequestError(error.messageAr, error.messageEn)
        } catch (error: FirebaseFirestoreException) {
            roleRequestError("رفض Firebase العملية أو تعذر الاتصال به.", "Firebase rejected the operation or could not be reached.", error)
        } catch (error: Exception) {
            roleRequestError("فشل اعتماد الطلب دون تغيير جزئي.", "Role request approval failed without partial changes.", error)
        }
    }

    override suspend fun rejectRoleRequest(requestId: String, reason: String, adminUid: String): DrovaResult<Unit> {
        if (requestId.isBlank() || adminUid.isBlank() || reason.trim().isBlank()) {
            return roleRequestError("سبب الرفض مطلوب.", "A rejection reason is required.")
        }
        return try {
            firestore.runTransaction { transaction ->
                val requestReference = firestore.collection("role_requests").document(requestId)
                val requestSnapshot = transaction.get(requestReference)
                if (!requestSnapshot.exists() || requestSnapshot.getString("status")?.uppercase() != "PENDING") {
                    throw RoleRequestTransactionException("طلب الاعتماد غير موجود أو تمت معالجته.", "The role request is missing or already processed.")
                }
                transaction.update(requestReference, mapOf(
                    "status" to "REJECTED",
                    "rejectedByAdminUid" to adminUid,
                    "rejectedAt" to FieldValue.serverTimestamp(),
                    "rejectionReason" to reason.trim(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
                null
            }.await()
            DrovaResult.Success(Unit)
        } catch (error: RoleRequestTransactionException) {
            roleRequestError(error.messageAr, error.messageEn)
        } catch (error: FirebaseFirestoreException) {
            roleRequestError("رفض Firebase العملية أو تعذر الاتصال به.", "Firebase rejected the operation or could not be reached.", error)
        } catch (error: Exception) {
            roleRequestError("فشل رفض الطلب دون تغيير جزئي.", "Role request rejection failed without partial changes.", error)
        }
    }

    private fun roleRequestError(messageAr: String, messageEn: String, cause: Throwable? = null): DrovaResult.Error =
        DrovaResult.Error(DrovaError.Domain.ValidationFailed("role_request", messageEn), messageAr, messageEn, cause)

    private class RoleRequestTransactionException(
        val messageAr: String,
        val messageEn: String
    ) : IllegalStateException()

    private fun assignmentError(messageAr: String, messageEn: String, cause: Throwable? = null): DrovaResult.Error =
        DrovaResult.Error(DrovaError.Domain.ValidationFailed("assignment", messageEn), messageAr, messageEn, cause)

    private class AssignmentTransactionException(
        val messageAr: String,
        val messageEn: String
    ) : IllegalStateException()

    private fun watch(collection: String, onData: (List<AdminRecord>) -> Unit) {
        val registration = firestore.collection(collection).addSnapshotListener { snapshot, error ->
            if (error != null) {
                _lastError.value = "${collection}: ${error.message ?: "Firestore listener failed"}"
                return@addSnapshotListener
            }
            onData(snapshot?.documents.orEmpty().map { document ->
                AdminRecord(document.id, document.data.orEmpty())
            })
            _lastError.value = null
        }
        registrations += registration
    }
}
