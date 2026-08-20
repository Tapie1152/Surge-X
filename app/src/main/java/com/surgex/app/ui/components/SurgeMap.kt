package com.surgex.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Paint
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
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private const val TAG_ROUTE_LINE = "surge_route"
private const val TAG_DEST_MARKER = "surge_dest"

@Composable
fun SurgeMap(
    modifier: Modifier = Modifier,
    startLatitude: Double = -33.9249,
    startLongitude: Double = 18.4241,
    zoomLevel: Double = 14.0,
    showMyLocation: Boolean = true,
    destinationLat: Double? = null,
    destinationLon: Double? = null
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    var locationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
            Configuration.getInstance().userAgentValue = ctx.packageName

            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(startLatitude, startLongitude))

                if (showMyLocation) {
                    val overlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    overlay.enableMyLocation()
                    overlay.enableFollowLocation()
                    overlays.add(overlay)
                    locationOverlay = overlay
                }
            }
        },
        update = { mapView ->
            mapView.onResume()

            // Remove old route and marker
            mapView.overlays.removeAll { overlay ->
                (overlay as? Polyline)?.id == TAG_ROUTE_LINE ||
                (overlay as? Marker)?.id == TAG_DEST_MARKER
            }

            if (destinationLat != null && destinationLon != null) {
                val destPoint = GeoPoint(destinationLat, destinationLon)
                val userPoint = locationOverlay?.myLocation
                    ?: GeoPoint(startLatitude, startLongitude)

                // Draw cyan route line
                val routeLine = Polyline(mapView).apply {
                    id = TAG_ROUTE_LINE
                    setPoints(listOf(userPoint, destPoint))
                    outlinePaint.apply {
                        color = android.graphics.Color.parseColor("#00E5FF")
                        strokeWidth = 6f
                        style = Paint.Style.STROKE
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                        isAntiAlias = true
                    }
                }
                mapView.overlays.add(routeLine)

                // Add destination marker
                val marker = Marker(mapView).apply {
                    id = TAG_DEST_MARKER
                    position = destPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Destination"
                }
                mapView.overlays.add(marker)

                // Zoom to show both points
                try {
                    val box = BoundingBox.fromGeoPoints(listOf(userPoint, destPoint))
                    mapView.zoomToBoundingBox(box.increaseByScale(1.4f), true, 120)
                } catch (e: Exception) {
                    mapView.controller.setCenter(destPoint)
                    mapView.controller.setZoom(14.0)
                }

                mapView.invalidate()
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {}
    }
}
