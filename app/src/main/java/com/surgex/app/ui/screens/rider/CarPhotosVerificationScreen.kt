package com.surgex.app.ui.screens.rider

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun CarPhotosVerificationScreen(
    onBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val context = LocalContext.current
    var frontPhotoUploaded by remember { mutableStateOf(false) }
    var backPhotoUploaded by remember { mutableStateOf(false) }
    var leftPhotoUploaded by remember { mutableStateOf(false) }
    var rightPhotoUploaded by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Update the last clicked photo
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    LaunchedEffect(Unit) {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        permissionGranted = hasCameraPermission
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = { Text("Car Verification", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Upload car photos for verification",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Car Photo Items
            CarPhotoUploadItem(
                "Front View",
                frontPhotoUploaded,
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
            CarPhotoUploadItem(
                "Back View",
                backPhotoUploaded,
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
            CarPhotoUploadItem(
                "Left Side",
                leftPhotoUploaded,
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
            CarPhotoUploadItem(
                "Right Side",
                rightPhotoUploaded,
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = onVerificationSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = frontPhotoUploaded && backPhotoUploaded && leftPhotoUploaded && rightPhotoUploaded,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(
                    "Submit Verification",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CarPhotoUploadItem(
    label: String,
    uploaded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.SemiBold)
                Text(
                    if (uploaded) "Uploaded" else "Not uploaded",
                    fontSize = 12.sp,
                    color = if (uploaded) Color(0xFF4CAF50) else Color.Gray
                )
            }

            if (uploaded) {
                Icon(
                    Icons.Default.CheckCircle,
                    "Uploaded",
                    tint = Color(0xFF4CAF50)
                )
            } else {
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.CameraAlt, "Upload")
                }
            }
        }
    }
}
