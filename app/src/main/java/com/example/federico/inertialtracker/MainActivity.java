package com.example.federico.inertialtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

	//startBool is used to implement singleton design pattern.
	boolean startBool = false;

	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final getInitGps initGps = new getInitGps(this, MainActivity.super.getParent());

		// Start Service
		final Button start = (Button) findViewById(R.id.start_button);
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!startBool) {
					Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
					startBool = true;

					// Get GPS Position
					initGps.getGpsPosition(MainActivity.this, MainActivity.super.getParent());
					// Start Services
					onStartService(v);
				} else {
					Toast.makeText(MainActivity.this, "Already Started", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


	public void onStartService(View v) {
		Log.d(TAG, "onStartService(View);");

		Intent logDataStart = new Intent(this, logData.class);
		Intent checkSendStart = new Intent(this, checkSend.class);

		startService(logDataStart);
		startService(checkSendStart);
	}
}
