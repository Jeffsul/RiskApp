package com.jeffsul.riskapp.players;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeffsul.riskapp.GameActivity.CardSetting;
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
	
	public PlayerPanel(Context ctx, Player p, CardSetting cardType) {
		super(ctx);
		this.setOrientation(VERTICAL);
		this.setPadding(PADDING, PADDING, PADDING, PADDING);
		player = p;
		this.cardType = cardType;
		
		troopCount = new TextView(ctx);
		troopCount.setText("0 troops");
		terrCount = new TextView(ctx);
		terrCount.setText("0 territories");
		bonusCount = new TextView(ctx);
		bonusCount.setText("0 bonus");
		
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
		
		if (cardType != CardSetting.NONE) {
			cardCount = new TextView(ctx);
			cardCount.setText("0 cards");
		}
				
		addView(nameLbl);
		addView(troopCount);
		addView(terrCount);
		addView(bonusCount);
		if (cardType != CardSetting.NONE)
			addView(cardCount);
	}
	
	public void update() {
		/*if (player.isLiving()) {
			troopCount.setText(map.getTroopCount(player) + " troops");
			int territs = map.getTerritoryCount(player);
			terrCount.setText(territs + " territories");
			
			if (cardType != CardSetting.NONE)
				cardCount.setText(player.getCardCount() + " cards");
			
			int bonus = getBonus(player);
			bonusCount.setText(bonus + " bonus");
		} else {
			bonusCount.setText("0 bonus");
			setBackground(Color.BLACK);
			nameLbl.setForeground(Color.LIGHT_GRAY);
		}*/
	}
	
	public void setActive(boolean active) {
		/*if (active)
			setBorder(ACTIVE_BORDER);
		else
			setBorder(null);*/
	}
}
