package com.reader.view.curl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
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

	private PointerPosition mPointerPos = new PointerPosition();

	private boolean mEnableTouchPressure = false;

	// Start position for dragging.
	private PointF mDragStartPos = new PointF();

	private boolean mAllowLastPageCurl = true;

	private PointF mAnimationSource = new PointF();
	private long mAnimationStartTime;
	private long mAnimationDurationTime = 300;
	private PointF mAnimationTarget = new PointF();
	private int mAnimationTargetEvent;

	private PointF mCurlDir = new PointF();

	private PointF mCurlPos = new PointF();
	private boolean mRenderLeftPage = true;

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
		// No dragging during animation at the moment.
		// TODO: Stop animation on touch event and return to drag mode.
		if (mAnimate) {
			return false;
		}
		// We need page rects quite extensively so get them for later use.
		RectF rightRect = mPageRectRight;
		RectF leftRect = mPageRectLeft;

		// Store pointer position.
		mPointerPos.mPos.set(event.getX(), event.getY());
		translate(mPointerPos.mPos);
		if (mEnableTouchPressure) {
			mPointerPos.mPressure = event.getPressure();
		} else {
			mPointerPos.mPressure = 0.8f;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			//set up our state
			// Once we receive pointer down event its position is mapped to
			// right or left edge of page and that'll be the position from where
			// user is holding the paper to make curl happen.
			mDragStartPos.set(mPointerPos.mPos);

			// First we make sure it's not over or below page. Pages are
			// supposed to be same height so it really doesn't matter do we use
			// left or right one.
			if (mDragStartPos.y > rightRect.top) {
				mDragStartPos.y = rightRect.top;
			} else if (mDragStartPos.y < rightRect.bottom) {
				mDragStartPos.y = rightRect.bottom;
			}

			// Then we have to make decisions for the user whether curl is going
			// to happen from left or right, and on which page.
			if (mViewMode == SHOW_TWO_PAGES) {
				// If we have an open book and pointer is on the left from right
				// page we'll mark drag position to left edge of left page.
				// Additionally checking mCurrentIndex is higher than zero tells
				// us there is a visible page at all.
				if (mDragStartPos.x < rightRect.left /* && mCurrentIndex > 0 */) {
					mDragStartPos.x = leftRect.left;
					startCurl(CURL_LEFT);
				}
				// Otherwise check pointer is on right page's side.
				else if (mDragStartPos.x >= rightRect.left
				/* && mCurrentIndex < mPageProvider.getPageCount() */) {
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl
					/* && mCurrentIndex >= mPageProvider.getPageCount() - 1 */) {
						return false;
					}
					startCurl(CURL_RIGHT);
				}
			} else if (mViewMode == SHOW_ONE_PAGE) {
				float halfX = (rightRect.right + rightRect.left) / 2;
				if (mDragStartPos.x < halfX /* && mCurrentIndex > 0 */) {
					mDragStartPos.x = rightRect.left;
					startCurl(CURL_LEFT);
				} else if (mDragStartPos.x >= halfX
				/* && mCurrentIndex < mPageProvider.getPageCount() */) {
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl
					/* && mCurrentIndex >= mPageProvider.getPageCount() - 1 */) {
						return false;
					}
					startCurl(CURL_RIGHT);
				}
			}
			// If we have are in curl state, let this case clause flow through
			// to next one. We have pointer position and drag position defined
			// and this will create first render request given these points.
			if (mCurlState == CURL_NONE) {
				return false;
			}
		}
		case MotionEvent.ACTION_MOVE: {
			updateCurlPos(mPointerPos);
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
				// Animation source is the point from where animation starts.
				// Also it's handled in a way we actually simulate touch events
				// meaning the output is exactly the same as if user drags the
				// page to other side. While not producing the best looking
				// result (which is easier done by altering curl position and/or
				// direction directly), this is done in a hope it made code a
				// bit more readable and easier to maintain.
				mAnimationSource.set(mPointerPos.mPos);
				mAnimationStartTime = System.currentTimeMillis();

				// Given the explanation, here we decide whether to simulate
				// drag to left or right end.
				if ((mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2)
						|| mViewMode == SHOW_TWO_PAGES
						&& mPointerPos.mPos.x > rightRect.left) {
					// On right side target is always right page's right border.
					mAnimationTarget.set(mDragStartPos);
					mAnimationTarget.x = mPageRectRight.right;
					mAnimationTargetEvent = SET_CURL_TO_RIGHT;
				} else {
					// On left side target depends on visible pages.
					mAnimationTarget.set(mDragStartPos);
					if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES) {
						mAnimationTarget.x = leftRect.left;
					} else {
						mAnimationTarget.x = rightRect.left;
					}
					mAnimationTargetEvent = SET_CURL_TO_LEFT;
				}
				mAnimate = true;
				this.mBitmapSetup.requestFresh();
			}
			break;
		}
		}

		return true;
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
		// updatePageRects();
		mBitmapSetup.requestFresh();

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
		if (this.state() == CURL_NONE) {

			Bitmap front = this.mBitmapSetup.frontBitmap();

			mPageRight.setRect(mPageRectRight);
			mPageRight.setFlipTexture(false);
			mPageRight.reset();
			CurlPage page = this.mPageRight.getTexturePage();
			page.setTexture(front, CurlPage.SIDE_BOTH);
			// page.setColor(Color.rgb(180, 0, 0), CurlPage.SIDE_BOTH);
			// page.setTexture(null, CurlPage.SIDE_BOTH);
			addCurlMesh(mPageRight);
		}
		// We are not animating.
		if (mAnimate == false) {
			for (int i = 0; i < mCurlMeshes.size(); ++i) {
				mCurlMeshes.get(i).onDrawFrame(gl);
			}
			return;
		}

		long currentTime = System.currentTimeMillis();
		// If animation is done.
		if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
			
			if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {
				// Switch curled page to right.
				CurlMesh right = mPageCurl;
				CurlMesh curl = mPageRight;
				right.setRect(mPageRectRight);
				right.setFlipTexture(false);
				right.reset();
				removeCurlMesh(curl);
				mPageCurl = curl;
				mPageRight = right;
				// If we were curling left page update current index.
				if (mCurlState == CURL_LEFT) {
					// --mCurrentIndex;
					mBitmapSetup.turnToPre();
				}
			} else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {
				// Switch curled page to left.
				CurlMesh left = mPageCurl;
				CurlMesh curl = mPageLeft;
				left.setRect(mPageRectLeft);
				left.setFlipTexture(true);
				left.reset();
				removeCurlMesh(curl);
				if (!mRenderLeftPage) {
					removeCurlMesh(left);
				}
				mPageCurl = curl;
				mPageLeft = left;
				// If we were curling right page update current index.
				if (mCurlState == CURL_RIGHT) {
					// ++mCurrentIndex;
					mBitmapSetup.turnToNext();
				}
			}
			mCurlState = CURL_NONE;
			mAnimate = false;
			this.mBitmapSetup.requestFresh();
		} else {
			mPointerPos.mPos.set(mAnimationSource);
			float t = 1f - ((float) (currentTime - mAnimationStartTime) / mAnimationDurationTime);
			t = 1f - (t * t * t * (3 - 2 * t));
			mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t;
			mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t;
			updateCurlPos(mPointerPos);
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
			// mObserver.onPageSizeChanged(bitmapW, bitmapH);
		}
	}

	/**
	 * Translates screen coordinates into view coordinates.
	 */
	private void translate(PointF pt) {
		pt.x = mViewRect.left + (mViewRect.width() * pt.x / mViewportWidth);
		pt.y = mViewRect.top - (-mViewRect.height() * pt.y / mViewportHeight);
	}

	/**
	 * Simple holder for pointer position.
	 */
	private class PointerPosition {
		PointF mPos = new PointF();
		float mPressure;
	}

	/**
	 * Switches meshes and loads new bitmaps if available. Updated to support 2
	 * pages in landscape
	 */
	private void startCurl(int page) {
		switch (page) {

		// Once right side page is curled, first right page is assigned into
		// curled page. And if there are more bitmaps available new bitmap is
		// loaded into right side mesh.
		case CURL_RIGHT: {
			// Remove meshes from renderer.
			removeCurlMesh(mPageLeft);
			removeCurlMesh(mPageRight);
			removeCurlMesh(mPageCurl);

			// We are curling right page.
			CurlMesh curl = mPageRight;
			mPageRight = mPageCurl;
			mPageCurl = curl;

			Bitmap back = mBitmapSetup.backBitmap();
			if (back != null) {
				updatePage(mPageRight.getTexturePage(), back);
				mPageRight.setRect(mPageRectRight);
				mPageRight.setFlipTexture(false);
				mPageRight.reset();
				
				addCurlMesh(mPageRight);
			}

			// Add curled page to renderer.
			mPageCurl.setRect(mPageRectRight);
			mPageCurl.setFlipTexture(false);
			mPageCurl.reset();
			addCurlMesh(mPageCurl);

			mCurlState = CURL_RIGHT;
			break;
		}

		// On left side curl, left page is assigned to curled page. And if
		// there are more bitmaps available before currentIndex, new bitmap
		// is loaded into left page.
		case CURL_LEFT: {
			// Remove meshes from renderer.
			removeCurlMesh(mPageLeft);
			removeCurlMesh(mPageRight);
			removeCurlMesh(mPageCurl);

			// We are curling left page.
			CurlMesh curl = mPageLeft;
			mPageLeft = mPageCurl;
			mPageCurl = curl;
			Bitmap back = mBitmapSetup.backBitmap();
			if (back != null) {
				updatePage(mPageLeft.getTexturePage(), back);
				mPageLeft.setFlipTexture(true);
				mPageLeft.setRect(mPageRectRight);
				mPageLeft.reset();
				if (mRenderLeftPage) {
					addCurlMesh(mPageLeft);
				}
			}

			// If there is something to show on right page add it to renderer.
			if (mBitmapSetup.backBitmap() != null) {
				mPageRight.setFlipTexture(false);
				mPageRight.setRect(mPageRectRight);
				mPageRight.reset();
				addCurlMesh(mPageRight);
			}

			// How dragging previous page happens depends on view mode.
			if (mViewMode == SHOW_ONE_PAGE
					|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {
				mPageCurl.setRect(mPageRectRight);
				mPageCurl.setFlipTexture(false);
			} else {
				mPageCurl.setRect(mPageRectLeft);
				mPageCurl.setFlipTexture(true);
			}
			mPageCurl.reset();
			addCurlMesh(mPageCurl);

			mCurlState = CURL_LEFT;
			break;
		}

		}
	}

	/**
	 * Updates curl position.
	 */
	private void updateCurlPos(PointerPosition pointerPos) {

		// Default curl radius.
		double radius = mPageRectRight.width() / 3;
		// TODO: This is not an optimal solution. Based on feedback received so
		// far; pressure is not very accurate, it may be better not to map
		// coefficient to range [0f, 1f] but something like [.2f, 1f] instead.
		// Leaving it as is until get my hands on a real device. On emulator
		// this doesn't work anyway.
		radius *= Math.max(1f - pointerPos.mPressure, 0f);
		// NOTE: Here we set pointerPos to mCurlPos. It might be a bit confusing
		// later to see e.g "mCurlPos.x - mDragStartPos.x" used. But it's
		// actually pointerPos we are doing calculations against. Why? Simply to
		// optimize code a bit with the cost of making it unreadable. Otherwise
		// we had to this in both of the next if-else branches.
		mCurlPos.set(pointerPos.mPos);

		// If curl happens on right page, or on left page on two page mode,
		// we'll calculate curl position from pointerPos.
		if (mCurlState == CURL_RIGHT
				|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {

			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y
					* mCurlDir.y);

			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
			float pageWidth = mPageRectRight.width();
			double curlLen = radius * Math.PI;
			if (dist > (pageWidth * 2) - curlLen) {
				curlLen = Math.max((pageWidth * 2) - dist, 0f);
				radius = curlLen / Math.PI;
			}

			// Actual curl position calculation.
			if (dist >= curlLen) {
				double translate = (dist - curlLen) / 2;
				if (mViewMode == SHOW_TWO_PAGES) {
					mCurlPos.x -= mCurlDir.x * translate / dist;
				} else {
					float pageLeftX = mPageRectRight.left;
					radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius),
							0f);
				}
				mCurlPos.y -= mCurlDir.y * translate / dist;
			} else {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				mCurlPos.x += mCurlDir.x * translate / dist;
				mCurlPos.y += mCurlDir.y * translate / dist;
			}
		}
		// Otherwise we'll let curl follow pointer position.
		else if (mCurlState == CURL_LEFT) {

			// Adjust radius regarding how close to page edge we are.
			float pageLeftX = mPageRectRight.left;
			radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);

			float pageRightX = mPageRectRight.right;
			mCurlPos.x -= Math.min(pageRightX - mCurlPos.x, radius);
			mCurlDir.x = mCurlPos.x + mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
		}

		setCurlPos(mCurlPos, mCurlDir, radius);
	}

	/**
	 * Sets mPageCurl curl position.
	 */
	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {

		// First reposition curl so that page doesn't 'rip off' from book.
		if (mCurlState == CURL_RIGHT
				|| (mCurlState == CURL_LEFT && mViewMode == SHOW_ONE_PAGE)) {
			RectF pageRect = mPageRectRight;
			if (curlPos.x >= pageRect.right) {
				mPageCurl.reset();
				this.mBitmapSetup.requestFresh();
				return;
			}
			if (curlPos.x < pageRect.left) {
				curlPos.x = pageRect.left;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.left;
				float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && leftY < pageRect.top) {
					curlDir.x = curlPos.y - pageRect.top;
					curlDir.y = pageRect.left - curlPos.x;
				} else if (curlDir.y > 0 && leftY > pageRect.bottom) {
					curlDir.x = pageRect.bottom - curlPos.y;
					curlDir.y = curlPos.x - pageRect.left;
				}
			}
		} else if (mCurlState == CURL_LEFT) {
			RectF pageRect = mPageRectLeft;
			if (curlPos.x <= pageRect.left) {
				mPageCurl.reset();
				this.mBitmapSetup.requestFresh();
				return;
			}
			if (curlPos.x > pageRect.right) {
				curlPos.x = pageRect.right;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.right;
				float rightY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && rightY < pageRect.top) {
					curlDir.x = pageRect.top - curlPos.y;
					curlDir.y = curlPos.x - pageRect.right;
				} else if (curlDir.y > 0 && rightY > pageRect.bottom) {
					curlDir.x = curlPos.y - pageRect.bottom;
					curlDir.y = pageRect.right - curlPos.x;
				}
			}
		}

		// Finally normalize direction vector and do rendering.
		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			mPageCurl.curl(curlPos, curlDir, radius);
		} else {
			mPageCurl.reset();
		}

		mBitmapSetup.requestFresh();
	}

	/**
	 * Updates given CurlPage via PageProvider for page located at index.
	 */
	private void updatePage(CurlPage page, Bitmap bitmap) {
		// Ask page provider to fill it up with bitmaps and colors.
		page.setTexture(bitmap, CurlPage.SIDE_BOTH);
	}
}
