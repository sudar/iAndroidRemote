/**
   iAndroidRemote - Control your Android phone's music player using Apple Remote 
    
   Copyright 2011  Sudar Muthu  (email : sudar@sudarmuthu.com)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License, version 2, as
    published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
 * The Android service which interacts with the Music Player service
 * 
 * @author "Sudar Muthu (http://sudarmuthu.com)"
 *
 */
public class AdjustVolumeService extends Service {
	private static final String TAG = "iAndroidRemote";
	
	private AudioManager audioManager;
	private boolean isHtc; // Flag to identify whether the phone is using HTC Music player
	private MediaPlayerServiceConnection musicConn; //handler to music player service

	/**
	 * When the service is created for the first time
	 * 		 
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
		
		// First try to connect to HTC Music player service
		i.setClassName("com.htc.music", "com.htc.music.MediaPlaybackService");
		
        if (!context.bindService(i, musicConn, Context.MODE_PRIVATE)) {
        	Log.d(TAG, "Using built-in media player");
        	
        	// Default to Android's Music Player service
        	isHtc = false;
            i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
            context.bindService(i, musicConn, Context.MODE_PRIVATE);
        }
		
		super.onCreate();
	}

	/**
	 * When the service is started
	 * 
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		
		Bundle bundle = intent.getExtras();
		String data = bundle.getString("Data");

		// Increase volume
		if (data.equals("Plus")) {
			Log.d(TAG, "Increased Volume");
			audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
		}

		// Decrease volume
		if (data.equals("Minus")) {
			Log.d(TAG, "Decreased Volume");			
			audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
		}

		// Select Next song
		if (data.equals("Next")) {
			Log.d(TAG, "Next Song");
			try {
				musicConn.nextSong();
			} catch (RemoteException e) {
				Log.w(TAG, "Some Exception while selecting next song");
				e.printStackTrace();
			}
		}
		
		// Select Previous song
		if (data.equals("Prev")) {
			Log.d(TAG, "Prev Song");
			try {
				musicConn.prevSong();
			} catch (RemoteException e) {
				Log.w(TAG, "Some Exception while selecting prev song");				
				e.printStackTrace();
			}
		}
		
		// Play/Pause song
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
	
	/**
	 * When the service is destroyed.
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed");

		// clean up
		audioManager = null;
		musicConn = null;
		super.onDestroy();
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
	 * @author "Sudar Muthu (http://sudarmuthu.com)"
	 *
	 */
	private class MediaPlayerServiceConnection implements ServiceConnection {
		
    	public com.htc.music.IMediaPlaybackService mServiceHtc; // HTC Music Player service
    	public com.android.music.IMediaPlaybackService mServiceAndroid; // Default Android Music Player service

    	/**
    	 * When the service is started
    	 */
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
		 * Select the next song
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
		 * Select the Previous song
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
		
		/**
		 * When the service is disconnected
		 */
		@Override		
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Disconnected from service");
		}
	}
}