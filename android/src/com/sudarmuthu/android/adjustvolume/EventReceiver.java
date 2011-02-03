/**
 * 
 */
package com.sudarmuthu.android.adjustvolume;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

import com.android.music.IMediaPlaybackService;

/**
 * @author "Sudar Muthu (sudarm@)"
 *
 */
public class EventReceiver extends BroadcastReceiver {

    protected static final String TAG = "AdjustVolumeService";
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		String data = null;
		
		Intent i = new Intent();
		i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
		 
//		ServiceConnection conn = new MediaPlayerServiceConnection();
//		context.bindService(i, conn, 0);
		
		if (intent != null) {
			String action = intent.getAction();
			if (action == null) {
				Log.d(TAG, "Null Action Received");
				return;
			}
			
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
						audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);						
					}
					
					if (data.equals("Minus")) {
						audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);						
					}
					
					if (data.equals("Next")) {
						
					}
					
				}
			}
		}		
	}
	
	public class MediaPlayerServiceConnection implements ServiceConnection {
		 
		IMediaPlaybackService service;		
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            Log.d(TAG, "Connected! Name: " + name.getClassName());
 
            // This is the important line
            service = IMediaPlaybackService.Stub.asInterface((IBinder) boundService);
 
            // If all went well, now we can use the interface
 
            try {
                Log.d(TAG, "Playing track: " + service.getTrackName());
                //Tell the player to pause the song
                    service.pause();
 
                //Log.i("MediaPlayerServiceConnection", "By artist: " + service.getArtistName());
                if (service.isPlaying()) {
                    Log.d(TAG, "Music player is playing.");
                } else {
                    Log.d(TAG, "Music player is not playing.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
 
        public void onServiceDisconnected(ComponentName name) {
            service=null;
            Log.d(TAG, "Disconnected!");
        }
    }	
}