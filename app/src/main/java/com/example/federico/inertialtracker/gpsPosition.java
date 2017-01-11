package com.example.federico.inertialtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

/**
 * gpsPosition è una classe adibita ad ottenere la attuale posizione gps.
 */

class gpsPosition {
  private static Location mCurrentLocation;
  private static LocationManager locationManager;
  static Context c;

  gpsPosition(Context c) {
    this.c = c;
    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    if (isGPSEnabled) {
      LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
          mCurrentLocation = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
      };

      if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions((Activity) c, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                parameters.MY_PERMISSION_ACCESS_COURSE_LOCATION);
      }

      //MinTime, MinDistance = 0
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

      mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
  }

  //Aggiungo al JSON le coordinate della posizione corrente e restituisco essa.
  public Location getGpsPosition() {
    double latitude = mCurrentLocation.getLatitude();
    double longitude = mCurrentLocation.getLongitude();

    JsonUtils.addGPS(System.currentTimeMillis(), latitude, longitude);

    return mCurrentLocation;
  }

  // Setto le TextView passate come parametro con le coordinate della Location passata come parametro
  public static void setGPSView(Location l, TextView latitudeText, TextView longitudeText) {
    double latitude = l.getLatitude();
    double longitude = l.getLongitude();

    latitudeText.setText(String.valueOf(latitude));
    longitudeText.setText(String.valueOf(longitude));

  }

  // Controllo se il GPS è attivo
  public static boolean checkGPSisEnabled() {
    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }
}
