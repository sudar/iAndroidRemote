/**
 * 
 */
package com.sudarmuthu.android.adjustvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

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
		Log.v(TAG, "Amarino Received Event received by Broadcast Receiver");
		
		String data = null;
		
		if (intent != null) {
			String action = intent.getAction();
			if (action == null) {
				Log.w(TAG, "Null action received by Broadcast Receiver");
				return;
			}
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
//			// we only expect String data though, but it is better to check if really string was sent
//			// later Amarino will support differnt data types, so far data comes always as string and
//			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				if (data != null){
					Log.d(TAG, "Broadcast Receiver received Data: " + data);
					Intent serviceIntent = new Intent(new Intent(context, AdjustVolumeService.class));
					serviceIntent.putExtra("Data", data);
					context.startService(serviceIntent);
				}
			}
		}		
	}
}