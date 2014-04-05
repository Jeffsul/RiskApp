package com.jeffsul.riskapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGamePlayers;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGameTerritories;
import com.jeffsul.riskapp.entities.Game;

/**
 * RiskGameDbFacade hides game loading operations from the front-end.
 */
public class RiskGameDbFacade {
	/**
	 * Loads and returns a Game object with the given ID.
	 * @param context context for the database.
	 * @param gameId
	 * @return loaded Game object with gameId.
	 */
	public static Game loadGameWithId(Context context, long gameId) {
		Game game = new Game();
		RiskGameDbHelper helper = new RiskGameDbHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query(RiskGame.TABLE_NAME, null, RiskGame._ID + "=?", new String[] {Long.toString(gameId)}, null, null, RiskGame._ID + " DESC");
		if (c.moveToFirst()) {
			game = Game.fromCursor(c);
		}

		Game.Player[] players = new Game.Player[game.numPlayers];
		c = db.query(RiskGamePlayers.TABLE_NAME, null, RiskGamePlayers.COLUMN_NAME_GAME_ID + "=?", new String[] {Long.toString(gameId)}, null, null, RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION + " ASC");
		int i = 0;
		while (c.moveToNext()) {
			players[i] = Game.Player.fromCursor(c);
			i++;
		}
		game.players = players;

		c = db.query(RiskGameTerritories.TABLE_NAME, null, RiskGameTerritories.COLUMN_NAME_GAME_ID + "=?", new String[] {Long.toString(gameId)}, null, null, RiskGameTerritories._ID + " ASC");
		Game.MapTerritory[] territs = new Game.MapTerritory[c.getCount()];
		i = 0;
		while (c.moveToNext()) {
			territs[i] = Game.MapTerritory.fromCursor(c);
			i++;
		}
		game.territories = territs;
		
		return game;
	}
}
