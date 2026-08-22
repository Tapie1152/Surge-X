package com.surgex.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Manages trip lifecycle and real-time updates
 */
class TripManagementService {
    private val _tripState = MutableStateFlow<TripState>(TripState.Idle)
    val tripState: StateFlow<TripState> = _tripState

    private val _currentTrip = MutableStateFlow<Trip?>(null)
    val currentTrip: StateFlow<Trip?> = _currentTrip

    private val _tripHistory = MutableStateFlow<List<CompletedTrip>>(emptyList())
    val tripHistory: StateFlow<List<CompletedTrip>> = _tripHistory

    /**
     * Initialize a new trip with rider and driver assignment
     */
    fun startTrip(
        riderId: String,
        driverId: String,
        pickupLat: Double,
        pickupLon: Double,
        dropoffLat: Double,
        dropoffLon: Double,
        estimatedFare: Double
    ) {
        val trip = Trip(
            tripId = generateTripId(),
            riderId = riderId,
            driverId = driverId,
            pickupLocation = LocationPoint(pickupLat, pickupLon),
            dropoffLocation = LocationPoint(dropoffLat, dropoffLon),
            status = "ACTIVE",
            estimatedFare = estimatedFare,
            startTime = LocalDateTime.now(),
            route = emptyList()
        )
        _currentTrip.value = trip
        _tripState.value = TripState.PickupInProgress
    }

    /**
     * Update trip status and route
     */
    fun updateTripRoute(routePoints: List<LocationPoint>) {
        _currentTrip.value?.let { trip ->
            _currentTrip.value = trip.copy(route = routePoints)
        }
    }

    /**
     * Mark pickup as complete
     */
    fun markPickupComplete() {
        _tripState.value = TripState.InProgress
        _currentTrip.value?.let { trip ->
            _currentTrip.value = trip.copy(status = "IN_PROGRESS")
        }
    }

    /**
     * Complete the trip
     */
    fun completeTrip(rating: Int = 5, paymentMethod: String = "CASH") {
        _currentTrip.value?.let { trip ->
            val endTime = LocalDateTime.now()
            val completedTrip = CompletedTrip(
                tripId = trip.tripId,
                riderId = trip.riderId,
                driverId = trip.driverId,
                pickupLocation = trip.pickupLocation,
                dropoffLocation = trip.dropoffLocation,
                startTime = trip.startTime,
                endTime = endTime,
                estimatedFare = trip.estimatedFare,
                actualFare = trip.estimatedFare, // In real app, calculate actual fare
                distance = 0.0, // Calculate from route
                rating = rating,
                paymentMethod = paymentMethod,
                status = "COMPLETED"
            )

            _tripHistory.value = _tripHistory.value + completedTrip
            _currentTrip.value = null
            _tripState.value = TripState.Completed(rating)
        }
    }

    /**
     * Cancel the trip
     */
    fun cancelTrip(reason: String = "User requested") {
        _tripState.value = TripState.Cancelled(reason)
        _currentTrip.value = null
    }

    private fun generateTripId(): String {
        return "TRIP_${System.currentTimeMillis()}"
    }
}

data class Trip(
    val tripId: String,
    val riderId: String,
    val driverId: String,
    val pickupLocation: LocationPoint,
    val dropoffLocation: LocationPoint,
    val status: String, // ACTIVE, IN_PROGRESS, COMPLETED, CANCELLED
    val estimatedFare: Double,
    val startTime: LocalDateTime,
    val route: List<LocationPoint>
)

data class CompletedTrip(
    val tripId: String,
    val riderId: String,
    val driverId: String,
    val pickupLocation: LocationPoint,
    val dropoffLocation: LocationPoint,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val estimatedFare: Double,
    val actualFare: Double,
    val distance: Double,
    val rating: Int,
    val paymentMethod: String,
    val status: String
)

data class LocationPoint(
    val latitude: Double,
    val longitude: Double
)

sealed class TripState {
    object Idle : TripState()
    object PickupInProgress : TripState()
    object InProgress : TripState()
    data class Completed(val rating: Int) : TripState()
    data class Cancelled(val reason: String) : TripState()
}
