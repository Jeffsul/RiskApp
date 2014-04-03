package com.jeffsul.riskapp.db;

import android.provider.BaseColumns;

public final class RiskGameContract {

	// Empty constructor to prevent someone from accidentally instantiating it.
	public RiskGameContract() {}
	
	public static abstract class RiskGame implements BaseColumns {
		public static final String TABLE_NAME = "games";
		public static final String COLUMN_NAME_CREATED = "date_created";
		public static final String COLUMN_NAME_LAST_PLAYED = "last_played";
		public static final String COLUMN_NAME_MAP_ID = "map_id";
		public static final String COLUMN_NAME_NUM_PLAYERS = "num_players";
		public static final String COLUMN_NAME_TURN_COUNTER = "turn_counter";
		public static final String COLUMN_NAME_INITIALIZED = "initialized";
	}
	
	public static abstract class RiskGamePlayers implements BaseColumns {
		public static final String TABLE_NAME = "gamePlayers";
		public static final String COLUMN_NAME_GAME_ID = "game_id";
		public static final String COLUMN_NAME_PLAYER_NAME = "player_name";
		//public static final String COLUMN_NAME_PLAYER_ID = "player_id";
		public static final String COLUMN_NAME_PLAYER_POSITION = "player_pos";
	}
	
	public static abstract class RiskGameTerritories implements BaseColumns {
		public static final String TABLE_NAME = "gameTerritories";
		public static final String COLUMN_NAME_GAME_ID = "game_id";
		public static final String COLUMN_NAME_TERRITORY_ID = "territory_id";
		public static final String COLUMN_NAME_OWNER = "owner";
		public static final String COLUMN_NAME_UNITS = "units";
	}
}

