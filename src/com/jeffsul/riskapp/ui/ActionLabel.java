package com.jeffsul.riskapp.ui;

import com.jeffsul.riskapp.GameActivity.State;
import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.StateListener;
import com.jeffsul.riskapp.players.Player;

import android.content.Context;
import android.widget.TextView;

public class ActionLabel extends TextView implements StateListener {

	public ActionLabel(Context context) {
		super(context);
	}

	@Override
	public void onStateChange(Player activePlayer, State newState) {
		switch (newState) {
		case PLACE:
		case DEPLOY:
			setText(getResources().getString(R.string.message_deploy_armies, activePlayer.name, activePlayer.getDeployCount()));
			break;
		case ATTACK:
			setText(R.string.message_attack);
			break;
		case ADVANCE:
			setText("Click to advance your armies.");
			break;
		case FORTIFY:
			setText(R.string.message_fortify);
			break;
		}
	}

}
