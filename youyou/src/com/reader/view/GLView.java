package com.reader.view;

import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_LEQUAL;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_NICEST;
import static javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.microedition.khronos.opengles.GL10.GL_PROJECTION;
import static javax.microedition.khronos.opengles.GL10.GL_SMOOTH;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.view.MotionEvent;
import android.view.View;

import com.reader.config.PageConfig;
import com.reader.view.curl.BookViewAnimation;

public class GLView extends GLSurfaceView implements View.OnTouchListener,
		Renderer {

	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	private BookViewAnimation mAnimation;
	public Bitmap m_book_bg = null;
	private boolean mInit = false;
	private Paint mPaint;

	// Rect for render area.
	private RectF mViewRect = new RectF();

	public GLView(Context context) {
		super(context);
		this.setRenderer(this);
		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		getHolder().setFormat(PixelFormat.RGB_565);

		setOnTouchListener(this);
		mPaint = PageConfig.pagePaintFromConfig(false);

		// this.mAnimation = new SimulateTurnPage(getContext());
		// this.mAnimation = new NoTurnAnimation(getContext());
//		this.mAnimation = new CurlAnimation(this);
		this.mAnimation.setBookView(this);
	}

	public Paint getPaint() {
		return this.mPaint;
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

	protected void sizeChanged(int w, int h) {
		this.mAnimation.onSizeChange(w, h, 0, 0);


		Bitmap BG = this.m_book_bg;
		int bitmap_w = BG.getWidth();
		int bitmap_h = BG.getHeight();
		Matrix m = new Matrix();
		m.postScale((float) w / (float) bitmap_w, (float) h / (float) bitmap_h);

		this.reset();
	}

	private void reset() {
		if (this.mInit == false) {
			// mBookContent.setCurPosition(mBook.openOffset);
			mInit = true;
		}
		this.mAnimation.update();
		postInvalidate();
	}

	long filterPoint = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Rect rect = new Rect(0, 0, v.getWidth(), v.getHeight());
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getX() < rect.exactCenterX() + 40
				&& event.getX() > rect.exactCenterX() - 40
				&& event.getY() > rect.exactCenterY() - 20
				&& event.getY() < rect.exactCenterY() + 20) { // gridview
			return false;
		}

		mAnimation.onTouch(v, event);
		requestRender();
		return true;
	}

	public void setTurnAnimation(BookViewAnimation noTurnAnimation) {
		this.mAnimation = noTurnAnimation;
		mAnimation.setBookView(this);
		this.mAnimation.onSizeChange(getWidth(), getHeight(), 0, 0);
	}


	@Override
	public void onDrawFrame(GL10 gl) {
		mAnimation.onDrawFrame(gl);
	}

	public static float[] light0Position = { 0, 0, 100f, 0f };

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		mViewRect.top = 1.0f;
		mViewRect.bottom = -1.0f;
		mViewRect.left = -ratio;
		mViewRect.right = ratio;

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, mViewRect.left, mViewRect.right, mViewRect.bottom,
				mViewRect.top);

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		sizeChanged(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	Bitmap bb;
}
