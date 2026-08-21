package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.surgex.app.ui.theme.SurgeGrey
import com.surgex.app.ui.theme.SurgeSurface
import com.surgex.app.ui.theme.SurgeWhite

data class TripsHistoryItem(
    val id: String,
    val riderName: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val distance: Double,
    val duration: Int,
    val fare: Double,
    val date: String,
    val time: String
)

@Composable
fun DriverTripHistoryScreen(
    onBack: () -> Unit
) {
    // Sample trip data
    val trips = listOf(
        TripsHistoryItem(
            id = "1",
            riderName = "John Doe",
            pickupLocation = "Cape Town CBD",
            dropoffLocation = "V&A Waterfront",
            distance = 2.3,
            duration = 8,
            fare = 85.50,
            date = "Aug 20, 2026",
            time = "14:30"
        ),
        TripsHistoryItem(
            id = "2",
            riderName = "Jane Smith",
            pickupLocation = "Camps Bay",
            dropoffLocation = "Clifton",
            distance = 1.8,
            duration = 6,
            fare = 65.00,
            date = "Aug 20, 2026",
            time = "12:15"
        ),
        TripsHistoryItem(
            id = "3",
            riderName = "Mike Johnson",
            pickupLocation = "Sea Point",
            dropoffLocation = "Green Point",
            distance = 3.2,
            duration = 12,
            fare = 120.75,
            date = "Aug 19, 2026",
            time = "18:45"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
                text = "Trip History",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Trip List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trips) { trip ->
                TripHistoryCard(trip = trip)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TripHistoryCard(trip: TripsHistoryItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurgeSurface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.riderName,
                    color = SurgeWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "R${String.format("%.2f", trip.fare)}",
                    color = Color(0xFF76FF03),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.pickupLocation,
                        color = SurgeGrey,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trip.dropoffLocation,
                        color = SurgeGrey,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${trip.distance} km • ${trip.duration} min",
                    color = SurgeGrey,
                    fontSize = 11.sp
                )
                Text(
                    text = "${trip.date} • ${trip.time}",
                    color = SurgeGrey,
                    fontSize = 11.sp
                )
            }
        }
    }
}
