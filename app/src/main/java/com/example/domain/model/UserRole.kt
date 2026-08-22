package com.example.domain.model

enum class UserRole(
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val tag: String
) {
    CUSTOMER(
        titleAr = "عميل",
        titleEn = "Customer",
        subtitleAr = "اطلب طعامك المفضل من أفضل المطاعم وتابعه مباشرة",
        subtitleEn = "Order your favorite food with real-time live tracking",
        tag = "ORDER_NOW"
    ),
    RESTAURANT(
        titleAr = "مطعم",
        titleEn = "Restaurant Partner",
        subtitleAr = "أدر طلباتك، المنيو، وتقارير المبيعات والأرباح بكفاءة",
        subtitleEn = "Manage incoming orders, menu, and sales analytics",
        tag = "PARTNER_HUB"
    ),
    CAPTAIN(
        titleAr = "كابتن",
        titleEn = "Delivery Captain",
        subtitleAr = "اختر بين نظام الورديات أو العمل الحر وحقق دخلك بثقة",
        subtitleEn = "Earn flexibly with Shift Mode or Free Mode delivery",
        tag = "EARN_FLEXIBLY"
    )
}
