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
	
	/**
	 * Overriding Activity.onCreate, run when ChallengeActivity launches
	 * @param savedInstanceState
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);
		
		getChallenges(null); // load the challenges asynchronously on screen load
	}	
	
	/**
	 * Method called in onClick of Challenge Menu button, calls ChallengeFacade interface to get list of user's challenges
	 * @param view
	 */

	public void getChallenges(View view) {

		// pass in implementation of interface, the onChallengeResponse method for the listener runs on its response
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				populateTable(response);
			}
		};
		ChallengeFacade.getChallenges(cListener);
	}

	/**
	 * Method called in onClick of Send Challenge button, calls ChallengeFacade interface to send a challenge request to the web server
	 * @param view
	 */

	public void createChallengeClicked(View view) {
		final TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
		final String userName = ((EditText)findViewById(R.id.challenge_username)).getText().toString();
		
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse(JSONArray response) {
				if (response == null) { // pre-response, create pending row in table
					int id = (int) Math.random();
					TableRow row = createTableRow(id);
					drawTableRow(tl, row, userName, "pending");
				}
				else { // when the server responds after the call
					try {
						JSONObject obj = response.getJSONObject(0);
						String status = obj.getString("status");
						if (status.equals("failure")) { // if the username does not exist, create a pop-up
							Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_user, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							View row = tl.findViewWithTag(userName);
							((TableLayout)row.getParent()).removeView(row);
						} else {
							sendNotification(obj);
							updateTableRowPending(obj);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		ChallengeFacade.createChallenge(cListener, userName);
	}
	
	/**
	 * Method to send Android notification to status bar of mobile device when a response is received from the server
	 * Called in the onChallengeResponse of the ChallengeFacade.Listener
	 * @param response
	 */
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
			} else {
				contentTitle = "Risk Challenge Declined";
				contentText = username + " declined your challenge. Tap to see the menu.";
			}

			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(contentTitle)
					.setContentText(contentText);

			// launch a new game when the notification is clicked
			Intent resultIntent = new Intent(this, MainActivity.class);
			resultIntent.putExtra("networked", true);
			SharedPreferences prefs = getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
			String myUsername = prefs.getString(LoginActivity.SHARED_PREFS_KEY, null);
			resultIntent.putExtra("players", new String[] {myUsername, username});

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(ChallengeActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIntent);

			NotificationManager notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationMgr.notify(id, builder.build());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Populates the Challenges TableView with rows of challenges (username and status columns)
	 * @param challenges
	 */

	private void populateTable(JSONArray challenges) {
		TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
		tl.removeAllViews();
		
		if (challenges == null) {
			return;
		}
		
		for (int i = 0; i < challenges.length(); i++) {
			try { // parse the JSON response object for username and status, attach a unique ID, create the row
				JSONObject challenge = challenges.getJSONObject(i);
				String username = challenge.getString("username");
				String status = challenge.getString("status");
				int id = challenge.getInt("id");
				
				TableRow row = createTableRow(id);
				drawTableRow(tl, row, username, status);      
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper method to create a TableRow given a Challenge's ID
	 * @param id
	 * @return the TableRow that was created in this method
	 */
	
	private TableRow createTableRow(int id) {
		TableRow row = new TableRow(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
		row.setLayoutParams(lp);
		row.setGravity(Gravity.CENTER);
		row.setId(id);
		return row;
	}

	/**
	 * Update the content of a TableRow containing an unknown Challenge ID - i.e. in a pending state - with new info
	 * @param rowObj
	 */
	
	private void updateTableRowPending(JSONObject rowObj) {
		try {
			TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
			String username = rowObj.getString("username");
			String status = rowObj.getString("status");
			TableRow row = (TableRow) tl.findViewWithTag(username);
			row.setId(rowObj.getInt("id"));
			row.removeAllViews();
			drawTableRow(tl, row, username, status);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Update the content of a TableRow containing a known Challenge ID with new info
	 * @param rowObj
	 */
	
	private void updateTableRow(JSONObject rowObj) {
		try {
			TableLayout tl = (TableLayout) findViewById(R.id.challenge_table);
			String username = rowObj.getString("username");
			String status = rowObj.getString("status");
			TableRow row = (TableRow) findViewById(rowObj.getInt("id"));
			row.removeAllViews();
			drawTableRow(tl, row, username, status);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper to draw a new TableRow in a given existing TableLayout
	 * @param tl
	 * @param row
	 * @param username
	 * @param status
	 */

	private void drawTableRow(TableLayout tl, TableRow row, String username, String status){		
		if (status.equals("pending") && tl.findViewWithTag(username) != null) { // if the tablerow being drawn represents a pending challenge
			Toast toast = Toast.makeText(getApplicationContext(), "You've already challenged " + username + "!", Toast.LENGTH_SHORT);
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
		if (status.equals("accepted") || status.equals("received")) { 
		// if you've sent a challenge that was accepted or if you've received a challenge, make the status clickable
			if (status.equals("accepted")) {
				statusText.setText(R.string.accepted_challenge_button);
			} else {
				statusText.setText(R.string.received_challenge_button);
			}
			final String user = username;
			statusText.setTextColor(Color.parseColor("#0000FF"));
			statusText.setOnClickListener(new TextView.OnClickListener() {
				@Override 
				public void onClick(View v) { // launch a new game when the text is clicked
					Intent intent = new Intent(ChallengeActivity.this, MainActivity.class);
					intent.putExtra("networked", true);
					SharedPreferences prefs = getSharedPreferences("com.jeffsul.riskapp", Context.MODE_PRIVATE);
					String myUsername = prefs.getString(LoginActivity.SHARED_PREFS_KEY, null);
					intent.putExtra("players", new String[] {myUsername, user});
					startActivity(intent);
				}
			});
		} else { // if your challenge has been declined or is pending
			statusText.setText(status);
		}

		row.addView(statusText);
		tl.addView(row);
	}
	
	/**
	 * Called in the onClick of the Back to Menu button; finish the ChallengeActivity and go back to other screen
	 * @param view
	 */

	public void menuButtonClicked(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
