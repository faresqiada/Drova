package com.example.presentation.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.core.di.ServiceLocator
import com.example.domain.model.*
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class RestaurantMainTab(
    val titleAr: String,
    val titleEn: String
) {
    DASHBOARD("الرئيسية", "Home"),
    ORDERS("الطلبات", "Orders"),
    MENU("القائمة", "Menu"),
    FINANCE("المالية", "Finance"),
    PROFILE("الحساب", "Account")
}

enum class OrderQueueFilter(
    val titleAr: String,
    val titleEn: String
) {
    ALL("الكل", "All"),
    NEW("جديد", "New"),
    PREPARING("قيد التحضير", "Preparing"),
    READY("جاهز للاستلام", "Ready"),
    COMPLETED("مكتمل / سابق", "Completed")
}

data class RestaurantAlert(
    val id: String = UUID.randomUUID().toString(),
    val titleAr: String,
    val titleEn: String = "",
    val messageAr: String,
    val messageEn: String = "",
    val type: AlertType = AlertType.INFO,
    val orderId: String? = null,
    val timestamp: String = "الآن"
)

enum class AlertType {
    INFO,
    SUCCESS,
    WARNING,
    NEW_ORDER
}

data class SettlementRecord(
    val id: String,
    val referenceNumber: String,
    val periodAr: String,
    val dateFormatted: String,
    val ordersCount: Int,
    val grossSalesEgp: Double,
    val commissionEgp: Double,
    val netPayoutEgp: Double,
    val statusAr: String = "تم التحويل بنجاح",
    val payoutMethodAr: String = "حساب بنكي (البنك الأهلي المصري)"
)

class RestaurantViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val restaurantRepository: RestaurantRepository = ServiceLocator.restaurantRepository,
    private val orderRepository: OrderRepository = ServiceLocator.orderRepository
) : ViewModel() {

    // ==========================================
    // Auth & Restaurant Profile State
    // ==========================================
    val currentUser: StateFlow<User?> = authRepository.currentUser

    val restaurantData: StateFlow<Restaurant?> = restaurantRepository.restaurants.combine(currentUser) { list, user ->
        list.find { it.id == (user?.id ?: "rest_1") } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ==========================================
    // Navigation & Tabs
    // ==========================================
    private val _selectedTab = MutableStateFlow(RestaurantMainTab.DASHBOARD)
    val selectedTab: StateFlow<RestaurantMainTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: RestaurantMainTab) {
        _selectedTab.value = tab
    }

    // ==========================================
    // Orders & Live Kitchen Queue
    // ==========================================
    val allOrders: StateFlow<List<Order>> = orderRepository.allOrders

    val restaurantOrders: StateFlow<List<Order>> = combine(
        allOrders,
        restaurantData
    ) { orders, rest ->
        val restId = rest?.id ?: "rest_1"
        orders.filter { it.restaurantId == restId || it.restaurantId.isEmpty() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedQueueFilter = MutableStateFlow(OrderQueueFilter.ALL)
    val selectedQueueFilter: StateFlow<OrderQueueFilter> = _selectedQueueFilter.asStateFlow()

    private val _orderSearchQuery = MutableStateFlow("")
    val orderSearchQuery: StateFlow<String> = _orderSearchQuery.asStateFlow()

    val filteredOrders: StateFlow<List<Order>> = combine(
        restaurantOrders,
        _selectedQueueFilter,
        _orderSearchQuery
    ) { orders, filter, query ->
        orders.filter { order ->
            val matchFilter = when (filter) {
                OrderQueueFilter.ALL -> true
                OrderQueueFilter.NEW -> order.status == OrderStatus.CREATED || order.status == OrderStatus.RESTAURANT_CONFIRMED
                OrderQueueFilter.PREPARING -> order.status == OrderStatus.PREPARING
                OrderQueueFilter.READY -> order.status == OrderStatus.READY_FOR_PICKUP || order.status == OrderStatus.CAPTAIN_ASSIGNED
                OrderQueueFilter.COMPLETED -> order.status.isTerminal || order.status == OrderStatus.DELIVERED || order.status == OrderStatus.ON_THE_WAY || order.status == OrderStatus.PICKED_UP
            }
            val matchQuery = query.isBlank() ||
                order.orderNumber.contains(query, ignoreCase = true) ||
                order.customerName.contains(query, ignoreCase = true) ||
                order.items.any { it.nameAr.contains(query, ignoreCase = true) }
            matchFilter && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected order for detailed modal
    private val _selectedOrderDetail = MutableStateFlow<Order?>(null)
    val selectedOrderDetail: StateFlow<Order?> = _selectedOrderDetail.asStateFlow()

    fun openOrderDetail(order: Order) {
        _selectedOrderDetail.value = order
    }

    fun closeOrderDetail() {
        _selectedOrderDetail.value = null
    }

    fun selectQueueFilter(filter: OrderQueueFilter) {
        _selectedQueueFilter.value = filter
    }

    fun updateOrderSearchQuery(query: String) {
        _orderSearchQuery.value = query
    }

    // ==========================================
    // KPIs & Operations Summary
    // ==========================================
    val todayOrdersCount: StateFlow<Int> = restaurantOrders.map { orders ->
        orders.count { it.status != OrderStatus.CANCELLED && it.status != OrderStatus.REJECTED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeOrdersCount: StateFlow<Int> = restaurantOrders.map { orders ->
        orders.count { !it.status.isTerminal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val preparingOrdersCount: StateFlow<Int> = restaurantOrders.map { orders ->
        orders.count { it.status == OrderStatus.PREPARING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val readyOrdersCount: StateFlow<Int> = restaurantOrders.map { orders ->
        orders.count { it.status == OrderStatus.READY_FOR_PICKUP || it.status == OrderStatus.CAPTAIN_ASSIGNED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedOrdersCount: StateFlow<Int> = restaurantOrders.map { orders ->
        orders.count { it.status == OrderStatus.COMPLETED || it.status == OrderStatus.DELIVERED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingActionsCount: StateFlow<Int> = restaurantOrders.map { orders ->
        orders.count { it.status == OrderStatus.CREATED || it.status == OrderStatus.RESTAURANT_CONFIRMED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaySalesEgp: StateFlow<Double> = restaurantOrders.map { orders ->
        orders.filter { it.status != OrderStatus.CANCELLED && it.status != OrderStatus.REJECTED }
            .sumOf { it.subtotalEgp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ==========================================
    // Order Lifecycle Actions (Restaurant Context)
    // ==========================================
    fun confirmOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.advanceOrderStatus(orderId, OrderStatus.RESTAURANT_CONFIRMED)
            triggerAlert(
                RestaurantAlert(
                    titleAr = "تم تأكيد الطلب",
                    titleEn = "Order Confirmed",
                    messageAr = "تم إشعار العميل بقبول الطلب وجاهزية المطبخ لبدء التحضير",
                    messageEn = "Customer notified of order confirmation",
                    type = AlertType.SUCCESS,
                    orderId = orderId
                )
            )
            refreshSelectedOrderDetail(orderId)
        }
    }

    fun startPreparing(orderId: String) {
        viewModelScope.launch {
            orderRepository.advanceOrderStatus(orderId, OrderStatus.PREPARING)
            triggerAlert(
                RestaurantAlert(
                    titleAr = "بدء التحضير بالمطبخ",
                    titleEn = "Preparation Started",
                    messageAr = "الطلب قيد الإعداد الآن في مطبخ المطعم",
                    messageEn = "Order is now in kitchen preparation",
                    type = AlertType.INFO,
                    orderId = orderId
                )
            )
            refreshSelectedOrderDetail(orderId)
        }
    }

    fun markReadyForPickup(orderId: String) {
        viewModelScope.launch {
            orderRepository.advanceOrderStatus(orderId, OrderStatus.READY_FOR_PICKUP)
            triggerAlert(
                RestaurantAlert(
                    titleAr = "الطلب جاهز للاستلام",
                    titleEn = "Ready for Pickup",
                    messageAr = "تم إشعار الكابتن بأن الطلب معبأ وجاهز للاستلام من المطعم",
                    messageEn = "Captain has been notified that order is ready for pickup",
                    type = AlertType.SUCCESS,
                    orderId = orderId
                )
            )
            refreshSelectedOrderDetail(orderId)
        }
    }

    fun rejectOrder(orderId: String, reason: String = "المطعم غير قادر على تلبية الطلب حالياً") {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId, reason)
            triggerAlert(
                RestaurantAlert(
                    titleAr = "تم رفض الطلب",
                    titleEn = "Order Rejected",
                    messageAr = "تم رفض الطلب وتسجيل السبب: $reason",
                    messageEn = "Order rejected: $reason",
                    type = AlertType.WARNING,
                    orderId = orderId
                )
            )
            refreshSelectedOrderDetail(orderId)
        }
    }

    fun simulateCaptainPickup(orderId: String) {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            orderRepository.advanceOrderStatus(orderId, OrderStatus.PICKED_UP)
            refreshSelectedOrderDetail(orderId)
        }
    }

    fun simulateOrderDelivery(orderId: String) {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            orderRepository.advanceOrderStatus(orderId, OrderStatus.DELIVERED)
            refreshSelectedOrderDetail(orderId)
        }
    }

    private fun refreshSelectedOrderDetail(orderId: String) {
        val updated = orderRepository.getOrderById(orderId)
        if (updated != null && _selectedOrderDetail.value?.id == orderId) {
            _selectedOrderDetail.value = updated
        }
    }

    // ==========================================
    // Store Availability Toggle & Profile
    // ==========================================
    fun toggleRestaurantStatus(isOpen: Boolean) {
        val restId = restaurantData.value?.id ?: "rest_1"
        viewModelScope.launch {
            restaurantRepository.toggleRestaurantOpenStatus(restId, isOpen)
            triggerAlert(
                RestaurantAlert(
                    titleAr = if (isOpen) "المطعم متاح للطلب الآن" else "تم إيقاف استقبال الطلبات مؤقتاً",
                    titleEn = if (isOpen) "Store is Online" else "Store is Paused",
                    messageAr = if (isOpen) "يستطيع العملاء الآن مشاهدة المنيو وتقديم الطلبات" else "لن يظهر المطعم كمفتوح في تطبيق العملاء",
                    type = if (isOpen) AlertType.SUCCESS else AlertType.WARNING
                )
            )
        }
    }

    fun updateStoreProfile(
        nameAr: String,
        descriptionAr: String,
        addressAr: String,
        phone: String,
        openingHours: String
    ) {
        val restId = restaurantData.value?.id ?: "rest_1"
        viewModelScope.launch {
            restaurantRepository.updateRestaurantProfile(
                restaurantId = restId,
                nameAr = nameAr,
                descriptionAr = descriptionAr,
                addressAr = addressAr,
                phone = phone,
                openingHours = openingHours
            )
            triggerAlert(
                RestaurantAlert(
                    titleAr = "تم حفظ بيانات المطعم",
                    titleEn = "Profile Saved",
                    messageAr = "تم تحديث البيانات بنجاح",
                    type = AlertType.SUCCESS
                )
            )
        }
    }

    // ==========================================
    // In-App Alerts System
    // ==========================================
    private val _activeAlert = MutableStateFlow<RestaurantAlert?>(
        if (BuildConfig.DEBUG) {
            RestaurantAlert(
                titleAr = "طلب جديد وارد! (#DRV-9012)",
                titleEn = "New Order Incoming! (#DRV-9012)",
                messageAr = "طلب جديد من سارة إبراهيم بقيمة 365.00 ج.م بانتظار التأكيد",
                messageEn = "New order awaiting your confirmation",
                type = AlertType.NEW_ORDER,
                orderId = "ord_105"
            )
        } else null
    )
    val activeAlert: StateFlow<RestaurantAlert?> = _activeAlert.asStateFlow()

    fun triggerAlert(alert: RestaurantAlert) {
        _activeAlert.value = alert
    }

    fun dismissAlert() {
        _activeAlert.value = null
    }

    // ==========================================
    // Menu Management State & Actions
    // ==========================================
    private val _selectedMenuCategory = MutableStateFlow("الكل")
    val selectedMenuCategory: StateFlow<String> = _selectedMenuCategory.asStateFlow()

    private val _menuSearchQuery = MutableStateFlow("")
    val menuSearchQuery: StateFlow<String> = _menuSearchQuery.asStateFlow()

    val menuCategories: StateFlow<List<String>> = restaurantData.map { rest ->
        val cats = rest?.menu?.map { it.category }?.distinct().orEmpty()
        listOf("الكل") + cats
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("الكل"))

    val filteredMenuItems: StateFlow<List<MenuItem>> = combine(
        restaurantData,
        _selectedMenuCategory,
        _menuSearchQuery
    ) { rest, cat, query ->
        val items = rest?.menu.orEmpty()
        items.filter { item ->
            val matchCat = cat == "الكل" || item.category == cat
            val matchQuery = query.isBlank() ||
                item.nameAr.contains(query, ignoreCase = true) ||
                item.nameEn.contains(query, ignoreCase = true) ||
                item.descriptionAr.contains(query, ignoreCase = true)
            matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Add / Edit Product Dialog State
    private val _isAddEditProductOpen = MutableStateFlow(false)
    val isAddEditProductOpen: StateFlow<Boolean> = _isAddEditProductOpen.asStateFlow()

    private val _editingProduct = MutableStateFlow<MenuItem?>(null)
    val editingProduct: StateFlow<MenuItem?> = _editingProduct.asStateFlow()

    fun openAddProductDialog() {
        _editingProduct.value = null
        _isAddEditProductOpen.value = true
    }

    fun openEditProductDialog(item: MenuItem) {
        _editingProduct.value = item
        _isAddEditProductOpen.value = true
    }

    fun closeAddEditProductDialog() {
        _isAddEditProductOpen.value = false
        _editingProduct.value = null
    }

    fun selectMenuCategory(category: String) {
        _selectedMenuCategory.value = category
    }

    fun updateMenuSearchQuery(query: String) {
        _menuSearchQuery.value = query
    }

    fun toggleMenuItemAvailability(itemId: String, isAvailable: Boolean) {
        val restId = restaurantData.value?.id ?: "rest_1"
        viewModelScope.launch {
            restaurantRepository.toggleMenuItemAvailability(restId, itemId, isAvailable)
        }
    }

    fun saveProduct(
        nameAr: String,
        nameEn: String,
        category: String,
        price: Double,
        descriptionAr: String,
        prepTimeMin: Int,
        imageUri: String? = null
    ) {
        val restId = restaurantData.value?.id ?: "rest_1"
        val existing = _editingProduct.value
        viewModelScope.launch {
            if (existing != null) {
                val updated = existing.copy(
                    nameAr = nameAr,
                    nameEn = nameEn,
                    category = category,
                    price = price,
                    descriptionAr = descriptionAr,
                    preparationTimeMin = prepTimeMin,
                    imageUri = imageUri
                )
                restaurantRepository.updateMenuItem(restId, updated)
                triggerAlert(
                    RestaurantAlert(
                        titleAr = "تم تعديل الصنف",
                        titleEn = "Item Updated",
                        messageAr = "تم حفظ تعديلات صنف '${nameAr}' بنجاح",
                        messageEn = "Item updated successfully",
                        type = AlertType.SUCCESS
                    )
                )
            } else {
                val newItem = MenuItem(
                    id = "m_${System.currentTimeMillis() % 100000}",
                    nameAr = nameAr,
                    nameEn = if (nameEn.isBlank()) nameAr else nameEn,
                    category = category,
                    price = price,
                    descriptionAr = descriptionAr,
                    preparationTimeMin = prepTimeMin,
                    isAvailable = true,
                    imageUri = imageUri
                )
                restaurantRepository.addMenuItem(restId, newItem)
                triggerAlert(
                    RestaurantAlert(
                        titleAr = "تم إضافة الصنف بنجاح",
                        titleEn = "Item Added",
                        messageAr = "أصبح الصنف '${nameAr}' متاحاً الآن في المنيو",
                        messageEn = "New item added to menu",
                        type = AlertType.SUCCESS
                    )
                )
            }
            closeAddEditProductDialog()
        }
    }

    fun deleteMenuItem(itemId: String) {
        val restId = restaurantData.value?.id ?: "rest_1"
        viewModelScope.launch {
            restaurantRepository.deleteMenuItem(restId, itemId)
            triggerAlert(
                RestaurantAlert(
                    titleAr = "تم حذف الصنف",
                    titleEn = "Item Deleted",
                    messageAr = "تم حذف الصنف من المنيو بنجاح",
                    type = AlertType.INFO
                )
            )
        }
    }

    // ==========================================
    // Financials & Settlements State
    // ==========================================
    val commissionRatePercent: Double = 12.0

    val grossSalesSumEgp: StateFlow<Double> = restaurantOrders.map { orders ->
        orders.filter { it.status != OrderStatus.CANCELLED && it.status != OrderStatus.REJECTED }
            .sumOf { it.subtotalEgp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), if (BuildConfig.DEBUG) 2480.0 else 0.0)

    val commissionDeductedEgp: StateFlow<Double> = grossSalesSumEgp.map { gross ->
        (gross * (commissionRatePercent / 100.0))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), if (BuildConfig.DEBUG) 297.6 else 0.0)

    val netSettlementBalanceEgp: StateFlow<Double> = combine(
        grossSalesSumEgp,
        commissionDeductedEgp
    ) { gross, commission ->
        gross - commission + if (BuildConfig.DEBUG) 12450.0 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), if (BuildConfig.DEBUG) 14632.40 else 0.0)

    val settlementHistory: List<SettlementRecord> = if (BuildConfig.DEBUG) listOf(
        SettlementRecord(
            id = "set_1",
            referenceNumber = "PAY-2026-0815",
            periodAr = "الأسبوع 32 (1 أغسطس - 7 أغسطس)",
            dateFormatted = "10 أغسطس 2026",
            ordersCount = 84,
            grossSalesEgp = 32450.0,
            commissionEgp = 3894.0,
            netPayoutEgp = 28556.0
        ),
        SettlementRecord(
            id = "set_2",
            referenceNumber = "PAY-2026-0808",
            periodAr = "الأسبوع 31 (25 يوليو - 31 يوليو)",
            dateFormatted = "3 أغسطس 2026",
            ordersCount = 92,
            grossSalesEgp = 36800.0,
            commissionEgp = 4416.0,
            netPayoutEgp = 32384.0
        ),
        SettlementRecord(
            id = "set_3",
            referenceNumber = "PAY-2026-0801",
            periodAr = "الأسبوع 30 (18 يوليو - 24 يوليو)",
            dateFormatted = "27 يوليو 2026",
            ordersCount = 76,
            grossSalesEgp = 29500.0,
            commissionEgp = 3540.0,
            netPayoutEgp = 25960.0
        )
    ) else emptyList()
}
