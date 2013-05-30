package com.gp2mv3.mines;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * This Class extends Activity and provide a single Menu for the menuButton
 * @author gp2mv3
 */
public class WithMenuActivity extends Activity
{
	/**
	 * Create the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent;
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.btNewGame:
	    	intent = new Intent(this, GameActivity.class);
			startActivity(intent);

	        return true;
	    case R.id.btAbout:
	    	intent = new Intent(this, AboutActivity.class);
			startActivity(intent);

	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
