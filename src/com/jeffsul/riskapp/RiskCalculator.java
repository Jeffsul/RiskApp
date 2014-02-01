package com.jeffsul.riskapp;

import java.text.NumberFormat;

public class RiskCalculator {	
	public static final double W1V1 = 15.0 / 36.0;
	public static final double L1V1 = 21.0 / 36.0;
	public static final double W2V1 = 125.0 / 216.0;
	public static final double L2V1 = 91.0 / 216.0;
	public static final double W3V1 = 855.0 / 1296.0;
	public static final double L3V1 = 441.0 / 1296.0;
	public static final double W1V2 = 55.0 / 216.0;
	public static final double L1V2 = 161.0 / 216.0;
	public static final double W2V2 = 295.0 / 1296.0;
	public static final double L2V2 = 581.0 / 1296.0;
	public static final double T2V2 = 420.0 / 1296.0;
	public static final double W3V2 = 2890.0 / 7776.0;
	public static final double L3V2 = 2275.0 / 7776.0;
	public static final double T3V2 = 2611.0 / 7776.0;
	
	private static NumberFormat format = NumberFormat.getPercentInstance(); {
		format.setMaximumFractionDigits(6);
		format.setMinimumFractionDigits(6);
	}
	
	public static String getResults(int attackers, int defenders) {
		double[][] outcome = new double[attackers + 1][defenders + 1];
		for (int i = 0; i < attackers + 1; i++) {
			for (int j = 0; j < defenders + 1; j++) {
				outcome[i][j] = -1.0;
			}
		}
		outcome[attackers][defenders] = 1.0;
		
		double winOdds = 0.0;
		String likelyOutcomeState = null;
		double likelyOutcome = -1.0;
		
		for (int i = attackers; i >= 2; i--) {
			winOdds += getOdds(i, 0, outcome, attackers, defenders);
			if (outcome[i][0] > likelyOutcome) {
				likelyOutcome = outcome[i][0];
				likelyOutcomeState = i + " vs " + 0;
			}
		}
		for (int i = 1; i <= defenders; i++) {
			if (outcome[1][i] > likelyOutcome) {
				likelyOutcome = outcome[1][i];
				likelyOutcomeState = 1 + " vs " + i;
			}
		}
		return "Victory odds: " + format.format(winOdds) + ", Likeliest outcome: " + likelyOutcomeState;
	}
	
	public static double getWinningOdds(int attackers, int defenders) {
		double[][] outcome = new double[attackers + 1][defenders + 1];
		for (int i = 0; i < attackers + 1; i++)
			for (int j = 0; j < defenders + 1; j++)
				outcome[i][j] = -1.0;
		outcome[attackers][defenders] = 1.0;
		
		double winOdds = 0.0;
		double likelyOutcome = -1.0;
		
		for (int i = attackers; i >= 2; i--)
		{
			winOdds += getOdds(i, 0, outcome, attackers, defenders);
			if (outcome[i][0] > likelyOutcome)
				likelyOutcome = outcome[i][0];
		}
		for (int i = 1; i <= defenders; i++)
		{
			if (outcome[1][i] > likelyOutcome)
				likelyOutcome = outcome[1][i];
		}
		return winOdds;
	}
	
	private static double getOdds(int a, int d, double[][] outcome, int attackers, int defenders) {
		if (outcome[a][d] != -1.0)
			return outcome[a][d];
		
		double odds = 0.0;
		if (a + 2 <= attackers && d >= 2) {
			if (a + 2 >= 4)
				odds += L3V2 * getOdds(a + 2, d, outcome, attackers, defenders);
			else if (a + 2 == 3)
				odds += L2V2 * getOdds(a + 2, d, outcome, attackers, defenders);
		}
		if (d + 2 <= defenders && a >= 3) {
			if (a >= 4)
				odds += W3V2 * getOdds(a, d + 2, outcome, attackers, defenders);
			else if (a == 3)
				odds += W2V2 * getOdds(a, d + 2, outcome, attackers, defenders);
		}
		if (a + 1 <= attackers && d + 1 <= defenders && d + 1 >= 2 && a + 1 >= 3) {
			if (a + 1 >= 4)
				odds += T3V2 * getOdds(a + 1, d + 1, outcome, attackers, defenders);
			else if (a + 1 == 3)
				odds += T2V2 * getOdds(a + 1, d + 1, outcome, attackers, defenders);
		}
		if (a + 1 <= attackers) {
			if (a + 1 == 2 && d >= 2)
				odds += L1V2 * getOdds(a + 1, d, outcome, attackers, defenders);
			else if (a + 1 >= 4 && d == 1)
				odds += L3V1 * getOdds(a + 1, d, outcome, attackers, defenders);
			else if (a + 1 == 3 && d == 1)
				odds += L2V1 * getOdds(a + 1, d, outcome, attackers, defenders);
			else if (a + 1 == 2 && d == 1)
				odds += L1V1 * getOdds(a + 1, d, outcome, attackers, defenders);
		}
		if (d + 1 <= defenders) {
			if (a == 2 && d + 1 >= 2)
				odds += W1V2 * getOdds(a, d + 1, outcome, attackers, defenders);
			else if (a >= 4 && d + 1 == 1)
				odds += W3V1 * getOdds(a, d + 1, outcome, attackers, defenders);
			else if (a == 3 && d + 1 == 1)
				odds += W2V1 * getOdds(a, d + 1, outcome, attackers, defenders);
			else if (a == 2 && d + 1 == 1)
				odds += W1V1 * getOdds(a, d + 1, outcome, attackers, defenders);
		}
		outcome[a][d] = odds;
		return odds;
	}
	
	public static void main(String[] args) {
		System.out.println(getResults(10, 3));
	}
}

