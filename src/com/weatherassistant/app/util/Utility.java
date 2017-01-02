package com.weatherassistant.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.weatherassistant.app.db.WeatherAssistantDB;
import com.weatherassistant.app.model.City;
import com.weatherassistant.app.model.County;
import com.weatherassistant.app.model.Province;
import com.weatherassistant.app.model.WeatherConstants;
import com.weatherassistant.app.model.WeatherInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 解析地区天气数据到本地的工具类
 * @author Sage Luo
 * @date 2016年10月5日 下午4:17:48
 */
public class Utility {
	
	/**
	 * 解析服务器返回的JSON数据，并将数据存储到本地
	 * @param context
	 * @param response
	 */
	public static void handlerWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherObject = jsonObject.getJSONObject("weatherinfo");
			WeatherInfo info = new WeatherInfo();
			info.setCityName(weatherObject.getString("city"));
			info.setWeatherCode(weatherObject.getString("cityid"));
			info.setTemp1(weatherObject.getString("temp1"));
			info.setTemp2(weatherObject.getString("temp2"));
			info.setWeatherDesp(weatherObject.getString("weather"));
			info.setPublishTime(weatherObject.getString("ptime"));
			SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
			info.setCurrentDate(format.format(new Date()));
			saveWeatherInfo(context, info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将服务器返回的天气信息保存到Sharedpreferences文件中
	 * @param context
	 * @param info
	 */
	public static void saveWeatherInfo(Context context, WeatherInfo info) {
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(WeatherConstants.CITY_SELECTED_KEY, true);
		editor.putString(WeatherConstants.CITY_NAME_KEY, info.getCityName());
		editor.putString(WeatherConstants.WEATHER_CODE_KEY, info.getWeatherCode());
		editor.putString(WeatherConstants.TEMP1_KEY, info.getTemp1());
		editor.putString(WeatherConstants.TEMP2_KEY, info.getTemp2());
		editor.putString(WeatherConstants.WEATHER_DESP_KEY, info.getWeatherDesp());
		editor.putString(WeatherConstants.PUBLISH_TIME_KEY, info.getPublishTime());
		editor.putString(WeatherConstants.CURRENT_DATE_KEY, info.getCurrentDate());
		editor.commit();
	}
	
	public static WeatherInfo getWeatherInfo(Context context){
		WeatherInfo info = new WeatherInfo();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		info.setCityName(preferences.getString(WeatherConstants.CITY_NAME_KEY, ""));
		info.setWeatherCode(preferences.getString(WeatherConstants.WEATHER_CODE_KEY, ""));
		info.setTemp1(preferences.getString(WeatherConstants.TEMP1_KEY, ""));
		info.setTemp2(preferences.getString(WeatherConstants.TEMP2_KEY, ""));
		info.setWeatherDesp(preferences.getString(WeatherConstants.WEATHER_DESP_KEY, ""));
		info.setPublishTime(preferences.getString(WeatherConstants.PUBLISH_TIME_KEY, ""));
		info.setCurrentDate(preferences.getString(WeatherConstants.CURRENT_DATE_KEY, ""));
		return info;
	}
	/**
	 * 解析和处理服务器返回的省级数据
	 * @param assistantDB
	 * @param response
	 * @return 是否已经成功解析的boolean值
	 */
	public synchronized static boolean handlerProvincesResponse(WeatherAssistantDB assistantDB, String response){
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到Province表
					assistantDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析和处理服务器返回的市级数据
	 * @param assistantDB
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public synchronized static boolean handlerCitiesResponse(WeatherAssistantDB assistantDB, String response, int provinceId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 将解析出来的数据存储到City表
					assistantDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析和处理服务器返回的县级数据
	 * @param assistantDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public synchronized static boolean handlerCountiesResponse(WeatherAssistantDB assistantDB, String response, int cityId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 将解析出来的数据存储到City表
					assistantDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
