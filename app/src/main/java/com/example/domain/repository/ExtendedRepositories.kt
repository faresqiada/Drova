package com.example.domain.repository

import com.example.core.result.DrovaResult
import com.example.domain.model.MenuItem
import kotlinx.coroutines.flow.StateFlow

interface MenuRepository {
    fun getMenuForRestaurant(restaurantId: String): List<MenuItem>
    suspend fun addMenuItem(restaurantId: String, item: MenuItem): DrovaResult<MenuItem>
    suspend fun updateMenuItem(restaurantId: String, item: MenuItem): DrovaResult<MenuItem>
    suspend fun deleteMenuItem(restaurantId: String, itemId: String): DrovaResult<Unit>
    suspend fun toggleAvailability(restaurantId: String, itemId: String, isAvailable: Boolean): DrovaResult<MenuItem>
}

interface WalletRepository {
    suspend fun requestPayout(userId: String, amountEgp: Double, note: String? = null): DrovaResult<Boolean>
    suspend fun getTransactions(userId: String): DrovaResult<List<com.example.domain.model.CaptainTransaction>>
}

interface NotificationRepository {
    suspend fun getNotifications(userId: String): DrovaResult<List<com.example.domain.model.CaptainNotification>>
    suspend fun markAsRead(userId: String, notificationId: String): DrovaResult<Unit>
    suspend fun clearAllNotifications(userId: String): DrovaResult<Unit>
}

data class FinancialSummary(
    val grossRevenueEgp: Double,
    val netPayoutEgp: Double,
    val platformCommissionEgp: Double,
    val pendingPayoutEgp: Double,
    val completedOrdersCount: Int
)

interface FinanceRepository {
    suspend fun getRestaurantFinanceSummary(restaurantId: String): DrovaResult<FinancialSummary>
}
