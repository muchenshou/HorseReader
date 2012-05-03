package com.Reader.Book.Manager;

import java.io.File;
import java.io.IOException;
import android.content.Context;
import android.graphics.Rect;

import com.Reader.Main.ReadingActivity;
import com.Reader.Book.Book;
import com.Reader.Book.BookFactory;
import com.Reader.Book.BookView.BookView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BookManager implements View.OnKeyListener, View.OnTouchListener {

	public static final int OPENBOOK = 7;
	public static final int BUFSIZE = 512;
	private Book book;
	private BookView bookView;
	private TextUtil textUtil;
	public ReadingActivity bookActivity;
	int bufferlocal = -1;//文本块的位置
	int BUFLEN = 4 * 1024;

	int position = 0;
	public BookManager(Context con, File file) throws IOException {
		bookActivity = (ReadingActivity) con;
		book = BookFactory.createBook(file);
		bookView = new BookView(con);
		bookView.setFocusable(true);
		bookView.setOnKeyListener(this);
		bookView.setOnTouchListener(this);
		
		textUtil = new TextUtil(bookView, book);
		bookView.setTextUtil(textUtil);
		textUtil.InitText();
	}

	public BookView getBookView() {
		return bookView;
	}

	public void openBook() throws IOException {
		book.openBook();
	}

	public void closeBook() {
		book.closeBook();
	}
	public int getBookSize(){
		return book.size();
	}
	int num = 0;

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				&& event.getAction() == KeyEvent.ACTION_UP) {

			Log.d("number of", "" + (++num));
			this.textUtil.nextPage();
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP
				&& event.getAction() == KeyEvent.ACTION_UP) {

			Log.d("number of", "" + (++num));

		}
		return false;
	}

	public boolean onTouch(View v, MotionEvent event) {
		Rect rect = new Rect(0, 0, v.getWidth(), v.getHeight());

		if (event.getX() < rect.exactCenterX() + 40
				&& event.getX() > rect.exactCenterX() - 40
				&& event.getY() > rect.exactCenterY() - 20
				&& event.getY() < rect.exactCenterY() + 20) {
			// gridview
			bookActivity.setBookSet();
			return false;
		}
		
		if (event.getY() > rect.exactCenterY()) {
			this.textUtil.nextPage();
		} else {
			this.textUtil.prePage();
		}
		return false;
	}


	public Book getBook() {
		return book;
	}
	public int getReadingPosition(){
		return this.textUtil.getCurLocal();
	}

	

}
