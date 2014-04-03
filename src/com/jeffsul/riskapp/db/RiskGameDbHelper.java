package com.jeffsul.riskapp.db;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGamePlayers;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGameTerritories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RiskGameDbHelper extends SQLiteOpenHelper {
	public static final int DB_VERSION = 3;
	public static final String DB_NAME = "RiskGame.db";
	
	private static final String SQL_CREATE_GAMES = "CREATE TABLE " + RiskGame.TABLE_NAME + " ("
			+ RiskGame._ID + " INTEGER PRIMARY KEY,"
			+ RiskGame.COLUMN_NAME_CREATED + " STRING,"
			+ RiskGame.COLUMN_NAME_LAST_PLAYED + " STRING,"
			+ RiskGame.COLUMN_NAME_NUM_PLAYERS + " INTEGER,"
			+ RiskGame.COLUMN_NAME_MAP_ID + " STRING,"
			+ RiskGame.COLUMN_NAME_INITIALIZED + " BOOLEAN,"
			+ RiskGame.COLUMN_NAME_TURN_COUNTER + " INTEGER)";
	private static final String SQL_DELETE_GAMES = "DROP TABLE IF EXISTS " + RiskGame.TABLE_NAME;

	private static final String SQL_CREATE_GAME_PLAYERS = "CREATE TABLE " + RiskGamePlayers.TABLE_NAME + " ("
			+ RiskGamePlayers._ID + " INTEGER PRIMARY KEY,"
			+ RiskGamePlayers.COLUMN_NAME_GAME_ID + " INTEGER,"
			+ RiskGamePlayers.COLUMN_NAME_PLAYER_NAME + " STRING,"
			+ RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION + " INTEGER)";
	private static final String SQL_DELETE_GAME_PLAYERS = "DROP TABLE IF EXISTS " + RiskGamePlayers.TABLE_NAME;
	
	private static final String SQL_CREATE_GAME_TERRITORIES = "CREATE TABLE " + RiskGameTerritories.TABLE_NAME + " ("
			+ RiskGameTerritories._ID + " INTEGER PRIMARY KEY,"
			+ RiskGameTerritories.COLUMN_NAME_GAME_ID + " INTEGER,"
			+ RiskGameTerritories.COLUMN_NAME_OWNER + " STRING,"
			+ RiskGameTerritories.COLUMN_NAME_TERRITORY_ID + " STRING,"
			+ RiskGameTerritories.COLUMN_NAME_UNITS + " INTEGER)";
	private static final String SQL_DELETE_GAME_TERRITORIES = "DROP TABLE IF EXISTS " + RiskGameTerritories.TABLE_NAME;

	public RiskGameDbHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_GAMES);
		db.execSQL(SQL_CREATE_GAME_PLAYERS);
		db.execSQL(SQL_CREATE_GAME_TERRITORIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_GAMES);
		db.execSQL(SQL_DELETE_GAME_PLAYERS);
		db.execSQL(SQL_DELETE_GAME_TERRITORIES);
		onCreate(db);
	}
}
