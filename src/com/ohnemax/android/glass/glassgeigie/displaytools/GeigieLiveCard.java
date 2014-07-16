/* Copyright (C) 2014, Moritz Kütt
 * 
 * This file is part of GlassGeigie.
 * 
 * GlassGeigie is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GlassGeigie is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GlassGeigie.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.ohnemax.android.glass.glassgeigie.displaytools;

import java.util.List;
import java.util.Random;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.ohnemax.android.glass.glassgeigie.R;
import com.ohnemax.android.glass.glassgeigie.ble.BluetoothLeService;
import com.ohnemax.android.glass.glassgeigie.displaytools.MenuActivity;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;

public class GeigieLiveCard extends Service {
	
	private LiveCard mLiveCard;

	private RemoteViews mLiveCardView;
	
	private final Handler mHandler = new Handler();
	private final UpdateLiveCardRunnable mUpdateLiveCardRunnable =
	        new UpdateLiveCardRunnable();
	
	private static final String LIVE_CARD_TAG = "bGeigie Card";
	
	public long DELAY_MILLIS = 5000;

	protected String fullstring = "";

	private BluetoothLeService mBluetoothLeService;
	
	protected static final String TAG = "geigerlivecard";
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize BluetoothLeService from LiveCard");
            }
            Log.d(TAG, "BluetoothLeService initialized from LiveCard");

        }
 
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "in onreceive of live card");
			final String action = intent.getAction();

			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				Log.d(TAG, "LiveCard has received data");
				int readi = intent.getIntExtra("lastreading", 0);
				if(readi > 0) {
					Log.d(TAG, "LiveCard has reading: " + String.valueOf(readi));
					mLiveCardView.setTextViewText(R.id.cpm, String.valueOf(readi) + " CPM");
					double doserate = ((double) readi) / 334.0;
					Log.d(TAG, "dose " + String.valueOf(doserate));
					doserate = (double)Math.round(doserate * 1000) / 1000;
					mLiveCardView.setTextViewText(R.id.dose, String.valueOf(doserate) + " µSv/h");
					mLiveCardView.setTextViewText(R.id.date, "Testdate");
				}
				String meadate = intent.getStringExtra("measuredate");
				if(meadate != null) {
					mLiveCardView.setTextViewText(R.id.date, meadate);
				}
				mLiveCard.setViews(mLiveCardView);
			}
		}
	};

	
	public void onCreate() {
		super.onCreate();
    	Intent forreceiver = this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
   		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mLiveCard == null) {
			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
			
			//Set GlassGeigie Layout
			mLiveCardView = new RemoteViews(getPackageName(),R.layout.service_geigieglass);

			// Set MenuActivity
            Intent menuIntent = new Intent(this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));


			mLiveCard.publish(PublishMode.REVEAL);
			mHandler.post(mUpdateLiveCardRunnable);
		}
		return START_STICKY;
	}
	   
	@Override
    public void onDestroy() {
		// Clean up Bluetooth
		mBluetoothLeService.disconnect();
    	mBluetoothLeService.close();
    	unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        
        // Clean up Live Card
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        
        super.onDestroy();
    }



	private class UpdateLiveCardRunnable implements Runnable{

		private boolean mIsStopped = false;

		public void run(){
			if(!isStopped()){
				mLiveCardView.setTextViewText(R.id.cpm," - Wait - ");
			}
			mLiveCard.setViews(mLiveCardView);
		}


		public boolean isStopped() {
			return mIsStopped;
		}

		public void setStop(boolean isStopped) {
			this.mIsStopped = isStopped;
		}

	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}