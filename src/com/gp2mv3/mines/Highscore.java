package com.gp2mv3.mines;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class surrounds the scores
 * @author gp2mv3
 */
public class Highscore {

	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;
	private Context ctx;
	private int mGames;
	private int mTime;
	
	/**
	 * Initialize the sharedPreferences
	 * @param ctx the context of the app
	 */
	public Highscore(Context ctx)
	{
		this.ctx = ctx;
		mPrefs = ctx.getSharedPreferences("MinesScore", 0);
		mEditor = mPrefs.edit();
		
		mPrefs = ctx.getSharedPreferences("MinesScore", 0);
		mGames= mPrefs.getInt("games", 0);
		mTime = mPrefs.getInt("best", Integer.MAX_VALUE);
	}
	
	/**
	 * Get the number of games
	 * @return the games
	 */
	public int getGames()
	{
		if(mGames == 0)
		{
			mPrefs = ctx.getSharedPreferences("MinesScore", 0);
			mGames = mPrefs.getInt("games", 0);
		}
		
		return mGames;
	}
	
	/**
	 * Return the best score
	 * @return the score
	 */
	public int getBest()
	{
		if(mTime == 0)
		{
			mPrefs = ctx.getSharedPreferences("MinesScore", 0);
			mTime = mPrefs.getInt("best", Integer.MAX_VALUE);
		}
		return mTime;
	}
	
	/**
	 * Increments the played number
	 */
	public void addGames()
	{
		int current = getGames();
		mGames = current + 1;
	}
	
	/**
	 * Set the new Best Score
	 * @param time
	 */
	public void setBest(int time)
	{
		if(time <= getBest())
		{
			Log.v("best", getBest()+" "+time);
			mTime = time;
		}
	}
	
	/**
	 * Save the score
	 */
	public void save()
	{
		mPrefs = ctx.getSharedPreferences("MinesScore", 0);
		mEditor.putInt("best", mTime);
		mEditor.putInt("games", mGames);
	
		mEditor.commit();
	}
	
	public void finalize()
	{
		this.save();
	}
}
