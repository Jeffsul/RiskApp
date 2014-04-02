package com.jeffsul.riskapp;

import java.net.URL;
import java.util.ArrayList;

import android.os.AsyncTask; 

public class ChallengeFacade extends ChallengeActivity {
	
	private ArrayList<Listener> listeners;
	
	public interface Listener {
		public void onChallengeResponse();
	}
	
	public static void createChallenge(Listener listener) {
		new ServerAsyncTask().execute(listener);
	}

	private static class ServerAsyncTask extends AsyncTask<Listener, Void, Listener> {
		
		@Override
		protected Listener doInBackground(Listener... listeners) {
			// Do Server Call - TO-DO: Figure out how to send push notifications and how to send different types of server calls all in this method
			return listeners[0];
		}
		
		@Override
		protected void onPostExecute(Listener listener) {
			listener.onChallengeResponse();
		}
	}
}