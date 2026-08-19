package com.surgex.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun SurgeMap(
    modifier: Modifier = Modifier,
    startLatitude: Double = -33.9249,   // Cape Town default
    startLongitude: Double = 18.4241,
    zoomLevel: Double = 14.0,
    showMyLocation: Boolean = true
) {
    val context = LocalContext.current

    // Ask for location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* handled below */ }

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            // Important for osmdroid
            Configuration.getInstance().load(
                ctx,
                PreferenceManager.getDefaultSharedPreferences(ctx)
            )
            Configuration.getInstance().userAgentValue = ctx.packageName

            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(startLatitude, startLongitude))

                if (showMyLocation) {
                    val locationOverlay = MyLocationNewOverlay(
                        GpsMyLocationProvider(ctx),
                        this
                    )
                    locationOverlay.enableMyLocation()
                    overlays.add(locationOverlay)
                }
            }
        },
        update = { mapView ->
            mapView.onResume()
        }
    )

    // Clean up when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            // MapView cleanup is handled by AndroidView
        }
    }
}
