package com.jeffsul.riskapp.login;

import com.jeffsul.riskapp.MainActivity;
import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.R.id;
import com.jeffsul.riskapp.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void sendMessage(View view) {
		
		if (view.getId() == R.id.login_button)
		{
			loginButtonClicked();
		}
		else if (view.getId() == R.id.create_account_button)
		{
			create_account_button();
		}
	}
	
	private void loginButtonClicked()
	{
		System.out.println("login button clicked");
		
		LoginMediator loginMediator = new LoginMediator();
		
		String username_input = getTextboxText(R.id.username_textbox);
		String password_input = getTextboxText(R.id.password_textbox);
		
		System.out.println("Username: " + username_input);
		System.out.println("Password: " + password_input);
		
		///
		
		/// verify login
		if (loginMediator.login(username_input, password_input))
		{
			advanceScreen();
		}
		
		//else
		// incorrectLogin()
	}
	
	private void create_account_button()
	{
		System.out.println("create account button clicked");
		
		LoginMediator loginMediator = new LoginMediator();
		
		String new_username_input = getTextboxText(R.id.new_username_textbox);
		String new_password_input = getTextboxText(R.id.new_password_textbox);
		String new_confirm_password_input = getTextboxText(R.id.new_confirm_password_textbox);
		
		if (new_password_input.equals(new_confirm_password_input))
		{
			System.out.println("New Password confirmed");
		}
		else
		{
			System.out.println("Passwords do not match: " + new_password_input + " " + new_confirm_password_input);
			
			// doesnt actually go here
			createDialogBox("The Passwords did not match!");
		}
		
		// verify new login data
		if (loginMediator.createAccount(new_username_input, new_password_input))
		{
			advanceScreen();
		}
	}

	private String getTextboxText(int id)
	{
		EditText editText = (EditText) findViewById(id);
		String input = editText.getText().toString();
		
		return input;
	}
	
	private void advanceScreen()
	{
		Intent intent = new Intent(this, MainActivity.class);
		
		startActivity(intent);
	}
	
	private void incorrectLogin()
	{
		createDialogBox("The Login information was Incorrect!");
	}
	
	private void createDialogBox (String message)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
				}
			  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
	}
}
