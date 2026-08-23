package com.example.domain.repository

import com.example.core.result.DrovaResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Read-oriented Admin data contract. Authorization is enforced before listeners start
 * and again by Firestore rules; this interface never grants client-side claim mutation.
 */
interface AdminRepository {
    val orders: StateFlow<List<AdminRecord>>
    val restaurants: StateFlow<List<AdminRecord>>
    val captains: StateFlow<List<AdminRecord>>
    val users: StateFlow<List<AdminRecord>>
    val assignmentRequests: StateFlow<List<AdminRecord>>
    val lastError: StateFlow<String?>

    fun startRealtime()
    fun stopRealtime()
    suspend fun pickupProofDownloadUrl(storagePath: String): DrovaResult<String>
    suspend fun approveAssignmentRequest(
        requestId: String,
        selectedCaptainIds: List<String>,
        adminUid: String
    ): DrovaResult<Unit>
    suspend fun rejectAssignmentRequest(
        requestId: String,
        reason: String,
        adminUid: String
    ): DrovaResult<Unit>
}

data class EligibleCaptain(
    val id: String,
    val name: String,
    val record: AdminRecord
)

data class AdminRecord(
    val id: String,
    val fields: Map<String, Any?>
) {
    fun text(key: String): String? = fields[key]?.toString()?.takeIf { it.isNotBlank() }
    fun number(key: String): Double? = when (val value = fields[key]) {
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull()
        else -> null
    }
    fun nested(path: String): Any? = path.split('.').fold<Any?>(fields) { current, part ->
        (current as? Map<*, *>)?.get(part)
    }
}
