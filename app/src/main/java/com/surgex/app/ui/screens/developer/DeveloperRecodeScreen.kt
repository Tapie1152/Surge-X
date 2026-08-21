package com.surgex.app.ui.screens.developer

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
import com.surgex.app.ui.theme.SurgeWhite

data class CodeModule(
    val name: String,
    val description: String,
    val status: String, // "Working", "In Progress", "Needs Fix"
    val progress: Float = 1f
)

@Composable
fun DeveloperRecodeScreen(
    onBack: () -> Unit
) {
    val codeModules = listOf(
        CodeModule("Profile Picture Upload", "Camera & Gallery integration with permissions", "Working", 1f),
        CodeModule("Rider Safety", "SOS button, emergency contacts, trip recording", "Working", 1f),
        CodeModule("Driver Menu", "Hamburger menu with Documents, Trip History, Settings", "Working", 1f),
        CodeModule("Rider Menu", "Full menu navigation with all rider options", "Working", 1f),
        CodeModule("Driver Settings", "Toggle options for online status, notifications, dark mode", "Working", 1f),
        CodeModule("Car Photos Verification", "Multi-photo upload for vehicle verification", "Working", 1f),
        CodeModule("State Persistence", "Save last screen and restore on app restart", "Working", 1f),
        CodeModule("Mandatory Profile Pic", "Require profile picture on first login", "Working", 1f),
        CodeModule("Developer Mode Toggle", "Quick role switching without verification", "Working", 1f),
        CodeModule("Dynamic Pricing", "Real price calculation based on distance/time", "In Progress", 0.7f),
        CodeModule("Rider-Driver Connection", "Live communication between rider and driver", "In Progress", 0.6f),
        CodeModule("Payment Integration", "Multiple payment methods and processing", "Needs Fix", 0.4f)
    )

    var expandedModule by remember { mutableStateOf<String?>(null) }

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
                text = "Code Modules",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Developer Mode - Code Status",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(codeModules) { module ->
                CodeModuleCard(
                    module = module,
                    isExpanded = expandedModule == module.name,
                    onToggle = {
                        expandedModule = if (expandedModule == module.name) null else module.name
                    }
                )
            }
        }
    }
}

@Composable
private fun CodeModuleCard(
    module: CodeModule,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val statusColor = when (module.status) {
        "Working" -> Color(0xFF76FF03)
        "In Progress" -> Color(0xFFFFB300)
        "Needs Fix" -> Color(0xFFFF6B6B)
        else -> Color.Gray
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        module.name,
                        color = SurgeWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        module.description,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        module.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = module.progress,
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp),
                        color = statusColor,
                        trackColor = Color(0xFF333333)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFF333333), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F0F0F)
                ) {
                    Text(
                        when (module.status) {
                            "Working" -> "✓ This module is fully functional and tested."
                            "In Progress" -> "⚠ This module is under development and may have incomplete features."
                            "Needs Fix" -> "✗ This module needs fixes and improvements."
                            else -> "No additional information"
                        },
                        color = statusColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
