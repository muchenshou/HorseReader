/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.Reader.Fragment.LocalFileListFragment;
import com.Reader.Record.BookLibrary;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class SearchBook extends AsyncTask<String, Integer, List<String>> {
	ProgressDialog mDialog;
	BookLibrary mBookLib;
	LinkedList<String> mList = new LinkedList<String>();
	private LocalFileListFragment mFileManager;
	public SearchBook(ProgressDialog dialog, BookLibrary con, LocalFileListFragment fileManager){
		this.mDialog = dialog;
		this.mBookLib = con;
		this.mFileManager = fileManager;
	}
	public List<String> getList(){
		return mList;
	}
	@Override
	protected List<String> doInBackground(String... params) {
		// TODO Auto-generated method stub
		mList = new LinkedList<String>();
		searchBookFromDir(mList, new File("/sdcard/"));
		return mList;
	}
	@Override
	protected void onPostExecute(List<String> result) {
		// doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
		// 这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"
		this.mDialog.dismiss();
		this.mFileManager.setListViewContent();
		super.onPostExecute(result);
	}
	public static boolean isBook(File file) {
		return file.toString().substring(file.toString().lastIndexOf('.') + 1)
				.toLowerCase().equals("umd")
				|| file.toString().substring(
						file.toString().lastIndexOf('.') + 1).toLowerCase()
						.equals("txt");
	}

	private void searchBookFromDir(List<String> list, File file) {
		File[] array = file.listFiles();
		if (array == null) {
			return;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i].isDirectory() == true) {
				searchBookFromDir(list, array[i]);
			} else {
				if (isBook(array[i]) == true) {
					list.add(array[i].toString());
					this.mBookLib.addBook(array[i].getPath());
				}
			}
		}
	}

}
