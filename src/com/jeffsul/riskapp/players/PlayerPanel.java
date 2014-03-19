package com.jeffsul.riskapp.players;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.players.Player;

public class PlayerPanel extends LinearLayout implements Map.Listener, Player.Listener {
	private static final int PADDING = 10;

	private Player player;
			
	private TextView nameLbl;
	private TextView troopCount;
	private TextView terrCount;
	private TextView cardCount;
	private TextView bonusCount;
	
	public PlayerPanel(Context ctx, Player p) {
		super(ctx);
		this.setOrientation(VERTICAL);
		this.setPadding(PADDING, PADDING, PADDING, PADDING);
		player = p;
		
		troopCount = new TextView(ctx);
		troopCount.setText("0 troops");
		terrCount = new TextView(ctx);
		terrCount.setText("0 territories");
		bonusCount = new TextView(ctx);
		bonusCount.setText("0 bonus");
		
		/*addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent event) {
				hiliteTerritories(map.getTerritories(player));
			}
			
			public void mouseExited(MouseEvent event) {
				unhiliteTerritories(map.getTerritories(player));
			}
		});*/
		
		setBackgroundColor(player.color);
		
		nameLbl = new TextView(ctx);
		nameLbl.setText(player.name);
		
		//if (cardType != CardSetting.NONE) {
			cardCount = new TextView(ctx);
			cardCount.setText("0 cards");
		//}
				
		addView(nameLbl);
		addView(troopCount);
		addView(terrCount);
		addView(bonusCount);
		//if (cardType != CardSetting.NONE) {
			addView(cardCount);
		//}
	}

	@Override
	public void onTroopCountChange(Player player, int count) {
		if (this.player == player) {
			troopCount.setText(getResources().getString(R.string.pp_troops, count));
		}
	}

	@Override
	public void onTerritoryCountChange(Player player, int count) {
		if (this.player == player) {
			terrCount.setText(getResources().getString(R.string.pp_territories, count));
			if (count == 0) {
				bonusCount.setText(getResources().getString(R.string.pp_bonus, 0));
				setBackgroundColor(getResources().getColor(R.color.pp_dead));
				nameLbl.setTextColor(Color.LTGRAY);
			}
		}
	}

	@Override
	public void onBonusChange(Player player, int bonus) {
		if (this.player == player) {
			bonusCount.setText(getResources().getString(R.string.pp_bonus, bonus));
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
		cardCount.setText(getResources().getString(R.string.pp_cards, count));
	}
}
