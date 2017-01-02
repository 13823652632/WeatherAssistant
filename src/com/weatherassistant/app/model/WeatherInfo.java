package com.weatherassistant.app.model;

public class WeatherInfo {
	private String cityName;
	private String weatherCode;
	private String temp1;
	private String temp2;
	private String weatherDesp;
	private String publishTime;
	private String currentDate;
	
	public WeatherInfo(){}
	
	public WeatherInfo(String cityName, String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		this.cityName = cityName;
		this.weatherCode = weatherCode;
		this.temp1 = temp1;
		this.temp2 = temp2;
		this.weatherDesp = weatherDesp;
		this.publishTime = publishTime;
	}
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getWeatherCode() {
		return weatherCode;
	}
	public void setWeatherCode(String weatherCode) {
		this.weatherCode = weatherCode;
	}
	public String getTemp1() {
		return temp1;
	}
	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}
	public String getTemp2() {
		return temp2;
	}
	public void setTemp2(String temp2) {
		this.temp2 = temp2;
	}
	public String getWeatherDesp() {
		return weatherDesp;
	}
	public void setWeatherDesp(String weatherDesp) {
		this.weatherDesp = weatherDesp;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	
}
