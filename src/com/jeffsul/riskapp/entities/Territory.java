package com.jeffsul.riskapp.entities;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.players.Player;

/**
 * Territory represents one of the locations on the map, abstractly and by
 * managing the associated UI button element.
 */
public class Territory {
	private static final int DEFAULT_UNITS = 3;	
	private static final float LARGE_FONT = 14;
		
	public String name;
	public int x;
	public int y;
	public int units = DEFAULT_UNITS;
	public Player owner;
	
	private Territory[] connectors;
	private final Button btn;
	
	private Listener listener;
	
	public interface Listener {
		public void onUnitsChanged(Player player, int units);
		public void onOwnerChanged(Player oldOwner, Player newOwner);
	}
	
	public Territory(Listener listener, Context ctx, String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.listener = listener;
		
		btn = new Button(ctx);
		btn.setBackgroundResource(R.drawable.territory_button);
		btn.setText(Integer.toString(units));
		btn.setTypeface(null, Typeface.BOLD);
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
		setUnits(units + num);
	}
	
	public void setUnits(int num) {
		if (num != units) {
			units = num;
			listener.onUnitsChanged(owner, units);
			btn.setText(Integer.toString(units));
		}
	}
	
	public void setOwner(Player newOwner) {
		listener.onOwnerChanged(owner, newOwner);
		owner = newOwner;
		btn.setTextColor(newOwner.color);
	}
	
	public Button getButton() {
		return btn;
	}
	
	public void hilite() {
		btn.setBackgroundResource(R.drawable.territory_button_highlighted);
	}
	
	public void unhilite() {
		btn.setBackgroundResource(R.drawable.territory_button);
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
		if (isConnecting(target)) {
			return true;
		}
		
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
