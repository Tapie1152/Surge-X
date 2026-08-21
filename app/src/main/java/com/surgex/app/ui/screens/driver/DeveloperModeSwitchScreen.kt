package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun DeveloperModeSwitchScreen(
    devMode: Boolean,
    onDevModeToggle: (Boolean) -> Unit,
    onQuickSwitchRider: () -> Unit,
    onQuickSwitchDriver: () -> Unit,
    onClose: () -> Unit
) {
    var isDevModeEnabled by remember { mutableStateOf(devMode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Developer Mode",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "✕",
                color = SurgeWhite,
                fontSize = 28.sp,
                modifier = Modifier.clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Dev Mode Toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1A1A1A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Enable Developer Mode",
                        color = SurgeWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Access testing tools & features",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = isDevModeEnabled,
                    onCheckedChange = {
                        isDevModeEnabled = it
                        onDevModeToggle(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF76FF03),
                        checkedTrackColor = Color(0xFF0A1A0A),
                        uncheckedThumbColor = Color(0xFF333333),
                        uncheckedTrackColor = Color(0xFF1A1A1A)
                    )
                )
            }
        }

        if (isDevModeEnabled) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "QUICK NAVIGATION",
                color = Color(0xFF76FF03),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Switch Rider
            Button(
                onClick = onQuickSwitchRider,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A3A3A)
                )
            ) {
                Text(
                    "🚗 Switch to Rider Mode",
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Switch Driver
            Button(
                onClick = onQuickSwitchDriver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A3A)
                )
            ) {
                Text(
                    "🚙 Switch to Driver Mode",
                    color = Color(0xFF76FF03),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "DEBUG INFO",
                color = Color(0xFF76FF03),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0A0A0A)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DebugInfoRow("App Version", "1.0.0")
                    DebugInfoRow("Build Number", "debug")
                    DebugInfoRow("Device", "Android")
                    DebugInfoRow("Dev Mode", if (isDevModeEnabled) "ON" else "OFF")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Close Button
        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                "CLOSE",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun DebugInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            value,
            color = Color(0xFF76FF03),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
