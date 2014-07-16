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

import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.app.Card;

import android.content.Context;
import android.util.Log;


public class MenuCards {
	
	private static final String TAG = "glassgeigie.menucards";
	private Context mContext;
	private List<Card> mCards;
	private List<Integer> bledev;
		
	public MenuCards(Context context) {
		mContext = context;
		mCards = new ArrayList<Card>();
		bledev = new ArrayList<Integer>();
	}
	
	public void addMenuItem(int textShow) {
		Card card = new Card(mContext);
		card.setText(textShow);
		mCards.add(card);
		bledev.add(-1);
	}
	
	public void addMenuItem(String textShow) {
		Card card = new Card(mContext);
		card.setText(textShow);
		mCards.add(card);
		bledev.add(-1);
	}	
	
	public void addMenuItem(String textShow, Integer bleindex) {
		Card card = new Card(mContext);
		card.setText(textShow);
		mCards.add(card);
		bledev.add(bleindex);
	}	
	
	public void removeBle() {
		int i = 0;
		while(i < mCards.size()) {
			Log.d(TAG, "removing item " + String.valueOf(i) + " of mcards " + String.valueOf(mCards.size()) + " and bledev " + String.valueOf(bledev.size()));
			if(bledev.get(i) != -1) {
				bledev.remove(i);
				mCards.remove(i);
				Log.d(TAG,"new size " + String.valueOf(mCards.size()));
			}
			else {
				i++;
			}
		}
	}
	
	public int getBluetoothId(int id) {
		return bledev.get(id);
	}
	
	public List<Card> getCards() {
		int numCards = mCards.size();
		return mCards;
	}
}