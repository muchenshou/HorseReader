package com.Reader.Book;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class BookBuffer {
	private Book mBook = null;
	private int mBuf1Position = -1;
	private int mBufferSize = 8*1024;// buffer 4k
	private ByteBuffer mBuffer1 = ByteBuffer.allocate(mBufferSize);
	private int mBuf1LenghtContent = 0;
	private int mBuf2Position = -1;
	private ByteBuffer mBuffer2 = ByteBuffer.allocate(mBufferSize);
	private int mBuf2LenghtContent = 0;
	public BookBuffer(Book book) {
		mBook = book;
	}

	boolean have(int location) {
		// Log.i("\nhave is\t", "location:"+location
		// +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent);
		if (mBuf1Position == -1)
			return false;
		if (location < mBuf1Position
				|| location > mBuf1Position + mBuf1LenghtContent - 1) {
			return false;
		}
		return true;
	}

	public char getChar(int location) {
		ByteBuffer charbuf = ByteBuffer.allocate(2);
		charbuf.clear();
		charbuf.put(this.getByte(location));
		charbuf.put(this.getByte(location + 1));
		charbuf.flip();
		charbuf.order(ByteOrder.LITTLE_ENDIAN);
		return charbuf.getChar();

	}

	public byte getByte(final int location) {
		if (location >= this.mBook.size())
			return 0;
		long one = System.currentTimeMillis();
		if (have(location)) {
			long two = System.currentTimeMillis();
			//Log.i("[Thread]", ""+(two - one));
			return mBuffer1.get(location - mBuf1Position);
		}
		if (haveInBuf2(location)) {
			final int buf1pos = this.mBuf2Position;
			synchronized (mBuffer2) {
				/*Log.i("[BookBuffer]", "mBuffer1 mBuf1LenghtContent:"
						+ mBuf1LenghtContent);
				Log.i("[BookBuffer]", "mBuffer1 pos:" + mBuf1Position);
				Log.i("[BookBuffer]", "mBuffer1 mBuf2LenghtContent:"
						+ this.mBuf2LenghtContent);
				Log.i("[BookBuffer]", "mBuffer1 pos:" + mBuf1Position);*/
				mBuffer1.clear();
				ByteBuffer mid = mBuffer1;
				mBuffer1 = mBuffer2;
				mBuffer2 = mid;
				mBuf1Position = mBuf2Position;
				mBuf1LenghtContent = mBuf2LenghtContent;
			}

			new Thread() {
				public void run() {
					synchronized (mBuffer2) {
						Log.i("[Thread]", "time is important");
						mBuffer2.clear();
						if(BookBuffer.this.mBook.isEof()){
							return;
						}
						mBuf2LenghtContent = mBook.getContent(buf1pos
								+ mBuf2LenghtContent, mBuffer2);
						mBuf2Position = buf1pos + mBuf2LenghtContent;
					}
				}
			}.start();
			long two = System.currentTimeMillis();
			Log.i("[Thread]", ""+(two - one));
			return this.getByte(location);
		}
		mBuffer1.clear();

		mBuf1LenghtContent = mBook.getContent(location, mBuffer1);
		this.mBuf1Position = location;

		final int buf1pos = this.mBuf1Position;
		new Thread() {
			public void run() {
				synchronized (mBuffer2) {
					mBuffer2.clear();
					mBuf2LenghtContent = mBook.getContent(buf1pos
							+ mBuf1LenghtContent, mBuffer2);
					mBuf2Position = buf1pos + mBuf1LenghtContent;
				}
			}
		}.start();
		Log.i("getbyteend",""+location);
		return this.getByte(location);
	}

	private boolean haveInBuf2(int location) {
		// Log.i("\nhave is\t", "location:"+location
		// +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent);
		if (mBuf2Position == -1)
			return false;
		if (location < mBuf2Position
				|| location > mBuf2Position + mBuf2LenghtContent - 1) {
			return false;
		}
		return true;
	}
}
