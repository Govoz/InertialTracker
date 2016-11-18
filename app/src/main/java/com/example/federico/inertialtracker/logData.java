package com.example.federico.inertialtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static java.lang.Enum.valueOf;

/**
 * Created by Federico on 15-Nov-16.
 *
 *
 */

public class logData extends ParallelIntentService implements SensorEventListener {

	private static final String TAG = logData.class.getSimpleName();
	long last_timestampACC = 0;
	long last_timestampMAGN = 0;
	long last_timestampGIR = 0;

	List<Sensor> sensorList;

	public logData() {
		// TAG is the name of process
		super(TAG);
	}

	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(Intent); Started, thread id: " + Thread.currentThread().getId());
	//	Log.d(TAG, "onHandleIntent(Intent); Sleeping for 1 second, thread id: " + Thread.currentThread().getId());
		try {
			SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//			StringBuilder msg = new StringBuilder();
//			for(Sensor model : sensorList) {
//				msg = msg.append(model.getName());
//
//			}
//			Log.d("LIST" , msg.toString());

			mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
	//		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED), SensorManager.SENSOR_DELAY_NORMAL);

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	//	Log.d(TAG, "onHandleIntent(Intent); Done, thread id: " + Thread.currentThread().getId());
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			long current_timestamp = event.timestamp;
			float xVal = event.values[0];
			float yVal = event.values[1];
			float zVal = event.values[2];

			if(current_timestamp - last_timestampACC > 2e+9){
				last_timestampACC = current_timestamp;
				Log.d("ACC", xVal + " - " + yVal + " - " + zVal);
			}
		}
		 if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

			 long current_timestamp = event.timestamp;
			 float xVal = event.values[0];
			 float yVal = event.values[1];
			 float zVal = event.values[2];

			 if(current_timestamp - last_timestampMAGN > 2e+9){
				 last_timestampMAGN = current_timestamp;
				 Log.d("MAGNETIC", xVal + " - " + yVal + " - " + zVal);
			 }
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {

			long current_timestamp = event.timestamp;
			float xVal = event.values[0];
			float yVal = event.values[1];
			float zVal = event.values[2];

			if(current_timestamp - last_timestampGIR > 2e+9){
				last_timestampGIR = current_timestamp;
				Log.d("GYROSCOPE", xVal + " - " + yVal + " - " + zVal);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
