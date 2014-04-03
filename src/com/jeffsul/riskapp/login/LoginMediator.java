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
	
	private ArrayList<Listener> listeners;
	
	public interface Listener {
		public void onLoginResponse(); //login response?
	}
	
	public static void attemptLogin(Listener listener, String username, String password) {
		new LoginAsyncTask(username, password).execute(listener);
	}

	private static class LoginAsyncTask extends AsyncTask<Listener, Void, Listener> {
		
		String username = "user";
		String password = "pw";
		
		public LoginAsyncTask(String usn, String pw) {
	        super();
	        
	        username = usn;
	        password = pw;
	        // do stuff
	    }
		
		@Override
		protected Listener doInBackground(Listener... listeners) {
			// Do Server Call - TO-DO: Figure out how to send push notifications and how to send different types of server calls all in this method
			
			//String username = "user";
			//String password = "pw";
			
			// will be the server address to login with
			String url = "http://wifinder-syde362.herokuapp.com/home?RSSI=" + username + "&ID=" + password + "&token=wfs"; 
			
			HttpResponse response = null;
			String stringRespons = "null";
			try {
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpGet request = new HttpGet(url);
			    response = httpclient.execute(request);
			    stringRespons = EntityUtils.toString(response.getEntity());
			    System.out.println("Response: " + stringRespons);
			} catch (Exception e) {
				System.out.println("Exception: " + e.toString());
			}
			
			return listeners[0];
		}
		
		// runs after doinbackground
		@Override
		protected void onPostExecute(Listener listener) {
			listener.onLoginResponse();
		}
	}
	
	public boolean login(String username, String password)
	{
		
		Listener loginListener = new Listener() {
			public void onLoginResponse() {
				System.out.println("login responded to");
			}
		};
		LoginMediator.attemptLogin(loginListener, username, password);
		
		System.out.println("Login Verified for user: " + username);
		return true;
	}
	
	public boolean createAccount(String username, String password)
	{
		
		System.out.println("New Account Created for user: " + username);
		return true;
	}
}
