package com.jfst.fileexplorer.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfst.fileexplorer.R;
import com.jfst.fileexplorer.domain.FileItem;

public class FileAdapter extends BaseAdapter {
	private Context context;
	private List<FileItem> list;

	public void setData(List<FileItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public FileAdapter(Context context, List<FileItem> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final FileItem item = list.get(position);
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.file_item, null);
			holder = new ViewHolder();
			holder.iv_file = (ImageView) convertView.findViewById(R.id.iv_file);
			holder.tv_file = (TextView) convertView.findViewById(R.id.tv_file);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.cb_list);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		File file = item.getFile();
		if (file == null) {
			holder.iv_file.setImageResource(R.drawable.back);
		} else if (file.isDirectory()) {
			holder.iv_file.setImageResource(R.drawable.dir);
		} else if (file.getName().endsWith(".txt")) {
			holder.iv_file.setImageResource(R.drawable.txt);
		} else if (file.getName().endsWith(".bmp")) {
			holder.iv_file.setImageResource(R.drawable.bmp);
		} else if (file.getName().endsWith(".gif")) {
			holder.iv_file.setImageResource(R.drawable.gif);
		} else if (file.getName().endsWith(".jpg")
				|| file.getName().endsWith(".jpeg")) {
			holder.iv_file.setImageResource(R.drawable.jpeg);
		} else if (file.getName().endsWith(".mp3")) {
			holder.iv_file.setImageResource(R.drawable.mp3);
		} else if (file.getName().endsWith(".png")) {
			holder.iv_file.setImageResource(R.drawable.png);
		} else if (file.getName().endsWith(".rar")) {
			holder.iv_file.setImageResource(R.drawable.rar);
		} else if (file.getName().endsWith(".zip")) {
			holder.iv_file.setImageResource(R.drawable.zip);
		} else if (file.getName().endsWith(".apk")) {
			holder.iv_file.setImageResource(R.drawable.apk);
		} else if (file.getName().endsWith(".java")) {
			holder.iv_file.setImageResource(R.drawable.java);
		} else if (file.getName().endsWith(".class")) {
			holder.iv_file.setImageResource(R.drawable.class2);
		} else if (file.getName().endsWith(".pdf")) {
			holder.iv_file.setImageResource(R.drawable.pdf);
		} else if (file.getName().endsWith(".jar")) {
			holder.iv_file.setImageResource(R.drawable.jar);
		} else if (file.getName().endsWith(".xml")) {
			holder.iv_file.setImageResource(R.drawable.xml);
		} else if (file.getName().endsWith(".html")) {
			holder.iv_file.setImageResource(R.drawable.html);
		} else if (file.getName().endsWith(".doc")) {
			holder.iv_file.setImageResource(R.drawable.doc);
		} else if (file.getName().endsWith(".mkv")) {
			holder.iv_file.setImageResource(R.drawable.mkv);
		} else if (file.getName().endsWith(".wmv")) {
			holder.iv_file.setImageResource(R.drawable.wmv);
		} else if (file.getName().endsWith(".wma")) {
			holder.iv_file.setImageResource(R.drawable.wma);
		} else if (file.getName().endsWith(".avi")) {
			holder.iv_file.setImageResource(R.drawable.avi);
		} else if (file.getName().endsWith(".mov")) {
			holder.iv_file.setImageResource(R.drawable.mov);
		} else if (file.getName().endsWith(".mp4")) {
			holder.iv_file.setImageResource(R.drawable.mp4);
		} else if (file.getName().endsWith(".ape")) {
			holder.iv_file.setImageResource(R.drawable.ape);
		} else if (file.getName().endsWith(".flac")) {
			holder.iv_file.setImageResource(R.drawable.flac);
		} else {
			holder.iv_file.setImageResource(R.drawable.unknown);
		}
		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked) {
				item.setChecked(isChecked);
			}
		});
		holder.checkbox.setChecked(item.isChecked());
		if (position == 0) {
			if (file == null) {
				holder.iv_file.setImageResource(R.drawable.root);
				holder.tv_file.setText("根目录");
			} else {
				holder.iv_file.setImageResource(R.drawable.back);
				holder.tv_file.setText("返回上一层");
			}
			holder.checkbox.setVisibility(View.INVISIBLE);
		} else {
			holder.tv_file.setText(file.getName());
			holder.checkbox.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView iv_file;
		TextView tv_file;
		CheckBox checkbox;
	}

}
