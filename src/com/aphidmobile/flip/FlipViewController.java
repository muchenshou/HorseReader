package com.aphidmobile.flip;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aphidmobile.utils.AphidLog;
import com.reader.book.Book;
import com.reader.book.bookview.BookView;
import com.reader.book.bookview.BookViewAnimation;
import com.reader.book.manager.BookContent;
import com.reader.config.PageConfig;
import com.reader.main.R;

/*
Copyright 2012 Aphid Mobile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

public class FlipViewController extends ViewGroup {
	
	private static final int MSG_SURFACE_CREATED = 1;

	private LinkedList<View> flipViews = new LinkedList<View>();
	
	private Handler handler = new Handler(new Handler.Callback() {		
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_SURFACE_CREATED) {
				width = 0;
				height = 0;
				requestLayout();
				return true;
			}
			return false;
		}
	});

	private GLSurfaceView surfaceView;
	private FlipRenderer renderer;

	private int width;
	private int height;

	private boolean flipping = false;

	public BookContent mBookContent;
	private BookViewAnimation mAnimation;
	public Bitmap m_book_bg = null;
	Book mBook;
	private PageConfig mPageConfig;
	private Paint mPaint;
	public FlipViewController(Context context, Book book) {
		super(context);
		setupSurfaceView();
		mPageConfig = new PageConfig(context);
		mPaint = mPageConfig.getPaint();
		mBookContent = new BookContent(book, mPageConfig);
		addFlipView(View.inflate(context, R.layout.readingview, null));
		addFlipView(View.inflate(context, R.layout.readingview, null));
		flipping = true;
	}

	private void setupSurfaceView() {
		surfaceView = new GLSurfaceView(getContext());
		
		renderer = new FlipRenderer(this);
		
		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setZOrderOnTop(true);
		surfaceView.setRenderer(renderer);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		addView(surfaceView);
	}

	public GLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	public FlipRenderer getRenderer() {
		return renderer;
	}

	public void addFlipView(View v) {
		flipViews.add(v);
		addView(v);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		AphidLog.i("onLayout: %d, %d, %d, %d; child %d", l, t, r, b, flipViews.size());
		Log.i("hello","onLayout"+flipViews.size());
		for (View child : flipViews)
			child.layout(0, 0, r - l, b - t);

		if (changed || width == 0) {
			int w = r - l;
			int h = b - t;
			surfaceView.layout(0, 0, w, h);
			
			if (width != w || height != h) {
				width = w;
				height = h;

				if (flipping && flipViews.size() >= 2) {
					View frontView = flipViews.get(flipViews.size() - 1);
					View backView = flipViews.get(flipViews.size() - 2);
					renderer.updateTexture(frontView, backView);
					frontView.setVisibility(View.INVISIBLE);
					backView.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Logger.i( String.format("onMeasure: %d, %d, ; child %d", widthMeasureSpec, heightMeasureSpec, flipViews.size()));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.i("hello","onMeasure"+flipViews.size());
		for (View child : flipViews)
			child.measure(widthMeasureSpec, heightMeasureSpec);
	}

	public void startFlipping() {
		flipping = true;
	}
	
	public void onResume() {
		surfaceView.onResume();
	}

	public void onPause() {
		surfaceView.onPause();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("hello","onsize");
		mBookContent.update(w - 20,
				h - BookView.getTextHeight(this.mPageConfig.getOthersPaint())
						- 20);
		TextView tv = (TextView)flipViews.get(1).findViewById(R.id.content);
		tv.setText("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
		tv = (TextView)flipViews.get(0).findViewById(R.id.content);
		tv.setText("333333333333333333333333333333333333asfdfasfadsfasfsda");
	}

	public void reloadTexture() {
		handler.sendMessage(Message.obtain(handler, MSG_SURFACE_CREATED));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return renderer.getCards().handleTouchEvent(event);
	}
}
