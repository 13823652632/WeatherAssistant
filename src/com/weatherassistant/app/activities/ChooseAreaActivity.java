package com.weatherassistant.app.activities;

import java.util.ArrayList;
import java.util.List;

import com.weatherassistant.app.BaseActivity;
import com.weatherassistant.app.R;
import com.weatherassistant.app.db.WeatherAssistantDB;
import com.weatherassistant.app.model.City;
import com.weatherassistant.app.model.County;
import com.weatherassistant.app.model.Province;
import com.weatherassistant.app.model.WeatherConstants;
import com.weatherassistant.app.util.HttpCallbackListener;
import com.weatherassistant.app.util.HttpUtil;
import com.weatherassistant.app.util.Utility;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends BaseActivity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private TextView tvTitle;
	private ListView lvAreaInfo;
	private ArrayAdapter<String> adapter;
	private WeatherAssistantDB weatherDB;
	private List<String> dataList = new ArrayList<>();
	
	// <-- 省市县列表 -->
	private List<Province> provinceList = new ArrayList<>();
	private List<City> cityList = new ArrayList<>();
	private List<County> countyList = new ArrayList<>();
	
	// <-- 选中的省市 -->
	private Province selectedProvince;
	private City selectedCity;
	
	// 选中的的级别
	private int currentLevel;
	// 是否是从WeatherActivity打开的
	private boolean isFromWaetherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initViews() {
		setCustomContentView(R.layout.activity_choose_area);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		lvAreaInfo = (ListView) findViewById(R.id.lvAreaInfo);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
		lvAreaInfo.setAdapter(adapter);
	}

	@Override
	protected void setData() {
		isFromWaetherActivity = getIntent().getBooleanExtra(WeatherConstants.IS_FROM_WEA_ACI_KEY, false);
		weatherDB = WeatherAssistantDB.getInstance(this);
		queryProvinces();
	}
	
	@Override
	protected void setListeners() {
		lvAreaInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
					if (dataList.size() == 1) {
						selectedCity = cityList.get(0);
						queryCounties();
					}
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(position).getCountyCode();
					Bundle extras = new Bundle();
					extras.putString(WeatherConstants.COUNTY_CODE_KEY, countyCode);
					simpleStartActivity(WeatherActivity.class, extras);
					finish();
				}
			}
		});
	}
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器查询。
	 */
	private void queryProvinces() {
		provinceList = weatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			lvAreaInfo.setSelection(0);
			tvTitle.setText(getString(R.string.txt_china));
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, WeatherAssistantDB.provinceTableName);
		}
	}
	
	/**
	 * 查询省内所有的市
	 */
	private void queryCities() {
		cityList = weatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			lvAreaInfo.setSelection(0);
			tvTitle.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), WeatherAssistantDB.cityTableName);
		}
	}
	
	/**
	 * 查询市内所有的县
	 */
	private void queryCounties() {
		countyList = weatherDB.loadCounty(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			lvAreaInfo.setSelection(0);
			tvTitle.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), WeatherAssistantDB.countyTableName);
		}
	}

	/**
	 * 从服务器查询省市县数据
	 * @param code
	 * @param tableType
	 */
	private void queryFromServer(String code, final String tableType) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = HttpUtil.baseUrl + "list3/city" + code + ".xml";
		} else {
			address = HttpUtil.baseUrl + "list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean isSuccess = false;
				if (tableType.equals(WeatherAssistantDB.provinceTableName)) {
					isSuccess = Utility.handlerProvincesResponse(weatherDB, response);
				} else if (tableType.equals(WeatherAssistantDB.cityTableName)) {
					isSuccess = Utility.handlerCitiesResponse(weatherDB, response, selectedProvince.getId());
				} else if (tableType.equals(WeatherAssistantDB.countyTableName)) {
					isSuccess = Utility.handlerCountiesResponse(weatherDB, response, selectedCity.getId());
				}
				
				if (isSuccess) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if (tableType.equals(WeatherAssistantDB.provinceTableName)) {
								queryProvinces();
							} else if (tableType.equals(WeatherAssistantDB.cityTableName)) {
								queryCities();
							} else if (tableType.equals(WeatherAssistantDB.countyTableName)) {
								queryCounties();
							}
						}

					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, ChooseAreaActivity.this.getString(R.string.txt_load_err), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		switch (currentLevel) {
		case LEVEL_COUNTY:
			queryCities();
			if (dataList.size() == 1) {
				queryProvinces();
			}
			break;
			
		case LEVEL_CITY:
			queryProvinces();
			break;

		default:
			if (isFromWaetherActivity) {
				simpleStartActivity(WeatherActivity.class);
			}
			finish();
			break;
		}
	}
}
