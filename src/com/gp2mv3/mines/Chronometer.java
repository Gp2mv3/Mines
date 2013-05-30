package com.gp2mv3.mines;

public class Chronometer {

	private long start;
	private long stop;
	private boolean stopped;
	
	private long startS;
	private long diff;
	
	public Chronometer()
	{
		stopped = false;
		diff = 0;
	}
	
	public void start()
	{
		start = System.currentTimeMillis();
		
		if(stopped)
			diff += start - startS;
		
		stopped = false;
	}
	
	public void stop()
	{
		stopped = true;
		stop = System.currentTimeMillis();
		startS = stop;
	}
	
	public long getTime()
	{
		long time = stop - start - diff;
		
		if(!stopped)
			time = System.currentTimeMillis() - start;
		
		return time;
	}
}
