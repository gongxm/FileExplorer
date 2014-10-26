package com.jfst.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.jfst.fileexplorer.Constants.Constants;
import com.jfst.fileexplorer.adapter.FileAdapter;
import com.jfst.fileexplorer.domain.FileItem;
import com.jfst.fileexplorer.utils.OpenFileUtils;

@SuppressLint("ShowToast")
public class MainActivity extends Activity implements OnClickListener {

	private ListView listview;
	private FileAdapter adapter;

	private boolean isShowHidden;// 是否显示以.开头的隐藏文件夹
	private boolean isDirFront;// 是否优先排列文件夹

	private Comparator<File> comparator;// 文件优先的比较器

	private int STATUS = 0;
	private Toast toast;
	private File dir;
	private boolean isSavePath;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listview = (ListView) findViewById(R.id.lv_main);
		adapter = new FileAdapter(this, null);
		listview.setAdapter(adapter);

		updateStatus();

		// 初始化比较器
		comparator = new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				if (file1.isDirectory() && !file2.isDirectory()) {
					return -1;
				} else if (!file1.isDirectory() && file2.isDirectory()) {
					return 1;
				}
				return file1.getName().compareTo(file2.getName());
			}
		};

		// 是否保存退出时的路径
		if (isSavePath) {
			String path = MyApplication.getConfig().getString(
					Constants.DIRPATH, "/");
			dir = new File(path);
			if (dir.isFile()) {
				dir = dir.getParentFile();
			}
		} else {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				dir = Environment.getExternalStorageDirectory();
			} else {
				dir = new File("/");
			}
		}
		listFiles(dir);

		// ListView的点击事件
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dir = ((FileItem) listview.getItemAtPosition(position))
						.getFile();
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

		toast = Toast.makeText(this, "再按一次退出！", Toast.LENGTH_SHORT);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (dir.isDirectory())
			listFiles(dir);
	}

	/**
	 * 更新排序状态
	 */
	private void updateStatus() {
		isShowHidden = MyApplication.getConfig().getBoolean(
				Constants.ISSHOWHIDDEN, false);
		isDirFront = MyApplication.getConfig().getBoolean(Constants.ISDIRFRONT,
				false);
		isSavePath = MyApplication.getConfig().getBoolean(Constants.SAVEPATH,
				false);
	}

	/**
	 * 遍历文件夹
	 * 
	 * @param file
	 */
	private void listFiles(File file) {
		updateStatus();
		if (file == null)
			return;
		else {
			if (file.isFile()) {
				Intent intent = OpenFileUtils.openFile(file.getAbsolutePath());
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
			// 不显示隐藏文件夹
			if (!isShowHidden) {
				CopyOnWriteArrayList<File> temp = new CopyOnWriteArrayList<File>(
						list);
				for (File f : temp) {
					if (f.getName().startsWith(".")) {
						temp.remove(f);
					}
				}
				list = new ArrayList<File>(temp);
			}

			// 排序
			if (isDirFront) {
				Collections.sort(list, comparator);
			}
			list.add(0, file.getParentFile());
			List<FileItem> items = new ArrayList<FileItem>();
			for (File f : list) {
				items.add(new FileItem(f, false));
			}
			adapter.setData(items);
			listview.setSelection(0);
			getWindow().setTitle(dir.getAbsolutePath());
		}
	}

	/**
	 * 设置返回键的事件
	 */
	@Override
	public void onBackPressed() {
		dir = ((FileItem) listview.getItemAtPosition(0)).getFile();
		if (dir == null) {
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
		if ("/".equals(dir.getName())) {
			super.onBackPressed();
		} else {
			listFiles(dir);
		}
	}

	/**
	 * 退出时取消Toast的显示
	 */
	@Override
	protected void onDestroy() {
		toast.cancel();
		if (isSavePath) {
			MyApplication.getConfig().edit()
					.putString(Constants.DIRPATH, dir.getAbsolutePath())
					.commit();
		}
		super.onDestroy();
	}

	/**
	 * 设置菜单点击事件
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();// 退出
			break;
		case 1:
			setting();// 设置
			break;
		case 2:
			about();// 关于
			break;
		case 3:
			toSdcard();// 转到SD卡
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void toSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)){
			dir = Environment.getExternalStorageDirectory();
			listFiles(dir);
		}else{
			Toast.makeText(this, "sdcard不存在！", 0).show();
		}
	}

	/**
	 * 关于
	 */
	private void about() {
		Builder builder = new Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		View view = View.inflate(this, R.layout.about, null);
		builder.setView(view);
		builder.setTitle("我的文件管理器");
		view.findViewById(R.id.sure).setOnClickListener(this);
		dialog = builder.show();
	}

	/**
	 * 打开设置界面
	 */
	private void setting() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置菜单项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "设置");
		menu.add(0, 3, 0, "转到SD卡");
		menu.add(0, 2, 0, "关于");
		menu.add(0, 0, 0, "退出");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		dialog.dismiss();
	}

}
