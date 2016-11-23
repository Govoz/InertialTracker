package com.example.federico.inertialtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
				sendData();
			}

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
					if(networkInfo != null){
						writeWifiNetworkLog(networkInfo);
					}
				}
				return true;
			}
		}
		else { //activeNetwork == null
			// cerco una rete a cui connettermi
			ScanResult wifiNetwork = searchFreeWifi();

			// mi connetto
			connectToWifi(wifiNetwork);

			//ricontrollo la networkActive
			activeNetwork = connectManager.getActiveNetworkInfo();

			// se sono riuscito a collegarmi ad una rete
			if(activeNetwork != null){
				if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
					writeWifiNetworkLog(wifiNetwork);
					return true;
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
		writeLogFile wlog = new writeLogFile();

		String msg = "WIFI - " +
				"TimeStamp : " + wifiNetwork.timestamp + " /n" +
				"BSSID : " + wifiNetwork.BSSID + " /n" +
				"SSID : " + wifiNetwork.SSID + " /n" +
				"RSSI : " + wifiNetwork.level + " /n";

		wlog.write(this, MainActivity.FILENAME, msg, Context.MODE_APPEND);
	}

	private void writeWifiNetworkLog(WifiInfo wifiNetwork) {
		writeLogFile wlog = new writeLogFile();

		String msg = "WIFI - " +
				"TimeStamp : " + System.currentTimeMillis() + " /n" +
				"BSSID : " + wifiNetwork.getBSSID() + " /n" +
				"SSID : " + wifiNetwork.getSSID() + " /n" +
				"RSSI : " + wifiNetwork.getRssi() + " /n";

		wlog.write(this, MainActivity.FILENAME, msg, Context.MODE_APPEND);
	}

	public void sendData(){

	}

}

