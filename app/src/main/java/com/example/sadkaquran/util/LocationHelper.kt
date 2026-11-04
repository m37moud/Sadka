package com.example.sadkaquran.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class DetectedLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val countryName: String
)

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun hasLocationPermission(): Boolean {
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return coarseGranted || fineGranted
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): DetectedLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) return@withContext null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                        bestLocation = loc
                    }
                }
            } catch (ignored: Exception) {
            }
        }

        val location = bestLocation ?: return@withContext null
        val (city, country) = resolveCityAndCountry(location.latitude, location.longitude)

        DetectedLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            cityName = city,
            countryName = country
        )
    }

    private fun resolveCityAndCountry(latitude: Double, longitude: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // In API 33+, synchronous getFromLocation is deprecated, but fallback works
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: Constants.DEFAULT_CITY
                    val country = address.countryName ?: Constants.DEFAULT_COUNTRY
                    Pair(city, country)
                } else {
                    Pair(Constants.DEFAULT_CITY, Constants.DEFAULT_COUNTRY)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: Constants.DEFAULT_CITY
                    val country = address.countryName ?: Constants.DEFAULT_COUNTRY
                    Pair(city, country)
                } else {
                    Pair(Constants.DEFAULT_CITY, Constants.DEFAULT_COUNTRY)
                }
            }
        } catch (e: Exception) {
            Pair(Constants.DEFAULT_CITY, Constants.DEFAULT_COUNTRY)
        }
    }
}
