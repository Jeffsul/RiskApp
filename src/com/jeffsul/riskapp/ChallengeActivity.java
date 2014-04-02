package com.jeffsul.riskapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle; 
import android.view.View;
import android.widget.EditText;

public class ChallengeActivity extends Activity {
	
	public static final String RESPONSE_USER_NAME = "user_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);
	}	
	
	public void startActivity(View view) {
		ChallengeFacade.Listener cListener = new ChallengeFacade.Listener() {
			public void onChallengeResponse() {
			}
		};
		finishActivity();
		ChallengeFacade.createChallenge(cListener);
	}
	
	public void finishActivity() {
		Intent result = new Intent();
		String userName = ((EditText)findViewById(R.id.challenge_user_name)).getText().toString();
		result.putExtra(RESPONSE_USER_NAME, userName);
		setResult(Activity.RESULT_OK, result);
		finish();
	}
}
