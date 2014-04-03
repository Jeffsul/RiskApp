package com.jeffsul.riskapp.login;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;

public class LoginMediator {	
	
	SharedPreferences sharedPreferences;
	
	public LoginMediator(Context context)
	{
		sharedPreferences = context.getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
	}
	
	private ArrayList<Listener> listeners;
	
	public interface Listener {
		public void onLoginResponse(); //login response?
	}
	
	public void attemptLogin(Listener listener, String username, String password) { //static
		new LoginAsyncTask(username, password).execute(listener);
	}

	private class LoginAsyncTask extends AsyncTask<Listener, Void, Listener> { //static
		
		String username;
		String password;
		
		public LoginAsyncTask(String usn, String pw) {
	        super();
	        
	        username = usn;
	        password = pw;
	    }
		
		@Override
		protected Listener doInBackground(Listener... listeners) {
			// will be the server address to login with
			String url = "http://wifinder-syde362.herokuapp.com/home?RSSI=" + username + "&ID=" + password + "&token=wfs"; 
			
			HttpResponse response = null;
			String stringRespons = "null";
			try {
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpGet request = new HttpGet(url);
			    response = httpclient.execute(request);
			    stringRespons = EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				System.out.println("Exception during GET: " + e.toString());
			}
			System.out.println("Response: " + stringRespons);
			
			/* Response form
			 * { riskAppLoginResponse: [
			 * 			{ "verified":true, "username":"nolan" }
			 * 		]
			 * }
			 * 
			 */
			
			// check get response for verified
			boolean loginVerified = true;
			
			/*
			 * if(stringRespons -- transformed into JSON -- .getVal("verified") != true OR !username.equals(.getVal("username")) )
			 * {
			 * 		not logged in
			 * 		loginVerified = false;
			 * }
			 * else
			 * {
			 * 		loginVerified = true;
			 * }
			 */
			
			if (loginVerified)
			{	
				storeGlobalUser(username);
				//System.out.println("Shared Perference: " + userKey + " as: " + username);
			}
			else
			{
				storeGlobalUser("NotLoggedIn");
			}
			
			return listeners[0];
		}
		
		// runs after doinbackground
		@Override
		protected void onPostExecute(Listener listener) {
			listener.onLoginResponse();
		}
	}
	
	private boolean storeGlobalUser(String username)
	{
		String userKey = "com.example.app.user";
		try {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(userKey, username);
			editor.commit();
		} catch (Exception e) {
			System.out.println("Exception during share: " + e.toString());
		}
		return true;
	}
	
	public boolean login(String username, String password, Listener loginListener)
	{
		
//		Listener loginListener = new Listener() {
//			public void onLoginResponse() {
//				System.out.println("login responded to");
//			}
//		};
		this.attemptLogin(loginListener, username, password);
		
		//System.out.println("Login Verified for user: " + username);
		return true;
	}
	
	public boolean createAccount(String username, String password)
	{
		
		System.out.println("New Account Created for user: " + username);
		return true;
	}
}
