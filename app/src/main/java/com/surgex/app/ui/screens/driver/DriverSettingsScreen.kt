package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite

@Composable
fun DriverSettingsScreen(
    onBack: () -> Unit
) {
    var onlineStatus by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
            .padding(20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = SurgeWhite,
                fontSize = 28.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 16.dp)
            )
            Text(
                text = "Settings",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Account",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            item {
                SettingItem(
                    icon = "🟢",
                    title = "Online Status",
                    description = "Accept new ride requests",
                    isToggle = true,
                    toggleValue = onlineStatus,
                    onToggle = { onlineStatus = it }
                )
            }

            item {
                Text(
                    "Preferences",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            item {
                SettingItem(
                    icon = "🔔",
                    title = "Notifications",
                    description = "Push notifications for ride requests",
                    isToggle = true,
                    toggleValue = notificationsEnabled,
                    onToggle = { notificationsEnabled = it }
                )
            }

            item {
                SettingItem(
                    icon = "🌙",
                    title = "Dark Mode",
                    description = "Dark mode enabled",
                    isToggle = true,
                    toggleValue = darkModeEnabled,
                    onToggle = { darkModeEnabled = it }
                )
            }

            item {
                Text(
                    "Other",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            item {
                SettingItem(
                    icon = "ℹ️",
                    title = "About",
                    description = "App version 1.0.0"
                )
            }

            item {
                SettingItem(
                    icon = "⚖️",
                    title = "Terms & Conditions",
                    description = "Review our policies"
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: String,
    title: String,
    description: String,
    isToggle: Boolean = false,
    toggleValue: Boolean = false,
    onToggle: (Boolean) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { if (isToggle) onToggle(!toggleValue) },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(icon, fontSize = 24.sp)
                Column {
                    Text(
                        title,
                        color = SurgeWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        description,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            if (isToggle) {
                Switch(
                    checked = toggleValue,
                    onCheckedChange = { onToggle(it) },
                    modifier = Modifier.scale(0.8f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF76FF03)
                    )
                )
            } else {
                Text("›", color = Color.Gray, fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun Modifier.scale(factor: Float): Modifier {
    return this.size(24.dp * factor)
}
