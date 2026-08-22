package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.http.*

interface OrderApiService {
    @GET("orders")
    suspend fun getAllOrders(): ApiResponseDto<List<OrderDto>>

    @GET("orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: String): ApiResponseDto<OrderDto>

    @GET("orders/customer/{customerId}")
    suspend fun getCustomerOrders(@Path("customerId") customerId: String): ApiResponseDto<List<OrderDto>>

    @GET("orders/restaurant/{restaurantId}")
    suspend fun getRestaurantOrders(@Path("restaurantId") restaurantId: String): ApiResponseDto<List<OrderDto>>

    @GET("orders/captain/{captainId}")
    suspend fun getCaptainOrders(@Path("captainId") captainId: String): ApiResponseDto<List<OrderDto>>

    @GET("orders/dispatch/available")
    suspend fun getAvailableOrdersForCaptains(): ApiResponseDto<List<OrderDto>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequestDto): ApiResponseDto<OrderDto>

    @PATCH("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequestDto
    ): ApiResponseDto<OrderDto>

    @POST("orders/{orderId}/assign-captain")
    suspend fun assignCaptain(
        @Path("orderId") orderId: String,
        @Body request: AssignCaptainRequestDto
    ): ApiResponseDto<OrderDto>

    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: String,
        @Body request: CancelOrderRequestDto
    ): ApiResponseDto<OrderDto>

    @POST("orders/{orderId}/reject")
    suspend fun rejectOrder(
        @Path("orderId") orderId: String,
        @Body request: RejectOrderRequestDto
    ): ApiResponseDto<OrderDto>

    @POST("orders/{orderId}/timeline")
    suspend fun addTimelineEvent(
        @Path("orderId") orderId: String,
        @Body request: AddTimelineEventRequestDto
    ): ApiResponseDto<OrderDto>
}
