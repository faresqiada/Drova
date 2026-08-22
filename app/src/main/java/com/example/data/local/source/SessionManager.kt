package com.example.data.local.source

import com.example.domain.model.User
import com.example.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages active user authentication session, tokens, and active role state.
 */
class SessionManager {

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _selectedRole = MutableStateFlow(UserRole.CUSTOMER)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    fun setAuthToken(token: String?) {
        _authToken.value = token
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun setSelectedRole(role: UserRole) {
        _selectedRole.value = role
    }

    fun clearSession() {
        _authToken.value = null
        _currentUser.value = null
    }

    val isAuthenticated: Boolean
        get() = _currentUser.value != null
}
