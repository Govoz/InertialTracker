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

    // Inizializzo il LocationManager e il LocationListener
    position = new gpsPosition(MainActivity.this);

    //--------------START BUTTON -----------------------------------------
    final Button start = (Button) findViewById(R.id.start_button);
    final Button stop = (Button) findViewById(R.id.stop_button);

    start.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //Controllo se il GPS è attivo
        boolean gpsStatus = gpsPosition.checkGPSisEnabled();
        if (!gpsStatus) {
          Toast.makeText(MainActivity.this, "Attiva il GPS", Toast.LENGTH_LONG).show();
        } else {

          Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();

          //Ottengo la gps position e aggiorno la label
          startGps = position.getGpsPosition();
          TextView latitudeText = (TextView) findViewById(R.id.latitudeGPS_start);
          TextView longitudeText = (TextView) findViewById(R.id.longitudeGPS_start);
          latitudeText.setVisibility(View.VISIBLE);
          longitudeText.setVisibility(View.VISIBLE);
          gpsPosition.setGPSView(startGps, latitudeText, longitudeText);

          //Avvio il service e comincia il log dei dati dai sensori
          Intent logDataStart = new Intent(MainActivity.this, logData.class);
          startService(logDataStart);

          //rendo clickabile il pulsante di stop e rende non clickabile quello di start
          start.setEnabled(false);
          stop.setEnabled(true);
          //Disabilito la possibilità di cambiare modalità
          MenuItem item = mMenu.findItem(R.id.action_changeMode);
          item.setEnabled(false);
          item.setVisible(false);
        }
      }
    });


    //--------------STOP BUTTON -----------------------------------------
    stop.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        //Controllo se il GPS è attivo
        boolean gpsStatus = gpsPosition.checkGPSisEnabled();
        if (!gpsStatus) {
          Toast.makeText(MainActivity.this, "Attiva il GPS", Toast.LENGTH_LONG).show();
        } else {
          //Ottengo la gps position e aggiorno la label
          stopGps = position.getGpsPosition();
          TextView latitudeText = (TextView) findViewById(R.id.latitudeGPS_stop);
          TextView longitudeText = (TextView) findViewById(R.id.longitudeGPS_stop);
          gpsPosition.setGPSView(stopGps, latitudeText, longitudeText);
          latitudeText.setVisibility(View.VISIBLE);
          longitudeText.setVisibility(View.VISIBLE);

          //Interrompo il service
          Intent logDataStart = new Intent(MainActivity.this, logData.class);
          stopService(logDataStart);

          //Disabilito il button Stop
          stop.setEnabled(false);

          //Compongo il fileName
          String latitudeStart = String.valueOf(startGps.getLatitude());
          String longitudeStart = String.valueOf(startGps.getLongitude());
          String latitudeStop = String.valueOf(stopGps.getLatitude());
          String longitudeStop = String.valueOf(stopGps.getLongitude());
          String fileName = JsonUtils.setFileName(latitudeStart, longitudeStart, latitudeStop, longitudeStop);

          //Salvo il jsonFile
          String jsonString = JsonUtils.save(MainActivity.this, fileName);

          //Mostro il jsonFile e rendo la textView scrollable
          TextView jsonTextView = (TextView) findViewById(R.id.jsonText);
          jsonTextView.setMovementMethod(new ScrollingMovementMethod());
          jsonTextView.setText(jsonString);

          //Mostro la label contenente il nome del file
          TextView labelFileName = (TextView) findViewById(R.id.filenameTextView);
          labelFileName.setText(fileName);

          //Mostro la horizontal rules
          View hr = (View) findViewById(R.id.hr);
          hr.setVisibility(View.VISIBLE);
        }
      }
    });

    //--------------GPS POSITION COORDINATES-----------------------------
    final TextView latitudeGPS_startTextView = (TextView) findViewById(R.id.latitudeGPS_start);
    final TextView longitudeGPS_startTextView = (TextView) findViewById(R.id.longitudeGPS_start);
    final TextView latitudeGPS_stopTextView = (TextView) findViewById(R.id.latitudeGPS_stop);
    final TextView longitudeGPS_stopTextView = (TextView) findViewById(R.id.longitudeGPS_stop);

    //Ogni coordinata la rendo clickabile, quando clickata parte l'intent verso GMaps
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
