package com.example.federico.inertialtracker;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  private static Location startGps;
  private static Location stopGps;
  gpsPosition position;
  Menu mMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Setto la toolbar come appBar in questa activity
    Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(toolbar);
    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    position = new gpsPosition(MainActivity.this);

    //--------------START BUTTON -----------------------------------------
    final Button start = (Button) findViewById(R.id.start_button);
    final Button stop = (Button) findViewById(R.id.stop_button);

    start.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();

        // Get GPS Position && set Label
        startGps = position.getGpsPosition();
        TextView latitudeText = (TextView) findViewById(R.id.latitudeGPS_start);
        TextView longitudeText = (TextView) findViewById(R.id.longitudeGPS_start);

        latitudeText.setVisibility(View.VISIBLE);
        longitudeText.setVisibility(View.VISIBLE);

        gpsPosition.setGPSView(startGps, latitudeText, longitudeText);

        //motionDetection();

        Intent logDataStart = new Intent(MainActivity.this, logData.class);
        Intent checkSendStart = new Intent(MainActivity.this, checkSend.class);

        startService(logDataStart);
        startService(checkSendStart);

        //rendo clickabile il pulsante di stop e rende non clickabile quello di start
        start.setEnabled(false);
        stop.setEnabled(true);
        MenuItem item = mMenu.findItem(R.id.action_changeMode);
        item.setEnabled(false);
        item.setVisible(false);
      }
    });


    //--------------STOP BUTTON -----------------------------------------
    stop.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        stopGps = position.getGpsPosition();

        TextView latitudeText = (TextView) findViewById(R.id.latitudeGPS_stop);
        TextView longitudeText = (TextView) findViewById(R.id.longitudeGPS_stop);

        gpsPosition.setGPSView(stopGps, latitudeText, longitudeText);

        latitudeText.setVisibility(View.VISIBLE);
        longitudeText.setVisibility(View.VISIBLE);

        Intent logDataStart = new Intent(MainActivity.this, logData.class);
        Intent checkSendStart = new Intent(MainActivity.this, checkSend.class);

        stopService(logDataStart);
        stopService(checkSendStart);

        stop.setEnabled(false);

        String latitudeStart = String.valueOf(startGps.getLatitude());
        String longitudeStart = String.valueOf(startGps.getLongitude());
        String latitudeStop = String.valueOf(stopGps.getLatitude());
        String longitudeStop = String.valueOf(stopGps.getLongitude());
        String fileName = JsonUtils.setFileName(latitudeStart, longitudeStart, latitudeStop, longitudeStop);

        String jsonString = JsonUtils.save(MainActivity.this, fileName);

        //set textView
        TextView jsonTextView = (TextView) findViewById(R.id.jsonText);
        jsonTextView.setMovementMethod(new ScrollingMovementMethod());
        jsonTextView.setText(jsonString);

        TextView labelFileName = (TextView) findViewById(R.id.filenameTextView);
        labelFileName.setText(fileName);

        View hr = (View) findViewById(R.id.hr);
        hr.setVisibility(View.VISIBLE);
      }
    });

    //--------------GPS POSITION COORDINATES-----------------------------
    final TextView latitudeGPS_startTextView = (TextView) findViewById(R.id.latitudeGPS_start);
    final TextView longitudeGPS_startTextView = (TextView) findViewById(R.id.longitudeGPS_start);
    final TextView latitudeGPS_stopTextView = (TextView) findViewById(R.id.latitudeGPS_stop);
    final TextView longitudeGPS_stopTextView = (TextView) findViewById(R.id.longitudeGPS_stop);


    latitudeGPS_startTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String lat = latitudeGPS_startTextView.getText().toString();
        String lon = longitudeGPS_startTextView.getText().toString();

        Uri location = Uri.parse("geo:" + lat + "," + lon);

        Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW, location);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
          startActivity(intent);
        }
      }
    });

    longitudeGPS_startTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String lat = latitudeGPS_startTextView.getText().toString();
        String lon = longitudeGPS_startTextView.getText().toString();

        Uri location = Uri.parse("geo:" + lat + "," + lon);

        Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW, location);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
          startActivity(intent);
        }
      }
    });

    latitudeGPS_stopTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String lat = latitudeGPS_stopTextView.getText().toString();
        String lon = longitudeGPS_stopTextView.getText().toString();

        Uri location = Uri.parse("geo:" + lat + "," + lon);

        Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW, location);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
          startActivity(intent);
        }
      }
    });

    longitudeGPS_stopTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String lat = latitudeGPS_stopTextView.getText().toString();
        String lon = longitudeGPS_stopTextView.getText().toString();

        Uri location = Uri.parse("geo:" + lat + "," + lon);

        Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW, location);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
          startActivity(intent);
        }
      }
    });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.app_bar, menu);
    mMenu = menu;
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_changeMode: {
        Intent i = new Intent(this, TestActivity.class);
        startActivity(i);
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
