package com.jeffsul.riskapp.login;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * LoginMediator facilitates communication with the Login server.
 */
public class LoginMediator {	
	
	// allows access to the sharedPreferences of this application
	private SharedPreferences sharedPreferences;
	
	// constructor
	// @Param context
	// the context of an Activity class
	public LoginMediator(Context context)
	{
		// gets the shared preference from the input context (context of an Activity)
		sharedPreferences = context.getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
	}
	
	// listener passed to the AsyncTask
	public interface Listener {
		public void onResponse(); //login response or create account response
	}
	
	// login elements ====================================================================================
	
	// executes the LoginAsyncTask
	//@Param listener
	//@Param username
	//@Param password
	private void attemptLogin(Listener listener, String username, String password) {
		new LoginAsyncTask(username, password).execute(listener);
	}

	// runs a background task asyncronously with the applications threads
	private class LoginAsyncTask extends AsyncTask<Listener, Void, Listener>
	{
		// the username and password attempting to be logged in with
		private String username;
		private String password;
		
		// constructor
		// @Param usn
		// @Param pw
		public LoginAsyncTask(String usn, String pw) {
	        super();
	        
	        username = usn;
	        password = pw;
	    }
		
		@Override
		// runs in the background along side the main thread
		protected Listener doInBackground(Listener... listeners)
		{
			// creates a GET request, sent to the web hosted server
			String stringResponse = makeGETLoginRequest(username, password);
			System.out.println("Response: " + stringResponse);
			
			//Response form, will not be used when server is real
			//stringResponse = "{'riskAppLoginResponse':{ 'verified':true, 'username':'" + username + "'} }";
			
			// collects the desired data from the GET response of the server
			boolean verified = false;
			String usernameResponded = "null";
			try {
				JSONObject jObject = new JSONObject(stringResponse);
				jObject = jObject.getJSONObject("riskAppLoginResponse");
				
				verified = jObject.getBoolean("verified");
				usernameResponded = jObject.getString("username");
			} catch (JSONException e) {
				System.out.println("Exception during JSON Parse: " + e.toString());
				usernameResponded = "**ErrorInLogin**";
		    }
			System.out.println("verified: " + verified + " username: " + usernameResponded);
			
			// check get response to make sure it verified
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
				// if the login was verified, set the Globally logged in user
				storeGlobalUser(username);
			}
			else
			{
				// if the login failed, mark the global user as not logged in
				storeGlobalUser("**NotLoggedIn**");
			}
			
			return listeners[0];
		}
		
		// runs after doinbackground
		@Override
		protected void onPostExecute(Listener listener) {
			// invokes the method waiting for a response
			listener.onResponse();
		}
	}
	
	// create account elements ====================================================================================

	// executes the CAccountAsyncTask
	//@Param listener
	//@Param username
	//@Param password
	private void attemptCAccount(Listener listener, String username, String password) {
		new CAccountAsyncTask(username, password).execute(listener);
	}
	
	private class CAccountAsyncTask extends AsyncTask<Listener, Void, Listener>
	{
		// the username and password attempting to create an account with
		String username;
		String password;
		
		public CAccountAsyncTask(String usn, String pw) {
	        super();
	        
	        username = usn;
	        password = pw;
	    }
		
		@Override
		// runs in the background as a parallel thread
		protected Listener doInBackground(Listener... listeners) 
		{
			// creates a GET request, sent to the web hosted server
			String stringResponse = makeGETCAccountRequest(username,  password);
			System.out.println("Response: " + stringResponse);
			
			//Response form, will not be used when server is real
			//stringResponse = "{'riskAppCAccountResponse':{ 'created':true, 'username':'" + username + "', 'usernameWasTaken': false} }";
			
			// collects the desired data from the GET response of the server
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
				usernameResponded = "**ErrorCreateAccount**";
		    }
			System.out.println("created: " + created + " username: " + usernameResponded + " usernameWasTaken: " + usernameWasTaken);
			
			// checks the GET response to see if it was verified
			boolean createVerified = true;
			if(!created || !username.equals(usernameResponded)) {
				createVerified = false;
			}
			
			// checks if the username was already taken
			boolean duplicate = false;
			if (usernameWasTaken) {
			  	duplicate = true;
			}
			
			if (createVerified && !duplicate)
			{	
				// if the user creation was verified and the user was not a duplicate, log them in
				storeGlobalUser(username);
			}
			else if (duplicate && !createVerified)
			{
				// if the username was already taken
				storeGlobalUser("**UsernameWasTaken**");
			}
			else
			{
				// some other case not created but not because of a duplicate
				storeGlobalUser("**NotLoggedIn**");
			}
			
			return listeners[0];
		}
		
		// runs after doinbackground
		@Override
		protected void onPostExecute(Listener listener) {
			listener.onResponse();
		}
	}
	
	// sends a GET request to the server with the username and password to login with and returns the GET response
	//@Param username
	//@Param password
	private String makeGETLoginRequest(String username, String password)
	{
		String loginURL = "http://wifinder-syde362.herokuapp.com/serverStubLogin?type=login&usn=" + username + "&pw=" + password; 
		return makeGETRequest(loginURL);
	}
	
	// sends a GET request to the server with the username and password to create an account with and returns the GET response
	//@Param username
	//@Param password
	private String makeGETCAccountRequest(String username, String password)
	{
		String cAccountURL = "http://wifinder-syde362.herokuapp.com/serverStubLogin?type=create&usn=" + username + "&pw=" + password; 
		return makeGETRequest(cAccountURL);
	}
	
	// creates and sends the GET request object to the provided URL
	// here there is a contract with the server that the GET request contains a 'type', 'usn' and 'pw' element
	// the 'type' being either login or create, there is also the contract that neither the username or password
	// contain spaces or certain characters including '*' as this may affect database querying
	//@Param url
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
	
	// stores a username as the globally logged in user of the App
	//@Param username
	private boolean storeGlobalUser(String username)
	{
		// this key is needed to retrieve the logged in user
		try {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(LoginActivity.SHARED_PREFS_KEY, username);
			editor.commit();
		} catch (Exception e) {
			System.out.println("Exception during share: " + e.toString());
		}
		return true;
	}
	
	// public method that tries to login with the given parameters
	//@Param username
	//@Param password
	//@Param loginListener
	public void login(String username, String password, Listener loginListener)
	{
		this.attemptLogin(loginListener, username, password);
	}
	
	// public method that tries to create an account with the given parameters
	//@Param username
	//@Param password
	//@Param loginListener
	public void createAccount(String username, String password, Listener caccountListener)
	{
		this.attemptCAccount(caccountListener, username, password);
		
		System.out.println("New Account Created for user: " + username);
	}
}
