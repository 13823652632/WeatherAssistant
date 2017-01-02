package com.weatherassistant.app;

import com.weatherassistant.app.activities.ChooseAreaActivity;
import com.weatherassistant.app.activities.WeatherActivity;
import com.weatherassistant.app.model.WeatherConstants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				go();
			}
		}, 100);
		
	}

	private void go() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (preferences.getBoolean(WeatherConstants.CITY_SELECTED_KEY, false)) {
					simpleStartActivity(WeatherActivity.class);
				} else {
					simpleStartActivity(ChooseAreaActivity.class);
				}
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				finish();
			}
		});
	}

}
