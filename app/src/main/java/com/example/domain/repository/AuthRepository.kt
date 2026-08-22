package com.example.domain.repository

import com.example.domain.model.CaptainMode
import com.example.domain.model.User
import com.example.domain.model.UserRole
import kotlinx.coroutines.flow.StateFlow

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val messageAr: String, val messageEn: String) : AuthResult()
}

interface AuthRepository {
    val currentUser: StateFlow<User?>
    val selectedRole: StateFlow<UserRole>

    fun setSelectedRole(role: UserRole)
    suspend fun login(phoneOrEmail: String, pinOrPassword: String): AuthResult
    suspend fun registerCustomer(fullName: String, phone: String, city: String, district: String): AuthResult
    suspend fun registerRestaurant(businessName: String, managerName: String, phone: String, commercialRegister: String, address: String): AuthResult
    suspend fun registerCaptain(fullName: String, phone: String, nationalId: String, vehicleType: String, captainMode: CaptainMode): AuthResult
    suspend fun logout()
    fun quickSwitchRole(role: UserRole)
}
