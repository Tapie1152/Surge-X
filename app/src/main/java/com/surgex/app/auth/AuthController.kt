package com.surgex.app.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication and user session management
 */
class AuthController {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: Boolean
        get() = _isLoggedIn.value

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _userRole = MutableStateFlow<UserRole>(UserRole.RIDER)
    val userRole: StateFlow<UserRole> = _userRole

    /**
     * Login user with phone and OTP
     */
    fun login(phoneNumber: String, otp: String): Result<User> {
        return try {
            // TODO: Verify OTP with backend
            val user = User(
                id = "USER_${System.currentTimeMillis()}",
                phoneNumber = phoneNumber,
                name = "User",
                email = "user@surgex.app",
                profilePictureUrl = null,
                accountStatus = "APPROVED",
                activeMode = UserRole.RIDER,
                createdAt = java.time.LocalDateTime.now()
            )
            _currentUser.value = user
            _isLoggedIn.value = true
            _userRole.value = user.activeMode
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register new user
     */
    fun register(phoneNumber: String, name: String, email: String): Result<User> {
        return try {
            val user = User(
                id = "USER_${System.currentTimeMillis()}",
                phoneNumber = phoneNumber,
                name = name,
                email = email,
                profilePictureUrl = null,
                accountStatus = "PENDING",
                activeMode = UserRole.RIDER,
                createdAt = java.time.LocalDateTime.now()
            )
            _currentUser.value = user
            _isLoggedIn.value = false
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Handle Google Sign-In with ID token
     */
    suspend fun handleGoogleSignInResult(idToken: String): AuthResult {
        return try {
            // TODO: Verify idToken with backend
            val user = User(
                id = "USER_${System.currentTimeMillis()}",
                phoneNumber = "",
                name = "Google User",
                email = "user@surgex.app",
                profilePictureUrl = null,
                accountStatus = "APPROVED",
                activeMode = UserRole.RIDER,
                createdAt = java.time.LocalDateTime.now()
            )
            _currentUser.value = user
            _isLoggedIn.value = true
            _userRole.value = user.activeMode
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google Sign-In failed")
        }
    }

    /**
     * Switch between rider and driver mode
     */
    fun switchMode(role: UserRole) {
        _userRole.value = role
        _currentUser.value?.let { user ->
            _currentUser.value = user.copy(activeMode = role)
        }
    }

    /**
     * Save active mode preference
     */
    suspend fun saveActiveMode(role: UserRole) {
        switchMode(role)
        // TODO: Save to backend/preferences
    }

    /**
     * Upload or update profile picture
     */
    fun uploadProfilePicture(imageUri: String): Result<String> {
        return try {
            _currentUser.value?.let { user ->
                _currentUser.value = user.copy(profilePictureUrl = imageUri)
            }
            Result.success(imageUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current user profile
     */
    fun getCurrentUserProfile(): User? {
        return _currentUser.value
    }

    /**
     * Logout
     */
    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _userRole.value = UserRole.RIDER
    }
}

data class User(
    val id: String,
    val phoneNumber: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val accountStatus: String, // PENDING, APPROVED, SUSPENDED
    val activeMode: UserRole,
    val createdAt: java.time.LocalDateTime,
    val rating: Double = 5.0,
    val totalTrips: Int = 0
)

enum class UserRole {
    RIDER, DRIVER
}
