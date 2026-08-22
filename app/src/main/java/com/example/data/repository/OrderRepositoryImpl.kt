package com.example.data.repository

import com.example.core.result.DrovaError
import com.example.core.result.DrovaResult
import com.example.data.local.source.OrderLocalDataSource
import com.example.data.local.source.OrderLocalDataSourceImpl
import com.example.data.remote.dto.*
import com.example.data.remote.source.OrderRemoteDataSource
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.OrderTimelineEvent
import com.example.domain.model.UserRole
import com.example.domain.repository.OrderRepository
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderRepositoryImpl(
    private val localDataSource: OrderLocalDataSource = OrderLocalDataSourceImpl(),
    private val remoteDataSource: OrderRemoteDataSource? = null
) : OrderRepository {

    override val allOrders: StateFlow<List<Order>> = localDataSource.ordersFlow

    override fun getCustomerOrders(customerId: String): List<Order> {
        return localDataSource.getAllOrders().filter { it.customerId == customerId || customerId.isEmpty() }
    }

    override fun getRestaurantOrders(restaurantId: String): List<Order> {
        return localDataSource.getAllOrders().filter { it.restaurantId == restaurantId || restaurantId.isEmpty() }
    }

    override fun getCaptainOrders(captainId: String): List<Order> {
        return localDataSource.getAllOrders().filter { it.captainId == captainId }
    }

    override fun getAvailableOrdersForCaptains(): List<Order> {
        return localDataSource.getAllOrders().filter { 
            it.status == OrderStatus.READY_FOR_PICKUP && it.captainId == null 
        }
    }

    override fun getOrderById(orderId: String): Order? {
        return localDataSource.getOrderById(orderId)
    }

    /**
     * Canonical lifecycle transition rules across the 9 stages + terminal cancellation/rejection
     */
    fun isValidTransition(current: OrderStatus, next: OrderStatus): Boolean {
        return when (current) {
            OrderStatus.CREATED -> next == OrderStatus.RESTAURANT_CONFIRMED || next == OrderStatus.CANCELLED || next == OrderStatus.REJECTED
            OrderStatus.RESTAURANT_CONFIRMED -> next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED || next == OrderStatus.REJECTED
            OrderStatus.PREPARING -> next == OrderStatus.READY_FOR_PICKUP || next == OrderStatus.CANCELLED
            OrderStatus.READY_FOR_PICKUP -> next == OrderStatus.CAPTAIN_ASSIGNED || next == OrderStatus.CANCELLED
            OrderStatus.CAPTAIN_ASSIGNED -> next == OrderStatus.PICKED_UP
            OrderStatus.PICKED_UP -> next == OrderStatus.ON_THE_WAY
            OrderStatus.ON_THE_WAY -> next == OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> next == OrderStatus.COMPLETED
            OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.REJECTED -> false
        }
    }

    private fun getActorRoleForStatus(status: OrderStatus): UserRole {
        return when (status) {
            OrderStatus.CREATED -> UserRole.CUSTOMER
            OrderStatus.RESTAURANT_CONFIRMED,
            OrderStatus.PREPARING,
            OrderStatus.READY_FOR_PICKUP,
            OrderStatus.REJECTED -> UserRole.RESTAURANT
            OrderStatus.CAPTAIN_ASSIGNED,
            OrderStatus.PICKED_UP,
            OrderStatus.ON_THE_WAY,
            OrderStatus.DELIVERED -> UserRole.CAPTAIN
            OrderStatus.COMPLETED,
            OrderStatus.CANCELLED -> UserRole.CUSTOMER
        }
    }

    private fun getCurrentFormattedTime(): String {
        return try {
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            "اليوم، " + formatter.format(Date())
        } catch (e: Exception) {
            "الآن"
        }
    }

    // ==========================================
    // Backward-Compatible Direct Mutators
    // ==========================================

    override suspend fun advanceOrderStatus(orderId: String, newStatus: OrderStatus): Boolean {
        val result = updateOrderStatus(orderId, newStatus)
        return result is DrovaResult.Success && result.data
    }

    override suspend fun assignCaptainToOrder(
        orderId: String,
        captainId: String,
        captainName: String,
        captainPhone: String
    ): Boolean {
        val result = assignCaptain(orderId, captainId, captainName, captainPhone)
        return result is DrovaResult.Success && result.data
    }

    override suspend fun cancelOrder(orderId: String, reason: String): Boolean {
        val result = cancelOrderWithResult(orderId, reason)
        return result is DrovaResult.Success && result.data
    }

    override suspend fun rejectOrder(orderId: String, reason: String): Boolean {
        val result = rejectOrderWithResult(orderId, reason)
        return result is DrovaResult.Success && result.data
    }

    override suspend fun createNewOrder(order: Order): String {
        val result = createOrder(order)
        return when (result) {
            is DrovaResult.Success -> result.data
            else -> order.id
        }
    }

    // ==========================================
    // Enhanced DrovaResult Operations
    // ==========================================

    override suspend fun createOrder(order: Order): DrovaResult<String> {
        val initialTimeline = if (order.timeline.isEmpty()) {
            listOf(
                OrderTimelineEvent(
                    status = order.status,
                    timestampMillis = order.createdAtMillis,
                    formattedTime = order.createdAtFormatted,
                    titleAr = "تم استلام طلبك بنجاح",
                    titleEn = "Order Created Successfully",
                    noteAr = "تم إرسال الطلب للمطعم للمراجعة والتأكيد",
                    noteEn = "Order sent to restaurant for confirmation",
                    actorRole = UserRole.CUSTOMER
                )
            )
        } else {
            order.timeline
        }

        val finalizedOrder = order.copy(timeline = initialTimeline)
        localDataSource.saveOrder(finalizedOrder)

        // Sync with remote if available (graceful fallback)
        remoteDataSource?.let { remote ->
            try {
                remote.createOrder(CreateOrderRequestDto(finalizedOrder.toDto()))
            } catch (e: Exception) {
                // Remote sync is non-blocking in offline/local-first mode
            }
        }

        return DrovaResult.Success(finalizedOrder.id)
    }

    override suspend fun getOrder(orderId: String): DrovaResult<Order?> {
        val localOrder = localDataSource.getOrderById(orderId)
        if (localOrder != null) {
            return DrovaResult.Success(localOrder)
        }

        remoteDataSource?.let { remote ->
            when (val remoteResult = remote.getOrderById(orderId)) {
                is DrovaResult.Success -> {
                    val order = remoteResult.data.toDomain()
                    localDataSource.saveOrder(order)
                    return DrovaResult.Success(order)
                }
                is DrovaResult.Error -> {
                    // Fall back to null if not found
                }
                DrovaResult.Loading -> {}
            }
        }

        return DrovaResult.Success(null)
    }

    override suspend fun fetchCustomerOrders(customerId: String): DrovaResult<List<Order>> {
        val local = getCustomerOrders(customerId)
        return DrovaResult.Success(local)
    }

    override suspend fun fetchRestaurantOrders(restaurantId: String): DrovaResult<List<Order>> {
        val local = getRestaurantOrders(restaurantId)
        return DrovaResult.Success(local)
    }

    override suspend fun fetchCaptainOrders(captainId: String): DrovaResult<List<Order>> {
        val local = getCaptainOrders(captainId)
        return DrovaResult.Success(local)
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): DrovaResult<Boolean> {
        val existingOrder = localDataSource.getOrderById(orderId)
            ?: return DrovaResult.Error(
                error = DrovaError.Domain.OrderNotFound(orderId),
                messageAr = "الطلب رقم $orderId غير موجود",
                messageEn = "Order $orderId not found"
            )

        if (!isValidTransition(existingOrder.status, newStatus)) {
            return DrovaResult.Error(
                error = DrovaError.Domain.InvalidStateTransition(existingOrder.status.name, newStatus.name),
                messageAr = "لا يمكن الانتقال من حالة ${existingOrder.status.titleAr} إلى ${newStatus.titleAr}",
                messageEn = "Cannot transition from ${existingOrder.status.name} to ${newStatus.name}"
            )
        }

        val formattedTime = getCurrentFormattedTime()
        val actorRole = getActorRoleForStatus(newStatus)

        val newEvent = OrderTimelineEvent(
            status = newStatus,
            timestampMillis = System.currentTimeMillis(),
            formattedTime = formattedTime,
            titleAr = newStatus.titleAr,
            titleEn = newStatus.titleEn,
            noteAr = newStatus.descriptionAr,
            noteEn = newStatus.descriptionEn,
            actorRole = actorRole
        )

        val updatedOrder = existingOrder.copy(
            status = newStatus,
            timeline = existingOrder.timeline + newEvent
        )

        localDataSource.updateOrder(updatedOrder)

        // Non-blocking remote sync
        remoteDataSource?.let { remote ->
            try {
                remote.updateOrderStatus(
                    orderId,
                    UpdateOrderStatusRequestDto(
                        orderId = orderId,
                        newStatus = newStatus.name,
                        actorRole = actorRole.name
                    )
                )
            } catch (e: Exception) {
                // Non-blocking fallback
            }
        }

        return DrovaResult.Success(true)
    }

    override suspend fun assignCaptain(
        orderId: String,
        captainId: String,
        captainName: String,
        captainPhone: String
    ): DrovaResult<Boolean> {
        val existingOrder = localDataSource.getOrderById(orderId)
            ?: return DrovaResult.Error(
                error = DrovaError.Domain.OrderNotFound(orderId),
                messageAr = "الطلب رقم $orderId غير موجود",
                messageEn = "Order $orderId not found"
            )

        // Captain assignment is valid from READY_FOR_PICKUP
        if (existingOrder.status != OrderStatus.READY_FOR_PICKUP) {
            return DrovaResult.Error(
                error = DrovaError.Domain.InvalidStateTransition(existingOrder.status.name, OrderStatus.CAPTAIN_ASSIGNED.name),
                messageAr = "لا يمكن تعيين كابتن إلا عندما يكون الطلب جاهزاً للاستلام",
                messageEn = "Captain can only be assigned when order is READY_FOR_PICKUP"
            )
        }

        val formattedTime = getCurrentFormattedTime()
        val newEvent = OrderTimelineEvent(
            status = OrderStatus.CAPTAIN_ASSIGNED,
            timestampMillis = System.currentTimeMillis(),
            formattedTime = formattedTime,
            titleAr = "تم تعيين الكابتن وتأكيد الاستلام",
            titleEn = "Captain Assigned & Confirmed",
            noteAr = "الكابتن $captainName قبل الطلب ويتوجه للمطعم لاستلامه",
            noteEn = "Captain $captainName accepted the order and heading to restaurant",
            actorRole = UserRole.CAPTAIN
        )

        val updatedOrder = existingOrder.copy(
            captainId = captainId,
            captainName = captainName,
            captainPhone = captainPhone,
            status = OrderStatus.CAPTAIN_ASSIGNED,
            timeline = existingOrder.timeline + newEvent
        )

        localDataSource.updateOrder(updatedOrder)

        remoteDataSource?.let { remote ->
            try {
                remote.assignCaptain(
                    orderId,
                    AssignCaptainRequestDto(
                        orderId = orderId,
                        captainId = captainId,
                        captainName = captainName,
                        captainPhone = captainPhone
                    )
                )
            } catch (e: Exception) {
                // Non-blocking fallback
            }
        }

        return DrovaResult.Success(true)
    }

    override suspend fun cancelOrderWithResult(orderId: String, reason: String): DrovaResult<Boolean> {
        val existingOrder = localDataSource.getOrderById(orderId)
            ?: return DrovaResult.Error(
                error = DrovaError.Domain.OrderNotFound(orderId),
                messageAr = "الطلب غير موجود",
                messageEn = "Order not found"
            )

        if (!isValidTransition(existingOrder.status, OrderStatus.CANCELLED)) {
            return DrovaResult.Error(
                error = DrovaError.Domain.InvalidStateTransition(existingOrder.status.name, OrderStatus.CANCELLED.name),
                messageAr = "لا يمكن إلغاء الطلب في هذه المرحلة",
                messageEn = "Order cannot be cancelled at this stage"
            )
        }

        val formattedTime = getCurrentFormattedTime()
        val cancelEvent = OrderTimelineEvent(
            status = OrderStatus.CANCELLED,
            timestampMillis = System.currentTimeMillis(),
            formattedTime = formattedTime,
            titleAr = "تم إلغاء الطلب",
            titleEn = "Order Cancelled",
            noteAr = "سبب الإلغاء: $reason",
            noteEn = "Cancellation reason: $reason",
            actorRole = UserRole.CUSTOMER
        )

        val updatedOrder = existingOrder.copy(
            status = OrderStatus.CANCELLED,
            cancellationReason = reason,
            timeline = existingOrder.timeline + cancelEvent
        )

        localDataSource.updateOrder(updatedOrder)
        return DrovaResult.Success(true)
    }

    override suspend fun rejectOrderWithResult(orderId: String, reason: String): DrovaResult<Boolean> {
        val existingOrder = localDataSource.getOrderById(orderId)
            ?: return DrovaResult.Error(
                error = DrovaError.Domain.OrderNotFound(orderId),
                messageAr = "الطلب غير موجود",
                messageEn = "Order not found"
            )

        if (!isValidTransition(existingOrder.status, OrderStatus.REJECTED)) {
            return DrovaResult.Error(
                error = DrovaError.Domain.InvalidStateTransition(existingOrder.status.name, OrderStatus.REJECTED.name),
                messageAr = "لا يمكن رفض الطلب في هذه المرحلة",
                messageEn = "Order cannot be rejected at this stage"
            )
        }

        val formattedTime = getCurrentFormattedTime()
        val rejectEvent = OrderTimelineEvent(
            status = OrderStatus.REJECTED,
            timestampMillis = System.currentTimeMillis(),
            formattedTime = formattedTime,
            titleAr = "تم رفض الطلب من قِبل المطعم",
            titleEn = "Order Rejected by Restaurant",
            noteAr = "سبب الرفض: $reason",
            noteEn = "Rejection reason: $reason",
            actorRole = UserRole.RESTAURANT
        )

        val updatedOrder = existingOrder.copy(
            status = OrderStatus.REJECTED,
            rejectionReason = reason,
            timeline = existingOrder.timeline + rejectEvent
        )

        localDataSource.updateOrder(updatedOrder)
        return DrovaResult.Success(true)
    }

    override suspend fun addTimelineEvent(orderId: String, event: OrderTimelineEvent): DrovaResult<Boolean> {
        val existingOrder = localDataSource.getOrderById(orderId)
            ?: return DrovaResult.Error(
                error = DrovaError.Domain.OrderNotFound(orderId),
                messageAr = "الطلب غير موجود",
                messageEn = "Order not found"
            )

        val updatedOrder = existingOrder.copy(
            timeline = existingOrder.timeline + event
        )
        localDataSource.updateOrder(updatedOrder)
        return DrovaResult.Success(true)
    }
}
