package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverEarningsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = { Text("Earnings", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Total Earnings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Earnings Today", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("R450.25", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Earnings Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EarningsSummaryCard("Trips", "12", Color(0xFF4CAF50))
                EarningsSummaryCard("Distance", "45.2 km", Color(0xFFFF9800))
                EarningsSummaryCard("Time", "4h 30m", Color(0xFF9C27B0))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Breakdown
            Text("Earnings Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            EarningsBreakdownItem("Fares", "R480.50")
            EarningsBreakdownItem("Bonuses", "R50.00")
            EarningsBreakdownItem("Commissions", "-R80.25")
        }
    }
}

@Composable
fun EarningsSummaryCard(label: String, value: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .weight(1f, fill = true)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun EarningsBreakdownItem(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp)
        Text(amount, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
