package com.jeffsul.riskapp;

import com.jeffsul.riskapp.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
	}
	
	public void sendMessage(View view) {
		if (view.getId() == R.id.button_challenge_menu) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else if (view.getId() == R.id.button_load_game) {
			Intent intent = new Intent(this, LoadActivity.class);
			startActivity(intent);
		} else if (view.getId() == R.id.button_create_game) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
}
