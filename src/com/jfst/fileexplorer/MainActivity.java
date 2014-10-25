package com.jfst.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.content.Intent;
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

import com.jfst.fileexplorer.Constants.Constants;
import com.jfst.fileexplorer.adapter.FileAdapter;
import com.jfst.fileexplorer.utils.OpenFileUtils;

public class MainActivity extends Activity {

	private ListView listview;
	private FileAdapter adapter;
	
	private boolean isShowHidden;//�Ƿ���ʾ��.��ͷ�������ļ���
	private boolean isDirFront;//�Ƿ����������ļ���
	
	private Comparator<File> comparator;//�ļ����ȵıȽ���
	
	private int STATUS = 0;
	private Toast toast;
	private File dir;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listview = (ListView) findViewById(R.id.lv_main);
		adapter = new FileAdapter(this, null);
		listview.setAdapter(adapter);
		
		//��ʼ���Ƚ���
		comparator=new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {
				if(file1.isDirectory()&&!file2.isDirectory()){
					return -1;
				}else if(!file1.isDirectory()&&file2.isDirectory()){
					return 1;
				}
				return file1.getName().compareTo(file2.getName());
			}
		};
		
		dir = Environment.getExternalStorageDirectory();
		if (dir == null) {
			dir = new File("/");
		}
		listFiles(dir);

		// ListView�ĵ���¼�
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dir = (File) listview.getItemAtPosition(position);
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

		toast = Toast.makeText(this, "�ٰ�һ���˳���", 0);
		
	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listFiles(dir);
		adapter.notifyDataSetChanged();//��ȡ������ʱˢ�½���
	}
	
	
	/**
	 * ��������״̬
	 */
	private void updateStatus(){
		isShowHidden=MyApplication.getConfig().getBoolean(Constants.ISSHOWHIDDEN, false);
		isDirFront=MyApplication.getConfig().getBoolean(Constants.ISDIRFRONT, false);
	}

	private void listFiles(File file) {
		updateStatus();
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
			//����ʾ�����ļ���
			if(!isShowHidden){
				CopyOnWriteArrayList<File> temp=new CopyOnWriteArrayList<File>(list);
				for(File f:temp){
					if(f.getName().startsWith(".")){
						temp.remove(f);
					}
				}
				list=new ArrayList<File>(temp);
			}
			
			//����
			if(isDirFront){
				Collections.sort(list, comparator);
			}
			list.add(0, file.getParentFile());
			adapter.setData(list);
			listview.setSelection(0);
		}
	}



	
	/**
	 * ���÷��ؼ����¼�
	 */
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

	
	/**
	 * �˳�ʱȡ��Toast����ʾ
	 */
	@Override
	protected void onDestroy() {
		toast.cancel();
		super.onDestroy();
	}

	
	/**
	 * ���ò˵�����¼�
	 */
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
	 * �����ý���
	 */
	private void setting() {
		Intent intent=new Intent(this,SettingActivity.class);
		startActivity(intent);
	}

	/**
	 * ���ò˵���
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "����");
		menu.add(0, 0, 0, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}

}
