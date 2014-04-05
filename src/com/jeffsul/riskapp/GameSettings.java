package com.jeffsul.riskapp;

import com.jeffsul.riskapp.GameActivity.CardSetting;

/**
 * GameSettings encapsulates the GameActivity from AIPlayer by providing an interface to the settings.
 */
public class GameSettings {
	public CardSetting cardSetting;
	
	public GameSettings(CardSetting cardSetting) {
		this.cardSetting = cardSetting;
	}
}
