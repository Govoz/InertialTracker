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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static java.lang.Enum.valueOf;

/**
 * Created by Federico on 15-Nov-16.
 */

public class logData extends ParallelIntentService implements SensorEventListener {

	private static final String TAG = logData.class.getSimpleName();
	long last_timestampACC = 0;
	long last_timestampMAGN = 0;
	long last_timestampGIR = 0;
	long last_timestampPRESS = 0;
	long last_timestampROT = 0;

	private static final double FREQUENCY = 2e+9;

	List<Sensor> sensorList;
	String FILENAME = "logFile";


	public logData() {
		// TAG is the name of process
		super(TAG);
	}

	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "onHandleIntent(Intent); Started, thread id: " + Thread.currentThread().getId());

		try {
			SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

			//printListSensor(mSensorManager);

			Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			Sensor magnetic_field = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			Sensor gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
			Sensor barometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			Sensor rotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

			if (accelerometer != null)
				mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			if (magnetic_field != null)
				mSensorManager.registerListener(this, magnetic_field, SensorManager.SENSOR_DELAY_NORMAL);
			if (gyroscope != null)
				mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
			if (barometer != null)
				mSensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
			if (rotation != null)
				mSensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL);

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		try {
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);


			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				long current_timestamp = event.timestamp;
				float xVal = event.values[0];
				float yVal = event.values[1];
				float zVal = event.values[2];

				if (checkFrequency(current_timestamp, last_timestampACC)) {
					last_timestampACC = current_timestamp;

					//siste
					String msg = current_timestamp + " - " + "ACC" + " - " + xVal + " - " + yVal + " - " + zVal + "\n";
					fos.write(msg.getBytes());
					Log.d("MSG", msg);
				}
			}
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

				long current_timestamp = event.timestamp;
				float xVal = event.values[0];
				float yVal = event.values[1];
				float zVal = event.values[2];

				double magnetic_strength_field = Math.sqrt(Math.pow(xVal, 2) + Math.pow(yVal, 2) + Math.pow(zVal, 2));
				double direction = (Math.atan2(xVal, yVal));
				if (direction < 0)
					direction += 2 * Math.PI;
				double directionDegree = direction * (180 / Math.PI);

				if (checkFrequency(current_timestamp, last_timestampMAGN)) {
					last_timestampMAGN = current_timestamp;
					//Log.d("MAGNETIC FIELD", magnetic_strength_field + " - " + directionDegree);
				}
			}

			//using a magnetometer instead of using a gyroscope.
			if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {

				long current_timestamp = event.timestamp;
				float xVal = event.values[0];
				float yVal = event.values[1];
				float zVal = event.values[2];

				if (checkFrequency(current_timestamp, last_timestampGIR)) {
					last_timestampGIR = current_timestamp;
					//Log.d("GYROSCOPE", xVal + " - " + yVal + " - " + zVal);
				}
			}

			if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
				long current_timestamp = event.timestamp;
				float val = event.values[0];

				if (checkFrequency(current_timestamp, last_timestampPRESS)) {
					last_timestampPRESS = current_timestamp;
					//Log.d("PRESSURE", String.valueOf(val));
				}
			}

			if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
				long current_timestamp = event.timestamp;
				float xVal = event.values[0];
				float yVal = event.values[1];
				float zVal = event.values[2];

				if (checkFrequency(current_timestamp, last_timestampPRESS)) {
					last_timestampROT = current_timestamp;
					//Log.d("ROTATION", xVal + " - " + yVal + " " + zVal);
				}
			}

			//scrivere il msg direttamente qua. Fare funzione che da getType ti da la stringa del sensore.
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public boolean checkFrequency(long current, long last) {
		if (current - last > FREQUENCY)
			return true;
		else
			return false;
	}

	public void printListSensor(SensorManager mSensorManager) {
		sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		StringBuilder msg = new StringBuilder();
		for (Sensor model : sensorList) {
			msg = msg.append(model.getName());

		}
		Log.d("LIST", msg.toString());
	}

}
