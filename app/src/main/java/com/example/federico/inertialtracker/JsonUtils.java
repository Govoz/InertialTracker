package com.example.federico.inertialtracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Federico on 30-Nov-16.
 */

public class JsonUtils {

	static JSONObject file = new JSONObject();
	private static JSONArray acc = new JSONArray();
	private static JSONArray magnetic = new JSONArray();
	private static JSONArray geoRotVect = new JSONArray();
	private static JSONArray pressure = new JSONArray();
	private static JSONArray rotVect = new JSONArray();

	public static void addGPS(Double lat, Double longit) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("latitude", lat);
			obj.put("longitude", longit);

			file.put("GPS", obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void addWifi(long timestamp, String bssid, String ssid, int rssi ){
		try {
			JSONObject obj = new JSONObject();
			obj.put("Timestamp", timestamp);
			obj.put("Bssid", bssid);
			obj.put("Ssid", ssid);
			obj.put("Rssi", rssi);

			file.put("WIFI", obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void addValue(String type, long timestamp, float x, float y, float z) {
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
				case "MAGNETIC_FIELD":
					try {
						obj.put("magnetic_strength_field", x);
						obj.put("directionDegree", y);

						magnetic.put(obj);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					break;

				case "GEO_ROT_VECT":
					try {
						obj.put("x", x);
						obj.put("y", y);
						obj.put("z", z);

						geoRotVect.put(obj);
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

				default:
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void prepareJson() {
		try {
			file.put("Acceleration", acc);
			file.put("Magnetic Field", magnetic);
			file.put("Geo Rotaction Vector", geoRotVect);
			file.put("Pressure" , pressure);
			file.put("Rotaction Vector", rotVect);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject getJsonFile() {
		return file;
	}

	public static String readJsonFile() {
		prepareJson();
		String json = getJsonFile().toString();
		Log.d("JSON", json);
		return json;
	}
}
