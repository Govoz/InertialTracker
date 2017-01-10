package com.example.federico.inertialtracker;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

  Button activate;
  Location startGps;
  gpsPosition position;
  Menu mMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    // Setto la toolbar come appBar in questa activity
    Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(toolbar);
    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    position = new gpsPosition(TestActivity.this);

    activate = (Button) findViewById(R.id.button_activate);
    activate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //Controllo se il GPS è attivo
        boolean gpsStatus = gpsPosition.checkGPSisEnabled();
        if (!gpsStatus) {
          Toast.makeText(TestActivity.this, "Attiva il GPS", Toast.LENGTH_LONG).show();
        } else {
          startGps = position.getGpsPosition();

          motionDetection();
          activate.setEnabled(false);

          MenuItem item = mMenu.findItem(R.id.action_changeMode);
          item.setEnabled(false);
          item.setVisible(false);
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
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  // When motionDetect() detect a motion start the services.
  public void motionDetection() {
    Toast.makeText(this, "MotionDetect attivato", Toast.LENGTH_SHORT).show();
    motionDetect md = new motionDetect(this);
  }
}
