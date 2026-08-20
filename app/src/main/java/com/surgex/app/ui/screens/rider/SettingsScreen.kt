package com.surgex.app.ui.screens.rider

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun SettingsScreen(
    preferences: SharedPreferences,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onReportIssue: () -> Unit
) {
    var lightMode by remember { mutableStateOf(preferences.getBoolean("light_mode", false)) }
    var navSounds by remember { mutableStateOf(preferences.getBoolean("nav_sounds", true)) }
    var voiceGuidance by remember { mutableStateOf(preferences.getBoolean("voice_guidance", true)) }
    var navigationEnabled by remember { mutableStateOf(preferences.getBoolean("navigation_enabled", true)) }

    Column(Modifier.fillMaxSize().background(SurgeBlack).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(20.dp, 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", color = SurgeWhite, fontSize = 24.sp, modifier = Modifier.clickable { onBack() }.padding(end = 16.dp))
            Text("Settings", color = SurgeWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(Modifier.height(8.dp))

        // Appearance
        Text("APPEARANCE", color = Color(0xFF666666), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(10.dp))
        Surface(Modifier.padding(horizontal = 20.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFF121212)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Light Mode", color = SurgeWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("Switch to white theme", color = Color(0xFF777777), fontSize = 12.sp)
                }
                Switch(checked = lightMode, onCheckedChange = {
                    lightMode = it
                    preferences.edit().putBoolean("light_mode", it).apply()
                }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF76FF03)))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Navigation & Sound
        Text("NAVIGATION & SOUND", color = Color(0xFF666666), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(10.dp))
        Surface(Modifier.padding(horizontal = 20.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFF121212)) {
            Column {
                SettingsToggleRow("Navigation Sounds", "Play turn-by-turn audio cues", navSounds) {
                    navSounds = it
                    preferences.edit().putBoolean("nav_sounds", it).apply()
                }
                SettingsToggleRow("Voice Guidance", "Spoken directions during trip", voiceGuidance) {
                    voiceGuidance = it
                    preferences.edit().putBoolean("voice_guidance", it).apply()
                }
                SettingsToggleRow("In-app Navigation", "Show map directions during ride", navigationEnabled) {
                    navigationEnabled = it
                    preferences.edit().putBoolean("navigation_enabled", it).apply()
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Support
        Text("SUPPORT", color = Color(0xFF666666), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(10.dp))
        Surface(Modifier.padding(horizontal = 20.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFF121212)) {
            Column {
                SettingsClickRow("Help & Support", "Message support (up to 48 hrs response)", onHelp)
                SettingsClickRow("Report an Issue", "App bugs, trip problems, safety (up to 48 hrs)", onReportIssue)
            }
        }

        Spacer(Modifier.height(40.dp))
        Text("SurgeX v1.0 • MOVE DIFFERENTLY", color = Color(0xFF333333), fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 32.dp))
    }
}

@Composable
private fun SettingsToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = SurgeWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color(0xFF777777), fontSize = 12.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF76FF03)))
    }
}

@Composable
private fun SettingsClickRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = SurgeWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color(0xFF777777), fontSize = 12.sp)
        }
        Text("›", color = Color(0xFF555555), fontSize = 20.sp)
    }
}
