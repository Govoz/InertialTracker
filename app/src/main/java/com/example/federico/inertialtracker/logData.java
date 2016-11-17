package com.example.federico.inertialtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Federico on 15-Nov-16.
 *
 *
 */

public class logData extends ParallelIntentService {
	private static final String TAG = logData.class.getSimpleName();

	public logData() {
		// TAG is the name of process
		super(TAG);
	}

	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(Intent); Started, thread id: " + Thread.currentThread().getId());
		Log.d(TAG, "onHandleIntent(Intent); Sleeping for 1 second, thread id: " + Thread.currentThread().getId());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "onHandleIntent(Intent); Done, thread id: " + Thread.currentThread().getId());
	}
}
