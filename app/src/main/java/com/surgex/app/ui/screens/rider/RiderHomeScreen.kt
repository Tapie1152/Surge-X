package com.surgex.app.ui.screens.rider

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.surgex.app.service.NominatimService
import com.surgex.app.service.PlaceResult
import com.surgex.app.ui.components.SurgeMap
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeGrey
import com.surgex.app.ui.theme.SurgeSurface
import com.surgex.app.ui.theme.SurgeSurfaceLight
import com.surgex.app.ui.theme.SurgeWhite
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RiderHomeScreen(
    onChooseRide: () -> Unit,
    onMenuClick: () -> Unit = {},
    onSwitchToDriver: () -> Unit = {},
    onTripHistory: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onSafety: () -> Unit = {},
    onSettings: () -> Unit = {},
    onProfilePicUpload: () -> Unit = {},
    devModeEnabled: Boolean = false,
    onDeveloperClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var pickup by remember { mutableStateOf("Current location") }
    var destination by remember { mutableStateOf("") }
    var destinationLat by remember { mutableStateOf<Double?>(null) }
    var destinationLon by remember { mutableStateOf<Double?>(null) }
    var searchResults by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        // Map with route line
        SurgeMap(
            startLatitude = -33.9249,
            startLongitude = 18.4241,
            zoomLevel = 14.0,
            showMyLocation = true,
            destinationLat = destinationLat,
            destinationLon = destinationLon
        )

        // Top bar with hamburger and developer buttons
        TopBar(
            onMenuClick = { menuOpen = true },
            onSwitchToDriver = onSwitchToDriver,
            devModeEnabled = devModeEnabled,
            onDeveloperClick = onDeveloperClick
        )

        // Bottom sheet
        RideRequestSheet(
            pickup = pickup,
            onPickupChange = { pickup = it },
            destination = destination,
            onDestinationChange = { typed ->
                destination = typed
                destinationLat = null
                destinationLon = null
                searchResults = emptyList()

                searchJob?.cancel()
                if (typed.length >= 3) {
                    searchJob = scope.launch {
                        delay(400)
                        isSearching = true
                        searchResults = NominatimService.search(typed)
                        isSearching = false
                    }
                }
            },
            searchResults = searchResults,
            isSearching = isSearching,
            onPlaceSelected = { place ->
                destination = place.shortName
                destinationLat = place.lat
                destinationLon = place.lon
                searchResults = emptyList()
            },
            destinationConfirmed = destinationLat != null,
            onChooseRide = onChooseRide
        )

        // Side menu overlay
        AnimatedVisibility(
            visible = menuOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { menuOpen = false }
            )
            
            RiderMenuScreen(
                onTripHistoryClick = {
                    menuOpen = false
                    onTripHistory()
                },
                onPaymentMethodsClick = {
                    menuOpen = false
                    onPaymentMethods()
                },
                onSafetyClick = {
                    menuOpen = false
                    onSafety()
                },
                onSettingsClick = {
                    menuOpen = false
                    onSettings()
                },
                onProfilePicClick = {
                    menuOpen = false
                    onProfilePicUpload()
                },
                onClose = { menuOpen = false }
            )
        }
    }
}

@Composable
private fun TopBar(
    onMenuClick: () -> Unit,
    onSwitchToDriver: () -> Unit,
    devModeEnabled: Boolean = false,
    onDeveloperClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger Menu Button
        CircleButton(
            text = "≡",
            onClick = onMenuClick
        )
        
        Text(
            text = "SurgeX",
            color = SurgeWhite,
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )
        
        // Developer Mode or Switch Button
        if (devModeEnabled) {
            CircleButton(
                text = "🔨",
                onClick = onDeveloperClick
            )
        } else {
            CircleButton(
                text = "ⓒ",
                onClick = onSwitchToDriver
            )
        }
    }
}

@Composable
private fun CircleButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable { onClick() },
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
    searchResults: List<PlaceResult>,
    isSearching: Boolean,
    onPlaceSelected: (PlaceResult) -> Unit,
    destinationConfirmed: Boolean,
    onChooseRide: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = SurgeBlack
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF3A3A3A))
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

                LocationInput(
                    label = "Pickup location",
                    value = pickup,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                LocationInput(
                    label = "Destination",
                    value = destination,
                    onValueChange = onDestinationChange
                )

                if (searchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp)
                    ) {
                        items(searchResults) { place ->
                            Text(
                                text = place.shortName,
                                color = SurgeWhite,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPlaceSelected(place) }
                                    .padding(8.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onChooseRide,
                    enabled = destinationConfirmed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (destinationConfirmed) Color.White else Color.Gray
                    )
                ) {
                    Text(
                        "Confirm Ride",
                        color = SurgeBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SurgeSurfaceLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📍",
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = TextStyle(
                    color = SurgeWhite,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(SurgeWhite),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = label,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
