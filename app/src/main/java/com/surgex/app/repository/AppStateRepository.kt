package com.surgex.app.repository

import androidx.compose.runtime.mutableStateOf
import com.surgex.app.service.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Central app state management and service coordination
 */
class AppStateRepository(
    val pricingEngine: PricingEngine = PricingEngine(),
    val tripManagement: TripManagementService = TripManagementService(),
    val locationTracking: LocationTrackingService? = null,
    val realTimeCommunication: RealTimeCommunicationService = RealTimeCommunicationService(),
    val ratingService: RatingService = RatingService(),
    val emergencyService: EmergencyService = EmergencyService(),
    val paymentService: PaymentService = PaymentService(),
    val tripHistoryRepository: TripHistoryRepository = TripHistoryRepository(),
    val notificationService: NotificationService = NotificationService()
) {
    private val _appState = MutableStateFlow<AppState>(AppState.Idle)
    val appState: StateFlow<AppState> = _appState

    private val _currentRideOption = MutableStateFlow<RideOption?>(null)
    val currentRideOption: StateFlow<RideOption?> = _currentRideOption

    /**
     * Request a ride - coordinate all services
     */
    fun requestRide(
        riderId: String,
        pickupLat: Double,
        pickupLon: Double,
        dropoffLat: Double,
        dropoffLon: Double
    ) {
        _appState.value = AppState.MatchingDriver

        // Calculate distance and fare
        val distance = pricingEngine.calculateDistance(
            pickupLat, pickupLon,
            dropoffLat, dropoffLon
        )
        val estimatedTime = pricingEngine.estimateTime(distance)
        val fare = pricingEngine.calculateFare(
            distance,
            estimatedTime,
            isRushHour = false,
            availableDrivers = 5 // Mock available drivers
        )

        // Create ride option
        _currentRideOption.value = RideOption(
            rideType = "REGULAR",
            distance = distance,
            estimatedTime = estimatedTime,
            fare = fare,
            driverRating = 4.8,
            driverId = "DRIVER_001"
        )

        // Connect real-time communication
        realTimeCommunication.connect(riderId, "RIDER")

        // Start trip when driver accepts
        tripManagement.startTrip(
            riderId = riderId,
            driverId = "DRIVER_001",
            pickupLat = pickupLat,
            pickupLon = pickupLon,
            dropoffLat = dropoffLat,
            dropoffLon = dropoffLon,
            estimatedFare = fare
        )

        _appState.value = AppState.PickupInProgress
    }

    /**
     * Complete trip - coordinate payment, rating, and history
     */
    fun completeTrip(rating: Int, paymentMethod: String) {
        _appState.value = AppState.ProcessingPayment

        tripManagement.currentTrip.value?.let { trip ->
            // Process payment
            val paymentResult = paymentService.processPayment(
                tripId = trip.tripId,
                amount = trip.estimatedFare,
                paymentMethodId = "PM_001",
                paymentType = paymentMethod
            )

            when (paymentResult) {
                is PaymentResult.Success -> {
                    // Complete the trip
                    tripManagement.completeTrip(rating, paymentMethod)

                    // Add to history
                    tripManagement.currentTrip.value?.let { completedTrip ->
                        val trip = CompletedTrip(
                            tripId = completedTrip.tripId,
                            riderId = completedTrip.riderId,
                            driverId = completedTrip.driverId,
                            pickupLocation = completedTrip.pickupLocation,
                            dropoffLocation = completedTrip.dropoffLocation,
                            startTime = completedTrip.startTime,
                            endTime = java.time.LocalDateTime.now(),
                            estimatedFare = completedTrip.estimatedFare,
                            actualFare = completedTrip.estimatedFare,
                            distance = 0.0,
                            rating = rating,
                            paymentMethod = paymentMethod,
                            status = "COMPLETED"
                        )
                        tripHistoryRepository.addCompletedTrip(trip)
                    }

                    // Submit rating
                    ratingService.submitRating(
                        tripId = trip.tripId,
                        ratedUserId = trip.driverId,
                        ratingValue = rating,
                        review = null
                    )

                    // Send notifications
                    notificationService.notifyTripCompleted(trip.tripId, trip.estimatedFare)
                    notificationService.notifyRatingReminder(trip.tripId)

                    _appState.value = AppState.TripCompleted
                }

                is PaymentResult.Failure -> {
                    _appState.value = AppState.PaymentFailed(paymentResult.error)
                }
            }
        }
    }

    /**
     * Cancel active trip
     */
    fun cancelTrip(reason: String = "User requested") {
        tripManagement.cancelTrip(reason)
        realTimeCommunication.disconnect()
        _appState.value = AppState.TripCancelled
    }
}

data class RideOption(
    val rideType: String, // REGULAR, PREMIUM, POOL
    val distance: Double,
    val estimatedTime: Int, // in minutes
    val fare: Double,
    val driverRating: Double,
    val driverId: String
)

sealed class AppState {
    object Idle : AppState()
    object MatchingDriver : AppState()
    object PickupInProgress : AppState()
    object InProgress : AppState()
    object ProcessingPayment : AppState()
    object TripCompleted : AppState()
    object TripCancelled : AppState()
    data class PaymentFailed(val error: String) : AppState()
    data class Error(val message: String) : AppState()
}
