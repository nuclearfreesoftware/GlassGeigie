/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohnemax.android.glass.glassgeigie.displaytools;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.lang.Runnable;

import com.ohnemax.android.glass.glassgeigie.R;
import com.ohnemax.android.glass.glassgeigie.ble.BluetoothLeService;

/**
 * Activity showing an options menu to stop the {@link OpenGlService}.
 */
public class MenuActivity extends Activity {

    private final Handler mHandler = new Handler();
    
	protected static final String TAG = "geigermenulivecard";


	
    @Override
    public void onAttachedToWindow() {
    	Log.v(TAG, "got to menu!");
        super.onAttachedToWindow();
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.v(TAG, "create options menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.geigie_livecard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.v(TAG, "tried to click");
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.stop:
                // Stop the service at the end of the message queue for proper options menu
                // animation. This is only needed when starting a new Activity or stopping a Service
                // that published a LiveCard.
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        stopService(new Intent(MenuActivity.this, GeigieLiveCard.class));
                        stopService(new Intent(MenuActivity.this, BluetoothLeService.class));
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the Activity.
        finish();
    }
}
