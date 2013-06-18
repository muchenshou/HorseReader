package com.reader.view;

import static javax.microedition.khronos.opengles.GL10.GL_AMBIENT;
import static javax.microedition.khronos.opengles.GL10.GL_BACK;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_CCW;
import static javax.microedition.khronos.opengles.GL10.GL_CLAMP_TO_EDGE;
import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_CULL_FACE;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_LEQUAL;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHT0;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_NICEST;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.microedition.khronos.opengles.GL10.GL_POSITION;
import static javax.microedition.khronos.opengles.GL10.GL_PROJECTION;
import static javax.microedition.khronos.opengles.GL10.GL_SMOOTH;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_S;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_T;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT;
import static javax.microedition.khronos.opengles.GL10.GL_VERTEX_ARRAY;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.reader.book.Book;
import com.reader.book.manager.PageProvider;
import com.reader.book.manager.BookManager;
import com.reader.config.PageConfig;
import com.reader.ui.gl.MyGLUtils;
import com.reader.ui.gl.Texture;
import com.reader.view.curl.BookViewAnimation;
import com.reader.view.curl.SimpleAnimation;

public class GLView extends GLSurfaceView implements View.OnTouchListener,
		Renderer {

	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	public PageProvider mBookContent;
	private BookViewAnimation mAnimation;
	public Bitmap m_book_bg = null;
	Book mBook;
	private boolean mInit = false;
	BookScreenDisplay mBookScreenDisplay;
	private Paint mPaint;
	private Bitmap mDisplayBitmap;

	float textureCoordinates[];
	FloatBuffer textureFloatBuffer;

	private short[] indices = { 0, 1, 2, 0, 2, 3 };
	private ShortBuffer indicesBuffer;
	private FloatBuffer vertexBuffer;

	public GLView(Context context, Book book) {
		super(context);
		this.setRenderer(this);
		getHolder().setFormat(PixelFormat.RGB_565);
		
		BookManager.Instance = this;
		setOnTouchListener(this);
		this.mBook = book;
		mPaint = PageConfig.pagePaintFromConfig(false);
		mBookContent = new PageProvider(book);

		indicesBuffer = MyGLUtils.toShortBuffer(indices);

		mBookScreenDisplay = new BookScreenDisplay(mBookContent);
		// this.mAnimation = new SimulateTurnPage(getContext());
		// this.mAnimation = new NoTurnAnimation(getContext());
		this.mAnimation = new SimpleAnimation(getContext());
		this.mAnimation.setBookView(this);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}

	public Paint getPaint() {
		return this.mPaint;
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

	protected void sizeChanged(int w, int h) {
		this.mAnimation.onSizeChange(w, h, 0, 0);
		Log.i("hello","w:"+w+"h"+h);
		float vertex[] = new float[] { 0f, (float) h, 0f, // top left
				0f, 0f, 0f, // bottom left
				(float) w, 0f, 0f,// bottom right
				(float) w, (float) h, 0f // top right
		};
		vertexBuffer = MyGLUtils.toFloatBuffer(vertex);
		
		int glw = Integer.highestOneBit(w - 1) << 1;
		int glh = Integer.highestOneBit(h - 1) << 1;
		textureCoordinates = new float[] { 0.0f, 0.0f,
				0.0f, (float)h/ (float)glh,
				(float)w/ (float)glw, (float)h/ (float)glh,
				(float)w/ (float)glw, 0.0f };
		textureFloatBuffer = MyGLUtils.toFloatBuffer(textureCoordinates);
		mBookContent.update(w - 20,
				h - PageConfig.getTextHeight(PageConfig.getOthersPaint(false))
						- 20);
		//
		mBookScreenDisplay.init(w, h);

		Bitmap BG = this.m_book_bg;
		int bitmap_w = BG.getWidth();
		int bitmap_h = BG.getHeight();
		Log.i("[BookView]", "" + bitmap_w + " " + bitmap_h);
		Matrix m = new Matrix();
		m.postScale((float) w / (float) bitmap_w, (float) h / (float) bitmap_h);
		mBookScreenDisplay.setBg(Bitmap.createBitmap(BG, 0, 0, bitmap_w,
				bitmap_h, m, true));

		mDisplayBitmap = Bitmap.createBitmap(BG, 0, 0, bitmap_w, bitmap_h, m,
				true);
		this.reset();
	}

	protected void drawDisplayBitmap(Canvas canvas) {
		if (mAnimation.state() == BookViewAnimation.NONE) {
			canvas.drawBitmap(mBookScreenDisplay
					.tranlateFrontBitmap(mBookContent.getCurPage()), 0, 0,
					mPaint);
			return;
		}
		if (mAnimation.state() == BookViewAnimation.STATE_ANIMATION_END) {
			if (mAnimation.DragToRight())
				mBookContent.turnToPre();
			else
				mBookContent.turnToNext();

			canvas.drawBitmap(mBookScreenDisplay
					.tranlateFrontBitmap(mBookContent.getCurPage()), 0, 0,
					mPaint);

			mAnimation.setState(BookViewAnimation.NONE);
			lock.lock();
			con.signal();
			lock.unlock();
			postInvalidate();
			return;
		}
		this.mAnimation.onDraw(canvas);
	}

	private void reset() {
		mBookContent.update();
		Log.i("hello", "bookview:reset");
		if (this.mInit == false) {
			// mBookContent.setCurPosition(mBook.openOffset);
			mInit = true;
		}
		mAnimation.setFrontBitmap(mBookScreenDisplay
				.tranlateFrontBitmap(mBookContent.getCurPage()));
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
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& mAnimation.state() != BookViewAnimation.NONE) {
			filterPoint = event.getDownTime();
		}
		if (filterPoint == event.getDownTime()) {
			return false;
		}
		mAnimation.onTouch(v, event);
		if (mAnimation.state() == BookViewAnimation.STATE_TOUCH_START) {

		}
		postInvalidate();
		return true;
	}

	public void setTurnAnimation(BookViewAnimation noTurnAnimation) {
		this.mAnimation = noTurnAnimation;
		mAnimation.setBookView(this);
		this.mAnimation.onSizeChange(getWidth(), getHeight(), 0, 0);
	}

	Lock lock = new ReentrantLock();
	Condition con = lock.newCondition();
	Thread thread = new Thread() {

		@Override
		public void run() {
			while (true) {
				lock.lock();
				try {
					con.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				if (mAnimation.DragToRight()) {
					mBookContent.getPrePage();
				} else {
					mBookContent.getNextPage();
				}
				lock.unlock();
			}

		}

	};

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Canvas canvas = new Canvas(mDisplayBitmap);
		drawDisplayBitmap(canvas);

		// save all clip
		canvas.save(Canvas.ALL_SAVE_FLAG);// ±£´æ
		// store
		canvas.restore();// ´æ´¢

		Texture texture = Texture.createTexture(mDisplayBitmap, gl);

		gl.glFrontFace(GL_CCW);

		gl.glEnable(GL_CULL_FACE);
		gl.glCullFace(GL_BACK);

		gl.glEnableClientState(GL_VERTEX_ARRAY);

		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnable(GL_TEXTURE_2D);
		gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		gl.glTexCoordPointer(2, GL_FLOAT, 0, textureFloatBuffer);
		gl.glBindTexture(GL_TEXTURE_2D, texture.getId()[0]);

		gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
		gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT,
				indicesBuffer);

		gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL_TEXTURE_2D);
		gl.glDisable(GL_BLEND);

		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisable(GL_CULL_FACE);
		texture.destroy(gl);
	}

	public static float[] light0Position = { 0, 0, 100f, 0f };

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, width, height);
		Log.i("hello", "width:" + width + "height:" + height);

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();

		float fovy = 20f;
		float eyeZ = height / 2f / (float) Math.tan(MyGLUtils.d2r(fovy / 2));

		GLU.gluPerspective(gl, fovy, (float) width / (float) height, 0.5f,
				Math.max(2500.0f, eyeZ));

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, width / 2.0f, height / 2f, eyeZ, width / 2.0f,
				height / 2.0f, 0.0f, 0.0f, 1.0f, 0.0f);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);

		float lightAmbient[] = new float[] { 3.5f, 3.5f, 3.5f, 1f };
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);

		light0Position = new float[] { 0, 0, eyeZ, 0f };
		gl.glLightfv(GL_LIGHT0, GL_POSITION, light0Position, 0);

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
	}

}
