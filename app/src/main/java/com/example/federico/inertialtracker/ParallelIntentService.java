package com.example.federico.inertialtracker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
Source:
https://github.com/schoentoon/ParallelIntentService

Service extends ParallelIntentService (extends Service) and execute onHandleIntent().
 */

public abstract class ParallelIntentService extends Service {
	private static final int CORE_POOL_SIZE = 2;       // the number of threads to keep in the pool
	private static final int MAXIMUM_POOL_SIZE = 2;  // the max number of threads to allow in the pool
	private static final int KEEP_ALIVE = 2;           //  when the number of threads is > than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.


	//Create new thread onDemand
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
		}
	};

	private final AtomicInteger tasks_left = new AtomicInteger(0);
	private final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(2);

	private final ThreadPoolExecutor ThreadPoolExecutor = new ParallelThreadPoolExecutor(CORE_POOL_SIZE,
			MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
	private boolean mRunnable = true;

	//----------------------------------------------------------------------
	private class ParallelThreadPoolExecutor extends ThreadPoolExecutor {

		public ParallelThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
		                                  TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}


		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			final int active_count = tasks_left.decrementAndGet();
			if (active_count == 0)
				stopSelf();
			else
				tasksLeft(active_count);
		}
	}
	//----------------------------------------------------------------------
	//START TASK
	public class Task implements Runnable {

		public Task(final Intent intent) {
			this.intent = intent;
		}

		public void run() {
			while(mRunnable) {
				onHandleIntent(intent);
			}
		}

		private final Intent intent;
	}
	//END TASK

	private boolean mRedelivery;

	public ParallelIntentService(String name) {
		super();
	}

	public ParallelIntentService() {
		super();
	}

	public void setIntentRedelivery(boolean enabled) {
		mRedelivery = enabled;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		ThreadPoolExecutor.execute(new Task(intent));
	}

	@Override
	public void onDestroy() {
		mRunnable = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent, startId);
		//return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected abstract void onHandleIntent(Intent intent);

	protected void tasksLeft(int tasks_left) {
	}
}