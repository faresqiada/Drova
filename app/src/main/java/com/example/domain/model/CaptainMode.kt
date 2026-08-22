package com.example.domain.model

enum class CaptainMode(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String
) {
    FREE_MODE(
        titleAr = "العمل الحر (Free Mode)",
        titleEn = "Free Mode",
        descriptionAr = "قبول الطلبات حسب رغبتك وموقعك مع احتساب العائد لكل رحلة",
        descriptionEn = "Accept trips on-demand with flexible per-delivery earnings"
    ),
    SHIFT_MODE(
        titleAr = "نظام الوردية (Shift Mode)",
        titleEn = "Shift Mode",
        descriptionAr = "ساعات عمل محددة مع ضمان دخل أساسي وحوافز إنجاز إضافية",
        descriptionEn = "Scheduled active shifts with guaranteed hourly rate plus delivery bonuses"
    )
}
