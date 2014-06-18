package com.taotao.kcroundprogressbar_example;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mindpin.android.kcroundprogressbar.KCRoundProgressBar;


public class MainActivity extends Activity {
	public String TAG = "MainActiviy";
	KCRoundProgressBar pwOne;
	KCRoundProgressBar pwTwo; 
	KCRoundProgressBar pwThree;
	KCRoundProgressBar pwFour;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pwOne = (KCRoundProgressBar) findViewById(R.id.pro_1);
		pwTwo = (KCRoundProgressBar) findViewById(R.id.pro_2);
		pwThree = (KCRoundProgressBar) findViewById(R.id.pro_3);
		pwFour = (KCRoundProgressBar) findViewById(R.id.pro_4);
		
		
		pwOne.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//pwOne.set_thickness(0.02f);
				
				if (!wheelRunning) {
					wheelProgress = 0;
					pwOne.reset();
					new Thread(r).start();
				}
			}
		});
		
		pwTwo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(wheelProgress >= pwTwo.get_max()){
					wheelProgress = 0;
				}
				wheelProgress += 30;
				pwTwo.set_current_smooth(wheelProgress);

				
			}
		});
		
		pwThree.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!wheelRunning) {
					wheelProgress = 0;
					//pwThree.reset();
					new Thread(r2).start();
				}

				
			}
		});
		
		pwFour.startRoll();
		pwFour.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(pwFour.isLoading()){  
					pwFour.stopRoll();
				}else{
					pwFour.startRoll();
				}
			}
		});
		
		
	}
	
	boolean wheelRunning;
	int wheelProgress;
	final Runnable r = new Runnable() {
		public void run() {
			wheelRunning = true;
			while (wheelProgress <= pwOne.get_max()) {
				pwOne.set_current(wheelProgress);
				wheelProgress++;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			wheelRunning = false;
		}
	};
	
	final Runnable r2 = new Runnable() {
		public void run() {
			wheelRunning = true;
			wheelProgress = pwThree.getProgress();
			Log.i(TAG, "progress = " + wheelProgress);
			while (wheelProgress <= pwThree.get_max()) {
				pwThree.set_current(wheelProgress);
				wheelProgress++;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			pwThree.reset();
			wheelRunning = false;
		}
	};

}
