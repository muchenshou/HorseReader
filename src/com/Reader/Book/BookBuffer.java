package com.Reader.Book;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class BookBuffer implements Runnable {
	private Book mBook = null;
	private int mBufferSize = 8 * 1024;// buffer 4k
	private ByteBuffer mBuffer1 = ByteBuffer.allocate(mBufferSize); // cur
	private int mBuf1Num = -1;
	private ByteBuffer mBuffer2 = ByteBuffer.allocate(mBufferSize); // next
	private int mBuf2Num = -1;
	private ByteBuffer mBuffer3 = ByteBuffer.allocate(mBufferSize);// pre
	private int mBuf3Num = -1;

	public BookBuffer(Book book) {
		mBook = book;
		new Thread(this).start();
	}

	boolean have(int location) {
		if (mBuf1Num == -1)
			return false;
		if (location / this.mBufferSize != this.mBuf1Num) {
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
		synchronized (this) {
			if (location >= this.mBook.size())
				return 0;
			if (have(location)) {
				return mBuffer1.get(location % this.mBufferSize);
			}
			if (haveInBuf2(location)) {

				mBuffer1.clear();
				ByteBuffer mid = mBuffer3;
				mBuffer3 = mBuffer1;
				mBuffer1 = mBuffer2;
				mBuffer2 = mid;
				mBuf3Num = mBuf1Num;
				mBuf1Num = mBuf2Num;
				this.notifyAll();

				Log.i("[Thread]", "location:" + location);
				return this.getByte(location);
			}
			if (haveInBuf3(location)) {
				return mBuffer3.get(location % this.mBufferSize);
			}
			mBuffer1.clear();
			Log.i("[Thread2]", "location" + location);

			mBook.getContent((location / this.mBufferSize) * this.mBufferSize,
					mBuffer1);
			mBuf1Num = location / this.mBufferSize;

			this.notifyAll();

			long two = System.currentTimeMillis();
			// Log.i("[Thread2]", "" + (two - one)+"ms");

			return this.getByte(location);
		}
	}

	private boolean haveInBuf2(int location) {
		// Log.i("\nhave is\t", "location:"+location
		// +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent);
		if (mBuf2Num == -1)
			return false;
		if (location / this.mBufferSize != this.mBuf2Num) {
			return false;
		}
		return true;
	}

	private boolean haveInBuf3(int location) {
		// Log.i("\nhave is\t", "location:"+location
		// +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent);
		if (mBuf3Num == -1)
			return false;
		if (location / this.mBufferSize != this.mBuf3Num) {
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {

			synchronized (this) {
				try {
					// this.isWaitting = true;
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// this.isWaitting = false;
				// Log.i("[thread]","getcontent");
				mBuffer2.clear();
				mBook.getContent((this.mBuf1Num + 1) * this.mBufferSize,
						mBuffer2);
				this.mBuf2Num = this.mBuf1Num + 1;
				this.notifyAll();
			}
		}
	}
}
