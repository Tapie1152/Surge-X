package com.surgex.app.ui.screens.driver

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeGrey
import com.surgex.app.ui.theme.SurgeSurface
import com.surgex.app.ui.theme.SurgeSurfaceLight
import com.surgex.app.ui.theme.SurgeWhite
import kotlinx.coroutines.delay

@Composable
fun DriverRideRequestScreen(
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {}
) {

    var secondsLeft by remember {
        mutableIntStateOf(15)
    }

    LaunchedEffect(Unit) {

        while (secondsLeft > 0) {

            delay(1000)

            secondsLeft--
        }

        if (secondsLeft == 0) {
            onDecline()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(
        label = "requestPulse"
    )

var showSafetyDialog by remember { mutableStateOf(false) }
var showSafetyDialog by remember { mutableStateOf(false) }

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 9000,
                easing = LinearEasing
            )
        ),
        label = "radarRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {

        RequestMap(
            rotation = rotation,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        )

        RequestDetails(
            secondsLeft = secondsLeft,
            onAccept = onAccept,
            onDecline = onDecline
        )

if (showSafetyDialog) {
    AlertDialog(
        onDismissRequest = { showSafetyDialog = false },
        title = {
            Text("Do you feel safe?", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Are you comfortable taking this ride?\n\nIf you select No, your live location will be shared with SurgeX monitoring and your emergency contact.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showSafetyDialog = false
                    onAccept()          // Driver feels safe → start the ride
                }
            ) {
                Text("YES, I FEEL SAFE", color = Color(0xFF00C853))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showSafetyDialog = false
                    // Later we will send location to monitoring + emergency contact
                    onDecline()         // For now just decline the ride
                }
            ) {
                Text("NO, CANCEL RIDE", color = Color.Red)
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray
    )

   }

@Composable
private fun RequestMap(
    rotation: Float,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .background(Color(0xFF181818)),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(230.dp)
                .rotate(rotation)
                .background(
                    SurgeWhite.copy(alpha = 0.025f),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(
                    SurgeWhite.copy(alpha = 0.05f),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    SurgeWhite,
                    CircleShape
                )
        )

        Text(
            text = "NEW RIDE",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 25.dp),
            color = SurgeWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun RequestDetails(
    secondsLeft: Int,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurgeBlack,
        shape = RoundedCornerShape(
            topStart = 30.dp,
            topEnd = 30.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
        ) {

            RequestHeader(
                secondsLeft = secondsLeft
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            RiderCard()

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            RouteCard()

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            EarningsCard()

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .weight(0.8f)
                        .height(56.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SurgeWhite
                    )
                ) {

                    Text(
                        text = "DECLINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

Button(
    onClick = { showSafetyDialog = true },
    modifier = Modifier
        .weight(1f)
        .height(54.dp),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
) {
    Text("ACCEPT RIDE", color = Color.Black, fontWeight = FontWeight.Bold)
}

                    Text(
                        text = "ACCEPT RIDE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestHeader(
    secondsLeft: Int
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "New ride request",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Nearby passenger",
                color = SurgeGrey,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    SurgeSurfaceLight,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "$secondsLeft",
                color = SurgeWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun RiderCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                SurgeSurface,
                RoundedCornerShape(17.dp)
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    SurgeSurfaceLight,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "R",
                color = SurgeWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(
            modifier = Modifier.width(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Rider",
                color = SurgeWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "⭐ 4.9 • Verified rider",
                color = SurgeGrey,
                fontSize = 11.sp
            )
        }

        Text(
            text = "1–4",
            color = SurgeGrey,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun RouteCard() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                SurgeSurface,
                RoundedCornerShape(17.dp)
            )
            .padding(16.dp)
    ) {

        RouteRow(
            title = "PICKUP",
            value = "2.4 km away"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        RouteRow(
            title = "DESTINATION",
            value = "Cape Town CBD"
        )
    }
}

@Composable
private fun RouteRow(
    title: String,
    value: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(9.dp)
                .background(
                    SurgeWhite,
                    CircleShape
                )
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column {

            Text(
                text = title,
                color = SurgeGrey,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = value,
                color = SurgeWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EarningsCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                SurgeSurface,
                RoundedCornerShape(17.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "ESTIMATED EARNINGS",
                color = SurgeGrey,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "R92.00",
                color = SurgeWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "12.8 km",
                color = SurgeWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "≈ 24 min",
                color = SurgeGrey,
                fontSize = 11.sp
            )
        }
    }
}
