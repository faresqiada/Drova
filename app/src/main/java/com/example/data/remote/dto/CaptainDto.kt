package com.example.data.remote.dto

import com.example.domain.model.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaptainEarningsDto(
    @Json(name = "today_deliveries_count") val todayDeliveriesCount: Int = 0,
    @Json(name = "today_net_earnings_egp") val todayNetEarningsEgp: Double = 0.0,
    @Json(name = "week_earnings_egp") val weekEarningsEgp: Double = 0.0,
    @Json(name = "wallet_balance_egp") val walletBalanceEgp: Double = 0.0,
    @Json(name = "pending_payout_egp") val pendingPayoutEgp: Double = 0.0,
    @Json(name = "base_earnings_egp") val baseEarningsEgp: Double = 0.0,
    @Json(name = "bonuses_egp") val bonusesEgp: Double = 0.0,
    @Json(name = "deductions_egp") val deductionsEgp: Double = 0.0,
    @Json(name = "acceptance_rate_percent") val acceptanceRatePercent: Int = 98,
    @Json(name = "on_time_delivery_rate_percent") val onTimeDeliveryRatePercent: Int = 99
)

@JsonClass(generateAdapter = true)
data class CaptainShiftDataDto(
    @Json(name = "is_shift_active") val isShiftActive: Boolean = true,
    @Json(name = "shift_start_formatted") val shiftStartFormatted: String = "09:00 ص",
    @Json(name = "hours_worked") val hoursWorked: Double = 5.5,
    @Json(name = "scheduled_hours") val scheduledHours: Double = 8.0,
    @Json(name = "hourly_guaranteed_rate_egp") val hourlyGuaranteedRateEgp: Double = 50.0,
    @Json(name = "shift_base_earnings_egp") val shiftBaseEarningsEgp: Double = 275.0,
    @Json(name = "shift_deliveries_bonus_egp") val shiftDeliveriesBonusEgp: Double = 110.0,
    @Json(name = "total_shift_earnings_egp") val totalShiftEarningsEgp: Double = 385.0
)

@JsonClass(generateAdapter = true)
data class CaptainTransactionDto(
    @Json(name = "id") val id: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "title_en") val titleEn: String,
    @Json(name = "reference_order_number") val referenceOrderNumber: String? = null,
    @Json(name = "date_formatted") val dateFormatted: String,
    @Json(name = "amount_egp") val amountEgp: Double,
    @Json(name = "is_credit") val isCredit: Boolean = true,
    @Json(name = "type") val type: String = "TRIP_EARNING",
    @Json(name = "status_ar") val statusAr: String = "مكتملة ومودعة",
    @Json(name = "status_en") val statusEn: String = "Completed & Credited"
)

@JsonClass(generateAdapter = true)
data class CaptainNotificationDto(
    @Json(name = "id") val id: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "title_en") val titleEn: String,
    @Json(name = "message_ar") val messageAr: String,
    @Json(name = "message_en") val messageEn: String,
    @Json(name = "timestamp_formatted") val timestampFormatted: String,
    @Json(name = "type") val type: String,
    @Json(name = "is_read") val isRead: Boolean = false
)

@JsonClass(generateAdapter = true)
data class DeliveryTaskDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "order_number") val orderNumber: String,
    @Json(name = "restaurant_id") val restaurantId: String = "rest_1",
    @Json(name = "restaurant_name_ar") val restaurantNameAr: String,
    @Json(name = "restaurant_name_en") val restaurantNameEn: String = "",
    @Json(name = "restaurant_address_ar") val restaurantAddressAr: String,
    @Json(name = "restaurant_phone") val restaurantPhone: String = "+201023456789",
    @Json(name = "pickup_distance_km") val pickupDistanceKm: Double,
    @Json(name = "customer_name") val customerName: String,
    @Json(name = "customer_phone") val customerPhone: String = "+201012345678",
    @Json(name = "customer_address_ar") val customerAddressAr: String,
    @Json(name = "dropoff_distance_km") val dropoffDistanceKm: Double,
    @Json(name = "estimated_time_min") val estimatedTimeMin: Int = 25,
    @Json(name = "base_earnings_egp") val baseEarningsEgp: Double = 35.0,
    @Json(name = "bonus_egp") val bonusEgp: Double = 7.5,
    @Json(name = "estimated_earnings_egp") val estimatedEarningsEgp: Double = 42.5,
    @Json(name = "payment_method") val paymentMethod: String = "CASH_ON_DELIVERY",
    @Json(name = "order_total_egp") val orderTotalEgp: Double = 285.0,
    @Json(name = "status") val status: String = "READY_FOR_PICKUP",
    @Json(name = "items_summary") val itemsSummary: String = "",
    @Json(name = "items_list") val itemsList: List<OrderItemDto> = emptyList(),
    @Json(name = "special_instructions") val specialInstructions: String = "",
    @Json(name = "created_at_formatted") val createdAtFormatted: String = "الآن"
)

@JsonClass(generateAdapter = true)
data class PayoutRequestDto(
    @Json(name = "amount_egp") val amountEgp: Double,
    @Json(name = "note") val note: String? = null
)

@JsonClass(generateAdapter = true)
data class ToggleOnlineRequestDto(
    @Json(name = "is_online") val isOnline: Boolean
)

@JsonClass(generateAdapter = true)
data class SetCaptainModeRequestDto(
    @Json(name = "mode") val mode: String
)

// DTO <-> Domain Mappers
fun CaptainEarningsDto.toDomain(): CaptainEarnings = CaptainEarnings(
    todayDeliveriesCount = todayDeliveriesCount,
    todayNetEarningsEgp = todayNetEarningsEgp,
    weekEarningsEgp = weekEarningsEgp,
    walletBalanceEgp = walletBalanceEgp,
    pendingPayoutEgp = pendingPayoutEgp,
    baseEarningsEgp = baseEarningsEgp,
    bonusesEgp = bonusesEgp,
    deductionsEgp = deductionsEgp,
    acceptanceRatePercent = acceptanceRatePercent,
    onTimeDeliveryRatePercent = onTimeDeliveryRatePercent
)

fun CaptainEarnings.toDto(): CaptainEarningsDto = CaptainEarningsDto(
    todayDeliveriesCount = todayDeliveriesCount,
    todayNetEarningsEgp = todayNetEarningsEgp,
    weekEarningsEgp = weekEarningsEgp,
    walletBalanceEgp = walletBalanceEgp,
    pendingPayoutEgp = pendingPayoutEgp,
    baseEarningsEgp = baseEarningsEgp,
    bonusesEgp = bonusesEgp,
    deductionsEgp = deductionsEgp,
    acceptanceRatePercent = acceptanceRatePercent,
    onTimeDeliveryRatePercent = onTimeDeliveryRatePercent
)

fun CaptainShiftDataDto.toDomain(): CaptainShiftData = CaptainShiftData(
    isShiftActive = isShiftActive,
    shiftStartFormatted = shiftStartFormatted,
    hoursWorked = hoursWorked,
    scheduledHours = scheduledHours,
    hourlyGuaranteedRateEgp = hourlyGuaranteedRateEgp,
    shiftBaseEarningsEgp = shiftBaseEarningsEgp,
    shiftDeliveriesBonusEgp = shiftDeliveriesBonusEgp,
    totalShiftEarningsEgp = totalShiftEarningsEgp
)

fun CaptainShiftData.toDto(): CaptainShiftDataDto = CaptainShiftDataDto(
    isShiftActive = isShiftActive,
    shiftStartFormatted = shiftStartFormatted,
    hoursWorked = hoursWorked,
    scheduledHours = scheduledHours,
    hourlyGuaranteedRateEgp = hourlyGuaranteedRateEgp,
    shiftBaseEarningsEgp = shiftBaseEarningsEgp,
    shiftDeliveriesBonusEgp = shiftDeliveriesBonusEgp,
    totalShiftEarningsEgp = totalShiftEarningsEgp
)

fun CaptainTransactionDto.toDomain(): CaptainTransaction {
    val txType = try {
        CaptainTransactionType.valueOf(type)
    } catch (e: Exception) {
        CaptainTransactionType.TRIP_EARNING
    }
    return CaptainTransaction(
        id = id,
        titleAr = titleAr,
        titleEn = titleEn,
        referenceOrderNumber = referenceOrderNumber,
        dateFormatted = dateFormatted,
        amountEgp = amountEgp,
        isCredit = isCredit,
        type = txType,
        statusAr = statusAr,
        statusEn = statusEn
    )
}

fun CaptainTransaction.toDto(): CaptainTransactionDto = CaptainTransactionDto(
    id = id,
    titleAr = titleAr,
    titleEn = titleEn,
    referenceOrderNumber = referenceOrderNumber,
    dateFormatted = dateFormatted,
    amountEgp = amountEgp,
    isCredit = isCredit,
    type = type.name,
    statusAr = statusAr,
    statusEn = statusEn
)

fun CaptainNotificationDto.toDomain(): CaptainNotification {
    val nType = try {
        CaptainNotificationType.valueOf(type)
    } catch (e: Exception) {
        CaptainNotificationType.NEW_REQUEST
    }
    return CaptainNotification(
        id = id,
        titleAr = titleAr,
        titleEn = titleEn,
        messageAr = messageAr,
        messageEn = messageEn,
        timestampFormatted = timestampFormatted,
        type = nType,
        isRead = isRead
    )
}

fun CaptainNotification.toDto(): CaptainNotificationDto = CaptainNotificationDto(
    id = id,
    titleAr = titleAr,
    titleEn = titleEn,
    messageAr = messageAr,
    messageEn = messageEn,
    timestampFormatted = timestampFormatted,
    type = type.name,
    isRead = isRead
)

fun DeliveryTaskDto.toDomain(): DeliveryTask {
    val orderStatus = try {
        OrderStatus.valueOf(status)
    } catch (e: Exception) {
        OrderStatus.READY_FOR_PICKUP
    }
    val method = try {
        PaymentMethod.valueOf(paymentMethod)
    } catch (e: Exception) {
        PaymentMethod.CASH_ON_DELIVERY
    }
    return DeliveryTask(
        orderId = orderId,
        orderNumber = orderNumber,
        restaurantId = restaurantId,
        restaurantNameAr = restaurantNameAr,
        restaurantNameEn = restaurantNameEn,
        restaurantAddressAr = restaurantAddressAr,
        restaurantPhone = restaurantPhone,
        pickupDistanceKm = pickupDistanceKm,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddressAr = customerAddressAr,
        dropoffDistanceKm = dropoffDistanceKm,
        estimatedTimeMin = estimatedTimeMin,
        baseEarningsEgp = baseEarningsEgp,
        bonusEgp = bonusEgp,
        estimatedEarningsEgp = estimatedEarningsEgp,
        paymentMethod = method,
        orderTotalEgp = orderTotalEgp,
        status = orderStatus,
        itemsSummary = itemsSummary,
        itemsList = itemsList.map { it.toDomain() },
        specialInstructions = specialInstructions,
        createdAtFormatted = createdAtFormatted
    )
}

fun DeliveryTask.toDto(): DeliveryTaskDto = DeliveryTaskDto(
    orderId = orderId,
    orderNumber = orderNumber,
    restaurantId = restaurantId,
    restaurantNameAr = restaurantNameAr,
    restaurantNameEn = restaurantNameEn,
    restaurantAddressAr = restaurantAddressAr,
    restaurantPhone = restaurantPhone,
    pickupDistanceKm = pickupDistanceKm,
    customerName = customerName,
    customerPhone = customerPhone,
    customerAddressAr = customerAddressAr,
    dropoffDistanceKm = dropoffDistanceKm,
    estimatedTimeMin = estimatedTimeMin,
    baseEarningsEgp = baseEarningsEgp,
    bonusEgp = bonusEgp,
    estimatedEarningsEgp = estimatedEarningsEgp,
    paymentMethod = paymentMethod.name,
    orderTotalEgp = orderTotalEgp,
    status = status.name,
    itemsSummary = itemsSummary,
    itemsList = itemsList.map { it.toDto() },
    specialInstructions = specialInstructions,
    createdAtFormatted = createdAtFormatted
)
