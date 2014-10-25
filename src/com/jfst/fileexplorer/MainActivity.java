package com.jfst.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jfst.fileexplorer.adapter.FileAdapter;
import com.jfst.fileexplorer.utils.OpenFileUtils;

public class MainActivity extends Activity {

	private ListView listview;
	private FileAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listview = (ListView) findViewById(R.id.lv_main);
		adapter = new FileAdapter(this, null);
		listview.setAdapter(adapter);
		File dir = Environment.getExternalStorageDirectory();
		if (dir == null) {
			dir = new File("/");
		}
		listFiles(dir);

		// ListView的点击事件
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File dir = (File) listview.getItemAtPosition(position);
				listFiles(dir);
			}
		});

		// ListView的长按事件
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// FIXME
				return false;
			}
		});

		toast = Toast.makeText(this, "再按一次退出！", 0);
	}

	private void listFiles(File file) {
		if (file == null)
			return;
		else {
			if (file.isFile()) {
				Intent intent=OpenFileUtils.openFile(file.getAbsolutePath());
				startActivity(intent);
				return;
			}
			File[] files = file.listFiles();
			List<File> list = null;
			if (files != null) {
				List<File> temp = Arrays.asList(files);
				list = new ArrayList<File>(temp);
			} else {
				list = new ArrayList<File>();
			}
			list.add(0, file.getParentFile());
			adapter.setData(list);
			listview.setSelection(0);
		}
	}

	private int STATUS = 0;
	private Toast toast;

	@Override
	public void onBackPressed() {
		File file = (File) listview.getItemAtPosition(0);
		if (file == null) {
			if (STATUS == 0) {
				STATUS++;
				toast.show();
				new Handler() {
					public void handleMessage(android.os.Message msg) {
						STATUS = 0;
					};
				}.sendEmptyMessageDelayed(STATUS, 500);
			} else {
				super.onBackPressed();
			}
			return;
		}
		if ("/".equals(file.getName())) {
			super.onBackPressed();
		} else {
			listFiles(file);
		}
	}

	@Override
	protected void onDestroy() {
		toast.cancel();
		super.onDestroy();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();
			break;
		case 1:
			setting();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	
	/**
	 * 打开设置界面
	 */
	private void setting() {
		Intent intent=new Intent(this,SettingActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "设置");
		menu.add(0, 0, 0, "退出");
		return super.onCreateOptionsMenu(menu);
	}

}
