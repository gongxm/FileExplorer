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
	 * ��ȡ������
	 * @return �����Ķ���
	 */
	public static Context getContext() {
		return context;
	}
	
	/**
	 * ��ȡӦ�õ������ļ�����
	 * @return �����ļ�����
	 */
	public static SharedPreferences getConfig(){
		return sp;
	}
}
