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
 * Created by Federico on 30-Nov-16.
 */

class gpsPosition {
  private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
  static Location mCurrentLocation;
  static LocationManager locationManager;

  gpsPosition(Context c){
    locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
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
              MY_PERMISSION_ACCESS_COURSE_LOCATION);
    }

    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    if (isGPSEnabled) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
      locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

      mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
  }

  public Location getGpsPosition() {
    if (mCurrentLocation != null) {
      double latitude = mCurrentLocation.getLatitude();
      double longitude = mCurrentLocation.getLongitude();

      JsonUtils.addGPS(System.currentTimeMillis(), latitude, longitude);
    }

    return mCurrentLocation;
  }

  public static void setGPSView(Location l, TextView latitudeText, TextView longitudeText) {
    double latitude = l.getLatitude();
    double longitude = l.getLongitude();

    latitudeText.setText(String.valueOf(latitude));
    longitudeText.setText(String.valueOf(longitude));
  }
}
