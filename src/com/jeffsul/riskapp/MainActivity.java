package com.jeffsul.riskapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {	
	private static final int MAX_NUM_PLAYERS = 6;
	private static final int MIN_NUM_PLAYERS = 2;

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
	
	public void sendMessage(View view) {
		if (view.getId() == R.id.button_load_game) {
			Intent intent = new Intent(this, LoadActivity.class);
			startActivity(intent);
		} else if (view.getId() == R.id.button_create_challenge){
			Intent intent = new Intent(this, ChallengeActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, GameActivity.class);
			intent.putExtra(GameActivity.NUM_PLAYERS_EXTRA,
					((Spinner) findViewById(R.id.number_players_spinner)).getSelectedItemPosition() + MIN_NUM_PLAYERS);
			intent.putExtra(GameActivity.MAP_EXTRA, ((Spinner) findViewById(R.id.spinner_cards_setting)).getSelectedItemPosition());
			intent.putExtra(GameActivity.CARD_SETTING_EXTRA, 
					((Spinner) findViewById(R.id.spinner_cards_setting)).getSelectedItemPosition());
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
