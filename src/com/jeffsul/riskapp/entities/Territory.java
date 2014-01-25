package com.jeffsul.riskapp.entities;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import com.jeffsul.riskapp.players.Player;

/**
 * Territory represents one of the locations on the map, abstractly and by
 * managing the associated UI button element.
 */
public class Territory {
	private static final int DEFAULT_UNITS = 3;
	private static final int REDUCE_FONT_LIMIT = 100;
	
	private static final float SMALL_FONT = 10;
	private static final float LARGE_FONT = 11;
		
	public String name;
	public int x;
	public int y;
	public int units = DEFAULT_UNITS;
	public Player owner;
	
	private Territory[] connectors;
	private final Button btn;
	
	public Territory(Context ctx, String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		
		btn = new Button(ctx);
		btn.setText(Integer.toString(units));
		btn.setTextSize(LARGE_FONT);
	}

	// TODO(jeffsul): Implement custom listener.
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
			if (units >= REDUCE_FONT_LIMIT) {
				btn.setTextSize(SMALL_FONT);
			} else {
				btn.setTextSize(LARGE_FONT);
			}
		}
	}
	
	public void setUnits(int num) {
		if (num != units) {
			units = num;
			btn.setText(Integer.toString(units));
			if (units >= REDUCE_FONT_LIMIT) {
				btn.setTextSize(SMALL_FONT);
			} else {
				btn.setTextSize(LARGE_FONT);
			}
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
		// TODO(jeffsul): Add border
		btn.setTextColor(Color.WHITE);
	}
	
	public void unhilite() {
		// TODO(jeffsul): Remove border
		btn.setTextColor(Color.BLACK);
	}
	
	public Territory[] getConnectors() {
		return connectors;
	}
	
	public Territory[] getFriendlyConnectors(Player player) {
		ArrayList<Territory> friends = new ArrayList<Territory>();
		for (int i = 0; i < connectors.length; i++) {
			if (connectors[i].owner == player) {
				friends.add(connectors[i]);
			}
		}
		Territory[] friendTerrits = new Territory[friends.size()];
		friends.toArray(friendTerrits);
		return friendTerrits;
	}
	
	public Territory[] getEnemyConnectors(Player player) {
		ArrayList<Territory> enemies = new ArrayList<Territory>();
		for (int i = 0; i < connectors.length; i++) {
			if (connectors[i].owner != player) {
				enemies.add(connectors[i]);
			}
		}
		Territory[] enemyTerrits = new Territory[enemies.size()];
		enemies.toArray(enemyTerrits);
		return enemyTerrits;
	}
	
	/**
	 * To check whether a territory is connected (i.e., can be attacked from) this one.
	 * @param territ
	 * @return true if territ is connected to this one
	 */
	public boolean isConnecting(Territory territ) {
		for (Territory conn : connectors) {
			if (conn == territ) {
				return true;
			}
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
			if (target == territ) {
				return true;
			}
			checked.put(territ, true);
			
			Territory[] conns = territ.getFriendlyConnectors(owner);
			for (Territory conn : conns) {
				if (!checked.containsKey(conn)) {
					territs.add(conn);
				}
			}
		}
		return false;
	}
}
