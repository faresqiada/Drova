package com.example.domain.model

data class CaptainEarnings(
    val todayDeliveriesCount: Int = 8,
    val todayNetEarningsEgp: Double = 385.00,
    val weekEarningsEgp: Double = 2450.00,
    val walletBalanceEgp: Double = 1240.50,
    val pendingPayoutEgp: Double = 860.00,
    val baseEarningsEgp: Double = 275.00,
    val bonusesEgp: Double = 110.00,
    val deductionsEgp: Double = 0.00,
    val acceptanceRatePercent: Int = 98,
    val onTimeDeliveryRatePercent: Int = 99
)

data class CaptainShiftData(
    val isShiftActive: Boolean = true,
    val shiftStartFormatted: String = "09:00 ص",
    val hoursWorked: Double = 5.5,
    val scheduledHours: Double = 8.0,
    val hourlyGuaranteedRateEgp: Double = 50.0,
    val shiftBaseEarningsEgp: Double = 275.0,
    val shiftDeliveriesBonusEgp: Double = 110.0,
    val totalShiftEarningsEgp: Double = 385.0
)

data class CaptainTransaction(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val referenceOrderNumber: String? = null,
    val dateFormatted: String,
    val amountEgp: Double,
    val isCredit: Boolean = true,
    val type: CaptainTransactionType = CaptainTransactionType.TRIP_EARNING,
    val statusAr: String = "مكتملة ومودعة",
    val statusEn: String = "Completed & Credited"
)

enum class CaptainTransactionType {
    TRIP_EARNING,
    SHIFT_BASE,
    BONUS,
    PAYOUT_WITHDRAWAL,
    DEDUCTION
}

data class CaptainNotification(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val messageAr: String,
    val messageEn: String,
    val timestampFormatted: String,
    val type: CaptainNotificationType,
    val isRead: Boolean = false
)

enum class CaptainNotificationType {
    NEW_REQUEST,
    REQUEST_ACCEPTED,
    RESTAURANT_READY,
    CUSTOMER_DESTINATION,
    DELIVERY_COMPLETED,
    EARNINGS_CREDITED
}

data class DeliveryTask(
    val orderId: String,
    val orderNumber: String,
    val restaurantId: String = "rest_1",
    val restaurantNameAr: String,
    val restaurantNameEn: String = "",
    val restaurantAddressAr: String,
    val restaurantPhone: String = "+201023456789",
    val pickupDistanceKm: Double,
    val customerName: String,
    val customerPhone: String = "+201012345678",
    val customerAddressAr: String,
    val dropoffDistanceKm: Double,
    val estimatedTimeMin: Int = 25,
    val baseEarningsEgp: Double = 35.0,
    val bonusEgp: Double = 7.5,
    val estimatedEarningsEgp: Double = baseEarningsEgp + bonusEgp,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val orderTotalEgp: Double = 285.0,
    val status: OrderStatus = OrderStatus.READY_FOR_PICKUP,
    val itemsSummary: String = "",
    val itemsList: List<OrderItem> = emptyList(),
    val specialInstructions: String = "",
    val createdAtFormatted: String = "الآن"
)
