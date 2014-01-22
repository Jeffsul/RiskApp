package com.jeffsul.riskapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
