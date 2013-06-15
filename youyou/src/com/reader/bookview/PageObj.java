/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.bookview;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.book.manager.BookContent;
import com.reader.config.PageConfig;

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
	float baseLineHeight;
	float fontHeight;
	private BookContent mBookContent;

	public PageObj(BookContent bookContent, Book book) {
		mBookContent = bookContent;
		Paint paint = PageConfig.pagePaintFromConfig(false);
		FontMetrics metrics = paint.getFontMetrics();
		baseLineHeight = -metrics.top;
		fontHeight = metrics.bottom - metrics.top;
	}

	private Page pageString;

	public void setPage(Page str) {
		this.pageString = str;
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		int y = 0;
		if (pageString == null)return;
		List<AreaDraw> list = pageString.getAreasDraw();
		for (AreaDraw strLine : list) {
			strLine.draw(canvas, 0, y + baseLineHeight, paint);
			y += strLine.getHeight();
		}
	}

}
