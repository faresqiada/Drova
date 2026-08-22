package com.example.domain.model

data class MenuItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val descriptionAr: String,
    val price: Double,
    val category: String,
    val isAvailable: Boolean = true,
    val preparationTimeMin: Int = 15
)

data class Restaurant(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val categoryAr: String,
    val categoryEn: String,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeMin: Int,
    val minOrderEgp: Double,
    val deliveryFeeEgp: Double,
    val isOpen: Boolean = true,
    val addressAr: String,
    val phone: String = "+20 100 887 9922",
    val descriptionAr: String = "أشهى المأكولات السورية والمشويات على الفحم بتتبيلة الريم الخاصة وخبرة تمتد لأكثر من 15 عاماً",
    val openingHours: String = "11:00 ص - 02:00 ص (يومياً)",
    val commissionRatePercent: Double = 12.0,
    val activeSubscriptionTier: String = "DROVA Pro Partner",
    val monthlySubscriptionEgp: Double = 450.0,
    val subscriptionRenewalDate: String = "15 سبتمبر 2026",
    val menu: List<MenuItem> = emptyList()
)
