package com.example.federico.inertialtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

	//startBool is used to implement singleton design pattern.
	boolean startBool = false;
	writeLogFile wlog = new writeLogFile();
	public static final String FILENAME = "fileLog";

	static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;

	//TAG is the process name
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Start Service
		final Button start = (Button) findViewById(R.id.start_button);
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!startBool) {
					Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
					startBool = true;

					// Get GPS Position
					getGpsPosition();

					// Start Services
					onStartService(v);

				} else {
					Toast.makeText(MainActivity.this, "Already Started", Toast.LENGTH_SHORT).show();
				}
			}
		});

		final Button view = (Button) findViewById(R.id.viewLog);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				readFile();
			}
		});
	}

	private void readFile() {
		FileInputStream fin = null;
		try {
			fin = openFileInput(FILENAME);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		InputStreamReader inputStreamReader = new InputStreamReader(fin);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
	}

	private void getGpsPosition() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions( MainActivity.this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
					MY_PERMISSION_ACCESS_COURSE_LOCATION);
		}

		locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 1, locationListener);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		long timestamp = location.getTime();

		String msg = String.valueOf(timestamp) + "-" + String.valueOf(latitude) + " - " + String.valueOf(longitude) + "/n";

		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

		//Write position in the file
		wlog.write(MainActivity.this, FILENAME, msg, Context.MODE_PRIVATE);
	}


	public void onStartService(View v) {
		Log.d(TAG, "onStartService(View);");

		Intent logDataStart = new Intent(this, logData.class);
		Intent checkSendStart = new Intent(this, checkSend.class);

		startService(logDataStart);
		startService(checkSendStart);
	}
}
