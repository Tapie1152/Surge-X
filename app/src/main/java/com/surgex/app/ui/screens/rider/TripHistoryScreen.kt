package com.surgex.app.ui.screens.rider

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite

data class TripHistoryItem(
    val id: String,
    val date: String,
    val time: String,
    val pickup: String,
    val destination: String,
    val carType: String,
    val fare: String,
    val paymentMethod: String,
    val commission: String,
    val driverName: String,
    val distance: String,
    val duration: String
)

@Composable
fun TripHistoryScreen(onBack: () -> Unit) {
    val trips = remember {
        listOf(
            TripHistoryItem("TX-10482", "20 Aug 2026", "14:32", "Windermere, Cape Town", "Cape Town CBD", "SurgeX Sedan • Toyota Corolla", "R92.00", "Cash", "R13.80 (15%)", "Thabo M.", "12.8 km", "24 min"),
            TripHistoryItem("TX-10471", "19 Aug 2026", "09:15", "Milnerton", "Century City", "SurgeX Comfort • VW Polo", "R68.50", "Card", "R10.28 (15%)", "Lerato K.", "8.4 km", "18 min"),
            TripHistoryItem("TX-10455", "18 Aug 2026", "21:40", "Sea Point", "Camps Bay", "SurgeX Priority • BMW 3 Series", "R145.00", "Cash", "R0.00 (R500 plan)", "Johan S.", "6.2 km", "15 min")
        )
    }
    var selectedTrip by remember { mutableStateOf<TripHistoryItem?>(null) }

    Box(Modifier.fillMaxSize().background(SurgeBlack)) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(20.dp, 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("←", color = SurgeWhite, fontSize = 24.sp, modifier = Modifier.clickable { onBack() }.padding(end = 16.dp))
                Text("Trip History", color = SurgeWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
                items(trips) { trip ->
                    Surface(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { selectedTrip = trip }, color = Color(0xFF121212), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${trip.date} • ${trip.time}", color = Color(0xFF888888), fontSize = 12.sp)
                                Text(trip.fare, color = SurgeWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(trip.pickup, color = SurgeWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("→ ${trip.destination}", color = Color(0xFFAAAAAA), fontSize = 13.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("${trip.carType}  •  ${trip.paymentMethod}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        selectedTrip?.let { trip ->
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.7f)).clickable { selectedTrip = null }, contentAlignment = Alignment.BottomCenter) {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), color = Color(0xFF0E0E0E)) {
                    Column(Modifier.padding(24.dp)) {
                        Text("Trip Receipt", color = SurgeWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("ID: ${trip.id}", color = Color(0xFF666666), fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        listOf("From" to trip.pickup, "To" to trip.destination, "Vehicle" to trip.carType, "Driver" to trip.driverName, "Distance" to trip.distance, "Payment" to trip.paymentMethod, "Commission" to trip.commission).forEach { (l, v) ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(l, color = Color(0xFF777777), fontSize = 13.sp)
                                Text(v, color = SurgeWhite, fontSize = 13.sp)
                            }
                        }
                        Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFF222222))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Fare", color = Color(0xFF777777), fontSize = 14.sp)
                            Text(trip.fare, color = Color(0xFF76FF03), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = {}, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                            Text("DOWNLOAD RECEIPT", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { selectedTrip = null }, Modifier.fillMaxWidth()) { Text("Close", color = Color(0xFF888888)) }
                    }
                }
            }
        }
    }
}
