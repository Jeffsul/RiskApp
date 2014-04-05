package com.jeffsul.riskapp;

import com.jeffsul.riskapp.entities.Territory;

/**
 * GameListener is an interface to encapsulate the GameActivity from AIPlayer.
 * It contains the full set of actions that a player can perform.
 */
public interface GameListener {
	/**
	 * Place troop to territory (before the first player's turn has occurred).
	 * @param territ
	 */
	public void place(Territory territ);
	/**
	 * Deploy troop to territory.
	 * @param territ
	 * @param all whether all available troops to deploy should be deployed.
	 */
	public void deploy(Territory territ, boolean all);
	/**
	 * Attack a territory.
	 * @param from the territory to attack from (owned by you).
	 * @param to the territory to attack (owned by someone else).
	 * @param all if true, keep attacking until either you win or your 'from' territory has 3 or less troops.
	 * @return true if the attack was successful, false otherwise.
	 */
	public boolean attack(Territory from, Territory to, boolean all);
	/**
	 * End the attack phase.
	 */
	public void endAttacks();
	/**
	 * Fortify your territories by moving troops from one to another.
	 * @param from the territory from which troops should be moved.
	 * @param to the territory where troops will move (connected by a chain of your own territories).
	 * @param all whether all troops should be move from 'from' to 'to'.
	 */
	public void fortify(Territory from, Territory to, boolean all);
	/**
	 * End the fortify phase.
	 */
	public void endFortifications();
	/**
	 * Advance troops to or from a conquered territory.
	 * @param to the territory to advance to.
	 * @param all whether to advance all troops, and end the advance; or, if false, whether to advance only 1.
	 */
	public void advance(Territory to, boolean all);
	/**
	 * End the advance phase.
	 */
	public void endAdvance();
}
