package com.jeffsul.riskapp.dialogs;

import com.jeffsul.riskapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PlaySetDialogFragment extends DialogFragment {
	private static final String ARG_ITEMS = "option_items";
	private static final String ARG_TROOPS = "troops";
	
	public interface Listener {
		public void onPlaySet(int which, int value);
		public void onCancelSet();
	}
	
	private Listener listener;
	
	public static PlaySetDialogFragment newInstance(String[] items, int troops) {
		PlaySetDialogFragment f = new PlaySetDialogFragment();
		
		Bundle args = new Bundle();
		args.putStringArray(ARG_ITEMS, items);
		args.putInt(ARG_TROOPS, troops);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.play_set_dialog_title)
				.setMessage(getResources().getString(R.string.play_set_message, getArguments().getInt(ARG_TROOPS)))
				.setItems(getArguments().getStringArray(ARG_ITEMS), new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onPlaySet(which, getArguments().getInt(ARG_TROOPS));
					}
				})
				.setNegativeButton(R.string.play_set_negative, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onCancelSet();
					}
				});
		return builder.create();
	}
}
