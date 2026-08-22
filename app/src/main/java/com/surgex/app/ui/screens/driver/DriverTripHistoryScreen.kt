package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class TripItem(
    val id: String,
    val passengerName: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val fare: String,
    val duration: String,
    val distance: String,
    val date: String
)

@Composable
fun DriverTripHistoryScreen(
    onBack: () -> Unit
) {
    // Mock data - replace with real data from backend
    val trips = listOf(
        TripItem("1", "John Doe", "Main St", "Park Ave", "R45.00", "15 min", "2.5 km", "Aug 21"),
        TripItem("2", "Jane Smith", "Mall Center", "Hospital", "R32.50", "10 min", "1.8 km", "Aug 21"),
        TripItem("3", "Mike Johnson", "Station", "Downtown", "R58.75", "22 min", "4.2 km", "Aug 20")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = { Text("Trip History", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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

        // Trips List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trips) { trip ->
                TripHistoryCard(trip)
            }
        }
    }
}

@Composable
fun TripHistoryCard(trip: TripItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                Column {
                    Text(trip.passengerName, fontWeight = FontWeight.SemiBold)
                    Text(trip.date, fontSize = 12.sp, color = Color.Gray)
                }
                Text(trip.fare, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4CAF50))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("${trip.pickupLocation} → ${trip.dropoffLocation}", fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${trip.distance}", fontSize = 12.sp, color = Color.Gray)
                Text(trip.duration, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
