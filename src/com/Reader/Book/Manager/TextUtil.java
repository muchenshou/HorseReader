package com.Reader.Book.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Reader.Book.Book;
import com.Reader.Book.BookView.BookView;
import com.Reader.Config.TextUtilConfig;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.Log;

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

public class TextUtil {

	private Paint mPaint = null;
	private BookReading bookreading;
	private BookView mBookView;
	private TextUtilConfig mTextConfig;
	private boolean mInit = false;
	public Bitmap m_book_bg = null;
	private int m_backColor = 0xffff9e85; // 背景颜色
	int mPosition = 0;
	public TextUtil(BookView bookView, Book book,TextUtilConfig config ) {
		mBookView = bookView;
		bookreading = new BookReading(book);
		mTextConfig = config;
		mPaint = mTextConfig.getPaint();
		bookreading.mPaint = mPaint;		
	}

	public void setTextSize(int size) {
		this.mTextConfig.mTextSize = size;
	}

	public void InitText() throws IOException {
		// 对画笔属性的设置
		mInit = true;
		
	}

	public int getLineHeight() {
		FontMetrics fm = mPaint.getFontMetrics();// 得到系统默认字体属性
		return (int) (Math.ceil(fm.descent - fm.top) +mTextConfig.mPadding);
	}

	public int getLineNum() {
		return (int) (mBookView.getHeight() / getLineHeight());
	}

	private List<String> pageString = new ArrayList<String>();

	/**
	 * 绘制字符串
	 * 
	 * @param canvas
	 * @throws IOException
	 */
	public void DrawText(Canvas canvas) throws IOException {
		if (this.mInit == true) {
			bookreading.pageWidth = this.mBookView.getWidth();
			bookreading.pageline = this.getLineNum() - 1;
			this.pageString = bookreading.getPageStr(mPosition);
			this.mInit = false;
		}
		if (m_book_bg == null)
			canvas.drawColor(m_backColor);
		else
			canvas.drawBitmap(m_book_bg, 0, 0, null);
		for (int j = 0; j < pageString.size(); j++) {
			//Log.i("text paint",pageString.get(j));
			canvas.drawText(pageString.get(j), 0,
					(this.getLineHeight() + this.mTextConfig.mPadding) * j + 30, mPaint);

		}
	}

	public int getCurPosition() {
		return bookreading.getCurPosition();
	}

	public void nextLine() {
		pageString = bookreading.nextLine();
	}

	public void nextPage() {
		pageString = bookreading.nextPage();
		
	}

	public void preLine() {
		pageString = bookreading.preLine();
	}

	public void prePage() {
		pageString = bookreading.prePage();
	}

	public void setLocal(int local) {
		if (local < 0)
			return;
		pageString = bookreading.getPageStr(local);
	}
	
	public void setBgBitmap(Bitmap BG) {
		
		m_book_bg = BG;
	}
}
