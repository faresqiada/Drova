package com.example.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.core.di.ServiceLocator
import com.example.data.mock.DrovaMockData
import com.example.domain.model.*
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.RestaurantRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class CustomerTab {
    HOME,
    ORDERS,
    PROFILE
}

sealed class CustomerDestination {
    object Main : CustomerDestination()
    data class RestaurantDetail(val restaurantId: String) : CustomerDestination()
    object Cart : CustomerDestination()
    object Checkout : CustomerDestination()
    data class OrderTracking(val orderId: String) : CustomerDestination()
    data class OrderDetail(val orderId: String) : CustomerDestination()
}

class CustomerViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val restaurantRepository: RestaurantRepository = ServiceLocator.restaurantRepository,
    private val orderRepository: OrderRepository = ServiceLocator.orderRepository
) : ViewModel() {

    // ==========================================
    // Navigation & UI Navigation Stack
    // ==========================================
    private val _currentTab = MutableStateFlow(CustomerTab.HOME)
    val currentTab: StateFlow<CustomerTab> = _currentTab.asStateFlow()

    private val _currentDestination = MutableStateFlow<CustomerDestination>(CustomerDestination.Main)
    val currentDestination: StateFlow<CustomerDestination> = _currentDestination.asStateFlow()

    // ==========================================
    // Auth & User State
    // ==========================================
    val currentUser: StateFlow<User?> = authRepository.currentUser

    // ==========================================
    // Addresses
    // ==========================================
    private val noRealAddress = SavedAddress(
        id = "__no_real_address__",
        labelAr = "لم تتم إضافة عنوان",
        labelEn = "No address added",
        districtAr = "",
        detailedAddressAr = "",
        isDefault = false
    )

    private val _savedAddresses = MutableStateFlow(
        if (BuildConfig.DEBUG) DrovaMockData.sampleAddresses else emptyList()
    )
    val savedAddresses: StateFlow<List<SavedAddress>> = _savedAddresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow(
        if (BuildConfig.DEBUG) DrovaMockData.sampleAddresses.first() else noRealAddress
    )
    val selectedAddress: StateFlow<SavedAddress> = _selectedAddress.asStateFlow()

    // ==========================================
    // Restaurants & Search / Filters
    // ==========================================
    val restaurants: StateFlow<List<Restaurant>> = restaurantRepository.restaurants
    val categories: List<String> = restaurantRepository.getCategories()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        restaurants,
        _selectedCategory,
        _searchQuery
    ) { list, category, query ->
        list.filter { rest ->
            val matchCategory = category == "الكل" || rest.categoryAr.contains(category) || rest.categoryEn.contains(category, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                rest.nameAr.contains(query, ignoreCase = true) ||
                rest.nameEn.contains(query, ignoreCase = true) ||
                rest.categoryAr.contains(query, ignoreCase = true) ||
                rest.menu.any { it.nameAr.contains(query, ignoreCase = true) || it.nameEn.contains(query, ignoreCase = true) }
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Featured restaurants for carousel
    val featuredRestaurants: StateFlow<List<Restaurant>> = restaurants.map { list ->
        list.filter { it.rating >= 4.8 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==========================================
    // Active Restaurant & Product Details Modal
    // ==========================================
    private val _activeRestaurant = MutableStateFlow<Restaurant?>(null)
    val activeRestaurant: StateFlow<Restaurant?> = _activeRestaurant.asStateFlow()

    private val _productDetailItem = MutableStateFlow<MenuItem?>(null)
    val productDetailItem: StateFlow<MenuItem?> = _productDetailItem.asStateFlow()

    private val _productDetailRestaurant = MutableStateFlow<Restaurant?>(null)
    val productDetailRestaurant: StateFlow<Restaurant?> = _productDetailRestaurant.asStateFlow()

    // ==========================================
    // Cart State
    // ==========================================
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartRestaurant = MutableStateFlow<Restaurant?>(null)
    val cartRestaurant: StateFlow<Restaurant?> = _cartRestaurant.asStateFlow()

    val cartItemCount: StateFlow<Int> = _cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotalEgp: StateFlow<Double> = _cartItems.map { items ->
        items.sumOf { it.totalEgp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartDeliveryFeeEgp: StateFlow<Double> = _cartRestaurant.map { rest ->
        rest?.deliveryFeeEgp ?: 20.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20.0)

    val platformFeeEgp: Double = 5.0

    val cartTotalEgp: StateFlow<Double> = combine(
        cartSubtotalEgp,
        cartDeliveryFeeEgp
    ) { subtotal, deliveryFee ->
        if (subtotal > 0) subtotal + deliveryFee + platformFeeEgp else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Conflict Dialog State (when adding item from another restaurant)
    private val _conflictItemPending = MutableStateFlow<Triple<Restaurant, MenuItem, Int>?>(null)
    val conflictItemPending = _conflictItemPending.asStateFlow()

    // ==========================================
    // Checkout State
    // ==========================================
    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.CASH_ON_DELIVERY)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _specialInstructions = MutableStateFlow("")
    val specialInstructions: StateFlow<String> = _specialInstructions.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    // Wallet balance
    val customerWalletBalanceEgp = MutableStateFlow(540.00)

    // ==========================================
    // Orders State
    // ==========================================
    val allOrders: StateFlow<List<Order>> = orderRepository.allOrders

    val activeCustomerOrders: StateFlow<List<Order>> = allOrders.map { list ->
        list.filter { !it.status.isTerminal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastCustomerOrders: StateFlow<List<Order>> = allOrders.map { list ->
        list.filter { it.status.isTerminal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Single order for tracking/detail view
    private val _currentTrackingOrderId = MutableStateFlow<String?>(null)
    val currentTrackingOrder: StateFlow<Order?> = combine(
        allOrders,
        _currentTrackingOrderId
    ) { orders, id ->
        if (id != null) orders.find { it.id == id } else orders.firstOrNull { !it.status.isTerminal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ==========================================
    // Navigation Functions
    // ==========================================
    fun selectTab(tab: CustomerTab) {
        _currentTab.value = tab
        _currentDestination.value = CustomerDestination.Main
    }

    fun navigateToRestaurant(restaurantId: String) {
        val rest = restaurantRepository.getRestaurantById(restaurantId)
        _activeRestaurant.value = rest
        _currentDestination.value = CustomerDestination.RestaurantDetail(restaurantId)
    }

    fun navigateToCart() {
        _currentDestination.value = CustomerDestination.Cart
    }

    fun navigateToCheckout() {
        if (_cartItems.value.isNotEmpty()) {
            _currentDestination.value = CustomerDestination.Checkout
        }
    }

    fun navigateToOrderTracking(orderId: String) {
        _currentTrackingOrderId.value = orderId
        _currentDestination.value = CustomerDestination.OrderTracking(orderId)
    }

    fun navigateToOrderDetail(orderId: String) {
        _currentTrackingOrderId.value = orderId
        _currentDestination.value = CustomerDestination.OrderDetail(orderId)
    }

    fun navigateBack() {
        when (_currentDestination.value) {
            is CustomerDestination.RestaurantDetail,
            CustomerDestination.Cart -> _currentDestination.value = CustomerDestination.Main
            CustomerDestination.Checkout -> _currentDestination.value = CustomerDestination.Cart
            is CustomerDestination.OrderTracking,
            is CustomerDestination.OrderDetail -> {
                _currentDestination.value = CustomerDestination.Main
                _currentTab.value = CustomerTab.ORDERS
            }
            CustomerDestination.Main -> { /* Handled at app level */ }
        }
    }

    // ==========================================
    // Filter & Search Functions
    // ==========================================
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ==========================================
    // Address Management
    // ==========================================
    fun selectAddress(address: SavedAddress) {
        _selectedAddress.value = address
    }

    fun addNewAddress(labelAr: String, districtAr: String, detailedAddressAr: String) {
        val newAddr = SavedAddress(
            id = "addr_${System.currentTimeMillis()}",
            labelAr = labelAr,
            labelEn = labelAr,
            districtAr = districtAr,
            detailedAddressAr = detailedAddressAr,
            isDefault = false
        )
        _savedAddresses.update { listOf(newAddr) + it }
        _selectedAddress.value = newAddr
    }

    // ==========================================
    // Product Details Modal
    // ==========================================
    fun openProductDetails(item: MenuItem, restaurant: Restaurant) {
        _productDetailItem.value = item
        _productDetailRestaurant.value = restaurant
    }

    fun closeProductDetails() {
        _productDetailItem.value = null
        _productDetailRestaurant.value = null
    }

    // ==========================================
    // Cart Functions
    // ==========================================
    fun addToCart(restaurant: Restaurant, item: MenuItem, quantity: Int, notes: String = ""): Boolean {
        val currentRest = _cartRestaurant.value
        if (currentRest != null && currentRest.id != restaurant.id && _cartItems.value.isNotEmpty()) {
            // Conflict with another restaurant
            _conflictItemPending.value = Triple(restaurant, item, quantity)
            return false
        }

        _cartRestaurant.value = restaurant
        _cartItems.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.menuItem.id == item.id }
            if (existingIndex != -1) {
                currentList.mapIndexed { index, cartItem ->
                    if (index == existingIndex) {
                        cartItem.copy(
                            quantity = cartItem.quantity + quantity,
                            specialNotes = if (notes.isNotBlank()) notes else cartItem.specialNotes
                        )
                    } else cartItem
                }
            } else {
                currentList + CartItem(
                    menuItem = item,
                    restaurantId = restaurant.id,
                    restaurantNameAr = restaurant.nameAr,
                    quantity = quantity,
                    specialNotes = notes
                )
            }
        }
        closeProductDetails()
        return true
    }

    fun confirmClearCartAndAddNewItem() {
        val pending = _conflictItemPending.value ?: return
        clearCart()
        _cartRestaurant.value = pending.first
        _cartItems.value = listOf(
            CartItem(
                menuItem = pending.second,
                restaurantId = pending.first.id,
                restaurantNameAr = pending.first.nameAr,
                quantity = pending.third
            )
        )
        _conflictItemPending.value = null
        closeProductDetails()
    }

    fun dismissCartConflict() {
        _conflictItemPending.value = null
    }

    fun updateCartItemQuantity(menuItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeCartItem(menuItemId)
            return
        }
        _cartItems.update { list ->
            list.map { item ->
                if (item.menuItem.id == menuItemId) item.copy(quantity = newQuantity) else item
            }
        }
    }

    fun removeCartItem(menuItemId: String) {
        _cartItems.update { list ->
            val updated = list.filter { it.menuItem.id != menuItemId }
            if (updated.isEmpty()) {
                _cartRestaurant.value = null
            }
            updated
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartRestaurant.value = null
    }

    // ==========================================
    // Checkout & Order Creation
    // ==========================================
    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun updateSpecialInstructions(notes: String) {
        _specialInstructions.value = notes
    }

    fun placeOrder() {
        val user = currentUser.value
        val restaurant = _cartRestaurant.value
        val items = _cartItems.value
        val address = _selectedAddress.value

        if (restaurant == null || items.isEmpty() || address.id == noRealAddress.id) return

        viewModelScope.launch {
            _isPlacingOrder.value = true
            delay(600) // Brief simulation of network order creation

            val orderItems = items.map { cartItem ->
                OrderItem(
                    id = "item_${System.currentTimeMillis() % 10000}_${cartItem.menuItem.id}",
                    nameAr = cartItem.menuItem.nameAr,
                    nameEn = cartItem.menuItem.nameEn,
                    quantity = cartItem.quantity,
                    unitPriceEgp = cartItem.menuItem.price,
                    notes = cartItem.specialNotes
                )
            }

            val subtotal = items.sumOf { it.totalEgp }
            val deliveryFee = restaurant.deliveryFeeEgp
            val total = subtotal + deliveryFee + platformFeeEgp

            val timeFormatter = SimpleDateFormat("اليوم، hh:mm a", Locale("ar"))
            val formattedTime = timeFormatter.format(Date())

            val newOrder = Order(
                id = "ord_${System.currentTimeMillis() % 100000}",
                orderNumber = "DRV-${(1000..9999).random()}",
                customerId = user?.id ?: "cust_1",
                customerName = user?.fullName ?: "أحمد مصطفى",
                customerPhone = user?.phone ?: "+201012345678",
                deliveryAddressAr = "${address.detailedAddressAr}، ${address.districtAr}",
                restaurantId = restaurant.id,
                restaurantNameAr = restaurant.nameAr,
                restaurantAddressAr = restaurant.addressAr,
                captainId = null,
                captainName = null,
                captainPhone = null,
                items = orderItems,
                subtotalEgp = subtotal,
                deliveryFeeEgp = deliveryFee,
                platformFeeEgp = platformFeeEgp,
                totalEgp = total,
                status = OrderStatus.CREATED,
                paymentMethod = _selectedPaymentMethod.value,
                createdAtFormatted = formattedTime,
                estimatedArrivalMin = restaurant.deliveryTimeMin,
                specialInstructions = _specialInstructions.value
            )

            val createdOrderId = orderRepository.createNewOrder(newOrder)

            // Deduct wallet balance if paid via wallet
            if (_selectedPaymentMethod.value == PaymentMethod.WALLET) {
                customerWalletBalanceEgp.update { (it - total).coerceAtLeast(0.0) }
            }

            clearCart()
            _specialInstructions.value = ""
            _isPlacingOrder.value = false

            // Navigate immediately to live tracking
            navigateToOrderTracking(createdOrderId)
        }
    }

    // ==========================================
    // Order Lifecycle Simulation (For verification & live testing)
    // ==========================================
    fun advanceOrderSimulation(orderId: String) {
        val order = orderRepository.getOrderById(orderId) ?: return
        val nextStatus = when (order.status) {
            OrderStatus.CREATED -> OrderStatus.RESTAURANT_CONFIRMED
            OrderStatus.RESTAURANT_CONFIRMED -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.READY_FOR_PICKUP
            OrderStatus.READY_FOR_PICKUP -> OrderStatus.CAPTAIN_ASSIGNED
            OrderStatus.CAPTAIN_ASSIGNED -> OrderStatus.PICKED_UP
            OrderStatus.PICKED_UP -> OrderStatus.ON_THE_WAY
            OrderStatus.ON_THE_WAY -> OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.REJECTED -> null
        } ?: return

        viewModelScope.launch {
            orderRepository.advanceOrderStatus(orderId, nextStatus)
        }
    }

    fun cancelOrder(orderId: String, reason: String = "طلب العميل إلغاء الطلب") {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId, reason)
        }
    }

    fun reorder(pastOrder: Order) {
        val rest = restaurantRepository.getRestaurantById(pastOrder.restaurantId) ?: return
        clearCart()
        _cartRestaurant.value = rest
        val newCartItems = pastOrder.items.mapNotNull { orderItem ->
            val matchingMenuItem = rest.menu.find { it.nameAr == orderItem.nameAr }
                ?: MenuItem(
                    id = "m_${orderItem.id}",
                    nameAr = orderItem.nameAr,
                    nameEn = orderItem.nameEn,
                    descriptionAr = "",
                    price = orderItem.unitPriceEgp,
                    category = "عام"
                )
            CartItem(
                menuItem = matchingMenuItem,
                restaurantId = rest.id,
                restaurantNameAr = rest.nameAr,
                quantity = orderItem.quantity,
                specialNotes = orderItem.notes
            )
        }
        _cartItems.value = newCartItems
        _currentDestination.value = CustomerDestination.Cart
    }
}
