package com.surgex.app.ui.screens.auth

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.auth.AuthController
import com.surgex.app.auth.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpScreen(
    phoneNumber: String,
    authController: AuthController,
    onVerified: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity
    val focusRequester = remember { FocusRequester() }

    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(80)
        visible = true
        focusRequester.requestFocus()
    }

    // Countdown timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        canResend = true
    }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Auto-verify when 6 digits entered
    LaunchedEffect(otpValue) {
        if (otpValue.length == 6) {
            isLoading = true
            errorMessage = null
            scope.launch {
                when (val result = authController.verifyOtp(otpValue)) {
                    is AuthResult.Success -> onVerified()
                    is AuthResult.Error -> {
                        errorMessage = result.message
                        isLoading = false
                        otpValue = ""
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .scale(pulse)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        // Hidden text field to capture keyboard input
        BasicTextField(
            value = otpValue,
            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otpValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester),
            cursorBrush = SolidColor(Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -20 }
            ) {
                Column {
                    Text(
                        text = "SurgeX",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(56.dp))

                    Text(
                        text = "Enter the\ncode.",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 44.sp,
                        letterSpacing = (-1.5).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "We sent a 6-digit code to $phoneNumber",
                        color = Color(0xFF505050),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, 200)) + slideInVertically(tween(700, 200)) { 40 }
            ) {
                Column {
                    // 6 OTP boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(6) { index ->
                            val char = otpValue.getOrNull(index)
                            val isCurrent = index == otpValue.length
                            val isFilled = char != null

                            val borderColor by animateColorAsState(
                                targetValue = when {
                                    isCurrent -> Color.White
                                    isFilled -> Color(0xFF333333)
                                    else -> Color(0xFF1A1A1A)
                                },
                                animationSpec = tween(200),
                                label = "border$index"
                            )

                            val cursorAlpha by rememberInfiniteTransition(label = "cursor$index")
                                .animateFloat(
                                    initialValue = 0f,
                                    targetValue = if (isCurrent) 1f else 0f,
                                    animationSpec = infiniteRepeatable(
                                        tween(600),
                                        RepeatMode.Reverse
                                    ),
                                    label = "blink$index"
                                )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.85f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0D0D0D))
                                    .border(
                                        width = if (isCurrent) 1.5.dp else 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { focusRequester.requestFocus() },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFilled) {
                                    Text(
                                        text = char.toString(),
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                } else if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(22.dp)
                                            .background(
                                                Color.White.copy(alpha = cursorAlpha),
                                                RoundedCornerShape(1.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    errorMessage?.let {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF150000)
                        ) {
                            Text(
                                text = it,
                                color = Color(0xFFFF4444),
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth().padding(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Countdown / Resend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!canResend) {
                            Text(
                                text = "Resend code in ",
                                color = Color(0xFF444444),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${countdown}s",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Didn't get it? ",
                                color = Color(0xFF444444),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Resend",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    val raw = phoneNumber.trim().removePrefix("0")
                                    val formatted = if (phoneNumber.startsWith("+")) phoneNumber else "+27$raw"
                                    canResend = false
                                    countdown = 60
                                    otpValue = ""
                                    errorMessage = null
                                    scope.launch {
                                        authController.sendOtp(
                                            phoneNumber = formatted,
                                            activity = activity,
                                            onCodeSent = {},
                                            onAutoVerified = { onVerified() },
                                            onError = { msg -> errorMessage = msg }
                                        )
                                        while (countdown > 0) {
                                            delay(1000)
                                            countdown--
                                        }
                                        canResend = true
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "← Back",
                        color = Color(0xFF303030),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().clickable { onBack() }
                    )
                }
            }
        }

        Text(
            text = "SURGEX • MOVE DIFFERENTLY",
            color = Color(0xFF181818),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        )
    }
}
