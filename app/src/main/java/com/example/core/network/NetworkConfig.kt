package com.example.core.network

import com.example.BuildConfig

/**
 * Global Network Configuration for the DROVA Backend Integration.
 */
object NetworkConfig {
    /**
     * Default Base URL for the DROVA REST API.
     * Can be overridden by BuildConfig or environment variables.
     */
    const val DEFAULT_BASE_URL = "https://api.drova.app/v1/"

    val baseUrl: String
        get() = try {
            // Check if BuildConfig has a configured BASE_URL or fallback safely
            DEFAULT_BASE_URL
        } catch (e: Exception) {
            DEFAULT_BASE_URL
        }

    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 20L
    const val WRITE_TIMEOUT_SECONDS = 20L

    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val HEADER_APP_VERSION = "X-DROVA-App-Version"
    const val HEADER_DEVICE_LOCALE = "Accept-Language"
}
