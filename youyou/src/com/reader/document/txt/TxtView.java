package com.reader.document.txt;

import android.content.Context;
import android.graphics.Bitmap;

import com.reader.app.R;
import com.reader.view.PageView;

public class TxtView extends PageView {
	TxtPageProvider _provider;
	
	public TxtView(Context context, TxtPageProvider provider) {
		super(context);
		_provider = provider;
	}

	public void setBitmap(Bitmap b[]) {
		this._animationView.setBitmapArray(new Bitmap[] { b[0], b[1], b[2] });
		_animationView.postInvalidate();
	}

	@Override
	public int startAnimation(DIR flags) {
		final DIR f = flags;
		TxtPageProvider._handle.post(new Runnable() {
			
			@Override
			public void run() {
				if (f == DIR.NEXT) {
					_pageindex++;
				} else {
					_pageindex--;
				}
				_provider.getPage(_pageindex);
			}
		});
		return super.startAnimation(flags);
	}

	@Override
	public int endAnimation(DIR flags) {
		
		this._animationView.setBitmapArray(new Bitmap[] {
				_provider.getPage(_pageindex-1),
				_provider.getPage(_pageindex),
				_provider.getPage(_pageindex+1) });
		_animationView.postInvalidate();
		return super.endAnimation(flags);
	}
}