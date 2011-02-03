package com.sudarmuthu.android.adjustvolume;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.abraxas.amarino.AmarinoIntent;

import com.android.music.IMediaPlaybackService;

public class AdjustVolume extends Activity {
	// change this to your Bluetooth device address 
//	private static final String DEVICE_ADDRESS =  "00:06:66:03:73:7B"; //"00:06:66:03:73:7B";
	
    protected static final String TAG = "AdjustVolume";

//	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
    
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
		i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
		 
		ServiceConnection conn = new MediaPlayerServiceConnection();
		this.bindService(i, conn, Context.BIND_AUTO_CREATE);
        
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
	

	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino
	 * events.
	 * 
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	public class ArduinoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

			String data = null;
			
//			 the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
//			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				if (data != null){
					Log.d(TAG, "Received Data: " + data);
					if (data.equals("Plus")) {
						audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);						
					}
					
					if (data.equals("Minus")) {
						audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);						
					}
				}
			}
		}
	}
	
//	public class MediaPlayerServiceConnection implements ServiceConnection {
		
//		public IMediaPlaybackService service;		
//        public void onServiceConnected(ComponentName name, IBinder boundService) {
//            Log.d(TAG, "Connected! Name: " + name.getClassName());
// 
//            // This is the important line
//            service = IMediaPlaybackService.Stub.asInterface((IBinder) boundService);
// 
//            // If all went well, now we can use the interface
// 
//            try {
//					Log.d(TAG, "Playing track: " + service.getTrackName());
//                //Tell the player to pause the song
//                    service.pause();
// 
//                //Log.i("MediaPlayerServiceConnection", "By artist: " + service.getArtistName());
//                if (service.isPlaying()) {
//                    Log.d(TAG, "Music player is playing.");
//                } else {
//                    Log.d(TAG, "Music player is not playing.");
//                }
//            } catch (RemoteException e) {
//            	Log.d(TAG, "Some exception");
//            	e.printStackTrace();
//            }
//        }
// 
//        public void onServiceDisconnected(ComponentName name) {
//            service=null;
//            Log.d(TAG, "Disconnected!");
//        }
//    }
	
	
	private class MediaPlayerServiceConnection implements ServiceConnection {

		public IMediaPlaybackService mService;

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("MediaPlayerServiceConnection", "Connected! Name: " + name.getClassName());

			// This is the important line
			mService = IMediaPlaybackService.Stub.asInterface(service);

			// If all went well, now we can use the interface
			try {
				Log.i("MediaPlayerServiceConnection", "Playing track: " + mService.getTrackName());
				Log.i("MediaPlayerServiceConnection", "By artist: " + mService.getArtistName());
				if (mService.isPlaying()) {
					Log.i("MediaPlayerServiceConnection", "Music player is playing.");
				} else {
					Log.i("MediaPlayerServiceConnection", "Music player is not playing.");
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