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
	private static final String SERVER_URL = "http://wifinder-syde362.herokuapp.com/serverStub";

	private static final String QUERY_TYPE_CREATE = "create";
	private static final String QUERY_TYPE_CHALLENGES = "challenges";

	/**
	 * Interface for receiving challenge responses from the server.
	 */
	public interface Listener {
		public void onChallengeResponse(JSONArray response);
	}

	/**
	 * Initiates a request to create a new challenge.
	 */
	public static void createChallenge(final Listener cListener, final String username) {
		new AsyncTask<Listener, Listener, JSONArray>() {
			@Override
			protected void onPreExecute(){
				cListener.onChallengeResponse(null);
			}
			
			@Override
			protected JSONArray doInBackground(Listener... listeners) {
				ArrayList<String> params = new ArrayList<String>();
				params.add(username); // name of user being challenged
				return callServer(QUERY_TYPE_CREATE, params);
			}
			
			@Override
			protected void onPostExecute(JSONArray response) {
				cListener.onChallengeResponse(response);
			}
			
		}.execute(cListener);
	}

	/**
	 * Initiates a request to return all active challenges.
	 */
	public static void getChallenges(final Listener cListener) {
		new AsyncTask<Listener, Void, JSONArray>() {
			@Override
			protected JSONArray doInBackground(Listener...listeners) {
				return callServer(QUERY_TYPE_CHALLENGES, null); // get challenges
			}
			
			@Override
			protected void onPostExecute(JSONArray challenges) {
				cListener.onChallengeResponse(challenges);
			}
		}.execute(cListener);
	}

	/**
	 * calls the external web server with an HTTP GET request to either create a challenge or get a list of challenges
	 * @return a server-generated JSON array of data
	 */
	private static JSONArray callServer(String queryString, ArrayList<String> params) {
		HttpClient client = new DefaultHttpClient(new BasicHttpParams());
		String qString = SERVER_URL + "?type=" + queryString;

		if (params != null) { // if the request is to challenge a user
			String user = params.get(0);
			qString += "&username=" + user;
		}
		
		try { // execute the external server request
			HttpGet httpget = new HttpGet(qString);
			HttpResponse response = client.execute(httpget);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			JSONObject jsonObj = new JSONObject(sb.toString());
			return jsonObj.getJSONArray(queryString);
		} catch (Exception e) {}
		return null;
	}
}