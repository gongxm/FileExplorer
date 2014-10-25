package com.jfst.fileexplorer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfst.fileexplorer.MyApplication;
import com.jfst.fileexplorer.R;

public class SettingItem extends RelativeLayout implements OnClickListener,
		OnCheckedChangeListener {

	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox checkbox;
	private String[] params;
	private String flag;

	public SettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SettingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.jfst.fileexplorer",
				"title");
		String desc = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.jfst.fileexplorer",
				"desc");
		flag = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.jfst.fileexplorer",
				"flag");
		params = desc.split("#");
		updateStatus();
		tv_title.setText(title);
	}

	/**
	 * 根据配置文件更新状态
	 */
	private void updateStatus() {
		boolean checked=MyApplication.getConfig().getBoolean(flag, false);
		if (checked) {
			tv_desc.setText(params[0]);
		} else {
			tv_desc.setText(params[1]);
		}
		checkbox.setChecked(checked);
	}

	public SettingItem(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化界面
	 * 
	 * @param context
	 */
	private void init(Context context) {
		View.inflate(context, R.layout.setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		this.setOnClickListener(this);
		checkbox.setOnCheckedChangeListener(this);
	}

	/**
	 * Item被点击时改变CheckBox的选中状态
	 */
	@Override
	public void onClick(View v) {
		checkbox.setChecked(!checkbox.isChecked());
	}

	/**
	 * 当CheckBox的状态发生改变时的监听器
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MyApplication.getConfig().edit().putBoolean(flag, isChecked).commit();
		updateStatus();
	}
}
