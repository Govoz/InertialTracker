package com.example.federico.inertialtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.sql.Timestamp;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

	//startBool is used to implement singleton design pattern.
	boolean startBool = false;

	public static final String FILENAME = "fileLog.txt";

	static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;

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
					getGpsPosition();

					// Start Services
					onStartService();

				} else {
					Toast.makeText(MainActivity.this, "Already Started", Toast.LENGTH_SHORT).show();
				}
			}
		});


		//--------------TEST BUTTON --------------------------
		final Button viewData = (Button) findViewById(R.id.viewLog);
		viewData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JsonUtils.readJsonFile();
			}
		});

		final Button sendButton = (Button) findViewById(R.id.sendData);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFile();
			}
		});
	}


	private void getGpsPosition() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
					MY_PERMISSION_ACCESS_COURSE_LOCATION);
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {
			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		long timestamp = location.getTime();

		String msg = String.valueOf(timestamp) + "-" + String.valueOf(latitude) + " - " + String.valueOf(longitude) + "/n";

		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

		JsonUtils.addGPS(latitude, longitude);

	}


	public void onStartService() {
		Log.d(TAG, "onStartService(View);");

		Intent logDataStart = new Intent(this, logData.class);
		Intent checkSendStart = new Intent(this, checkSend.class);

		startService(logDataStart);
		startService(checkSendStart);
	}


//Funzione che ritorna il MimeType di un file, mi serviva per capire come sto trattando il mio file. (text/plain)

	public void fileType() {
		String type = getMimeType(FILENAME);
		Toast.makeText(this, type, Toast.LENGTH_SHORT).show();
	}

	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}

	public void sendFile() {

		String fileString = JsonUtils.readJsonFile();

		Toast.makeText(this, fileString, Toast.LENGTH_LONG).show();

		RequestParams params = new RequestParams();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		params.put("timestamp", timestamp);
		params.put("data", fileString);

		HttpRequest.post(checkSend.URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.d("Send", "Invio Eseguito");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.d("Send", "Invio Fallito");
			}
		});

	}

}
