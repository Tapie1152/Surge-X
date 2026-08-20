package com.surgex.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.auth.AuthControllerEnhanced
import com.surgex.app.auth.AuthResult
import com.surgex.app.utils.PhoneValidator
import kotlinx.coroutines.launch

@Composable
fun UnifiedSignupScreen(
    authController: AuthControllerEnhanced,
    onSignupSuccess: (phone: String, mode: String) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("RIDER") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -30 }
            ) {
                Column {
                    Text(
                        text = "SurgeX",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "Create Account",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Join SurgeX as a rider or driver.",
                        color = Color(0xFF888888),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Mode Selection
            Text(
                text = "I am a",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModeButton(
                    label = "Rider",
                    isSelected = selectedMode == "RIDER",
                    color = Color(0xFF00E5FF),
                    onClick = { selectedMode = "RIDER" },
                    modifier = Modifier.weight(1f)
                )

                ModeButton(
                    label = "Driver",
                    isSelected = selectedMode == "DRIVER",
                    color = Color(0xFF76FF03),
                    onClick = { selectedMode = "DRIVER" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Form Fields
            SurgeXTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                label = "Full name",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(12.dp))

            SurgeXTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = "Email address",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            SurgeXTextField(
                value = phone,
                onValueChange = { phone = it; errorMessage = null },
                label = "Phone (+27821234567 or 0821234567)",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            SurgeXTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            SurgeXTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMessage = null },
                label = "Confirm password",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1A0000)
                ) {
                    Text(
                        text = it,
                        color = Color(0xFFFF4444),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Please enter your name."
                        email.isBlank() -> errorMessage = "Please enter your email."
                        phone.isBlank() -> errorMessage = "Please enter your phone number."
                        !PhoneValidator.isValidSouthAfricanPhone(phone) ->
                            errorMessage = "Please enter a valid South African phone (+27...)"
                        password.isBlank() -> errorMessage = "Please enter a password."
                        password != confirmPassword -> errorMessage = "Passwords do not match."
                        password.length < 6 -> errorMessage = "Password must be at least 6 characters."
                        else -> {
                            isLoading = true
                            scope.launch {
                                when (val result = authController.register(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    password = password,
                                    role = selectedMode
                                )) {
                                    is AuthResult.Success -> {
                                        onSignupSuccess(phone, selectedMode)
                                    }
                                    is AuthResult.Error -> {
                                        errorMessage = result.message
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color(0xFF1A1A1A)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "CREATE ACCOUNT",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "← Back",
                color = Color(0xFF555555),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().clickable { onBack() }
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        Text(
            text = "SURGEX • MOVE DIFFERENTLY",
            color = Color(0xFF1A1A1A),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        )
    }
}

@Composable
fun ModeButton(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF1A1A1A),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (isSelected) color else Color(0xFF888888),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
