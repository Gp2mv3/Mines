package com.gp2mv3.mines;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This Class manage the add preference, The user can disale ads by using shared preferences.
 **/
public class AdManager
{
	public static final String PREFS_NAME = "MinesAd";
	private boolean enabled;
	private Context ctx;

	/**
	 * Open the Shared preferences
	 **/
	public AdManager(Context ctx)
	{
		this.ctx = ctx;
		SharedPreferences settings = this.ctx.getSharedPreferences(PREFS_NAME, 0);
		enabled = settings.getBoolean("enabled", true);
	}

	/**
	 * Check if it's enabled.
	 * @return true if the ads are enabled
	 **/
	public boolean isEnabled()
	{
		return enabled;
	}

	public void disable() { toggle(false); }
	public void enable() { toggle(true); }

	/**
	 * Toggle the state of the ads
	 **/
	private void toggle(boolean newValue)
	{
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("enabled", newValue);
		editor.commit();

		enabled = newValue;
	}
}
