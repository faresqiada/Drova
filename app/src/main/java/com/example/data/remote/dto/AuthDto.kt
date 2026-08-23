package com.example.data.remote.dto

import com.example.domain.model.CaptainMode
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "role") val role: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "district") val district: String? = null,
    @Json(name = "business_name") val businessName: String? = null,
    @Json(name = "commercial_register") val commercialRegister: String? = null,
    @Json(name = "captain_mode") val captainMode: String? = null,
    @Json(name = "is_online") val isOnline: Boolean? = null,
    @Json(name = "vehicle_type") val vehicleType: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "phone_or_email") val phoneOrEmail: String,
    @Json(name = "password") val password: String,
    @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class CustomerRegisterRequestDto(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "city") val city: String,
    @Json(name = "district") val district: String
)

@JsonClass(generateAdapter = true)
data class RestaurantRegisterRequestDto(
    @Json(name = "business_name") val businessName: String,
    @Json(name = "manager_name") val managerName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "commercial_register") val commercialRegister: String,
    @Json(name = "address") val address: String
)

@JsonClass(generateAdapter = true)
data class CaptainRegisterRequestDto(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "national_id") val nationalId: String,
    @Json(name = "vehicle_type") val vehicleType: String,
    @Json(name = "captain_mode") val captainMode: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "token") val token: String? = null,
    @Json(name = "user") val user: UserDto? = null,
    @Json(name = "message") val message: String? = null
)

// DTO <-> Domain Mappers
fun UserDto.toDomain(): User {
    val userRole = when (role.uppercase()) {
        "ADMIN" -> UserRole.ADMIN
        "RESTAURANT" -> UserRole.RESTAURANT
        "CAPTAIN" -> UserRole.CAPTAIN
        else -> UserRole.CUSTOMER
    }

    val mode = when (captainMode?.uppercase()) {
        "SHIFT_MODE" -> CaptainMode.SHIFT_MODE
        else -> CaptainMode.FREE_MODE
    }

    return User(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email ?: "",
        role = userRole,
        city = city ?: "القاهرة",
        district = district ?: "التجمع الخامس",
        businessName = businessName,
        commercialRegister = commercialRegister,
        captainMode = mode,
        isOnline = isOnline ?: true,
        vehicleType = vehicleType ?: "دراجة نارية (موتوسيكل)"
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
        role = role.name,
        avatarUrl = null,
        city = city,
        district = district,
        businessName = businessName,
        commercialRegister = commercialRegister,
        captainMode = captainMode.name,
        isOnline = isOnline,
        vehicleType = vehicleType
    )
}
