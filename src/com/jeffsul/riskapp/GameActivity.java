package com.jeffsul.riskapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.jeffsul.riskapp.RiskCalculator;
import com.jeffsul.riskapp.entities.Card;
import com.jeffsul.riskapp.entities.Continent;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.entities.Territory;
import com.jeffsul.riskapp.players.AIPlayer;
import com.jeffsul.riskapp.players.Player;
import com.jeffsul.riskapp.players.PlayerPanel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

public class GameActivity extends Activity {
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
	
	private boolean shiftKeyDown;
	private boolean ctrlKeyDown;
	
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
			if (!playerType[i].isSelected()) {
				players[i] = new Player(i + 1, playerNames[i].getText(), PLAYER_COLOURS[i]);
			} else {
				players[i] = new AIPlayer(i + 1, PLAYER_COLOURS[i], this);
			}
			
			PlayerPanel playerPnl = new PlayerPanel(players[i]);
			if (i < half) {
				sidePnl1.add(playerPnl);
			} else {
				sidePnl2.add(playerPnl);
			}
		}
		
		activePlayer = players[index];
		playerPnlHash.get(activePlayer).setActive(true);
		message(activePlayer.name + ", you have " + placeNum + " armies left to place.");
		
		ArrayList<Territory> territories = map.getTerritories();
		int terrCount = territories.size();
		deck.removeAll(deck);
		for (int i = 0; i < terrCount; i++) {
			Territory territ = territories.remove((int) (Math.random() * territories.size()));
			territ.setOwner(players[i % numPlayers]);
			deck.add(new Card(territ, i % 3));
			gamePnl.add(territ.getButton());
		}
		
		for (Player player : players) {
			playerPnlHash.get(player).update();
		}
		log("Game Initialized.");
		
		ImageIcon img = (useEpicMap) ? new ImageIcon(getClass().getResource("Epic.jpg")) : new ImageIcon(getClass().getResource("Regular.jpg"));
		JLabel imgLbl = new JLabel(img);
		gamePnl.add(imgLbl);
		imgLbl.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
		
		saveGame();
		beginPlacement();
	}
	
	private void initGUI() {
		gameScreen = new JPanel(new BorderLayout());
		
		final JTabbedPane tabs = new JTabbedPane();
		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				if (tabs.getSelectedIndex() != 1) {
					tabs.requestFocus();
				}
			}
		});
		
		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				if (tabs.getSelectedIndex() == 1) {
					updateStats();
					statsScreen.requestFocus();
				} else {
					tabs.requestFocus();
				}
			}
		});
		
		tabs.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_SHIFT)
					shiftKeyDown = true;
				else if (event.getKeyCode() == KeyEvent.VK_CONTROL)
					ctrlKeyDown = true;
			}
			
			public void keyReleased(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_SHIFT)
					shiftKeyDown = false;
				else if (event.getKeyCode() == KeyEvent.VK_CONTROL)
					ctrlKeyDown = false;
			}
		});
		
		JPanel playPnl = new JPanel();
		playPnl.setLayout(new BorderLayout());
		tabs.addTab("Game", playPnl);
		
		gamePnl = new JPanel();
		playPnl.add(gamePnl, BorderLayout.CENTER);
		if (useEpicMap)
			gamePnl.setPreferredSize(new Dimension(900, 618));
		else
			tabs.setPreferredSize(new Dimension(794, 618));
		gamePnl.setLayout(null);
		gameScreen.add(tabs, BorderLayout.CENTER);
		
		actionLbl = new JLabel();
		actionLbl.setAlignmentX(CENTER_ALIGNMENT);
		actionLbl.setFont(new Font("Helvetica", Font.BOLD, 16));
		
		JPanel optionPnl = new JPanel();
		optionPnl.setLayout(new BoxLayout(optionPnl, BoxLayout.Y_AXIS));
		if (actionBtn == null) {
			actionBtn = new JButton(ACTION_END_ATTACKS);
			actionBtn.setAlignmentX(CENTER_ALIGNMENT);
			actionBtn.setFocusable(false);
			actionBtn.setEnabled(false);
			actionBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
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
			});
		}
		
		JPanel actionPnl = new JPanel();
		if (troopAmountCombo == null) {
			troopAmountCombo = new JComboBox<String>(new String[] {"1", "2", "3", "5", "10", "All"});
			troopAmountCombo.setFocusable(false);
		}
		actionPnl.add(actionBtn);
		actionPnl.add(new JLabel("Troops/click:"));
		actionPnl.add(troopAmountCombo);
		
		gameLog = new JTextArea(6, 5);
		gameLog.setEditable(false);
		JScrollPane sp = new JScrollPane(gameLog);
		sp.setPreferredSize(new Dimension(400, 300));
		JButton logBtn = new JButton("Log");
		logBtn.setFocusable(false);
		final JFrame logWin = new JFrame("Game Log");
		logWin.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 300, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 125);
		logWin.add(sp);
		logWin.pack();
		logBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logWin.setVisible(true);
			}
		});
		actionPnl.add(logBtn);
		
		autoPlayBtn = new JButton("Autoplay");
		autoPlayBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO(jeffsul): Implement.
			}
		});
		actionPnl.add(autoPlayBtn);
		
		if (cardType == REGULAR) {
			cashInLbl = new JLabel(Integer.toString(CASH_IN[0]));
			actionPnl.add(new JLabel("Next Cash-In:"));
			actionPnl.add(cashInLbl);
			actionPnl.add(new JLabel("troops"));
		}
		optionPnl.add(actionPnl);
		
		statsScreen = new StatsScreen(players);
		tabs.addTab("Statistics", statsScreen);
		
		optionPnl.add(actionLbl);
		playPnl.add(optionPnl, BorderLayout.PAGE_END);
		
		sidePnl1 = new JPanel();
		sidePnl2 = new JPanel();
		JScrollPane sp1 = new JScrollPane(sidePnl1);
		JScrollPane sp2 = new JScrollPane(sidePnl2);
		int rows = (int) Math.ceil(numPlayers / 2.0);
		sidePnl1.setLayout(new GridLayout(rows, 1));
		sidePnl2.setLayout(new GridLayout(rows, 1));
		Dimension dim = (useEpicMap) ? new Dimension(120, 640) : new Dimension(120, 500);
		sp1.setPreferredSize(dim);
		sp2.setPreferredSize(dim);
		gameScreen.add(sp1, BorderLayout.LINE_START);
		gameScreen.add(sp2, BorderLayout.LINE_END);
	}
	
	private void handleTurn() {
		if (activePlayer.isAI())
			handleAITurn();
		else
			beginTurn();
	}
	
	private void handleAITurn() {
		while (activePlayer.isAI()) {
			AIPlayer aiPlayer = (AIPlayer) activePlayer;
			beginTurn();
			aiPlayer.deploy();
			aiPlayer.attack();
			aiPlayer.fortify();
			
			playerPnlHash.get(activePlayer).update();
			gamePnl.paintImmediately(0, 0, gamePnl.getWidth(), gamePnl.getHeight());
			sidePnl1.paintImmediately(0, 0, sidePnl1.getWidth(), sidePnl1.getHeight());
			sidePnl2.paintImmediately(0, 0, sidePnl2.getWidth(), sidePnl2.getHeight());
		}
	}
	
	private void incrementTurn() {
		index++;
		for (int i = index;; i++) {
			index = i % numPlayers;
			if (players[index].isLiving())
				break;
		}
			
		if (index == firstPlayerIndex) {
			updateRound();
			round++;
			log("Beginning Round " + round);
		}
			
		playerPnlHash.get(activePlayer).setActive(false);
		activePlayer = players[index];
		
		if (saveFile != null)
			saveGame();
		
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
		
		int troopCount = Math.max(map.getTerritoryCount(activePlayer) / 3, 3);
		log(activePlayer.name + " gets " + troopCount + " troops for " + map.getTerritoryCount(activePlayer) + " territories.");
		
		int extra = 0;
		if (activePlayer.hasSet())
			extra += playSet();
		troopCount += extra;
		
		state = State.DEPLOY;
		
		conqueredTerritory = false;
		fromTerrit = null;
		toTerrit = null;
		
		actionBtn.setText("End Attacks");
		actionBtn.setEnabled(false);
		autoPlayBtn.setEnabled(true);
		
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
	
	public int playSet() {
		Card[][] sets = activePlayer.getSets();
		if (sets.length == 0)
			return 0;
		
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
		if (cardType == REGULAR)
			extra = (cashes >= CASH_IN.length) ? 5 * cashes - 10 : CASH_IN[cashes];
		else if (cardType == MODIFIED)
			extra = (activePlayer.cashes >= CASH_IN.length) ? 5 * activePlayer.cashes - 10 : CASH_IN[activePlayer.cashes];
		
		String setChosen = null;
		if (!activePlayer.isAI()) {
			if (activePlayer.getCardCount() >= 5) {
				while (setChosen == null) {
					setChosen = (String) JOptionPane.showInputDialog(this, "You have a set to play for " + extra + " extra troops. Choose a set:", "Play a Set",
							JOptionPane.QUESTION_MESSAGE, null, setTexts, "");
				}
			} else {
				setChosen = (String) JOptionPane.showInputDialog(this, "You have a set to play for " + extra + " extra troops. Choose a set or cancel:", "Play a Set",
						JOptionPane.QUESTION_MESSAGE, null, setTexts, "");
			}
		} else {
			setChosen = setTexts[0];
		}
		
		if (setChosen != null) {
			if (cardType == REGULAR)
				cashes++;
			else
				activePlayer.cashes++;
			
			Card[] setPlayed = null;
			for (int i = 0; i < setTexts.length; i++) {
				if (setTexts[i].equals(setChosen)) {
					setPlayed = sets[i];
					break;
				}
			}
			if (setPlayed == null)
				return 0;
			
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
			
			if (cardType == REGULAR) {
				int nextCashIn = (cashes >= CASH_IN.length) ? extra + 5 : CASH_IN[cashes];
				cashInLbl.setText(Integer.toString(nextCashIn));
			}
			
			playerPnlHash.get(activePlayer).update();
			if (activePlayer.hasSet())
				extra += playSet();
		}
		else
			return 0;
		
		return extra;
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
			message(activePlayer.name + ", you have " + placeNum + " armies left to deploy.");
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
		String placing = troopAmountCombo.getSelectedItem().toString();
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
		actionBtn.setEnabled(true);
		autoPlayBtn.setEnabled(false);
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
		actionBtn.setText("End Attacks");
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
				((AIPlayer) eliminatedPlayer).message("Argh!", RiskGame.this);
			
			if (count <= 1) {
				activePlayer.setStats(Player.TERRITORIES, map.getTerritoryCount(activePlayer));
				activePlayer.setStats(Player.TROOPS, map.getTroopCount(activePlayer));
				JOptionPane.showMessageDialog(null, activePlayer.name + " won the game!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
				gameOver = true;
				return;
			}
			activePlayer.giveCards(eliminatedPlayer.takeAllCards());
			playerPnlHash.get(activePlayer).update();
			playerPnlHash.get(eliminatedPlayer).update();
			eliminatedPlayer = null;
			
			if (activePlayer.getCardCount() >= 5) {
				deployNum = playSet();
				state = State.DEPLOY;
				actionBtn.setEnabled(false);
				message(activePlayer.name + " you have " + deployNum + " troops to place from your cash-in.");
			}
		}
	}
	
	public void endAttacks() {
		if (state != State.ATTACK)
			return;
		
		state = State.FORTIFY;
		actionBtn.setText("End Fortification");
		if (fromTerrit != null)
			fromTerrit.unhilite();
		fromTerrit = null;
		message("Fortify - click from one territory to another to fortify troops.");
	}
	
	public void endFortifications() {
		if (fromTerrit != null)
			fromTerrit.unhilite();
		if (toTerrit != null)
			toTerrit.unhilite();
		endTurn();
	}
	
	public void attack(Territory territ, boolean all) {
		if (territ.owner == activePlayer) {
			if (fromTerrit != null)
				fromTerrit.unhilite();
			
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
			
			int[] outcome = getOutcome(fromTerrit, toTerrit);
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
					message(actionLbl.getText() + " - Click to advance your armies.");
					actionBtn.setText("Advance Troops");
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
			}
			else if (all && fromTerrit.units > 3)
				attack(toTerrit, true);
			else
				toTerrit = null;
			
			playerPnlHash.get(activePlayer).update();
			playerPnlHash.get(otherPlayer).update();
		}
	}
	
	public int[] getOutcome(Territory from, Territory to) {
		int attackers = from.units;
		int defenders = to.units;
		
		int[] attackDice;
		int[] defendDice;
		
		if (attackers >= 4)
			attackDice = new int[] {rollDice(), rollDice(), rollDice()};
		else if (attackers == 3)
			attackDice = new int[] {rollDice(), rollDice()};
		else
			attackDice = new int[] {rollDice()};
		
		if (defenders >= 2)
			defendDice = new int[] {rollDice(), rollDice()};
		else
			defendDice = new int[] {rollDice()};
		
		int attackMax = 0;
		int attackMed = 0;
		boolean first = true;
		String dice = "";
		for (int i = 0; i < attackDice.length; i++) {
			if (attackDice[i] > attackMax) {
				attackMed = attackMax;
				attackMax = attackDice[i];
			} else if (attackDice[i] > attackMed)
				attackMed = attackDice[i];
			if (first)
				first = false;
			else
				dice += ",";
			dice += attackDice[i];
		}
		
		int defendMax = 0;
		int defendLow = 0;
		first = true;
		dice += " vs. ";
		for (int i = 0; i < defendDice.length; i++) {
			if (defendDice[i] > defendMax) {
				defendLow = defendMax;
				defendMax = defendDice[i];
			}
			else
				defendLow = defendDice[i];
			if (first)
				first = false;
			else
				dice += ",";
			dice += defendDice[i];
		}
		message(dice);
		
		int[] outcome = {0, 0};
		if (attackMax > defendMax)
			outcome[1]--;
		else
			outcome[0]--;
		
		if (attackMed > defendLow && attackMed != 0 && defendLow != 0)
			outcome[1]--;
		else if (defendLow >= attackMed && attackMed != 0 && defendLow != 0)
			outcome[0]--;
		
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
		String amountString = troopAmountCombo.getSelectedItem().toString();
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
			
			if (all)
				endAdvance();
		}
	}
	
	public void fortify(Territory territ, boolean all) {
		if (territ.owner != activePlayer)
			return;
		
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
			
			if (all)
				endFortifications();
		} else if (territ == toTerrit) {
			if (fromTerrit.units == 1)
				return;
			
			int toPlace = getTroopTransferCount(all, fromTerrit.units - 1);
			toTerrit.addUnits(toPlace);
			fromTerrit.addUnits(-toPlace);
			
			log(activePlayer.name + " fortified " + territ.name + " with " + toPlace + " troops from " + fromTerrit.name + ".");
			
			if (all)
				endFortifications();
		} else if (territ == fromTerrit) {
			if (toTerrit.units == 1)
				return;
			
			int toPlace = getTroopTransferCount(all, toTerrit.units - 1);
			toTerrit.addUnits(-toPlace);
			fromTerrit.addUnits(toPlace);
			
			log(activePlayer.name + " fortified " + territ.name + " with " + toPlace + " troops from " + toTerrit.name + ".");
			
			if (all)
				endFortifications();
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
	
	public void simulate(int n) {
		simulate = true;
		simulateCount = n;
	}
	
	private int rollDice() {
		return (int) (Math.random() * 6) + 1;
	}
	
	public void message(String msg) {
		actionLbl.setText(msg);
	}
	
	public void log(String msg) {
		gameLog.append(msg + "\n");
	}
	
	public void error(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Oops!", JOptionPane.ERROR_MESSAGE);
	}
	
	public void hiliteTerritories(Territory[] territories) {
		for (Territory territ : territories) {
			JButton btn = territ.getButton();
			if (btn.getBorder() == null)
				btn.setBorder(Territory.BORDER_HIGHLIGHT);
		}
	}
	
	public void unhiliteTerritories(Territory[] territories) {
		for (Territory territ : territories) {
			if (territ != fromTerrit && territ != toTerrit)
				territ.getButton().setBorder(null);
		}
	}
}
