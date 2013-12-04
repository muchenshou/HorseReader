package com.reader.animation;

public interface IAnimation {
	enum DIR {
		PRE, NEXT
	};

	public int startAnimation(int flags);

	public int endAnimation(int flags);
}
