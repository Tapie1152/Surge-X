package com.surgex.app.ui.screens.rider

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
fun HelpScreen(onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(SurgeBlack).padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", color = SurgeWhite, fontSize = 24.sp, modifier = Modifier.clickable { onBack() }.padding(end = 16.dp))
            Text("Help & Support", color = SurgeWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(12.dp))
        Text("Send us a message. Our team will respond within 48 hours.", color = Color(0xFF888888), fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        if (sent) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF0A1A0A), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("✓ Message sent", color = Color(0xFF76FF03), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("We received your message. Expect a reply within 48 hours.", color = Color(0xFF4A7A00), fontSize = 14.sp)
                }
            }
        } else {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                placeholder = { Text("Describe your issue or question…", color = Color(0xFF555555)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SurgeWhite, unfocusedTextColor = SurgeWhite,
                    focusedBorderColor = Color(0xFF76FF03), unfocusedBorderColor = Color(0xFF333333),
                    cursorColor = Color(0xFF76FF03), focusedContainerColor = Color(0xFF121212), unfocusedContainerColor = Color(0xFF121212)
                ),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { if (message.isNotBlank()) sent = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = message.isNotBlank()
            ) {
                Text("SEND MESSAGE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
