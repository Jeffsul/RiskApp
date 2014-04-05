package com.jeffsul.riskapp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;

import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.entities.Territory;
import com.jeffsul.riskapp.players.Player;

/**
 * TerritoryButton is the boundary object for a Territory entity object.
 */
public class TerritoryButton extends Button implements Territory.Listener {
	public TerritoryButton(Context context) {
		super(context);
		setBackgroundResource(R.drawable.territory_button);
		setText("3");
		setTextSize(14);
		setPadding(0, 0, 0, 0);
		setTypeface(null, Typeface.BOLD);
	}
	
	public void setSelected() {
		setBackgroundResource(R.drawable.territory_button_highlighted);
	}

	public void setDeselected() {
		setBackgroundResource(R.drawable.territory_button);
	}

	@Override
	public void onUnitsChanged(Player player, int units) {
		setText(Integer.toString(units));
	}

	@Override
	public void onOwnerChanged(Player oldOwner, Player newOwner) {
		setTextColor(newOwner.color);
	}
}
