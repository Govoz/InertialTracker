package com.example.federico.inertialtracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.List;

/**
 * Created by Federico on 15-Nov-16.
 *
 */

public class logData extends ParallelIntentService implements SensorEventListener {

	private static final String TAG = logData.class.getSimpleName();

	long last_timestampACC = 0;
	long last_timestampMAGN = 0;
	long last_timestampGIR = 0;
	long last_timestampPRESS = 0;
	long last_timestampROT = 0;

	String typeSensor = "";

	private static final double FREQUENCY = 2e+9;

	List<Sensor> sensorList;

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

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			long current_timestamp = event.timestamp;
			float xVal = event.values[0];
			float yVal = event.values[1];
			float zVal = event.values[2];

			if (checkFrequency(current_timestamp, last_timestampACC)) {
				last_timestampACC = current_timestamp;

				typeSensor = "ACC";

				JsonUtils.addValue(typeSensor,current_timestamp, xVal, yVal, zVal);
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

				typeSensor = "MAGNETIC_FIELD";

				JsonUtils.addValue(typeSensor, current_timestamp, magnetic_strength_field, directionDegree, 0);
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

				typeSensor = "GEO_ROT_VECT";
				JsonUtils.addValue(typeSensor,current_timestamp, xVal, yVal, zVal);
			}
		}

		if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
			long current_timestamp = event.timestamp;
			float val = event.values[0];

			if (checkFrequency(current_timestamp, last_timestampPRESS)) {
				last_timestampPRESS = current_timestamp;

				typeSensor = "PRESSURE";
				JsonUtils.addValue(typeSensor,current_timestamp, val, 0, 0);
			}
		}

		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			long current_timestamp = event.timestamp;
			float xVal = event.values[0];
			float yVal = event.values[1];
			float zVal = event.values[2];

			if (checkFrequency(current_timestamp, last_timestampPRESS)) {
				last_timestampROT = current_timestamp;

				typeSensor = "ROT_VECT";
				JsonUtils.addValue(typeSensor, current_timestamp, xVal, yVal, zVal);
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public boolean checkFrequency(long current, long last) {
		return current - last > FREQUENCY;
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
