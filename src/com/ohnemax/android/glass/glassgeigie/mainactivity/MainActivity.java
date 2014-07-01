package com.ohnemax.android.glass.glassgeigie.mainactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.ohnemax.android.glass.glassgeigie.R;
import com.ohnemax.android.glass.glassgeigie.ble.BleDeviceList;
import com.ohnemax.android.glass.glassgeigie.ble.BluetoothLeService;
import com.ohnemax.android.glass.glassgeigie.displaytools.GeigieCardScrollAdapter;
import com.ohnemax.android.glass.glassgeigie.displaytools.GeigieLiveCard;
import com.ohnemax.android.glass.glassgeigie.displaytools.MenuCards;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class MainActivity extends Activity {
	
	private static final String TAG = "glassgeigie.mainactivity";

	private CardScrollAdapter mMainCardAdapter;
	private CardScrollView mMainCardScroller;
	private MenuCards menucards;
	
    private boolean mScanning;
    private static final long SCAN_PERIOD = 5000;
	
	static final int SHOW = 0;
    static final int SCAN = 1;
    static final int CONNECT = 2;
    static final int ABOUT = 3;
	
    private ArrayList<Card> cards = new ArrayList<Card>();

	private Handler mHandler;

	private BleDeviceList mDevices;

	private BluetoothAdapter mBluetoothAdapter;
	public BluetoothLeService mBluetoothLeService = null;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	protected boolean mConnected;
	private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private BluetoothGattCharacteristic mChara = null;

	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		 
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize BluetoothLeService");
                finish();
            }
            Log.d(TAG, "BluetoothLeService initialized");

        }
 
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

		mHandler = new Handler();
		mDevices = new BleDeviceList();
		
    	final BluetoothManager bluetoothManager =
    	        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    	mBluetoothAdapter = bluetoothManager.getAdapter();
    	
		if (mBluetoothAdapter == null) {
			Log.d(TAG, "no bluetooth???");
			finish();
			return;
		}
    	Log.d(TAG, "created devices");
    	
    	Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    	
        menucards = new MenuCards(this);
        menucards.addMenuItem(R.string.text_show);
        menucards.addMenuItem(R.string.text_scan);
        menucards.addMenuItem(R.string.text_about);
        mMainCardAdapter = new GeigieCardScrollAdapter(menucards.getCards());
        mMainCardScroller = new CardScrollView(this);
        mMainCardScroller.setAdapter(mMainCardAdapter);
        setContentView(mMainCardScroller);
        Log.d(TAG,"ContentView Set");
        setCardScrollerListener();
    }
    
    protected void onResume() {
        super.onResume();
        mMainCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mMainCardScroller.deactivate();
        super.onPause();
    }
    
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.d(TAG, "MainActivity has received connected");
             
                
              //  updateConnectionState(R.string.connected);
                
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.d(TAG,"Disconnected");
      //          updateConnectionState(R.string.disconnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	Log.d(TAG, "MainActivity has received services discovered");
            	   //Find mChara
                Log.d(TAG, "Search for Main RX characteristic");
            	
                List<BluetoothGattService> gattServices;
                gattServices = mBluetoothLeService.getSupportedGattServices();
                Log.d(TAG,"mosize " + String.valueOf(gattServices.size()));
				for (BluetoothGattService gattService : gattServices) {
					String uuid = gattService.getUuid().toString();
					Log.d(TAG, uuid + " END");
					Log.d(TAG,"ef080d8c-c3be-41ff-bd3f-05a5f4795d7f"+ " END");
					if (uuid.equals("ef080d8c-c3be-41ff-bd3f-05a5f4795d7f")) {
						Log.d(TAG, "BLEBEE Services detected");
						List<BluetoothGattCharacteristic> gattCharacteristics = gattService
								.getCharacteristics();
						for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
							if (gattCharacteristic.getUuid().toString().equals("a1e8f5b1-696b-4e4c-87c6-69dfe0b0093b")) {
								Log.d(TAG, "Found RX characteristic");
								mChara = gattCharacteristic;
								List<BluetoothGattDescriptor> desclist = mChara.getDescriptors();
								for(BluetoothGattDescriptor descr : desclist) {
									Log.d(TAG, "Descriptor " + descr.toString());
								}
								 // mBluetoothLeService.setCharacteristicNotification(mChara, true);
								   
								 //   BluetoothGattDescriptor descriptor = mChara.getDescriptor(UUID.fromString("a1e8f5b1-696b-4e4c-87c6-69dfe0b0093b"));
								 //   descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
								 //   mBluetoothLeService.writeDescriptor(descriptor);    
							}
						}

					}
                }
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	Log.d(TAG, "MainActivity has received data");
            	//BluetoothLeService.
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    
    private void displayData(String data) {
        if (data != null) {
            Log.d(TAG,data);
        }
    }
 
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        
        for (BluetoothGattService gattService : gattServices) {
        	String uuid = gattService.getUuid().toString();
        	Log.d(TAG, uuid);
        	if(uuid == "ef080d8c-c3be-41ff-bd3f-05a5f4795d7f") {
        		Log.d(TAG, "BLEBEE Services detected");
        		List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
        		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
        			gattCharacteristic.getProperties();
        		}
        		
        	}
        }
        String uuid = null;
        String unknownServiceString = "unknown";
 //       String unknownServiceString = getResources().getString(R.string.unknown_service);
   //     String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
 
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, unknownServiceString);
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);
 
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
 
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, "unknown-characteristic");
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
 
    }
    
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {

				@Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

	private void setCardScrollerListener() {
        mMainCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {



			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked main card menu at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                if(position == 0) {
                //	Log.d(TAG, "Try to read from RX");
                //	mBluetoothLeService.readCharacteristic(mChara);
                	Log.d(TAG, "Try to activate notifications");
                	mBluetoothLeService.activateNotifications();
          //      	startService(new Intent(MainActivity.this, GeigieLiveCard.class));
 
                }
                if(position == 1) {
//                	menucards.addMenuItem("ble-1",3);
//                	menucards.addMenuItem("ble-2",3);
//                	menucards.addMenuItem("ble-3",3);
//                    mMainCardAdapter.notifyDataSetChanged();
                	scanLeDevice(true);
                }
                if(position == 2) {
                	menucards.removeBle();
                    mMainCardAdapter.notifyDataSetChanged();
                	mMainCardScroller.setSelection(0);
                }
                if(position >= 3) {
                	
                	int deviceId = menucards.getBluetoothId(position);
                	if(deviceId != -1) {
                		BluetoothDevice device = mDevices.getDevice(deviceId);
                		Log.d(TAG, "Try to connect to "
    							+ deviceId);
                		String mDeviceAddress = device.getAddress();
                		if (mBluetoothLeService != null) {
                			mBluetoothLeService.connect(mDeviceAddress);
                		}
                		else {
                			Log.e(TAG, "No BLE Service available");
                		}
                		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                		menucards.removeBle();
                    	menucards.addMenuItem("Connected!");
                        mMainCardAdapter.notifyDataSetChanged();	
                	}
                	
                	
                	
                	
                	
                	

                }
                              

                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG,"Called Scan-Callback");
					if(mDevices.getIndex(device) == 0) {
						Log.d(TAG,"Device already in List");
					}
					else {
						Log.d(TAG,"New device found - add it to List");
						mDevices.addDevice(device);
						menucards.addMenuItem("Connect to: " + device.getName(), mDevices.getIndex(device));
						Log.d(TAG,"and to menu (device idx = " + String.valueOf(mDevices.getIndex(device) + ")"));
						mMainCardAdapter.notifyDataSetChanged();
					}
				}
			});
		}
	};
	
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}