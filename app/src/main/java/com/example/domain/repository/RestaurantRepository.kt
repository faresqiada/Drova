package com.example.domain.repository

import com.example.domain.model.MenuItem
import com.example.domain.model.Restaurant
import kotlinx.coroutines.flow.StateFlow

interface RestaurantRepository {
    val restaurants: StateFlow<List<Restaurant>>

    fun getRestaurantById(id: String): Restaurant?
    fun getCategories(): List<String>
    suspend fun toggleRestaurantOpenStatus(restaurantId: String, isOpen: Boolean)
    suspend fun toggleMenuItemAvailability(restaurantId: String, itemId: String, isAvailable: Boolean)
    suspend fun addMenuItem(restaurantId: String, item: MenuItem)
    suspend fun updateMenuItem(restaurantId: String, item: MenuItem)
    suspend fun deleteMenuItem(restaurantId: String, itemId: String)
    suspend fun updateRestaurantProfile(
        restaurantId: String,
        nameAr: String,
        descriptionAr: String,
        addressAr: String,
        phone: String,
        openingHours: String
    )
}

