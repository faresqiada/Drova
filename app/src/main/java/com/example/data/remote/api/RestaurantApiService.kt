package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.http.*

interface RestaurantApiService {
    @GET("restaurants")
    suspend fun getRestaurants(): ApiResponseDto<List<RestaurantDto>>

    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): ApiResponseDto<RestaurantDto>

    @PATCH("restaurants/{id}/open-status")
    suspend fun toggleOpenStatus(
        @Path("id") id: String,
        @Body request: ToggleOpenStatusRequestDto
    ): ApiResponseDto<RestaurantDto>

    @PUT("restaurants/{id}/profile")
    suspend fun updateRestaurantProfile(
        @Path("id") id: String,
        @Body request: UpdateRestaurantProfileRequestDto
    ): ApiResponseDto<RestaurantDto>

    @POST("restaurants/{id}/menu")
    suspend fun addMenuItem(
        @Path("id") id: String,
        @Body item: MenuItemDto
    ): ApiResponseDto<MenuItemDto>

    @PUT("restaurants/{id}/menu/{itemId}")
    suspend fun updateMenuItem(
        @Path("id") id: String,
        @Path("itemId") itemId: String,
        @Body item: MenuItemDto
    ): ApiResponseDto<MenuItemDto>

    @DELETE("restaurants/{id}/menu/{itemId}")
    suspend fun deleteMenuItem(
        @Path("id") id: String,
        @Path("itemId") itemId: String
    ): ApiResponseDto<Unit>

    @PATCH("restaurants/{id}/menu/{itemId}/availability")
    suspend fun toggleMenuItemAvailability(
        @Path("id") id: String,
        @Path("itemId") itemId: String,
        @Body request: ToggleMenuItemAvailabilityRequestDto
    ): ApiResponseDto<MenuItemDto>
}
