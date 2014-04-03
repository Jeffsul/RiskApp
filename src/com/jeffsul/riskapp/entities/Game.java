package com.jeffsul.riskapp.entities;

import java.util.Date;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;

import android.database.Cursor;

public class Game {
	public int id;
	public String created;
	public String lastPlayed;
	public int numPlayers;
	public String mapId;
	public String cardSetting = "regular";
	public boolean initialized;

	public Game() {
		// TODO(jeffsul): Builder pattern.
	}

	public String getFormattedDateCreated() {
		long l = Long.parseLong(created);
		Date d = new Date(l);
		return d.toString();
	}

	public static Game fromCursor(Cursor c) {
		Game game = new Game();
		game.id = c.getInt(c.getColumnIndexOrThrow(RiskGame._ID));
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_CREATED) > -1) {
			game.created = c.getString(c.getColumnIndex(RiskGame.COLUMN_NAME_CREATED));
		}
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_LAST_PLAYED) > -1) {
			game.lastPlayed = c.getString(c.getColumnIndex(RiskGame.COLUMN_NAME_LAST_PLAYED));
		}
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_MAP_ID) > -1) {
			game.mapId = c.getString(c.getColumnIndex(RiskGame.COLUMN_NAME_MAP_ID));
		}
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_NUM_PLAYERS) > -1) {
			game.numPlayers = c.getInt(c.getColumnIndex(RiskGame.COLUMN_NAME_NUM_PLAYERS));
		}
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_INITIALIZED) > -1) {
			game.initialized = c.getInt(c.getColumnIndex(RiskGame.COLUMN_NAME_INITIALIZED)) == 1;
		}
		return game;
	}
}
