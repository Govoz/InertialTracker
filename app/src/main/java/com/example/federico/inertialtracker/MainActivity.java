package com.example.federico.inertialtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	//startBool is used to implement singleton design pattern.
	private boolean active = false;
	static final double FREQUENCY = 2000;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Start Service
		final Button start = (Button) findViewById(R.id.start_button);
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!active) {
					Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
					active = true;

					// Get GPS Position
					gpsPosition.getGpsPosition(MainActivity.this);

					motionDetection();

				} else {
					Toast.makeText(MainActivity.this, "Already Started", Toast.LENGTH_SHORT).show();
				}
			}
		});


		//--------------TEST BUTTON -----------------------------------------
		final Button viewData = (Button) findViewById(R.id.viewLog);
		viewData.setOnClickListener(new View.OnClickListener() {

			// Make a toast and Log.d with json file.
			@Override
			public void onClick(View v) {
				//Toast.makeText(MainActivity.this, JsonUtils.readJsonFile(), Toast.LENGTH_LONG).show();
				JsonUtils.largeLog("VIEWLOG", JsonUtils.readJsonFile());
			}
		});

		final Button sendButton = (Button) findViewById(R.id.sendData);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override

			// Test send Json to server.
			public void onClick(View v) {

			}
		});
	}

	// When motionDetect() detect a motion start the services.
	public void motionDetection() {
		Toast.makeText(this, "MotionDetect attivato", Toast.LENGTH_SHORT).show();
		motionDetect md = new motionDetect(this);
	}

}
