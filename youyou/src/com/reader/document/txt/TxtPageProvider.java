package com.reader.document.txt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class TxtPageProvider {
	TxtDocument _txtDocument;
	Activity _activity;
	String _path;
	Bitmap _bitmap;

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
		_bitmap = Bitmap.createBitmap(_activity.getWindowManager()
				.getDefaultDisplay().getWidth(), _activity.getWindowManager()
				.getDefaultDisplay().getHeight(), Config.ARGB_8888);

		_activity.setContentView(v);
		v.setBitmap( getPage(0));
		v.invalidate();
		return true;
	}

	public Bitmap getPage(int index) {
		_txtDocument.getPage(1, _bitmap);
		return _bitmap;
	}

	public int getPageCount() {
		return _txtDocument.pageCount();
	}
}
