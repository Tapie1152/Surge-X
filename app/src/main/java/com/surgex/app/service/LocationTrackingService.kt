package com.surgex.app.service

import android.content.Context
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Real-time location tracking service for driver and rider
 */
class LocationTrackingService(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _driverLocation = MutableStateFlow<LocationPoint?>(null)
    val driverLocation: StateFlow<LocationPoint?> = _driverLocation

    private val _riderLocation = MutableStateFlow<LocationPoint?>(null)
    val riderLocation: StateFlow<LocationPoint?> = _riderLocation

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance

    private val _eta = MutableStateFlow(0) // in seconds
    val eta: StateFlow<Int> = _eta

    /**
     * Update driver's current location
     */
    fun updateDriverLocation(lat: Double, lon: Double) {
        _driverLocation.value = LocationPoint(lat, lon)
        calculateDistance()
    }

    /**
     * Update rider's current location
     */
    fun updateRiderLocation(lat: Double, lon: Double) {
        _riderLocation.value = LocationPoint(lat, lon)
        calculateDistance()
    }

    /**
     * Calculate real-time distance between driver and destination
     */
    private fun calculateDistance() {
        val driver = _driverLocation.value
        val rider = _riderLocation.value

        if (driver != null && rider != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                driver.latitude, driver.longitude,
                rider.latitude, rider.longitude,
                results
            )
            val distanceMeters = results[0]
            _distance.value = (distanceMeters / 1000.0) // Convert to km

            // Estimate ETA (assuming 30 km/h average speed)
            val speedKmH = 30.0
            val timeHours = _distance.value / speedKmH
            _eta.value = (timeHours * 3600).toInt()
        }
    }

    /**
     * Get last known location
     */
    fun getLastKnownLocation(provider: String = LocationManager.GPS_PROVIDER): LocationPoint? {
        return try {
            val location = locationManager.getLastKnownLocation(provider)
            location?.let {
                LocationPoint(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }
}
