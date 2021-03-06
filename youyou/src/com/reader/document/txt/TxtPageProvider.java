package com.reader.document.txt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.reader.app.R;
import com.reader.util.DisplayUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TxtPageProvider {
	TxtDocument _txtDocument;
	Activity _activity;
	String _path;
	TxtView _txtView;
	public static Handler _handle;

	Map<Integer, Bitmap> _imageCache = new HashMap<Integer, Bitmap>();
	Bitmap bg;
	
	class BitmapThread extends Thread {

		@Override
		public void run() {
			Looper.prepare();
			_handle = new Handler();
			Looper.loop();
		}

	}

	public TxtPageProvider(Activity activity, String path) {
		// TODO Auto-generated constructor stub
		_activity = activity;
		_path = path;
	}
	public byte[] loadResourceBytes(int id) {
		try {
			InputStream is = _activity.getResources().openRawResource(id);
			return loadResourceBytes(is);
		} catch (Exception e) {
			Log.e("youyou","cannot load resource");
			return null;
		}
	}
	public static byte[] loadResourceBytes(InputStream is) {
		try {
			int available = is.available();
			if (available <= 0)
				return null;
			byte buf[] = new byte[available];
			if (is.read(buf) != available)
				throw new IOException("Resource not read fully");
			is.close();
			return buf;
		} catch (Exception e) {
			Log.e("youyou","cannot load resource");
			return null;
		}
	}
	public boolean loadDocument() {
		_txtDocument = new TxtDocument();
		Log.i("hello","song size:"+DisplayUtil.sp2px(_activity, 20));
		clear();
		_txtDocument.loadDocument(_path, _activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(),DisplayUtil.sp2px(_activity, 20));
		bg = BitmapFactory.decodeResource(_activity.getResources(), R.drawable.bg1);
		_txtDocument.setBg(loadResourceBytes(R.drawable.bg1));
		_txtView = new TxtView(_activity, this);
		new BitmapThread().start();
		while (_handle == null) {
			;
		}
		System.gc();
		_activity.setContentView(_txtView);

		g_bitmap =Bitmap.createScaledBitmap(bg, _activity
				.getWindowManager().getDefaultDisplay()
				.getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(),true);
		_txtDocument.getPage(getPageIndexHistory(), g_bitmap);
		int index = getPageIndexHistory();
		_txtView._pageindex = index;
		_txtView.setBitmap(new Bitmap[] { getPage(index - 1), getPage(index),
				getPage(index + 1) });
		_txtView.invalidate();
		return true;
	}

	Bitmap g_bitmap;
	int a = 0;
	
	private void clear() {
		List<Integer> list = new ArrayList<Integer>();
		Set<Entry<Integer, Bitmap>> set = _imageCache.entrySet();
		Iterator<Entry<Integer, Bitmap>> iter = set.iterator();
		while (iter.hasNext()) {
			Entry<Integer, Bitmap> entry = iter.next();
			Integer key = entry.getKey();
			Bitmap value = entry.getValue();
			value.recycle();
			list.add(key);
		}
		for (Integer i : list) {
			_imageCache.remove(i);
		}
		System.gc();
	}
	public Bitmap getPage(final int index) {
		if (index > _txtDocument.pageCount() - 1 || index < 0) {
			return null;
		}
		_handle.post(new Runnable() {

			@Override
			public void run() {
				synchronized (_imageCache) {
					savePageIndexHistory();
					// Log.i("song", "thread id " +
					// Thread.currentThread().getId());
					System.gc();
					List<Integer> list = new ArrayList<Integer>();
					final int count = 3;
					Set<Entry<Integer, Bitmap>> set = _imageCache.entrySet();
					Iterator<Entry<Integer, Bitmap>> iter = set.iterator();
					while (iter.hasNext()) {
						Entry<Integer, Bitmap> entry = iter.next();
						Integer key = entry.getKey();
						Bitmap value = entry.getValue();
						if (key < index - count || key > index + count) {
							value.recycle();
							list.add(key);
						}
					}
					for (Integer i : list) {
						_imageCache.remove(i);
					}
					int min = index - count > 0 ? index - count : 0;
					// must fix it in future
					int max = _txtDocument.pageCount() < index + count ? _txtDocument
							.pageCount() : index + count;
					for (int i = min; i < max; i++) {
						if (_imageCache.get(i) == null) {
							Bitmap b;
							b = Bitmap.createBitmap(_activity
									.getWindowManager().getDefaultDisplay()
									.getWidth(), _activity.getWindowManager()
									.getDefaultDisplay().getHeight(),
									Config.ARGB_8888);

//							b = Bitmap.createScaledBitmap(bg, _activity
//									.getWindowManager().getDefaultDisplay()
//									.getWidth(), _activity.getWindowManager()
//									.getDefaultDisplay().getHeight(),true);
							
							_txtDocument.getPage(i, b);

							_imageCache.put(i, b);
						}
					}
				}
			}
		});
		Bitmap _bitmap;
		Log.i("song", "bitmap " + index);
		if (_imageCache.get(index) != null) {
			{
				return _imageCache.get(index);
			}
		}
		_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);
		_txtDocument.getPage(index, _bitmap);

		return _bitmap;
	}

	public int getPageCount() {
		return _txtDocument.pageCount();
	}

	int getPageIndexHistory() {
		SharedPreferences sp = _activity.getSharedPreferences("txt_history",
				Activity.MODE_PRIVATE);
		String s = sp.getString(_path, "0");
//		String s = sp.getString("a", "0");
		return Integer.decode(s);
	}

	void savePageIndexHistory() {
		SharedPreferences sp = _activity.getSharedPreferences("txt_history",
				Activity.MODE_PRIVATE);
		Editor e = sp.edit();
		e.putString(_path, "" + _txtView._pageindex);
//		e.putString("a", "" + _txtView._pageindex);
		e.commit();
	}
}
