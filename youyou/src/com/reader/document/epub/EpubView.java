package com.reader.document.epub;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.reader.util.BitmapInfo;
import com.reader.view.PageView;

public class EpubView extends PageView {
	EpubPageProvider _provider;
	public EpubPageAddr _pageindex ;
	private BitmapInfo _bitmapinfo = new BitmapInfo();
	public EpubView(Context context, EpubPageProvider provider) {
		super(context);
		_provider = provider;
		_pageindex = provider.getPageIndexHistory(); 
		_bitmapinfo._bitmap[0] = _provider.createBitmap();
		_bitmapinfo._bitmap[1] = _provider.createBitmap();
		_bitmapinfo._bitmap[2] = _provider.createBitmap();
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
		_bitmapinfo._addr = _pageindex;
		
		_provider.getPage(_bitmapinfo);
		this._animationView.setBitmapArray(_bitmapinfo._bitmap);
		_animationView.postInvalidate();
		return super.endAnimation(flags);
	}
}