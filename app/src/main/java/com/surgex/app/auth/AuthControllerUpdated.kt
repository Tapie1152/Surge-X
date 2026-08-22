package com.surgex.app.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.surgex.app.utils.PhoneValidator
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthControllerUpdated {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var storedVerificationId: String? = null

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole
    ): AuthResult {
        return try {
            // Validate phone number format
            if (!PhoneValidator.isValidSouthAfricanPhone(phone)) {
                return AuthResult.Error("Please enter a valid South African phone number (+27...")
            }

            val formattedPhone = PhoneValidator.formatToInternational(phone)
            
            // Create Firebase auth user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Registration failed.")
            
            // Create user profile
            val userData = hashMapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "phone" to formattedPhone,
                "role" to role.name,
                "activeMode" to role.name,
                "accountStatus" to "ACTIVE",
                "phoneVerified" to false,
                "profilePictureUrl" to "",
                "createdAt" to System.currentTimeMillis(),
                "lastLogin" to System.currentTimeMillis()
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
            auth.signInWithEmailAndPassword(email, password).await()
            
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

    suspend fun handleGoogleSignInResult(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            AuthResult.Success
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error("Google account not registered.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign-in failed.")
        }
    }

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onAutoVerified: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validate phone first
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
                onError(e.message ?: "Failed to send OTP. Check the number and try again.")
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                onCodeSent()
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
            AuthResult.Success
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("This phone number is already linked to another account.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid OTP. Please try again.")
        }
    }

    suspend fun getCurrentUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (!doc.exists()) return null
            val roleStr = doc.getString("role") ?: "RIDER"
            val activeModeStr = doc.getString("activeMode") ?: roleStr
            UserProfile(
                uid = uid,
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: "",
                role = if (roleStr == "DRIVER") UserRole.DRIVER else UserRole.RIDER,
                activeMode = if (activeModeStr == "DRIVER") UserRole.DRIVER else UserRole.RIDER,
                accountStatus = doc.getString("accountStatus") ?: "ACTIVE",
                phoneVerified = doc.getBoolean("phoneVerified") ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveActiveMode(role: UserRole): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(uid)
                .update("activeMode", role.name).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveDriverProfile(driverProfile: DriverProfile): AuthResult {
        val uid = auth.currentUser?.uid ?: return AuthResult.Error("No user logged in.")
        return try {
            db.collection("users").document(uid)
                .update(mapOf(
                    "driverProfile" to driverProfile,
                    "activeMode" to "DRIVER"
                )).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to save driver profile.")
        }
    }

    fun logout() {
        auth.signOut()
        storedVerificationId = null
    }
}
