package com.example.federico.inertialtracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by Federico on 30-Nov-16.
 *
 */

class JsonUtils {

	private static JSONObject file = new JSONObject();

	private static JSONArray wifi = new JSONArray();
	private static JSONArray acc = new JSONArray();
	private static JSONArray gir = new JSONArray();
	private static JSONArray orientation = new JSONArray();
	private static JSONArray magnetic = new JSONArray();
	private static JSONArray pressure = new JSONArray();
	private static JSONArray rotVect = new JSONArray();

	static void addGPS(Double lat, Double longit) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("latitude", lat);
			obj.put("longitude", longit);

			file.put("GPS", obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	static void addWifi(long timestamp, String bssid, String ssid, int rssi){
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

				case "ROT_VECT":
					try {
						obj.put("x", x);
						obj.put("y", y);
						obj.put("z", z);

						rotVect.put(obj);
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

	private static void prepareJson() {
		try {
			file.put("Acceleration", acc);
			file.put("Gyroscope", gir);
			file.put("MagneticField", magnetic);
			file.put("Pressure" , pressure);
			file.put("RotactionVector", rotVect);
			file.put("WiFi", wifi);
			file.put("Orientation", orientation);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	static JSONObject getJsonFile() {
		return file;
	}

	static String readJsonFile() {
		prepareJson();
		String json = getJsonFile().toString();

		return json;
	}

	static JSONObject prepareToSend(){
		prepareJson();
		return file;
	}



	//Util for log a large text.
	public static void largeLog(String tag, String content) {
		if (content.length() > 4000) {
			Log.d(tag, content.substring(0, 4000));
			largeLog(tag, content.substring(4000));
		} else {
			Log.d(tag, content);
		}
	}

	public static boolean checkFrequency(long current, long last) {
		return current - last > MainActivity.FREQUENCY;
	}
}
