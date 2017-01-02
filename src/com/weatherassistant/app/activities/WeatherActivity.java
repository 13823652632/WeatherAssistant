package com.weatherassistant.app.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.weatherassistant.app.BaseActivity;
import com.weatherassistant.app.R;
import com.weatherassistant.app.model.WeatherConstants;
import com.weatherassistant.app.model.WeatherInfo;
import com.weatherassistant.app.services.AutoUpdateService;
import com.weatherassistant.app.util.HttpCallbackListener;
import com.weatherassistant.app.util.HttpUtil;
import com.weatherassistant.app.util.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends BaseActivity implements OnClickListener {
//	private final int QUERY_WEATHER_INFO = 0x111;
	private final String TAG = "WeatherActivity";
	public final String COUNTY_CODE = "countyCode";
	public final String WEATHER_CODE = "weatherCode";
	
	private LinearLayout llWeatherInfo;
	private TextView tvTitle, tvPublishTime, tvCurrentDate, tvWeatherDesp, tvTemp1, tvTemp2;
	private ImageButton ibnHome, ibnRefresh;
	
	private String countyCode;
	
	boolean isEOF;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initViews() {
		setContentView(R.layout.activity_weather);
		llWeatherInfo = (LinearLayout) findViewById(R.id.llWeatherInfo);
		tvTitle = (TextView) findViewById(R.id.tvTitle); 
		tvPublishTime = (TextView) findViewById(R.id.tvPublishTime); 
		tvCurrentDate = (TextView) findViewById(R.id.tvCurrentDate); 
		tvWeatherDesp = (TextView) findViewById(R.id.tvWeatherDesp); 
		tvTemp1 = (TextView) findViewById(R.id.tvTemp1); 
		tvTemp2 = (TextView) findViewById(R.id.tvTemp2); 
		ibnHome = (ImageButton) findViewById(R.id.ibnHome);
		ibnRefresh = (ImageButton) findViewById(R.id.ibnRefresh);
		ibnHome.setVisibility(View.VISIBLE);
		ibnRefresh.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void setData() {
		countyCode = getIntent().getStringExtra(WeatherConstants.COUNTY_CODE_KEY);
	}
	
	@Override
	protected void changeViews() {
		// 有县级代号就去查询天气
		if (!TextUtils.isEmpty(countyCode)) {
			tvPublishTime.setText(getString(R.string.txt_synchronization));
			llWeatherInfo.setVisibility(View.INVISIBLE);
			tvTitle.setVisibility(View.INVISIBLE);
			queryWeatherCode();
		} else {
			// 没有取到县级代号就直接从SharedPrefefences获取缓存天气信息显示
			showWeather();
		}
	}
	
	@Override
	protected void setListeners() {
		ibnHome.setOnClickListener(this);
		ibnRefresh.setOnClickListener(this);
	}
	/**
	 * 查询县级代号所对应的天气代号
	 * @param countyCode2
	 */
	private void queryWeatherCode() {
		isEOF = false;
		String address = HttpUtil.baseUrl + "list3/city" + countyCode + ".xml";
		queryFromServer(address, COUNTY_CODE);
	}
	/**
	 * 查询天气
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode){
		isEOF = true;
		String address = HttpUtil.baseUrl + "cityinfo/" + weatherCode + ".html";
		queryFromServer(address, WEATHER_CODE);
	}

	/**
	 * 查询天气代号或者天气信息
	 * @param address
	 * @param type
	 */
	private void queryFromServer(String address, final String type) {
		Log.e(TAG, "address = " + address + "; type = " + type);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				if (type.equals(COUNTY_CODE)) {
					// 解析天气代号
					String[] array = response.split("\\|");
					if (array != null && array.length == 2) {
						// 根据天气代号查询天气信息
						queryWeatherInfo(array[1]);
					}
				} else if (type.equals(WEATHER_CODE)) {
					Utility.handlerWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}

					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvPublishTime.setText(getString(R.string.txt_synchronization_err));
					}
				});
			}
		}, isEOF);
	}
	
	private void showWeather() {
		WeatherInfo info = Utility.getWeatherInfo(this);
		tvTitle.setText(info.getCityName());
		tvTemp1.setText(info.getTemp1());
		tvTemp2.setText(info.getTemp2());
		tvWeatherDesp.setText(info.getWeatherDesp());
		
		SimpleDateFormat format = new SimpleDateFormat("HHmm", Locale.CHINA);
		Date date = new Date();
		String cTime = format.format(date);
		int curTime = Integer.valueOf(cTime);
		int pTime = Integer.valueOf(info.getPublishTime().replace(":", "").replace(" ", ""));
		Log.v(TAG, "curTime = " + curTime + "; pTime = " + pTime);
		String publish;
		if (curTime < pTime) {
			publish = getString(R.string.txt_yesterday) + info.getPublishTime() + getString(R.string.txt_publish);
		} else {
			publish = getString(R.string.txt_today) + info.getPublishTime() + getString(R.string.txt_publish);
		}
		tvPublishTime.setText(publish);
		tvCurrentDate.setText(info.getCurrentDate());
		llWeatherInfo.setVisibility(View.VISIBLE);
		tvTitle.setVisibility(View.VISIBLE);
		
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibnHome:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra(WeatherConstants.IS_FROM_WEA_ACI_KEY, true);
			startActivity(intent);
			finish();
			break;
			
		case R.id.ibnRefresh:
			String weatherCode = PreferenceManager.getDefaultSharedPreferences(this).getString(WeatherConstants.WEATHER_CODE_KEY, "");
			if (!TextUtils.isEmpty(weatherCode)) {
				tvPublishTime.setText(getString(R.string.txt_synchronization));
				queryWeatherInfo(weatherCode);
			}
			break;
			
		default:
			break;
		}
	}
}
