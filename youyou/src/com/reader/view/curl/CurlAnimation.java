package com.reader.view.curl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CurlAnimation extends BookViewAnimation {
	CurlMesh mPageLeft = new CurlMesh(10);
	CurlMesh mPageRight = new CurlMesh(10);
	CurlMesh mPageCurl = new CurlMesh(10);
	// Curl meshes used for static and dynamic rendering.
	private Vector<CurlMesh> mCurlMeshes = new Vector<CurlMesh>();
	private RectF mMargins = new RectF();

	// Constants for changing view mode.
	public static final int SHOW_ONE_PAGE = 1;
	public static final int SHOW_TWO_PAGES = 2;

	// View mode.
	private int mViewMode = SHOW_ONE_PAGE;

	// Rect for render area.
	private RectF mViewRect = new RectF();
	// Page rectangles.
	private RectF mPageRectLeft = new RectF();
	private RectF mPageRectRight = new RectF();

	// Screen size.
	private int mViewportWidth, mViewportHeight;
	public CurlAnimation(BitmapSetup setup) {
		super(setup);
		mPageLeft.setFlipTexture(true);
		mPageRight.setFlipTexture(false);
	}

	@Override
	public boolean DragToRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBookView(View bookview) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSizeChange(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		mPageLeft.resetTexture();
		mPageRight.resetTexture();
		mPageCurl.resetTexture();
		
		this.mViewportWidth = w;
		this.mViewportHeight = h;
		float ratio = (float) w / h;
		mViewRect.top = 1.0f;
		mViewRect.bottom = -1.0f;
		mViewRect.left = -ratio;
		mViewRect.right = ratio;
		setMargins(0, 0, 0, 0);
		Log.i("hello","w:"+w+"h:"+h);
		//updatePageRects();
		mBitmapSetup.requestFresh();

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
		if (this.state() == NONE) {
			
			Bitmap front = this.mBitmapSetup.frontBitmap();
		
			mPageRight.setRect(mPageRectRight);
			mPageRight.setFlipTexture(false);
			mPageRight.reset();
			CurlPage page = this.mPageRight.getTexturePage();
			page.setTexture(front, CurlPage.SIDE_FRONT);
			//page.setColor(Color.rgb(180, 0, 0), CurlPage.SIDE_BOTH);
			//page.setTexture(null, CurlPage.SIDE_BOTH);
			addCurlMesh(mPageRight);
		}

		for (int i = 0; i < mCurlMeshes.size(); ++i) {
			mCurlMeshes.get(i).onDrawFrame(gl);
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	/**
	 * Adds CurlMesh to this renderer.
	 */
	private synchronized void addCurlMesh(CurlMesh mesh) {
		removeCurlMesh(mesh);
		mCurlMeshes.add(mesh);
	}

	/**
	 * Removes CurlMesh from this renderer.
	 */
	private synchronized void removeCurlMesh(CurlMesh mesh) {
		while (mCurlMeshes.remove(mesh))
			;
	}

	/**
	 * Set margins or padding. Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public synchronized void setMargins(float left, float top, float right,
			float bottom) {
		mMargins.left = left;
		mMargins.top = top;
		mMargins.right = right;
		mMargins.bottom = bottom;
		updatePageRects();
	}

	/**
	 * Sets visible page count to one or two. Should be either SHOW_ONE_PAGE or
	 * SHOW_TWO_PAGES.
	 */
	public synchronized void setViewMode(int viewmode) {
		if (viewmode == SHOW_ONE_PAGE) {
			mViewMode = viewmode;
			updatePageRects();
		} else if (viewmode == SHOW_TWO_PAGES) {
			mViewMode = viewmode;
			updatePageRects();
		}
	}

	private void updatePageRects() {
		if (mViewRect.width() == 0 || mViewRect.height() == 0) {
			return;
		} else if (mViewMode == SHOW_ONE_PAGE) {
			mPageRectRight.set(mViewRect);
			mPageRectRight.left += mViewRect.width() * mMargins.left;
			mPageRectRight.right -= mViewRect.width() * mMargins.right;
			mPageRectRight.top += mViewRect.height() * mMargins.top;
			mPageRectRight.bottom -= mViewRect.height() * mMargins.bottom;

			mPageRectLeft.set(mPageRectRight);
			mPageRectLeft.offset(-mPageRectRight.width(), 0);

			int bitmapW = (int) ((mPageRectRight.width() * mViewportWidth) / mViewRect
					.width());
			int bitmapH = (int) ((mPageRectRight.height() * mViewportHeight) / mViewRect
					.height());
			// Log.i("hello","viewRect"+mViewRect);
			// Log.i("hello","mPageRectRight"+mPageRectRight);
			// Log.i("hello","mMargin"+mMargins);
			// Log.i("hello","bitmapW:"+bitmapW+"bitmapH:"+bitmapH);
			// Log.i("hello","viewW:"+mViewportWidth+"viewH:"+mViewportHeight);
			// mObserver.onPageSizeChanged(bitmapW, bitmapH);
		} else if (mViewMode == SHOW_TWO_PAGES) {
			mPageRectRight.set(mViewRect);
			mPageRectRight.left += mViewRect.width() * mMargins.left;
			mPageRectRight.right -= mViewRect.width() * mMargins.right;
			mPageRectRight.top += mViewRect.height() * mMargins.top;
			mPageRectRight.bottom -= mViewRect.height() * mMargins.bottom;

			mPageRectLeft.set(mPageRectRight);
			mPageRectLeft.right = (mPageRectLeft.right + mPageRectLeft.left) / 2;
			mPageRectRight.left = mPageRectLeft.right;

			int bitmapW = (int) ((mPageRectRight.width() * mViewportWidth) / mViewRect
					.width());
			int bitmapH = (int) ((mPageRectRight.height() * mViewportHeight) / mViewRect
					.height());
			// mObserver.onPageSizeChanged(bitmapW, bitmapH);
		}
	}
}
