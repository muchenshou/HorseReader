/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Record;

import android.graphics.drawable.Drawable;

public class BookInfo {
	public String bookName;
	public int book_id;
	public String mProcess;
	public int mSize;
	public Drawable mBookImage = null;
	public Drawable getBookImage(){
		return mBookImage;
	}
	public void setBookImage(Drawable d){
		this.mBookImage = d;
	}
}