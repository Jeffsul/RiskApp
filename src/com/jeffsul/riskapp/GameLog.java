package com.jeffsul.riskapp;

import java.util.ArrayList;

public class GameLog {
	private ArrayList<String> logMessages;
	
	public GameLog() {
		logMessages = new ArrayList<String>();
	}

	public void log(String message) {
		logMessages.add(message);
	}
}
