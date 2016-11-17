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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Federico on 16-Nov-16.
 */

public class getInitGps implements LocationListener {

	private LocationManager locationManager;
	static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;

	public getInitGps(Context c, Activity a) {
		locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(a, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
					MY_PERMISSION_ACCESS_COURSE_LOCATION);
		}
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, (LocationListener) this);
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	public void getGpsPosition(Context c, Activity a){
		LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

		if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(a, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
					MY_PERMISSION_ACCESS_COURSE_LOCATION);
		}
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		String msg = "New Latitude: " + location.getLatitude()
				+ "New Longitude: " + location.getLongitude();

		Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
	}
}

// TEST