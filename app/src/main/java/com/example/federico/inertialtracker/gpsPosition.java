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
 *
 */

class gpsPosition {

	private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;


	static Location getGpsPosition(Context c) {
		LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
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

		/*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {
			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		*/

		locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		JsonUtils.addGPS(System.currentTimeMillis(),latitude, longitude);

		return location;
	}

	public static void setGPSView(Location l, TextView latitudeText, TextView longitudeText){
		double latitude = l.getLatitude();
		double longitude = l.getLongitude();

		latitudeText.setText(String.valueOf(latitude));
		longitudeText.setText(String.valueOf(longitude));
	}
}
