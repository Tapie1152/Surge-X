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

@Composable
fun CarPhotosVerificationScreen(
    onBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    var verificationComplete by remember { mutableStateOf(false) }
    val carPhotoTypes = listOf(
        "Front View" to false,
        "Rear View" to false,
        "Driver Side" to false,
        "Passenger Side" to false,
        "Interior" to false,
        "License Plate" to false
    )
    var uploadedPhotos by remember { mutableStateOf(setOf<String>()) }

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
                text = "Car Photos Verification",
                color = SurgeWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            item {
                Text(
                    "Upload clear photos of your vehicle",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(carPhotoTypes.size) { index ->
                val (photoType, isUploaded) = carPhotoTypes[index]
                val isPhotoUploaded = uploadedPhotos.contains(photoType)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable { uploadedPhotos = uploadedPhotos + photoType }
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPhotoUploaded) Color(0xFF1A3A1A) else Color(0xFF1A1A1A)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    color = if (isPhotoUploaded) Color(0xFF0A2A0A) else Color(0xFF0A0A0A),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isPhotoUploaded) "✓" else "📸",
                                fontSize = if (isPhotoUploaded) 24.sp else 32.sp,
                                color = if (isPhotoUploaded) Color(0xFF76FF03) else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                photoType,
                                color = SurgeWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (isPhotoUploaded) "✓ Uploaded" else "Tap to upload",
                                color = if (isPhotoUploaded) Color(0xFF76FF03) else Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        if (isPhotoUploaded) {
                            Text("✓", color = Color(0xFF76FF03), fontSize = 20.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                if (uploadedPhotos.size == carPhotoTypes.size && !verificationComplete) {
                    Button(
                        onClick = {
                            verificationComplete = true
                            onVerificationSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            "VERIFY ALL PHOTOS",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else if (verificationComplete) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0A1A0A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "✓ Verification Complete",
                                color = Color(0xFF76FF03),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Your vehicle photos have been verified",
                                color = Color(0xFF4A7A00),
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF333333),
                            disabledContainerColor = Color(0xFF333333)
                        ),
                        enabled = false
                    ) {
                        Text(
                            "UPLOAD ALL PHOTOS (${uploadedPhotos.size}/${carPhotoTypes.size})",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
