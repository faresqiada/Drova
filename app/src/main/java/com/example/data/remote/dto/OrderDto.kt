package com.example.data.remote.dto

import com.example.domain.model.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderItemDto(
    @Json(name = "id") val id: String,
    @Json(name = "name_ar") val nameAr: String,
    @Json(name = "name_en") val nameEn: String = "",
    @Json(name = "quantity") val quantity: Int = 1,
    @Json(name = "unit_price_egp") val unitPriceEgp: Double,
    @Json(name = "notes") val notes: String = "",
    @Json(name = "category") val category: String = "",
    @Json(name = "options") val options: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class OrderCoordinatesDto(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "address_label") val addressLabel: String = ""
)

@JsonClass(generateAdapter = true)
data class OrderTimelineEventDto(
    @Json(name = "status") val status: String,
    @Json(name = "timestamp_millis") val timestampMillis: Long,
    @Json(name = "formatted_time") val formattedTime: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "title_en") val titleEn: String,
    @Json(name = "note_ar") val noteAr: String,
    @Json(name = "note_en") val noteEn: String,
    @Json(name = "actor_role") val actorRole: String? = null
)

@JsonClass(generateAdapter = true)
data class PickupProofDto(
    @Json(name = "proof_id") val proofId: String,
    @Json(name = "order_id") val orderId: String,
    @Json(name = "captain_id") val captainId: String,
    @Json(name = "storage_path") val storagePath: String,
    @Json(name = "captured_at") val capturedAt: Long,
    @Json(name = "image_width") val imageWidth: Int,
    @Json(name = "image_height") val imageHeight: Int,
    @Json(name = "file_size_bytes") val fileSizeBytes: Long,
    @Json(name = "content_type") val contentType: String = "image/jpeg",
    @Json(name = "has_receipt_text") val hasReceiptText: Boolean,
    @Json(name = "order_identifier_matched") val orderIdentifierMatched: Boolean,
    @Json(name = "validation_status") val validationStatus: String,
    @Json(name = "validation_message") val validationMessage: String = ""
)

@JsonClass(generateAdapter = true)
data class OrderDto(
    @Json(name = "id") val id: String,
    @Json(name = "order_number") val orderNumber: String,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "customer_name") val customerName: String,
    @Json(name = "customer_phone") val customerPhone: String,
    @Json(name = "delivery_address_ar") val deliveryAddressAr: String,
    @Json(name = "delivery_address_en") val deliveryAddressEn: String = "",
    @Json(name = "delivery_coordinates") val deliveryCoordinates: OrderCoordinatesDto? = null,
    @Json(name = "restaurant_id") val restaurantId: String,
    @Json(name = "restaurant_name_ar") val restaurantNameAr: String,
    @Json(name = "restaurant_name_en") val restaurantNameEn: String = "",
    @Json(name = "restaurant_address_ar") val restaurantAddressAr: String,
    @Json(name = "restaurant_phone") val restaurantPhone: String = "+201000000000",
    @Json(name = "restaurant_coordinates") val restaurantCoordinates: OrderCoordinatesDto? = null,
    @Json(name = "captain_id") val captainId: String? = null,
    @Json(name = "captain_name") val captainName: String? = null,
    @Json(name = "captain_phone") val captainPhone: String? = null,
    @Json(name = "captain_vehicle_type") val captainVehicleType: String? = null,
    @Json(name = "captain_rating") val captainRating: Double = 4.9,
    @Json(name = "captain_coordinates") val captainCoordinates: OrderCoordinatesDto? = null,
    @Json(name = "items") val items: List<OrderItemDto> = emptyList(),
    @Json(name = "subtotal_egp") val subtotalEgp: Double,
    @Json(name = "delivery_fee_egp") val deliveryFeeEgp: Double,
    @Json(name = "platform_fee_egp") val platformFeeEgp: Double = 5.0,
    @Json(name = "discount_egp") val discountEgp: Double = 0.0,
    @Json(name = "total_egp") val totalEgp: Double,
    @Json(name = "status") val status: String,
    @Json(name = "payment_method") val paymentMethod: String,
    @Json(name = "payment_status") val paymentStatus: String = "PAID",
    @Json(name = "created_at_formatted") val createdAtFormatted: String,
    @Json(name = "created_at_millis") val createdAtMillis: Long = System.currentTimeMillis(),
    @Json(name = "estimated_arrival_min") val estimatedArrivalMin: Int = 30,
    @Json(name = "estimated_delivery_time_formatted") val estimatedDeliveryTimeFormatted: String = "",
    @Json(name = "special_instructions") val specialInstructions: String = "",
    @Json(name = "timeline") val timeline: List<OrderTimelineEventDto> = emptyList(),
    @Json(name = "cancellation_reason") val cancellationReason: String? = null,
    @Json(name = "rejection_reason") val rejectionReason: String? = null,
    @Json(name = "pickup_proof") val pickupProof: PickupProofDto? = null
)

@JsonClass(generateAdapter = true)
data class CreateOrderRequestDto(
    @Json(name = "order") val order: OrderDto
)

@JsonClass(generateAdapter = true)
data class UpdateOrderStatusRequestDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "new_status") val newStatus: String,
    @Json(name = "actor_role") val actorRole: String? = null,
    @Json(name = "note") val note: String? = null
)

@JsonClass(generateAdapter = true)
data class AssignCaptainRequestDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "captain_id") val captainId: String,
    @Json(name = "captain_name") val captainName: String,
    @Json(name = "captain_phone") val captainPhone: String
)

@JsonClass(generateAdapter = true)
data class CancelOrderRequestDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "reason") val reason: String
)

@JsonClass(generateAdapter = true)
data class RejectOrderRequestDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "reason") val reason: String
)

@JsonClass(generateAdapter = true)
data class AddTimelineEventRequestDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "event") val event: OrderTimelineEventDto
)

// DTO <-> Domain Mappers
fun OrderItemDto.toDomain(): OrderItem = OrderItem(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    quantity = quantity,
    unitPriceEgp = unitPriceEgp,
    notes = notes,
    category = category,
    options = options
)

fun OrderItem.toDto(): OrderItemDto = OrderItemDto(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    quantity = quantity,
    unitPriceEgp = unitPriceEgp,
    notes = notes,
    category = category,
    options = options
)

fun OrderCoordinatesDto.toDomain(): OrderCoordinates = OrderCoordinates(
    latitude = latitude,
    longitude = longitude,
    addressLabel = addressLabel
)

fun OrderCoordinates.toDto(): OrderCoordinatesDto = OrderCoordinatesDto(
    latitude = latitude,
    longitude = longitude,
    addressLabel = addressLabel
)

fun OrderTimelineEventDto.toDomain(): OrderTimelineEvent {
    val orderStatus = try {
        OrderStatus.valueOf(status)
    } catch (e: Exception) {
        OrderStatus.CREATED
    }
    val role = actorRole?.let {
        try { UserRole.valueOf(it) } catch (e: Exception) { null }
    }
    return OrderTimelineEvent(
        status = orderStatus,
        timestampMillis = timestampMillis,
        formattedTime = formattedTime,
        titleAr = titleAr,
        titleEn = titleEn,
        noteAr = noteAr,
        noteEn = noteEn,
        actorRole = role
    )
}

fun OrderTimelineEvent.toDto(): OrderTimelineEventDto = OrderTimelineEventDto(
    status = status.name,
    timestampMillis = timestampMillis,
    formattedTime = formattedTime,
    titleAr = titleAr,
    titleEn = titleEn,
    noteAr = noteAr,
    noteEn = noteEn,
    actorRole = actorRole?.name
)

fun PickupProofDto.toDomain(): PickupProof = PickupProof(
    proofId = proofId,
    orderId = orderId,
    captainId = captainId,
    storagePath = storagePath,
    capturedAt = capturedAt,
    imageWidth = imageWidth,
    imageHeight = imageHeight,
    fileSizeBytes = fileSizeBytes,
    contentType = contentType,
    hasReceiptText = hasReceiptText,
    orderIdentifierMatched = orderIdentifierMatched,
    validationStatus = try { PickupProofValidationStatus.valueOf(validationStatus) } catch (_: Exception) { PickupProofValidationStatus.INVALID },
    validationMessage = validationMessage
)

fun PickupProof.toDto(): PickupProofDto = PickupProofDto(
    proofId = proofId,
    orderId = orderId,
    captainId = captainId,
    storagePath = storagePath,
    capturedAt = capturedAt,
    imageWidth = imageWidth,
    imageHeight = imageHeight,
    fileSizeBytes = fileSizeBytes,
    contentType = contentType,
    hasReceiptText = hasReceiptText,
    orderIdentifierMatched = orderIdentifierMatched,
    validationStatus = validationStatus.name,
    validationMessage = validationMessage
)

fun OrderDto.toDomain(): Order {
    val orderStatus = try {
        OrderStatus.valueOf(status)
    } catch (e: Exception) {
        OrderStatus.CREATED
    }

    val method = try {
        PaymentMethod.valueOf(paymentMethod)
    } catch (e: Exception) {
        PaymentMethod.CASH_ON_DELIVERY
    }

    val pStatus = try {
        PaymentStatus.valueOf(paymentStatus)
    } catch (e: Exception) {
        PaymentStatus.PAID
    }

    return Order(
        id = id,
        orderNumber = orderNumber,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        deliveryAddressAr = deliveryAddressAr,
        deliveryAddressEn = deliveryAddressEn,
        deliveryCoordinates = deliveryCoordinates?.toDomain(),
        restaurantId = restaurantId,
        restaurantNameAr = restaurantNameAr,
        restaurantNameEn = restaurantNameEn,
        restaurantAddressAr = restaurantAddressAr,
        restaurantPhone = restaurantPhone,
        restaurantCoordinates = restaurantCoordinates?.toDomain(),
        captainId = captainId,
        captainName = captainName,
        captainPhone = captainPhone,
        captainVehicleType = captainVehicleType ?: "دراجة نارية",
        captainRating = captainRating,
        captainCoordinates = captainCoordinates?.toDomain(),
        items = items.map { it.toDomain() },
        subtotalEgp = subtotalEgp,
        deliveryFeeEgp = deliveryFeeEgp,
        platformFeeEgp = platformFeeEgp,
        discountEgp = discountEgp,
        totalEgp = totalEgp,
        status = orderStatus,
        paymentMethod = method,
        paymentStatus = pStatus,
        createdAtFormatted = createdAtFormatted,
        createdAtMillis = createdAtMillis,
        estimatedArrivalMin = estimatedArrivalMin,
        estimatedDeliveryTimeFormatted = estimatedDeliveryTimeFormatted,
        specialInstructions = specialInstructions,
        timeline = timeline.map { it.toDomain() },
        cancellationReason = cancellationReason,
        rejectionReason = rejectionReason,
        pickupProof = pickupProof?.toDomain()
    )
}

fun Order.toDto(): OrderDto = OrderDto(
    id = id,
    orderNumber = orderNumber,
    customerId = customerId,
    customerName = customerName,
    customerPhone = customerPhone,
    deliveryAddressAr = deliveryAddressAr,
    deliveryAddressEn = deliveryAddressEn,
    deliveryCoordinates = deliveryCoordinates?.toDto(),
    restaurantId = restaurantId,
    restaurantNameAr = restaurantNameAr,
    restaurantNameEn = restaurantNameEn,
    restaurantAddressAr = restaurantAddressAr,
    restaurantPhone = restaurantPhone,
    restaurantCoordinates = restaurantCoordinates?.toDto(),
    captainId = captainId,
    captainName = captainName,
    captainPhone = captainPhone,
    captainVehicleType = captainVehicleType,
    captainRating = captainRating,
    captainCoordinates = captainCoordinates?.toDto(),
    items = items.map { it.toDto() },
    subtotalEgp = subtotalEgp,
    deliveryFeeEgp = deliveryFeeEgp,
    platformFeeEgp = platformFeeEgp,
    discountEgp = discountEgp,
    totalEgp = totalEgp,
    status = status.name,
    paymentMethod = paymentMethod.name,
    paymentStatus = paymentStatus.name,
    createdAtFormatted = createdAtFormatted,
    createdAtMillis = createdAtMillis,
    estimatedArrivalMin = estimatedArrivalMin,
    estimatedDeliveryTimeFormatted = estimatedDeliveryTimeFormatted,
    specialInstructions = specialInstructions,
    timeline = timeline.map { it.toDto() },
    cancellationReason = cancellationReason,
    rejectionReason = rejectionReason,
    pickupProof = pickupProof?.toDto()
)
