package com.surgex.app.ui.screens.rider

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
fun RiderMenuScreen(
    onTripHistoryClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onSafetyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfilePicClick: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack.copy(alpha = 0.95f))
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Menu",
                color = SurgeWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "✕",
                color = SurgeWhite,
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable { onClose() }
                    .padding(8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MenuItemButton(
                    icon = "📸",
                    title = "Profile Picture",
                    description = "Update your profile photo",
                    onClick = onProfilePicClick
                )
            }

            item {
                MenuItemButton(
                    icon = "📋",
                    title = "Trip History",
                    description = "View your past trips",
                    onClick = onTripHistoryClick
                )
            }

            item {
                MenuItemButton(
                    icon = "💳",
                    title = "Payment Methods",
                    description = "Manage payment options",
                    onClick = onPaymentMethodsClick
                )
            }

            item {
                MenuItemButton(
                    icon = "🛡️",
                    title = "Safety",
                    description = "Safety features and SOS",
                    onClick = onSafetyClick
                )
            }

            item {
                MenuItemButton(
                    icon = "⚙️",
                    title = "Settings",
                    description = "App and account settings",
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
private fun MenuItemButton(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 28.sp)
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
            Text("›", color = Color.Gray, fontSize = 20.sp)
        }
    }
}
