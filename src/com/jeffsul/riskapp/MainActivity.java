package com.jeffsul.riskapp;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGamePlayers;
import com.jeffsul.riskapp.db.RiskGameDbHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {	
	private static final int MAX_NUM_PLAYERS = 6;
	private static final int MIN_NUM_PLAYERS = 2;
	private static final int CHALLENGE = 1;
	private static final String LOCAL_PLAYER_TYPE = "Local player's name";
	private static final String AI_PLAYER_TYPE = "AI's name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Spinner spinner = (Spinner) findViewById(R.id.number_players_spinner);
		CharSequence[] objects = new CharSequence[MAX_NUM_PLAYERS - MIN_NUM_PLAYERS + 1];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = Integer.toString(i + MIN_NUM_PLAYERS);
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, objects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		Spinner mapSpinner = (Spinner) findViewById(R.id.spinner_map_type);
		ArrayAdapter<CharSequence> mapAdapter = ArrayAdapter.createFromResource(this,
				R.array.option_map_types, android.R.layout.simple_spinner_item);
		mapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mapSpinner.setAdapter(mapAdapter);
		
		Spinner cardsSpinner = (Spinner) findViewById(R.id.spinner_cards_setting);
		ArrayAdapter<CharSequence> cardsAdapter = ArrayAdapter.createFromResource(this,
				R.array.option_cards_settings, android.R.layout.simple_spinner_item);
		cardsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cardsSpinner.setAdapter(cardsAdapter);
	}
	
	private long saveNewGame() {
		RiskGameDbHelper helper = new RiskGameDbHelper(this);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RiskGame.COLUMN_NAME_CREATED, Long.toString(System.currentTimeMillis()));
		values.put(RiskGame.COLUMN_NAME_LAST_PLAYED, Long.toString(System.currentTimeMillis()));
		// int mapSetting = ((Spinner) findViewById(R.id.spinner_cards_setting)).getSelectedItemPosition();
		values.put(RiskGame.COLUMN_NAME_MAP_ID, "map_classic");
		//int cardSetting = ((Spinner) findViewById(R.id.spinner_cards_setting)).getSelectedItemPosition();
		values.put(RiskGame.COLUMN_NAME_TURN_COUNTER, 0);
		int numPlayers = ((Spinner) findViewById(R.id.number_players_spinner)).getSelectedItemPosition() + MIN_NUM_PLAYERS;
		values.put(RiskGame.COLUMN_NAME_NUM_PLAYERS, numPlayers);
		long gameId = db.insert(RiskGame.TABLE_NAME, "null", values);
		for (int i = 0; i < numPlayers; i++) {
			ContentValues values2 = new ContentValues();
			values2.put(RiskGamePlayers.COLUMN_NAME_GAME_ID, gameId);
			values2.put(RiskGamePlayers.COLUMN_NAME_PLAYER_NAME, "Player " + i);
			values2.put(RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION, i);
			db.insert(RiskGamePlayers.TABLE_NAME, "null", values2);
		}
		return gameId;
	}
	
	public void sendMessage(View view) {
		if (view.getId() == R.id.button_challenge_menu) {
			Intent intent = new Intent(this, ChallengeActivity.class);
			startActivity(intent);
		}
		
		else if (view.getId() == R.id.button_add_local) {
			CharSequence newPlayerHint = "Local player's name";
			addPlayer(newPlayerHint);
		}
		
		else if (view.getId() == R.id.button_add_ai) {
			CharSequence newPlayerHint = "AI's name";
			addPlayer(newPlayerHint);
		}
		
		else if (view.getId() == R.id.button_load_game) {
			Intent intent = new Intent(this, LoadActivity.class);
			startActivity(intent);
		} 

		else {
			Intent intent = new Intent(this, GameActivity.class);
			intent.putExtra(GameActivity.GAME_ID_EXTRA, saveNewGame());
			startActivity(intent);
		}
	}
	
	private void addPlayer(CharSequence type) {
		String defaultPlayerName = "";
		String defaultLabelName = type.toString();
		
		LinearLayout playerList = (LinearLayout) findViewById(R.id.player_list);
		
		LinearLayout layout = new LinearLayout(this);
	    layout.setOrientation(LinearLayout.HORIZONTAL);
	    layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	    TextView label = new TextView(this);
	    label.setText(defaultLabelName);
	    layout.addView(label);
	    
		EditText playerNameField = new EditText(this);
		playerNameField.setGravity(Gravity.CENTER);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		playerNameField.setLayoutParams(params);
		playerNameField.setHint("Enter name");
		playerNameField.setText(defaultPlayerName);
		layout.addView(playerNameField);
		
		playerList.addView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
