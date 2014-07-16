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


	