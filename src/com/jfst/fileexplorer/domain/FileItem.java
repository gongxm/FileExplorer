package com.jfst.fileexplorer.domain;

import java.io.File;
import java.io.Serializable;

public class FileItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private File file;
	private boolean isChecked;

	public FileItem() {

	}

	public FileItem(File file, boolean isChecked) {
		this.file = file;
		this.isChecked = isChecked;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
