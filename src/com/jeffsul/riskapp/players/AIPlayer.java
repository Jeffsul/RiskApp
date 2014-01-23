package com.jeffsul.riskapp.players;

import java.util.ArrayList;

import com.jeffsul.riskapp.RiskCalculator;
import com.jeffsul.riskapp.entities.Continent;
import com.jeffsul.riskapp.entities.Map;
import com.jeffsul.riskapp.entities.Territory;

public class AIPlayer extends Player
{	
	public static final int QUOTE_ELIMINATED = 0;
	
	private static final double THREAT_LIMIT = 2.0;
	
	private static final String[] OPTIONS = {"Continue", "Pause", "Simulate 1 Round", "Simulate 5 Rounds", "Simulate 10 Rounds", "Simulate Game"};
	
	protected ImageIcon img;
	
	private ArrayList<Territory> attackers;
	private ArrayList<Territory> conquests;
	private ArrayList<Continent> continents;
	private ArrayList<Territory> borders;
	private Continent optimalCont;
	
	protected RiskGame game;
	
	public AIPlayer(int num, Color color, RiskGame rg) 
	{
		super(num, "Achilles", color);
		game = rg;
		isAI = true;
		img = new ImageIcon(getClass().getResource("ai/" + name + ".jpg"));
	}
	
	public void message(String msg)
	{
		if (game.simulate)
			return;
		if (game.autoGame)
		{
			String ans = (String) JOptionPane.showInputDialog(game.actionBtn, msg, name + " says...", JOptionPane.INFORMATION_MESSAGE, img, OPTIONS, OPTIONS[0]);
			if (ans != null)
			{
				if (ans.equals(OPTIONS[1]))
					game.pauseGame();
				else if (ans.equals(OPTIONS[2]))
					game.simulate(1);
				else if (ans.equals(OPTIONS[3]))
					game.simulate(5);
				else if (ans.equals(OPTIONS[4]))
					game.simulate(10);
				else if (ans.equals(OPTIONS[5]))
					game.simulate(-1);
			}
		}
		else
			JOptionPane.showMessageDialog(game.actionBtn, msg, name + " says...", JOptionPane.INFORMATION_MESSAGE, img);
	}
	
	public void message(String msg, Component comp)
	{
		JOptionPane.showMessageDialog(comp, msg, name + " has been eliminated...", JOptionPane.INFORMATION_MESSAGE, img);
	}
	
	public void attack(Territory from, Territory to, boolean all)
	{
		message("Attacking from " + from.name + " to " + to.name + ".");
		game.attack(from, to, true);
		if (all)
		{
			while (game.state != RiskGame.State.ADVANCE && from.units > 1)
				game.attack(to, true);
		}
	}
	
	public void place()
	{
		Territory deployTerrit = null;
		Continent[] conts = game.getMap().getContinents();
		Continent targetCont = null;
		int max = Integer.MIN_VALUE;
		for (Continent cont : conts)
		{
			if (cont.getBonus() > 0)
			{
				int score = cont.getBonus() * (2 * cont.getFriendlyTerritories(this).length - cont.getSize());
				if (score > max)
				{
					max = score;
					targetCont = cont;
				}
			}
		}
		
		if (max >= 0)
		{
			Territory[] territs = targetCont.getFriendlyTerritories(this);
			max = Integer.MIN_VALUE;
			for (Territory t : territs)
			{
				int score = t.getEnemyConnectors(this).length;
				if (score > max)
				{
					max = score;
					deployTerrit = t;
				}
			}
		}
		else
		{
			Territory[] territs = game.getMap().getTerritories(this);
			max = Integer.MIN_VALUE;
			for (Territory t : territs)
			{
				int n = t.getFriendlyConnectors(this).length;
				int score = (t.getConnectors().length - n) * n;
				if (score > max)
				{
					max = score;
					deployTerrit = t;
				}
			}
		}
		game.place(deployTerrit);
	}
	
	public void deploy()
	{
		Map map = game.getMap();
		RiskCalculator riskCalc = game.riskCalc;
		
		conquests = new ArrayList<Territory>();
		attackers = new ArrayList<Territory>();
		Continent[] conts = map.getContinents();
		Territory[] myTerrits = map.getTerritories(this);
		
		borders = new ArrayList<Territory>();
		for (Continent cont : conts)
		{
			if (cont.hasContinent(this))
			{
				ArrayList<Territory> temp = fortifyBorders(cont, false);
				if (temp != null)
					borders.addAll(temp);
			}
		}
		
		double maxScore = Double.NEGATIVE_INFINITY;
		optimalCont = null;
		for (Continent cont : conts)
		{
			if (cont.getBonus() == 0 || cont.hasContinent(this))
				continue;
			
			double score = 0;
			for (Territory territ : myTerrits)
			{
				Territory[] cons = territ.getConnectors();
				for (int k = 0; k < cons.length; k++)
				{
					if (cont.hasTerritory(cons[k]))
					{
						score += territ.units;
						break;
					}
				}
			}
			
			if (score != 0)
			{
				Territory[] territs = cont.getTerritories();
				for (Territory territ : territs)
				{
					if (territ.owner != this)
						score -= territ.units;
				}
				
				if (score > 0)
					score /= cont.getSize();
				else
					score *= cont.getSize();
				
				if (score > maxScore)
				{
					maxScore = score;
					optimalCont = cont;
				}
			}
		}
		
		if (optimalCont == null)
			optimalCont = map.getContinent("Antarctica");
		
		int maxPlayerScore = Integer.MIN_VALUE;
		Player threat = null;
		for (Player player : game.players)
		{
			if (player != this)
			{
				int playerScore = map.getTroopCount(player) + player.bonus * 3;
				if (playerScore > maxPlayerScore)
				{
					maxPlayerScore = playerScore;
					threat = player;
				}
			}
		}
		
		if (maxPlayerScore / (double)(map.getTroopCount(this) + bonus * 3) > THREAT_LIMIT)
		{
			for (Territory myTerrit : myTerrits)
			{
				Territory[] enemyCons = myTerrit.getEnemyConnectors(this);
				for (Territory enemyCon : enemyCons)
				{
					if (enemyCon.owner == threat)
					{
						Continent cont = map.getContinent(enemyCon);
						if (!conquests.contains(enemyCon) && cont.hasContinent(threat))
						{
							if (riskCalc.getWinningOdds(myTerrit.units + game.deployNum, enemyCon.units) > Math.random() / 7 + 0.5)
							{
								while (game.deployNum > 0 && riskCalc.getWinningOdds(myTerrit.units, enemyCon.units) < 0.75)
									game.deploy(myTerrit, false);
								attackers.add(myTerrit);
								conquests.add(enemyCon);
							}
						}
					}
				}
			}
		}
		
		continents = new ArrayList<Continent>();
		while (true)
		{
			Territory[] optimalContTerrits = optimalCont.getTerritories();
			int min = Integer.MAX_VALUE;
			Territory target = null;
			Territory attacker = null;
			
			for (Territory optimalContTerrit : optimalContTerrits)
			{
				if (optimalContTerrit.owner != this && !conquests.contains(optimalContTerrit))
				{
					Territory[] friendlyCons = optimalContTerrit.getFriendlyConnectors(this);
					if (friendlyCons.length > 0)
					{
						Territory maxInvader = null;
						for (Territory friendlyCon : friendlyCons)
						{
							int units = friendlyCon.units;
							if (attackers.contains(attacker))
								units = attacker.units - conquests.get(attackers.indexOf(attacker)).units;
							if (maxInvader == null || units > maxInvader.units)
								maxInvader = friendlyCon;
						}
	
						if (target == null || (optimalContTerrit.units >= target.units && (optimalContTerrit.units == target.units && optimalContTerrit.units - maxInvader.units < min)))
						{
							min = optimalContTerrit.units - maxInvader.units;
							target = optimalContTerrit;
							attacker = maxInvader;
						}
					}
				}
			}
			
			if (attacker == null)
			{
				fortifyBorders(optimalCont, false);
				for (Territory t : myTerrits)
				{
					Territory[] cons = t.getConnectors();
					for (Territory con : cons)
					{
						if (con.owner == this)
							continue;
						Continent c = map.getContinent(con);
						if (c.hasContinent(con.owner) && !conquests.contains(con))
						{
							if (game.riskCalc.getWinningOdds(t.units + game.deployNum, con.units) > Math.random() / 5 + 0.75)
							{
								while (game.deployNum > 0 && game.riskCalc.getWinningOdds(t.units, con.units) < 0.8)
									game.deploy(t, false);
								attackers.add(t);
								conquests.add(con);
							}
						}
					}
				}
				
				for (int j = 0; j < 2 && game.deployNum > 0; j++)
				{
					for (Territory att : attackers)
					{
						if (att.owner == this)
							game.deploy(att, false);
					}
				}
				continents.add(optimalCont);
				optimalCont = getOptimalContinent(continents);
				
				if (optimalCont == null)
				{
					while (game.deployNum > 0)
					{
						for (Territory att : attackers)
						{
							if (att.owner == this)
								game.deploy(att, false);
						}
					}
					break;
				}
				continue;
			}
			
			int units = attacker.units;
			for (int i = 0; i < attackers.size(); i++)
				if (attackers.get(i) == attacker)
					units -= conquests.get(i).units;
			if (borders.contains(attacker))
				units -= 4;
			Territory[] conns = target.getEnemyConnectors(this);
			conquests.add(target);
			attackers.add(attacker);
			for (Territory conn : conns)
			{
				if (!optimalCont.hasTerritory(conn) || conn.getFriendlyConnectors(this).length != 0)
					continue;
				units -= conn.units;
				conquests.add(conn);
				attackers.add(target);
			}
			while (units - target.units < 4 && game.deployNum > 0)
			{
				game.deploy(attacker, false);
				units++;
			}
		}
	}
	
	public void attack()
	{
		Map map = game.getMap();
		ArrayList<Territory> noAttack = new ArrayList<Territory>();
		Territory att, con;
		while (conquests.size() > 0)
		{
			att = attackers.remove(0);
			con = conquests.remove(0);
			if (con.owner == this || att.owner != this)
				continue;
			Continent contin = map.getContinent(con);
			if (continents.indexOf(contin) >= 0 && continents.indexOf(map.getContinent(att)) > -1)
			{
				if (continents.indexOf(map.getContinent(att)) < continents.indexOf(contin) && !map.getContinent(att).hasContinent(this))
					continue;
			}
			double ods = game.riskCalc.getWinningOdds(att.units, con.units);
			if (ods < 0.5 + Math.random() / 8)
				continue;
			int maxEnemy = 0; int maxEnemy2 = 0; int maxOtherEnemy = 0;
			if (borders.contains(att))
			{
				Territory[] cons = att.getEnemyConnectors(this);
				for (int i = 0; i < cons.length; i++)
				{
					if (cons[i].units > maxEnemy && cons[i] != con)
						maxEnemy = cons[i].units;
					if (cons[i].units > maxOtherEnemy && cons[i].owner != con.owner)
						maxOtherEnemy = cons[i].units;
				}
				cons = con.getEnemyConnectors(this);
				for (Territory conn : cons)
				{
					if (conn.units > maxEnemy2)
						maxEnemy2 = conn.units;
				}
				maxEnemy2 = Math.max(maxEnemy, maxEnemy2);
				if (maxOtherEnemy > 0)
				{
					if (att.units >= maxOtherEnemy + 3)
						message("Attacking from " + att.name + " to " + con.name);
					else
						noAttack.add(att);
					while (att.units >= maxOtherEnemy + 3 && game.state != RiskGame.State.ADVANCE)
					{
						game.attack(att, con, false);
					}
				}
				else
					attack(att, con, false);
			}
			else
			{
				if (att.owner == this)
					attack(att, con, false);
			}
			borders = new ArrayList<Territory>();
			Continent[] conts = map.getContinents();
			for (Continent c : conts)
			{
				if (c.hasContinent(this))
				{
					ArrayList<Territory> o = fortifyBorders(c, false);
					if (o != null)
						borders.addAll(o);
				}
			}
			Territory[] conns = att.getEnemyConnectors(this);
			for (int i = 0; i < conns.length; i++)
			{
				if (conns[i].units > maxEnemy && conns[i] != con)
					maxEnemy = conns[i].units;
			}
			if (game.state == RiskGame.State.ADVANCE)
			{
				Territory[] cons = con.getConnectors();
				double maxOdds = 0.0;
				Territory next = null;
				Continent newCont = getOptimalContinent(continents);
				for (int i = 0; i < cons.length; i++)
				{
					if (cons[i].owner == this)
						continue;
					if (map.getContinent(cons[i]) != newCont && !continents.contains(map.getContinent(cons[i])) && !map.getContinent(cons[i]).hasContinent(cons[i].owner))
						continue;
					double odds = game.riskCalc.getWinningOdds(att.units, cons[i].units);
					if (odds > maxOdds)
					{
						maxOdds = odds;
						next = cons[i];
					}
				}
				if (attackers.contains(att) && att.getEnemyConnectors(this).length != 0)
				{
					if (next != null && maxOdds >= Math.random() / 4 + 0.6)
					{
						int enemies = 0;
						for (int i = 0; i < attackers.size(); i++)
							if (attackers.get(i) == att)
								enemies += conquests.get(i).units;
						while (game.riskCalc.getWinningOdds(att.units, enemies) > 0.85 && game.riskCalc.getWinningOdds(con.units, next.units) < 0.8)
							game.advance(con, false);
						if (con.units > 1)
						{
							attackers.add(con);
							conquests.add(next);
						}
						game.endAdvance();
					}
					else
						game.endAdvance();
				}
				else
				{
					if ((next != null && maxOdds >= Math.random() / 4 + 0.6) || attackers.contains(con))
					{
						if (!attackers.contains(con))
						{
							attackers.add(0, con);
							conquests.add(0, next);
						}
						if (borders.contains(att))
						{
							if (maxEnemy == 0)
								game.advance(con, true);
							else
							{
								while (att.units >= maxEnemy + 4)
								{
									game.advance(con, false);
								}
								game.endAdvance();
							}
						}
						else
							game.advance(con, true);
					}
					else
					{
						int moveCount = con.getConnectors().length - con.getFriendlyConnectors(this).length;
						int stayCount = att.getConnectors().length - att.getFriendlyConnectors(this).length;
						int num = (int)Math.ceil((double)(att.units - 1)*((double)moveCount / (double)(moveCount+stayCount)));
						if (borders.contains(att))
						{
							if (maxEnemy == 0)
								game.advance(con, true);
							else
							{
								while (att.units >= maxEnemy + 4)
									game.advance(con, false);
								game.endAdvance();
							}
						}
						else if (moveCount > 0 && continents.indexOf(map.getContinent(con)) > -1 && (continents.indexOf(map.getContinent(con)) < continents.indexOf(map.getContinent(att)) || continents.indexOf(map.getContinent(att)) == -1))
							game.advance(con, true);
						else
						{
							for (int j = 0; j < num; j++)
								game.advance(con, false);
							game.endAdvance();
						}
					}
				}
			}
			else
			{
				Territory[] cons = con.getFriendlyConnectors(this);
				Territory t = null;
				for (int i = 0; i < cons.length; i++)
				{
					if (cons[i] == att || noAttack.contains(cons[i]))
						continue;
					if (t == null || cons[i].units > t.units)
						t = cons[i];
				}
				if (t != null)
				{
					attackers.add(t);
					conquests.add(con);
				}
			}
		}
		if (!game.conqueredTerritory && game.cardType != RiskGame.NONE)
		{
			double maxScore = 0.0;
			Territory from = null;
			Territory to = null;
			Territory[] myTerrits = map.getTerritories(this);
			for (int i = 0; i < myTerrits.length; i++)
			{
				Territory[] cons = myTerrits[i].getConnectors();
				for (int j = 0; j < cons.length; j++)
				{
					if (cons[j].owner == this)
						continue;
					double odds = game.riskCalc.getWinningOdds(myTerrits[i].units, cons[j].units);
					if (odds > maxScore)
					{
						maxScore = odds;
						from = myTerrits[i];
						to = cons[j];
					}
				}
			}
			attack(from, to, false);
			if (game.state == RiskGame.State.ADVANCE)
				game.advance(from, true);
		}
		game.endAttacks();
	}
	
	public void fortify()
	{
		if (game.state != RiskGame.State.FORTIFY)
			return;
		Map map = game.getMap();
		Continent[] conts = map.getContinents();
		borders = new ArrayList<Territory>();
		for (int i = 0; i < conts.length; i++)
		{
			if (conts[i].hasContinent(this))
			{
				ArrayList<Territory> o = fortifyBorders(conts[i], false);
				if (o != null)
					borders.addAll(o);
			}
		}
		if (borders.size() == 0)
		{
			if (optimalCont != null)
				borders.addAll(fortifyBorders(optimalCont, true));
			else
				borders.addAll(fortifyBorders(continents.get(0), true));
		}
		if (fortify(borders))
			return;
		if (game.state == RiskGame.State.FORTIFY)
		{
			for (Continent c : continents)
			{
				if (!c.hasContinent(this))
				{
					ArrayList<Territory> o = fortifyBorders(c, true);
					if (o != null)
						borders.addAll(o);
				}
			}
			if (fortify(borders))
				return;
		}
		if (game.state == RiskGame.State.FORTIFY)
		{
			int minVal = Integer.MAX_VALUE;
			Territory toFortify = null;
			for (int j = 0; j < borders.size(); j++)
			{
				
				Territory[] cons = borders.get(j).getConnectors();
				for (int i = 0; i < cons.length; i++)
				{
					if (cons[i].owner == this)
						continue;
					int val = borders.get(j).units - cons[i].units;
					if (val < minVal)
					{
						minVal = val;
						toFortify = borders.get(j);
					}
				}
			}
			Territory from = null;
			int maxDiff = Integer.MIN_VALUE;
			for (Territory t : borders)
			{
				if (!t.isFortifyConnecting(toFortify))
					continue;
				Territory[] conns = t.getEnemyConnectors(this);
				int maxEnemy = 0;
				for (Territory conn : conns)
				{
					maxEnemy = Math.max(conn.units, maxEnemy);
				}
				if (t.units - maxEnemy > maxDiff)
				{
					maxDiff = t.units - maxEnemy;
					from = t;
				}
			}
			if (from != null && toFortify != null)
			{
				message("Fortifying from " + from.name + " to " + toFortify.name);
				while (maxDiff > 1 && minVal < 4)
				{
					minVal++;
					maxDiff--;
					game.fortify(from, toFortify, false);
				}
			}
		}
		game.endFortifications();
	}
	
	private boolean fortify(ArrayList<Territory> borders)
	{
		Map map = game.getMap();
		Territory[] terrs = map.getTerritories(this);
		Territory t = null;
		int maxTroops = 0;
		int minVal = Integer.MAX_VALUE;
		Territory toFortify = null;
		for (int j = 0; j < borders.size(); j++)
		{
			Territory[] cons = borders.get(j).getConnectors();
			for (int i = 0; i < cons.length; i++)
			{
				if (cons[i].owner == this)
					continue;
				int val = borders.get(j).units - cons[i].units;
				if (val < minVal)
				{
					minVal = val;
					toFortify = borders.get(j);
				}
			}
		}
		if (toFortify != null)
		{
			for (Territory terr : terrs)
			{
				if (!terr.isFortifyConnecting(toFortify))
					continue;
				if (terr.getConnectors().length == terr.getFriendlyConnectors(this).length)
				{
					if (terr.units >= maxTroops && !borders.contains(terr))
					{
						maxTroops = terr.units;
						t = terr;
					}
				}
				else if (!map.getContinent(terr).hasContinent(this))
				{
					if (terr.units > maxTroops && !borders.contains(terr))
					{
						for (int j = 0; j < borders.size(); j++)
						{
							if (terr.isFortifyConnecting(borders.get(j)))
							{
								maxTroops = terr.units;
								t = terr;
								break;
							}
						}
					}
				}
			}
			if (t != null && t.units > 1)
			{
				message("Fortifying from " + t.name + " to " + toFortify.name);
				game.fortify(t, toFortify, true);
				message("Exiting fortify(borders) - true");
				return true;
			}
		}
		return false;
	}
	
	private ArrayList<Territory> fortifyBorders(Continent cont, boolean a)
	{	
		ArrayList<Territory> output = new ArrayList<Territory>();
		if (a && !cont.hasContinent(this))
		{
			Territory[] friendlyTerrits = cont.getFriendlyTerritories(this);
			main: for (int i = 0; i < friendlyTerrits.length; i++)
			{
				Territory[] enemyConnectors = friendlyTerrits[i].getEnemyConnectors(this);
				for (int j = 0; j < enemyConnectors.length; j++)
				{
					if (cont.hasTerritory(enemyConnectors[j]))
					{
						for (int k = 0; k < friendlyTerrits.length; k++)
						{
							if (k == i)
								continue;
							if (i > k && compareConnectors(friendlyTerrits[i], friendlyTerrits[k], cont) == 0)
								continue main;
						}
						output.add(friendlyTerrits[i]);
						continue main;
					}
				}
			}
		}
		else
		{
			Territory[] borders = cont.getBorders();
			for (int i = 0; i < borders.length; i++)
			{
				if (borders[i].owner == this)
				{
					ArrayList<Territory> checked = new ArrayList<Territory>();
					ArrayList<Territory> territs = new ArrayList<Territory>();
					territs.add(borders[i]);
					main: while (territs.size() > 0)
					{
						Territory t = territs.remove(0);
						checked.add(t);
						Territory[] cons = t.getConnectors();
						for (int j = 0; j < cons.length; j++)
						{
							if (cons[j].owner != this)
							{
								output.add(t);
								continue main;
							}
						}
						for (int j = 0; j < cons.length; j++)
						{
							if (cont.hasTerritory(cons[j]) || checked.contains(cons[j]) || territs.contains(cons[j]))
								continue;
							territs.add(cons[j]);
						}
					}
				}
			}
		}
		
		ArrayList<Territory> after = new ArrayList<Territory>();
		if (game.deployNum > 0)
		{
			ArrayList<Territory> others;
			ArrayList<Territory> avoid = new ArrayList<Territory>();
			for (int i = 0; i < output.size(); i++)
			{
				if (output.get(i).getEnemyConnectors(this).length == 1 && !avoid.contains(output.get(i)))
				{
					others = new ArrayList<Territory>();
					Territory t = output.get(i).getEnemyConnectors(this)[0];
					others.add(output.get(i));
					for (int j = 0; j < output.size(); j++)
					{
						if (j == i)
							continue;
						for (Territory uncon : output.get(j).getEnemyConnectors(this))
							if (uncon == t)
								others.add(output.get(j));
					}
					int enemy = t.units;
					Territory[] terrs = t.getEnemyConnectors(this);
					for (Territory terr : terrs)
					{
						if (terr.units > enemy)
							enemy = terr.units;
					}
					int max = 0;
					Territory target = null;
					for (int j = 0; j < others.size(); j++)
					{
						if (others.get(j).units > max)
						{
							max = others.get(j).units;
							target = others.get(j);
						}
					}
					if (others.size() > 1)
					{
						while (max - enemy < 4)
						{
							game.deploy(target, false);
							max++;
						}
						if (!conquests.contains(t) && target.units - t.units >= enemy)
						{
							conquests.add(t);
							attackers.add(target);
						}
						avoid.addAll(others);
					}
					else
						after.add(others.get(0));
				}
			}
			output.removeAll(avoid);
		}
		
		while (game.deployNum > 0)
		{
			int max = -4;
			Territory t = null;
			for (int i = 0; i < output.size(); i++)
			{
				Territory[] cons = output.get(i).getConnectors();
				int maxEnemy = 0;
				for (int j = 0; j < cons.length; j++)
				{
					if (cons[j].owner == this)
						continue;
					maxEnemy = Math.max(maxEnemy, cons[j].units);
				}
				if (maxEnemy - output.get(i).units > max)
				{
					max = maxEnemy - output.get(i).units;
					t = output.get(i);
				}
			}
			if (t != null)
				game.deploy(t, false);
			else
				break;
		}
		
		for (Territory territ : after)
		{
			Territory enemyTerr = territ.getEnemyConnectors(this)[0];
			int enemy = enemyTerr.units;
			Territory[] terrs = enemyTerr.getEnemyConnectors(this);
			for (Territory terr : terrs)
			{
				if (terr.units > enemy)
				{
					enemy = terr.units;
				}
			}
			int max = territ.units;
			while (max - enemy < 4)
			{
				game.deploy(territ, false);
				max++;
			}
			if (!conquests.contains(territ) && territ.units - territ.units >= enemy)
			{
				conquests.add(enemyTerr);
				attackers.add(territ);
			}
		}
		
		return output;
	}
	
	private int compareConnectors(Territory a, Territory b, Continent c)
	{
		Territory[] tA = a.getEnemyConnectors(this);
		Territory[] tB = b.getEnemyConnectors(this);
		int count = tA.length;
		main: for (Territory t : tA)
		{
			for (Territory tt : tB)
			{
				if (t == tt || !c.hasTerritory(t))
				{
					count--;
					continue main;
				}
			}
		}
		return count;
	}
	
	private Continent getOptimalContinent(ArrayList<Continent> continents)
	{
		Map map = game.getMap();
		Continent[] conts = map.getContinents();
		Territory[] terrs = map.getTerritories(this);
		double max = Integer.MIN_VALUE;
		Continent cont = null;
		for (Continent c : conts)
		{
			if (c.hasContinent(this) || continents.contains(c) || c.getBonus() == 0)
				continue;
			Territory[] territs = c.getTerritories();
			int count = 0;
			for (int j = 0; j < territs.length; j++)
			{
				if (territs[j].owner != this)
					count += territs[j].units;
			}
			int support = 0;
			for (int j = 0; j < terrs.length; j++)
			{
				Territory[] cons = terrs[j].getConnectors();
				for (int k = 0; k < cons.length; k++)
				{
					if (c.hasTerritory(cons[k]))
					{
						support += terrs[j].units;
						break;
					}
				}
			}
			if (support == 0)
				continue;
			double score = (double)(support - count) / (double)c.getSize();
			if (score > max)
			{
				max = score;
				cont = c;
			}
		}
		if (max < 0)
			return null;
		return cont;
	}
}
