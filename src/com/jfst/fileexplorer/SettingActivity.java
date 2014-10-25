package com.jfst.fileexplorer;

import android.app.Activity;
import android.os.Bundle;

import com.jfst.fileexplorer.ui.SettingItem;

public class SettingActivity extends Activity {
	private SettingItem dir_comparator;
	private SettingItem showHidden;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

	/**
	 * 初始化界面
	 */
	private void initUI() {
		setContentView(R.layout.setting_layout);
		dir_comparator = (SettingItem) findViewById(R.id.dir_comparator);
		showHidden = (SettingItem) findViewById(R.id.showHidden);
	}


}
