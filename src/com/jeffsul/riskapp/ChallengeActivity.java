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
import android.widget.Toast;

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
		String userName = ((EditText)findViewById(R.id.challenge_username)).getText().toString();
		
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				try {
					JSONObject obj = response.getJSONObject(0);
					String status = obj.getString("status");
					if (status.equals("failure")) {
						Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_user, 5);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					else {
						sendNotification(obj);
						int id = obj.getInt("id");
						updateTableRow(id);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		ChallengeFacade.createChallenge(cListener, userName);
	}
	
	public void sendNotification(JSONObject response) {
		String contentTitle = null;
		String contentText = null;
		try {
			String status = response.getString("status");
			String username = response.getString("username");
			
			System.out.println(status);
			
			if (status.equals("accepted")) {
				contentTitle = "Risk Challenge Accepted";
				contentText = username + " accepted your challenge. Tap to play!";
			}
			
			else {
				contentTitle = "Risk Challenge Declined";
				contentText = username + " declined your challenge. Tap to see the menu.";
			}
			
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle(contentTitle)
			        .setContentText(contentText);

			Intent resultIntent = new Intent(this, ChallengeActivity.class);
			// TODO: launch game here for success case

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
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void populateTable(JSONArray challenges) {
		TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);

		if (challenges == null) {
			System.out.println("Null");
		}
		
		for (int i = 0; i < challenges.length(); i++) {
			try {
				JSONObject challenge = challenges.getJSONObject(i);
				String username = challenge.getString("username");
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
	        	TextView statusText = new TextView(this);

		        if (status.equals("accepted")) { // if you've sent a challenge that was accepted
		        	statusText.setText(R.string.accept_challenge_button);
		        	statusText.setTextColor(Color.parseColor("#0000FF"));
		        	statusText.setOnClickListener(new TextView.OnClickListener() {
		        		@Override
		        		public void onClick(View v) {
		        			// TODO: enter game
		        		}
		        	});
		        }
		        else {
			        statusText.setText(status);
		        }
		        
		        row.addView(statusText);
		        tl.addView(row);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateTableRow(int challengeId) {
		
	}
	
	public void menuButtonClicked(View view) {
		finish();
	}
}
