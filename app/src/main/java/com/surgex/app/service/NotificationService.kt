package com.surgex.app.service

/**
 * In-app notification service for trip updates, cancellations, and messages
 */
class NotificationService {
    private val notifications = mutableListOf<AppNotification>()

    /**
     * Send trip started notification
     */
    fun notifyTripStarted(driverName: String, vehicleInfo: String, eta: Int) {
        val notification = AppNotification(
            id = "TRIP_START_${System.currentTimeMillis()}",
            type = "TRIP_STARTED",
            title = "Driver $driverName is on the way",
            message = "$vehicleInfo - ETA $eta minutes",
            timestamp = java.time.LocalDateTime.now()
        )
        notifications.add(notification)
    }

    /**
     * Send trip completed notification
     */
    fun notifyTripCompleted(tripId: String, fare: Double) {
        val notification = AppNotification(
            id = "TRIP_END_$tripId",
            type = "TRIP_COMPLETED",
            title = "Trip completed",
            message = "Total fare: R$fare",
            timestamp = java.time.LocalDateTime.now()
        )
        notifications.add(notification)
    }

    /**
     * Send rating reminder notification
     */
    fun notifyRatingReminder(tripId: String) {
        val notification = AppNotification(
            id = "RATING_$tripId",
            type = "RATING_REMINDER",
            title = "Rate your trip",
            message = "Help us improve by rating your recent trip",
            timestamp = java.time.LocalDateTime.now()
        )
        notifications.add(notification)
    }

    /**
     * Send promotion notification
     */
    fun notifyPromotion(promoCode: String, discount: Double) {
        val notification = AppNotification(
            id = "PROMO_$promoCode",
            type = "PROMOTION",
            title = "Special offer: $discount% off",
            message = "Use code $promoCode on your next ride",
            timestamp = java.time.LocalDateTime.now()
        )
        notifications.add(notification)
    }
}

data class AppNotification(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: java.time.LocalDateTime
)
