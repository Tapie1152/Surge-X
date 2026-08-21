package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun DriverProfileScreen(
    onBack: () -> Unit
) {
    // These will later come from Firebase / real data
    val driverName = "Tapie"
    val rating = 5.0
    val totalTrips = 0
    val feedbackTags = listOf(
        "Nice Car" to 0,
        "Clean Car" to 0,
        "Good Music" to 0,
        "Best Driver" to 0,
        "Polite" to 0,
        "Safe Driver" to 0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        // Top Bar
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
                text = "Driver Profile",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text("📷", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Tap to add photo", color = Color.Gray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(driverName, color = SurgeWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("★", color = Color(0xFFFFD700), fontSize = 22.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format("%.1f", rating),
                    color = SurgeWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("($totalTrips trips)", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Feedback Tags
            Text(
                "RIDER FEEDBACK",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(feedbackTags) { (tag, count) ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1A1A1A)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tag, color = SurgeWhite, fontSize = 13.sp)
                            if (count > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("$count", color = Color(0xFF76FF03), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Complaints / Messages section
            Text(
                "RIDER MESSAGES & COMPLAINTS",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF121212)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "No messages yet",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Rider feedback and complaints will appear here after trips.",
                        color = Color(0xFF666666),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
