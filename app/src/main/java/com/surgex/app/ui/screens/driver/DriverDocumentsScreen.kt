package com.surgex.app.ui.screens.driver

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.screens.auth.SurgeXTextField
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite

@Composable
fun DriverDocumentsScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val scrollState = rememberScrollState()

    var licenseNumber by remember { mutableStateOf("") }
    var licenseExpiry by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var carBrand by remember { mutableStateOf("") }
    var carModel by remember { mutableStateOf("") }
    var carYear by remember { mutableStateOf("") }
    var carColor by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
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
                text = "Driver Documents",
                color = SurgeWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Complete your verification to start earning",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // License Section
        SectionTitle("Driver License")
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = licenseNumber,
            onValueChange = { licenseNumber = it; errorMessage = null },
            label = "License Number",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = licenseExpiry,
            onValueChange = { licenseExpiry = it; errorMessage = null },
            label = "Expiry Date (DD/MM/YYYY)",
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = idNumber,
            onValueChange = { idNumber = it; errorMessage = null },
            label = "South African ID Number",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Vehicle Section
        SectionTitle("Vehicle Information")
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = carBrand,
            onValueChange = { carBrand = it; errorMessage = null },
            label = "Brand (e.g. Toyota)",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = carModel,
            onValueChange = { carModel = it; errorMessage = null },
            label = "Model",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = carYear,
            onValueChange = { carYear = it; errorMessage = null },
            label = "Year",
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = carColor,
            onValueChange = { carColor = it; errorMessage = null },
            label = "Color",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(12.dp))

        SurgeXTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it; errorMessage = null },
            label = "License Plate",
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Messages
        errorMessage?.let {
            Text(
                text = it,
                color = Color(0xFFFF5252),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        successMessage?.let {
            Text(
                text = it,
                color = Color(0xFF69F0AE),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Save Button
        Button(
            onClick = {
                when {
                    licenseNumber.isBlank() -> errorMessage = "License number is required"
                    idNumber.isBlank() -> errorMessage = "ID number is required"
                    carBrand.isBlank() -> errorMessage = "Car brand is required"
                    carModel.isBlank() -> errorMessage = "Car model is required"
                    licensePlate.isBlank() -> errorMessage = "License plate is required"
                    else -> {
                        isLoading = true
                        // Simulate saving (we will connect real save later)
                        successMessage = "Documents submitted successfully! Pending verification."
                        isLoading = false
                        // Optional: automatically go back after success
                        // onSaved()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E676),
                contentColor = Color.Black
            ),
            enabled = !isLoading
        ) {
            Text(
                text = if (isLoading) "SAVING..." else "SUBMIT DOCUMENTS",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "← Back to Driver Home",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF00E676),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}
