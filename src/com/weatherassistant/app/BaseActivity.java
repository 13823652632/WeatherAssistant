package com.weatherassistant.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

public class BaseActivity extends ActionBarActivity {
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().hide();
		initViews();
		setData();
		changeViews();
		setListeners();
		// Very Good!
		// 提交一个汉语注释，让你的代码更新一下。
		// 再Very Good一下，提交。
}

	protected void setCustomContentView(int layoutResID){
		setContentView(layoutResID);
	}
	
	protected void initViews() {
		
	}
	
	protected void setData() {
		
	}
	
	protected void changeViews() {
		
	}
	
	protected void setListeners() {
		
	}
	
	protected void showProgressDialog(){
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getString(R.string.txt_loading));
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	protected void closeProgressDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	public void simpleStartActivity(Class<?> cls){
		startActivity(new Intent(this, cls));
	}
	
	public void simpleStartActivity(Class<?> cls, Bundle extras){
		Intent intent = new Intent(this, cls);
		intent.putExtras(extras);
		startActivity(intent);
	}
}
