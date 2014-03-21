package com.jeffsul.riskapp.players;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.players.Player;

public class PlayerPanel extends LinearLayout implements Map.Listener, Player.Listener {
	private static final int PADDING = 10;

	private Player player;
	
	public PlayerPanel(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		setOrientation(VERTICAL);
		setPadding(PADDING, PADDING, PADDING, PADDING);
		
		//setBackgroundColor(player.color);
	}

	@Override
	public void onTroopCountChange(Player player, int count) {
		if (this.player == player) {
			((TextView) findViewById(R.id.troop_count_textview)).setText(getResources().getString(R.string.pp_troops, count));
		}
	}

	@Override
	public void onTerritoryCountChange(Player player, int count) {
		if (this.player == player) {
			((TextView) findViewById(R.id.territory_count_textview)).setText(getResources().getString(R.string.pp_territories, count));
			if (count == 0) {
				((TextView) findViewById(R.id.bonus_count_textview)).setText(getResources().getString(R.string.pp_bonus, 0));
				setBackgroundColor(getResources().getColor(R.color.pp_dead));
				//nameLbl.setTextColor(Color.LTGRAY);
			}
		}
	}

	@Override
	public void onBonusChange(Player player, int bonus) {
		if (this.player == player) {
			((TextView) findViewById(R.id.bonus_count_textview)).setText(getResources().getString(R.string.pp_bonus, bonus));
		}
	}
	
	public void setActive(boolean active) {
		/*if (active)
			setBorder(ACTIVE_BORDER);
		else
			setBorder(null);*/
	}

	@Override
	public void onCardCountChange(int count) {
		((TextView) findViewById(R.id.card_count_textview)).setText(getResources().getString(R.string.pp_cards, count));
	}
}
