package com.example.federico.inertialtracker;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JsonUtils è una classe che serve per gestire il file Json che contiene tutti i dati prelevati dai
 * sensori.
 */

class JsonUtils {

  private static JSONObject file = new JSONObject();

  private static JSONArray gps = new JSONArray();
  private static JSONArray wifi = new JSONArray();
  private static JSONArray acc = new JSONArray();
  private static JSONArray gir = new JSONArray();
  private static JSONArray orientation = new JSONArray();
  private static JSONArray magnetic = new JSONArray();
  private static JSONArray pressure = new JSONArray();

  static void addGPS(long timestamp, Double lat, Double longit) {
    try {
      JSONObject obj = new JSONObject();
      obj.put("Timestamp", timestamp);
      obj.put("latitude", lat);
      obj.put("longitude", longit);

      gps.put(obj);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  static void addWifi(long timestamp, String bssid, String ssid, int rssi) {
    try {
      JSONObject obj = new JSONObject();
      obj.put("Timestamp", timestamp);
      obj.put("Bssid", bssid);
      obj.put("Ssid", ssid);
      obj.put("Rssi", rssi);

      wifi.put(obj);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  static void addValue(String type, long timestamp, float x, float y, float z) {
    try {
      JSONObject obj = new JSONObject();
      obj.put("timestamp", timestamp);

      switch (type) {
        case "ACC":
          try {
            obj.put("x", x);
            obj.put("y", y);
            obj.put("z", z);

            acc.put(obj);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          break;

        case "GIR":
          try {
            obj.put("x", x);
            obj.put("y", y);
            obj.put("z", z);

            gir.put(obj);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          break;

        case "MAGNETIC_FIELD":
          try {
            obj.put("magnetic_strength_field", x);
            obj.put("directionDegree", y);

            magnetic.put(obj);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          break;

        case "PRESSURE":
          try {
            obj.put("val", x);

            pressure.put(obj);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          break;

        case "ORIENTATION":
          try {
            obj.put("azimuth", x);
            obj.put("pitch", y);
            obj.put("roll", z);

            orientation.put(obj);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          break;

        default:
          break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  // Prendo i JSONArray e li inserisco nel JSONObject.
  private static void prepareJson() {
    try {
      file.put("Gps", gps);
      file.put("Acceleration", acc);
      file.put("Gyroscope", gir);
      file.put("MagneticField", magnetic);
      file.put("Pressure", pressure);
      file.put("WiFi", wifi);
      file.put("Orientation", orientation);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  // prepareToSend chiama prepareJson e mi ritorna il JSONObject corrispondente al file.
  static JSONObject prepareToSend() {
    prepareJson();
    return file;
  }

  // checkFrequency restituisce true se la differenza tra i due timestamp "current" e "last" è superiore
  // alla FREQUENCY.
  public static boolean checkFrequency(long current, long last) {
    return current - last > parameters.FREQUENCY;
  }

  // timeStampReadable trasforma il timestamp passato come parametro in un formato human-readable.
  public static String timeStampReadable(long timestamp) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
    return dateFormat.format(new Date(timestamp));
  }

  // save serve per salvare manualmente il file di nome "fileName" nella memoria del device.
  public static String save(Context c, String fileName) {

    JSONObject json = JsonUtils.prepareToSend();

    // Creo un file nella directory corrispondente e trasformo il JsonObject in stringa.
    File file = new File(c.getExternalFilesDir(null), fileName);
    String jsonString = json.toString();

    // Creo un FileOutputStream e un OutputStreamWriter e ci appendo la stringa corrispondente al json.
    try {
      FileOutputStream fOut = new FileOutputStream(file, true);
      OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
      myOutWriter.append(jsonString);

      myOutWriter.close();
      fOut.close();

      Toast.makeText(c, "Salvato", Toast.LENGTH_SHORT).show();

      return jsonString;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  // Crea la stringa che indica il nome del file in questa maniera: "latitudeStart_longitudeStart__latitudeStop_longitudeStop_timestamp"
  public static String setFileName(String latitudeStart, String longitudeStart, String latitudeStop, String longitudeStop) {
    String timeStamp = JsonUtils.timeStampReadable(System.currentTimeMillis());
    String fileName = latitudeStart + "_" + longitudeStart + "__" + latitudeStop + "_" + longitudeStop + "_" + timeStamp + ".txt";
    return fileName;
  }
}
