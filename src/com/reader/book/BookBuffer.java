/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BookBuffer implements Runnable {
	private Book mBook = null;
	private int mBufferSize = 8 * 1024;// buffer 4k
	private Lock mBufferlock = new ReentrantLock();
	private Condition putContentIntoBuffer = mBufferlock.newCondition();

	class BookBufBlock {
		ByteBuffer mBufBlock;
		int mBlockNum;
	}

	class BookBufBlockList {
		LinkedList<BookBufBlock> mBlockList = new LinkedList<BookBufBlock>();

		public BookBufBlockList() {
			for (int i = 0; i < 3; i++) {
				BookBufBlock block = new BookBufBlock();
				block.mBlockNum = -1;
				block.mBufBlock = ByteBuffer.allocate(mBufferSize);
				this.mBlockList.add(block);
			}
		}

		BookBufBlock element(int element) {
			for (BookBufBlock b : this.mBlockList) {
				if (b.mBlockNum == element) {
					return b;
				}
			}
			return null;
		}

		void setCur(BookBufBlock b) {
			if (b.mBlockNum > 0) {
				if (element(b.mBlockNum - 1) != null) {
					this.mBlockList.set(0, element(b.mBlockNum - 1));
				} else {
					this.mBlockList.get(0).mBlockNum = -1;
				}
			}
			if (element(b.mBlockNum + 1) != null) {
				this.mBlockList.set(2, element(b.mBlockNum + 1));
			} else {
				this.mBlockList.get(2).mBlockNum = -1;
			}
			this.mBlockList.set(1, b);
		}

		BookBufBlock getCur() {
			return this.mBlockList.get(1);
		}

		BookBufBlock getPre() {
			return this.mBlockList.get(0);
		}

		BookBufBlock getNext() {
			return this.mBlockList.get(2);
		}
	}

	private BookBufBlockList mBufList = new BookBufBlockList();

	public BookBuffer(Book book) {

		mBook = book;

		new Thread(this).start();
	}

	boolean have(int location) {
		if (this.mBufList.getCur().mBlockNum == -1)
			return false;
		if (location / this.mBufferSize != this.mBufList.getCur().mBlockNum) {
			return false;
		}
		return true;
	}

	boolean handleBuf(int location) {
		if (this.mBufList.getCur().mBlockNum == -1) {
			return false;
		}
		if (mBufList.element(location / this.mBufferSize) == null) {
			return false;
		}
		this.mBufList.setCur(mBufList.element(location / this.mBufferSize));
		this.mBufList.getCur().mBlockNum = location / this.mBufferSize;
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
		try {
			mBufferlock.lock();
			if (location >= this.mBook.size())
				return 0;
			if (have(location)) {
				return this.mBufList.getCur().mBufBlock.get(location
						% this.mBufferSize);
			}
			if (this.handleBuf(location)) {
				putContentIntoBuffer.signal();
				return this.getByte(location);
			}

			this.mBufList.getCur().mBufBlock.clear();
			mBook.getContent((location / this.mBufferSize) * this.mBufferSize,
					this.mBufList.getCur().mBufBlock);
			this.mBufList.getCur().mBlockNum = location / this.mBufferSize;

			putContentIntoBuffer.signal();
			return this.getByte(location);
		} finally {
			mBufferlock.unlock();
		}

	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				mBufferlock.lock();
				try {
					putContentIntoBuffer.await();
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
				if (mBufList.getPre().mBlockNum == -1 && mBufList.getCur().mBlockNum != 0) {
					this.mBufList.getPre().mBufBlock.clear();
					mBook.getContent((mBufList.getCur().mBlockNum - 1)
							* this.mBufferSize,
							this.mBufList.getPre().mBufBlock);
					this.mBufList.getPre().mBlockNum = mBufList.getCur().mBlockNum - 1;
				}
				if (mBufList.getNext().mBlockNum == -1 && mBufList.getCur().mBlockNum != mBook.size()/mBufferSize) {
					this.mBufList.getNext().mBufBlock.clear();
					mBook.getContent((mBufList.getCur().mBlockNum + 1)
							* this.mBufferSize,
							this.mBufList.getNext().mBufBlock);
					this.mBufList.getNext().mBlockNum = mBufList.getCur().mBlockNum + 1;
				}
				// this.notifyAll();
			} finally {
				mBufferlock.unlock();
			}

		}
	}
}
