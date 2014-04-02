package com.jeffsul.riskapp;

import java.util.ArrayList;

public class ChallengeFacade extends ChallengeActivity {
	
	private ArrayList<Listener> listeners;
	
	public interface Listener {
		public void onChallengeResponse();
	}
	
	public static void createChallenge(Listener listener) {
		// Do Server Things
		listener.onChallengeResponse();
	}
}
