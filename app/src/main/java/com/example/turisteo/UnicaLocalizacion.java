package com.example.turisteo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class UnicaLocalizacion {
    LocationCallback locationCallback;


    public UnicaLocalizacion(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            locationCallback.onNewLocationAvailable(new CoordenadasGPS(location.getLatitude(),
                    location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };

    public interface LocationCallback {
        void onNewLocationAvailable(CoordenadasGPS location);
    }

    @SuppressLint("MissingPermission")
    public void requestSingleUpdate(Context context){
        //se accede a los servicios de gps y network
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500,
                    100, locationListener);
        }else{
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,
                        100, locationListener);
            }
        }

    }
    public static class CoordenadasGPS {
        private double latitude;
        private double longitude;

        public CoordenadasGPS(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }


        public double getLongitude() {
            return longitude;
        }


    }

}
