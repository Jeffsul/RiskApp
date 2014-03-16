package com.jeffsul.riskapp;

import com.jeffsul.riskapp.entities.Territory;

public interface GameListener {
	public void place(Territory territ);
	public void deploy(Territory territ, boolean all);
	public boolean attack(Territory from, Territory to, boolean all);
	public void endAttacks();
	public void fortify(Territory from, Territory to, boolean all);
	public void endFortifications();
	public void advance(Territory to, boolean all);
	public void endAdvance();
}
