package com.surgex.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.PI

/**
 * Dynamic pricing calculation engine
 * Implements surge pricing, distance-based fares, and time-based multipliers
 */
class PricingEngine {
    companion object {
        // Base rates (in ZAR/Rand)
        private const val BASE_FARE = 10.0
        private const val DISTANCE_RATE = 7.5  // per km
        private const val TIME_RATE = 2.5      // per minute
        private const val MINIMUM_FARE = 25.0

        // Surge pricing factors
        private const val BASE_SURGE_MULTIPLIER = 1.0
        private const val HIGH_DEMAND_SURGE = 1.5
        private const val PEAK_HOURS_SURGE = 1.8
        private const val EXTREME_SURGE = 2.5
    }

    private val _demandLevel = MutableStateFlow(1.0) // Demand multiplier
    val demandLevel: StateFlow<Double> = _demandLevel

    /**
     * Calculate fare based on distance, time, and current surge pricing
     * @param distanceKm Distance in kilometers
     * @param estimatedMinutes Estimated trip duration in minutes
     * @param isRushHour Whether it's during peak hours (7-9 AM, 5-7 PM)
     * @param availableDrivers Number of available drivers (affects surge)
     * @return Calculated fare amount in ZAR
     */
    fun calculateFare(
        distanceKm: Double,
        estimatedMinutes: Int,
        isRushHour: Boolean = false,
        availableDrivers: Int = 10
    ): Double {
        // Base fare components
        val distanceFare = distanceKm * DISTANCE_RATE
        val timeFare = estimatedMinutes * TIME_RATE
        val subtotal = BASE_FARE + distanceFare + timeFare

        // Calculate surge multiplier
        val surgeMultiplier = calculateSurgeMultiplier(isRushHour, availableDrivers)
        _demandLevel.value = surgeMultiplier

        // Apply surge multiplier
        val totalFare = subtotal * surgeMultiplier

        // Ensure minimum fare
        return maxOf(totalFare, MINIMUM_FARE)
    }

    /**
     * Calculate surge multiplier based on demand and time
     */
    private fun calculateSurgeMultiplier(isRushHour: Boolean, availableDrivers: Int): Double {
        return when {
            // If very few drivers available and rush hour - extreme surge
            availableDrivers < 2 && isRushHour -> EXTREME_SURGE
            // Rush hour premium
            isRushHour -> PEAK_HOURS_SURGE
            // High demand when few drivers
            availableDrivers < 5 -> HIGH_DEMAND_SURGE
            // Normal demand
            else -> BASE_SURGE_MULTIPLIER
        }
    }

    /**
     * Calculate estimated time based on distance and traffic (simplified)
     */
    fun estimateTime(distanceKm: Double, trafficLevel: Int = 0): Int {
        // Average speed: 30 km/h in city
        val baseMinutes = (distanceKm / 30.0) * 60.0
        // Add traffic delay (0 = no traffic, 1 = moderate, 2 = heavy)
        val trafficDelay = trafficLevel * 5
        return (baseMinutes + trafficDelay).toInt().coerceAtLeast(5)
    }

    /**
     * Calculate Haversine distance between two coordinates
     */
    fun calculateDistance(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Double {
        val R = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(endLat - startLat)
        val dLon = Math.toRadians(endLon - startLon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(startLat)) * cos(Math.toRadians(endLat)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * Math.asin(sqrt(a))
        return R * c
    }

    /**
     * Get breakdown of fare components
     */
    fun getFareBreakdown(
        distanceKm: Double,
        estimatedMinutes: Int,
        isRushHour: Boolean = false,
        availableDrivers: Int = 10
    ): FareBreakdown {
        val distanceFare = distanceKm * DISTANCE_RATE
        val timeFare = estimatedMinutes * TIME_RATE
        val subtotal = BASE_FARE + distanceFare + timeFare
        val surgeMultiplier = calculateSurgeMultiplier(isRushHour, availableDrivers)
        val surgeFee = (subtotal * (surgeMultiplier - 1.0)).coerceAtLeast(0.0)
        val totalFare = maxOf(subtotal + surgeFee, MINIMUM_FARE)

        return FareBreakdown(
            baseFare = BASE_FARE,
            distanceFare = distanceFare,
            timeFare = timeFare,
            surgeFee = surgeFee,
            totalFare = totalFare,
            surgeMultiplier = surgeMultiplier
        )
    }
}

data class FareBreakdown(
    val baseFare: Double,
    val distanceFare: Double,
    val timeFare: Double,
    val surgeFee: Double,
    val totalFare: Double,
    val surgeMultiplier: Double
)
