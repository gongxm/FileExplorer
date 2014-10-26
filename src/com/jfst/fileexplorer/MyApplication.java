package com.jfst.fileexplorer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {
	private static Context context;
	private static SharedPreferences sp;
	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context=this;
		sp=getSharedPreferences("config", MODE_PRIVATE);
	}
	
	/**
	 * 获取上下文
	 * @return 上下文对象
	 */
	public static Context getContext() {
		return context;
	}
	
	/**
	 * 获取应用的配置文件对象
	 * @return 配置文件对象
	 */
	public static SharedPreferences getConfig(){
		return sp;
	}
}
