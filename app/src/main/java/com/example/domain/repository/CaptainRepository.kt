package com.example.domain.repository

import com.example.domain.model.*
import kotlinx.coroutines.flow.StateFlow

interface CaptainRepository {
    val isOnline: StateFlow<Boolean>
    val captainMode: StateFlow<CaptainMode>
    val earnings: StateFlow<CaptainEarnings>
    val shiftData: StateFlow<CaptainShiftData>
    val availableTasks: StateFlow<List<DeliveryTask>>
    val activeTask: StateFlow<DeliveryTask?>
    val completedTasks: StateFlow<List<DeliveryTask>>
    val transactions: StateFlow<List<CaptainTransaction>>
    val notifications: StateFlow<List<CaptainNotification>>

    suspend fun toggleOnlineStatus(online: Boolean)
    suspend fun setCaptainMode(mode: CaptainMode): Boolean
    suspend fun acceptTask(orderId: String): Boolean
    suspend fun updateTaskStatus(orderId: String, newStatus: OrderStatus): Boolean
    suspend fun rejectTask(orderId: String)
    suspend fun requestPayout(amountEgp: Double): Boolean
    suspend fun markNotificationAsRead(id: String)
    suspend fun clearAllNotifications()
}
