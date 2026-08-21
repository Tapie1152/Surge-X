package com.surgex.app.ui.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.surgex.app.auth.AuthController
import com.surgex.app.auth.UserRole
import com.surgex.app.core.trip.SurgeXTripController
import com.surgex.app.domain.payment.PaymentMethod
import com.surgex.app.ui.screens.auth.LoginScreen
import com.surgex.app.ui.screens.auth.OtpScreen
import com.surgex.app.ui.screens.auth.PhoneVerifyScreen
import com.surgex.app.ui.screens.auth.RegisterScreen
import com.surgex.app.ui.screens.driver.*
import com.surgex.app.ui.screens.onboarding.RoleSelectionScreen
import com.surgex.app.ui.screens.rider.*
import com.surgex.app.ui.screens.splash.SplashScreen
import kotlinx.coroutines.launch

private enum class SurgeXScreen {
    SPLASH, ROLE_SELECTION, LOGIN, REGISTER,
    PHONE_VERIFY, OTP_VERIFY, PROFILE_PIC_MANDATORY,
    RIDER_HOME, RIDER_SAFETY, RIDER_MENU, DRIVER_PROFILE, RIDE_SELECTION, SEARCHING_DRIVER,
    DRIVER_HOME, DRIVER_MENU, DRIVER_RIDE_REQUEST, DRIVER_PICKUP,
    PASSENGER_VERIFICATION, LIVE_TRIP, TRIP_SUMMARY,
    RIDER_PAYMENT, RECEIPT,
    TRIP_HISTORY, PAYMENT_METHODS, DRIVER_SAFETY, SETTINGS,
    HELP, REPORT_ISSUE, DRIVER_DOCUMENTS, PROFILE_PIC_UPLOAD,
    CAR_PHOTOS_VERIFICATION, DRIVER_TRIP_HISTORY, DRIVER_EARNINGS,
    DRIVER_SETTINGS, DEVELOPER_MODE_SWITCH, DEVELOPER_RECODE
}

private const val PREFS_NAME = "surgex_preferences"
private const val LAST_MODE_KEY = "last_mode"
private const val LAST_SCREEN_KEY = "last_screen"
private const val DEV_MODE_KEY = "dev_mode"
private const val PROFILE_PIC_UPLOADED_KEY = "profile_pic_uploaded"

@Composable
fun SurgeXNavigation(
    onGoogleSignInRequest: ((Boolean) -> Unit) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val preferences = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val authController = remember { AuthController() }
    val tripController = remember { SurgeXTripController() }

    var currentScreen by remember { mutableStateOf(SurgeXScreen.SPLASH) }
    var selectedRole by remember { mutableStateOf(UserRole.RIDER) }
    var checkingSession by remember { mutableStateOf(true) }
    var pendingPhone by remember { mutableStateOf("") }
    var devMode by remember { mutableStateOf(preferences.getBoolean(DEV_MODE_KEY, false)) }
    var profilePicUploaded by remember { mutableStateOf(preferences.getBoolean(PROFILE_PIC_UPLOADED_KEY, false)) }

    // Load last screen from preferences
    LaunchedEffect(Unit) {
        if (!authController.isLoggedIn) {
            checkingSession = false
            currentScreen = SurgeXScreen.ROLE_SELECTION
        } else {
            val profile = authController.getCurrentUserProfile()
            if (profile == null || profile.accountStatus != "APPROVED") {
                authController.logout()
                checkingSession = false
                currentScreen = SurgeXScreen.ROLE_SELECTION
            } else {
                // Check if profile picture is uploaded
                profilePicUploaded = preferences.getBoolean(PROFILE_PIC_UPLOADED_KEY, false)
                
                if (!profilePicUploaded) {
                    // Mandatory profile picture upload
                    checkingSession = false
                    currentScreen = SurgeXScreen.PROFILE_PIC_MANDATORY
                } else {
                    val lastMode = preferences.getString(LAST_MODE_KEY, UserRole.RIDER.name)
                    selectedRole = if (lastMode == UserRole.DRIVER.name && profile.accountStatus == "APPROVED") {
                        UserRole.DRIVER
                    } else {
                        UserRole.RIDER
                    }
                    preferences.edit().putString(LAST_MODE_KEY, selectedRole.name).apply()
                    checkingSession = false
                    
                    // Restore last screen
                    val lastScreen = preferences.getString("last_screen_${selectedRole.name}", null)
                    currentScreen = if (selectedRole == UserRole.RIDER)
                        SurgeXScreen.RIDER_HOME else SurgeXScreen.DRIVER_HOME
                }
            }
        }
    }

    // Save current screen to preferences
    DisposableEffect(currentScreen, selectedRole) {
        preferences.edit().putString("last_screen_${selectedRole.name}", currentScreen.name).apply()
        onDispose { }
    }

    if (checkingSession) {
        SplashScreen {}
        return
    }

    when (currentScreen) {

        SurgeXScreen.SPLASH -> SplashScreen { currentScreen = SurgeXScreen.ROLE_SELECTION }

        SurgeXScreen.ROLE_SELECTION -> RoleSelectionScreen(
            onRiderSelected = {
                selectedRole = UserRole.RIDER
                preferences.edit().putString(LAST_MODE_KEY, UserRole.RIDER.name).apply()
                currentScreen = if (authController.isLoggedIn) {
                    if (!profilePicUploaded) SurgeXScreen.PROFILE_PIC_MANDATORY else SurgeXScreen.RIDER_HOME
                } else SurgeXScreen.LOGIN
            },
            onDriverSelected = {
                selectedRole = UserRole.DRIVER
                preferences.edit().putString(LAST_MODE_KEY, UserRole.DRIVER.name).apply()
                currentScreen = if (authController.isLoggedIn) {
                    if (!profilePicUploaded) SurgeXScreen.PROFILE_PIC_MANDATORY else SurgeXScreen.DRIVER_HOME
                } else SurgeXScreen.LOGIN
            }
        )

        SurgeXScreen.LOGIN -> LoginScreen(
            authController = authController,
            onLoginSuccess = {
                scope.launch {
                    val profile = authController.getCurrentUserProfile()
                    selectedRole = profile?.activeMode ?: UserRole.RIDER
                    profilePicUploaded = false
                    currentScreen = SurgeXScreen.PROFILE_PIC_MANDATORY
                }
            },
            onRegister = { currentScreen = SurgeXScreen.REGISTER },
            onBack = { currentScreen = SurgeXScreen.ROLE_SELECTION },
            onGoogleSignIn = { callback ->
                onGoogleSignInRequest { success ->
                    if (success) {
                        scope.launch {
                            val profile = authController.getCurrentUserProfile()
                            selectedRole = profile?.activeMode ?: UserRole.RIDER
                            profilePicUploaded = false
                            currentScreen = SurgeXScreen.PROFILE_PIC_MANDATORY
                        }
                    }
                    callback(success)
                }
            }
        )

        SurgeXScreen.REGISTER -> RegisterScreen(
            role = selectedRole,
            authController = authController,
            onRegisterSuccess = { phone ->
                pendingPhone = phone
                currentScreen = SurgeXScreen.PHONE_VERIFY
            },
            onBack = { currentScreen = SurgeXScreen.LOGIN }
        )

        SurgeXScreen.PHONE_VERIFY -> PhoneVerifyScreen(
            phoneNumber = pendingPhone,
            authController = authController,
            onCodeSent = { currentScreen = SurgeXScreen.OTP_VERIFY },
            onBack = { currentScreen = SurgeXScreen.REGISTER }
        )

        SurgeXScreen.OTP_VERIFY -> OtpScreen(
            phoneNumber = pendingPhone,
            authController = authController,
            onVerified = {
                profilePicUploaded = false
                currentScreen = SurgeXScreen.PROFILE_PIC_MANDATORY
            },
            onBack = { currentScreen = SurgeXScreen.PHONE_VERIFY }
        )

        SurgeXScreen.PROFILE_PIC_MANDATORY -> ProfilePicUploadScreen(
            isMandatory = true,
            onBack = { currentScreen = SurgeXScreen.OTP_VERIFY },
            onUploadSuccess = {
                profilePicUploaded = true
                preferences.edit().putBoolean(PROFILE_PIC_UPLOADED_KEY, true).apply()
                currentScreen = if (selectedRole == UserRole.RIDER)
                    SurgeXScreen.RIDER_HOME else SurgeXScreen.DRIVER_HOME
            }
        )

        SurgeXScreen.RIDER_HOME -> RiderHomeScreen(
            onChooseRide = { currentScreen = SurgeXScreen.RIDE_SELECTION },
            onMenuClick = { currentScreen = SurgeXScreen.RIDER_MENU },
            onSwitchToDriver = {
                selectedRole = UserRole.DRIVER
                preferences.edit().putString(LAST_MODE_KEY, UserRole.DRIVER.name).apply()
                scope.launch { authController.saveActiveMode(UserRole.DRIVER) }
                currentScreen = if (devMode) SurgeXScreen.DRIVER_HOME else SurgeXScreen.DRIVER_DOCUMENTS
            },
            onTripHistory = { currentScreen = SurgeXScreen.TRIP_HISTORY },
            onPaymentMethods = { currentScreen = SurgeXScreen.PAYMENT_METHODS },
            onSafety = { currentScreen = SurgeXScreen.RIDER_SAFETY },
            onSettings = { currentScreen = SurgeXScreen.SETTINGS },
            onProfilePicUpload = { currentScreen = SurgeXScreen.PROFILE_PIC_UPLOAD },
            devModeEnabled = devMode,
            onDeveloperClick = { currentScreen = SurgeXScreen.DEVELOPER_MODE_SWITCH }
        )

        SurgeXScreen.RIDER_MENU -> RiderMenuScreen(
            onTripHistoryClick = {
                currentScreen = SurgeXScreen.TRIP_HISTORY
            },
            onPaymentMethodsClick = {
                currentScreen = SurgeXScreen.PAYMENT_METHODS
            },
            onSafetyClick = {
                currentScreen = SurgeXScreen.RIDER_SAFETY
            },
            onSettingsClick = {
                currentScreen = SurgeXScreen.SETTINGS
            },
            onProfilePicClick = {
                currentScreen = SurgeXScreen.PROFILE_PIC_UPLOAD
            },
            onClose = { currentScreen = SurgeXScreen.RIDER_HOME }
        )

        SurgeXScreen.RIDE_SELECTION -> RideSelectionScreen(
            onBack = { currentScreen = SurgeXScreen.RIDER_HOME },
            onConfirmRide = {
                tripController.createRide(
                    riderName = "SurgeX Rider",
                    driverName = "SurgeX Driver",
                    pickupAddress = "Current pickup",
                    destinationAddress = "Cape Town CBD"
                )
                currentScreen = SurgeXScreen.SEARCHING_DRIVER
            }
        )

        SurgeXScreen.SEARCHING_DRIVER -> SearchingDriverScreen(
            onDriverFound = { currentScreen = SurgeXScreen.LIVE_TRIP },
            onCancel = {
                tripController.clear()
                currentScreen = SurgeXScreen.RIDER_HOME
            }
        )

        SurgeXScreen.RIDER_SAFETY -> SafetyScreen(
            onBack = { currentScreen = SurgeXScreen.RIDER_HOME }
        )

        SurgeXScreen.DRIVER_SAFETY -> DriverSafetyScreen(
            onBack = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_HOME -> DriverHomeScreen(
            onOnlineChanged = {},
            onRideRequest = { currentScreen = SurgeXScreen.DRIVER_RIDE_REQUEST },
            onMenuClick = { currentScreen = SurgeXScreen.DRIVER_MENU },
            onSwitchToRider = {
                selectedRole = UserRole.RIDER
                preferences.edit().putString(LAST_MODE_KEY, UserRole.RIDER.name).apply()
                scope.launch { authController.saveActiveMode(UserRole.RIDER) }
                currentScreen = SurgeXScreen.RIDER_HOME
            },
            onSafetyClick = {
                currentScreen = SurgeXScreen.DRIVER_SAFETY
            },
            onDocumentsClick = {
                currentScreen = SurgeXScreen.DRIVER_DOCUMENTS
            },
            onProfileClick = {
                currentScreen = SurgeXScreen.DRIVER_PROFILE
            },
            onTripHistoryClick = {
                currentScreen = SurgeXScreen.DRIVER_TRIP_HISTORY
            },
            onEarningsClick = {
                currentScreen = SurgeXScreen.DRIVER_EARNINGS
            },
            onSettingsClick = {
                currentScreen = SurgeXScreen.DRIVER_SETTINGS
            },
            devModeEnabled = devMode,
            onDeveloperClick = { currentScreen = SurgeXScreen.DEVELOPER_MODE_SWITCH }
        )

        SurgeXScreen.DRIVER_MENU -> DriverMenuScreen(
            onDocumentsClick = {
                currentScreen = SurgeXScreen.DRIVER_DOCUMENTS
            },
            onTripHistoryClick = {
                currentScreen = SurgeXScreen.DRIVER_TRIP_HISTORY
            },
            onSettingsClick = {
                currentScreen = SurgeXScreen.DRIVER_SETTINGS
            },
            onProfileClick = {
                currentScreen = SurgeXScreen.DRIVER_PROFILE
            },
            onClose = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_PROFILE -> DriverProfileScreen(
            onBack = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_TRIP_HISTORY -> DriverTripHistoryScreen(
            onBack = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_EARNINGS -> DriverEarningsScreen(
            onBack = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_SETTINGS -> DriverSettingsScreen(
            onBack = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_RIDE_REQUEST -> DriverRideRequestScreen(
            onAccept = { currentScreen = SurgeXScreen.DRIVER_PICKUP },
            onDecline = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.DRIVER_PICKUP -> DriverPickupScreen(
            onArrived = { currentScreen = SurgeXScreen.PASSENGER_VERIFICATION },
            onCancel = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.PASSENGER_VERIFICATION -> PassengerVerificationScreen(
            onStartTrip = {
                tripController.startTrip()
                currentScreen = SurgeXScreen.LIVE_TRIP
            },
            onCancel = { currentScreen = SurgeXScreen.DRIVER_HOME }
        )

        SurgeXScreen.LIVE_TRIP -> LiveTripScreen(
            onEndTrip = {
                tripController.updateTripMetrics(distanceKm = 12.8, durationMinutes = 24)
                tripController.completeTrip(paymentMethod = PaymentMethod.CASH)
                currentScreen = SurgeXScreen.TRIP_SUMMARY
            },
            onSafety = {}
        )

        SurgeXScreen.TRIP_SUMMARY -> {
            val completedTrip = tripController.completedTrip
            if (completedTrip == null) {
                currentScreen = SurgeXScreen.DRIVER_HOME
            } else {
                TripSummaryScreen(
                    fare = completedTrip.fare,
                    distanceKm = completedTrip.ride.distanceKm,
                    durationMinutes = completedTrip.ride.durationMinutes,
                    onDone = { currentScreen = SurgeXScreen.RIDER_PAYMENT }
                )
            }
        }

        SurgeXScreen.RIDER_PAYMENT -> {
            val completedTrip = tripController.completedTrip
            if (completedTrip == null) {
                currentScreen = SurgeXScreen.RIDER_HOME
            } else {
                RiderPaymentScreen(
                    total = completedTrip.fare.total,
                    onPaymentSuccess = { currentScreen = SurgeXScreen.RECEIPT },
                    onCancel = { currentScreen = SurgeXScreen.TRIP_SUMMARY }
                )
            }
        }

        SurgeXScreen.RECEIPT -> {
            val completedTrip = tripController.completedTrip
            if (completedTrip == null) {
                currentScreen = SurgeXScreen.RIDER_HOME
            } else {
                ReceiptScreen(
                    ride = completedTrip.ride,
                    receipt = completedTrip.receipt,
                    fare = completedTrip.fare,
                    paymentMethod = completedTrip.ride.paymentMethod,
                    onDone = {
                        tripController.clear()
                        currentScreen = SurgeXScreen.RIDER_HOME
                    }
                )
            }
        }

        SurgeXScreen.TRIP_HISTORY -> TripHistoryScreen(
            onBack = { currentScreen = SurgeXScreen.RIDER_HOME }
        )

        SurgeXScreen.PAYMENT_METHODS -> PaymentMethodsScreen(
            preferences = preferences,
            onBack = { currentScreen = SurgeXScreen.RIDER_HOME }
        )

        SurgeXScreen.SETTINGS -> SettingsScreen(
            preferences = preferences,
            onBack = { currentScreen = SurgeXScreen.RIDER_HOME },
            onHelp = { currentScreen = SurgeXScreen.HELP },
            onReportIssue = { currentScreen = SurgeXScreen.REPORT_ISSUE }
        )

        SurgeXScreen.HELP -> HelpScreen(
            onBack = { currentScreen = SurgeXScreen.SETTINGS }
        )

        SurgeXScreen.REPORT_ISSUE -> ReportIssueScreen(
            onBack = { currentScreen = SurgeXScreen.SETTINGS }
        )

        SurgeXScreen.DRIVER_DOCUMENTS -> DriverDocumentsScreen(
            onBack = {
                if (devMode) {
                    selectedRole = UserRole.RIDER
                    preferences.edit().putString(LAST_MODE_KEY, UserRole.RIDER.name).apply()
                    currentScreen = SurgeXScreen.RIDER_HOME
                } else {
                    currentScreen = SurgeXScreen.DRIVER_HOME
                }
            },
            onSaved = { currentScreen = SurgeXScreen.DRIVER_HOME },
            onCarPhotosClick = { currentScreen = SurgeXScreen.CAR_PHOTOS_VERIFICATION }
        )

        SurgeXScreen.PROFILE_PIC_UPLOAD -> ProfilePicUploadScreen(
            isMandatory = false,
            onBack = {
                currentScreen = if (selectedRole == UserRole.RIDER) 
                    SurgeXScreen.RIDER_HOME else SurgeXScreen.DRIVER_HOME
            },
            onUploadSuccess = {
                profilePicUploaded = true
                preferences.edit().putBoolean(PROFILE_PIC_UPLOADED_KEY, true).apply()
                currentScreen = if (selectedRole == UserRole.RIDER) 
                    SurgeXScreen.RIDER_HOME else SurgeXScreen.DRIVER_HOME
            }
        )

        SurgeXScreen.CAR_PHOTOS_VERIFICATION -> CarPhotosVerificationScreen(
            onBack = { currentScreen = SurgeXScreen.DRIVER_DOCUMENTS },
            onVerificationSuccess = { currentScreen = SurgeXScreen.DRIVER_DOCUMENTS }
        )

        SurgeXScreen.DEVELOPER_MODE_SWITCH -> DeveloperModeSwitchScreen(
            devMode = devMode,
            onDevModeToggle = { enabled: Boolean ->
                devMode = enabled
                preferences.edit().putBoolean(DEV_MODE_KEY, enabled).apply()
            },
            onQuickSwitchRider = {
                selectedRole = UserRole.RIDER
                currentScreen = SurgeXScreen.RIDER_HOME
            },
            onQuickSwitchDriver = {
                selectedRole = UserRole.DRIVER
                currentScreen = SurgeXScreen.DRIVER_HOME
            },
            onRecodeClick = {
                currentScreen = SurgeXScreen.DEVELOPER_RECODE
            },
            onClose = {
                currentScreen = if (selectedRole == UserRole.RIDER)
                    SurgeXScreen.RIDER_HOME else SurgeXScreen.DRIVER_HOME
            }
        )

        SurgeXScreen.DEVELOPER_RECODE -> DeveloperRecodeScreen(
            onBack = { currentScreen = SurgeXScreen.DEVELOPER_MODE_SWITCH }
        )
    }
}
