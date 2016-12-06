package com.example.federico.inertialtracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.sql.Timestamp;
import java.util.List;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

import static com.example.federico.inertialtracker.JsonUtils.checkFrequency;


/**
 * Created by Federico on 15-Nov-16.
 *
 */

public class checkSend extends ParallelIntentService {

	private static final String TAG = checkSend.class.getSimpleName();
	static String URL = "http://192.168.1.2:80";
	private static final double FREQUENCY = 2e+9;
	long last_timestamp;
	long current_timestamp;

	public checkSend() {
		// TAG is the name of process
		super(TAG);
	}

	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(Intent); Started, thread id: " + Thread.currentThread().getId());
		try {

			current_timestamp = System.currentTimeMillis();
			if (checkFrequency(current_timestamp, last_timestamp)) {
				last_timestamp = current_timestamp;
				// Controllo se sono connesso
				if (checkConnection()) {

					Log.d("CheckConnection", "Connesso.");

					String fileString = JsonUtils.readJsonFile();
					sendData(fileString);
				}

			} else {
				Log.d("CheckConnection", "Non è possibile connettersi.");
			}

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	// Function return true if I am actually connected to Wifi Connection.
	public boolean checkConnection() {
		ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();

		// esiste una network di default
		if (activeNetwork != null) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				// controllo se sono connesso
				if (activeNetwork.isConnected()) {
					WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					WifiInfo networkInfo = wm.getConnectionInfo();

					writeWifiNetworkLog(networkInfo);
				}
				return true;
			}
		} else { //activeNetwork == null
			// cerco una rete a cui connettermi
			ScanResult wifiNetwork = searchFreeWifi();

			if (wifiNetwork != null) {
				// mi connetto
				connectToWifi(wifiNetwork);

				//ricontrollo la networkActive
				activeNetwork = connectManager.getActiveNetworkInfo();

				// se sono riuscito a collegarmi ad una rete
				if (activeNetwork != null && activeNetwork.isConnected()) {
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
		wc.SSID = "\"" + networkSSID + "\"";

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

		List<ScanResult> listScanResult = wifiManager.getScanResults();

		int maxPowerRSSI = 0;
		ScanResult wifiToConnect = null;

		for (ScanResult result : listScanResult) {
			String capability = result.capabilities;

			// Open Network
			if (!((capability.toUpperCase().contains("WEP")) ||
					capability.toUpperCase().contains("WPA"))) {

				//Search WiFi with max RSSI
				if (result.level > maxPowerRSSI) {
					wifiToConnect = result;
					maxPowerRSSI = result.level;
				}
			}
		}
		return wifiToConnect;
	}

	private void writeWifiNetworkLog(ScanResult wifiNetwork) {
		if (wifiNetwork != null) {
			long timestamp = System.currentTimeMillis();
			String bssid = wifiNetwork.BSSID;
			String ssid = wifiNetwork.SSID;
			int rssi = wifiNetwork.level;

			JsonUtils.addWifi(timestamp, bssid, ssid, rssi);
		}
	}

	private void writeWifiNetworkLog(WifiInfo wifiNetwork) {
		if (wifiNetwork != null) {
			long timestamp = System.currentTimeMillis();
			String bssid = wifiNetwork.getBSSID();
			String ssid = wifiNetwork.getSSID();
			int rssi = wifiNetwork.getRssi();

			JsonUtils.addWifi(timestamp, bssid, ssid, rssi);
		}
	}

	public void sendData(String dataFile) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		RequestParams data = new RequestParams();

		data.put("timestamp", timestamp);
		data.put("data", dataFile);

		HttpRequest.post(URL, data, new AsyncHttpResponseHandler() {

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

