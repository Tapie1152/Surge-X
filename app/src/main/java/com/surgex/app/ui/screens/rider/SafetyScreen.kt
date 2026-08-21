package com.surgex.app.ui.screens.rider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun SafetyScreen(
    onBack: () -> Unit
) {
    var emergencyContacts by remember { mutableStateOf(listOf(
        SafetyContact("Mom", "+1234567890"),
        SafetyContact("Dad", "+0987654321")
    )) }
    var sosActive by remember { mutableStateOf(false) }

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
                text = "Safety",
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
            // SOS Button
            item {
                Button(
                    onClick = { sosActive = !sosActive },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sosActive) Color.Red else Color(0xFF1A1A1A)
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "🆘",
                            fontSize = 32.sp
                        )
                        Text(
                            "SOS - Emergency Alert",
                            color = if (sosActive) SurgeWhite else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Share Trip Details
            item {
                SafetyOptionCard(
                    icon = "📍",
                    title = "Share Trip Details",
                    description = "Share your trip location with emergency contacts"
                )
            }

            // Record Trip
            item {
                SafetyOptionCard(
                    icon = "🎥",
                    title = "Record Trip",
                    description = "Automatically record trip audio for safety"
                )
            }

            // Emergency Contacts
            item {
                Text(
                    "Emergency Contacts",
                    color = SurgeWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(emergencyContacts.size) { index ->
                ContactCard(
                    contact = emergencyContacts[index],
                    onRemove = {
                        emergencyContacts = emergencyContacts.filterIndexed { i, _ -> i != index }
                    }
                )
            }

            // Add Contact Button
            item {
                Button(
                    onClick = {
                        emergencyContacts = emergencyContacts + SafetyContact("New Contact", "+1111111111")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        "+ Add Contact",
                        color = SurgeBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class SafetyContact(
    val name: String,
    val phone: String
)

@Composable
private fun SafetyOptionCard(
    icon: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { },
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

@Composable
private fun ContactCard(
    contact: SafetyContact,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    contact.name,
                    color = SurgeWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    contact.phone,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Text(
                "✕",
                color = Color.Red,
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onRemove() }
                    .padding(8.dp)
            )
        }
    }
}
