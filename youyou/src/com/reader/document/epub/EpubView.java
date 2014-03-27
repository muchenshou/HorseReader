package com.reader.document.epub;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.reader.util.BitmapInfo;
import com.reader.view.PageView;

public class EpubView extends PageView {
	EpubPageProvider _provider;
	public EpubPageAddr _pageindex;

	public EpubView(Context context, EpubPageProvider provider) {
		super(context);
		_provider = provider;
		_pageindex = provider.getPageIndexHistory();
	}

	public void setBitmap(Bitmap b[]) {
		this._animationView.setBitmapArray(new Bitmap[] { b[0], b[1], b[2] });
		_animationView.postInvalidate();
	}

	@Override
	public int startAnimation(DIR flags) {
		final DIR f = flags;
		EpubPageProvider._handle.post(new Runnable() {

			@Override
			public void run() {
				if (f == DIR.NEXT) {
					_pageindex = _pageindex.next();
				} else {
					_pageindex = _pageindex.pre();
				}
				_provider.getPage(_pageindex);
			}
		});

		return super.startAnimation(flags);
	}

	@Override
	public int endAnimation(DIR flags) {

		this._animationView.setBitmapArray(new Bitmap[] {
				_provider.getPage(_pageindex.pre()),
				_provider.getPage(_pageindex),
				_provider.getPage(_pageindex.next()) });
		_animationView.postInvalidate();
		return super.endAnimation(flags);
	}
}