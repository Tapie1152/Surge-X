package com.surgex.app.ui.screens.driver

import android.content.Context
import android.media.RingtoneManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.components.SurgeMap
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeGrey
import com.surgex.app.ui.theme.SurgeSurface
import com.surgex.app.ui.theme.SurgeWhite

private fun playNotificationSound(context: Context) {
    try {
        val notification = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )
        val ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone?.play()
    } catch (e: Exception) {
        // ignore if sound fails
    }
}

@Composable
fun DriverHomeScreen(
    onOnlineChanged: (Boolean) -> Unit = {},
    onRideRequest: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSwitchToRider: () -> Unit = {},
    onDocumentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSafetyClick: () -> Unit = {},
    onTripHistoryClick: () -> Unit = {},
    onEarningsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var isOnline by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var rideRequest by remember { mutableStateOf<RideRequestData?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        // Real Map
        SurgeMap(
            startLatitude = -33.9249,
            startLongitude = 18.4241,
            zoomLevel = 14.0,
            showMyLocation = true
        )

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clickable Menu Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable { menuOpen = true },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(3) {
                        Box(modifier = Modifier.width(18.dp).height(2.dp).background(SurgeWhite, RoundedCornerShape(1.dp)))
                    }
                }
            }

            Text(
                text = "SurgeX Driver",
                color = SurgeWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // Quick Switch to Rider mode
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .clickable { onSwitchToRider() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 18.sp)
            }
        }

        // Bottom Sheet
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = Color(0xFF0A0A0A)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp)
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF2A2A2A))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Online / Offline Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurgeSurface)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isOnline) "You are Online" else "You are Offline",
                                color = SurgeWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isOnline) "Ready to receive rides" else "Go online to start earning",
                                color = SurgeGrey,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = isOnline,
                            onCheckedChange = { newValue ->
                                isOnline = newValue
                                onOnlineChanged(newValue)
                                if (newValue) {
                                    playNotificationSound(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00C853),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFF333333)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quick Actions
                    Text(
                        text = "Quick Actions",
                        color = SurgeGrey,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionButton(
                            title = "Documents",
                            emoji = "📄",
                            modifier = Modifier.weight(1f)
                        ) {
                            onDocumentsClick()
                        }

                        ActionButton(
                            title = "Earnings",
                            emoji = "💰",
                            modifier = Modifier.weight(1f)
                        ) {
                            onEarningsClick()
                        }

                        ActionButton(
                            title = "Support",
                            emoji = "🆘",
                            modifier = Modifier.weight(1f)
                        ) {}
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Simulate Ride Request (for testing)
                    if (isOnline) {
                        Button(
                            onClick = {
                                // Simulate a ride request
                                rideRequest = RideRequestData(
                                    riderName = "John Doe",
                                    pickupAddress = "Cape Town CBD",
                                    destinationAddress = "V&A Waterfront",
                                    estimatedFare = 85.50,
                                    distance = 2.3
                                )
                                onRideRequest()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00C853),
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "SIMULATE RIDE REQUEST",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Side Menu Overlay
        AnimatedVisibility(
            visible = menuOpen,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { menuOpen = false }
            )
        }

        // Side Menu
        AnimatedVisibility(
            visible = menuOpen,
            enter = slideInHorizontally(tween(300)) { -it },
            exit = slideOutHorizontally(tween(300)) { -it }
        ) {
            DriverSideMenu(
                onClose = { menuOpen = false },
                onDocuments = {
                    menuOpen = false
                    onDocumentsClick()
                },
                onProfile = {
                    menuOpen = false
                    onProfileClick()
                },
                onTripHistory = {
                    menuOpen = false
                    onTripHistoryClick()
                },
                onSafety = {
                    menuOpen = false
                    onSafetyClick()
                },
                onSettings = {
                    menuOpen = false
                    onSettingsClick()
                },
                onEarnings = {
                    menuOpen = false
                    onEarningsClick()
                }
            )
        }
    }
}

@Composable
private fun DriverSideMenu(
    onClose: () -> Unit,
    onDocuments: () -> Unit,
    onProfile: () -> Unit,
    onTripHistory: () -> Unit,
    onSafety: () -> Unit,
    onSettings: () -> Unit,
    onEarnings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color(0xFF0A0A0A))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "SurgeX", color = SurgeWhite, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "DRIVER MODE", color = Color(0xFF76FF03), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(40.dp))
            Divider(color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(24.dp))

            MenuItemRow(icon = "👤", label = "Profile", onClick = onProfile)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "📄", label = "Documents", onClick = onDocuments)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "🧾", label = "Trip History", onClick = onTripHistory)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "💰", label = "Earnings", onClick = onEarnings)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "🛡️", label = "Safety", onClick = onSafety)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "⚙️", label = "Settings", onClick = onSettings)
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "SURGEX • EARN DIFFERENTLY", color = Color(0xFF1E1E1E), fontSize = 9.sp, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun MenuItemRow(icon: String, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = SurgeWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ActionButton(
    title: String,
    emoji: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurgeSurface)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = SurgeWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

data class RideRequestData(
    val riderName: String,
    val pickupAddress: String,
    val destinationAddress: String,
    val estimatedFare: Double,
    val distance: Double
)
