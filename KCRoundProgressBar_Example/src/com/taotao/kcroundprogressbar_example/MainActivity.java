package com.taotao.kcroundprogressbar_example;

import com.taotao.kcroundprogressbar.ProgressWheel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	public String TAG = "MainActiviy";
	ProgressWheel pwOne;
	ProgressWheel pwTwo;
	ProgressWheel pwThree;
	ProgressWheel pwFour;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pwOne = (ProgressWheel) findViewById(R.id.pro_1);
		pwTwo = (ProgressWheel) findViewById(R.id.pro_2);
		pwThree = (ProgressWheel) findViewById(R.id.pro_3);
		pwFour = (ProgressWheel) findViewById(R.id.pro_4);
		
		pwOne.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!wheelRunning) {
					wheelProgress = 0;
					pwOne.reset();
					new Thread(r).start();
				}
			}
		});
		
		pwTwo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(wheelProgress >= pwTwo.getMax_value()){
					wheelProgress = 0;
				}
				wheelProgress += 30;
				pwTwo.setProgressSmooth(wheelProgress);

				
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
			while (wheelProgress <= pwOne.getMax_value()) {
				pwOne.setProgress(wheelProgress);
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
			while (wheelProgress <= pwThree.getMax_value()) {
				pwThree.setProgress(wheelProgress);
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
