package com.example.data.remote.source

import com.example.core.network.safeApiCall
import com.example.core.result.DrovaResult
import com.example.data.remote.api.*
import com.example.data.remote.dto.*
import com.example.domain.model.*

interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequestDto): DrovaResult<AuthResponseDto>
    suspend fun registerCustomer(request: CustomerRegisterRequestDto): DrovaResult<AuthResponseDto>
    suspend fun registerRestaurant(request: RestaurantRegisterRequestDto): DrovaResult<AuthResponseDto>
    suspend fun registerCaptain(request: CaptainRegisterRequestDto): DrovaResult<AuthResponseDto>
    suspend fun getCurrentUserProfile(): DrovaResult<UserDto>
    suspend fun logout(): DrovaResult<Unit>
}

class AuthRemoteDataSourceImpl(
    private val apiService: AuthApiService
) : AuthRemoteDataSource {
    override suspend fun login(request: LoginRequestDto): DrovaResult<AuthResponseDto> = safeApiCall {
        val response = apiService.login(request)
        response.data ?: throw IllegalStateException(response.messageEn ?: "Login failed")
    }

    override suspend fun registerCustomer(request: CustomerRegisterRequestDto): DrovaResult<AuthResponseDto> = safeApiCall {
        val response = apiService.registerCustomer(request)
        response.data ?: throw IllegalStateException(response.messageEn ?: "Registration failed")
    }

    override suspend fun registerRestaurant(request: RestaurantRegisterRequestDto): DrovaResult<AuthResponseDto> = safeApiCall {
        val response = apiService.registerRestaurant(request)
        response.data ?: throw IllegalStateException(response.messageEn ?: "Registration failed")
    }

    override suspend fun registerCaptain(request: CaptainRegisterRequestDto): DrovaResult<AuthResponseDto> = safeApiCall {
        val response = apiService.registerCaptain(request)
        response.data ?: throw IllegalStateException(response.messageEn ?: "Registration failed")
    }

    override suspend fun getCurrentUserProfile(): DrovaResult<UserDto> = safeApiCall {
        val response = apiService.getCurrentUserProfile()
        response.data ?: throw IllegalStateException("Profile not found")
    }

    override suspend fun logout(): DrovaResult<Unit> = safeApiCall {
        apiService.logout()
        Unit
    }
}

interface OrderRemoteDataSource {
    suspend fun getAllOrders(): DrovaResult<List<OrderDto>>
    suspend fun getOrderById(orderId: String): DrovaResult<OrderDto>
    suspend fun getCustomerOrders(customerId: String): DrovaResult<List<OrderDto>>
    suspend fun getRestaurantOrders(restaurantId: String): DrovaResult<List<OrderDto>>
    suspend fun getCaptainOrders(captainId: String): DrovaResult<List<OrderDto>>
    suspend fun getAvailableOrdersForCaptains(): DrovaResult<List<OrderDto>>
    suspend fun createOrder(request: CreateOrderRequestDto): DrovaResult<OrderDto>
    suspend fun updateOrderStatus(orderId: String, request: UpdateOrderStatusRequestDto): DrovaResult<OrderDto>
    suspend fun assignCaptain(orderId: String, request: AssignCaptainRequestDto): DrovaResult<OrderDto>
    suspend fun cancelOrder(orderId: String, request: CancelOrderRequestDto): DrovaResult<OrderDto>
    suspend fun rejectOrder(orderId: String, request: RejectOrderRequestDto): DrovaResult<OrderDto>
    suspend fun addTimelineEvent(orderId: String, request: AddTimelineEventRequestDto): DrovaResult<OrderDto>
}

class OrderRemoteDataSourceImpl(
    private val apiService: OrderApiService
) : OrderRemoteDataSource {
    override suspend fun getAllOrders(): DrovaResult<List<OrderDto>> = safeApiCall {
        apiService.getAllOrders().data ?: emptyList()
    }

    override suspend fun getOrderById(orderId: String): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.getOrderById(orderId)
        response.data ?: throw NoSuchElementException("Order $orderId not found")
    }

    override suspend fun getCustomerOrders(customerId: String): DrovaResult<List<OrderDto>> = safeApiCall {
        apiService.getCustomerOrders(customerId).data ?: emptyList()
    }

    override suspend fun getRestaurantOrders(restaurantId: String): DrovaResult<List<OrderDto>> = safeApiCall {
        apiService.getRestaurantOrders(restaurantId).data ?: emptyList()
    }

    override suspend fun getCaptainOrders(captainId: String): DrovaResult<List<OrderDto>> = safeApiCall {
        apiService.getCaptainOrders(captainId).data ?: emptyList()
    }

    override suspend fun getAvailableOrdersForCaptains(): DrovaResult<List<OrderDto>> = safeApiCall {
        apiService.getAvailableOrdersForCaptains().data ?: emptyList()
    }

    override suspend fun createOrder(request: CreateOrderRequestDto): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.createOrder(request)
        response.data ?: throw IllegalStateException("Failed to create order")
    }

    override suspend fun updateOrderStatus(orderId: String, request: UpdateOrderStatusRequestDto): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.updateOrderStatus(orderId, request)
        response.data ?: throw IllegalStateException("Failed to update status")
    }

    override suspend fun assignCaptain(orderId: String, request: AssignCaptainRequestDto): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.assignCaptain(orderId, request)
        response.data ?: throw IllegalStateException("Failed to assign captain")
    }

    override suspend fun cancelOrder(orderId: String, request: CancelOrderRequestDto): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.cancelOrder(orderId, request)
        response.data ?: throw IllegalStateException("Failed to cancel order")
    }

    override suspend fun rejectOrder(orderId: String, request: RejectOrderRequestDto): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.rejectOrder(orderId, request)
        response.data ?: throw IllegalStateException("Failed to reject order")
    }

    override suspend fun addTimelineEvent(orderId: String, request: AddTimelineEventRequestDto): DrovaResult<OrderDto> = safeApiCall {
        val response = apiService.addTimelineEvent(orderId, request)
        response.data ?: throw IllegalStateException("Failed to add timeline event")
    }
}

interface RestaurantRemoteDataSource {
    suspend fun getRestaurants(): DrovaResult<List<RestaurantDto>>
    suspend fun getRestaurantById(id: String): DrovaResult<RestaurantDto>
    suspend fun toggleOpenStatus(id: String, isOpen: Boolean): DrovaResult<RestaurantDto>
    suspend fun updateRestaurantProfile(request: UpdateRestaurantProfileRequestDto): DrovaResult<RestaurantDto>
    suspend fun addMenuItem(restaurantId: String, item: MenuItemDto): DrovaResult<MenuItemDto>
    suspend fun updateMenuItem(restaurantId: String, itemId: String, item: MenuItemDto): DrovaResult<MenuItemDto>
    suspend fun deleteMenuItem(restaurantId: String, itemId: String): DrovaResult<Unit>
    suspend fun toggleMenuItemAvailability(restaurantId: String, itemId: String, isAvailable: Boolean): DrovaResult<MenuItemDto>
}

class RestaurantRemoteDataSourceImpl(
    private val apiService: RestaurantApiService
) : RestaurantRemoteDataSource {
    override suspend fun getRestaurants(): DrovaResult<List<RestaurantDto>> = safeApiCall {
        apiService.getRestaurants().data ?: emptyList()
    }

    override suspend fun getRestaurantById(id: String): DrovaResult<RestaurantDto> = safeApiCall {
        apiService.getRestaurantById(id).data ?: throw NoSuchElementException("Restaurant $id not found")
    }

    override suspend fun toggleOpenStatus(id: String, isOpen: Boolean): DrovaResult<RestaurantDto> = safeApiCall {
        apiService.toggleOpenStatus(id, ToggleOpenStatusRequestDto(id, isOpen)).data
            ?: throw IllegalStateException("Failed to toggle open status")
    }

    override suspend fun updateRestaurantProfile(request: UpdateRestaurantProfileRequestDto): DrovaResult<RestaurantDto> = safeApiCall {
        apiService.updateRestaurantProfile(request.restaurantId, request).data
            ?: throw IllegalStateException("Failed to update profile")
    }

    override suspend fun addMenuItem(restaurantId: String, item: MenuItemDto): DrovaResult<MenuItemDto> = safeApiCall {
        apiService.addMenuItem(restaurantId, item).data ?: throw IllegalStateException("Failed to add item")
    }

    override suspend fun updateMenuItem(restaurantId: String, itemId: String, item: MenuItemDto): DrovaResult<MenuItemDto> = safeApiCall {
        apiService.updateMenuItem(restaurantId, itemId, item).data ?: throw IllegalStateException("Failed to update item")
    }

    override suspend fun deleteMenuItem(restaurantId: String, itemId: String): DrovaResult<Unit> = safeApiCall {
        apiService.deleteMenuItem(restaurantId, itemId)
        Unit
    }

    override suspend fun toggleMenuItemAvailability(restaurantId: String, itemId: String, isAvailable: Boolean): DrovaResult<MenuItemDto> = safeApiCall {
        apiService.toggleMenuItemAvailability(restaurantId, itemId, ToggleMenuItemAvailabilityRequestDto(restaurantId, itemId, isAvailable)).data
            ?: throw IllegalStateException("Failed to toggle availability")
    }
}

interface CaptainRemoteDataSource {
    suspend fun getEarnings(captainId: String): DrovaResult<CaptainEarningsDto>
    suspend fun getShiftData(captainId: String): DrovaResult<CaptainShiftDataDto>
    suspend fun toggleOnlineStatus(captainId: String, isOnline: Boolean): DrovaResult<Unit>
    suspend fun setCaptainMode(captainId: String, mode: String): DrovaResult<Unit>
    suspend fun getActiveTask(captainId: String): DrovaResult<DeliveryTaskDto?>
    suspend fun getCompletedTasks(captainId: String): DrovaResult<List<DeliveryTaskDto>>
    suspend fun acceptTask(captainId: String, orderId: String): DrovaResult<DeliveryTaskDto>
    suspend fun rejectTask(captainId: String, orderId: String): DrovaResult<Unit>
}

class CaptainRemoteDataSourceImpl(
    private val apiService: CaptainApiService
) : CaptainRemoteDataSource {
    override suspend fun getEarnings(captainId: String): DrovaResult<CaptainEarningsDto> = safeApiCall {
        apiService.getEarnings(captainId).data ?: CaptainEarningsDto()
    }

    override suspend fun getShiftData(captainId: String): DrovaResult<CaptainShiftDataDto> = safeApiCall {
        apiService.getShiftData(captainId).data ?: CaptainShiftDataDto()
    }

    override suspend fun toggleOnlineStatus(captainId: String, isOnline: Boolean): DrovaResult<Unit> = safeApiCall {
        apiService.toggleOnlineStatus(captainId, ToggleOnlineRequestDto(isOnline))
        Unit
    }

    override suspend fun setCaptainMode(captainId: String, mode: String): DrovaResult<Unit> = safeApiCall {
        apiService.setCaptainMode(captainId, SetCaptainModeRequestDto(mode))
        Unit
    }

    override suspend fun getActiveTask(captainId: String): DrovaResult<DeliveryTaskDto?> = safeApiCall {
        apiService.getActiveTask(captainId).data
    }

    override suspend fun getCompletedTasks(captainId: String): DrovaResult<List<DeliveryTaskDto>> = safeApiCall {
        apiService.getCompletedTasks(captainId).data ?: emptyList()
    }

    override suspend fun acceptTask(captainId: String, orderId: String): DrovaResult<DeliveryTaskDto> = safeApiCall {
        apiService.acceptTask(captainId, orderId).data ?: throw IllegalStateException("Failed to accept task")
    }

    override suspend fun rejectTask(captainId: String, orderId: String): DrovaResult<Unit> = safeApiCall {
        apiService.rejectTask(captainId, orderId)
        Unit
    }
}

interface WalletRemoteDataSource {
    suspend fun getTransactions(userId: String): DrovaResult<List<CaptainTransactionDto>>
    suspend fun requestPayout(userId: String, request: PayoutRequestDto): DrovaResult<CaptainTransactionDto>
}

class WalletRemoteDataSourceImpl(
    private val apiService: WalletApiService
) : WalletRemoteDataSource {
    override suspend fun getTransactions(userId: String): DrovaResult<List<CaptainTransactionDto>> = safeApiCall {
        apiService.getTransactions(userId).data ?: emptyList()
    }

    override suspend fun requestPayout(userId: String, request: PayoutRequestDto): DrovaResult<CaptainTransactionDto> = safeApiCall {
        apiService.requestPayout(userId, request).data ?: throw IllegalStateException("Failed to process payout")
    }
}

interface NotificationRemoteDataSource {
    suspend fun getNotifications(userId: String): DrovaResult<List<CaptainNotificationDto>>
    suspend fun markAsRead(userId: String, notificationId: String): DrovaResult<Unit>
    suspend fun clearAllNotifications(userId: String): DrovaResult<Unit>
}

class NotificationRemoteDataSourceImpl(
    private val apiService: NotificationApiService
) : NotificationRemoteDataSource {
    override suspend fun getNotifications(userId: String): DrovaResult<List<CaptainNotificationDto>> = safeApiCall {
        apiService.getNotifications(userId).data ?: emptyList()
    }

    override suspend fun markAsRead(userId: String, notificationId: String): DrovaResult<Unit> = safeApiCall {
        apiService.markAsRead(userId, notificationId)
        Unit
    }

    override suspend fun clearAllNotifications(userId: String): DrovaResult<Unit> = safeApiCall {
        apiService.clearAllNotifications(userId)
        Unit
    }
}
