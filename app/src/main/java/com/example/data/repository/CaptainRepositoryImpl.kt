package com.example.data.repository

import com.example.data.mock.DrovaMockData
import com.example.domain.model.*
import com.example.domain.repository.CaptainRepository
import com.example.domain.repository.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

fun Order.toDeliveryTask(
    pickupDistanceKm: Double = 1.5,
    dropoffDistanceKm: Double = 2.8
): DeliveryTask {
    val base = (deliveryFeeEgp * 0.85).coerceAtLeast(25.0)
    val bonus = 7.5
    val itemsSummary = if (items.isNotEmpty()) {
        items.joinToString(" + ") { "${it.quantity} ${it.nameAr}" }
    } else "طلب طعام جاهز"

    return DeliveryTask(
        orderId = id,
        orderNumber = orderNumber,
        restaurantId = restaurantId,
        restaurantNameAr = restaurantNameAr,
        restaurantNameEn = restaurantNameEn,
        restaurantAddressAr = restaurantAddressAr,
        restaurantPhone = restaurantPhone,
        pickupDistanceKm = pickupDistanceKm,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddressAr = deliveryAddressAr,
        dropoffDistanceKm = dropoffDistanceKm,
        estimatedTimeMin = estimatedArrivalMin,
        baseEarningsEgp = base,
        bonusEgp = bonus,
        estimatedEarningsEgp = base + bonus,
        paymentMethod = paymentMethod,
        orderTotalEgp = totalEgp,
        status = status,
        itemsSummary = itemsSummary,
        itemsList = items,
        specialInstructions = specialInstructions,
        createdAtFormatted = createdAtFormatted
    )
}

class CaptainRepositoryImpl(
    private val orderRepository: OrderRepository
) : CaptainRepository {

    private val _isOnline = MutableStateFlow(true)
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _captainMode = MutableStateFlow(CaptainMode.SHIFT_MODE)
    override val captainMode: StateFlow<CaptainMode> = _captainMode.asStateFlow()

    private val _earnings = MutableStateFlow(DrovaMockData.defaultEarnings)
    override val earnings: StateFlow<CaptainEarnings> = _earnings.asStateFlow()

    private val _shiftData = MutableStateFlow(DrovaMockData.defaultShiftData)
    override val shiftData: StateFlow<CaptainShiftData> = _shiftData.asStateFlow()

    private val _rejectedOrderIds = MutableStateFlow<Set<String>>(emptySet())

    private val _activeTask = MutableStateFlow<DeliveryTask?>(null)
    override val activeTask: StateFlow<DeliveryTask?> = _activeTask.asStateFlow()

    private val _completedTasks = MutableStateFlow(DrovaMockData.sampleCompletedTasks)
    override val completedTasks: StateFlow<List<DeliveryTask>> = _completedTasks.asStateFlow()

    private val _transactions = MutableStateFlow(DrovaMockData.sampleTransactions)
    override val transactions: StateFlow<List<CaptainTransaction>> = _transactions.asStateFlow()

    private val _notifications = MutableStateFlow(DrovaMockData.sampleCaptainNotifications)
    override val notifications: StateFlow<List<CaptainNotification>> = _notifications.asStateFlow()

    private val _availableTasks = MutableStateFlow<List<DeliveryTask>>(emptyList())
    override val availableTasks: StateFlow<List<DeliveryTask>> = _availableTasks.asStateFlow()

    init {
        refreshAvailableTasks()
        CoroutineScope(Dispatchers.Unconfined).launch {
            combine(orderRepository.allOrders, _isOnline, _rejectedOrderIds) { orders, isOnline, rejectedIds ->
                if (!isOnline) {
                    emptyList()
                } else {
                    orders.filter { order ->
                        order.status == OrderStatus.READY_FOR_PICKUP &&
                        order.captainId == null &&
                        !rejectedIds.contains(order.id) &&
                        !rejectedIds.contains(order.orderNumber)
                    }.map { it.toDeliveryTask() }
                }
            }.collect { list ->
                _availableTasks.value = list
            }
        }
    }

    private fun refreshAvailableTasks() {
        if (!_isOnline.value) {
            _availableTasks.value = emptyList()
        } else {
            val rejectedIds = _rejectedOrderIds.value
            _availableTasks.value = orderRepository.allOrders.value.filter { order ->
                order.status == OrderStatus.READY_FOR_PICKUP &&
                order.captainId == null &&
                !rejectedIds.contains(order.id) &&
                !rejectedIds.contains(order.orderNumber)
            }.map { it.toDeliveryTask() }
        }
    }

    override suspend fun toggleOnlineStatus(online: Boolean) {
        _isOnline.value = online
        if (online) {
            _rejectedOrderIds.value = emptySet()
        }
        refreshAvailableTasks()
    }

    override suspend fun setCaptainMode(mode: CaptainMode): Boolean {
        if (_activeTask.value != null) {
            return false // Cannot switch modes while on an active delivery
        }
        _captainMode.value = mode
        return true
    }

    override suspend fun acceptTask(orderId: String): Boolean {
        if (!_isOnline.value) return false
        if (_activeTask.value != null) return false

        val order = orderRepository.getOrderById(orderId) ?: return false
        if (order.status != OrderStatus.READY_FOR_PICKUP) return false

        val assignSuccess = orderRepository.assignCaptainToOrder(
            orderId = orderId,
            captainId = "cap_1",
            captainName = "محمود عادل (كابتن DROVA)",
            captainPhone = "+201198765432"
        )

        if (!assignSuccess) return false

        val updatedOrder = orderRepository.getOrderById(orderId) ?: order
        val assignedTask = updatedOrder.toDeliveryTask().copy(status = OrderStatus.CAPTAIN_ASSIGNED)
        _activeTask.value = assignedTask
        refreshAvailableTasks()

        val notif = CaptainNotification(
            id = "notif_${System.currentTimeMillis()}",
            titleAr = "تم قبول الطلب بنجاح ${assignedTask.orderNumber}",
            titleEn = "Order ${assignedTask.orderNumber} Accepted",
            messageAr = "توجه الآن إلى مطعم ${assignedTask.restaurantNameAr} لاستلام الطلب",
            messageEn = "Head to ${assignedTask.restaurantNameAr} for pickup",
            timestampFormatted = "الآن",
            type = CaptainNotificationType.REQUEST_ACCEPTED,
            isRead = false
        )
        _notifications.update { listOf(notif) + it }

        return true
    }

    override suspend fun updateTaskStatus(orderId: String, newStatus: OrderStatus): Boolean {
        val current = _activeTask.value ?: return false
        if (current.orderId != orderId && current.orderNumber != orderId) return false

        // Validate canonical captain lifecycle transitions:
        // CAPTAIN_ASSIGNED -> PICKED_UP
        // PICKED_UP -> ON_THE_WAY
        // ON_THE_WAY -> DELIVERED
        val isValidCaptainTransition = when (current.status) {
            OrderStatus.CAPTAIN_ASSIGNED -> newStatus == OrderStatus.PICKED_UP
            OrderStatus.PICKED_UP -> newStatus == OrderStatus.ON_THE_WAY
            OrderStatus.ON_THE_WAY -> newStatus == OrderStatus.DELIVERED
            else -> false
        }

        if (!isValidCaptainTransition) {
            return false
        }

        val advanceSuccess = orderRepository.advanceOrderStatus(orderId, newStatus)
        if (!advanceSuccess) {
            return false
        }

        when (newStatus) {
            OrderStatus.PICKED_UP -> {
                _activeTask.value = current.copy(status = OrderStatus.PICKED_UP)
                val notif = CaptainNotification(
                    id = "notif_${System.currentTimeMillis()}",
                    titleAr = "تم تأكيد استلام الطلب من المطعم",
                    titleEn = "Order Picked Up from Restaurant",
                    messageAr = "تم استلام ${current.orderNumber} من ${current.restaurantNameAr} بنجاح",
                    messageEn = "Order ${current.orderNumber} collected",
                    timestampFormatted = "الآن",
                    type = CaptainNotificationType.RESTAURANT_READY,
                    isRead = false
                )
                _notifications.update { listOf(notif) + it }
            }
            OrderStatus.ON_THE_WAY -> {
                _activeTask.value = current.copy(status = OrderStatus.ON_THE_WAY)
                val notif = CaptainNotification(
                    id = "notif_${System.currentTimeMillis()}",
                    titleAr = "في الطريق للعميل",
                    titleEn = "On the Way to Customer",
                    messageAr = "جاري التوجه إلى ${current.customerAddressAr} (${current.customerName})",
                    messageEn = "Heading to ${current.customerAddressAr}",
                    timestampFormatted = "الآن",
                    type = CaptainNotificationType.CUSTOMER_DESTINATION,
                    isRead = false
                )
                _notifications.update { listOf(notif) + it }
            }
            OrderStatus.DELIVERED -> {
                val earned = current.estimatedEarningsEgp
                val base = current.baseEarningsEgp
                val bonus = current.bonusEgp

                _earnings.update {
                    it.copy(
                        todayDeliveriesCount = it.todayDeliveriesCount + 1,
                        todayNetEarningsEgp = it.todayNetEarningsEgp + earned,
                        weekEarningsEgp = it.weekEarningsEgp + earned,
                        walletBalanceEgp = it.walletBalanceEgp + earned,
                        pendingPayoutEgp = it.pendingPayoutEgp + earned,
                        baseEarningsEgp = it.baseEarningsEgp + base,
                        bonusesEgp = it.bonusesEgp + bonus
                    )
                }

                _shiftData.update {
                    it.copy(
                        totalShiftEarningsEgp = it.totalShiftEarningsEgp + earned,
                        shiftDeliveriesBonusEgp = it.shiftDeliveriesBonusEgp + bonus
                    )
                }

                val newTx = CaptainTransaction(
                    id = "tx_${System.currentTimeMillis() % 100000}",
                    titleAr = "عائد توصيل طلب",
                    titleEn = "Delivery Earning",
                    referenceOrderNumber = current.orderNumber,
                    dateFormatted = "الآن",
                    amountEgp = earned,
                    isCredit = true,
                    type = CaptainTransactionType.TRIP_EARNING
                )
                _transactions.update { listOf(newTx) + it }

                val completedTask = current.copy(status = OrderStatus.DELIVERED)
                _completedTasks.update { listOf(completedTask) + it }

                val notifDelivered = CaptainNotification(
                    id = "notif_${System.currentTimeMillis()}",
                    titleAr = "تم تسليم الطلب ${current.orderNumber} بنجاح!",
                    titleEn = "Order ${current.orderNumber} Delivered!",
                    messageAr = "تم إيداع ${earned} ج.م في محفظة DROVA الخاصة بك",
                    messageEn = "${earned} EGP credited to your DROVA Wallet",
                    timestampFormatted = "الآن",
                    type = CaptainNotificationType.DELIVERY_COMPLETED,
                    isRead = false
                )
                _notifications.update { listOf(notifDelivered) + it }

                _activeTask.value = null
            }
            else -> {}
        }
        return true
    }

    override suspend fun rejectTask(orderId: String) {
        _rejectedOrderIds.update { it + orderId }
        refreshAvailableTasks()
    }

    override suspend fun requestPayout(amountEgp: Double): Boolean {
        val currentBalance = _earnings.value.walletBalanceEgp
        if (amountEgp <= 0 || amountEgp > currentBalance) {
            return false
        }

        _earnings.update {
            it.copy(
                walletBalanceEgp = (it.walletBalanceEgp - amountEgp).coerceAtLeast(0.0),
                pendingPayoutEgp = (it.pendingPayoutEgp - amountEgp).coerceAtLeast(0.0)
            )
        }

        val payoutTx = CaptainTransaction(
            id = "tx_${System.currentTimeMillis() % 100000}",
            titleAr = "طلب سحب أرباح فوري",
            titleEn = "Instant Payout Request",
            dateFormatted = "الآن",
            amountEgp = amountEgp,
            isCredit = false,
            type = CaptainTransactionType.PAYOUT_WITHDRAWAL,
            statusAr = "قيد التحويل الفوري",
            statusEn = "Instant Transfer In Progress"
        )
        _transactions.update { listOf(payoutTx) + it }

        val notif = CaptainNotification(
            id = "notif_${System.currentTimeMillis()}",
            titleAr = "تم استلام طلب سحب الأرباح",
            titleEn = "Payout Request Received",
            messageAr = "جاري تحويل مبلغ $amountEgp ج.م إلى حسابك المسجل",
            messageEn = "Transfer of $amountEgp EGP in progress",
            timestampFormatted = "الآن",
            type = CaptainNotificationType.EARNINGS_CREDITED,
            isRead = false
        )
        _notifications.update { listOf(notif) + it }

        return true
    }

    override suspend fun markNotificationAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    override suspend fun clearAllNotifications() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }
}
