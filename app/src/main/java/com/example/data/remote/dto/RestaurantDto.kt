package com.example.data.remote.dto

import com.example.domain.model.MenuItem
import com.example.domain.model.Restaurant
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItemDto(
    @Json(name = "id") val id: String,
    @Json(name = "name_ar") val nameAr: String,
    @Json(name = "name_en") val nameEn: String = "",
    @Json(name = "description_ar") val descriptionAr: String = "",
    @Json(name = "price") val price: Double,
    @Json(name = "category") val category: String,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "is_available") val isAvailable: Boolean = true,
    @Json(name = "preparation_time_min") val preparationTimeMin: Int = 15
)

@JsonClass(generateAdapter = true)
data class RestaurantDto(
    @Json(name = "id") val id: String,
    @Json(name = "name_ar") val nameAr: String,
    @Json(name = "name_en") val nameEn: String = "",
    @Json(name = "category_ar") val categoryAr: String,
    @Json(name = "category_en") val categoryEn: String = "",
    @Json(name = "rating") val rating: Double = 4.8,
    @Json(name = "review_count") val reviewCount: Int = 100,
    @Json(name = "delivery_time_min") val deliveryTimeMin: Int = 30,
    @Json(name = "min_order_egp") val minOrderEgp: Double = 50.0,
    @Json(name = "delivery_fee_egp") val deliveryFeeEgp: Double = 20.0,
    @Json(name = "is_open") val isOpen: Boolean = true,
    @Json(name = "address_ar") val addressAr: String,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "phone") val phone: String = "+201000000000",
    @Json(name = "description_ar") val descriptionAr: String = "",
    @Json(name = "opening_hours") val openingHours: String = "11:00 AM - 02:00 AM",
    @Json(name = "commission_rate_percent") val commissionRatePercent: Double = 12.0,
    @Json(name = "active_subscription_tier") val activeSubscriptionTier: String = "DROVA Pro Partner",
    @Json(name = "monthly_subscription_egp") val monthlySubscriptionEgp: Double = 450.0,
    @Json(name = "subscription_renewal_date") val subscriptionRenewalDate: String = "15 سبتمبر 2026",
    @Json(name = "menu") val menu: List<MenuItemDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UpdateRestaurantProfileRequestDto(
    @Json(name = "restaurant_id") val restaurantId: String,
    @Json(name = "name_ar") val nameAr: String,
    @Json(name = "description_ar") val descriptionAr: String,
    @Json(name = "address_ar") val addressAr: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "opening_hours") val openingHours: String
)

@JsonClass(generateAdapter = true)
data class ToggleOpenStatusRequestDto(
    @Json(name = "restaurant_id") val restaurantId: String,
    @Json(name = "is_open") val isOpen: Boolean
)

@JsonClass(generateAdapter = true)
data class ToggleMenuItemAvailabilityRequestDto(
    @Json(name = "restaurant_id") val restaurantId: String,
    @Json(name = "item_id") val itemId: String,
    @Json(name = "is_available") val isAvailable: Boolean
)

// DTO <-> Domain Mappers
fun MenuItemDto.toDomain(): MenuItem = MenuItem(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    descriptionAr = descriptionAr,
    price = price,
    category = category,
    isAvailable = isAvailable,
    preparationTimeMin = preparationTimeMin,
    imageUri = imageUrl
)

fun MenuItem.toDto(): MenuItemDto = MenuItemDto(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    descriptionAr = descriptionAr,
    price = price,
    category = category,
    imageUrl = imageUri,
    isAvailable = isAvailable,
    preparationTimeMin = preparationTimeMin
)

fun RestaurantDto.toDomain(): Restaurant = Restaurant(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    categoryAr = categoryAr,
    categoryEn = categoryEn,
    rating = rating,
    reviewCount = reviewCount,
    deliveryTimeMin = deliveryTimeMin,
    minOrderEgp = minOrderEgp,
    deliveryFeeEgp = deliveryFeeEgp,
    isOpen = isOpen,
    addressAr = addressAr,
    imageUrl = imageUrl,
    phone = phone,
    descriptionAr = descriptionAr,
    openingHours = openingHours,
    commissionRatePercent = commissionRatePercent,
    activeSubscriptionTier = activeSubscriptionTier,
    monthlySubscriptionEgp = monthlySubscriptionEgp,
    subscriptionRenewalDate = subscriptionRenewalDate,
    menu = menu.map { it.toDomain() }
)

fun Restaurant.toDto(): RestaurantDto = RestaurantDto(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    categoryAr = categoryAr,
    categoryEn = categoryEn,
    rating = rating,
    reviewCount = reviewCount,
    deliveryTimeMin = deliveryTimeMin,
    minOrderEgp = minOrderEgp,
    deliveryFeeEgp = deliveryFeeEgp,
    isOpen = isOpen,
    addressAr = addressAr,
    imageUrl = imageUrl,
    phone = phone,
    descriptionAr = descriptionAr,
    openingHours = openingHours,
    commissionRatePercent = commissionRatePercent,
    activeSubscriptionTier = activeSubscriptionTier,
    monthlySubscriptionEgp = monthlySubscriptionEgp,
    subscriptionRenewalDate = subscriptionRenewalDate,
    menu = menu.map { it.toDto() }
)
