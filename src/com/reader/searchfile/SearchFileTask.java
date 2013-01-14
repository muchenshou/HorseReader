package com.reader.searchfile;

import com.reader.record.BookLibrary;
import com.reader.searchfile.service.ISearchFileService;
import com.reader.searchfile.service.ISearchFileServiceCallBack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class SearchFileTask extends AsyncTask<Void, Void, Void> {
	public interface SearchFileTaskCallBack{
		public void findOneFile(String file);
	}
	private ISearchFileService mService = null;
	private Intent mServiceIntent;
	private SearchFileTask.SearchFileTaskCallBack mTaskCallTask;
	BookLibrary mBookLib;
	private final ISearchFileServiceCallBack mServiceCallBack = new ISearchFileServiceCallBack.Stub() {
		public void findOneFile(String file) throws RemoteException {
			mTaskCallTask.findOneFile(file);
			mBookLib.addBook(file);
		}
	};
	private final ServiceConnection mConnect = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			try {
				mService.unregisterCallback(mServiceCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mService = null;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ISearchFileService.Stub.asInterface(service);
			try {
				mService.registerCallback(mServiceCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	private Context mContext;
	/**
	 * @param context
	 * @param cl
	 */
	public SearchFileTask(Context context,SearchFileTask.SearchFileTaskCallBack cl, BookLibrary lib) {
		mContext = context;
		mTaskCallTask = cl;
		mBookLib = lib;
	}
	private boolean mBound;
	@Override
	protected void onPreExecute() {
		mServiceIntent = new Intent(ISearchFileService.class.getName());
		ComponentName comp = mContext.startService(mServiceIntent);
		if (comp == null) {
			mBound = false;
		} else {
			mBound = mContext.bindService(mServiceIntent, mConnect, 0);
		}
		super.onPreExecute();
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			Log.i("_____----","doinbackground");
			while (mService == null) {
                // Wait till the Service is bound
            }
			if (!mBound)
				return null;
			mService.searchFileForExts(new String[]{"txt","umd"});
			Log.i("_____----","doinbackgrou222nd");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		Toast.makeText(mContext, "finish", Toast.LENGTH_LONG).show();
		super.onPostExecute(result);
	}
}
