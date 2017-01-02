package com.weatherassistant.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
/**
 * 与服务器交互，获取响应数据
 * @author Sage Luo
 * @date 2016年10月5日 下午4:16:04
 */
public class HttpUtil {
	public static final String baseUrl = "http://www.weather.com.cn/data/";
	
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		sendHttpRequest(address, listener, false);
	}
	
	public synchronized static void sendHttpRequest(final String address, final HttpCallbackListener listener, final boolean isEOF){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = null;
					StringBuilder response = new StringBuilder();
					if (!isEOF) {
						reader = new BufferedReader(new InputStreamReader(in));
						String line;
						while ((line = reader.readLine()) != null){
							response.append(line);
						}
					} else {
						char[] data = new char[1024 * 512];
						reader = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
						int len = reader.read(data);
						String rexml = String.valueOf(data, 0, len);
						response.append(rexml);
					}
						
					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
