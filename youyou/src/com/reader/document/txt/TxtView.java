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
	public int startAnimation(int flags) {
		// TODO Auto-generated method stub
		return super.startAnimation(flags);
	}

	@Override
	public int endAnimation(DIR flags) {
		if (flags == DIR.NEXT) {
			_pageindex++;
		} else {
			_pageindex--;
		}
		this._animationView.setBitmapArray(new Bitmap[] {
				_provider.getPage(_pageindex-1),
				_provider.getPage(_pageindex),
				_provider.getPage(_pageindex+1) });
		_animationView.postInvalidate();
		return super.endAnimation(flags);
	}
}