package com.reader.document.epub;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.reader.view.PageView;

public class EpubView extends PageView {
	EpubPageProvider _provider;
	public EpubPageAddr _pageindex ;

	public EpubView(Context context, EpubPageProvider provider) {
		super(context);
		_provider = provider;
		_pageindex = new EpubPageAddr(_provider._epubDocument);
		_pageindex._chapter_index = 0;
		_pageindex._page_index = 0;
	}

	public void setBitmap(Bitmap b[]) {
		this._animationView.setBitmapArray(new Bitmap[] { b[0], b[1], b[2] });
		_animationView.postInvalidate();
	}

	@Override
	public int startAnimation(int flags) {
		// TODO Auto-generated method stub
		return super.startAnimation(flags);
	}

	@Override
	public int endAnimation(DIR flags) {
		if (flags == DIR.NEXT) {
			_pageindex = _pageindex.next();
		} else {
			_pageindex = _pageindex.pre();
		}
		Log.i("song","endAnimation:"+"pre:"+_pageindex.pre().toString() + "cur:"+_pageindex.toString()+"next:"+_pageindex.next().toString());
		this._animationView.setBitmapArray(new Bitmap[] {
				_provider.getPage(_pageindex.pre()),
				_provider.getPage(_pageindex),
				_provider.getPage(_pageindex.next()) });
		_animationView.postInvalidate();
		return super.endAnimation(flags);
	}
}