package com.jeffsul.riskapp;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jeffsul.riskapp.login.LoginActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle; 
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ChallengeActivity is a screen where challenges can be issued and reviewed.
 */
public class ChallengeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);
		
		getChallenges(null);
	}	
	
	public void getChallenges(View view) {
		
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				populateTable(response);
			}
		};
		ChallengeFacade.getChallenges(cListener);
	}

	public void createChallengeClicked(View view) {
		final TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
		final String userName = ((EditText)findViewById(R.id.challenge_username)).getText().toString();
		
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				if (response == null) { // pre-response
					int id = (int) Math.random();
					TableRow row = createTableRow(id);
			        drawTableRow(tl, row, userName, "pending");
				}
				else {
					try {
						JSONObject obj = response.getJSONObject(0);
						String status = obj.getString("status");
						if (status.equals("failure")) {
							Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_user, 5);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							View row = tl.findViewWithTag(userName);
							((TableLayout)row.getParent()).removeView(row);
						}
						else {
							sendNotification(obj);
							updateTableRowPending(obj);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		ChallengeFacade.createChallenge(cListener, userName);
	}
	
	private void sendNotification(JSONObject response) {
		String contentTitle = null;
		String contentText = null;
		try {
			String status = response.getString("status");
			String username = response.getString("username");
			int id = response.getInt("id");
			
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

			Intent resultIntent = new Intent(this, MainActivity.class);
			resultIntent.putExtra("networked", true);
			SharedPreferences prefs = getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
			String myUsername = prefs.getString(LoginActivity.SHARED_PREFS_KEY, null);
			resultIntent.putExtra("players", new String[] {myUsername, username});

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
			mNotificationManager.notify(id, mBuilder.build());
			System.out.println("Notification delivered");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void populateTable(JSONArray challenges) {
		TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
		tl.removeAllViews();
		
		if (challenges == null) {
			return;
		}
		
		for (int i = 0; i < challenges.length(); i++) {
			try {
				JSONObject challenge = challenges.getJSONObject(i);
				String username = challenge.getString("username");
				String status = challenge.getString("status");
				int id = challenge.getInt("id");
				
				TableRow row = createTableRow(id);
		        drawTableRow(tl, row, username, status);      
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private TableRow createTableRow(int id) {
		TableRow row = new TableRow(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        row.setGravity(Gravity.CENTER);
        row.setId(id);
        return row;
	}
	
	private void updateTableRowPending(JSONObject rowObj) {
		try {
			TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
			String username = rowObj.getString("username");
			String status = rowObj.getString("status");
			TableRow row = (TableRow) tl.findViewWithTag(username);
			row.setId(rowObj.getInt("id"));

			row.removeAllViews();
			drawTableRow(tl, row, username, status);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void updateTableRow(JSONObject rowObj) {
		try {
			TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
			String username = rowObj.getString("username");
			String status = rowObj.getString("status");
			int id = rowObj.getInt("id");
			TableRow row = (TableRow) findViewById(rowObj.getInt("id"));

			row.removeAllViews();
			drawTableRow(tl, row, username, status);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void drawTableRow(TableLayout tl, TableRow row, String username, String status){
		final Context context = this;
		
		if (status.equals("pending") && tl.findViewWithTag(username) != null){
			Toast toast = Toast.makeText(getApplicationContext(), "You've already challenged " + username + "!", 5);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		TextView userText = new TextView(this);
        userText.setText(username);
        row.addView(userText);
        
        if (status.equals("pending")) {
        	row.setTag(username);
        }
        
    	TextView statusText = new TextView(this);
    	
        if (status.equals("accepted") || status.equals("received")) { // if you've sent a challenge that was accepted or if you've received a challenge
        	
        	if (status.equals("accepted")) {
            	statusText.setText(R.string.accepted_challenge_button);
        	}
        	
        	else {
                statusText.setText(R.string.received_challenge_button);
        	}
        	final String user = username;
        	statusText.setTextColor(Color.parseColor("#0000FF"));
        	statusText.setOnClickListener(new TextView.OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			Intent intent = new Intent(ChallengeActivity.this, MainActivity.class);
        			intent.putExtra("networked", true);
        			SharedPreferences prefs = getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
        			String myUsername = prefs.getString(LoginActivity.SHARED_PREFS_KEY, null);
        			intent.putExtra("players", new String[] {myUsername, user});
        			startActivity(intent);
        		}
        	});
        }
        else {
	        statusText.setText(status);
        }
        
        row.addView(statusText);
        tl.addView(row);
	}
	
	public void menuButtonClicked(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
