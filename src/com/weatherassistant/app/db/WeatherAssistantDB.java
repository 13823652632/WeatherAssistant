package com.weatherassistant.app.db;

import java.util.ArrayList;
import java.util.List;

import com.weatherassistant.app.model.City;
import com.weatherassistant.app.model.County;
import com.weatherassistant.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作工具类
 * @author Sage Luo
 * @date 2016年10月5日
 */
public class WeatherAssistantDB {
	private static final String TAG = "WeatherAssistantDB";
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "weather_assistant";
	/**
	 * 数据库版本
	 */
	public static final int version = 1;
	
	private static WeatherAssistantDB weatherDB;
	private SQLiteDatabase db;
	
	// 省份数据库key
	public final static String provinceTableName = "Province";
	private final static String provinceNameKey = "province_name";
	private final static String provinceCodeKey = "province_code";
	
	// 城市数据库key
	public final static String cityTableName = "City";
	private final static String cityNameKey = "city_name";
	private final static String cityCodeKey = "city_code";
	private final static String provinceIdKey = "province_id";
	
	// 县级数据库key
	public final static String countyTableName = "County";
	private final static String countyNameKey = "county_name";
	private final static String countyCodeKey = "county_code";
	private final static String cityIdKey = "city_id";
	
	/**
	 * 构造方法私有化
	 * @param context
	 */
	private WeatherAssistantDB(Context context){
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, version);
		db = dbHelper.getWritableDatabase();
	}
	/**
	 * 获取WeatherAssistant对象实例
	 * @param context
	 * @return WeatherAssistantDB对象实例
	 */
	public synchronized static WeatherAssistantDB getInstance(Context context){
		if (weatherDB == null) {
			weatherDB = new WeatherAssistantDB(context);
		}
		return weatherDB;
	}
	/**
	 * 将province对象存储到数据库
	 * @param province
	 */
	public void saveProvince(Province province){
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put(provinceNameKey, province.getProvinceName());
			values.put(provinceCodeKey, province.getProvinceCode());
			db.insert(provinceTableName, null, values);
		} else {
			Log.e(TAG, "province is null");
		}
	}
	/**
	 * 从数据库读取全国所有省份信息
	 * @return 省份信息列表
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query(provinceTableName, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex(provinceNameKey)));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex(provinceCodeKey)));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	/**
	 * 保存城市对象到数据库
	 * @param city
	 */
	public void saveCity(City city){
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put(cityNameKey, city.getCityName());
			values.put(cityCodeKey, city.getCityCode());
			values.put(provinceIdKey, city.getProvinceId());
			db.insert(cityTableName, null, values);
		} else {
			Log.e(TAG, "city is null");
		}
	}
	/**
	 * 读取某省份下的所有城市信息
	 * @return 城市列表信息
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query(cityTableName, null, provinceIdKey + "=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex(cityNameKey)));
				city.setCityCode(cursor.getString(cursor.getColumnIndex(cityCodeKey)));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	public void saveCounty(County county){
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put(countyNameKey, county.getCountyName());
			values.put(countyCodeKey, county.getCountyCode());
			values.put(cityIdKey, county.getCityId());
			db.insert(countyTableName, null, values);
		} else {
			Log.e(TAG, "county is null");
		}
	}
	
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query(countyTableName, null, cityIdKey + "=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex(countyNameKey)));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex(countyCodeKey)));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
}
