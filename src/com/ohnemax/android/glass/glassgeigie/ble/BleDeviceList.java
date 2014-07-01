package com.ohnemax.android.glass.glassgeigie.ble;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;

public class BleDeviceList {
	private ArrayList<BluetoothDevice> bleDevices;

	public BleDeviceList() {
		bleDevices = new ArrayList<BluetoothDevice>();
	}
	
	public void addDevice(BluetoothDevice device) {
		if (!bleDevices.contains(device)) {
			bleDevices.add(device);
		}
		
	}
	
	public BluetoothDevice getDevice(int position) {
		return bleDevices.get(position);
	}
	
	public int getIndex(BluetoothDevice device) {
		return bleDevices.indexOf(device);
	}

	public void clear() {
		bleDevices.clear();
	}
}


	