package com.reader.view.curl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;

public class CurlAnimation extends BookViewAnimation {
	CurlMesh mPageLeft = new CurlMesh(10);
	CurlMesh mPageRight = new CurlMesh(10);
	CurlMesh mPageCurl = new CurlMesh(10);
	// Curl meshes used for static and dynamic rendering.
	private Vector<CurlMesh> mCurlMeshes;

	public CurlAnimation(BitmapSetup setup) {
		super(setup);
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

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
		if (this.state() == NONE) {
			Bitmap front = this.mBitmapSetup.frontBitmap();
			CurlPage page = this.mPageRight.getTexturePage();
			page.setTexture(front, CurlPage.SIDE_BACK);
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
}
