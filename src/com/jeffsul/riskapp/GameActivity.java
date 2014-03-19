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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.jeffsul.riskapp.dialogs.AutoGameDialogFragment;
import com.jeffsul.riskapp.dialogs.PlaySetDialogFragment;
import com.jeffsul.riskapp.entities.Card;
import com.jeffsul.riskapp.entities.Continent;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.entities.Territory;
import com.jeffsul.riskapp.players.AIPlayer;
import com.jeffsul.riskapp.players.Player;
import com.jeffsul.riskapp.players.PlayerPanel;
import com.jeffsul.riskapp.ui.ActionButton;
import com.jeffsul.riskapp.ui.ActionLabel;

public class GameActivity extends Activity implements AutoGameDialogFragment.Listener,
		PlaySetDialogFragment.Listener, View.OnClickListener, View.OnLongClickListener, GameListener {
	public static final String NUM_PLAYERS_EXTRA = "com.jeffsul.risk.NUM_PLAYERS";
	public static final String CARD_SETTING_EXTRA = "com.jeffsul.risk.CARD_SETTING";
	public static final String MAP_EXTRA = "com.jeffsul.risk.MAP";
	
	private static final int[] PLAYER_COLOURS = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
		Color.CYAN};
	private static final int[] CASH_IN = {4, 6, 8, 10, 12, 15};
	private static final int CASH_IN_INCREMENT = 5;
	private static final int INITIAL_PLACE_COUNT = 1;

	public static enum State {PLACE, DEPLOY, ATTACK, ADVANCE, FORTIFY};
	public static enum CardSetting {REGULAR, NONE, MODIFIED};
	
	private int numPlayers;
	public Player[] players;
	private HashMap<Player, PlayerPanel> playerPnlHash;
	private Player activePlayer;
	private Player eliminatedPlayer;
	
	private int index;
	private int round;
	private State state;
	private ArrayList<StateListener> stateListeners;
	private int firstPlayerIndex;
	
	public ArrayList<Card> deck;
	private int cashes;
	
	private Territory fromTerrit;
	private Territory toTerrit;
	
	private Map map;
	private HashMap<View, Territory> buttonMap;
	public CardSetting cardType;
	
	private GameLog gameLog;
	
	public boolean autoGame;
	public boolean simulate;
	//private int simulateCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Intent intent = getIntent();
		numPlayers = intent.getIntExtra(NUM_PLAYERS_EXTRA, 2);
		switch (intent.getIntExtra(MAP_EXTRA, 0)) {
		case 0:
			map = new Map(this, false);
			break;
		case 1:
			map = new Map(this, true);
			break;
		}
		switch (intent.getIntExtra(CARD_SETTING_EXTRA, 0)) {
		case 0:
			cardType = CardSetting.REGULAR;
			break;
		case 1:
			cardType = CardSetting.NONE;
			break;
		case 2:
			cardType = CardSetting.MODIFIED;
			break;
		}
		initializeGame();
	}

	private void initializeGame() {
		players = new Player[numPlayers];
		firstPlayerIndex = (int) (numPlayers * Math.random());
		index = firstPlayerIndex;
		playerPnlHash = new HashMap<Player, PlayerPanel>();
		
		TextView cashInLabel = (TextView) findViewById(R.id.cash_in_label);
		if (cardType != CardSetting.REGULAR) {
			((ViewGroup) cashInLabel.getParent()).removeView(cashInLabel);
		} else {
			cashInLabel.setText(getResources().getString(R.string.next_cash_in, CASH_IN[0]));
		}
		
		int half = (int) Math.ceil(numPlayers / 2.0);
		int panelHeight = getResources().getDisplayMetrics().heightPixels / half;
		ViewGroup sidePanelLeft = (ViewGroup) findViewById(R.id.side_panel_left);
		ViewGroup sidePanelRight = (ViewGroup) findViewById(R.id.side_panel_right);
		for (int i = 0; i < numPlayers; i++) {
			//if (!playerType[i].isSelected()) {
				players[i] = new Player(i + 1, "Player " + (i + 1), PLAYER_COLOURS[i]);
				players[i].setDeployCount(INITIAL_PLACE_COUNT);
			//} else {
			//	players[i] = new AIPlayer(i + 1, PLAYER_COLOURS[i], this);
			//}
			PlayerPanel playerPnl = new PlayerPanel(this, players[i]);
			playerPnl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, panelHeight));
			map.addListener(playerPnl);
			players[i].addListener(playerPnl);
			playerPnlHash.put(players[i], playerPnl);
			if (i < half) {
				sidePanelLeft.addView(playerPnl);
			} else {
				sidePanelRight.addView(playerPnl);
			}
		}
		map.setPlayers(players);
		
		activePlayer = players[index];
		playerPnlHash.get(activePlayer).setActive(true);
		
		RelativeLayout gamePnl = (RelativeLayout) findViewById(R.id.game_panel);
		ArrayList<Territory> territories = map.getTerritories();
		buttonMap = new HashMap<View, Territory>();
		int terrCount = territories.size();
		deck = new ArrayList<Card>();
		for (int i = 0; i < terrCount; i++) {
			Territory territ = territories.remove((int) (Math.random() * territories.size()));
			territ.setOwner(players[i % numPlayers]);
			buttonMap.put(territ.getButton(), territ);
			deck.add(new Card(territ, i % 3));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(35, 35);
			params.leftMargin = territ.x - 10;
			params.topMargin = territ.y - 10;
			gamePnl.addView(territ.getButton(), params);
		}
		
		gameLog = new GameLog();
		gameLog.log(getResources().getString(R.string.log_game_initialized));
		
		//saveGame();

		stateListeners = new ArrayList<StateListener>();
		stateListeners.add((ActionButton) findViewById(R.id.action_button));
		stateListeners.add((ActionLabel) findViewById(R.id.action_label));
		for (Player p : players) {
			stateListeners.add(p);
		}
		changeState(State.PLACE);
	}

	private void changeState(State newState) {
		state = newState;
		activePlayer.setState(newState);
		for (StateListener listener : stateListeners) {
			listener.onStateChange(activePlayer, newState);
		}
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
		}
			
		playerPnlHash.get(activePlayer).setActive(false);
		activePlayer = players[index];
		
		//if (saveFile != null)
		//	saveGame();
		
		beginTurn();
	}
	
	private void beginTurn() {
		if (!autoGame) {
			autoGame = true;
			for (int i = 0; i < numPlayers; i++) {
				if (!players[i].isAI() && players[i].isLiving()) {
					autoGame = false;
				}
			}
		}

		activePlayer.resetForTurn();
		playerPnlHash.get(activePlayer).setActive(true);
		
		continueTurnAfterSet(0);
	}
	
	private void continueTurnAfterSet(int extra) {
		if (activePlayer.hasSet()) {
			playSet();
			return;
		}
		
		int troopCount = Math.max(map.getTerritoryCount(activePlayer) / 3, 3);
		gameLog.log(getResources().getString(R.string.log_territory_bonus, activePlayer.name, troopCount, map.getTerritoryCount(activePlayer)));
		
		troopCount += extra;

		fromTerrit = null;
		toTerrit = null;
		
		Continent[] conts = map.getContinents();
		for (Continent cont : conts) {
			if (cont.hasContinent(activePlayer)) {
				troopCount += cont.getBonus();
				gameLog.log(getResources().getString(R.string.log_continent_bonus, activePlayer.name, cont.getBonus(), cont.name));
			}
		}
		activePlayer.updateStats(Player.BONUS, troopCount - extra);
		activePlayer.setDeployCount(troopCount);
		changeState(State.DEPLOY);
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

	@Override
	public void place(Territory territ) {
		if (territ.owner != activePlayer) {
			return;
		}
		territ.addUnits(1);
		activePlayer.changeDeployCount(-1);
		gameLog.log(getResources().getString(R.string.log_deployed_troops, activePlayer.name, 1, territ.name));
		
		playerPnlHash.get(activePlayer).setActive(false);
		index++;
		activePlayer = players[index % numPlayers];
		playerPnlHash.get(activePlayer).setActive(true);
		if (activePlayer.getDeployCount() > 0) {
			changeState(State.PLACE);
		} else {
			updateRound();
			beginTurn();
		}
	}

	@Override
	public void deploy(Territory territ, boolean all) {
		if (territ.owner != activePlayer) {
			return;
		}
		int toPlace = all ? activePlayer.getDeployCount() : 1;
		territ.addUnits(toPlace);
		activePlayer.changeDeployCount(-toPlace);
		activePlayer.updateStats(Player.TROOPS_DEPLOYED, toPlace);
		gameLog.log(getResources().getString(R.string.log_deployed_troops, activePlayer.name, toPlace, territ.name));
		
		if (activePlayer.getDeployCount() == 0) {
			changeState(State.ATTACK);
		} else {
			message(getResources().getString(R.string.message_deploy_armies, activePlayer.name, activePlayer.getDeployCount()));
		}
	}

	@Override
	public boolean attack(Territory from, Territory to, boolean all) {
		fromTerrit = from;
		return attack(to, all);
	}

	@Override
	public void fortify(Territory from, Territory to, boolean all) {
		fromTerrit = from;
		fortify(to, all);
	}

	@Override
	public void endAdvance() {
		changeState(State.ATTACK);
		if (toTerrit.units != 1) {
			fromTerrit.unhilite();
			fromTerrit = toTerrit;
		} else {
			toTerrit.unhilite();
		}
		toTerrit = null;
		
		if (eliminatedPlayer != null) {
			gameLog.log(getResources().getString(R.string.log_player_eliminated, activePlayer.name, eliminatedPlayer.name));
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
				if (players[i].isLiving()) {
					count++;
				}
			}
			
			if (eliminatedPlayer.isAI() && !simulate) {
				((AIPlayer) eliminatedPlayer).message("Argh!", this, autoGame);
			}
			
			if (count <= 1) {
				activePlayer.setStats(Player.TERRITORIES, map.getTerritoryCount(activePlayer));
				activePlayer.setStats(Player.TROOPS, map.getTroopCount(activePlayer));
				// TODO(jeffsul): Game win message.
				return;
			}
			activePlayer.giveCards(eliminatedPlayer.takeAllCards());
			eliminatedPlayer = null;
			
			if (activePlayer.getCardCount() >= 5) {
				playSet();
			}
		}
	}

	@Override
	public void endAttacks() {
		if (state != State.ATTACK) {
			return;
		}
		
		changeState(State.FORTIFY);
		if (fromTerrit != null) {
			fromTerrit.unhilite();
		}
		fromTerrit = null;
	}

	@Override
	public void endFortifications() {
		if (fromTerrit != null) {
			fromTerrit.unhilite();
		}
		if (toTerrit != null) {
			toTerrit.unhilite();
		}
		endTurn();
	}
	
	private boolean attack(Territory territ, boolean all) {
		if (territ.owner == activePlayer) {
			if (fromTerrit != null) {
				fromTerrit.unhilite();
			}
			
			if (territ.units == 1) {
				error(getResources().getString(R.string.error_attack_1_troop));
				return false;
			}
			
			fromTerrit = territ;
			fromTerrit.hilite();
			message(getResources().getString(R.string.message_attack_from, fromTerrit.name));
		} else if (fromTerrit != null) {
			if (fromTerrit.units == 1) {
				error(getResources().getString(R.string.error_attack_1_troop));
				return false;
			}
			
			if (!fromTerrit.isConnecting(territ)) {
				error(getResources().getString(R.string.error_does_not_connect, fromTerrit.name, territ.name));
				return false;
			}
			
			toTerrit = territ;
			Player otherPlayer = toTerrit.owner;
			
			int[] attackDice;
			int[] defendDice;
			if (fromTerrit.units >= 4) {
				attackDice = rollDice(3);
			} else if (fromTerrit.units == 3) {
				attackDice = rollDice(2);
			} else {
				attackDice = rollDice(1);
			}
			defendDice = rollDice(toTerrit.units >= 2 ? 2 : 1);
			int[] outcome = getAttackOutcome(attackDice, defendDice);
			message(getResources().getString(R.string.message_dice, printDice(attackDice), printDice(defendDice)));
			updatePlayerDiceStats(attackDice.length, defendDice.length, outcome);
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
				activePlayer.setHasConqueredTerritory();
				
				if (fromTerrit.units > 1) {
					changeState(State.ADVANCE);
					fromTerrit.hilite();
					toTerrit.hilite();
				} else {
					endAdvance();
					fromTerrit.unhilite();
				}
				
				gameLog.log(getResources().getString(R.string.log_territory_conquered, activePlayer.name, territ.name, territ.owner.name));
				activePlayer.updateStats(Player.TERRITORIES_CONQUERED, 1);
				otherPlayer.updateStats(Player.TERRITORIES_LOST, 1);

				return true;
			} else if (all && fromTerrit.units > 3) {
				attack(toTerrit, true);
			} else {
				toTerrit = null;
			}
		}
		return false;
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

	/**
	 * 
	 * @param attackDice Attacker's dice rolls, sorted in ascending order.
	 * @param defendDice Defender's dice rolls, sorted in ascending order.
	 * @return
	 */
	private static int[] getAttackOutcome(int[] attackDice, int[] defendDice) {
		Arrays.sort(attackDice);
		Arrays.sort(defendDice);
		int[] outcome = {0, 0};
		for (int i = attackDice.length - 1, j = defendDice.length - 1; i >= 0 && j >= 0; i--, j--) {
			outcome[attackDice[i] > defendDice[j] ? 1 : 0]--;
		}
		return outcome;
	}

	private void updatePlayerDiceStats(int attackDice, int defendDice, int[] outcome) {
		if (attackDice == 3 && defendDice == 2) {
			if (outcome[0] == 0 && outcome[1] == -2)
				activePlayer.updateDiceStats(Player.W3V2);
			else if (outcome[0] == -1 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.T3V2);
			else
				activePlayer.updateDiceStats(Player.L3V2);
		} else if (attackDice == 2 && defendDice == 2) {
			if (outcome[0] == 0 && outcome[1] == -2)
				activePlayer.updateDiceStats(Player.W2V2);
			else if (outcome[0] == -1 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.T2V2);
			else
				activePlayer.updateDiceStats(Player.L2V2);
		} else if (attackDice == 3 && defendDice == 1) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W3V1);
			else
				activePlayer.updateDiceStats(Player.L3V1);
		} else if (attackDice == 2 && defendDice == 1) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W2V1);
			else
				activePlayer.updateDiceStats(Player.L2V1);
		} else if (attackDice == 1 && defendDice == 1) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W1V1);
			else
				activePlayer.updateDiceStats(Player.L1V1);
		} else if (attackDice == 1 && defendDice == 2) {
			if (outcome[0] == 0 && outcome[1] == -1)
				activePlayer.updateDiceStats(Player.W1V2);
			else
				activePlayer.updateDiceStats(Player.L1V2);
		}
	}

	@Override
	public void advance(Territory territ, boolean all) {
		if (territ == fromTerrit) {
			if (toTerrit.units > 1) {
				int toPlace = all ? toTerrit.units - 1 : 1;
				toTerrit.addUnits(-toPlace);
				fromTerrit.addUnits(toPlace);
			}
			
			if (all)
				endAdvance();
		} else if (territ == toTerrit) {
			if (fromTerrit.units > 1) {
				int toPlace = all ? fromTerrit.units - 1 : 1;
				fromTerrit.addUnits(-toPlace);
				toTerrit.addUnits(toPlace);
			}
			
			if (all) {
				endAdvance();
			}
		}
	}
	
	private void fortify(Territory territ, boolean all) {
		if (territ.owner != activePlayer) {
			return;
		}
		
		if (fromTerrit == null) {
			if (territ.units == 1) {
				return;
			}
			fromTerrit = territ;
			fromTerrit.hilite();
			message(getResources().getString(R.string.message_fortify_from, fromTerrit.name));
		} else if (toTerrit == null) {
			if (!fromTerrit.isFortifyConnecting(territ)) {
				error(getResources().getString(R.string.error_does_not_connect, fromTerrit.name, territ.name));
				return;
			}
			
			toTerrit = territ;
			toTerrit.hilite();
			int toPlace = all ? fromTerrit.units - 1 : 1;
			toTerrit.addUnits(toPlace);
			fromTerrit.addUnits(-toPlace);
			
			gameLog.log(getResources().getString(R.string.log_fortified, activePlayer.name, toTerrit.name, toPlace, fromTerrit.name));
			
			if (all) {
				endFortifications();
			}
		} else if (territ == toTerrit) {
			if (fromTerrit.units == 1) {
				return;
			}
			
			int toPlace = all ? fromTerrit.units - 1 : 1;
			toTerrit.addUnits(toPlace);
			fromTerrit.addUnits(-toPlace);
			
			gameLog.log(getResources().getString(R.string.log_fortified, activePlayer.name, territ.name, toPlace, fromTerrit.name));
			
			if (all) {
				endFortifications();
			}
		} else if (territ == fromTerrit) {
			if (toTerrit.units == 1) {
				return;
			}
			
			int toPlace = all ? toTerrit.units - 1 : 1;
			toTerrit.addUnits(-toPlace);
			fromTerrit.addUnits(toPlace);
			
			gameLog.log(getResources().getString(R.string.log_fortified, activePlayer.name, territ.name, toPlace, toTerrit.name));
			
			if (all) {
				endFortifications();
			}
		}
	}
	
	// TODO(jeffsul): Provide way to see attack odds.
	/*private void calculate(Territory t) {
		if (fromTerrit == null && t.owner != activePlayer)
			return;
		if (fromTerrit == null || t.owner == activePlayer) {
			if (t.units == 1) {
				error(getResources().getString(R.string.error_attack_1_troop));
				return;
			}
			if (fromTerrit != null)
				fromTerrit.unhilite();
			fromTerrit = t;
			fromTerrit.hilite();
		} else {
			if (!fromTerrit.isConnecting(t)) {
				error(getResources().getString(R.string.error_does_not_connect, fromTerrit.name, t.name));
				return;
			}
			message(RiskCalculator.getResults(fromTerrit.units, t.units));
		}
	}*/
	
	private void endTurn() {
		if (cardType != CardSetting.NONE && activePlayer.hasConqueredTerritory()) {
			activePlayer.giveCard(deck.get((int) (Math.random() * deck.size())));
			gameLog.log(getResources().getString(R.string.log_gets_card, activePlayer.name));
		}
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
		round++;
		gameLog.log(getResources().getString(R.string.log_beginning_round, round));
		
		updateStats();
		for (Player player : players) {
			if (player.isLiving()) {
				player.updateRound();
			}
		}
	}
	
	public void simulate(int n) {
		simulate = true;
		//simulateCount = n;
	}
	
	public void message(String msg) {
		((TextView) findViewById(R.id.action_label)).setText(msg);
	}
	
	public Map getMap() {
		return map;
	}
	
	private void error(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle(R.string.error_alert_title)
				.setMessage(msg);
		builder.create().show();
	}
	
	/*private static void hiliteTerritories(Territory[] territories) {
		for (Territory territ : territories) {
			territ.hilite();
		}
	}
	
	private static void unhiliteTerritories(Territory[] territories) {
		for (Territory territ : territories) {
			territ.unhilite();
		}
	}*/

	@Override
	public void onGameContinue() {
		// TODO(jeffsul): Implement
	}

	@Override
	public void onGamePause() {
		// TODO(jeffsul): Implement
	}

	@Override
	public void onGameSimulate(int rounds) {
		simulate(rounds);
	}

	@Override
	public void onPlaySet(int which, int extra) {
		if (state == State.ATTACK) {
			activePlayer.setDeployCount(extra);
			changeState(State.DEPLOY);
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
		
		if (activePlayer.isAI() && !simulate) {
			((AIPlayer) activePlayer).message(activePlayer.name + " played a set worth " + extra + " troops.", this, autoGame);
		}
		gameLog.log(activePlayer.name + " played a set worth " + extra + " troops.");
		
		if (cardType == CardSetting.REGULAR) {
			int nextCashIn = (cashes >= CASH_IN.length) ? extra + 5 : CASH_IN[cashes];
			((TextView) findViewById(R.id.cash_in_label)).setText(Integer.toString(nextCashIn));
		}
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
