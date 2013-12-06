package com.reader.document.txt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TxtPageProvider {
	TxtDocument _txtDocument;
	Activity _activity;
	String _path;
	public static Handler _handle;

	Map<Integer, Bitmap> _imageCache = new HashMap<Integer, Bitmap>();

	class BitmapThread extends Thread {

		@Override
		public void run() {
			// /强引用的Bitmap对象
			// Bitmap bitmap = BitmapFactory.decodeStream(InputStream);
			// //软引用的Bitmap对象
			// SoftReference<Bitmap> bitmapcache = new
			// SoftReference<Bitmap>(bitmap);
			// //添加该对象到Map中使其缓存
			// imageCache.put("1",softRbitmap);
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

	public boolean loadDocument() {
		_txtDocument = new TxtDocument();
		_txtDocument.loadDocument(_path, _activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight());
		Log.i("hello", "here");
		TxtView v = new TxtView(_activity, this);
		new BitmapThread().start();
		while (_handle == null) {
			;
		}

		_activity.setContentView(v);

		g_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);
		_txtDocument.getPage(0, g_bitmap);
		// _txtDocument.getPage(1, g_bitmap);
		// _txtDocument.getPage(2, g_bitmap);
		// g_bitmap = getPage(0);
		v.setBitmap(g_bitmap);
		v.invalidate();
		return true;
	}

	Bitmap g_bitmap;
	int a = 0;

	public Bitmap getPage(final int index) {
		_handle.post(new Runnable() {

			@Override
			public void run() {
				synchronized (_imageCache) {
					Log.i("song", "thread id " + Thread.currentThread().getId());
					System.gc();
					List<Integer> list = new ArrayList<Integer>();
					final int count = 10;
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
					// must fix in future
					int max = index + count;
					for (int i = min; i < max; i++) {
						if (_imageCache.get(i) == null) {
							Bitmap b;
							b = Bitmap.createBitmap(_activity
									.getWindowManager().getDefaultDisplay()
									.getWidth(), _activity.getWindowManager()
									.getDefaultDisplay().getHeight(),
									Config.ARGB_8888);
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
			Log.i("song", "hello world 1");
			{
				Log.i("song", "hello world");
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
}
