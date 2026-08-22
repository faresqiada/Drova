package com.example.data.repository

import com.example.data.local.source.RestaurantLocalDataSource
import com.example.data.local.source.RestaurantLocalDataSourceImpl
import com.example.data.remote.dto.toDto
import com.example.data.remote.source.RestaurantRemoteDataSource
import com.example.domain.model.MenuItem
import com.example.domain.model.Restaurant
import com.example.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.StateFlow

class RestaurantRepositoryImpl(
    private val localDataSource: RestaurantLocalDataSource = RestaurantLocalDataSourceImpl(),
    private val remoteDataSource: RestaurantRemoteDataSource? = null
) : RestaurantRepository {

    override val restaurants: StateFlow<List<Restaurant>> = localDataSource.restaurantsFlow

    override fun getRestaurantById(id: String): Restaurant? {
        return localDataSource.getRestaurantById(id)
    }

    override fun getCategories(): List<String> {
        return listOf("الكل", "شاورما ومشويات", "برجر وسريع", "مأكولات شعبية", "بيتزا وإيطالي", "حلويات ومخابز", "أسماك وبحرية")
    }

    override suspend fun toggleRestaurantOpenStatus(restaurantId: String, isOpen: Boolean) {
        val existing = localDataSource.getRestaurantById(restaurantId) ?: return
        localDataSource.updateRestaurant(existing.copy(isOpen = isOpen))

        remoteDataSource?.let { remote ->
            try { remote.toggleOpenStatus(restaurantId, isOpen) } catch (e: Exception) {}
        }
    }

    override suspend fun toggleMenuItemAvailability(restaurantId: String, itemId: String, isAvailable: Boolean) {
        val existing = localDataSource.getRestaurantById(restaurantId) ?: return
        val updatedMenu = existing.menu.map {
            if (it.id == itemId) it.copy(isAvailable = isAvailable) else it
        }
        localDataSource.updateRestaurant(existing.copy(menu = updatedMenu))

        remoteDataSource?.let { remote ->
            try { remote.toggleMenuItemAvailability(restaurantId, itemId, isAvailable) } catch (e: Exception) {}
        }
    }

    override suspend fun addMenuItem(restaurantId: String, item: MenuItem) {
        localDataSource.addMenuItem(restaurantId, item)

        remoteDataSource?.let { remote ->
            try { remote.addMenuItem(restaurantId, item.toDto()) } catch (e: Exception) {}
        }
    }

    override suspend fun updateMenuItem(restaurantId: String, item: MenuItem) {
        localDataSource.updateMenuItem(restaurantId, item)

        remoteDataSource?.let { remote ->
            try { remote.updateMenuItem(restaurantId, item.id, item.toDto()) } catch (e: Exception) {}
        }
    }

    override suspend fun deleteMenuItem(restaurantId: String, itemId: String) {
        localDataSource.deleteMenuItem(restaurantId, itemId)

        remoteDataSource?.let { remote ->
            try { remote.deleteMenuItem(restaurantId, itemId) } catch (e: Exception) {}
        }
    }

    override suspend fun updateRestaurantProfile(
        restaurantId: String,
        nameAr: String,
        descriptionAr: String,
        addressAr: String,
        phone: String,
        openingHours: String
    ) {
        val existing = localDataSource.getRestaurantById(restaurantId) ?: return
        val updated = existing.copy(
            nameAr = nameAr,
            descriptionAr = descriptionAr,
            addressAr = addressAr,
            phone = phone,
            openingHours = openingHours
        )
        localDataSource.updateRestaurant(updated)
    }
}
