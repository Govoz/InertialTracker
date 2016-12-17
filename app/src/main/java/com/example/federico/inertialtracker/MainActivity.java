package com.example.federico.inertialtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

	//startBool is used to implement singleton design pattern.
	private boolean active = false;
	static final double FREQUENCY = 2000;
	private static String startGps;
	private static String stopGps;


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
					startGps = gpsPosition.getGpsPosition(MainActivity.this);

					motionDetection();

				} else {
					Toast.makeText(MainActivity.this, "Already Started", Toast.LENGTH_SHORT).show();
				}
			}
		});


		//--------------STOP BUTTON -----------------------------------------
		final Button stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//JsonUtils.largeLog("VIEWLOG", JsonUtils.readJsonFile());
				stopGps = gpsPosition.getGpsPosition(MainActivity.this);

				Intent logDataStart = new Intent(MainActivity.this, logData.class);
				Intent checkSendStart = new Intent(MainActivity.this, checkSend.class);

				MainActivity.this.stopService(logDataStart);
				MainActivity.this.stopService(checkSendStart);
			}
		});

		//--------------SAVE BUTTON -----------------------------------------
		final Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {
			@Override

			// Test send Json to server.
			public void onClick(View v) {
				save();
			}
		});
	}

	private void save() {
		JSONObject json = JsonUtils.prepareToSend();
		String fileName = setFileName();
		Toast.makeText(MainActivity.this, fileName, Toast.LENGTH_SHORT).show();

		File file = new File(getExternalFilesDir(null), fileName);

		String jsonString = json.toString();

		Toast.makeText(this,jsonString, Toast.LENGTH_SHORT).show();

		try {
			FileOutputStream fOut = new FileOutputStream(file, true);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(jsonString);

			myOutWriter.close();
			fOut.close();

			Toast.makeText(MainActivity.this, "Salvato", Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String setFileName() {
		String fileName = startGps + "_" + stopGps + ".txt";
		return fileName;
	}

	// When motionDetect() detect a motion start the services.
	public void motionDetection() {
		Toast.makeText(this, "MotionDetect attivato", Toast.LENGTH_SHORT).show();
		motionDetect md = new motionDetect(this);
	}

}
