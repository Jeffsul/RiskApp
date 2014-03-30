package com.jeffsul.riskapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;

public class LoginActivity extends Activity {	
	private static final int MAX_NUM_PLAYERS = 6;
	private static final int MIN_NUM_PLAYERS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
	}
	
	public void sendMessage(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		
		if (view.getId() == R.id.login_button)
		{
			System.out.println("login button clicked");
			
			EditText username_editText = (EditText) findViewById(R.id.username_textbox);
			String username_input = username_editText.getText().toString();
			EditText password_editText = (EditText) findViewById(R.id.password_textbox);
			String password_input = password_editText.getText().toString();
			
			System.out.println("Username: " + username_input);
			System.out.println("Password: " + password_input);
			
			///
			
			/// verify login
			//if verified
			startActivity(intent);
			
		}
		else if (view.getId() == R.id.create_account_button)
		{
			System.out.println("create account button clicked");
			
			EditText new_username_editText = (EditText) findViewById(R.id.new_username_textbox);
			String new_username_input = new_username_editText.getText().toString();
			EditText new_password_editText = (EditText) findViewById(R.id.new_password_textbox);
			String new_password_input = new_password_editText.getText().toString();
			EditText new_confirm_password_editText = (EditText) findViewById(R.id.new_confirm_password_textbox);
			String new_confirm_password_input = new_confirm_password_editText.getText().toString();
			
			if (new_password_input.equals(new_confirm_password_input))
			{
				System.out.println("New Password confirmed");
			}
			else
			{
				System.out.println("Passwords do not match: " + new_password_input + " " + new_confirm_password_input);
			}
			
			// verify new login data
			
			//--> startActivity(intent);
			
		}
			
		//startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
