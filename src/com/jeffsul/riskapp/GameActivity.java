package com.jeffsul.riskapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeffsul.riskapp.dialogs.AutoGameDialogFragment;
import com.jeffsul.riskapp.dialogs.PlaySetDialogFragment;
import com.jeffsul.riskapp.entities.Card;
import com.jeffsul.riskapp.entities.Continent;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.entities.Territory;
import com.jeffsul.riskapp.players.AIPlayer;
import com.jeffsul.riskapp.players.Player;
import com.jeffsul.riskapp.players.PlayerPanel;

public class GameActivity extends Activity implements AutoGameDialogFragment.Listener,
		PlaySetDialogFragment.Listener, View.OnClickListener, View.OnLongClickListener {
	public static final String NUM_PLAYERS_EXTRA = "com.jeffsul.risk.NUM_PLAYERS";
	
	public static final int[] PLAYER_COLOURS = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
		Color.CYAN};
	private static final int[] CASH_IN = {4, 6, 8, 10, 12, 15};
	private static final int CASH_IN_INCREMENT = 5;
	private static final int INITIAL_PLACE_COUNT = 1;

	public static enum State {PLACE, DEPLOY, ATTACK, ADVANCE, FORTIFY};
	public static enum CardSetting {REGULAR, NONE, MODIFIED};
	
	private int numPlayers;
	public Player[] players;
	private HashMap<Player, PlayerPanel> playerPnlHash = new HashMap<Player, PlayerPanel>();
	private Player activePlayer;
	private Player eliminatedPlayer;
	public boolean conqueredTerritory;
	
	private HashMap<View, Territory> buttonMap;
	
	private int index;
	private int round;
	public State state = State.PLACE;
	private int placeNum = INITIAL_PLACE_COUNT;
	public int deployNum;
	private int firstPlayerIndex;
	
	public ArrayList<Card> deck = new ArrayList<Card>();
	private int cashes;
	
	private Territory fromTerrit;
	private Territory toTerrit;
	
	private Map map;
	private boolean useEpicMap;
	public CardSetting cardType = CardSetting.REGULAR;
	
	public RiskCalculator riskCalc = new RiskCalculator(false);
	
	public boolean gameOver;
	
	public boolean autoGame;
	public boolean simulate;
	public int simulateCount;
	public boolean paused;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Intent intent = getIntent();
		numPlayers = intent.getIntExtra(NUM_PLAYERS_EXTRA, 2);
		initializeGame();
	}

	private void initializeGame() {
		players = new Player[numPlayers];
		firstPlayerIndex = (int) (numPlayers * Math.random());
		index = firstPlayerIndex;
		playerPnlHash.clear();
		
		initGUI();
		
		map = new Map(this, useEpicMap);
		
		int half = (int) Math.ceil(numPlayers / 2.0);
		for (int i = 0; i < numPlayers; i++) {
			//if (!playerType[i].isSelected()) {
				players[i] = new Player(i + 1, "Player " + (i + 1), PLAYER_COLOURS[i]);
			//} else {
			//	players[i] = new AIPlayer(i + 1, PLAYER_COLOURS[i], this);
			//}
			
			ViewGroup sidePanelLeft = (ViewGroup) findViewById(R.id.side_panel_left);
			ViewGroup sidePanelRight = (ViewGroup) findViewById(R.id.side_panel_right);
			PlayerPanel playerPnl = new PlayerPanel(this, players[i], cardType);
			playerPnlHash.put(players[i], playerPnl);
			if (i < half) {
				sidePanelLeft.addView(playerPnl);
			} else {
				sidePanelRight.addView(playerPnl);
			}
		}
		
		activePlayer = players[index];
		playerPnlHash.get(activePlayer).setActive(true);
		message(activePlayer.name + ", you have " + placeNum + " armies left to place.");
		
		RelativeLayout gamePnl = (RelativeLayout) findViewById(R.id.game_panel);
		
		ArrayList<Territory> territories = map.getTerritories();
		buttonMap = new HashMap<View, Territory>();
		int terrCount = territories.size();
		deck.removeAll(deck);
		for (int i = 0; i < terrCount; i++) {
			Territory territ = territories.remove((int) (Math.random() * territories.size()));
			territ.setOwner(players[i % numPlayers]);
			buttonMap.put(territ.getButton(), territ);
			territ.addMouseListener(this, this);
			deck.add(new Card(territ, i % 3));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(40, 30);
			params.leftMargin = territ.x - 41;
			params.topMargin = territ.y - 51;
			gamePnl.addView(territ.getButton(), params);
		}
		
		for (Player player : players) {
			playerPnlHash.get(player).update();
		}
		log("Game Initialized.");
		
		//saveGame();
		beginPlacement();
	}

	/**
	 * Receives click events from Action button.
	 * @param view
	 */
	public void sendMessage(View view) {
		if (view.getId() == R.id.action_button) {
			switch (state) {
				case ATTACK:
					endAttacks();
					break;
				case ADVANCE:
					endAdvance();
					break;
				case FORTIFY:
					endFortifications();
					break;
				default:
					break;
			}
		}
	}
	
	private void initGUI() {
		TextView cashInLabel = (TextView) findViewById(R.id.cash_in_label);
		if (cardType == CardSetting.NONE) {
			((ViewGroup) cashInLabel.getParent()).removeView(cashInLabel);
		} else {
			cashInLabel.setText(getResources().getString(R.id.cash_in_label, CASH_IN[0]));
		}
	}
	
	private void handleTurn() {
		if (activePlayer.isAI()) {
			handleAITurn();
		} else {
			beginTurn();
		}
	}
	
	private void handleAITurn() {
		while (activePlayer.isAI()) {
			AIPlayer aiPlayer = (AIPlayer) activePlayer;
			beginTurn();
			aiPlayer.deploy();
			aiPlayer.attack();
			aiPlayer.fortify();
			
			playerPnlHash.get(activePlayer).update();
		}
	}
	
	private void incrementTurn() {
		index++;
		for (int i = index;; i++) {
			index = i % numPlayers;
			if (players[index].isLiving()) {
				break;
			}
		}
			
		if (index == firstPlayerIndex) {
			updateRound();
			round++;
			log("Beginning Round " + round);
		}
			
		playerPnlHash.get(activePlayer).setActive(false);
		activePlayer = players[index];
		
		//if (saveFile != null)
		//	saveGame();
		
		if (!activePlayer.isAI())
			handleTurn();
	}
	
	public void beginTurn() {
		if (!autoGame) {
			autoGame = true;
			for (int i = 0; i < numPlayers; i++) {
				if (!players[i].isAI() && players[i].isLiving())
					autoGame = false;
			}
		}
		
		playerPnlHash.get(activePlayer).setActive(true);
		
		state = State.DEPLOY;
		deployNum = 0;
		continueTurnAfterSet(0);
	}
	
	private void continueTurnAfterSet(int extra) {
		deployNum += extra;
		if (activePlayer.hasSet()) {
			playSet();
			return;
		}
		
		int troopCount = Math.max(map.getTerritoryCount(activePlayer) / 3, 3);
		log(activePlayer.name + " gets " + troopCount + " troops for " + map.getTerritoryCount(activePlayer) + " territories.");
		
		troopCount += extra;
		
		conqueredTerritory = false;
		fromTerrit = null;
		toTerrit = null;
		
		Button actionBtn = (Button) findViewById(R.id.action_button);
		actionBtn.setText(R.string.action_end_attacks);
		actionBtn.setEnabled(false);
		
		Continent[] conts = map.getContinents();
		for (Continent cont : conts) {
			if (cont.hasContinent(activePlayer)) {
				troopCount += cont.getBonus();
				log(activePlayer.name + " gets " + cont.getBonus() + " troops for holding " + cont.name + ".");
			}
		}
		activePlayer.updateStats(Player.BONUS, troopCount - extra);
		message(activePlayer.name + ", you have " + troopCount + " troops to place.");
		deployNum = troopCount;
	}
	
	public void playSet() {
		Card[][] sets = activePlayer.getSets();
		String[] setTexts = new String[sets.length];
		Arrays.fill(setTexts, "");
		for (int i = 0; i < sets.length; i++) {
			for (int j = 0; j < 3; j++) {
				if (j > 0)
					setTexts[i] += ", ";
				if (sets[i][j].territory.owner == activePlayer)
					setTexts[i] += "[ " + sets[i][j].territory.name + " ]";
				else
					setTexts[i] += sets[i][j].territory.name;
			}
		}
		
		int extra = 0;
		if (cardType == CardSetting.REGULAR)
			extra = (cashes >= CASH_IN.length) ? CASH_IN_INCREMENT * cashes - 10 : CASH_IN[cashes];
		else if (cardType == CardSetting.MODIFIED)
			extra = (activePlayer.cashes >= CASH_IN.length) ? CASH_IN_INCREMENT * activePlayer.cashes - 10 : CASH_IN[activePlayer.cashes];
		
		if (!activePlayer.isAI()) {
			if (activePlayer.getCardCount() >= 5) {
				// TODO(jeffsul): Disallow cancel button.
			}
			PlaySetDialogFragment dialogFragment = PlaySetDialogFragment.newInstance(setTexts, extra);
			dialogFragment.show(getFragmentManager(), "playset");
		} else {
			// TODO(jeffsul): Make AI smarter.
			onPlaySet(0, extra);
		}
	}
	
	private void beginPlacement() {
		state = State.PLACE;
		if (activePlayer.isAI())
			handleAIPlacement();
	}
	
	private void handleAIPlacement() {
		while (activePlayer.isAI()) {
			((AIPlayer) activePlayer).place();
		}
	}
	
	private void incrementPlacementTurn() {
		playerPnlHash.get(activePlayer).setActive(false);
		index++;
		activePlayer = players[index % numPlayers];
		playerPnlHash.get(activePlayer).setActive(true);
		if (index % numPlayers == firstPlayerIndex)
			placeNum--;
		if (placeNum > 0) {
			message(getResources().getString(R.string.message_deploy_armies, activePlayer.name, placeNum));
		} else {
			updateRound();
			handleTurn();
		}
	}
	
	public void place(Territory territ) {
		if (territ.owner != activePlayer)
			return;
		territ.addUnits(1);
		playerPnlHash.get(activePlayer).update();
		log(activePlayer.name + " placed 1 troop on " + territ.name + ".");
		incrementPlacementTurn();
	}
	
	public void deploy(Territory territ, boolean all) {
		if (territ.owner != activePlayer)
			return;
		String placing = "1";//troopAmountCombo.getSelectedItem().toString();
		int toPlace = (all || placing.equals("All")) ? deployNum : Math.min(Integer.parseInt(placing), deployNum);
		territ.addUnits(toPlace);
		deployNum -= toPlace;
		activePlayer.updateStats(Player.TROOPS_DEPLOYED, toPlace);
		log(activePlayer.name + " placed " + toPlace + " troops on " + territ.name + ".");
		
		if (deployNum == 0)
			enterAttackState();
		else
			message(activePlayer.name + ", you have " + deployNum + " troops to place.");
		playerPnlHash.get(activePlayer).update();
	}
	
	private void enterAttackState() {
		state = State.ATTACK;
		((Button) findViewById(R.id.action_button)).setEnabled(true);
		message("Attack - click from your territory to an adjacent one to attack.");
	}
	
	public void attack(Territory from, Territory to, boolean all) {
		fromTerrit = from;
		attack(to, all);
	}
	
	public void fortify(Territory from, Territory to, boolean all) {
		fromTerrit = from;
		fortify(to, all);
	}
	
	public void endAdvance() {
		if (state != State.ADVANCE)
			return;
		
		state = State.ATTACK;
		((Button) findViewById(R.id.action_button)).setText(R.string.action_end_attacks);
		if (toTerrit.units != 1) {
			fromTerrit.unhilite();
			fromTerrit = toTerrit;
		}
		else
			toTerrit.unhilite();
		toTerrit = null;
		
		message("Attack - click from your territory to an adjacent one to attack.");
		
		if (eliminatedPlayer != null) {
			log(activePlayer.name + " eliminated " + eliminatedPlayer.name + " from the game.");
			if (eliminatedPlayer.number - 1 == firstPlayerIndex) {
				int i = firstPlayerIndex;
				while (true) {
					i++;
					if (players[i % numPlayers].isLiving()) {
						firstPlayerIndex = i % numPlayers;
						break;
					}
				}
			}
			
			int count = 0;
			for (int i = 0; i < numPlayers; i++) {
				if (players[i].isLiving())
					count++;
			}
			
			if (eliminatedPlayer.isAI())
				((AIPlayer) eliminatedPlayer).message("Argh!");
			
			if (count <= 1) {
				activePlayer.setStats(Player.TERRITORIES, map.getTerritoryCount(activePlayer));
				activePlayer.setStats(Player.TROOPS, map.getTroopCount(activePlayer));
				//JOptionPane.showMessageDialog(null, activePlayer.name + " won the game!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
				gameOver = true;
				return;
			}
			activePlayer.giveCards(eliminatedPlayer.takeAllCards());
			playerPnlHash.get(activePlayer).update();
			playerPnlHash.get(eliminatedPlayer).update();
			eliminatedPlayer = null;
			
			if (activePlayer.getCardCount() >= 5) {
				playSet();
			}
		}
	}
	
	public void endAttacks() {
		if (state != State.ATTACK) {
			return;
		}
		
		state = State.FORTIFY;
		((Button) findViewById(R.id.action_button)).setText(R.string.action_end_fortifications);
		if (fromTerrit != null) {
			fromTerrit.unhilite();
		}
		fromTerrit = null;
		message("Fortify - click from one territory to another to fortify troops.");
	}
	
	public void endFortifications() {
		if (fromTerrit != null) {
			fromTerrit.unhilite();
		}
		if (toTerrit != null) {
			toTerrit.unhilite();
		}
		endTurn();
	}
	
	public void attack(Territory territ, boolean all) {
		if (territ.owner == activePlayer) {
			if (fromTerrit != null) {
				fromTerrit.unhilite();
			}
			
			if (territ.units == 1) {
				error("You cannot attack with 1 troop!");
				return;
			}
			
			fromTerrit = territ;
			fromTerrit.hilite();
			message("Attack from " + fromTerrit.name + " to an adjacent territory.");
		} else if (fromTerrit != null) {
			if (fromTerrit.units == 1) {
				error("You cannot attack with 1 troop!");
				return;
			}
			
			if (!fromTerrit.isConnecting(territ)) {
				error(fromTerrit.name + " does not connect with " + territ.name + "!");
				return;
			}
			
			toTerrit = territ;
			Player otherPlayer = toTerrit.owner;
			
			int[] outcome = getAttackOutcome(fromTerrit.units, toTerrit.units);
			fromTerrit.addUnits(outcome[0]);
			toTerrit.addUnits(outcome[1]);
			activePlayer.updateStats(Player.TROOPS_KILLED, -outcome[1]);
			activePlayer.updateStats(Player.TROOPS_LOST, -outcome[0]);
			otherPlayer.updateStats(Player.TROOPS_KILLED, -outcome[0]);
			otherPlayer.updateStats(Player.TROOPS_LOST, -outcome[1]);
			
			if (toTerrit.units == 0) {
				if (map.getTerritoryCount(toTerrit.owner) == 1) {
					eliminatedPlayer = otherPlayer;
					eliminatedPlayer.setLiving(false);
				}
				
				toTerrit.setOwner(activePlayer);
				toTerrit.addUnits(1);
				fromTerrit.addUnits(-1);
				conqueredTerritory = true;
				
				if (fromTerrit.units > 1) {
					state = State.ADVANCE;
					message(((Button) findViewById(R.id.action_button)).getText() + " - Click to advance your armies.");
					((Button) findViewById(R.id.action_button)).setText(R.string.action_advance_troops);
					fromTerrit.hilite();
					toTerrit.hilite();
				} else {
					state = State.ADVANCE;
					endAdvance();
					fromTerrit.unhilite();
				}
				
				log(activePlayer.name + " conquered " + territ.name + " from " + territ.owner.name + ".");
				activePlayer.updateStats(Player.TERRITORIES_CONQUERED, 1);
				otherPlayer.updateStats(Player.TERRITORIES_LOST, 1);
			} else if (all && fromTerrit.units > 3) {
				attack(toTerrit, true);
			} else {
				toTerrit = null;
			}
			
			playerPnlHash.get(activePlayer).update();
			playerPnlHash.get(otherPlayer).update();
		}
	}

	private static int[] rollDice(int n) {
		int[] results = new int[n];
		for (int i = 0; i < n; i++) {
			results[i] = (int) (Math.random() * 6) + 1;
		}
		return results;
	}
	
	private static String printDice(int[] dice) {
		String ret = "";
		for (int i = 0; i < dice.length; i++) {
			if (i != 0) {
				ret += ",";
			}
			ret += dice[i];
		}
		return ret;
	}

	private int[] getAttackOutcome(int attackers, int defenders) {		
		int[] attackDice;
		int[] defendDice;
		if (attackers >= 4) {
			attackDice = rollDice(3);
		} else if (attackers == 3) {
			attackDice = rollDice(2);
		} else {
			attackDice = rollDice(1);
		}
		defendDice = rollDice(defenders >= 2 ? 2 : 1);

		Arrays.sort(attackDice);
		Arrays.sort(defendDice);

		message(getResources().getString(R.string.message_dice, printDice(attackDice), printDice(defendDice)));
		
		int[] outcome = {0, 0};
		for (int i = attackDice.length - 1, j = defendDice.length - 1; i >= 0 && j >= 0; i--, j--) {
			outcome[attackDice[i] > defendDice[j] ? 1 : 0]++;
		}
		
		if (attackDice.length == 3 && defendDice.length == 2) {
			if (outcome[0] == 0 && outcome[1] == -2)
				activePlayer.updateDiceStats(Player.W3V2);
			else if (outcome[0] == -1 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.T3V2);
			else
				activePlayer.updateDiceStats(Player.L3V2);
		} else if (attackDice.length == 2 && defendDice.length == 2) {
			if (outcome[0] == 0 && outcome[1] == -2)
				activePlayer.updateDiceStats(Player.W2V2);
			else if (outcome[0] == -1 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.T2V2);
			else
				activePlayer.updateDiceStats(Player.L2V2);
		} else if (attackDice.length == 3 && defendDice.length == 1) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W3V1);
			else
				activePlayer.updateDiceStats(Player.L3V1);
		} else if (attackDice.length == 2 && defendDice.length == 1) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W2V1);
			else
				activePlayer.updateDiceStats(Player.L2V1);
		} else if (attackDice.length == 1 && defendDice.length == 1) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W1V1);
			else
				activePlayer.updateDiceStats(Player.L1V1);
		} else if (attackDice.length == 1 && defendDice.length == 2) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W1V2);
			else
				activePlayer.updateDiceStats(Player.L1V2);
		}
		
		return outcome;
	}
	
	private int getTroopTransferCount(boolean all, int max) {
		String amountString = "1";//troopAmountCombo.getSelectedItem().toString();
		return (all || amountString.equals("All")) ? max : Math.min(max, Integer.parseInt(amountString));
	}
	
	public void advance(Territory territ, boolean all) {
		if (territ == fromTerrit) {
			if (toTerrit.units > 1) {
				int toPlace = getTroopTransferCount(all, toTerrit.units - 1);
				toTerrit.addUnits(-toPlace);
				fromTerrit.addUnits(toPlace);
			}
			
			if (all)
				endAdvance();
		} else if (territ == toTerrit) {
			if (fromTerrit.units > 1) {
				int toPlace = getTroopTransferCount(all, fromTerrit.units - 1);
				fromTerrit.addUnits(-toPlace);
				toTerrit.addUnits(toPlace);
			}
			
			if (all) {
				endAdvance();
			}
		}
	}
	
	public void fortify(Territory territ, boolean all) {
		if (territ.owner != activePlayer) {
			return;
		}
		
		if (fromTerrit == null) {
			if (territ.units == 1)
				return;
			fromTerrit = territ;
			fromTerrit.hilite();
			message("Click to fortify from " + fromTerrit.name + ".");
		} else if (toTerrit == null) {
			if (!fromTerrit.isFortifyConnecting(territ)) {
				error(fromTerrit.name + " does not connect with " + territ.name + "!");
				return;
			}
			
			toTerrit = territ;
			toTerrit.hilite();
			int toPlace = getTroopTransferCount(all, fromTerrit.units - 1);
			toTerrit.addUnits(toPlace);
			fromTerrit.addUnits(-toPlace);
			
			log(activePlayer.name + " fortified " + toTerrit.name + " with " + toPlace + " troops from " + fromTerrit.name + ".");
			
			if (all) {
				endFortifications();
			}
		} else if (territ == toTerrit) {
			if (fromTerrit.units == 1)
				return;
			
			int toPlace = getTroopTransferCount(all, fromTerrit.units - 1);
			toTerrit.addUnits(toPlace);
			fromTerrit.addUnits(-toPlace);
			
			log(activePlayer.name + " fortified " + territ.name + " with " + toPlace + " troops from " + fromTerrit.name + ".");
			
			if (all) {
				endFortifications();
			}
		} else if (territ == fromTerrit) {
			if (toTerrit.units == 1)
				return;
			
			int toPlace = getTroopTransferCount(all, toTerrit.units - 1);
			toTerrit.addUnits(-toPlace);
			fromTerrit.addUnits(toPlace);
			
			log(activePlayer.name + " fortified " + territ.name + " with " + toPlace + " troops from " + toTerrit.name + ".");
			
			if (all) {
				endFortifications();
			}
		}
	}
	
	private void calculate(Territory t) {
		if (fromTerrit == null && t.owner != activePlayer)
			return;
		if (fromTerrit == null || t.owner == activePlayer) {
			if (t.units == 1) {
				error("Cannot attack with only 1 troop!");
				return;
			}
			if (fromTerrit != null)
				fromTerrit.unhilite();
			fromTerrit = t;
			fromTerrit.hilite();
		} else {
			if (!fromTerrit.isConnecting(t)) {
				error(fromTerrit.name + " does not connect with " + t.name + "!");
				return;
			}
			message(riskCalc.getResults(fromTerrit.units, t.units));
		}
	}
	
	public void endTurn() {
		if (cardType != CardSetting.NONE && conqueredTerritory) {
			activePlayer.giveCard(deck.get((int) (Math.random() * deck.size())));
			log(activePlayer.name + " gets a card.");
		}
		playerPnlHash.get(activePlayer).update();
		incrementTurn();
	}
	
	private void updateStats() {
		for (Player player : players) {
			if (player.isLiving()) {
				player.setStats(Player.TROOPS, map.getTroopCount(player));
				player.setStats(Player.TERRITORIES, map.getTerritoryCount(player));
				player.updateLuckStats();
			}
		}
	}
	
	private void updateRound() {
		updateStats();
		for (Player player : players) {
			if (player.isLiving())
				player.updateRound();
		}
	}
	
	public int getBonus(Player player) {
		Continent[] continents = map.getContinents();
		int total = 0;
		for (Continent cont : continents) {
			if (cont.hasContinent(player))
				total += cont.getBonus();
		}
		return Math.max((int) (map.getTerritoryCount(player) / 3), 3) + total;
	}
	
	public void resumeGame() {
		paused = false;
		((Button) findViewById(R.id.action_button)).setText("");
	}
	
	public void pauseGame() {
		paused = true;
	}
	
	public void simulate(int n) {
		simulate = true;
		simulateCount = n;
	}
	
	public void message(String msg) {
		((TextView) findViewById(R.id.action_label)).setText(msg);
	}
	
	public Map getMap() {
		return map;
	}
	
	public void log(String msg) {
		//gameLog.append(msg + "\n");
	}
	
	public void error(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle(R.string.error_alert_title)
				.setMessage(msg);
		builder.create().show();
	}
	
	public void hiliteTerritories(Territory[] territories) {
		//for (Territory territ : territories) {
			//Button btn = territ.getButton();
			//if (btn.getBorder() == null)
			//	btn.setBorder(Territory.BORDER_HIGHLIGHT);
		//}
	}
	
	public void unhiliteTerritories(Territory[] territories) {
		//for (Territory territ : territories) {
		//	if (territ != fromTerrit && territ != toTerrit)
		//		territ.getButton().setBorder(null);
		//}
	}

	@Override
	public void onGameContinue() {
		
	}

	@Override
	public void onGamePause() {
		pauseGame();
	}

	@Override
	public void onGameSimulate(int rounds) {
		simulate(rounds);
	}

	@Override
	public void onPlaySet(int which, int extra) {
		if (state != State.DEPLOY) {
			state = State.DEPLOY;
			deployNum = extra;
			((Button) findViewById(R.id.action_button)).setEnabled(false);
			message(activePlayer.name + " you have " + deployNum + " troops to place from your cash-in.");
			return;
		}
		
		if (cardType == CardSetting.REGULAR) {
			cashes++;
		} else {
			activePlayer.cashes++;
		}
		
		Card[] setPlayed = activePlayer.getSets()[which];
		activePlayer.playSet(setPlayed);
		
		for (int i = 0; i < 3; i++) {
			if (setPlayed[i].territory.owner == activePlayer) {
				setPlayed[i].territory.addUnits(2);
				activePlayer.updateStats(Player.TROOPS_DEPLOYED, 2);
			}
		}
		
		if (activePlayer.isAI())
			((AIPlayer) activePlayer).message(activePlayer.name + " played a set worth " + extra + " troops.");
		log(activePlayer.name + " played a set worth " + extra + " troops.");
		
		if (cardType == CardSetting.REGULAR) {
			int nextCashIn = (cashes >= CASH_IN.length) ? extra + 5 : CASH_IN[cashes];
			((TextView) findViewById(R.id.cash_in_label)).setText(Integer.toString(nextCashIn));
		}
		
		playerPnlHash.get(activePlayer).update();
		continueTurnAfterSet(extra);
	}

	@Override
	public void onCancelSet() {
		continueTurnAfterSet(0);
	}

	@Override
	public void onClick(View v) {
		Territory territ = buttonMap.get(v);
		if (territ == null) {
			return;
		}
		switch (state) {
		case PLACE:
			place(territ);
			break;
		case DEPLOY:
			deploy(territ, false);
			break;
		case ATTACK:
			attack(territ, false);
			break;
		case ADVANCE:
			advance(territ, false);
			break;
		case FORTIFY:
			fortify(territ, false);
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		Territory territ = buttonMap.get(v);
		if (territ == null) {
			return false;
		}
		switch (state) {
		case PLACE:
			place(territ);
			break;
		case DEPLOY:
			deploy(territ, true);
			break;
		case ATTACK:
			attack(territ, true);
			break;
		case ADVANCE:
			advance(territ, true);
			break;
		case FORTIFY:
			fortify(territ, true);
			break;
		}
		return true;
	}
}
