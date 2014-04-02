package com.jeffsul.riskapp.entities;

import java.util.Date;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;

import android.database.Cursor;

public class Game {

	public int id;
	public String created;

	public Game(int id, String created) {
		this.id = id;
		this.created = created;
	}

	public String getFormattedDateCreated() {
		long l = Long.parseLong(created);
		Date d = new Date(l);
		return d.toString();
	}

	public static Game fromCursor(Cursor c) {
		int id = c.getInt(c.getColumnIndexOrThrow(RiskGame._ID));
		String created = c.getString(c.getColumnIndexOrThrow(RiskGame.COLUMN_NAME_CREATED));
		return new Game(id, created);
	}
}
