package com.surgex.app.ui.screens.rider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.components.SurgeMap
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeGrey
import com.surgex.app.ui.theme.SurgeSurface
import com.surgex.app.ui.theme.SurgeSurfaceLight
import com.surgex.app.ui.theme.SurgeWhite

@Composable
fun RiderHomeScreen(
    onChooseRide: () -> Unit,
    onSwitchToDriver: () -> Unit = {}
) {
    var pickup by remember { mutableStateOf("Current location") }
    var destination by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        MapFoundation()
        TopBar(onSwitchToDriver = onSwitchToDriver)
        RideRequestSheet(
            pickup = pickup,
            onPickupChange = { pickup = it },
            destination = destination,
            onDestinationChange = { destination = it },
            onChooseRide = onChooseRide
        )
    }
}

@Composable
private fun MapFoundation() {
    SurgeMap(
        startLatitude = -33.9249,   // Cape Town
        startLongitude = 18.4241,
        zoomLevel = 14.0,
        showMyLocation = true
    )
}

@Composable
private fun TopBar(onSwitchToDriver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleButton(text = "☰")

        Text(
            text = "SurgeX",
            color = SurgeWhite,
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Switch to Driver mode
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1A1A))
                .clickable { onSwitchToDriver() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🚗", fontSize = 18.sp)
        }
    }
}

@Composable
private fun CircleButton(text: String) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = SurgeWhite, fontSize = 18.sp)
    }
}

@Composable
private fun RideRequestSheet(
    pickup: String,
    onPickupChange: (String) -> Unit,
    destination: String,
    onDestinationChange: (String) -> Unit,
    onChooseRide: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color(0xFF0A0A0A)
        ) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp)) {

                // Drag handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF2A2A2A))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Where to?",
                    color = SurgeWhite,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pickup field (now typeable)
                LocationInput(
                    label = "Pickup location",
                    value = pickup,
                    onValueChange = onPickupChange,
                    placeholder = "Enter pickup location"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Destination field (now typeable)
                LocationInput(
                    label = "Destination",
                    value = destination,
                    onValueChange = onDestinationChange,
                    placeholder = "Where are you going?"
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Quick destinations",
                    color = SurgeGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickDestination(title = "Home", modifier = Modifier.weight(1f)) {
                        onDestinationChange("Home")
                    }
                    QuickDestination(title = "Work", modifier = Modifier.weight(1f)) {
                        onDestinationChange("Work")
                    }
                    QuickDestination(title = "Recent", modifier = Modifier.weight(1f)) {
                        onDestinationChange("Recent place")
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onChooseRide,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurgeWhite,
                        contentColor = SurgeBlack
                    ),
                    enabled = destination.isNotBlank()
                ) {
                    Text(
                        text = "CHOOSE A RIDE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun LocationInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurgeSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = SurgeGrey,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = SurgeWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            cursorBrush = SolidColor(SurgeWhite),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF555555),
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun QuickDestination(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurgeSurfaceLight)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = SurgeWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
