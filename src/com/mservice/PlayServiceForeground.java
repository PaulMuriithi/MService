package com.mservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class PlayServiceForeground extends Service {

	private int[] notes = { R.raw.a4, R.raw.b4 };
	private int NOTE_DURATION = 400; // millisec
	private MediaPlayer mediaPlayer;
	private boolean paused;
	private static final int NOTIFICATION_ID = 1;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();
		enforceForegroundMode();
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

	private final void enforceForegroundMode() {
		startForeground(NOTIFICATION_ID, getForegroundNotification());
	}

	private final void releaseForegroundMode() {
		stopForeground(true);
	}

	@SuppressWarnings("deprecation")
	protected Notification getForegroundNotification() {
		// Set the icon, scrolling text, and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher,
				"playback running", System.currentTimeMillis());
		// the PendingIntent to launch the activity if the user selects this
		// notification
		Intent startIntent = new Intent(getApplicationContext(),
				PlayServiceForeground.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				startIntent, 0);
		// Set the info for the views that show in the notification panel
		notification.setLatestEventInfo(this, "Playing Music",
				"Playback running", contentIntent);
		return notification;
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
		releaseForegroundMode();
		paused = true;
	}
}
