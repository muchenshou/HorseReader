package com.reader.document.txt;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

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
	static Handler _handle;
	Map<Integer, SoftReference<Bitmap>> _imageCache = new HashMap<Integer, SoftReference<Bitmap>>();

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
		v.setBitmap(getPage(0));
		v.invalidate();
		return true;
	}

	public Bitmap getPage(final int index) {
		Bitmap _bi;
		_handle.post(new Runnable() {

			@Override
			public void run() {
				for (int i = index; i < index+10; i++) {
					Bitmap _bitmap;
					_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
							.getDefaultDisplay().getWidth(),
							_activity.getWindowManager().getDefaultDisplay()
									.getHeight(), Config.ARGB_8888);
					if (_imageCache.get(i) == null) {
						_txtDocument.getPage(i, _bitmap);
						_imageCache.put(i, new SoftReference<Bitmap>(_bitmap));
						if (_imageCache.get(i).get() == null) {
							_imageCache.put(i, new SoftReference<Bitmap>(_bitmap));
						}
					}
				}

			}
		});
		if (_imageCache.get(index) != null) {
			if (_imageCache.get(index).get() != null)
				return _imageCache.get(index).get();
		}
		_bi = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(),
				_activity.getWindowManager().getDefaultDisplay()
					.getHeight(), Config.ARGB_8888);
		_txtDocument.getPage(index, _bi);
		return _bi;
	}

	public int getPageCount() {
		return _txtDocument.pageCount();
	}
}
