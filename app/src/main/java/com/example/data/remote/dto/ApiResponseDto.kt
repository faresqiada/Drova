package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseDto<T>(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "data") val data: T? = null,
    @Json(name = "message_ar") val messageAr: String? = null,
    @Json(name = "message_en") val messageEn: String? = null,
    @Json(name = "error_code") val errorCode: String? = null
)
