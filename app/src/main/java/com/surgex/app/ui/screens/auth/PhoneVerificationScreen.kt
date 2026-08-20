package com.surgex.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.auth.AuthControllerEnhanced
import com.surgex.app.auth.AuthResult
import kotlinx.coroutines.launch

@Composable
fun PhoneVerificationScreen(
    phone: String,
    authController: AuthControllerEnhanced,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var resendCountdown by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(resendCountdown) {
        if (resendCountdown > 0) {
            kotlinx.coroutines.delay(1000)
            resendCountdown--
        }
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -30 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Verify Your Number",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "We sent a code to $phone",
                        color = Color(0xFF888888),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(52.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, 200)) + slideInVertically(tween(800, 200)) { 40 }
            ) {
                Column {
                    SurgeXTextField(
                        value = otp,
                        onValueChange = { 
                            otp = it.take(6)
                            errorMessage = null 
                        },
                        label = "Enter 6-digit code",
                        keyboardType = KeyboardType.Number
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
                                otp.isBlank() -> errorMessage = "Please enter the 6-digit code."
                                otp.length != 6 -> errorMessage = "Code must be 6 digits."
                                else -> {
                                    isLoading = true
                                    scope.launch {
                                        when (val result = authController.verifyOtp(otp)) {
                                            is AuthResult.Success -> onVerificationSuccess()
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
                        enabled = !isLoading && otp.length == 6
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "VERIFY",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (resendCountdown > 0) {
                        Text(
                            text = "Resend code in ${resendCountdown}s",
                            color = Color(0xFF888888),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Didn't receive code? Tap to resend",
                            color = Color(0xFF00E5FF),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "← Back",
                        color = Color(0xFF555555),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBack() },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
