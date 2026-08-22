package com.example.domain.model

data class User(
    val id: String,
    val fullName: String,
    val phone: String,
    val email: String = "",
    val role: UserRole,
    val city: String = "القاهرة",
    val district: String = "التجمع الخامس",
    val businessName: String? = null,
    val commercialRegister: String? = null,
    val captainMode: CaptainMode = CaptainMode.FREE_MODE,
    val isOnline: Boolean = true,
    val vehicleType: String? = "دراجة نارية (موتوسيكل)"
)
