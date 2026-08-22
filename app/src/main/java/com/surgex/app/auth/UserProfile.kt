package com.surgex.app.auth

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val activeMode: UserRole,
    val accountStatus: String,
    val phoneVerified: Boolean
)
