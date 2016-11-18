package com.example.federico.inertialtracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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
	//	Log.d(TAG, "onHandleIntent(Intent); Sleeping for 1 second, thread id: " + Thread.currentThread().getId());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	//	Log.d(TAG, "onHandleIntent(Intent); Done, thread id: " + Thread.currentThread().getId());
	}

}

