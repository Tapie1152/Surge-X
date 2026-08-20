package com.surgex.app.ui.screens.rider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite

@Composable
fun SafetyScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(SurgeBlack).padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", color = SurgeWhite, fontSize = 24.sp, modifier = Modifier.clickable { onBack() }.padding(end = 16.dp))
            Text("Safety", color = SurgeWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(16.dp))

        listOf(
            "🚨" to ("Emergency SOS" to "Share live location and call for help"),
            "📍" to ("Share Trip" to "Send real-time trip status to family or friends"),
            "🛡️" to ("Verified Drivers" to "All drivers go through ID, license and background checks"),
            "📞" to ("24/7 Support Line" to "Call SurgeX safety team any time during a trip")
        ).forEach { (icon, pair) ->
            Surface(Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFF121212)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 28.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(pair.first, color = SurgeWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(pair.second, color = Color(0xFF888888), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
