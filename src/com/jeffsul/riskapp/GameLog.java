package com.jeffsul.riskapp;

import java.util.ArrayList;

/**
 * GameLog is a utility for collecting the messages logged during a Risk game.
 */
public class GameLog {
	private ArrayList<String> logMessages;
	
	public GameLog() {
		logMessages = new ArrayList<String>();
	}

	public void log(String message) {
		logMessages.add(message);
	}
}
