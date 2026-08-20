package com.surgex.app.auth

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

enum class UserRole { RIDER, DRIVER }

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val activeMode: UserRole,
    val accountStatus: String,
    val phoneVerified: Boolean = false
)

class AuthController {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var storedVerificationId: String? = null

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = auth.currentUser != null

    // Web Client ID from Firebase
    private val webClientId = "629944240953-8o0qmd7noccdkj26pm7luoq6meq47edj.apps.googleusercontent.com"

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole
    ): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Registration failed.")
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "role" to role.name,
                "activeMode" to role.name,
                "accountStatus" to "APPROVED",
                "phoneVerified" to false,
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("users").document(uid).set(userData).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed.")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed. Check your email and password.")
        }
    }

    suspend fun signInWithGoogle(activity: Activity): AuthResult {
        return try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(activity, gso)
            googleSignInClient.signOut().await()

            val signInIntent = googleSignInClient.signInIntent
            activity.startActivityForResult(signInIntent, 9001)

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google Sign-In failed.")
        }
    }

    suspend fun handleGoogleSignInResult(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return AuthResult.Error("Google Sign-In failed.")

            val doc = db.collection("users").document(user.uid).get().await()
            if (!doc.exists()) {
                val userData = hashMapOf(
                    "name" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "phone" to "",
                    "role" to UserRole.RIDER.name,
                    "activeMode" to UserRole.RIDER.name,
                    "accountStatus" to "APPROVED",
                    "phoneVerified" to true,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(user.uid).set(userData).await()
            }

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google Sign-In failed.")
        }
    }

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onAutoVerified: () -> Unit,
        onError: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onAutoVerified()
            }
            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.message ?: "Failed to send OTP.")
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
            .setPhoneNumber(phoneNumber)
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
            db.collection("users").document(uid).update("phoneVerified", true).await()
            AuthResult.Success
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
                accountStatus = doc.getString("accountStatus") ?: "APPROVED",
                phoneVerified = doc.getBoolean("phoneVerified") ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveActiveMode(role: UserRole): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(uid).update("activeMode", role.name).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
    }
}
