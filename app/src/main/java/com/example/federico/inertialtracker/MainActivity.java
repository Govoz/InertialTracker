package com.example.federico.inertialtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	//startBool is used to implement singleton design pattern.
	private boolean startBool = false;

	//TAG is the process name
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Start Service
		final Button start = (Button) findViewById(R.id.start_button);
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!startBool) {
					Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
					startBool = true;

					// Get GPS Position
					gpsPosition.getGpsPosition(MainActivity.this);

					// Start Services
					onStartService();

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
				Toast.makeText(MainActivity.this, JsonUtils.readJsonFile(), Toast.LENGTH_LONG).show();
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


	/*
	* Create two intents and start the services.
	 */
	public void onStartService() {
		Log.d(TAG, "onStartService(View);");

		Intent logDataStart = new Intent(this, logData.class);
		Intent checkSendStart = new Intent(this, checkSend.class);

		startService(logDataStart);
		startService(checkSendStart);
	}

}
