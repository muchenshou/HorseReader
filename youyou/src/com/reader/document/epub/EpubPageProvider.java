package com.reader.document.epub;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import com.reader.app.R;
import com.reader.document.txt.TxtPageProvider;

public class EpubPageProvider {
	EpubDocument _epubDocument;
	Activity _activity;
	String _path;
	EpubView _txtView;
	public static Handler _handle;

	Map<EpubPageAddr, Bitmap> _imageCache = new HashMap<EpubPageAddr, Bitmap>();

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
		_epubDocument.setBg(loadResourceBytes(R.drawable.bg1));
		_activity.setContentView(_txtView);

		g_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);
		_epubDocument.getPage(getPageIndexHistory(), g_bitmap);
		// /_txtDocument.getPage(1, g_bitmap);
		// _txtDocument.getPage(2, g_bitmap);
		// g_bitmap = getPage(0);
		EpubPageAddr addr = new EpubPageAddr(_epubDocument);
		_txtView.setBitmap(new Bitmap[] { getPage(addr), getPage(addr),
				getPage(addr) });
		_txtView.invalidate();
		return true;
	}

	Bitmap g_bitmap;
	int a = 0;

	public Bitmap getPage(final EpubPageAddr index) {
		final EpubPageAddr local_index = index;// >= getPageCount() ?
												// getPageCount()-1:index;
		_handle.post(new Runnable() {

			@Override
			public void run() {
				synchronized (_imageCache) {
					Log.i("song","epub1:"+local_index);
					savePageIndexHistory();
					System.gc();
					Set<EpubPageAddr> needInCachePages = new HashSet<EpubPageAddr>();
					needInCachePages.add(local_index);
					final int count = 5;
					EpubPageAddr pre = local_index.pre();
					
					for (int i = 0; i < count / 2; i++) {
						needInCachePages.add(pre);
						pre = pre.pre();
					}
					Log.i("song","epub2:"+pre.toString());
					EpubPageAddr next = local_index.next();
					for (int i = 0; i < count / 2; i++) {
						needInCachePages.add(next);
						next = next.next();
					}
					Log.i("song",next.toString());
					Set<EpubPageAddr> remove_pages = new HashSet<EpubPageAddr>();

					Set<Entry<EpubPageAddr, Bitmap>> set = _imageCache
							.entrySet();
					Iterator<Entry<EpubPageAddr, Bitmap>> iter = set.iterator();
					Log.i("song","epub3");
					while (iter.hasNext()) {
						Entry<EpubPageAddr, Bitmap> entry = iter.next();
						EpubPageAddr key = entry.getKey();
						Bitmap value = entry.getValue();
						if (!needInCachePages.contains(key)) {
							value.recycle();
							remove_pages.add(key);
						}
					}
					Log.i("song","epub4");
					for (EpubPageAddr i : remove_pages) {
						Bitmap b = _imageCache.remove(i);
						if (b == null) {
							Log.i("song","remove fail");
						} else {
							Log.i("song", "remove ok");
						}
					}
					Log.i("song","epub5");
					for (EpubPageAddr a : needInCachePages) {
						if (!_imageCache.containsKey(a)) {
							Bitmap b;
							b = Bitmap.createBitmap(_activity
									.getWindowManager().getDefaultDisplay()
									.getWidth(), _activity.getWindowManager()
									.getDefaultDisplay().getHeight(),
									Config.RGB_565);
							_epubDocument.getPage(a, b);
							_imageCache.put(a, b);
						}
					}
				}
			}
		});
		Bitmap _bitmap;
		Log.i("song", "_bitmap:"+local_index._chapter_index +":"+local_index._page_index);
		if (_imageCache.get(local_index) != null) {
			return _imageCache.get(local_index);
		}
		Log.i("song","ddddd");
		_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);
		_epubDocument.getPage(local_index, _bitmap);

		return _bitmap;
	}

	public int getPageCount() {
		return _epubDocument.pageCount();
	}

	EpubPageAddr getPageIndexHistory() {
		SharedPreferences sp = _activity.getSharedPreferences("epub_history",
				Activity.MODE_PRIVATE);
		int v = sp.getInt(_path, 0);
		EpubPageAddr addr = new EpubPageAddr(_epubDocument);
		addr._chapter_index = v >> 16;
		addr._page_index = 0x0000FFFF & v;
		addr._chapter_index = 0;
		addr._page_index = 0;
		return addr;
	}

	void savePageIndexHistory() {
//		SharedPreferences sp = _activity.getSharedPreferences("epub_history",
//				Activity.MODE_PRIVATE);
//		Editor e = sp.edit();
//		e.putInt(
//				_path,
//				_txtView._pageindex._chapter_index << 16 + _txtView._pageindex._page_index);
//		;
//		e.commit();
	}

	public byte[] loadResourceBytes(int id) {
		try {
			InputStream is = _activity.getResources().openRawResource(id);
			return TxtPageProvider.loadResourceBytes(is);
		} catch (Exception e) {
			Log.e("youyou", "cannot load resource");
			return null;
		}

	}
}
