package com.jeffsul.riskapp.players;

import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeffsul.riskapp.GameActivity;
import com.jeffsul.riskapp.GameActivity.CardSetting;
import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.players.Player;

public class PlayerPanel extends LinearLayout {
	private static final int PADDING = 10;

	private Player player;
	
	private CardSetting cardType;
			
	private TextView nameLbl;
	private TextView troopCount;
	private TextView terrCount;
	private TextView cardCount;
	private TextView bonusCount;
	
	private GameActivity game;
	
	public PlayerPanel(GameActivity game, Player p, CardSetting cardType) {
		super(game);
		this.setOrientation(VERTICAL);
		this.setPadding(PADDING, PADDING, PADDING, PADDING);
		this.game = game;
		player = p;
		this.cardType = cardType;
		
		troopCount = new TextView(game);
		troopCount.setText("0 troops");
		terrCount = new TextView(game);
		terrCount.setText("0 territories");
		bonusCount = new TextView(game);
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
		
		nameLbl = new TextView(game);
		nameLbl.setText(player.name);
		
		if (cardType != CardSetting.NONE) {
			cardCount = new TextView(game);
			cardCount.setText("0 cards");
		}
				
		addView(nameLbl);
		addView(troopCount);
		addView(terrCount);
		addView(bonusCount);
		if (cardType != CardSetting.NONE) {
			addView(cardCount);
		}
	}
	
	public void update() {
		if (player.isLiving()) {
			troopCount.setText(getResources().getString(R.string.pp_troops, game.getMap().getTroopCount(player)));
			int territs = game.getMap().getTerritoryCount(player);
			terrCount.setText(getResources().getString(R.string.pp_territories, territs));
			
			if (cardType != CardSetting.NONE) {
				cardCount.setText(getResources().getString(R.string.pp_cards, player.getCardCount()));
			}
			
			int bonus = game.getBonus(player);
			bonusCount.setText(getResources().getString(R.string.pp_bonus, bonus));
		} else {
			bonusCount.setText(getResources().getString(R.string.pp_bonus, 0));
			setBackgroundColor(getResources().getColor(R.color.pp_dead));
			nameLbl.setTextColor(Color.LTGRAY);
		}
	}
	
	public void setActive(boolean active) {
		/*if (active)
			setBorder(ACTIVE_BORDER);
		else
			setBorder(null);*/
	}
}
