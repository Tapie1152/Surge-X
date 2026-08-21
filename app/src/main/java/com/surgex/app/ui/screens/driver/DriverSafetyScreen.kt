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
fun DriverSafetyScreen(
    onBack: () -> Unit
) {
    var emergencyName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
            .padding(horizontal = 20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
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
                text = "Safety Kit",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Set your emergency contact. This person will be notified if you feel unsafe during a trip.",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("EMERGENCY CONTACT", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emergencyName,
            onValueChange = { emergencyName = it },
            label = { Text("Contact Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SurgeWhite,
                unfocusedTextColor = SurgeWhite,
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color(0xFF333333),
                focusedLabelColor = Color(0xFF00E5FF),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF00E5FF)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = emergencyPhone,
            onValueChange = { emergencyPhone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SurgeWhite,
                unfocusedTextColor = SurgeWhite,
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color(0xFF333333),
                focusedLabelColor = Color(0xFF00E5FF),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF00E5FF)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (emergencyName.isNotBlank() && emergencyPhone.length >= 10) {
                    saved = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("SAVE EMERGENCY CONTACT", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        if (saved) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("✓ Emergency contact saved", color = Color(0xFF00C853), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Info cards
        SafetyInfoCard(
            title = "Do you feel safe?",
            description = "When you accept a ride, the app will ask if you feel safe. If you say No, your location will be shared with SurgeX monitoring and your emergency contact."
        )

        Spacer(modifier = Modifier.height(14.dp))

        SafetyInfoCard(
            title = "Record Trip",
            description = "Coming soon: Ability to record audio during a trip for your safety."
        )
    }
}

@Composable
private fun SafetyInfoCard(title: String, description: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF121212),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = SurgeWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
