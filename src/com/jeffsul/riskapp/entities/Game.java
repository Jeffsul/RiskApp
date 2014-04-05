package com.jeffsul.riskapp.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGamePlayers;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGameTerritories;

import android.database.Cursor;

/**
 * Enity object representing a saved Risk game.
 */
public class Game {
	private static final SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d 'at' HH:mm");
	
	public int id;
	public String created;
	public String lastPlayed;
	public int numPlayers;
	public String mapId;
	public String cardSetting = "regular";
	/** Whether or not the game has been initialized, i.e., territories randomly assigned. */
	public boolean initialized;
	/** Index of the active player. */
	public int turnCounter;

	public Player[] players;
	public MapTerritory[] territories;

	public Game() {
		// TODO(jeffsul): Builder pattern.
	}

	/**
	 * 
	 * @return the formatted string representing the creation date.
	 */
	public String getFormattedDateCreated() {
		long l = Long.parseLong(created);
		Date d = new Date(l);
		return format.format(d);
	}

	/**
	 * 
	 * @return the formatted string representing the date last played.
	 */
	public String getFormattedLastPlayed() {
		long l = System.currentTimeMillis();
		try {
			l = Long.parseLong(lastPlayed);
		} catch (Exception e) {}
		Date d = new Date(l);
		return format.format(d);
	}

	/**
	 * Instantiate and load a new Game object from a database cursor.
	 * @param c database cursor containing some columns from RiskGame table (at least _ID).
	 * @return game object with all the data from c.
	 */
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

	/**
	 * Entity object storing a player in a particular game.
	 */
	public static class Player {
		public String name;
		public int pos;

		/**
		 * Instantiate and load a new Player object from a database cursor.
		 * @param c database cursor containing some columns from RiskGamePlayers table.
		 * @return player object with all the data from c.
		 */
		public static Player fromCursor(Cursor c) {
			Player p = new Player();
			p.name = c.getString(c.getColumnIndexOrThrow(RiskGamePlayers.COLUMN_NAME_PLAYER_NAME));
			p.pos = c.getInt(c.getColumnIndexOrThrow(RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION));
			return p;
		}
	}

	/**
	 * Entity object storing the state of a territory in a particular game.
	 */
	public static class MapTerritory {
		public String name;
		public int owner;
		public int units;

		/**
		 * Instantiate and load a new MapTerritory object from a database cursor.
		 * @param c database cursor containing some columns from RiskGameTerritories table.
		 * @return territory object with all the data from c.
		 */
		public static MapTerritory fromCursor(Cursor c) {
			MapTerritory t = new MapTerritory();
			t.name = c.getString(c.getColumnIndexOrThrow(RiskGameTerritories.COLUMN_NAME_TERRITORY_ID));
			t.owner = c.getInt(c.getColumnIndexOrThrow(RiskGameTerritories.COLUMN_NAME_OWNER));
			t.units = c.getInt(c.getColumnIndexOrThrow(RiskGameTerritories.COLUMN_NAME_UNITS));
			return t;
		}
	}
}
