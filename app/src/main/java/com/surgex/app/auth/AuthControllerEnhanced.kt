package com.surgex.app.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.surgex.app.utils.PhoneValidator
import com.surgex.app.data.models.User
import com.surgex.app.data.models.DriverProfile
import com.surgex.app.data.models.RiderProfile
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthControllerEnhanced {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var storedVerificationId: String? = null

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = auth.currentUser != null
    val currentUserId: String? get() = auth.currentUser?.uid

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String = "RIDER"
    ): AuthResult {
        return try {
            // Validate phone
            if (!PhoneValidator.isValidSouthAfricanPhone(phone)) {
                return AuthResult.Error("Please enter a valid South African phone number (+27...)")
            }

            // Validate email
            if (email.isBlank() || !email.contains("@")) {
                return AuthResult.Error("Please enter a valid email address.")
            }

            // Validate password
            if (password.length < 6) {
                return AuthResult.Error("Password must be at least 6 characters.")
            }

            val formattedPhone = PhoneValidator.formatToInternational(phone)
            
            // Create Firebase auth user
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Registration failed.")
            
            // Create user profile
            val userData = User(
                uid = uid,
                name = name.trim(),
                email = email.trim(),
                phone = formattedPhone,
                role = role,
                activeMode = role,
                accountStatus = "ACTIVE",
                phoneVerified = false,
                createdAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                riderProfile = if (role == "RIDER") RiderProfile(uid = uid) else null,
                driverProfile = if (role == "DRIVER") DriverProfile(uid = uid) else null
            )
            
            db.collection("users").document(uid).set(userData).await()
            AuthResult.Success
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("This email is already registered.")
        } catch (e: FirebaseAuthWeakPasswordException) {
            AuthResult.Error("Password must be at least 6 characters.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed.")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            
            // Update last login
            val uid = auth.currentUser?.uid
            if (uid != null) {
                db.collection("users").document(uid)
                    .update("lastLogin", System.currentTimeMillis()).await()
            }
            
            AuthResult.Success
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error("No account found with this email.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Incorrect password.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed.")
        }
    }

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onAutoVerified: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!PhoneValidator.isValidSouthAfricanPhone(phoneNumber)) {
            onError("Invalid South African phone number format.")
            return
        }

        val formattedPhone = PhoneValidator.formatToInternational(phoneNumber)
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onAutoVerified()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.message ?: "Failed to send OTP. Please check your phone number.")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun verifyOtp(otp: String): AuthResult {
        val verificationId = storedVerificationId
            ?: return AuthResult.Error("Session expired. Please request a new OTP.")
        
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            auth.currentUser?.linkWithCredential(credential)?.await()
            
            val uid = auth.currentUser?.uid ?: return AuthResult.Error("No user found.")
            db.collection("users").document(uid)
                .update("phoneVerified", true).await()
            
            storedVerificationId = null
            AuthResult.Success
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("This phone number is already linked to another account.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid OTP. Please try again.")
        }
    }

    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun switchMode(newMode: String): AuthResult {
        val uid = auth.currentUser?.uid ?: return AuthResult.Error("Not logged in.")
        
        return try {
            db.collection("users").document(uid)
                .update("activeMode", newMode).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to switch mode.")
        }
    }

    suspend fun saveDriverProfile(driverProfile: DriverProfile): AuthResult {
        val uid = auth.currentUser?.uid ?: return AuthResult.Error("Not logged in.")
        
        return try {
            db.collection("users").document(uid)
                .update(
                    "driverProfile" to driverProfile,
                    "role" to "DRIVER",
                    "activeMode" to "DRIVER"
                ).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to save driver profile.")
        }
    }

    suspend fun updateProfilePicture(imageUrl: String): AuthResult {
        val uid = auth.currentUser?.uid ?: return AuthResult.Error("Not logged in.")
        
        return try {
            db.collection("users").document(uid)
                .update("profilePictureUrl", imageUrl).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update profile picture.")
        }
    }

    suspend fun updateDrivingHours(hours: Long): AuthResult {
        val uid = auth.currentUser?.uid ?: return AuthResult.Error("Not logged in.")
        
        return try {
            db.collection("users").document(uid)
                .update("driverProfile.totalDrivingHours", hours).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update driving hours.")
        }
    }

    fun logout() {
        auth.signOut()
        storedVerificationId = null
    }
}
