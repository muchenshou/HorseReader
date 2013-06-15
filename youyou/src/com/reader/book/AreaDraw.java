package com.reader.book;

import com.reader.book.model.MarkupElement;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class AreaDraw {
	enum TYPE{
		TEXT,
		IMAGE,
	}
	public MarkupElement element;
	public int offset;
	public int length;
	public abstract float getWidth();
	public abstract float getHeight();
	public abstract void draw(Canvas canvas, float left, float top,Paint paint);
	public abstract void fill();
}
