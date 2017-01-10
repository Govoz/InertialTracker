package com.example.federico.inertialtracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static com.example.federico.inertialtracker.JsonUtils.checkFrequency;

/**
 * Created by Federico on 15-Nov-16.
 */

public class logData extends ParallelIntentService implements SensorEventListener {

  private static final String TAG = logData.class.getSimpleName();

  long current_timestamp;

  long last_timestampACC;
  long last_timestampMAGN;
  long last_timestampPRES;
  long last_timestampGIR;
  long last_timestampORIENT;

  SensorManager mSensorManager;
  Sensor accelerometer;
  Sensor magnetic_field;
  Sensor barometer;
  Sensor gyroscope;

  String typeSensor = "";

  //Use for getOrientation()
  float[] mGravity;
  float[] mGeomagnetic;

  List<Sensor> sensorList;

  public logData() {
    // TAG is the name of process
    super(TAG);
  }

  @Override
  public void onDestroy() {
    mSensorManager.unregisterListener(this);
    super.onDestroy();
  }

  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "onHandleIntent(Intent); Started, thread id: " + Thread.currentThread().getId());

    try {
      mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

      accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      magnetic_field = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
      barometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
      gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

      if (accelerometer != null)
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
      if (magnetic_field != null)
        mSensorManager.registerListener(this, magnetic_field, SensorManager.SENSOR_DELAY_NORMAL);
      if (barometer != null)
        mSensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
      if (gyroscope != null)
        mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    float xVal = event.values[0];
    float yVal = event.values[1];
    float zVal = event.values[2];

    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      current_timestamp = System.currentTimeMillis();
      if (checkFrequency(current_timestamp, last_timestampACC)) {
        last_timestampACC = current_timestamp;
        typeSensor = "ACC";

        //mGravity mi serve per il getOrientation()
        mGravity = event.values;

        String msg = typeSensor + " - " + String.valueOf(xVal) + " - " + String.valueOf(yVal) + " - " + String.valueOf(zVal);
        Log.d("Sensor", msg);

        JsonUtils.addValue(typeSensor, current_timestamp, xVal, yVal, zVal);
      }
    } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
      current_timestamp = System.currentTimeMillis();
      if (checkFrequency(current_timestamp, last_timestampGIR)) {
        last_timestampGIR = current_timestamp;
        typeSensor = "GIR";

        String msg = typeSensor + " - " + String.valueOf(xVal) + " - " + String.valueOf(yVal) + " - " + String.valueOf(zVal);
        Log.d("Sensor", msg);

        JsonUtils.addValue(typeSensor, current_timestamp, xVal, yVal, zVal);
      }
    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
      current_timestamp = System.currentTimeMillis();
      if (checkFrequency(current_timestamp, last_timestampMAGN)) {
        last_timestampMAGN = current_timestamp;

        double magnetic_strength_field = Math.sqrt(Math.pow(xVal, 2) + Math.pow(yVal, 2) + Math.pow(zVal, 2));
        double direction = (Math.atan2(xVal, yVal));

        if (direction < 0)
          direction += 2 * Math.PI;

        double directionDegree = direction * (180 / Math.PI);
        typeSensor = "MAGNETIC_FIELD";

        xVal = (float) magnetic_strength_field;
        yVal = (float) directionDegree;
        zVal = 0;

        //mGeomagnetic mi serve per il getOrientation()
        mGeomagnetic = event.values;

        String msg = typeSensor + " - " + String.valueOf(xVal) + " - " + String.valueOf(yVal) + " - " + String.valueOf(zVal);
        Log.d("Sensor", msg);

        JsonUtils.addValue(typeSensor, current_timestamp, xVal, yVal, zVal);
      }

    } else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
      current_timestamp = System.currentTimeMillis();
      if (checkFrequency(current_timestamp, last_timestampPRES)) {
        last_timestampPRES = current_timestamp;

        typeSensor = "PRESSURE";
        yVal = 0;
        zVal = 0;

        String msg = typeSensor + " - " + String.valueOf(xVal) + " - " + String.valueOf(yVal) + " - " + String.valueOf(zVal);
        Log.d("Sensor", msg);

        JsonUtils.addValue(typeSensor, current_timestamp, xVal, yVal, zVal);
      }
    }

    // getOrientation()
    if (mGravity != null && mGeomagnetic != null) {
      current_timestamp = System.currentTimeMillis();
      if (checkFrequency(current_timestamp, last_timestampORIENT)) {
        last_timestampORIENT = current_timestamp;
        float R[] = new float[9];
        float I[] = new float[9];

        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
        if (success) {
          float orientation[] = new float[3];
          SensorManager.getOrientation(R, orientation);

          double azimuth = Math.toDegrees(orientation[0]);
          if (azimuth < 0) {
            azimuth += 360;
          }

          Log.d("ORIENTATION", azimuth + " - " + orientation[1] + " - " + orientation[2]);
          typeSensor = "ORIENTATION";

          JsonUtils.addValue(typeSensor, current_timestamp, (float) azimuth, orientation[1], orientation[2]);
        }
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }


  //Printa la lista dei sensori disponibile nel device.
  public void printListSensor(SensorManager mSensorManager) {
    sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    StringBuilder msg = new StringBuilder();
    for (Sensor model : sensorList) {
      msg = msg.append(model.getName());

    }
    Log.d("LIST", msg.toString());
  }

}
