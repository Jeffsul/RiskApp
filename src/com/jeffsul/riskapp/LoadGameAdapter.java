package com.jeffsul.riskapp;

import java.util.ArrayList;

import com.jeffsul.riskapp.db.RiskGameDbHelper;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.entities.Game;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
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
		System.out.println("GAMES: " + c.getCount());
		savedGames = new ArrayList<Game>();
		
		int i = 0;
		while (c.moveToNext()) {
			System.out.println(c.getInt(c.getColumnIndex(RiskGame._ID)) + " :: " + c.getString(c.getColumnIndex(RiskGame.COLUMN_NAME_CREATED)));
			savedGames.add(Game.fromCursor(c));
		}
	}

	@Override
	public int getCount() {
		return savedGames.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
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
			v.setOrientation(LinearLayout.VERTICAL);
			v.setLayoutParams(new GridView.LayoutParams(85, 85));
			v.setPadding(8, 8, 8, 8);
			
			TextView tv = new TextView(context);
			tv.setText(Integer.toString(g.id) + " (" + g.getFormattedDateCreated() + ")");
			v.addView(tv);
		} else {
			v = (LinearLayout) convertView;
		}
		
		return v;
	}

}
