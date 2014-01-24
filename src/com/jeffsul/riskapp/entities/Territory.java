package com.jeffsul.riskapp.entities;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import com.jeffsul.riskapp.players.Player;

public class Territory {
	private static final int DEFAULT_UNITS = 3;
	//private static final int X_OFFSET = -41;
	//private static final int Y_OFFSET = -51;
	//private static final int BTN_WIDTH = 22;
	//private static final int BTN_HEIGHT = 16;
	
	private static final int REDUCE_FONT_LIMIT = 100;
	
	//public static final Border BORDER_HIGHLIGHT = BorderFactory.createLineBorder(Color.WHITE, 2);
	
	public String name;
	public int x;
	public int y;
	public int units = DEFAULT_UNITS;
	public Player owner;
	
	private Territory[] connectors;
	private final Button btn;
	
	private static final float SMALL_FONT = 10;
	private static final float LARGE_FONT = 11;
	
	public Territory(Context ctx, String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		
		btn = new Button(ctx);
		btn.setText("3");
		btn.setTextSize(LARGE_FONT);
		btn.setFocusable(false);
		btn.setTextColor(Color.BLACK);
	}
	
	public void addMouseListener(OnClickListener listener, OnLongClickListener listener2) {
		btn.setOnClickListener(listener);
		btn.setOnLongClickListener(listener2);
	}
	
	public void connect(Territory[] conns) {
		connectors = conns;
	}
	
	public void addUnits(int num) {
		if (num != 0) {
			units += num;
			btn.setText(Integer.toString(units));
			if (units >= REDUCE_FONT_LIMIT)
				btn.setTextSize(SMALL_FONT);
			else
				btn.setTextSize(LARGE_FONT);
		}
	}
	
	public void setUnits(int num) {
		if (num != units) {
			units = num;
			btn.setText(Integer.toString(units));
			if (units >= REDUCE_FONT_LIMIT)
				btn.setTextSize(SMALL_FONT);
			else
				btn.setTextSize(LARGE_FONT);
		}
	}
	
	public void setOwner(Player newOwner) {
		owner = newOwner;
		btn.setBackgroundColor(newOwner.color);
	}
	
	public Button getButton() {
		return btn;
	}
	
	public void hilite() {
		//btn.setBorder(BORDER_HIGHLIGHT);
		btn.setTextColor(Color.WHITE);
	}
	
	public void unhilite() {
		//btn.setBorder(null);
		btn.setTextColor(Color.BLACK);
	}
	
	public Territory[] getConnectors() {
		return connectors;
	}
	
	public Territory[] getFriendlyConnectors(Player player) {
		ArrayList<Territory> friends = new ArrayList<Territory>();
		for (int i = 0; i < connectors.length; i++) {
			if (connectors[i].owner == player)
				friends.add(connectors[i]);
		}
		Territory[] friendTerrits = new Territory[friends.size()];
		friends.toArray(friendTerrits);
		return friendTerrits;
	}
	
	public Territory[] getEnemyConnectors(Player player) {
		ArrayList<Territory> enemies = new ArrayList<Territory>();
		for (int i = 0; i < connectors.length; i++)
		{
			if (connectors[i].owner != player)
				enemies.add(connectors[i]);
		}
		Territory[] enemyTerrits = new Territory[enemies.size()];
		enemies.toArray(enemyTerrits);
		return enemyTerrits;
	}
	
	public boolean isConnecting(Territory territ) {
		for (Territory conn : connectors) {
			if (conn == territ)
				return true;
		}
		return false;
	}
	
	public boolean isFortifyConnecting(Territory target) {
		if (isConnecting(target))
			return true;
		
		HashMap<Territory, Boolean> checked  = new HashMap<Territory, Boolean>();
		ArrayList<Territory> territs = new ArrayList<Territory>();
		territs.add(this);
		while (territs.size() > 0) {
			Territory territ = territs.remove(0);
			if (target == territ)
				return true;
			checked.put(territ, true);
			
			Territory[] conns = territ.getFriendlyConnectors(owner);
			for (Territory conn : conns) {
				if (!checked.containsKey(conn))
					territs.add(conn);
			}
		}
		return false;
	}
}
