package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.http.*

interface WalletApiService {
    @GET("wallet/{userId}/transactions")
    suspend fun getTransactions(@Path("userId") userId: String): ApiResponseDto<List<CaptainTransactionDto>>

    @POST("wallet/{userId}/payout")
    suspend fun requestPayout(
        @Path("userId") userId: String,
        @Body request: PayoutRequestDto
    ): ApiResponseDto<CaptainTransactionDto>
}

interface NotificationApiService {
    @GET("notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): ApiResponseDto<List<CaptainNotificationDto>>

    @PATCH("notifications/{userId}/{notificationId}/read")
    suspend fun markAsRead(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): ApiResponseDto<Unit>

    @POST("notifications/{userId}/clear-all")
    suspend fun clearAllNotifications(@Path("userId") userId: String): ApiResponseDto<Unit>
}
