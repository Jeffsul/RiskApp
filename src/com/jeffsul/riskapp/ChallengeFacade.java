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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask; 
import android.widget.TableLayout;

public class ChallengeFacade {
	
	private ArrayList<Listener> listeners;
	private final static String SERVER_URL = "http://wifinder-syde362.herokuapp.com/serverStub";
	
	public interface Listener {
		public void onChallengeResponse(JSONArray response);
	}
	
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
				ArrayList<String> params = new ArrayList();
				params.add(userName); // name of user being challenged
				JSONArray response = null;
				try {
					response = new JSONArray().put(0, new JSONObject().put("status", "accepted").put("username", "each3ric").put("id", 1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return response;
				//return callServer("create", params);
			}
			
			@Override
			protected void onPostExecute(JSONArray response) {
				listener.onChallengeResponse(response);
			}
			
		}.execute(cListener);
	}
	
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