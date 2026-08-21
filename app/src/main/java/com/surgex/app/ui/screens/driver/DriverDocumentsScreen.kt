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
    var idNumber by remember { mutableStateOf("") }
    var carBrand by remember { mutableStateOf("") }
    var carModel by remember { mutableStateOf("") }
    var carColor by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }

    if (submitted) {
        // PENDING STATE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurgeBlack)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("⏳", fontSize = 64.sp)
            Spacer(Modifier = Modifier.height(24.dp))
            Text(
                "Documents Submitted",
                color = SurgeWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Your documents are under review.\nThis usually takes 24–48 hours.\n\nYou can switch back to Rider mode while waiting.",
                color = Color.Gray,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("SWITCH TO RIDER MODE", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { /* stay on pending */ }) {
                Text("Stay on this screen", color = Color.Gray)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", color = SurgeWhite, fontSize = 28.sp, modifier = Modifier.clickable { onBack() }.padding(end = 16.dp))
            Text("Driver Documents", color = SurgeWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Complete verification to start earning", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))

        // Personal Documents
        Text("PERSONAL DOCUMENTS", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        SurgeXTextField(value = licenseNumber, onValueChange = { licenseNumber = it }, label = "Driver's License Number")
        Spacer(modifier = Modifier.height(14.dp))
        SurgeXTextField(value = idNumber, onValueChange = { idNumber = it }, label = "ID / Passport Number", keyboardType = KeyboardType.Number)

        Spacer(modifier = Modifier.height(28.dp))

        // Vehicle
        Text("VEHICLE DETAILS", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        SurgeXTextField(value = carBrand, onValueChange = { carBrand = it }, label = "Car Brand (e.g. Toyota)")
        Spacer(modifier = Modifier.height(14.dp))
        SurgeXTextField(value = carModel, onValueChange = { carModel = it }, label = "Car Model (e.g. Corolla)")
        Spacer(modifier = Modifier.height(14.dp))
        SurgeXTextField(value = carColor, onValueChange = { carColor = it }, label = "Colour")
        Spacer(modifier = Modifier.height(14.dp))
        SurgeXTextField(value = licensePlate, onValueChange = { licensePlate = it }, label = "License Plate")

        Spacer(modifier = Modifier.height(16.dp))
        Text("Photos of car (front, back, inside) will be required in the next version.", color = Color(0xFF666666), fontSize = 12.sp)

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = Color(0xFFFF4444), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
    when {
        licenseNumber.isBlank() || licenseNumber.length < 8 -> {
            errorMessage = "Enter a valid South African driver's license number"
        }
        !licenseNumber.matches(Regex("^[0-9]{8,12}$")) -> {
            errorMessage = "License number should contain only numbers (8–12 digits)"
        }
        idNumber.isBlank() || idNumber.length < 6 -> {
            errorMessage = "Enter your ID Number or Passport Number"
        }
        idNumber.length == 13 && !idNumber.matches(Regex("^[0-9]{13}$")) -> {
            errorMessage = "South African ID must be exactly 13 digits"
        }
        carBrand.isBlank() || carModel.isBlank() -> {
            errorMessage = "Please complete vehicle brand and model"
        }
        carColor.isBlank() -> {
            errorMessage = "Please enter the car colour"
        }
        licensePlate.isBlank() || licensePlate.length < 5 -> {
            errorMessage = "Enter a valid license plate"
        }
        else -> {
            isLoading = true
            errorMessage = null
            submitted = true
            isLoading = false
        }
    }
}
          

            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
            } else {
                Text("SUBMIT FOR REVIEW", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
