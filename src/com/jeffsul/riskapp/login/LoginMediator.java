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
import org.json.JSONException;
import org.json.JSONObject;

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
		public void onResponse(); //login response or create account response?
	}
	
	public void attemptLogin(Listener listener, String username, String password) {
		new LoginAsyncTask(username, password).execute(listener);
	}

	private class LoginAsyncTask extends AsyncTask<Listener, Void, Listener> {
		
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
			
			String stringResponse = makeGETLoginRequest(username, password);
			
			System.out.println("Response: " + stringResponse);
			
			//Response form, will not be used when server is real
			stringResponse = "{'riskAppLoginResponse':{ 'verified':true, 'username':'" + username + "'} }";
			  
			boolean verified = false;
			String usernameResponded = "null";
			
			try {
				JSONObject jObject = new JSONObject(stringResponse);
				jObject = jObject.getJSONObject("riskAppLoginResponse");
				
				verified = jObject.getBoolean("verified");
				usernameResponded = jObject.getString("username");
			} catch (JSONException e) {
				System.out.println("Exception during JSON Parse: " + e.toString());
		    }
			System.out.println("verified: " + verified + " username: " + usernameResponded);
			
			// check get response for verified
			boolean loginVerified = true;
			
			if(!verified || !username.equals(usernameResponded))
			{
				//not logged in
				loginVerified = false;
			}
			else if (verified && username.equals(usernameResponded))
			{
				loginVerified = true;
			}
			
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
			listener.onResponse();
		}
	}
	
	public void attemptCAccount(Listener listener, String username, String password) {
		new CAccountAsyncTask(username, password).execute(listener);
	}
	
	private class CAccountAsyncTask extends AsyncTask<Listener, Void, Listener> {
		
		String username;
		String password;
		
		public CAccountAsyncTask(String usn, String pw) {
	        super();
	        
	        username = usn;
	        password = pw;
	    }
		
		@Override
		protected Listener doInBackground(Listener... listeners) {
			// will be the server address to login with
			
			String stringResponse = makeGETCAccountRequest(username,  password);
					
			System.out.println("Response: " + stringResponse);
			
			//Response form, will not be used when server is real
			stringResponse = "{'riskAppCAccountResponse':{ 'created':true, 'username':'" + username + "', 'usernameWasTaken': false} }";
			  
			boolean created = false;
			String usernameResponded = "null";
			boolean usernameWasTaken = false;
			
			try {
				JSONObject jObject = new JSONObject(stringResponse);
				jObject = jObject.getJSONObject("riskAppCAccountResponse");
				
				created = jObject.getBoolean("created");
				usernameResponded = jObject.getString("username");
				usernameWasTaken = jObject.getBoolean("usernameWasTaken");
			} catch (JSONException e) {
				System.out.println("Exception during JSON Parse: " + e.toString());
		    }
			System.out.println("created: " + created + " username: " + usernameResponded + " usernameWasTaken: " + usernameWasTaken);
			
			// check get response for verified
			boolean createVerified = true;
			
			if(!created || !username.equals(usernameResponded)) {
			 	//not logged in
				createVerified = false;
			}
			else {
				createVerified = true;
			}
			
			boolean duplicate = false;
			
			
			if (usernameWasTaken) {
			 	//was a duplicate
			  	duplicate = true;
			}
			else {
				duplicate = false;
			}
			
			if (createVerified && !duplicate)
			{	
				storeGlobalUser(username);
				//System.out.println("Shared Perference: " + userKey + " as: " + username);
			}
			else if (duplicate && !createVerified)
			{
				storeGlobalUser("UsernameWasTaken");
			}
			else
			{
				// some other case not created but not duplicate
				storeGlobalUser("NotLoggedIn");
			}
			
			return listeners[0];
		}
		
		// runs after doinbackground
		@Override
		protected void onPostExecute(Listener listener) {
			listener.onResponse();
		}
	}
	
	private String makeGETLoginRequest(String username, String password)
	{
		String loginURL = "http://wifinder-syde362.herokuapp.com/home?RSSI=" + username + "&ID=" + password + "&token=wfs"; 
		return makeGETRequest(loginURL);
	}
	
	private String makeGETCAccountRequest(String username, String password)
	{
		String cAccountURL = "http://wifinder-syde362.herokuapp.com/home?RSSI=" + username + "&ID=" + password + "&token=wfs"; 
		return makeGETRequest(cAccountURL);
	}
	
	private String makeGETRequest(String url)
	{
		HttpResponse response = null;
		String stringResponse = "null";
		try {
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpGet request = new HttpGet(url);
		    response = httpclient.execute(request);
		    stringResponse = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			System.out.println("Exception during GET: " + e.toString());
		}
		return stringResponse;
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
		this.attemptLogin(loginListener, username, password);
		
		//System.out.println("Login Verified for user: " + username);
		return true;
	}
	
	public boolean createAccount(String username, String password, Listener caccountListener)
	{
		this.attemptCAccount(caccountListener, username, password);
		
		System.out.println("New Account Created for user: " + username);
		return true;
	}
}
