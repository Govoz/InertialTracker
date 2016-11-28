package com.example.federico.inertialtracker;


import android.os.Looper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;


/**
 * Created by Federico on 24-Nov-16.
 *
 * http://stackoverflow.com/questions/24646635/android-loopj-async-http-crashes-after-1-4-5-update
 */

public class HttpRequest {

	// A SyncHttpClient is an AsyncHttpClient
	public static AsyncHttpClient syncHttpClient= new SyncHttpClient();
	public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		getClient().get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		getClient().post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return checkSend.URL + relativeUrl;
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