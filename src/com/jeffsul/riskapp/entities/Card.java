package com.jeffsul.riskapp.entities;

public class Card {
	public final static int INFANTRY = 0;
	public final static int CAVALRY = 1;
	public final static int ARTILLERY = 2;
	
	public final int type;
	public final Territory territory;
	
	public Card(Territory territory, int type) {
		this.type = type;
		this.territory = territory;
	}
}
