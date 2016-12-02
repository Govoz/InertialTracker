package com.example.federico.inertialtracker;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Federico on 02-Dec-16.
 */

public class motionDetect implements SensorEventListener {

	private static final double SENSIBILITY = 1;

	private static SensorManager mSensorManager;
	private Sensor accelerometer;
	private double mAccelCurrent;
	private double mAccelLast;
	private Context context;

	public motionDetect(Context c){
		context = c;
		mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

			// Shake detection
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];

			// g-force
			mAccelCurrent = Math.sqrt(x*x + y*y + z*z);

			double delta = mAccelCurrent - mAccelLast;

			if(delta > SENSIBILITY){
				Toast.makeText(context , "MOTION DETECTED", Toast.LENGTH_SHORT).show();
				// Start Services
				onStartService(context);
				this.stop();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void stop(){
		mSensorManager.unregisterListener(this);
	}

	public void onStartService(Context c) {
		Toast.makeText(c, "START SERVICE", Toast.LENGTH_SHORT).show();

		Intent logDataStart = new Intent(c, logData.class);
		Intent checkSendStart = new Intent(c, checkSend.class);

		c.startService(logDataStart);
		c.startService(checkSendStart);
	}

}


