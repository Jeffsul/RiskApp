package com.jeffsul.riskapp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle; 
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ChallengeActivity extends Activity {
	
	public static final String RESPONSE_USER_NAME = "user_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);
		
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				populateTable(response);
			}
		};
		ChallengeFacade.getChallenges(cListener);
	}	

	public void createChallengeClicked(View view) {
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				sendNotification();
			}
		};
		ChallengeFacade.createChallenge(cListener);
	}
	
	public void sendNotification() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!");

		Intent resultIntent = new Intent(this, ChallengeActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		stackBuilder.addParentStack(ChallengeActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
		System.out.println("Notification delivered");
	}
	
	public void populateTable(JSONArray challenges) {
		TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);

		if (challenges == null) {
			System.out.println("Null");
		}
		
		for (int i = 0; i < challenges.length(); i++) {
			try {
				JSONObject challenge = challenges.getJSONObject(i);
				String username = challenge.getString("name");
				String status = challenge.getString("status");
				int id = challenge.getInt("id");
				
				TableRow row = new TableRow(this);
				TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
		        row.setLayoutParams(lp);
		        row.setGravity(Gravity.CENTER);
		        row.setId(id);
		        TextView userText = new TextView(this);
		        userText.setText(username);
		        row.addView(userText);
		        
		        if (status.equals("accepted") || status.equals("pending")) {
		        	Button statusButton = new Button(this);
		        	statusButton.setText(R.string.accept_challenge_button);
		        	statusButton.setWidth(300);
		        	statusButton.setOnClickListener(new Button.OnClickListener() {
		        		public void onClick(View v) {
		        			// TODO: enter game
		        		}
		        	});
		        	row.addView(statusButton);
		        }
		        
		        else {
		        	TextView statusText = new TextView(this);
			        statusText.setText(status);
			        row.addView(statusText);
		        }
		    
		        tl.addView(row);
			}
			catch(Exception e) {
				//
			}
		}
	}
	
	public void menuButtonClicked(View view) {
		finish();
	}
}
