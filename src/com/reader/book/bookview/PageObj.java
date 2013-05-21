/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.bookview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.book.manager.BookContent;

class StringUtils {

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}

public class PageObj extends DrawableObj {

	private BookContent mBookContent;

	public PageObj(BookContent bookContent, Book book) {
		mBookContent = bookContent;
	}

	private Page pageString;

	public void setPage(Page str) {
		this.pageString = str;
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		int y = 0;
		for (AreaDraw strLine : pageString.getAreasDraw()) {
			strLine.draw(canvas, 10, y,paint);
			y += strLine.getHeight();
		}
	}

}
