package com.jeffsul.riskapp;

import com.jeffsul.riskapp.db.RiskGameContract.RiskGame;
import com.jeffsul.riskapp.db.RiskGameContract.RiskGamePlayers;
import com.jeffsul.riskapp.db.RiskGameDbHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * MainActivity is a screen to create a local game.
 */
public class MainActivity extends Activity implements OnItemSelectedListener {	
	private static final int MAX_NUM_PLAYERS = 6;
	private static final int MIN_NUM_PLAYERS = 2;

	private int numPlayers = MIN_NUM_PLAYERS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getIntent().getBooleanExtra("networked", false)) {
			Intent intent = new Intent(this, GameActivity.class);
			intent.putExtra(GameActivity.GAME_ID_EXTRA, saveNewNetworkedGame(getIntent().getStringArrayExtra("players")));
			startActivity(intent);
			finish();
		}
		
		setContentView(R.layout.activity_main);
	
		// Initialize drop-down game creation options:
		Spinner spinner = (Spinner) findViewById(R.id.number_players_spinner);
		CharSequence[] objects = new CharSequence[MAX_NUM_PLAYERS - MIN_NUM_PLAYERS + 1];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = Integer.toString(i + MIN_NUM_PLAYERS);
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, objects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
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

		for (int i = 0; i < MIN_NUM_PLAYERS; i++) {
			addPlayer(i + 1);
		}
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
		
		ViewGroup playerListView = (ViewGroup) findViewById(R.id.player_list);
		for (int i = 0; i < numPlayers; i++) {
			ContentValues values2 = new ContentValues();
			values2.put(RiskGamePlayers.COLUMN_NAME_GAME_ID, gameId);
			String name = ((EditText) ((LinearLayout) playerListView.getChildAt(i)).findViewById(R.id.player_name_edittext))
					.getText().toString();
			values2.put(RiskGamePlayers.COLUMN_NAME_PLAYER_NAME, name);
			values2.put(RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION, i);
			db.insert(RiskGamePlayers.TABLE_NAME, "null", values2);
		}
		return gameId;
	}

	private long saveNewNetworkedGame(String[] playerNames) {
		RiskGameDbHelper helper = new RiskGameDbHelper(this);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RiskGame.COLUMN_NAME_CREATED, Long.toString(System.currentTimeMillis()));
		values.put(RiskGame.COLUMN_NAME_LAST_PLAYED, Long.toString(System.currentTimeMillis()));
		// int mapSetting = ((Spinner) findViewById(R.id.spinner_cards_setting)).getSelectedItemPosition();
		values.put(RiskGame.COLUMN_NAME_MAP_ID, "map_classic");
		//int cardSetting = ((Spinner) findViewById(R.id.spinner_cards_setting)).getSelectedItemPosition();
		values.put(RiskGame.COLUMN_NAME_TURN_COUNTER, 0);
		int numPlayers = 2;
		values.put(RiskGame.COLUMN_NAME_NUM_PLAYERS, numPlayers);
		long gameId = db.insert(RiskGame.TABLE_NAME, "null", values);
		System.out.println(gameId);
		
		for (int i = 0; i < numPlayers; i++) {
			ContentValues values2 = new ContentValues();
			values2.put(RiskGamePlayers.COLUMN_NAME_GAME_ID, gameId);
			values2.put(RiskGamePlayers.COLUMN_NAME_PLAYER_NAME, playerNames[i]);
			System.out.println(playerNames[i]);
			values2.put(RiskGamePlayers.COLUMN_NAME_PLAYER_POSITION, i);
			db.insert(RiskGamePlayers.TABLE_NAME, "null", values2);
		}
		return gameId;
	}

	/**
	 * Handles button click.
	 * @param view
	 */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GameActivity.GAME_ID_EXTRA, saveNewGame());
		startActivity(intent);
		finish();
	}

	private void addPlayer(int n) {				
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		TextView label = new TextView(this);
		label.setText(Integer.toString(n));
		layout.addView(label);
	    
		EditText playerNameField = new EditText(this);
		playerNameField.setId(R.id.player_name_edittext);
		playerNameField.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		playerNameField.setMinWidth(400);
		playerNameField.setText("Player " + n);
		layout.addView(playerNameField);
		
		ToggleButton toggle = new ToggleButton(this);
		toggle.setTextOn("Human");
		toggle.setTextOff("AI");
		toggle.setChecked(true);
		layout.addView(toggle);
		
		((ViewGroup) findViewById(R.id.player_list)).addView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		int num = pos + MIN_NUM_PLAYERS;
		if (num > numPlayers) {
			for (int i = numPlayers + 1; i <= num; i++) {
				addPlayer(i);
			}
		} else if (num < numPlayers) {
			ViewGroup playerListView = (ViewGroup) findViewById(R.id.player_list);
			int count = numPlayers - num;
			playerListView.removeViews(playerListView.getChildCount() - count, count);
		}
		numPlayers = num;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
}
