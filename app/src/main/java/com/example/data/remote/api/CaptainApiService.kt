package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.http.*

interface CaptainApiService {
    @GET("captains/{captainId}/earnings")
    suspend fun getEarnings(@Path("captainId") captainId: String): ApiResponseDto<CaptainEarningsDto>

    @GET("captains/{captainId}/shift")
    suspend fun getShiftData(@Path("captainId") captainId: String): ApiResponseDto<CaptainShiftDataDto>

    @PATCH("captains/{captainId}/online-status")
    suspend fun toggleOnlineStatus(
        @Path("captainId") captainId: String,
        @Body request: ToggleOnlineRequestDto
    ): ApiResponseDto<Unit>

    @PATCH("captains/{captainId}/mode")
    suspend fun setCaptainMode(
        @Path("captainId") captainId: String,
        @Body request: SetCaptainModeRequestDto
    ): ApiResponseDto<Unit>

    @GET("captains/{captainId}/tasks/active")
    suspend fun getActiveTask(@Path("captainId") captainId: String): ApiResponseDto<DeliveryTaskDto>

    @GET("captains/{captainId}/tasks/completed")
    suspend fun getCompletedTasks(@Path("captainId") captainId: String): ApiResponseDto<List<DeliveryTaskDto>>

    @POST("captains/{captainId}/tasks/{orderId}/accept")
    suspend fun acceptTask(
        @Path("captainId") captainId: String,
        @Path("orderId") orderId: String
    ): ApiResponseDto<DeliveryTaskDto>

    @POST("captains/{captainId}/tasks/{orderId}/reject")
    suspend fun rejectTask(
        @Path("captainId") captainId: String,
        @Path("orderId") orderId: String
    ): ApiResponseDto<Unit>
}
