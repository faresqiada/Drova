package com.example.data.local.source

import com.example.data.mock.DrovaMockData
import com.example.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface OrderLocalDataSource {
    val ordersFlow: StateFlow<List<Order>>
    fun getAllOrders(): List<Order>
    fun getOrderById(orderId: String): Order?
    fun saveOrder(order: Order)
    fun updateOrder(order: Order): Boolean
    fun removeOrder(orderId: String): Boolean
}

class OrderLocalDataSourceImpl(
    initialOrders: List<Order> = DrovaMockData.sampleOrders
) : OrderLocalDataSource {
    private val _orders = MutableStateFlow(initialOrders)
    override val ordersFlow: StateFlow<List<Order>> = _orders.asStateFlow()

    override fun getAllOrders(): List<Order> = _orders.value

    override fun getOrderById(orderId: String): Order? {
        return _orders.value.find { it.id == orderId || it.orderNumber == orderId }
    }

    override fun saveOrder(order: Order) {
        _orders.update { current ->
            val index = current.indexOfFirst { it.id == order.id || it.orderNumber == order.orderNumber }
            if (index >= 0) {
                current.toMutableList().apply { set(index, order) }
            } else {
                listOf(order) + current
            }
        }
    }

    override fun updateOrder(order: Order): Boolean {
        var updated = false
        _orders.update { current ->
            val index = current.indexOfFirst { it.id == order.id || it.orderNumber == order.orderNumber }
            if (index >= 0) {
                updated = true
                current.toMutableList().apply { set(index, order) }
            } else {
                current
            }
        }
        return updated
    }

    override fun removeOrder(orderId: String): Boolean {
        var removed = false
        _orders.update { current ->
            val filtered = current.filterNot { it.id == orderId || it.orderNumber == orderId }
            if (filtered.size != current.size) {
                removed = true
                filtered
            } else {
                current
            }
        }
        return removed
    }
}

interface RestaurantLocalDataSource {
    val restaurantsFlow: StateFlow<List<Restaurant>>
    fun getAllRestaurants(): List<Restaurant>
    fun getRestaurantById(id: String): Restaurant?
    fun updateRestaurant(restaurant: Restaurant)
    fun addMenuItem(restaurantId: String, item: MenuItem)
    fun updateMenuItem(restaurantId: String, item: MenuItem)
    fun deleteMenuItem(restaurantId: String, itemId: String)
}

class RestaurantLocalDataSourceImpl(
    initialRestaurants: List<Restaurant> = DrovaMockData.sampleRestaurants
) : RestaurantLocalDataSource {
    private val _restaurants = MutableStateFlow(initialRestaurants)
    override val restaurantsFlow: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    override fun getAllRestaurants(): List<Restaurant> = _restaurants.value

    override fun getRestaurantById(id: String): Restaurant? =
        _restaurants.value.find { it.id == id }

    override fun updateRestaurant(restaurant: Restaurant) {
        _restaurants.update { current ->
            current.map { if (it.id == restaurant.id) restaurant else it }
        }
    }

    override fun addMenuItem(restaurantId: String, item: MenuItem) {
        _restaurants.update { current ->
            current.map { rest ->
                if (rest.id == restaurantId) {
                    rest.copy(menu = rest.menu + item)
                } else rest
            }
        }
    }

    override fun updateMenuItem(restaurantId: String, item: MenuItem) {
        _restaurants.update { current ->
            current.map { rest ->
                if (rest.id == restaurantId) {
                    rest.copy(menu = rest.menu.map { if (it.id == item.id) item else it })
                } else rest
            }
        }
    }

    override fun deleteMenuItem(restaurantId: String, itemId: String) {
        _restaurants.update { current ->
            current.map { rest ->
                if (rest.id == restaurantId) {
                    rest.copy(menu = rest.menu.filterNot { it.id == itemId })
                } else rest
            }
        }
    }
}
