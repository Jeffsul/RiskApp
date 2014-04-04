package com.jeffsul.riskapp;

import java.util.ArrayList;

import com.jeffsul.riskapp.db.RiskGameDbHelper;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.entities.Game;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LoadGameAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Game> savedGames;
	
	public LoadGameAdapter(Context context) {
		this.context = context;
		
		RiskGameDbHelper helper = new RiskGameDbHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] projection = {RiskGame._ID, RiskGame.COLUMN_NAME_CREATED, RiskGame.COLUMN_NAME_MAP_ID};
		Cursor c = db.query(RiskGame.TABLE_NAME, projection, null, null, null, null, RiskGame._ID + " DESC");
		savedGames = new ArrayList<Game>();
		
		while (c.moveToNext()) {
			savedGames.add(Game.fromCursor(c));
		}
	}

	@Override
	public int getCount() {
		return savedGames.size();
	}

	@Override
	public Object getItem(int position) {
		Game g = savedGames.get(position);
		return g.id;
	}

	@Override
	public long getItemId(int position) {
		return savedGames.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout v;
	
		if (convertView == null) {
			Game g = savedGames.get(position);
			
			v = new LinearLayout(context);
			v.setOrientation(LinearLayout.HORIZONTAL);
			v.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			v.setPadding(8, 8, 8, 8);
			
			ImageView iv = new ImageView(context);
			iv.setBackgroundResource(R.drawable.classic_map_small);
			int w = 60;
			LayoutParams ivParams = new LayoutParams(w, (int)((266.0/400.0)*w));
			iv.setLayoutParams(ivParams);
			v.addView(iv);
			
			TextView tv = new TextView(context);
			tv.setText(Html.fromHtml("<b>Game: </b>" + Integer.toString(g.id) + "<br><i>Date created: </i>" + g.getFormattedDateCreated()
					+ ", <i>Last played: </i>" + g.getFormattedLastPlayed()));
			v.addView(tv);
		} else {
			v = (LinearLayout) convertView;
		}
		
		return v;
	}

}
