package com.surgex.app.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.surgex.app.service.*
import java.time.LocalDateTime

/**
 * Central repository for trip history with caching and real-time updates
 */
class TripHistoryRepository {
    private val _riderTripHistory = MutableStateFlow<List<CompletedTrip>>(emptyList())
    val riderTripHistory: StateFlow<List<CompletedTrip>> = _riderTripHistory

    private val _driverTripHistory = MutableStateFlow<List<CompletedTrip>>(emptyList())
    val driverTripHistory: StateFlow<List<CompletedTrip>> = _driverTripHistory

    private val _driverEarnings = MutableStateFlow(0.0)
    val driverEarnings: StateFlow<Double> = _driverEarnings

    private val _riderSpent = MutableStateFlow(0.0)
    val riderSpent: StateFlow<Double> = _riderSpent

    /**
     * Add completed trip to history
     */
    fun addCompletedTrip(trip: CompletedTrip) {
        if (trip.status == "COMPLETED") {
            // Add to rider history
            _riderTripHistory.value = _riderTripHistory.value + trip
            _riderSpent.value += trip.actualFare

            // Add to driver history
            _driverTripHistory.value = _driverTripHistory.value + trip
            _driverEarnings.value += (trip.actualFare * 0.75) // Driver gets 75%, platform gets 25%
        }
    }

    /**
     * Get trips for a specific user
     */
    fun getUserTrips(userId: String, isDriver: Boolean): List<CompletedTrip> {
        return if (isDriver) {
            _driverTripHistory.value.filter { it.driverId == userId }
        } else {
            _riderTripHistory.value.filter { it.riderId == userId }
        }
    }

    /**
     * Get trip statistics
     */
    fun getTripStatistics(userId: String, isDriver: Boolean): TripStatistics {
        val trips = getUserTrips(userId, isDriver)
        val totalDistance = trips.sumOf { it.distance }
        val averageRating = if (trips.isEmpty()) 5.0 else trips.map { 4.5 }.average() // Placeholder
        val totalEarnings = if (isDriver) {
            trips.sumOf { it.actualFare * 0.75 }
        } else {
            trips.sumOf { it.actualFare }
        }

        return TripStatistics(
            totalTrips = trips.size,
            totalDistance = totalDistance,
            totalEarningsOrSpent = totalEarnings,
            averageRating = averageRating,
            joinDate = LocalDateTime.now().minusMonths(3)
        )
    }

    /**
     * Filter trips by date range
     */
    fun getTripsInDateRange(
        userId: String,
        isDriver: Boolean,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CompletedTrip> {
        val allTrips = getUserTrips(userId, isDriver)
        return allTrips.filter { trip ->
            trip.startTime.isAfter(startDate) && trip.startTime.isBefore(endDate)
        }
    }
}

data class TripStatistics(
    val totalTrips: Int,
    val totalDistance: Double,
    val totalEarningsOrSpent: Double,
    val averageRating: Double,
    val joinDate: LocalDateTime
)
