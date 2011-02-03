package com.sudarmuthu.android.adjustvolume;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AdjustVolume extends Activity {
	
    protected static final String TAG = "AdjustVolume";
	private boolean isHtc;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        
        Button upButton = (Button) findViewById(R.id.Button01);
        upButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Up Button Clicked");
				audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
			}
		});
        
        Button downButton = (Button) findViewById(R.id.Button02);
        downButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		Log.d(TAG, "Down Button Clicked");
				audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);        		
        	}
        });
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our receiver
//		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));

		Intent i = new Intent();
		ServiceConnection conn = new MediaPlayerServiceConnection();
		
		isHtc = true;
		i.setClassName("com.htc.music", "com.htc.music.MediaPlaybackService");
		
        if (!this.bindService(i, conn, Context.MODE_PRIVATE)) {
        	isHtc = false;
            i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
            this.bindService(i, conn, Context.MODE_PRIVATE);
        }
		
//		i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
//		 
//		this.bindService(i, conn, Context.MODE_PRIVATE);
        
		Log.d(TAG, "Binded Service");
				
		// this is how you tell Amarino to connect to a specific BT device from within your own code
//		Amarino.connect(this, DEVICE_ADDRESS);
	}


	@Override
	protected void onStop() {
		super.onStop();
		
		// if you connect in onStart() you must not forget to disconnect when your app is closed
//		Amarino.disconnect(this, DEVICE_ADDRESS);
//		
		// do never forget to unregister a registered receiver
//		unregisterReceiver(arduinoReceiver);
	}
	
	private class MediaPlayerServiceConnection implements ServiceConnection {
    	public com.htc.music.IMediaPlaybackService mServiceHtc;
    	public com.android.music.IMediaPlaybackService mServiceAndroid;

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("MediaPlayerServiceConnection", "Connected! Name: " + name.getClassName());

			// This is the important line
    		if (isHtc)
    			mServiceHtc = com.htc.music.IMediaPlaybackService.Stub.asInterface(service);
			else
				mServiceAndroid = com.android.music.IMediaPlaybackService.Stub.asInterface(service);
			
			// If all went well, now we can use the interface
			try {
				
				if (isHtc) {
					
					Log.i("MediaPlayerServiceConnection", "Playing track: " + mServiceHtc.getTrackName());
					Log.i("MediaPlayerServiceConnection", "By artist: " + mServiceHtc.getArtistName());
					if (mServiceHtc.isPlaying()) {
						Log.i("MediaPlayerServiceConnection", "Music player is playing.");
						// Next Track
						mServiceHtc.next();
					} else {
						Log.i("MediaPlayerServiceConnection", "Music player is not playing.");
					}
				} else {
					
					Log.i("MediaPlayerServiceConnection", "Playing track: " + mServiceAndroid.getTrackName());
					Log.i("MediaPlayerServiceConnection", "By artist: " + mServiceAndroid.getArtistName());
					if (mServiceAndroid.isPlaying()) {
						Log.i("MediaPlayerServiceConnection", "Music player is playing.");
					} else {
						Log.i("MediaPlayerServiceConnection", "Music player is not playing.");
					}
				}
			} catch (Exception e) {
				Log.i("MediaPlayerServiceConnection", "Some Exception");
	    		e.printStackTrace();
	    		throw new RuntimeException(e);
			}
		}

		@Override		
		public void onServiceDisconnected(ComponentName name) {
			Log.i("MediaPlayerServiceConnection", "Disconnected!");
		}
	}	
}