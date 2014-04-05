package com.jeffsul.riskapp.entities;

import java.util.ArrayList;

import com.jeffsul.riskapp.players.Player;

/**
 * Continent represents a collection of territories that provide a bonus when all are
 * owned by the same player.
 */
public class Continent {
	public String name;
	private int bonus;
	private Territory[] territs;
	private Territory[] borders;
	
	public Continent(String name, int bonus, Territory[] territs) {
		this.name = name;
		this.bonus = bonus;
		this.territs = territs;
		this.borders = null;
	}

	/**
	 * @return The bonus troops provided by holding this entire continent.
	 */
	public int getBonus() {
		return bonus;
	}

	/**
	 * @return The number of territories in this continent.
	 */
	public int getSize() {
		return territs.length;
	}
	
	public Territory[] getTerritories() {
		return territs;
	}
	
	public Territory[] getBorders() {
		return borders;
	}

	/**
	 * Get the territories owned by a player in the continent.
	 * @param player
	 * @return array of territories owned by player.
	 */
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

	/**
	 * Get the number of territories owned by a player in the continent.
	 * @param player
	 * @return number of territories own by player in continent.
	 */
	public int getFriendlyTerritoryCount(Player player) {
		int count = 0;
		for (Territory territ : territs) {
			if (territ.owner == player)
				count++;
		}
		return count;
	}

	/**
	 * 
	 * @param player
	 * @return list of territories not owned by the player in this continent.
	 */
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

	/**
	 * 
	 * @param target
	 * @return whether target is a territory in this continent.
	 */
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
