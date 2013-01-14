package com.reader.searchfile.service;

import java.io.File;
import java.io.FilenameFilter;

import com.reader.util.FilenameExtFilter;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class SearchFileService extends Service {
	private final ISearchFileService.Stub mBinder = new ISearchFileService.Stub() {

		public void unregisterCallback(ISearchFileServiceCallBack cb)
				throws RemoteException {
			if (cb != null) {
				mCallBack.unregister(cb);
			}
		}

		public void searchFileForExts(String[] exts) throws RemoteException {
			FilenameExtFilter fef = new FilenameExtFilter(exts);
			Log.i("2222", "33333333");
			searchBookFromDir(new File(Environment
					.getExternalStorageDirectory().getPath()), fef);

		}

		private void searchBookFromDir(File file, FilenameFilter filter) {
			File[] array = file.listFiles(filter);
			if (array == null) {
				return;
			}
			for (int i = 0; i < array.length; i++) {
				if (array[i].isDirectory() == true) {
					searchBookFromDir(array[i], filter);
				} else {
					findOneFile(array[i].getPath());
				}
			}
		}

		private void findOneFile(String file) {
			Log.i("TAG", "ddddd");
			int M = mCallBack.beginBroadcast();
			for (int i = 0; i < M; i++) {
				try {
					mCallBack.getBroadcastItem(i).findOneFile(file);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			mCallBack.finishBroadcast();
		}

		public void registerCallback(ISearchFileServiceCallBack cb)
				throws RemoteException {
			Log.i("2222", "3333333113");
			if (cb != null) {
				mCallBack.register(cb);
			}
		}
	};
	
	private final RemoteCallbackList<ISearchFileServiceCallBack> mCallBack = new RemoteCallbackList<ISearchFileServiceCallBack>();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
