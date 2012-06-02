package com.Reader.Book.BookView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Reader.Book.Book;
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

public class PageObj extends DrawableObj{
	
	private BookView mBookView;
	
	public int mPosition = 0;
	public PageObj(BookView bookView, Book book ) {
		mBookView = bookView;		
	}	

	private List<String> pageString = new ArrayList<String>();

	public void nextLine() {
		pageString = this.mBookView.bookreading.nextLine();
	}

	public void nextPage() {
		pageString = this.mBookView.bookreading.nextPage();
		
	}

	public void preLine() {
		pageString = this.mBookView.bookreading.preLine();
	}

	public void prePage() {
		pageString = this.mBookView.bookreading.prePage();
	}

	public void setLocal(int local) {
		if (local < 0)
			return;
		pageString = this.mBookView.bookreading.getPageStr(local);
	}

	@Override
	public	void Draw(Canvas canvas,Paint paint) {
		
		
		for (int j = 0; j < pageString.size(); j++) {
			//Log.i("text paint",pageString.get(j));
			canvas.drawText(pageString.get(j), 0,
					(this.mBookView.bookreading.getLineHeight() ) * j + 30, paint);

		}
	}
	

}
