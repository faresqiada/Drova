package com.example.core.location

import com.example.domain.model.OrderCoordinates
import kotlin.math.*

/**
 * Production Location & Geodesic utilities for DROVA delivery ecosystem.
 */
object LocationUtils {

    private const val EARTH_RADIUS_KM = 6371.0
    private const val AVERAGE_SPEED_KMH = 28.0 // Urban delivery speed in Cairo/Giza
    private const val PREPARATION_BUFFER_MIN = 10 // Kitchen prep buffer minutes

    /**
     * Calculates the great-circle distance between two coordinates using the Haversine formula.
     * @return Distance in kilometers (rounded to 2 decimal places).
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        if (lat1 == lat2 && lon1 == lon2) return 0.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val originLatRad = Math.toRadians(lat1)
        val destLatRad = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(originLatRad) * cos(destLatRad)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = EARTH_RADIUS_KM * c

        return (round(distance * 100) / 100.0).coerceAtLeast(0.1)
    }

    /**
     * Overload for OrderCoordinates.
     */
    fun calculateDistanceKm(
        from: OrderCoordinates?,
        to: OrderCoordinates?
    ): Double {
        if (from == null || to == null) return 2.5 // Fallback estimated standard urban distance
        return calculateDistanceKm(from.latitude, from.longitude, to.latitude, to.longitude)
    }

    /**
     * Calculates estimated delivery travel time in minutes based on distance.
     */
    fun estimateTravelTimeMinutes(distanceKm: Double, trafficFactor: Double = 1.25): Int {
        val travelHours = (distanceKm / AVERAGE_SPEED_KMH) * trafficFactor
        val travelMinutes = (travelHours * 60).roundToInt()
        return travelMinutes.coerceAtLeast(5)
    }

    /**
     * Calculates total estimated order arrival time (prep time + travel time).
     */
    fun estimateTotalDeliveryTimeMinutes(distanceKm: Double): Int {
        val travelTime = estimateTravelTimeMinutes(distanceKm)
        return travelTime + PREPARATION_BUFFER_MIN
    }

    /**
     * Formats distance into localized Arabic and English labels.
     */
    fun formatDistance(distanceKm: Double): Pair<String, String> {
        return if (distanceKm < 1.0) {
            val meters = (distanceKm * 1000).roundToInt()
            "$meters متر" to "$meters m"
        } else {
            "%.1f كم".format(distanceKm) to "%.1f km".format(distanceKm)
        }
    }
}
