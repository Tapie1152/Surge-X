package com.surgex.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

/**
 * Rating and review management system
 */
class RatingService {
    private val _userRatings = MutableStateFlow<List<UserRating>>(emptyList())
    val userRatings: StateFlow<List<UserRating>> = _userRatings

    private val _averageRating = MutableStateFlow(5.0)
    val averageRating: StateFlow<Double> = _averageRating

    /**
     * Submit rating for a completed trip
     */
    fun submitRating(
        tripId: String,
        ratedUserId: String,
        ratingValue: Int,
        review: String? = null
    ) {
        val rating = UserRating(
            tripId = tripId,
            ratedUserId = ratedUserId,
            rating = ratingValue,
            review = review,
            timestamp = LocalDateTime.now()
        )
        _userRatings.value = _userRatings.value + rating
        recalculateAverageRating()
    }

    /**
     * Get ratings for a specific user
     */
    fun getUserRatings(userId: String): List<UserRating> {
        return _userRatings.value.filter { it.ratedUserId == userId }
    }

    /**
     * Get average rating for a user
     */
    fun getUserAverageRating(userId: String): Double {
        val userRatings = getUserRatings(userId)
        return if (userRatings.isEmpty()) {
            5.0
        } else {
            userRatings.map { it.rating }.average()
        }
    }

    /**
     * Recalculate overall average rating
     */
    private fun recalculateAverageRating() {
        _averageRating.value = if (_userRatings.value.isEmpty()) {
            5.0
        } else {
            _userRatings.value.map { it.rating }.average()
        }
    }
}

data class UserRating(
    val tripId: String,
    val ratedUserId: String,
    val rating: Int, // 1-5
    val review: String? = null,
    val timestamp: LocalDateTime
)
