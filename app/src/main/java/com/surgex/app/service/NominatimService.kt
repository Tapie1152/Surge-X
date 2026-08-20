package com.surgex.app.service

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

data class PlaceResult(
    val displayName: String,
    val shortName: String,
    val lat: Double,
    val lon: Double
)

object NominatimService {

    suspend fun search(query: String): List<PlaceResult> = withContext(Dispatchers.IO) {
        if (query.length < 3) return@withContext emptyList()

        return@withContext try {
            val encoded = Uri.encode(query)
            val url = "https://nominatim.openstreetmap.org/search" +
                    "?q=$encoded" +
                    "&format=json" +
                    "&limit=5" +
                    "&countrycodes=za" +
                    "&addressdetails=1"

            val connection = URL(url).openConnection()
            connection.setRequestProperty("User-Agent", "SurgeX-App/1.0")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val response = connection.getInputStream().bufferedReader().readText()
            val jsonArray = JSONArray(response)

            val results = mutableListOf<PlaceResult>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val displayName = item.getString("display_name")
                val lat = item.getString("lat").toDouble()
                val lon = item.getString("lon").toDouble()

                // Make short name from first two parts
                val parts = displayName.split(",")
                val shortName = parts.take(2).joinToString(",").trim()

                results.add(PlaceResult(displayName, shortName, lat, lon))
            }
            results
        } catch (e: Exception) {
            emptyList()
        }
    }
}
