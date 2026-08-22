package com.example.domain.repository

import com.example.core.result.DrovaResult
import com.example.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    suspend fun getUserProfile(userId: String): DrovaResult<User>
    suspend fun updateUserProfile(user: User): DrovaResult<User>
}
