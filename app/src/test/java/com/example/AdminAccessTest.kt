package com.example

import android.app.Activity
import com.example.core.result.DrovaResult
import com.example.domain.model.CaptainMode
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.AdminRecord
import com.example.domain.repository.AdminRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult
import com.example.presentation.admin.AdminAccessState
import com.example.presentation.admin.AdminViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminAccessTest {
    @Test
    fun `non admin claim is denied and realtime listeners do not start`() = runBlocking {
        val repository = FakeAdminRepository()
        val viewModel = AdminViewModel(FakeAuthRepository(admin = false), repository)
        delay(100)
        assertTrue(viewModel.accessState.value is AdminAccessState.Denied)
        assertFalse(repository.started)
    }

    @Test
    fun `verified admin claim authorizes dashboard and starts realtime listeners`() = runBlocking {
        val repository = FakeAdminRepository()
        val viewModel = AdminViewModel(FakeAuthRepository(admin = true), repository)
        delay(100)
        assertTrue(viewModel.accessState.value is AdminAccessState.Authorized)
        assertTrue(repository.started)
    }
}

private class FakeAdminRepository : AdminRepository {
    private val empty = MutableStateFlow<List<AdminRecord>>(emptyList())
    override val orders: StateFlow<List<AdminRecord>> = empty
    override val restaurants: StateFlow<List<AdminRecord>> = empty
    override val captains: StateFlow<List<AdminRecord>> = empty
    override val users: StateFlow<List<AdminRecord>> = empty
    override val assignmentRequests: StateFlow<List<AdminRecord>> = empty
    override val roleRequests: StateFlow<List<AdminRecord>> = empty
    override val lastError: StateFlow<String?> = MutableStateFlow(null)
    var started = false

    override fun startRealtime() { started = true }
    override fun stopRealtime() { started = false }
    override suspend fun pickupProofDownloadUrl(storagePath: String): DrovaResult<String> = DrovaResult.Success(storagePath)
    override suspend fun approveAssignmentRequest(requestId: String, selectedCaptainIds: List<String>, adminUid: String): DrovaResult<Unit> = DrovaResult.Success(Unit)
    override suspend fun rejectAssignmentRequest(requestId: String, reason: String, adminUid: String): DrovaResult<Unit> = DrovaResult.Success(Unit)
    override suspend fun approveRoleRequest(requestId: String, adminUid: String): DrovaResult<Unit> = DrovaResult.Success(Unit)
    override suspend fun rejectRoleRequest(requestId: String, reason: String, adminUid: String): DrovaResult<Unit> = DrovaResult.Success(Unit)
}

private class FakeAuthRepository(private val admin: Boolean) : AuthRepository {
    override val currentUser: StateFlow<User?> = MutableStateFlow(null)
    override val firebaseUid: StateFlow<String?> = MutableStateFlow(null)
    override val selectedRole: StateFlow<UserRole> = MutableStateFlow(UserRole.CUSTOMER)
    override fun setSelectedRole(role: UserRole) = Unit
    override suspend fun hasAdminClaim(): Boolean = admin
    override suspend fun login(phoneOrEmail: String, pinOrPassword: String): AuthResult = AuthResult.Error("", "")
    override suspend fun signInWithGoogle(activity: Activity): AuthResult = AuthResult.Error("", "")
    override suspend fun registerCustomer(fullName: String, phone: String, city: String, district: String): AuthResult = AuthResult.Error("", "")
    override suspend fun registerRestaurant(businessName: String, managerName: String, phone: String, commercialRegister: String, address: String): AuthResult = AuthResult.Error("", "")
    override suspend fun registerCaptain(fullName: String, phone: String, nationalId: String, vehicleType: String, captainMode: CaptainMode): AuthResult = AuthResult.Error("", "")
    override suspend fun logout() = Unit
    override fun quickSwitchRole(role: UserRole) = Unit
}

