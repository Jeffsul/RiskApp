package com.jeffsul.riskapp.login;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;

public class LoginMediator {	
	
	public boolean login(String username, String password)
	{
		
		System.out.println("Login Verified for user: " + username);
		return true;
	}
	
	public boolean createAccount(String username, String password)
	{
		
		System.out.println("New Account Created for user: " + username);
		return true;
	}
}
