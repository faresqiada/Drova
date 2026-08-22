package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/register/customer")
    suspend fun registerCustomer(@Body request: CustomerRegisterRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/register/restaurant")
    suspend fun registerRestaurant(@Body request: RestaurantRegisterRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/register/captain")
    suspend fun registerCaptain(@Body request: CaptainRegisterRequestDto): ApiResponseDto<AuthResponseDto>

    @GET("auth/profile")
    suspend fun getCurrentUserProfile(): ApiResponseDto<UserDto>

    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): ApiResponseDto<UserDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponseDto<Unit>
}
