package com.jeffsul.riskapp.dialogs;

import com.jeffsul.riskapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AutoGameDialogFragment extends DialogFragment {
	private static final String ARG_TITLE = "dialog_title";
	private static final String ARG_MESSAGE = "dialog_message";
	
	public interface Listener {
		public void onGameContinue();
		public void onGamePause();
		public void onGameSimulate(int rounds);
	}
	
	private Listener listener;
	
	public static AutoGameDialogFragment newInstance(String title, String message) {
		AutoGameDialogFragment f = new AutoGameDialogFragment();
		
		Bundle args = new Bundle();
		args.putString(ARG_TITLE, title);
		args.putString(ARG_MESSAGE, message);
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (Listener) activity;
		} catch (ClassCastException ex) {
			throw new ClassCastException(activity.toString() + " must implement Listener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getArguments().getString(ARG_TITLE))
				.setMessage(getArguments().getString(ARG_MESSAGE))
				.setItems(R.array.ai_simulate_options, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							listener.onGameContinue();
							break;
						case 1:
							listener.onGamePause();
							break;
						case 2:
							listener.onGameSimulate(1);
							break;
						case 3:
							listener.onGameSimulate(5);
							break;
						case 4:
							listener.onGameSimulate(10);
							break;
						default:
							listener.onGameSimulate(-1);
							break;
						}
					}
				});
		return builder.create();
	}
}
