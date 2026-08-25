package com.example.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.di.ServiceLocator
import com.example.domain.repository.AdminRecord
import com.example.domain.repository.EligibleCaptain
import com.example.core.result.DrovaResult
import com.example.domain.repository.AdminRepository
import com.example.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AdminSection(val titleAr: String, val titleEn: String) {
    OVERVIEW("نظرة عامة", "Overview"),
    ORDERS("الطلبات", "Orders"),
    RESTAURANTS("المطاعم", "Restaurants"),
    CAPTAINS("الكباتن", "Captains"),
    ASSIGNMENTS("طلبات التعيين", "Assignment Requests"),
    ROLE_REQUESTS("طلبات اعتماد الأدوار", "Role Approval Requests"),
    USERS("المستخدمون", "Users"),
    FINANCE("المالية", "Finance"),
    SETTINGS("الإعدادات", "Settings")
}

sealed interface AdminAccessState {
    data object Loading : AdminAccessState
    data object Authorized : AdminAccessState
    data object Denied : AdminAccessState
}

sealed interface AdminOperationState {
    data object Idle : AdminOperationState
    data object Loading : AdminOperationState
    data class Success(val message: String) : AdminOperationState
    data class Failure(val message: String) : AdminOperationState
}

class AdminViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val adminRepository: AdminRepository = ServiceLocator.adminRepository
) : ViewModel() {
    private val _accessState = MutableStateFlow<AdminAccessState>(AdminAccessState.Loading)
    val accessState: StateFlow<AdminAccessState> = _accessState.asStateFlow()
    val orders: StateFlow<List<AdminRecord>> = adminRepository.orders
    val restaurants: StateFlow<List<AdminRecord>> = adminRepository.restaurants
    val captains: StateFlow<List<AdminRecord>> = adminRepository.captains
    val users: StateFlow<List<AdminRecord>> = adminRepository.users
    val assignmentRequests: StateFlow<List<AdminRecord>> = adminRepository.assignmentRequests
    val roleRequests: StateFlow<List<AdminRecord>> = adminRepository.roleRequests
    val lastError: StateFlow<String?> = adminRepository.lastError

    private val _selectedSection = MutableStateFlow(AdminSection.OVERVIEW)
    val selectedSection: StateFlow<AdminSection> = _selectedSection.asStateFlow()

    private val _selectedRecord = MutableStateFlow<AdminRecord?>(null)
    val selectedRecord: StateFlow<AdminRecord?> = _selectedRecord.asStateFlow()

    private val _proofImageUrl = MutableStateFlow<String?>(null)
    val proofImageUrl: StateFlow<String?> = _proofImageUrl.asStateFlow()

    private val _operationState = MutableStateFlow<AdminOperationState>(AdminOperationState.Idle)
    val operationState: StateFlow<AdminOperationState> = _operationState.asStateFlow()

    init {
        verifyAndStart()
        viewModelScope.launch {
            adminRepository.assignmentRequests.collect { records ->
                val currentId = _selectedRecord.value?.id ?: return@collect
                records.firstOrNull { it.id == currentId }?.let { _selectedRecord.value = it }
            }
        }
        viewModelScope.launch {
            adminRepository.roleRequests.collect { records ->
                val currentId = _selectedRecord.value?.id ?: return@collect
                records.firstOrNull { it.id == currentId }?.let { _selectedRecord.value = it }
            }
        }
    }

    fun verifyAndStart() {
        _accessState.value = AdminAccessState.Loading
        viewModelScope.launch {
            if (authRepository.hasAdminClaim()) {
                _accessState.value = AdminAccessState.Authorized
                adminRepository.startRealtime()
            } else {
                adminRepository.stopRealtime()
                _accessState.value = AdminAccessState.Denied
            }
        }
    }

    fun eligibleCaptains(): List<EligibleCaptain> = captains.value.mapNotNull { record ->
        val approved = record.text("approvalStatus")?.uppercase() == "APPROVED"
        val suspended = record.fields["suspended"] == true
        val enabled = record.fields["enabledForReceivingOrders"] == true
        if (!approved || suspended || !enabled) return@mapNotNull null
        EligibleCaptain(record.id, record.text("name") ?: record.text("fullName") ?: record.id, record)
    }

    fun approveAssignment(requestId: String, selectedCaptainIds: List<String>) {
        runAssignmentOperation {
            val adminUid = authRepository.firebaseUid.value.orEmpty()
            adminRepository.approveAssignmentRequest(requestId, selectedCaptainIds, adminUid)
        }
    }

    fun rejectAssignment(requestId: String, reason: String) {
        runAssignmentOperation {
            val adminUid = authRepository.firebaseUid.value.orEmpty()
            adminRepository.rejectAssignmentRequest(requestId, reason, adminUid)
        }
    }

    fun approveRoleRequest(requestId: String) {
        runAssignmentOperation {
            adminRepository.approveRoleRequest(requestId, authRepository.firebaseUid.value.orEmpty())
        }
    }

    fun rejectRoleRequest(requestId: String, reason: String) {
        runAssignmentOperation {
            adminRepository.rejectRoleRequest(requestId, reason, authRepository.firebaseUid.value.orEmpty())
        }
    }

    fun clearOperationState() {
        _operationState.value = AdminOperationState.Idle
    }

    private fun runAssignmentOperation(operation: suspend () -> DrovaResult<Unit>) {
        _operationState.value = AdminOperationState.Loading
        viewModelScope.launch {
            when (val result = operation()) {
                is DrovaResult.Success -> _operationState.value = AdminOperationState.Success("تم تنفيذ العملية بنجاح.")
                is DrovaResult.Error -> _operationState.value = AdminOperationState.Failure(result.messageAr)
                DrovaResult.Loading -> _operationState.value = AdminOperationState.Loading
            }
        }
    }

    fun selectSection(section: AdminSection) {
        _selectedSection.value = section
        _selectedRecord.value = null
    }

    fun openRecord(record: AdminRecord) {
        _selectedRecord.value = record
        _proofImageUrl.value = null
        val storagePath = record.nested("pickupProof.storagePath")?.toString()
            ?: record.text("storagePath")
        if (!storagePath.isNullOrBlank()) {
            viewModelScope.launch {
                _proofImageUrl.value = (adminRepository.pickupProofDownloadUrl(storagePath) as? DrovaResult.Success)?.data
            }
        }
    }

    fun closeRecord() {
        _selectedRecord.value = null
        _proofImageUrl.value = null
    }

    override fun onCleared() {
        adminRepository.stopRealtime()
        super.onCleared()
    }
}
