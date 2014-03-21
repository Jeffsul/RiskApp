package com.jeffsul.riskapp.ui;

import com.jeffsul.riskapp.GameActivity.State;
import com.jeffsul.riskapp.R;
import com.jeffsul.riskapp.StateListener;
import com.jeffsul.riskapp.players.Player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ActionButton extends Button implements StateListener {

	public ActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onStateChange(Player activePlayer, State newState) {
		switch (newState) {
		case PLACE:
			setEnabled(false);
			break;
		case DEPLOY:
			setEnabled(false);
			break;
		case ATTACK:
			setEnabled(true);
			setText(R.string.action_end_attacks);
			break;
		case ADVANCE:
			setEnabled(true);
			setText(R.string.action_advance_troops);
			break;
		case FORTIFY:
			setEnabled(true);
			setText(R.string.action_end_fortifications);
			break;
		}
	}

}
