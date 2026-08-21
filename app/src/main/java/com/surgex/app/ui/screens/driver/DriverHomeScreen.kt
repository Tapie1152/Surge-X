package com.surgex.app.ui.screens.driver

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.components.SurgeMap
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeGrey
import com.surgex.app.ui.theme.SurgeSurface
import com.surgex.app.ui.theme.SurgeWhite

@Composable
fun DriverHomeScreen(
    onOnlineChanged: (Boolean) -> Unit = {},
    onRideRequest: () -> Unit = {},
    onSwitchToRider: () -> Unit = {},
    onDocumentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSafetyClick: () -> Unit = {}
) {
    var isOnline by remember { mutableStateOf(false) }

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

            // Menu
            CircleButton(text = "☰")

            Text(
                text = "SurgeX Driver",
                color = SurgeWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // Switch back to Rider mode
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

                                // Play sound when going online
                                if (newValue) {
                                    try {
                                        val context = androidx.compose.ui.platform.LocalContext.current
                                        val notification = android.media.RingtoneManager.getDefaultUri(
                                            android.media.RingtoneManager.TYPE_NOTIFICATION
                                        )
                                        val ringtone = android.media.RingtoneManager.getRingtone(context, notification)
                                        ringtone?.play()
                                    } catch (e: Exception) {
                                        // ignore if sound fails
                                    }
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
                        ) {}

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
                            onClick = onRideRequest,
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
    }
}

@Composable
private fun CircleButton(text: String) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = SurgeWhite, fontSize = 18.sp)
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
