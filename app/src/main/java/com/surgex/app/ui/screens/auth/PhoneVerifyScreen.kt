package com.surgex.app.ui.screens.auth

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.auth.AuthController
import kotlinx.coroutines.launch

@Composable
fun PhoneVerifyScreen(
    phoneNumber: String,
    authController: AuthController,
    onCodeSent: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var phone by remember { mutableStateOf(phoneNumber) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // South African phone validation
    fun isValidSouthAfricanPhone(number: String): Boolean {
        val cleaned = number.replace(" ", "").replace("-", "")
        return when {
            cleaned.startsWith("+27") && cleaned.length == 12 -> true
            cleaned.startsWith("27") && cleaned.length == 11 -> true
            cleaned.startsWith("0") && cleaned.length == 10 -> true
            else -> false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(horizontal = 28.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "← Back",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onBack() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Verify your number",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We will send you a 6-digit verification code",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        SurgeXTextField(
            value = phone,
            onValueChange = {
                phone = it
                errorMessage = null
            },
            label = "Phone number (e.g. 0821234567)",
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(12.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color(0xFFFF5252),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    phone.isBlank() -> errorMessage = "Please enter your phone number"
                    !isValidSouthAfricanPhone(phone) -> {
                        errorMessage = "Please enter a valid South African number\nExample: 0821234567 or +27821234567"
                    }
                    else -> {
                        isLoading = true
                        scope.launch {
                            // Here we would normally call Firebase to send OTP
                            // For now we just proceed
                            onCodeSent()
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            enabled = !isLoading
        ) {
            Text(
                text = if (isLoading) "SENDING..." else "SEND VERIFICATION CODE",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
