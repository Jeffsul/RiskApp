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

public class ChallengeFacade extends ChallengeActivity {
	
	private ArrayList<Listener> listeners;
	final static String SERVER_URL = "http://10.20.67.126:1337/serverStub";
	
	public interface Listener {
		public void onChallengeResponse(JSONArray response);
	}
	
	public static void createChallenge(final Listener clistener) {
		new AsyncTask<Listener, Void, JSONArray>() {
			final Listener listener = clistener;
		
			@Override
			protected JSONArray doInBackground(Listener... listeners) {
				// Do Server Call - TO-DO: Figure out how to send push notifications and how to send different types of server calls all in this method
				for (int i = 0; i <1000; i++){
					System.out.println(i);
				}
				return new JSONArray();
			}
			
			@Override
			protected void onPostExecute(JSONArray response) {
				// Check if failed, declined, or accepted
				listener.onChallengeResponse(response);
			}
			
		}.execute(clistener);
	}
	
	public static void getChallenges(final Listener cListener) {
		new AsyncTask<Listener, Void, JSONArray>() {
		
			final Listener listener = cListener;
	
			@Override
			protected JSONArray doInBackground(Listener...listeners) {
				HttpClient client = new DefaultHttpClient(new BasicHttpParams());
				HttpGet httpget = new HttpGet(SERVER_URL);
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
				    JSONArray challengeArray = jsonObj.getJSONArray("challenges");
				    return challengeArray;
				}
				catch(Exception e) {
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(JSONArray challenges) {
				listener.onChallengeResponse(challenges);
			}
		}.execute(cListener);
	}
}