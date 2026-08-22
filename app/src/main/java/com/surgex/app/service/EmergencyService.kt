package com.surgex.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Emergency assistance and safety features
 */
class EmergencyService {
    private val _emergencyActive = MutableStateFlow(false)
    val emergencyActive: StateFlow<Boolean> = _emergencyActive

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts

    private val _sosHistory = MutableStateFlow<List<SOSAlert>>(emptyList())
    val sosHistory: StateFlow<List<SOSAlert>> = _sosHistory

    /**
     * Activate emergency SOS
     */
    fun activateEmergencySOS(
        userId: String,
        tripId: String?,
        location: LocationPoint
    ) {
        _emergencyActive.value = true
        val alert = SOSAlert(
            userId = userId,
            tripId = tripId,
            location = location,
            timestamp = java.time.LocalDateTime.now()
        )
        _sosHistory.value = _sosHistory.value + alert

        // TODO: Notify emergency services and contacts
    }

    /**
     * Deactivate emergency
     */
    fun deactivateEmergency() {
        _emergencyActive.value = false
    }

    /**
     * Add emergency contact
     */
    fun addEmergencyContact(
        name: String,
        phoneNumber: String,
        relationship: String
    ) {
        val contact = EmergencyContact(
            name = name,
            phoneNumber = phoneNumber,
            relationship = relationship
        )
        _emergencyContacts.value = _emergencyContacts.value + contact
    }

    /**
     * Remove emergency contact
     */
    fun removeEmergencyContact(phoneNumber: String) {
        _emergencyContacts.value = _emergencyContacts.value.filter {
            it.phoneNumber != phoneNumber
        }
    }

    /**
     * Share live location with emergency contacts
     */
    fun shareLiveLocation(
        tripId: String,
        location: LocationPoint,
        durationMinutes: Int = 60
    ) {
        // TODO: Implement live location sharing via WebSocket
    }
}

data class EmergencyContact(
    val name: String,
    val phoneNumber: String,
    val relationship: String
)

data class SOSAlert(
    val userId: String,
    val tripId: String?,
    val location: LocationPoint,
    val timestamp: java.time.LocalDateTime
)
