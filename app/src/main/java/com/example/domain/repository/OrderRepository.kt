package com.example.domain.repository

import com.example.core.result.DrovaResult
import com.example.domain.model.PickupProof
import com.example.domain.model.PickupProofConfirmation
import java.io.File
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.OrderTimelineEvent
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth interface for Order management across Customer, Restaurant, and Captain modules.
 */
interface OrderRepository {
    val allOrders: StateFlow<List<Order>>

    // Synchronous / Memory Queries
    fun getCustomerOrders(customerId: String): List<Order>
    fun getRestaurantOrders(restaurantId: String): List<Order>
    fun getCaptainOrders(captainId: String): List<Order>
    fun getAvailableOrdersForCaptains(): List<Order>
    fun getOrderById(orderId: String): Order?

    // Standard Direct Mutators (Preserved for compatibility)
    suspend fun advanceOrderStatus(orderId: String, newStatus: OrderStatus): Boolean
    suspend fun assignCaptainToOrder(
        orderId: String,
        captainId: String,
        captainName: String,
        captainPhone: String
    ): Boolean
    suspend fun cancelOrder(orderId: String, reason: String): Boolean
    suspend fun rejectOrder(orderId: String, reason: String): Boolean
    suspend fun createNewOrder(order: Order): String

    // Backend-Ready DrovaResult Operations
    suspend fun createOrder(order: Order): DrovaResult<String>
    suspend fun getOrder(orderId: String): DrovaResult<Order?>
    suspend fun fetchCustomerOrders(customerId: String): DrovaResult<List<Order>>
    suspend fun fetchRestaurantOrders(restaurantId: String): DrovaResult<List<Order>>
    suspend fun fetchCaptainOrders(captainId: String): DrovaResult<List<Order>>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): DrovaResult<Boolean>
    suspend fun confirmPickupWithProof(orderId: String, captainId: String, imageFile: File): PickupProofConfirmation
    suspend fun applyValidatedPickupProof(orderId: String, captainId: String, proof: PickupProof): DrovaResult<Boolean>
    suspend fun assignCaptain(
        orderId: String,
        captainId: String,
        captainName: String,
        captainPhone: String
    ): DrovaResult<Boolean>
    suspend fun cancelOrderWithResult(orderId: String, reason: String): DrovaResult<Boolean>
    suspend fun rejectOrderWithResult(orderId: String, reason: String): DrovaResult<Boolean>
    suspend fun addTimelineEvent(orderId: String, event: OrderTimelineEvent): DrovaResult<Boolean>
}
