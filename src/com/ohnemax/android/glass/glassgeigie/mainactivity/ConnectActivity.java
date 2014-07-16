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

package com.ohnemax.android.glass.glassgeigie.mainactivity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.ohnemax.android.glass.glassgeigie.R;
import com.ohnemax.android.glass.glassgeigie.displaytools.GeigieCardScrollAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class ConnectActivity extends Activity {
	
	private static final String TAG = "glassgeigie.mainactivity";

	private CardScrollAdapter mMainCardAdapter;
	private CardScrollView mMainCardScroller;
	
	private int backcard;
	
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mMainCardAdapter = new GeigieCardScrollAdapter(createCards(this));
        mMainCardScroller = new CardScrollView(this);
        mMainCardScroller.setAdapter(mMainCardAdapter);
        setContentView(mMainCardScroller);
        setCardScrollerListener();
    }

	private List<Card> createCards(Context context) {
		backcard = 2;
		ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(0, new Card(context).setText("test1"));
        cards.add(1, new Card(context).setText(R.string.text_scan));
        cards.add(backcard, new Card(context).setText("Back"));
        return cards;
	}
	
	private void setCardScrollerListener() {
        mMainCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked main card menu at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                if(position == backcard) {
                	
                }
                else {
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Nothing selected");
                }

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
	}
}
