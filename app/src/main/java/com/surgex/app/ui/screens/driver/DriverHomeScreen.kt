package com.surgex.app.ui.screens.driver

import androidx.compose.animation.*
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
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite

@Composable
fun DriverHomeScreen(
    onOnlineChanged: (Boolean) -> Unit = {},
    onRideRequest: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSwitchToRider: () -> Unit = {},
    onSafetyClick: () -> Unit = {},
    onDocumentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTripHistoryClick: () -> Unit = {},
    onEarningsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    devModeEnabled: Boolean = false,
    onDeveloperClick: () -> Unit = {}
) {
    var isOnline by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var showRideRequest by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        // Background
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Status Display
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        if (isOnline) Color(0xFF76FF03)
                        else Color(0xFF1A1A1A)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isOnline) "🟢" else "🔴",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                if (isOnline) "Online" else "Offline",
                color = SurgeWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Switch(
                checked = isOnline,
                onCheckedChange = {
                    isOnline = it
                    onOnlineChanged(it)
                },
                modifier = Modifier.scale(1.5f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF76FF03)
                )
            )
        }

        // Top Bar with Hamburger and Developer
        TopBar(
            onMenuClick = { menuOpen = true },
            onSwitchToRider = onSwitchToRider,
            devModeEnabled = devModeEnabled,
            onDeveloperClick = onDeveloperClick
        )

        // Side menu
        AnimatedVisibility(
            visible = menuOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { menuOpen = false }
            )
            
            DriverMenuScreen(
                onDocumentsClick = {
                    menuOpen = false
                    onDocumentsClick()
                },
                onTripHistoryClick = {
                    menuOpen = false
                    onTripHistoryClick()
                },
                onSettingsClick = {
                    menuOpen = false
                    onSettingsClick()
                },
                onProfileClick = {
                    menuOpen = false
                    onProfileClick()
                },
                onClose = { menuOpen = false }
            )
        }

        // Ride Request Popup
        if (showRideRequest) {
            RideRequestPopup(
                onAccept = {
                    showRideRequest = false
                    onRideRequest()
                },
                onDecline = {
                    showRideRequest = false
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    onMenuClick: () -> Unit,
    onSwitchToRider: () -> Unit,
    devModeEnabled: Boolean = false,
    onDeveloperClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger Menu
        CircleButton(
            text = "≡",
            onClick = onMenuClick
        )
        
        Text(
            text = "Driver",
            color = SurgeWhite,
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )
        
        // Developer or Switch Button
        if (devModeEnabled) {
            CircleButton(
                text = "🔨",
                onClick = onDeveloperClick
            )
        } else {
            CircleButton(
                text = "🚵",
                onClick = onSwitchToRider
            )
        }
    }
}

@Composable
private fun CircleButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = SurgeWhite, fontSize = 18.sp)
    }
}

@Composable
private fun RideRequestPopup(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = SurgeBlack
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "New Ride Request",
                    color = SurgeWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "John Doe is requesting a ride",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDecline,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Text("Decline", color = SurgeWhite)
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Accept", color = SurgeBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun Modifier.scale(factor: Float): Modifier {
    return this.size(48.dp * factor)
}
