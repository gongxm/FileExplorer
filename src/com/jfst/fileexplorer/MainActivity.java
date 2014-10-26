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

	private boolean isShowHidden;// �Ƿ���ʾ��.��ͷ�������ļ���
	private boolean isDirFront;// �Ƿ����������ļ���

	private Comparator<File> comparator;// �ļ����ȵıȽ���

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

		// ��ʼ���Ƚ���
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

		// �Ƿ񱣴��˳�ʱ��·��
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

		// ListView�ĵ���¼�
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dir = ((FileItem) listview.getItemAtPosition(position))
						.getFile();
				listFiles(dir);
			}
		});

		// ListView�ĳ����¼�
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// FIXME
				return false;
			}
		});

		toast = Toast.makeText(this, "�ٰ�һ���˳���", Toast.LENGTH_SHORT);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (dir.isDirectory())
			listFiles(dir);
	}

	/**
	 * ��������״̬
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
	 * �����ļ���
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
			// ����ʾ�����ļ���
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

			// ����
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
	 * ���÷��ؼ����¼�
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
	 * �˳�ʱȡ��Toast����ʾ
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
	 * ���ò˵�����¼�
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();// �˳�
			break;
		case 1:
			setting();// ����
			break;
		case 2:
			about();// ����
			break;
		case 3:
			toSdcard();// ת��SD��
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
			Toast.makeText(this, "sdcard�����ڣ�", 0).show();
		}
	}

	/**
	 * ����
	 */
	private void about() {
		Builder builder = new Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		View view = View.inflate(this, R.layout.about, null);
		builder.setView(view);
		builder.setTitle("�ҵ��ļ�������");
		view.findViewById(R.id.sure).setOnClickListener(this);
		dialog = builder.show();
	}

	/**
	 * �����ý���
	 */
	private void setting() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	/**
	 * ���ò˵���
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "����");
		menu.add(0, 3, 0, "ת��SD��");
		menu.add(0, 2, 0, "����");
		menu.add(0, 0, 0, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		dialog.dismiss();
	}

}
