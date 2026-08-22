package com.surgex.app.ui.screens.rider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Ride selection screen with dynamic pricing display
 */
@Composable
fun RideSelectionScreen(
    onBack: () -> Unit,
    onConfirmRide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = { Text("Choose Ride", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        // Ride Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Regular Ride
            RideOptionCard(
                title = "SurgeX",
                description = "Affordable ride for everyday trips",
                fare = "R45.50",
                eta = "5 min away",
                surge = 1.0,
                isSelected = true,
                onClick = onConfirmRide
            )

            // Premium Ride
            RideOptionCard(
                title = "SurgeX Premium",
                description = "Premium comfort and professional drivers",
                fare = "R78.75",
                eta = "8 min away",
                surge = 1.2,
                isSelected = false,
                onClick = {}
            )

            // Pool Ride
            RideOptionCard(
                title = "SurgeX Pool",
                description = "Share ride, split fare",
                fare = "R28.25",
                eta = "12 min away",
                surge = 1.0,
                isSelected = false,
                onClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))

            // Terms and Confirm
            Text(
                "By confirming, you agree to our Terms of Service",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = onConfirmRide,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Confirm Ride", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RideOptionCard(
    title: String,
    description: String,
    fare: String,
    eta: String,
    surge: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(description, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(eta, fontSize = 12.sp, color = Color(0xFF2196F3))
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(fare, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (surge > 1.0) {
                        Text(
                            "Surge ${String.format("%.1f", surge)}x",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6F00)
                        )
                    }
                }
            }
        }
    }
}
