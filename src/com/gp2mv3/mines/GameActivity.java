package com.gp2mv3.mines;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.ads.AdView;

/**
 * The main activity (the game board)
 * @author gp2mv3
 */
public class GameActivity extends WithMenuActivity
{
	private TableLayout table;
	private Context ctx;
	private TableRow[] rows;
	private ImageView[][] img;
	//private TextView chronoTxt;
	private TextView gamesTxt;
	private TextView scoreTxt;
	private Resources res;
	private AlertDialog.Builder alert;
	
	private boolean shown[][];
	private boolean bombs[][];
	private boolean flags[][];
	private int values[][];
	
	private boolean paused = false;
	private int mines = Const.MINES;
	private Highscore hs;
	private Chronometer chrono;
	private AdManager adManager;
	private AdView adView;
	private TextView chronoTxt;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx = getApplicationContext();
        res = getResources();
        newGame();

        
        new Thread(new Runnable() {
        	public void run() {
        		chronoTxt.post(new Runnable() {
        			public void run() {
        				while(true)
        				{
        					long sec = (int)(chrono.getTime()/1000);
        					long min = (int)(sec/60);
        					chronoTxt.setText(min+":"+(sec%60));
        					try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
        				}
        			}
        		});
        	}
        }).start();
        
        
        //adManager = new AdManager(this);
        //if(adManager.isEnabled())
        //	displayAd();
    }

/**
    private void displayAd()
    {
	    adView = new AdView(this, AdSize.BANNER, "CHANGE IT"); //TODO change this !!!!
	    
	    RelativeLayout l = (RelativeLayout) findViewById(R.id.relative);
	    l.addView(adView);
	    
	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)adView.getLayoutParams();
	    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    adView.setLayoutParams(params);
	    
	    AdRequest adRequest = new AdRequest();
	    //adRequest.addTestDevice("");
	    adView.loadAd(adRequest);
	}
**/
	/**
     * Start a new game:
     * Initialize the board
     */
    private void newGame()
    {
    	chrono = new Chronometer();
    	mines = Const.MINES;
    	
        ctx = getApplicationContext();
        hs = new Highscore(ctx);
        table = (TableLayout) findViewById(R.id.table);
        Button btBreak = (Button) findViewById(R.id.btBreak);
        btBreak.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		breakDiag();
        	}
        });
        
        drawMap();
        randCases();
        
        chronoTxt = (TextView) findViewById(R.id.chrono);
        chrono.start();
        
        hs.addGames();
        
        gamesTxt = (TextView) findViewById(R.id.played);
        gamesTxt.setText(String.format(res.getString(R.string.playedTxt), hs.getGames()));
        
        if(hs.getBest() != Integer.MAX_VALUE)
        {
        	scoreTxt = (TextView) findViewById(R.id.score);
        	scoreTxt.setText(String.format(res.getString(R.string.scoreTxt), hs.getBest()));
        }
    }

    /**
     * Draw the board on the screen
     */
    private void drawMap()
    {
    	//Get the sizes of the screen
    	Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
    	int width = (int) Math.floor(display.getWidth()/Const.SIZE);  

    	TableRow.LayoutParams layoutParams  = new TableRow.LayoutParams(width, width);
    	
    	rows = new TableRow[Const.SIZE];
    	img = new ImageView[Const.SIZE][Const.SIZE];
    	shown = new boolean[Const.SIZE][Const.SIZE];
    	bombs = new boolean[Const.SIZE][Const.SIZE];
    	flags = new boolean[Const.SIZE][Const.SIZE];
    	values = new int[Const.SIZE][Const.SIZE];
    	
    	for(int y = 0; y < Const.SIZE; y++)
    	{
    		rows[y] = new TableRow(ctx);
    		
    		for(int x = 0; x < Const.SIZE; x++)
    		{
    			img[x][y] = new ImageView(ctx);
    			
    			img[x][y].setLayoutParams(layoutParams);

    			img[x][y].setImageResource(getResource(-1));    			
    			img[x][y].setOnClickListener(new CaseListener(x, y));
    			img[x][y].setOnLongClickListener(new FlagListener(x, y));
    			rows[y].addView(img[x][y]);
    		}
    		table.addView(rows[y]);
    	}
    }
    
    /**
     * Listener of the flag action (it places the flags)
     * @author gp2mv3
     */
    private class FlagListener implements OnLongClickListener
    {
    	private int x;
    	private int y;
    	
    	public FlagListener(int x, int y)
    	{
    		this.x = x;
    		this.y = y;
    	}
    	
		@Override
		public boolean onLongClick(View v)
		{
			if(!shown[x][y])
			{
				if(flags[x][y])
				{
					img[x][y].setImageResource(getResource(-1));
					flags[x][y] = false;
				}
				else
				{
					img[x][y].setImageResource(R.drawable.flag);
					flags[x][y] = true;
				}
				
				if(hasWon())
					endGame(true);
				
				return true;
			}
			else
				return false;
		}
    }
    
    /**
     * Reveal the pressed case
     * @author gp2mv3
     */
    private class CaseListener implements OnClickListener
    {
    	private int x;
    	private int y;
    	
    	public CaseListener(int x, int y)
    	{
    		this.x = x;
    		this.y = y;
    	}
    	
		@Override
		public void onClick(View v)
		{
			if(Const.DEBUG) Log.v("case", x+":"+y);
			
			if(!bombs[x][y])
			{
				img[x][y].setImageResource(getResource(values[x][y]));
				
				if(values[x][y] == 0)
					switchEmpty(x, y);
				else
					shown[x][y] = true;
				
				flags[x][y] = false;
				
				if(hasWon())
					endGame(true);
			}
			else
			{
				img[x][y].setImageResource(getResource(10));
				endGame(false);
				if(Const.DEBUG) Log.e("Game Over", "Game Over ! Fail !");
			}
		}
    }
    
    /**
     * Create the bomb table (places the bombs randomly on the map and compute the values of the cases)
     */
    private void randCases()
    {
    	while(mines > 0)
    	{
    		int randX = (int)(Math.random()*10);
    		int randY = (int)(Math.random()*10);
    		
    		if(!bombs[randX][randY])
    		{
    			if(isIn(randX-1, randY-1))
    				values[randX-1][randY-1]++;
    	
    			if(isIn(randX-1, randY))
    				values[randX-1][randY]++;
    			
    			if(isIn(randX-1, randY+1))
    				values[randX-1][randY+1]++;
    			
    			if(isIn(randX, randY-1))
    				values[randX][randY-1]++;
    			
    			if(isIn(randX, randY))
    				values[randX][randY]++;
    			
    			if(isIn(randX, randY+1))
    				values[randX][randY+1]++;
 
    			if(isIn(randX+1, randY-1))
    				values[randX+1][randY-1]++;
    			
    			if(isIn(randX+1, randY))
    				values[randX+1][randY]++;
    			
    			if(isIn(randX+1, randY+1))
    				values[randX+1][randY+1]++;
    			
    			bombs[randX][randY] = true;
    			mines--;
    		}
    	}
    }
    
    /**
     * Reveal the blank cases
     * @param x the X coordonate
     * @param y the Y coordonate
     */
    public void switchEmpty(int x, int y)
    {
    	if(isIn(x, y))
    	{
    		if(!shown[x][y] && bombs[x][y] == false)
    		{
    			shown[x][y] = true;
    			
    			if(values[x][y] > 0)
    				img[x][y].setImageResource(getResource(values[x][y]));
    			else
    				img[x][y].setImageResource(getResource(0));


    			// Check surrounding cells
    			if(values[x][y] == 0)
    			{
    				for(int r = -1; r <= 1; r++)
    				{
    					for(int c = -1; c <= 1; c++)
    					{
    						switchEmpty(x + r, y + c);
    					}
    				}
    			}
    		}
    	}
    }
    
    /**
     * Check if the case is in the board
     * @param x the X
     * @param y the Y
     * @return true if in
     */
    private static boolean isIn(int x, int y)
    {
    	return (x >= 0 && x < Const.SIZE && y >= 0 && y < Const.SIZE);
    }
    
    /**
     * Get the ressource id for a certain value
     * @param the case value
     * @return the ressource
     */
    private int getResource(int value)
    {
    	switch(value)
    	{
    	case 0:
    		return R.drawable.empty;

    	case 1:
    		return R.drawable.case1;

    	case 2:
    		return R.drawable.case2;

    	case 3:
    		return R.drawable.case3;

    	case 4:
    		return R.drawable.case4;

    	case 5:
    		return R.drawable.case5;

    	case 6:
    		return R.drawable.case6;

    	case 7:
    		return R.drawable.case7;

    	case 8:
    		return R.drawable.case8;
    		
    	case 9:
    		return R.drawable.flag;
    		
    	case 10:
    		return R.drawable.explosion;
    		
    	case -1:
    		return R.drawable.case0;
    	}
    	return R.drawable.empty;
    }
    
    /**
     * Create the break dialog
     */
    private void breakDiag()
    {
    	paused = true;
    	chrono.stop();
		alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.breakTitle);
		alert.setMessage(R.string.breakTxt);

		alert.setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				paused = false;
				alert = null;
				resumeGame();
			}
		});

		alert.show();
    }
    
    /**
     * Resume game (just restart the chrono)
     */
    public void resumeGame()
    {
    	chrono.start();
    }
    
    /**
     * End the game (display the dialog and save scores)
     * @param win if true, the user has won
     */
    private void endGame(boolean win)
    {
    	chrono.stop();
    	
    	alert = new AlertDialog.Builder(this);
    	alert.setCancelable(false);
    	if(win)
    	{
    		long time = chrono.getTime();     
    		hs.setBest((int)(time/1000));
    		
    		alert.setTitle(R.string.winTitle);
    		alert.setMessage(String.format(res.getString(R.string.winTxt), (int)(time/1000)));
    	}
    	else
    	{
    		alert.setTitle(R.string.looseTitle);
    		alert.setMessage(R.string.looseTxt); 		
    	}
	
		alert.setPositiveButton(R.string.newGame, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				paused = true;
				Intent intent = new Intent(GameActivity.this, GameActivity.class);
				startActivity(intent);
				GameActivity.this.finish();
			}
		});

		alert.show();
		hs.save();
    }
    
    /**
     * Check if the game is done
     * @return true if the player won
     */
    private boolean hasWon()
    {
    	int nb = 0;
    	if(Const.DEBUG) Log.e("nb won", ""+Const.MINES);
    	
    	for(int i = 0; i < Const.SIZE; i++)
    	{
    		for(int j = 0; j < Const.SIZE; j++)
    		{
    			if(shown[i][j])
    				nb++;
    		}
    	}
    	
    	if(Const.DEBUG) Log.i("nb won", ""+nb);
    	return (nb == (Const.SIZE*Const.SIZE - Const.MINES));
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	if(!paused)
    		breakDiag();
    }
    
    /**
     * Surcharge le bouton back, pour permettre de ne pas quitter sans le vouloir.
     */
    @Override
    public void onBackPressed()
    {
    	AlertDialog.Builder quitDiag = new AlertDialog.Builder(this);

    	quitDiag.setTitle(R.string.quitTitle);
    	quitDiag.setMessage(R.string.quitTxt);

    	quitDiag.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton)
    		{
    			dialog = null;
    			GameActivity.this.finish();
    		}
    	});
    	
    	quitDiag.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton)
    		{
    			dialog = null;
    		}
    	});

    	quitDiag.show();
    }
}
