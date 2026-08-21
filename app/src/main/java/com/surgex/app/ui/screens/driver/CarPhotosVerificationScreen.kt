package com.surgex.app.ui.screens.driver

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

data class CarPhoto(
    val id: String,
    val type: String, // "front", "back", "left", "right", "interior"
    val uri: Uri? = null,
    val isVerified: Boolean = false
)

@Composable
fun CarPhotosVerificationScreen(
    onBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val context = LocalContext.current
    var carPhotos by remember {
        mutableStateOf(listOf(
            CarPhoto("1", "Front"),
            CarPhoto("2", "Back"),
            CarPhoto("3", "Left Side"),
            CarPhoto("4", "Right Side"),
            CarPhoto("5", "Interior")
        ))
    }
    var uploadingPhotoId by remember { mutableStateOf<String?>(null) }
    var allPhotosVerified by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null && uploadingPhotoId != null) {
            carPhotos = carPhotos.map { photo ->
                if (photo.id == uploadingPhotoId) {
                    photo.copy(isVerified = true)
                } else photo
            }
            uploadingPhotoId = null
            allPhotosVerified = carPhotos.all { it.isVerified }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && uploadingPhotoId != null) {
            carPhotos = carPhotos.map { photo ->
                if (photo.id == uploadingPhotoId) {
                    photo.copy(uri = uri, isVerified = true)
                } else photo
            }
            uploadingPhotoId = null
            allPhotosVerified = carPhotos.all { it.isVerified }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        }
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Please upload photos of your vehicle",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(carPhotos) { photo ->
                PhotoUploadCard(
                    photo = photo,
                    onCameraClick = {
                        uploadingPhotoId = photo.id
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onGalleryClick = {
                        uploadingPhotoId = photo.id
                        galleryLauncher.launch("image/*")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (allPhotosVerified) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0A1A0A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "✓ All photos verified",
                        color = Color(0xFF76FF03),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Button(
            onClick = {
                if (allPhotosVerified) {
                    onVerificationSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (allPhotosVerified) Color.White else Color.Gray
            ),
            enabled = allPhotosVerified
        ) {
            Text(
                "Complete Verification",
                color = SurgeBlack,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PhotoUploadCard(
    photo: CarPhoto,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
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
                    text = photo.type,
                    color = SurgeWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (photo.isVerified) "✓ Uploaded" else "Not uploaded",
                    color = if (photo.isVerified) Color(0xFF76FF03) else Color.Gray,
                    fontSize = 12.sp
                )
            }

            if (!photo.isVerified) {
                Row(
                    modifier = Modifier.gap(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCameraClick,
                        modifier = Modifier
                            .size(40.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("📷", fontSize = 16.sp)
                    }
                    Button(
                        onClick = onGalleryClick,
                        modifier = Modifier
                            .size(40.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("🖼️", fontSize = 16.sp)
                    }
                }
            } else {
                Text(
                    "✓",
                    color = Color(0xFF76FF03),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RowScope.gap(size: Dp) {
    Spacer(modifier = Modifier.width(size))
}
