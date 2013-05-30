package com.gp2mv3.mines;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class AboutActivity extends WithMenuActivity
{
	private AdManager adManager = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        adManager = new AdManager(this);
        
        ToggleButton disableAd = (ToggleButton) findViewById(R.id.disableAd);
        disableAd.setChecked(adManager.isEnabled()); 
        disableAd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked)
					adManager.enable();
				else
					adManager.disable();
			}
		});
    }
}
