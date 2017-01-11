package com.example.federico.inertialtracker;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

/**
 * Created by Federico on 02-Dec-16.
 *
 */

class motionDetect implements SensorEventListener {


  private static SensorManager mSensorManager;
  private double mGravity;
  private Context context;

  motionDetect(Context c) {
    context = c;
    mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
    Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

    mGravity = SensorManager.GRAVITY_EARTH;
  }


  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

      // Shake detection
      double x = event.values[0];
      double y = event.values[1];
      double z = event.values[2];

      // magnitude
      double magnitude = Math.sqrt(x * x + y * y + z * z);

      double delta = magnitude - mGravity;

      if (delta > parameters.SENSIBILITY) {
        Toast.makeText(context, "MOTION DETECTED", Toast.LENGTH_SHORT).show();
        // Start Services
        onStartService(context);

        mSensorManager.unregisterListener(this);
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  private void onStartService(Context c) {
    Intent logDataStart = new Intent(c, logData.class);
    Intent checkSendStart = new Intent(c, checkSend.class);

    c.startService(logDataStart);
    c.startService(checkSendStart);
  }

}


