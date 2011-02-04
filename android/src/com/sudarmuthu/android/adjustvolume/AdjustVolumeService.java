/**
 * 
 */
package com.sudarmuthu.android.adjustvolume;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author "Sudar Muthu (sudarm@)"
 *
 */
public class AdjustVolumeService extends Service {
	private static final String TAG = "AdjustVolumeService";
	
	AudioManager audioManager;
	private boolean isHtc; // Flag to identify whether the phone is using HTC Music player
	private MediaPlayerServiceConnection musicConn;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.d(TAG, "Service Created");
		
		Context context = getApplicationContext();
		
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		musicConn = new MediaPlayerServiceConnection();
		isHtc = true;
		
		Intent i = new Intent();
		i.setClassName("com.htc.music", "com.htc.music.MediaPlaybackService");
		
        if (!context.bindService(i, musicConn, Context.MODE_PRIVATE)) {
        	Log.d(TAG, "Using built-in media player");
        	
        	isHtc = false;
            i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
            context.bindService(i, musicConn, Context.MODE_PRIVATE);
        }
		
		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed");
		
		audioManager = null;
		musicConn = null;
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		
		Bundle bundle = intent.getExtras();
		String data = bundle.getString("Data");

		if (data.equals("Plus")) {
			Log.d(TAG, "Increased Volume");
			audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
		}

		if (data.equals("Minus")) {
			Log.d(TAG, "Decreased Volume");			
			audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
		}

		if (data.equals("Next")) {
			Log.d(TAG, "Next Song");
			try {
				musicConn.nextSong();
			} catch (RemoteException e) {
				Log.w(TAG, "Some Exception while selecting next song");
				e.printStackTrace();
			}
		}
		
		if (data.equals("Prev")) {
			Log.d(TAG, "Prev Song");
			try {
				musicConn.prevSong();
			} catch (RemoteException e) {
				Log.w(TAG, "Some Exception while selecting prev song");				
				e.printStackTrace();
			}
		}
		
		if (data.equals("Center")) {
			Log.d(TAG, "Play/Pausing Song");
			try {
				musicConn.playPause();
			} catch (RemoteException e) {
				Log.w(TAG, "Some Exception while play/pausing song");				
				e.printStackTrace();
			}
		}
		
		super.onStart(intent, startId);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}	
	
	/**
	 * Media Player Service Connection Service 
	 * 
	 * @author "Sudar Muthu (sudarm@)"
	 *
	 */
	private class MediaPlayerServiceConnection implements ServiceConnection {
    	public com.htc.music.IMediaPlaybackService mServiceHtc;
    	public com.android.music.IMediaPlaybackService mServiceAndroid;

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Connected to service: " + name.getClassName());

			// This is the important line, where we bind the music service
    		if (isHtc) {
    			mServiceHtc = com.htc.music.IMediaPlaybackService.Stub.asInterface(service);
    		} else {
    			mServiceAndroid = com.android.music.IMediaPlaybackService.Stub.asInterface(service);
    		}
		}

		/**
		 * Selects the next song
		 * 
		 * @throws RemoteException 
		 * 
		 */
		public void nextSong() throws RemoteException {
			if (isHtc) {
				mServiceHtc.next();
			} else {
				mServiceAndroid.next();
			}
		}
		
		/**
		 * Selects the Previous song
		 * 
		 * @throws RemoteException
		 */
		public void prevSong() throws RemoteException {
			if (isHtc) {
				mServiceHtc.prev();
			} else {
				mServiceAndroid.prev();
			}
		}

		/**
		 * Play/pause the song.
		 * 
		 * @throws RemoteException
		 */
		public void playPause() throws RemoteException {
			if (isHtc) {
				if (mServiceHtc.isPlaying()) {
					mServiceHtc.pause();
				} else {
					mServiceHtc.play();
				}
			} else {
				if (mServiceAndroid.isPlaying()) {
					mServiceAndroid.pause();
				} else {
					mServiceAndroid.play();
				}
			}
		}
		
		@Override		
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Disconnected from service");
		}
	}
}