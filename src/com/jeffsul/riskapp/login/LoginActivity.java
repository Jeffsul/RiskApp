package com.jeffsul.riskapp.login;

import com.jeffsul.riskapp.ChallengeActivity;
import com.jeffsul.riskapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {	
	private static final String SHARED_PREFS_KEY = "com.jeffsul.riskapp.login";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		SharedPreferences prefs = getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
		String username = prefs.getString(SHARED_PREFS_KEY, null);
		if (username != null) {
			advanceScreen();
		}
	}
	
	// function invoked when a button on the login screen is clicked
	public void loginScreenButtonClicked(View view)
	{
		// if the login button was the one pressed
		if (view.getId() == R.id.login_button)
		{
			loginButtonClicked();
		}
		// if the create account button was the one pressed
		else if (view.getId() == R.id.create_account_button)
		{
			create_account_button();
		}
	}
	
	// login elements ====================================================================================
	
	// login button functionality
	private void loginButtonClicked()
	{
		System.out.println("login button clicked");
		
		// creates an instance of the loginMediator for communication with the server
		LoginMediator loginMediator = new LoginMediator(this.getApplicationContext());
		
		// collects the input in the login fields
		String username_input = getTextboxText(R.id.username_textbox);
		String password_input = getTextboxText(R.id.password_textbox);
		
		System.out.println("Username: " + username_input);
		System.out.println("Password: " + password_input);
		
		// force input to match some criteria
		boolean inputIsValid = true;
		if (username_input.equals("") || password_input.equals(""))
			inputIsValid = false;
		if (username_input.contains(" ") || password_input.contains(" "))
			inputIsValid = false;
		if (username_input.equals(password_input))
			inputIsValid = false;
		if (username_input.contains("*") || password_input.contains("*"))
			inputIsValid = false;
		
		// if the input is valid, go on to ask the server if it is correct
		if (inputIsValid)
		{
			// creates a listener, this invokes the function when a response is obtained
			LoginMediator.Listener loginListener = new LoginMediator.Listener() {
				public void onResponse() {
					System.out.println("login responded to");
					loginResponded();
				}
			};
			// uses the loginMediator to check the server for the input data
			loginMediator.login(username_input, password_input, loginListener);
		}
		else
		{
			System.out.println("Login information was not correcly formatted");
			createDialogBox("The input is not valid!");
		}
	}
	
	// this function is called when the listener is invoked
	private void loginResponded()
	{
		String username = "null";
		//get the global user
		SharedPreferences prefs = this.getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
		String userKey = "com.example.app.user";
		username = prefs.getString(userKey, "no user found"); 
		
		// if the stored user was not marked as still not logged in
		if (!(username.equals("**NotLoggedIn**")))
		{
			System.out.println("Sucessfull login for user: " + username);
			// advance to the main menu
			advanceScreen();
		}
		// if the stored user was not logged in
		else
		{
			System.out.println("Failed Login");
			incorrectLogin();
		}
	}
	
	// create account elements ====================================================================================
	
	// create account button functionality
	private void create_account_button()
	{
		System.out.println("Create account button clicked");
		
		// creates an instance of the loginMediator for communication with the server
		LoginMediator loginMediator = new LoginMediator(this.getApplicationContext());
		
		// collects the user input from the create account fields
		String new_username_input = getTextboxText(R.id.new_username_textbox);
		String new_password_input = getTextboxText(R.id.new_password_textbox);
		String new_confirm_password_input = getTextboxText(R.id.new_confirm_password_textbox);
		
		// force input to match some criteria
		boolean inputIsValid = true;
		if (new_username_input.equals("") || new_password_input.equals(""))
			inputIsValid = false;
		if (new_username_input.contains(" ") || new_password_input.contains(" "))
			inputIsValid = false;
		if (new_username_input.equals(new_password_input))
			inputIsValid = false;
		if (new_username_input.contains("*") || new_password_input.contains("*"))
			inputIsValid = false;
				
		// if the password and confirmed password match and the input is valid
		if (new_password_input.equals(new_confirm_password_input) && inputIsValid)
		{	
			// verify the new account data
			LoginMediator.Listener caccountListener = new LoginMediator.Listener() {
				public void onResponse() {
					System.out.println("create account responded to");
					cAccountResponded();
				}
			};
			loginMediator.createAccount(new_username_input, new_password_input, caccountListener);
		}
		else if (inputIsValid)
		{
			System.out.println("Passwords do not match: " + new_password_input + " " + new_confirm_password_input);
			
			createDialogBox("The passwords did not match!");
		}
		else
		{
			createDialogBox("The input is not valid!");
		}
	}
	
	// the method called when a server response is obtained
	public void cAccountResponded()
	{
		String username = "null";
		//get the global user
		SharedPreferences prefs = this.getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
		String userKey = "com.example.app.user";
		username = prefs.getString(userKey, "no user found"); 
		
		// if the global user was marked as bad, display a message
		if (username.equals("**UsernameWasTaken**"))
		{
			createDialogBox("This username has already been taken!");
		}
		else if (username.equals("**NotLoggedIn**"))
		{
			createDialogBox("Something went wrong creating the account...");
		}
		// if the user was created and logged in successful
		else
		{
			System.out.println("Sucessfull login for user: " + username);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			// set dialog message
			alertDialogBuilder
				.setMessage("Your account was created successfully! \n Welcome " + username + "!!")
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// advance when button is clicked
						advanceScreen();
					}
				  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
		}
	}
	
	// UI elements ====================================================================================

	// gets the text from a editText field on the UI by its ID
	private String getTextboxText(int id)
	{
		EditText editText = (EditText) findViewById(id);
		String input = editText.getText().toString();
		
		return input;
	}
	
	// moves the App to the main menu screen
	private void advanceScreen()
	{
		Intent intent = new Intent(this, ChallengeActivity.class);
		
		startActivity(intent);
		finish();
	}
	
	// displays a dialog box indicating an incorrect login attempt
	private void incorrectLogin()
	{
		createDialogBox("The Login information was Incorrect!");
	}
	
	// creates an 'ok' dialog box with an inputed message
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
