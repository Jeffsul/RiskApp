package com.jeffsul.riskapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.entities.Game;

public class RiskGameDbFacade {
	public static Game loadGameWithId(Context context, long gameId) {
		Game game = new Game();
		RiskGameDbHelper helper = new RiskGameDbHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] projection = null;
		Cursor c = db.query(RiskGame.TABLE_NAME, projection, RiskGame._ID + "=?", new String[] {Long.toString(gameId)}, null, null, RiskGame._ID + " DESC");
		if (c.moveToFirst()) {
			game = Game.fromCursor(c);
		}
		return game;
	}
}
