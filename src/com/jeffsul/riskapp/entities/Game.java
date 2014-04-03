package com.jeffsul.riskapp.entities;

import java.util.Date;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGamePlayers;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGameTerritories;

import android.database.Cursor;

public class Game {
	public int id;
	public String created;
	public String lastPlayed;
	public int numPlayers;
	public String mapId;
	public String cardSetting = "regular";
	public boolean initialized;
	public int turnCounter;

	public Player[] players;
	public MapTerritory[] territories;

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
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_TURN_COUNTER) > -1) {
			game.turnCounter = c.getInt(c.getColumnIndex(RiskGame.COLUMN_NAME_TURN_COUNTER));
		}
		if (c.getColumnIndex(RiskGame.COLUMN_NAME_INITIALIZED) > -1) {
			game.initialized = c.getInt(c.getColumnIndex(RiskGame.COLUMN_NAME_INITIALIZED)) == 1;
		}
		return game;
	}

	public static class Player {
		public String name;
		public int pos;

		public static Player fromCursor(Cursor c) {
			Player p = new Player();
			p.name = c.getString(c.getColumnIndexOrThrow(RiskGamePlayers.COLUMN_NAME_PLAYER_NAME));
			p.pos = c.getInt(c.getColumnIndexOrThrow(RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION));
			return p;
		}
	}

	public static class MapTerritory {
		public String name;
		public int owner;
		public int units;

		public static MapTerritory fromCursor(Cursor c) {
			MapTerritory t = new MapTerritory();
			t.name = c.getString(c.getColumnIndexOrThrow(RiskGameTerritories.COLUMN_NAME_TERRITORY_ID));
			t.owner = c.getInt(c.getColumnIndexOrThrow(RiskGameTerritories.COLUMN_NAME_OWNER));
			t.units = c.getInt(c.getColumnIndexOrThrow(RiskGameTerritories.COLUMN_NAME_UNITS));
			return t;
		}
	}
}
