package com.jeffsul.riskapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LoadActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);

		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(new LoadGameAdapter(this));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
				Intent intent = new Intent(LoadActivity.this, GameActivity.class);
				intent.putExtra(GameActivity.GAME_ID_EXTRA, id);
				startActivity(intent);
			}
		});
	}
}
