package com.jeffsul.riskapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LoadGameAdapter extends BaseAdapter {
	private Context context;
	
	public LoadGameAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// Return saved game ID.
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Instantiate new view representing saved game.
		return null;
	}

}
