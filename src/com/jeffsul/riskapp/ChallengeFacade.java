package com.jeffsul.riskapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask; 

/**
 * ChallengeFacade facilitates communication with the server regarding challenge requests.
 */
public class ChallengeFacade {
	private final static String SERVER_URL = "http://wifinder-syde362.herokuapp.com/serverStub";

	/**
	 * Interface for receiving challenge responses from the server.
	 */
	public interface Listener {
		public void onChallengeResponse(JSONArray response);
	}

	/**
	 * Initiates a request to create a new challenge.
	 * @param cListener
	 * @param username
	 */
	public static void createChallenge(final Listener cListener, final String username) {
		new AsyncTask<Listener, Listener, JSONArray>() {
			final Listener listener = cListener;
			final String userName = username;

			@Override
			protected void onPreExecute(){
				listener.onChallengeResponse(null);
			}
			
			@Override
			protected JSONArray doInBackground(Listener... listeners) {
				ArrayList<String> params = new ArrayList<String>();
				params.add(userName); // name of user being challenged
				return callServer("create", params);
			}
			
			@Override
			protected void onPostExecute(JSONArray response) {
				listener.onChallengeResponse(response);
			}
			
		}.execute(cListener);
	}

	/**
	 * Initiates a request to return all active challenges.
	 * @param cListener
	 */
	public static void getChallenges(final Listener cListener) {
		new AsyncTask<Listener, Void, JSONArray>() {
		
			final Listener listener = cListener;
	
			@Override
			protected JSONArray doInBackground(Listener...listeners) {
				return callServer("challenges", null); // get challenges
			}
			
			@Override
			protected void onPostExecute(JSONArray challenges) {
				listener.onChallengeResponse(challenges);
			}
		}.execute(cListener);
	}
	
	private static JSONArray callServer(String queryString, ArrayList<String> params) {
		HttpClient client = new DefaultHttpClient(new BasicHttpParams());
		String qString = SERVER_URL + "?type=" + queryString;
		
		if (params != null) {
			String user = params.get(0);
			qString += ("&username=" + user);
		}
		
		HttpGet httpget = new HttpGet(qString);
		String result = null;
		
		try {
			HttpResponse response = client.execute(httpget);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
		    StringBuilder sb = new StringBuilder();
		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		        sb.append(line + "\n");
		    }
		    result = sb.toString();
		    JSONObject jsonObj = new JSONObject(result);
		    JSONArray challengeArray = jsonObj.getJSONArray(queryString);
		    return challengeArray;
		}
		catch(Exception e) {
			return null;
		}
	}
}