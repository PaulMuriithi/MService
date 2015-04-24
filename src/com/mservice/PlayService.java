package com.mservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class PlayService extends Service {

	private int[] notes = { R.raw.a4, R.raw.b4 };
	private int NOTE_DURATION = 400; // millisec
	private MediaPlayer mediaPlayer;
	private boolean paused;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();
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
	}
}
