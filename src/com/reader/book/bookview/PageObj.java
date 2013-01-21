/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.bookview;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.reader.book.Book;

import android.graphics.Canvas;
import android.graphics.Paint;

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

	private BookView mBookView;

	public PageObj(BookView bookView, Book book) {
		mBookView = bookView;
	}

	private List<String> pageString = new ArrayList<String>();

	public void setPageString(List<String> str) {
		this.pageString = str;
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		int y = 15;
		for (String strLine : pageString) {
			y += this.mBookView.mBookContent.getLineHeight();
			canvas.drawText(strLine, 10, y, paint);
		}
	}

}
