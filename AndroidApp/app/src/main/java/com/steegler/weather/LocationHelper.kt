package com.steegler.weather

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class LocationHelper @Inject constructor(private val locationManager: LocationManager) {

    var location: MutableStateFlow<Location?> = MutableStateFlow(null)

    private lateinit var locationListener: LocationListener

    @SuppressLint("MissingPermission")
    fun runLocation() {
        locationListener = LocationListener { p0 ->
            location.update { p0 }
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000, 1000f, locationListener, Looper.getMainLooper()
        )
    }
}


