package com.example.domain.model

/**
 * Shared canonical Order Item representation across DROVA modules.
 */
data class OrderItem(
    val id: String,
    val nameAr: String,
    val nameEn: String = "",
    val quantity: Int = 1,
    val unitPriceEgp: Double,
    val notes: String = "",
    val category: String = "",
    val options: List<String> = emptyList()
) {
    val totalEgp: Double get() = quantity * unitPriceEgp
}

/**
 * Supported payment methods across the DROVA platform.
 */
enum class PaymentMethod(
    val titleAr: String,
    val titleEn: String,
    val isElectronic: Boolean = false
) {
    CASH_ON_DELIVERY("الدفع نقداً عند الاستلام", "Cash on Delivery", isElectronic = false),
    WALLET("محفظة DROVA", "DROVA Wallet", isElectronic = true),
    CREDIT_CARD("بطاقة بنكية (فيزا / ماستركارد)", "Credit / Debit Card", isElectronic = true),
    INSTAPAY("إنستاباي InstaPay", "InstaPay Egypt", isElectronic = true),
    VODAFONE_CASH("فودافون كاش", "Vodafone Cash", isElectronic = true)
}

/**
 * Payment lifecycle status for an order.
 */
enum class PaymentStatus(
    val titleAr: String,
    val titleEn: String
) {
    PENDING("قيد الانتظار", "Pending"),
    PAID("مدفوع", "Paid"),
    REFUNDED("مسترد للمحفظة", "Refunded"),
    FAILED("فشل الدفع", "Failed")
}

/**
 * Geographic coordinates for restaurant and customer drop-off.
 */
data class OrderCoordinates(
    val latitude: Double,
    val longitude: Double,
    val addressLabel: String = ""
)

/**
 * Timestamped lifecycle event recording state changes across the 9 stages.
 */
data class OrderTimelineEvent(
    val status: OrderStatus,
    val timestampMillis: Long = System.currentTimeMillis(),
    val formattedTime: String = "الآن",
    val titleAr: String = status.titleAr,
    val titleEn: String = status.titleEn,
    val noteAr: String = status.descriptionAr,
    val noteEn: String = status.descriptionEn,
    val actorRole: UserRole? = null
)

/**
 * Canonical, single-source-of-truth Order model shared seamlessly across:
 * 1. Customer Module (Order placement, live 9-stage tracking, rating)
 * 2. Restaurant Module (Kitchen queue, order acceptance, prep timer, menu itemization)
 * 3. Captain Module (Task dispatch, pickup verification, GPS routing, cash collection)
 */
data class Order(
    val id: String,
    val orderNumber: String,
    
    // Customer Information
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddressAr: String,
    val deliveryAddressEn: String = "",
    val deliveryCoordinates: OrderCoordinates? = null,
    
    // Restaurant Information
    val restaurantId: String,
    val restaurantNameAr: String,
    val restaurantNameEn: String = "",
    val restaurantAddressAr: String,
    val restaurantPhone: String = "+201000000000",
    val restaurantCoordinates: OrderCoordinates? = null,
    
    // Captain Information
    val captainId: String? = null,
    val captainName: String? = null,
    val captainPhone: String? = null,
    val captainVehicleType: String? = "دراجة نارية",
    val captainRating: Double = 4.9,
    val captainCoordinates: OrderCoordinates? = null,
    
    // Items & Pricing (in EGP)
    val items: List<OrderItem>,
    val subtotalEgp: Double,
    val deliveryFeeEgp: Double,
    val platformFeeEgp: Double = 5.0,
    val discountEgp: Double = 0.0,
    val totalEgp: Double = (subtotalEgp + deliveryFeeEgp + platformFeeEgp - discountEgp).coerceAtLeast(0.0),
    
    // Lifecycle & Payment
    val status: OrderStatus,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) PaymentStatus.PENDING else PaymentStatus.PAID,
    
    // Time & Tracking
    val createdAtFormatted: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val estimatedArrivalMin: Int = 30,
    val estimatedDeliveryTimeFormatted: String = "",
    val specialInstructions: String = "",
    /** Optional discriminator for flows that reuse the canonical order pipeline. */
    val requestType: String = "",
    /** Optional restaurant-selected delivery zone for restaurant-originated requests. */
    val deliveryZone: String? = null,
    
    // Audit & Reasons
    val timeline: List<OrderTimelineEvent> = emptyList(),
    val cancellationReason: String? = null,
    val rejectionReason: String? = null,
    val pickupProof: PickupProof? = null
) {
    // Computed Helpers
    val totalQuantity: Int get() = items.sumOf { it.quantity }
    
    val isActive: Boolean get() = status.isActive
    val isTerminal: Boolean get() = status.isTerminal
    
    /**
     * Estimated net payout to the restaurant after platform commission (e.g. 12%)
     */
    val netRestaurantPayoutEgp: Double get() = (subtotalEgp * 0.88).coerceAtLeast(0.0)
    
    /**
     * Estimated earnings for captain (base delivery fee share + bonus)
     */
    val estimatedCaptainEarningsEgp: Double get() = (deliveryFeeEgp * 0.85 + 10.0).coerceAtLeast(25.0)
    
    /**
     * Lifecycle actionability flags
     */
    val canCustomerCancel: Boolean get() = status == OrderStatus.CREATED
    val canRestaurantConfirm: Boolean get() = status == OrderStatus.CREATED
    val canRestaurantStartPrep: Boolean get() = status == OrderStatus.RESTAURANT_CONFIRMED
    val canRestaurantMarkReady: Boolean get() = status == OrderStatus.PREPARING
    val canCaptainAccept: Boolean get() = status == OrderStatus.READY_FOR_PICKUP && captainId == null
    val canCaptainPickUp: Boolean get() = (status == OrderStatus.READY_FOR_PICKUP || status == OrderStatus.CAPTAIN_ASSIGNED) && captainId != null
    val canCaptainStartDelivery: Boolean get() = status == OrderStatus.PICKED_UP
    val canCaptainDeliver: Boolean get() = status == OrderStatus.ON_THE_WAY
    val canComplete: Boolean get() = status == OrderStatus.DELIVERED
    
    /**
     * Normalized progress value (0.0 to 1.0) along the 9-stage active path
     */
    val progressFraction: Float
        get() = when (status) {
            OrderStatus.CREATED -> 0.11f
            OrderStatus.RESTAURANT_CONFIRMED -> 0.22f
            OrderStatus.PREPARING -> 0.33f
            OrderStatus.READY_FOR_PICKUP -> 0.44f
            OrderStatus.CAPTAIN_ASSIGNED -> 0.55f
            OrderStatus.PICKED_UP -> 0.66f
            OrderStatus.ON_THE_WAY -> 0.77f
            OrderStatus.DELIVERED -> 0.88f
            OrderStatus.COMPLETED -> 1.00f
            OrderStatus.CANCELLED, OrderStatus.REJECTED -> 0.00f
        }
}
