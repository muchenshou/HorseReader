package com.Reader.Book;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BookBuffer {
	private Book mBook = null;
	private int mPosition = -1;
	private int mBufferSize = 1024;//buffer 4k
	private ByteBuffer mBuffer = ByteBuffer.allocate(mBufferSize);
	private int mLenghtContent = 0;
	public BookBuffer(Book book){
		mBook = book;
	}
	
	boolean have(int location){
		//Log.i("\nhave is\t", "location:"+location +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent);
		if (mPosition == -1)
			return false;
		if (location < mPosition || location > mPosition + mLenghtContent - 1){
			return false;
		}
		return true;
	}
	public char getChar(int location){
		if (have(location)) {
			return mBuffer.getChar(location - mPosition);
		}
		try {
			mBuffer.clear();
			mBook.getContent(location, mBuffer);
			mLenghtContent = mBufferSize; 
			this.mPosition = location;
			return this.getChar(location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public byte getByte(int location){
		return 0;
	}
}
