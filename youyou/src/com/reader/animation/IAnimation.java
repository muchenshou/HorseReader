package com.reader.animation;

public interface IAnimation {
	enum DIR {
		PRE, NEXT
	};

	public int startAnimation(DIR flags);

	public int endAnimation(DIR flags);
}
