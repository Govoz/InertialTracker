package com.example.federico.inertialtracker;

import android.content.Context;
import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;


/**
 * Created by Federico on 24-Nov-16.
 */

public class HttpRequest {

	String URL = "http://192.168.1.10:80";

	public static AsyncHttpClient syncHttpClient= new SyncHttpClient();
	public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public void post(RequestParams params, AsyncHttpResponseHandler responseHandler) {
		getClient().post(URL, params, responseHandler);
	}

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		getClient().get(url, params, responseHandler);
	}

	public static void setCookieStore(PersistentCookieStore cookieStore) {
		getClient().setCookieStore(cookieStore);
	}

	/**
	 * @return an async client when calling from the main thread, otherwise a sync client.
	 */
	private static AsyncHttpClient getClient()
	{
		// Return the synchronous HTTP client when the thread is not prepared
		if (Looper.myLooper() == null)
			return syncHttpClient;
		return asyncHttpClient;
	}
}