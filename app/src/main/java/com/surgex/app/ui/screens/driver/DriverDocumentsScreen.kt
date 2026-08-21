package com.surgex.app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

data class Document(
    val id: String,
    val name: String,
    val type: String,
    val expiryDate: String,
    val isVerified: Boolean = false
)

@Composable
fun DriverDocumentsScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onCarPhotosClick: () -> Unit = {}
) {
    var documents by remember {
        mutableStateOf(listOf(
            Document("1", "Driver License", "License", "2026-12-31", true),
            Document("2", "Vehicle Registration", "Registration", "2027-06-30", true),
            Document("3", "Insurance Certificate", "Insurance", "2025-12-31", false),
            Document("4", "Background Check", "Background", "2026-08-21", true)
        ))
    }

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
                text = "Documents",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Required Documents",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(documents.size) { index ->
                DocumentCard(
                    document = documents[index],
                    onUpdate = {
                        documents = documents.toMutableList().apply {
                            this[index] = this[index].copy(isVerified = true)
                        }
                    }
                )
            }

            // Car Photos Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Additional",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                CarPhotosButton(onCarPhotosClick = onCarPhotosClick)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaved,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                "Continue",
                color = SurgeBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DocumentCard(
    document: Document,
    onUpdate: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
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
                    document.name,
                    color = SurgeWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Expires: ${document.expiryDate}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            if (document.isVerified) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "✓",
                        color = Color(0xFF76FF03),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Verified",
                        color = Color(0xFF76FF03),
                        fontSize = 10.sp
                    )
                }
            } else {
                Button(
                    onClick = onUpdate,
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                ) {
                    Text(
                        "Upload",
                        color = SurgeBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CarPhotosButton(
    onCarPhotosClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onCarPhotosClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Car Photos Verification",
                    color = SurgeWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Upload front, back, sides, and interior photos",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Text("›", color = Color.Gray, fontSize = 20.sp)
        }
    }
}
