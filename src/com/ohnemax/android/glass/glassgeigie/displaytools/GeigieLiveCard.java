package com.ohnemax.android.glass.glassgeigie.displaytools;

import java.util.List;
import java.util.Random;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.ohnemax.android.glass.glassgeigie.R;
import com.ohnemax.android.glass.glassgeigie.ble.BluetoothLeService;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

	protected static final String TAG = "geigerlivecard";

	public void onCreate() {
       super.onCreate();
       
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

   }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mLiveCard == null) {

			// Get an instance of a live card
			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

			// Inflate a layout into a remote view
			mLiveCardView = new RemoteViews(getPackageName(),R.layout.service_geigieglass);


			// Set up the live card's action with a pending intent
			//	            // to show a menu when tapped
				            Intent menuIntent = new Intent(this, GeigieLiveCard.class);
				            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				                Intent.FLAG_ACTIVITY_CLEAR_TASK);
				            mLiveCard.setAction(PendingIntent.getActivity(
				                this, 0, menuIntent, 0));

			// Publish the live card
			mLiveCard.publish(PublishMode.REVEAL);

			// Queue the update text runnable
			mHandler.post(mUpdateLiveCardRunnable);
		}
		return START_STICKY;
	}
	   
	   private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	final String action = intent.getAction();
	        	String test = intent.getStringExtra(BluetoothLeService.ACTION_DATA_AVAILABLE);
	                
          	    Log.d(TAG, test);
          	    fullstring = test;
	        }
	    };


	    
	    private class UpdateLiveCardRunnable implements Runnable{

	        private boolean mIsStopped = false;

	        /*
	         * If you are executing a long running task to get data to update a
	         * live card(e.g, making a web call), do this in another thread or
	         * AsyncTask.
	         */
	        public void run(){
	        	if(!isStopped()){

	        		
	        		mLiveCardView.setTextViewText(R.id.device_lat_info,
	        				fullstring);

	        		mLiveCardView.setTextViewText(R.id.device_long_info,
	        				"Dev. Longitude:");                 	

	        	}

	        	// Always call setViews() to update the live card's RemoteViews.
	        	mLiveCard.setViews(mLiveCardView);

	        	// Queue another score update in 30 seconds.
	        	mHandler.postDelayed(mUpdateLiveCardRunnable, DELAY_MILLIS);
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
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
	        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
	        return intentFilter;
	    }
}