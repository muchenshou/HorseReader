package com.reader.document.epub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reader.document.txt.TxtDocument;
import com.reader.document.txt.TxtView;

public class EpubPageProvider {
	EpubDocument _epubDocument;
	Activity _activity;
	String _path;
	EpubView _txtView;
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

	public EpubPageProvider(Activity activity, String path) {
		// TODO Auto-generated constructor stub
		_activity = activity;
		_path = path;
	}

	public boolean loadDocument() {
		_epubDocument = new EpubDocument();
		_epubDocument.loadDocument(_path, _activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight());
		_txtView = new EpubView(_activity, this);
		new BitmapThread().start();
		while (_handle == null) {
			;
		}

		_activity.setContentView(_txtView);

		g_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);
		_epubDocument.getPage(getPageIndexHistory(), g_bitmap);
		///_txtDocument.getPage(1, g_bitmap);
		// _txtDocument.getPage(2, g_bitmap);
		// g_bitmap = getPage(0);
		_txtView.setBitmap(new Bitmap[]{getPage(0),getPage(0),getPage(0)});
		_txtView.invalidate();
		return true;
	}

	Bitmap g_bitmap;
	int a = 0;

	public Bitmap getPage(final int index) {
		final int local_index = index >= getPageCount() ? getPageCount()-1:index;
		
		_handle.post(new Runnable() {

			@Override
			public void run() {
				synchronized (_imageCache) {
					
					savePageIndexHistory();
					Log.i("song", "thread id " + Thread.currentThread().getId());
					System.gc();
					List<Integer> list = new ArrayList<Integer>();
					final int count = 5;
					Set<Entry<Integer, Bitmap>> set = _imageCache.entrySet();
					Iterator<Entry<Integer, Bitmap>> iter = set.iterator();
					while (iter.hasNext()) {
						Entry<Integer, Bitmap> entry = iter.next();
						Integer key = entry.getKey();
						Bitmap value = entry.getValue();
						if (key < local_index - count || key > local_index + count) {
							value.recycle();
							list.add(key);
						}
					}
					for (Integer i : list) {
						_imageCache.remove(i);
					}
					int min = local_index - count > 0 ? local_index - count : 0;
					// must fix in future
					int max = local_index + count;
					for (int i = min; i < max; i++) {
						if (_imageCache.get(i) == null) {
							Bitmap b;
							b = Bitmap.createBitmap(_activity
									.getWindowManager().getDefaultDisplay()
									.getWidth(), _activity.getWindowManager()
									.getDefaultDisplay().getHeight(),
									Config.RGB_565);
							_epubDocument.getPage(i, b);
							_imageCache.put(i, b);
						}
					}
				}
			}
		});
		Bitmap _bitmap;
		Log.i("song", "bitmap " + local_index);
		if (_imageCache.get(local_index) != null) {
			Log.i("song", "hello world 1");
			{
				Log.i("song", "hello world");
				return _imageCache.get(local_index);
			}
		}
		_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);
		_epubDocument.getPage(local_index, _bitmap);
		
		return _bitmap;
	}

	public int getPageCount() {
		Log.i("song","getpagecount:"+_epubDocument.pageCount());
		return _epubDocument.pageCount();
	}
	
	int getPageIndexHistory() {
		SharedPreferences sp = _activity.getSharedPreferences("txt_history", Activity.MODE_PRIVATE);
		String s = sp.getString("epub", "0");
		return Integer.decode(s);
	}
	void savePageIndexHistory() {
//		SharedPreferences sp = _activity.getSharedPreferences("txt_history", Activity.MODE_PRIVATE);
//		Editor e = sp.edit();
//		e.putString("epub",""+_txtView._pageindex);
//		e.commit();
	}
}
