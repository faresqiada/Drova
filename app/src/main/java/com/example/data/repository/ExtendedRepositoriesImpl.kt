package com.example.data.repository

import com.example.core.result.DrovaError
import com.example.core.result.DrovaResult
import com.example.data.local.source.SessionManager
import com.example.data.remote.dto.toDomain
import com.example.data.remote.dto.toDto
import com.example.data.remote.source.AuthRemoteDataSource
import com.example.data.remote.source.NotificationRemoteDataSource
import com.example.data.remote.source.WalletRemoteDataSource
import com.example.domain.model.*
import com.example.domain.repository.*
import kotlinx.coroutines.flow.StateFlow

class UserRepositoryImpl(
    private val sessionManager: SessionManager,
    private val remoteDataSource: AuthRemoteDataSource? = null
) : UserRepository {

    override val currentUser: StateFlow<User?> = sessionManager.currentUser

    override suspend fun getUserProfile(userId: String): DrovaResult<User> {
        val current = sessionManager.currentUser.value
        if (current != null && current.id == userId) {
            return DrovaResult.Success(current)
        }

        remoteDataSource?.let { remote ->
            when (val result = remote.getCurrentUserProfile()) {
                is DrovaResult.Success -> {
                    val user = result.data.toDomain()
                    sessionManager.setCurrentUser(user)
                    return DrovaResult.Success(user)
                }
                is DrovaResult.Error -> {
                    // Fall back to local
                }
                DrovaResult.Loading -> {}
            }
        }

        return if (current != null) {
            DrovaResult.Success(current)
        } else {
            DrovaResult.Error(
                error = DrovaError.Domain.OrderNotFound(userId),
                messageAr = "المستخدم غير موجود",
                messageEn = "User not found"
            )
        }
    }

    override suspend fun updateUserProfile(user: User): DrovaResult<User> {
        sessionManager.setCurrentUser(user)
        return DrovaResult.Success(user)
    }
}

class MenuRepositoryImpl(
    private val restaurantRepository: RestaurantRepository
) : MenuRepository {

    override fun getMenuForRestaurant(restaurantId: String): List<MenuItem> {
        val rest = restaurantRepository.getRestaurantById(restaurantId)
        return rest?.menu ?: emptyList()
    }

    override suspend fun addMenuItem(restaurantId: String, item: MenuItem): DrovaResult<MenuItem> {
        restaurantRepository.addMenuItem(restaurantId, item)
        return DrovaResult.Success(item)
    }

    override suspend fun updateMenuItem(restaurantId: String, item: MenuItem): DrovaResult<MenuItem> {
        restaurantRepository.updateMenuItem(restaurantId, item)
        return DrovaResult.Success(item)
    }

    override suspend fun deleteMenuItem(restaurantId: String, itemId: String): DrovaResult<Unit> {
        restaurantRepository.deleteMenuItem(restaurantId, itemId)
        return DrovaResult.Success(Unit)
    }

    override suspend fun toggleAvailability(restaurantId: String, itemId: String, isAvailable: Boolean): DrovaResult<MenuItem> {
        restaurantRepository.toggleMenuItemAvailability(restaurantId, itemId, isAvailable)
        val updated = getMenuForRestaurant(restaurantId).find { it.id == itemId }
        return if (updated != null) {
            DrovaResult.Success(updated)
        } else {
            DrovaResult.Error(
                error = DrovaError.Network.NotFound(404),
                messageAr = "الصنف غير موجود",
                messageEn = "Menu item not found"
            )
        }
    }
}

class WalletRepositoryImpl(
    private val captainRepository: CaptainRepository,
    private val remoteDataSource: WalletRemoteDataSource? = null
) : WalletRepository {

    override suspend fun requestPayout(userId: String, amountEgp: Double, note: String?): DrovaResult<Boolean> {
        val success = captainRepository.requestPayout(amountEgp)
        return if (success) {
            DrovaResult.Success(true)
        } else {
            DrovaResult.Error(
                error = DrovaError.Domain.InsufficientBalance,
                messageAr = "الرصيد المتاح غير كافٍ لإتمام طلب السحب",
                messageEn = "Insufficient balance for payout request"
            )
        }
    }

    override suspend fun getTransactions(userId: String): DrovaResult<List<CaptainTransaction>> {
        val local = captainRepository.transactions.value
        return DrovaResult.Success(local)
    }
}

class NotificationRepositoryImpl(
    private val captainRepository: CaptainRepository,
    private val remoteDataSource: NotificationRemoteDataSource? = null
) : NotificationRepository {

    override suspend fun getNotifications(userId: String): DrovaResult<List<CaptainNotification>> {
        val local = captainRepository.notifications.value
        return DrovaResult.Success(local)
    }

    override suspend fun markAsRead(userId: String, notificationId: String): DrovaResult<Unit> {
        captainRepository.markNotificationAsRead(notificationId)
        return DrovaResult.Success(Unit)
    }

    override suspend fun clearAllNotifications(userId: String): DrovaResult<Unit> {
        captainRepository.clearAllNotifications()
        return DrovaResult.Success(Unit)
    }
}

class FinanceRepositoryImpl(
    private val orderRepository: OrderRepository,
    private val restaurantRepository: RestaurantRepository
) : FinanceRepository {

    override suspend fun getRestaurantFinanceSummary(restaurantId: String): DrovaResult<FinancialSummary> {
        val orders = orderRepository.getRestaurantOrders(restaurantId)
        val completed = orders.filter { it.status == OrderStatus.COMPLETED || it.status == OrderStatus.DELIVERED }

        val gross = completed.sumOf { it.subtotalEgp }
        val net = completed.sumOf { it.netRestaurantPayoutEgp }
        val commission = (gross - net).coerceAtLeast(0.0)
        val pending = completed.takeLast(3).sumOf { it.netRestaurantPayoutEgp }

        return DrovaResult.Success(
            FinancialSummary(
                grossRevenueEgp = gross,
                netPayoutEgp = net,
                platformCommissionEgp = commission,
                pendingPayoutEgp = pending,
                completedOrdersCount = completed.size
            )
        )
    }
}
