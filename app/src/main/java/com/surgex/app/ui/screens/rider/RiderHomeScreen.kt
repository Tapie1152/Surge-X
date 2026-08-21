package com.surgex.app.ui.screens.rider

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
    onProfilePicUpload: () -> Unit = {}
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

        // Top bar
        TopBar(
            onMenuClick = { menuOpen = true },
            onSwitchToDriver = onSwitchToDriver
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
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { menuOpen = false }
            )
        }

        AnimatedVisibility(
            visible = menuOpen,
            enter = slideInHorizontally(tween(300)) { -it },
            exit = slideOutHorizontally(tween(300)) { -it }
        ) {
            SideMenu(
                onClose = { menuOpen = false },
                onSwitchToDriver = {
                    menuOpen = false
                    onSwitchToDriver()
                },
                onHome = { menuOpen = false },
                onTripHistory = {
                    menuOpen = false
                    onTripHistory()
                },
                onPaymentMethods = {
                    menuOpen = false
                    onPaymentMethods()
                },
                onSafety = {
                    menuOpen = false
                    onSafety()
                },
                onSettings = {
                    menuOpen = false
                    onSettings()
                },
                onProfilePicUpload = {
                    menuOpen = false
                    onProfilePicUpload()
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    onMenuClick: () -> Unit,
    onSwitchToDriver: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable { onMenuClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(3) {
                    Box(modifier = Modifier.width(18.dp).height(2.dp).background(SurgeWhite, RoundedCornerShape(1.dp)))
                }
            }
        }

        Text(text = "SurgeX", color = SurgeWhite, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1A0F))
                .clickable { onSwitchToDriver() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🚗", fontSize = 18.sp)
        }
    }
}

@Composable
private fun SideMenu(
    onClose: () -> Unit,
    onSwitchToDriver: () -> Unit,
    onHome: () -> Unit = {},
    onTripHistory: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onSafety: () -> Unit = {},
    onSettings: () -> Unit = {},
    onProfilePicUpload: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color(0xFF0A0A0A))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "SurgeX", color = SurgeWhite, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "RIDER MODE", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(40.dp))
            Divider(color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(24.dp))
            MenuItemRow(icon = "🏠", label = "Home", onClick = onHome)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "👤", label = "Profile Picture", onClick = onProfilePicUpload)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "🧾", label = "Trip History", onClick = onTripHistory)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "💳", label = "Payment Methods", onClick = onPaymentMethods)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "🛡️", label = "Safety", onClick = onSafety)
            Spacer(modifier = Modifier.height(8.dp))
            MenuItemRow(icon = "⚙️", label = "Settings", onClick = onSettings)
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F1A0F))
                    .clickable { onSwitchToDriver() }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚗", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = "Switch to Driver", color = Color(0xFF76FF03), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Start earning now", color = Color(0xFF4A7A00), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "SURGEX • MOVE DIFFERENTLY", color = Color(0xFF1E1E1E), fontSize = 9.sp, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun MenuItemRow(icon: String, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = SurgeWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color(0xFF0A0A0A)
        ) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp)) {

                Box(
                    modifier = Modifier
                        .width(40.dp).height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF2A2A2A))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(text = "Where to?", color = SurgeWhite, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(16.dp))

                LocationInput(
                    label = "PICKUP",
                    value = pickup,
                    onValueChange = onPickupChange,
                    placeholder = "Enter pickup location",
                    dotColor = Color(0xFF00E5FF),
                    confirmed = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                LocationInput(
                    label = "DESTINATION",
                    value = destination,
                    onValueChange = onDestinationChange,
                    placeholder = "Where are you going?",
                    dotColor = SurgeGrey,
                    confirmed = destinationConfirmed
                )

                // Search results dropdown
                AnimatedVisibility(visible = isSearching) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Searching...", color = SurgeGrey, fontSize = 13.sp)
                    }
                }

                if (searchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF141414)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            items(searchResults) { place ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onPlaceSelected(place) }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "📍", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = place.shortName,
                                            color = SurgeWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = place.displayName.split(",").drop(2).take(2).joinToString(",").trim(),
                                            color = SurgeGrey,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Divider(color = Color(0xFF1E1E1E))
                            }
                        }
                    }
                }

                // Route confirmed indicator
                if (destinationConfirmed) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF001A00)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✓", color = Color(0xFF76FF03), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Route shown on map", color = Color(0xFF4A7A00), fontSize = 12.sp)
                        }
                    }
                }

                if (!destinationConfirmed) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Quick destinations", color = SurgeGrey, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickDestination(title = "🏠 Home", modifier = Modifier.weight(1f)) { onDestinationChange("Home Cape Town") }
                        QuickDestination(title = "💼 Work", modifier = Modifier.weight(1f)) { onDestinationChange("Work Cape Town") }
                        QuickDestination(title = "🕐 Recent", modifier = Modifier.weight(1f)) { onDestinationChange("Cape Town CBD") }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onChooseRide,
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurgeWhite,
                        contentColor = SurgeBlack,
                        disabledContainerColor = Color(0xFF1A1A1A),
                        disabledContentColor = SurgeGrey
                    ),
                    enabled = destinationConfirmed
                ) {
                    Text(
                        text = if (!destinationConfirmed) "SELECT A DESTINATION" else "CHOOSE A RIDE",
                        fontSize = 13.sp,
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
    placeholder: String,
    dotColor: Color,
    confirmed: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (confirmed) Color(0xFF0A1A0A) else SurgeSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (confirmed) Color(0xFF76FF03) else dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = SurgeGrey, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(3.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = SurgeWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                cursorBrush = SolidColor(SurgeWhite),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty()) Text(text = placeholder, color = Color(0xFF444444), fontSize = 14.sp)
                    inner()
                }
            )
        }
        if (confirmed) {
            Text(text = "✓", color = Color(0xFF76FF03), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickDestination(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurgeSurfaceLight)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = SurgeWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
