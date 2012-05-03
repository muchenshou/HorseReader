package com.Reader.Book.BookView;

import java.io.IOException;

import com.Reader.Book.Manager.BookManager;
import com.Reader.Book.Manager.TextUtil;
import com.Reader.Config.ViewConfig;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class BookView extends View {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	private TextUtil textUtil = null;
	private boolean updatesize = true;
	private ViewConfig config = new ViewConfig();
	public BookView(Context context) {
		super(context);
	}
	public void setTextUtil(TextUtil t){
		textUtil = t;
	}

	public void updateSize(){
		updatesize = true;
	}
	@Override
	public void onDraw(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setColor(config.backColor);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		//Log.d("width height", "" + getWidth() + getHeight());
		try {
			textUtil.DrawText(canvas);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
