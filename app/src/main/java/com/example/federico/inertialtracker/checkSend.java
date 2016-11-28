package com.example.federico.inertialtracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;


/**
 * Created by Federico on 15-Nov-16.
 *
 *
 */

public class checkSend extends ParallelIntentService {

	private static final String TAG = checkSend.class.getSimpleName();

	public checkSend() {
		// TAG is the name of process
		super(TAG);
	}

	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(Intent); Started, thread id: " + Thread.currentThread().getId());
		try {

			// Controllo se sono connesso
			if(checkConnection()){
				Log.d("SEND", "SI");
				//sendData();
			}
			else
				Log.d("SEND", "NO");

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	// Function return true if I am actually connected to Wifi Connection.
	public boolean checkConnection(){
		ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();

		// esiste una network di default
		if(activeNetwork != null){
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
				// controllo se sono connesso
				if(activeNetwork.isConnected()){
					WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					WifiInfo networkInfo = wm.getConnectionInfo();

					writeWifiNetworkLog(networkInfo);
				}
				return true;
			}
		}
		else { //activeNetwork == null
			// cerco una rete a cui connettermi
			ScanResult wifiNetwork = searchFreeWifi();

			if(wifiNetwork!= null) {
				// mi connetto
				connectToWifi(wifiNetwork);

				//ricontrollo la networkActive
				activeNetwork = connectManager.getActiveNetworkInfo();

				// se sono riuscito a collegarmi ad una rete
				if (activeNetwork != null) {
					if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
						writeWifiNetworkLog(wifiNetwork);
						return true;
					}
				}
			}
		}
		return false;
	}

	// Provo a connettermi a wifiNetwork (Open Wifi)
	private boolean connectToWifi(ScanResult wifiNetwork) {
		WifiConfiguration wc = new WifiConfiguration();
		String networkSSID = wifiNetwork.SSID;
		wc.SSID = "\"" + networkSSID +"\"";

		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		int netID = wm.addNetwork(wc);
		wm.disconnect();
		wm.enableNetwork(netID, true);
		return (wm.reconnect());
	}


	// Cerco la rete Open con massimo power RSSI
	private ScanResult searchFreeWifi() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		List <ScanResult> listScanResult = wifiManager.getScanResults();

		int maxPowerRSSI = 0;
		ScanResult wifiToConnect = null;

		for (ScanResult result : listScanResult){
			String capability = result.capabilities;

			// Open Network
			if(!((capability.toUpperCase().contains("WEP")) ||
					capability.toUpperCase().contains("WPA"))){

				//Search WiFi with max RSSI
				if(result.level > maxPowerRSSI){
					wifiToConnect = result;
					maxPowerRSSI = result.level;
				}
			}
		}

		return wifiToConnect;
	}

	private void writeWifiNetworkLog(ScanResult wifiNetwork) {
		if(wifiNetwork != null) {
			writeLogFile wlog = new writeLogFile();

			StringBuilder log = new StringBuilder(5);

			log.append("WIFI - ");
			log.append("TimeStamp : ").append(System.currentTimeMillis()).append(" /n");

			if(wifiNetwork.BSSID != null) {
				log.append("BSSID : ").append(wifiNetwork.BSSID).append(" /n");
			}
			if(wifiNetwork.SSID != null) {
				log.append("SSID : ").append(wifiNetwork.SSID).append(" /n");
			}
			log.append("RSSI : ").append(wifiNetwork.level).append(" /n");

			wlog.write(this, MainActivity.FILENAME, log.toString(), Context.MODE_APPEND);
		}
	}

	private void writeWifiNetworkLog(WifiInfo wifiNetwork) {
		if(wifiNetwork != null) {
			writeLogFile wlog = new writeLogFile();

			StringBuilder log = new StringBuilder(5);

			log.append("WIFI - ");
			log.append("TimeStamp : ").append(System.currentTimeMillis()).append(" /n");

			if(wifiNetwork.getBSSID() != null) {
				log.append("BSSID : ").append(wifiNetwork.getBSSID()).append(" /n");
			}
			if(wifiNetwork.getSSID() != null) {
				log.append("SSID : ").append(wifiNetwork.getSSID()).append(" /n");
			}
			log.append("RSSI : ").append(wifiNetwork.getRssi()).append(" /n");

			wlog.write(this, MainActivity.FILENAME, log.toString(), Context.MODE_APPEND);
		}
	}

	HttpRequest httpRequest = new HttpRequest();

	public void sendData() {

		File file = new File(getApplicationContext().getFilesDir(), MainActivity.FILENAME);
		if (file != null) {
			RequestParams params = new RequestParams();
			try {
				params.put("Device", "InertialTracker");
				params.put("file", file, "");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			FileAsyncHttpResponseHandler handler = new FileAsyncHttpResponseHandler(this) {

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, File file) {
					Log.d("sendData", "Server Error.");
				}

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, File file) {
					Log.d("sendData", "Server Success.");
				}
			};

			//handler.setUseSynchronousMode(true);

			httpRequest.post(params, handler);
		}
		else
			Log.d("sendData","File NULL");
	}

}

