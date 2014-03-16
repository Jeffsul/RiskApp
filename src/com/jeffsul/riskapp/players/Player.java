package com.jeffsul.riskapp.players;

import java.util.ArrayList;

import com.jeffsul.riskapp.GameActivity;
import com.jeffsul.riskapp.GameActivity.State;
import com.jeffsul.riskapp.RiskCalculator;
import com.jeffsul.riskapp.StateListener;
import com.jeffsul.riskapp.entities.Card;

public class Player implements StateListener {
	public static final int TROOPS_DEPLOYED = 0;
	public static final int TROOPS_KILLED = 1;
	public static final int TROOPS_LOST = 2;
	public static final int TERRITORIES_CONQUERED = 3;
	public static final int TERRITORIES_LOST = 4;
	public static final int TROOPS = 5;
	public static final int TERRITORIES = 6;
	public static final int BONUS = 7;
	public static final int LUCK_FACTOR = 8;
	
	public static final int W3V2 = 0;
	public static final int T3V2 = 1;
	public static final int L3V2 = 2;
	public static final int W3V1 = 3;
	public static final int L3V1 = 4;
	public static final int W2V2 = 5;
	public static final int T2V2 = 6;
	public static final int L2V2 = 7;
	public static final int W2V1 = 8;
	public static final int L2V1 = 9;
	public static final int W1V1 = 10;
	public static final int L1V1 = 11;
	public static final int W1V2 = 12;
	public static final int L1V2 = 13;
	
	public int number;
	public String name;
	public int color;
	public int cashes;
	public int bonus = 3;
	
	protected int deployCount;
	protected GameActivity.State state;
	
	private boolean alive = true;
	private boolean hasConqueredTerritory;
	
	private ArrayList<Card> cards = new ArrayList<Card>();
	
	private ArrayList<int[]> stats = new ArrayList<int[]>();
	private int[] diceStats;
	
	public Player(int num, String n, int c) {
		name = n;
		number = num;
		color = c;
		
		stats.add(new int[LUCK_FACTOR + 1]);
		diceStats = new int[L1V2 + 1];
	}
	
	public boolean isLiving() {
		return alive;
	}
	
	public void setLiving(boolean living) {
		alive = living;
		if (!alive) {
			setStats(TROOPS, 0);
			setStats(TERRITORIES, 0);
		}
	}
	
	public boolean isAI() {
		return false;
	}
	
	public int getDeployCount() {
		return deployCount;
	}
	
	public void setDeployCount(int deployCount) {
		this.deployCount = deployCount;
	}

	public void changeDeployCount(int delta) {
		deployCount += delta;
	}
	
	public void setState(GameActivity.State newState) {
		state = newState;
	}

	public void resetForTurn() {
		hasConqueredTerritory = false;
		deployCount = 0;
	}

	public void setHasConqueredTerritory() {
		hasConqueredTerritory = true;
	}

	public boolean hasConqueredTerritory() {
		return hasConqueredTerritory;
	}
	
	public int getCardCount() {
		return cards.size();
	}
	
	public boolean hasSet() {
		Card[] temp = new Card[cards.size()];
		cards.toArray(temp);
		return isSet(temp);
	}
	
	private static boolean isSet(Card[] cards) {	
		if (cards.length < 3) {
			return false;
		}
		if (cards.length >= 5) {
			return true;
		}
		
		int inf = 0;
		int cav = 0;
		int art = 0;
		for (Card card : cards) {
			switch (card.type) {
				case Card.INFANTRY:
					inf++;
					break;
				case Card.CAVALRY:
					cav++;
					break;
				case Card.ARTILLERY:
					art++;
					break;
			}
		}
		
		if (inf >= 3 || cav >= 3 || art >= 3 || (inf >= 1 && cav >= 1 && art >= 1)) {
			return true;
		}
		return false;
	}
	
	public Card[][] getSets() {
		int count = cards.size();
		if (count < 3) {
			return new Card[0][];
		}
		
		ArrayList<Card[]> sets = new ArrayList<Card[]>();
		for (int i = 0; i < count; i++) {
			for (int j = i + 1; j < count; j++) {
				for (int k = j + 1; k < count; k++) {
					Card[] set = {cards.get(i), cards.get(j), cards.get(k)};
					if (isSet(set)) {
						sets.add(set);
					}
				}
			}
		}
		
		Card[][] result = new Card[sets.size()][3];
		sets.toArray(result);
		return result;
	}
	
	public void playSet(Card[] set) {	
		cards.remove(set[0]);
		cards.remove(set[1]);
		cards.remove(set[2]);
	}
	
	public void giveCard(Card givenCard) {
		cards.add(givenCard);
	}
	
	public void giveCards(ArrayList<Card> givenCards) {
		cards.addAll(givenCards);
	}
	
	public ArrayList<Card> takeAllCards() {
		ArrayList<Card> temp = new ArrayList<Card>();
		temp.addAll(cards);
		cards.removeAll(cards);
		return temp;
	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}
	
	public void updateRound() {
		stats.add(new int[LUCK_FACTOR + 1]);
		int[] prevStats = stats.get(stats.size() - 2);
		setStats(TROOPS_DEPLOYED, prevStats[TROOPS_DEPLOYED]);
		setStats(TROOPS_KILLED, prevStats[TROOPS_KILLED]);
		setStats(TROOPS_LOST, prevStats[TROOPS_LOST]);
		setStats(TERRITORIES_CONQUERED, prevStats[TERRITORIES_CONQUERED]);
		setStats(TERRITORIES_LOST, prevStats[TERRITORIES_LOST]);
	}
	
	public void setStats(int type, int value) {
		stats.get(stats.size() - 1)[type] = value;
	}
	
	public void updateStats(int type, int change) {
		stats.get(stats.size() - 1)[type] += change;
	}
	
	public int getCurrentStats(int type) {
		return stats.get(stats.size() - 1)[type];
	}
	
	public int[] getStats(int type) {
		int[] temp = new int[stats.size() - 1];
		for (int i = 0; i < temp.length; i++)
			temp[i] = stats.get(i)[type];
		return temp;
	}
	
	public void updateDiceStats(int type) {
		diceStats[type]++;
	}
	
	public void updateLuckStats() {
		setStats(LUCK_FACTOR, getLuckiness(diceStats));
	}
	
	public static int getLuckiness(int[] diceStats) {
		int total3V2 = diceStats[W3V2] + diceStats[T3V2] + diceStats[L3V2];
		int total2V2 = diceStats[W2V2] + diceStats[T2V2] + diceStats[L2V2];
		int total3V1 = diceStats[W3V1] + diceStats[L3V1];
		int total2V1 = diceStats[W2V1] + diceStats[L2V1];
		int total1V1 = diceStats[W1V1] + diceStats[L1V1];
		int total1V2 = diceStats[W1V2] + diceStats[L1V2];
		int diff3V2 = (int)(2*(diceStats[W3V2] - RiskCalculator.W3V2*total3V2)
				- 2*(diceStats[L3V2] - (RiskCalculator.L3V2 / (1.0 - RiskCalculator.W3V2))*(total3V2-diceStats[W3V2])));
		int diff2V2 = (int)(2*(diceStats[W2V2] - RiskCalculator.W2V2*total2V2)
				- 2*(diceStats[L2V2] - (RiskCalculator.L2V2 / (1.0 - RiskCalculator.W2V2))*(total2V2-diceStats[W2V2])));
		int diff3V1 = (int)(diceStats[W3V1] - RiskCalculator.W3V1*total3V1);
		int diff2V1 = (int)(diceStats[W2V1] - RiskCalculator.W2V1*total2V1);
		int diff1V1 = (int)(diceStats[W1V1] - RiskCalculator.W1V1*total1V1);
		int diff1V2 = (int)(diceStats[W1V2] - RiskCalculator.W1V2*total1V2);
		return diff3V2 + diff2V2 + diff3V1 + diff2V1 + diff1V1 + diff1V2;
	}

	@Override
	public void onStateChange(Player activePlayer, State newState) {
		
	}
}
