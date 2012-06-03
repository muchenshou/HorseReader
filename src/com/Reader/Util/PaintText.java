package com.Reader.Util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class PaintText {

	/**
	 * 获取当前分辨率下指定单位对应的像素大小（根据设备信息） px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @param unit
	 *            TypedValue.COMPLEX_UNIT_*
	 * @param size
	 * @return
	 */
	public static float getRawSize(Context con, int unit, float size) {
		Context c = con;
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();

		return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
}
