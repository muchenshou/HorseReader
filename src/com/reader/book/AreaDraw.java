package com.reader.book;

import com.reader.book.model.Element;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class AreaDraw {
	enum TYPE{
		TEXT,
		IMAGE,
	}
	public Element element;
	public int offset;
	public int lenght;
	public abstract float getWidth();
	public abstract float getHeight();
	public abstract void draw(Canvas canvas, float left, float top,Paint paint);
	public abstract void fill();
}
