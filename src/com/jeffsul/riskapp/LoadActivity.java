package com.jeffsul.riskapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class LoadActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);		
		
		LoadGameView a = (LoadGameView) findViewById(R.id.view1);
		TextView tv = new TextView(this);
		tv.setText("hi");
		a.addView(tv, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
//		TableLayout table = (TableLayout) findViewById(R.id.savedGamesTable);
//		for (int x = 0; x < 5; x++) {
//			TextView tv = new TextView(this);
//			Button btn = new Button(this);
//			btn.setGravity(Gravity.RIGHT);
//			btn.setText("Hello");
//			
//			tv.setTextSize(26);
//			tv.setPadding(20 * table.getLeft(), 0, 0, 0);
//			tv.setText("This is row number=" + (x + 1));
//			TableRow tr = new TableRow(this);
//			tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			tr.addView(tv);
//			tr.addView(btn);
//			table.addView(tr, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		}
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }

	  public void onClicked(View view) {
//	    String text = view.getId() == R.id.view1 ? "Background" : "Foreground";
//	    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	  }

}
