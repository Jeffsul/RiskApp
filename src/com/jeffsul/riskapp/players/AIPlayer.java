package com.jeffsul.riskapp.players;

import android.app.Activity;
import android.app.AlertDialog;

import com.jeffsul.riskapp.GameActivity.State;
import com.jeffsul.riskapp.GameListener;
import com.jeffsul.riskapp.GameSettings;
import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.dialogs.AutoGameDialogFragment;
import com.jeffsul.riskapp.entities.Map;

/**
 * AIPlayer is the abstract superclass for all AI algorithm implementations.
 */
public abstract class AIPlayer extends Player {		
	protected int imgResId;
	
	protected GameListener listener;
	protected Map map;
	protected GameSettings gameSettings;
	
	public AIPlayer(int num, int color, GameListener listener, Map map, GameSettings gameSettings) {
		super(num, "Achilles", color);
		this.listener = listener;
		this.map = map;
		this.gameSettings = gameSettings;
	}

	@Override
	final public boolean isAI() {
		return true;
	}

	/**
	 * Communicate a pop-up message to the user.
	 * @param msg
	 * @param activity
	 * @param autoGame
	 */
	public void message(String msg, Activity activity, boolean autoGame) {
		if (autoGame) {
			AutoGameDialogFragment dialogFragment = AutoGameDialogFragment.newInstance(activity.getResources().getString(R.string.ai_player_message_title, name), msg);
			dialogFragment.show(activity.getFragmentManager(), "autogame");
		} else {
			AlertDialog messageDialog = new AlertDialog.Builder(activity).create();
			messageDialog.setTitle(activity.getResources().getString(R.string.ai_player_message_title, name));
			messageDialog.setMessage(msg);
			messageDialog.setIcon(imgResId);
			messageDialog.show();
		}
	}

	@Override
	public void onStateChange(Player activePlayer, State newState) {
		if (activePlayer != this) {
			return;
		}
		switch (newState) {
		case PLACE:
			place();
			break;
		case DEPLOY:
			deploy();
			break;
		case ATTACK:
			attack();
			break;
		case ADVANCE:
			break;
		case FORTIFY:
			fortify();
			break;
		}
	}

	/**
	 * AI implementation of placement logic.
	 */
	protected abstract void place();
	/**
	 * AI implementation of deployment logic.
	 */
	protected abstract void deploy();
	/**
	 * AI implementation of attack logic.
	 */
	protected abstract void attack();
	/**
	 * AI implementation of fortification logic.
	 */
	protected abstract void fortify();
}
