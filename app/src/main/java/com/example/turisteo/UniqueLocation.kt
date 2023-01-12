package com.example.turisteo

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager


class UniqueLocation(private var locationCallback: LocationCallback) {

    private var locationListener: LocationListener = LocationListener { location ->
        locationCallback.onNewLocationAvailable(
            GPSCoordinates(
                location.latitude,
                location.longitude
            )
        )
    }

    interface LocationCallback {
        fun onNewLocationAvailable(location: GPSCoordinates)
    }

    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(context: Context) {
        // access to gps and network services
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                500,
                100f,
                locationListener
            )
            return
        }

        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500,
                100f,
                locationListener
            )
        }
    }

    companion object class GPSCoordinates(private var latitude: Double, private var longitude: Double)
}