package com.example.federico.inertialtracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.sql.Timestamp;
import java.util.List;

import com.loopj.android.http.*;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.federico.inertialtracker.JsonUtils.checkFrequency;


/**
 * checkSend è un service e si occupa di controllare se è presente una connessione wifi open e
 * manda il json al server.
 */

public class checkSend extends ParallelIntentService {

  private static final String TAG = checkSend.class.getSimpleName();
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

        // Controllo se sono connesso o se mi è possibile farlo
        if (checkConnection()) {
          Log.d("CheckConnection", "Connesso.");

          JSONObject toSendJSON = JsonUtils.prepareToSend();
          // Invio il file al server.
          try {
            sendData(toSendJSON);
          } catch (Exception e){
            e.printStackTrace();
          }
        }
      } else {
        Log.d("CheckConnection", "Non è possibile connettersi.");
      }

      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  // checkConnection restituisce True se sono attualmente connesso.
  public boolean checkConnection() {
    ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();

    // Esiste una network di default ed è di tipo WiFi.
    if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

      // Controllo se sono connesso
      if (activeNetwork.isConnected()) {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Ottengo le informazioni della network e le scrivo nel JSON.
        WifiInfo networkInfo = wm.getConnectionInfo();
        writeWifiNetworkLog(networkInfo);
      }
      return true;

    } else {
      // Cerco una rete di tipo Open a cui collegarmi
      ScanResult wifiNetwork = searchFreeWifi();
      Log.d("TEST", wifiNetwork.toString());
      if (wifiNetwork != null) {
        // mi connetto ad essa
        Log.d("TEST", wifiNetwork.toString());
        connectToWifi(wifiNetwork);

        // ricontrollo la networkActive
        activeNetwork = connectManager.getActiveNetworkInfo();

        // se sono riuscito a collegarmi ad una rete invio le informazioni di essa al Json
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

  // searchFreeWifi cera una rete wifi open con il massimo valore di RSSI.
  private ScanResult searchFreeWifi() {
    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    // Scansiono tramite il wifiManager la lista delle rete wifi disponibili
    List<ScanResult> listScanResult = wifiManager.getScanResults();

    int maxPowerRSSI = -100;
    // E' possibile anche non trovarne affatto
    ScanResult wifiToConnect = null;

    for (ScanResult result : listScanResult) {
      // Per ogni ScanResult ottengo le politiche di accesso relative.
      String capability = result.capabilities;

      // Controllo che siano di tipo Open
      if (!(capability.toUpperCase().contains("WEP")) && !(capability.toUpperCase().contains("WPA"))) {

        // Cerca la rete con il massimo valore di RSSI.
        if (result.level > maxPowerRSSI) {
          wifiToConnect = result;
          maxPowerRSSI = result.level;
        }
      }
    }
    return wifiToConnect;
  }


  // connectToWifi prova a connettersi alla rete passata come parametro.
  private void connectToWifi(ScanResult wifiNetwork) {
    // Mi creo una nuova configurazion e imposto nessuna key di accesso.
    WifiConfiguration wc = new WifiConfiguration();
    String networkSSID = wifiNetwork.SSID;
    wc.SSID = "\"" + networkSSID + "\"";
    wc.BSSID = wifiNetwork.BSSID;

    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

    WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    // Aggiungo la configurazione alla lista delle network. netID in caso di fallimento è -1
    int netID = wm.addNetwork(wc);
    Log.d("TEST", Integer.toString(netID));

    // Nel caso in cui io sia riuscito ad aggiungere la nuova network. Disconnetto il wifiManager,
    // abilito la nuova rete e mi riconnetto.
    if (netID != -1) {
      wm.disconnect();
      wm.enableNetwork(netID, true);
      wm.reconnect();
    }
  }

  // writeWifiNetworkLog sono due metodi che mi servono per ottenere le informazioni di una determinata
  // rete e aggiungerle al Json.
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

  // sendData in caso di connessione disponibile invia il dataJson al server.
  public void sendData(JSONObject dataJson) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    // Invio sia il json che il timestamp.
    RequestParams data = new RequestParams();
    data.setUseJsonStreamer(true);
    data.put("timestamp", timestamp);
    data.put("data", dataJson);

    try {
      HttpRequest.post(parameters.URL, data, new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
          Log.d("Send", "Invio Eseguito");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
          Log.d("Send", "Invio Fallito");
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}