package com.jeffsul.riskapp.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;

import com.jeffsul.riskapp.players.Player;

public class Map implements Territory.Listener {
	public HashMap<String, Territory> territories;
	public Set<Continent> continents;
	
	private Player[] players;
	
	private ArrayList<Listener> listeners;
	
	public interface Listener {
		public void onTroopCountChange(Player player, int count);
		public void onTerritoryCountChange(Player player, int count);
		public void onBonusChange(Player player, int bonus);
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public Map(Activity activity, int mapResId) {
		try {
			parse(activity.getResources().getXml(mapResId));
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		listeners = new ArrayList<Listener>();
	}
	
	private void parse(XmlResourceParser parser) throws XmlPullParserException, IOException {
		try {
			parser.next();
			parser.nextTag();
			
			parser.require(XmlResourceParser.START_TAG, null, "map");
			parser.nextTag();
			readTerritories(parser);
			parser.nextTag();
			readConnections(parser);
		} finally {
			parser.close();
		}
	}
	
	private void readTerritories(XmlResourceParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlResourceParser.START_TAG, null, "territories");
		territories = new HashMap<String, Territory>();
		continents = new HashSet<Continent>();
		while (parser.next() != XmlResourceParser.END_TAG) {
			if (parser.getEventType() != XmlResourceParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("continent")) {
				continents.add(readContinent(parser));
			} else if (name.equals("territory")) {
				Territory t = readTerritory(parser);
				territories.put(t.name, t);
			} else {
				skip(parser);
			}
		}
	}
	
	private void readConnections(XmlResourceParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlResourceParser.START_TAG, null, "connections");
		while (parser.next() != XmlResourceParser.END_TAG) {
			if (parser.getEventType() != XmlResourceParser.START_TAG) {
				continue;
			}
			if (parser.getName().equals("connection")) {
				readConnection(parser);
			} else {
				skip(parser);
			}
		}
	}
	
	private void readConnection(XmlResourceParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlResourceParser.START_TAG, null, "connection");
		String t1 = parser.getAttributeValue(null, "t1");
		String t2 = parser.getAttributeValue(null, "t2");
		Territory territory1 = territories.get(t1);
		Territory territory2 = territories.get(t2);
		if (territory1 == null || territory2 == null) {
			throw new XmlPullParserException("Could not find territory with name: " + (territory1 == null ? t1 : t2));
		}
		territory1.addConnector(territory2);
		territory2.addConnector(territory1);
		parser.nextTag();
		parser.require(XmlResourceParser.END_TAG, null, "connection");
	}
	
	private Continent readContinent(XmlResourceParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlResourceParser.START_TAG, null, "continent");
		String continentName = parser.getAttributeValue(null, "name");
		int bonus = Integer.parseInt(parser.getAttributeValue(null, "bonus"));
		Set<Territory> territoryList = new HashSet<Territory>();
		while (parser.next() != XmlResourceParser.END_TAG) {
			if (parser.getEventType() != XmlResourceParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("continent")) {
				Continent subContinent = readContinent(parser);
				continents.add(subContinent);
				territoryList.addAll(Arrays.asList(subContinent.getTerritories()));
			} else if (name.equals("territory")) {
				Territory territory = readTerritory(parser);
				territories.put(territory.name, territory);
				territoryList.add(territory);
			} else {
				skip(parser);
			}
		}
		return new Continent(continentName, bonus, territoryList.toArray(new Territory[territoryList.size()]));
	}

	private Territory readTerritory(XmlResourceParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlResourceParser.START_TAG, null, "territory");
		int x = Integer.parseInt(parser.getAttributeValue(null, "x"));
		int y = Integer.parseInt(parser.getAttributeValue(null, "y"));
		String name = "";
		if (parser.next() == XmlResourceParser.TEXT) {
			name = parser.getText();
			parser.nextTag();
		} else {
			// Throw Exception
		}
		parser.require(XmlResourceParser.END_TAG, null, "territory");
		Territory territory = new Territory(name, x, y);
		territory.addListener(this);
		return territory;
	}

	private void skip(XmlResourceParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlResourceParser.START_TAG) {
			throw new IllegalStateException("Attempt to skip tag failed: not at start of tag.");
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlResourceParser.END_TAG:
				depth--;
				break;
			case XmlResourceParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	/**
	 * Get the smallest Continent containing the given Territory.
	 */
	public Continent getContinent(Territory t) {
		// TODO(jeffsul): Cache this with HashMap.
		Continent c = null;
		Iterator<Continent> itr = continents.iterator();
		Continent n;
		while (itr.hasNext()) {
			n = itr.next();
			if (n.hasTerritory(t)) {
				if (c == null) {
					c = n;
				} else if (n.getSize() < c.getSize()) {
					c = n;
				}
			}
		}
		return c;
	}
	
	public Continent[] getContinents() {
		return continents.toArray(new Continent[continents.size()]);
	}
	
	public int getTerritoryCount(Player p) {
		int count = 0;
		for (Territory t : territories.values()) {
			if (t.owner == p) {
				count++;
			}
		}
		return count;
	}
	
	public int getTroopCount(Player p) {
		int count = 0;
		for (Territory t : territories.values()) {
			if (t.owner == p) {
				count += t.units;
			}
		}
		return count;
	}
	
	public Territory[] getTerritories() {
		return territories.values().toArray(new Territory[territories.size()]);
	}
	
	public Territory[] getTerritories(Player p) {
		Set<Territory> playerTerritories = new HashSet<Territory>();
		for (Territory t : territories.values()) {
			if (t.owner == p) {
				playerTerritories.add(t);
			}
		}
		return playerTerritories.toArray(new Territory[playerTerritories.size()]);
	}
	
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public Player[] getPlayers() {
		return players;
	}

	@Override
	public void onUnitsChanged(Player player, int units) {
		for (Listener listener : listeners) {
			listener.onTroopCountChange(player, getTroopCount(player));
		}
	}
	
	private int getBonus(Player player) {
		int total = 0;
		for (Continent cont : continents) {
			if (cont.hasContinent(player)) {
				total += cont.getBonus();
			}
		}
		return Math.max((int) (getTerritoryCount(player) / 3), 3) + total;
	}

	@Override
	public void onOwnerChanged(Player oldOwner, Player newOwner) {
		for (Listener listener : listeners) {
			listener.onTroopCountChange(oldOwner, getTerritoryCount(oldOwner));
			listener.onBonusChange(oldOwner, getBonus(oldOwner));
			listener.onTroopCountChange(newOwner, getTerritoryCount(newOwner));
			listener.onBonusChange(newOwner, getBonus(newOwner));
		}
	}
}

