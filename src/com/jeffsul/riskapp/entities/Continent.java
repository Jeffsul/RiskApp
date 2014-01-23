package com.jeffsul.riskapp.entities;

import java.util.ArrayList;

import com.jeffsul.riskapp.players.Player;

public class Continent {
	public String name;
	private int bonus;
	private Territory[] territs;
	private Territory[] borders;
	
	public Continent(String nm, int bonus, Territory[] territs, Territory[] borders) {
		name = nm;
		this.bonus = bonus;
		this.territs = territs;
		this.borders = borders;
	}
	
	public int getBonus() {
		return bonus;
	}
	
	public int getSize() {
		return territs.length;
	}
	
	public Territory[] getTerritories() {
		return territs;
	}
	
	public Territory[] getBorders() {
		return borders;
	}
	
	public Territory[] getFriendlyTerritories(Player player) {
		ArrayList<Territory> friends = new ArrayList<Territory>();
		for (Territory territ : territs) {
			if (territ.owner == player)
				friends.add(territ);
		}
		Territory[] friendlyTerrits = new Territory[friends.size()];
		friends.toArray(friendlyTerrits);
		return friendlyTerrits;
	}
	
	public int getFriendlyTerritoryCount(Player player) {
		int count = 0;
		for (Territory territ : territs) {
			if (territ.owner == player)
				count++;
		}
		return count;
	}
	
	public Territory[] getEnemyTerritories(Player player) {
		ArrayList<Territory> enemies = new ArrayList<Territory>();
		for (Territory territ : territs) {
			if (territ.owner != player)
				enemies.add(territ);
		}
		Territory[] enemyTerrits = new Territory[enemies.size()];
		enemies.toArray(enemyTerrits);
		return enemyTerrits;
	}
	
	public boolean hasTerritory(Territory target) {
		for (Territory territ : territs) {
			if (territ == target)
				return true;
		}
		return false;
	}
	
	public boolean hasContinent(Player player) {
		return getFriendlyTerritoryCount(player) == territs.length;
	}
}
