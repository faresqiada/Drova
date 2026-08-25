package com.example.presentation.auth

import android.app.Activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.di.ServiceLocator
import com.example.domain.model.CaptainMode
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class PendingApproval(val messageAr: String, val messageEn: String) : AuthUiState
    data class Error(val messageAr: String, val messageEn: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository
) : ViewModel() {

    val selectedRole: StateFlow<UserRole> = authRepository.selectedRole
    val currentUser: StateFlow<User?> = authRepository.currentUser
    val firebaseUid: StateFlow<String?> = authRepository.firebaseUid

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun selectRole(role: UserRole) {
        authRepository.setSelectedRole(role)
        _uiState.value = AuthUiState.Idle
    }

    fun quickSwitchRole(role: UserRole) {
        authRepository.quickSwitchRole(role)
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.signInWithGoogle(activity)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.user)
                is AuthResult.PendingApproval -> _uiState.value = AuthUiState.PendingApproval(result.messageAr, result.messageEn)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.messageAr, result.messageEn)
            }
        }
    }

    fun completePhoneSignIn(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.completePhoneSignIn(firebaseUser)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.user)
                is AuthResult.PendingApproval -> _uiState.value = AuthUiState.PendingApproval(result.messageAr, result.messageEn)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.messageAr, result.messageEn)
            }
        }
    }

    fun login(phoneOrEmail: String, pinOrPassword: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.login(phoneOrEmail, pinOrPassword)) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState.Success(result.user)
                }
                is AuthResult.PendingApproval -> {
                    _uiState.value = AuthUiState.PendingApproval(result.messageAr, result.messageEn)
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.messageAr, result.messageEn)
                }
            }
        }
    }

    fun registerCustomer(fullName: String, phone: String, city: String, district: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.registerCustomer(fullName, phone, city, district)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.user)
                is AuthResult.PendingApproval -> _uiState.value = AuthUiState.PendingApproval(result.messageAr, result.messageEn)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.messageAr, result.messageEn)
            }
        }
    }

    fun registerRestaurant(
        businessName: String,
        managerName: String,
        phone: String,
        commercialRegister: String,
        address: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.registerRestaurant(businessName, managerName, phone, commercialRegister, address)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.user)
                is AuthResult.PendingApproval -> _uiState.value = AuthUiState.PendingApproval(result.messageAr, result.messageEn)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.messageAr, result.messageEn)
            }
        }
    }

    fun registerCaptain(
        fullName: String,
        phone: String,
        nationalId: String,
        vehicleType: String,
        captainMode: CaptainMode
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.registerCaptain(fullName, phone, nationalId, vehicleType, captainMode)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.user)
                is AuthResult.PendingApproval -> _uiState.value = AuthUiState.PendingApproval(result.messageAr, result.messageEn)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.messageAr, result.messageEn)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }
}
