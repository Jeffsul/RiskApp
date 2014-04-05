package com.jeffsul.riskapp;

import com.jeffsul.riskapp.players.Player;

/**
 * Implement StateListener and register with GameActivity to receive state change updates that occur in a Risk game.
 */
public interface StateListener {
	/**
	 * Invoked to notify the listener when the state changes.
	 * @param activePlayer the player currently active in a Risk game.
	 * @param newState the new state.
	 */
	public void onStateChange(Player activePlayer, GameActivity.State newState);
}
