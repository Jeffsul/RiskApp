package com.jeffsul.riskapp.entities;

/**
 * Entity object representing a card.
 */
public class Card {
	public final static int INFANTRY = 0;
	public final static int CAVALRY = 1;
	public final static int ARTILLERY = 2;

	/**
	 * Either INFANTRY, CAVALRY, ARTILLERY.
	 */
	public final int type;
	/**
	 * The associated territory (important for cash-in).
	 */
	public final Territory territory;
	
	public Card(Territory territory, int type) {
		this.type = type;
		this.territory = territory;
	}
}
