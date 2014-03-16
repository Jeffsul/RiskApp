package com.jeffsul.riskapp;

import com.jeffsul.riskapp.players.Player;

public interface StateListener {
	public void onStateChange(Player activePlayer, GameActivity.State newState);
}
