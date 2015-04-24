package com.mservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

public class PlayServiceWakeLock extends Service {

	private int[] notes = { R.raw.a4, R.raw.b4 };
	private int NOTE_DURATION = 400; // millisec
	private MediaPlayer mediaPlayer;
	private boolean paused;
	private static final String LOG_TAG = PlayServiceWakeLock.class
			.getSimpleName();
	private WakeLock mWakeLock = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();
		setWakeLock();
		paused = false;
		// Stand-alone play_music() function call causes
		// main thread to hang. Instead, create
		// separate thread for time-consuming task.
		Thread initBkgdThread = new Thread(new Runnable() {
			public void run() {
				playMusic();
			}
		});
		initBkgdThread.start();
	}

	private void setWakeLock() {
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				LOG_TAG);
		mWakeLock.acquire();
	}

	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
	}

	private void playMusic() {
		for (int i = 0; i < 12; i++) {
			// Check to ensure main activity not paused
			if (!paused) {
				if (mediaPlayer != null) {
					mediaPlayer.release();
				}
				mediaPlayer = MediaPlayer.create(this, notes[i % 2]);
				mediaPlayer.start();
				try {
					Thread.sleep(NOTE_DURATION);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service stopped ...", Toast.LENGTH_LONG).show();
		paused = true;
		releaseWakeLock();
	}
}
