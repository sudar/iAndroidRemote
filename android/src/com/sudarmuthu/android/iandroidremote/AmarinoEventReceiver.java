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
package com.sudarmuthu.android.iandroidremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

/**
 * The broadcast Receiver which listens for events from Amarino
 * 
 * @author "Sudar Muthu (http://sudarmuthu.com)"
 *
 */
public class AmarinoEventReceiver extends BroadcastReceiver {

    protected static final String TAG = "iAndroidRemote";
	
    /**
     * When the event from Amarino is received
     * 
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
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				if (data != null){
					Log.d(TAG, "Broadcast Receiver received Data: " + data);
					
					//Send the data to the service
					Intent serviceIntent = new Intent(new Intent(context, AdjustVolumeService.class));
					serviceIntent.putExtra("Data", data);
					context.startService(serviceIntent);
				}
			}
		}		
	}
}