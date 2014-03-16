package com.jeffsul.riskapp.players;

import com.jeffsul.riskapp.GameListener;
import com.jeffsul.riskapp.GameSettings;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.entities.Territory;

public class SimpleAIPlayer extends AIPlayer {

	public SimpleAIPlayer(int num, int color, GameListener listener, Map map, GameSettings gameSettings) {
		super(num, color, listener, map, gameSettings);
	}

	@Override
	protected void place() {
		Territory[] myTerritories = map.getTerritories(this);
		listener.place(myTerritories[(int) (Math.random() * myTerritories.length)]); 
	}

	@Override
	protected void deploy() {
		Territory[] myTerritories = map.getTerritories(this);
		listener.deploy(myTerritories[(int) (Math.random() * myTerritories.length)], true);
	}

	@Override
	protected void attack() {
		
	}

	@Override
	protected void fortify() {
		
	}

}
