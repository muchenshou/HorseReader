package com.reader.code.epub;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class ContainerHandler {
	InputStream inputStream;

	public ContainerHandler(InputStream in) {
		inputStream = in;
	}

	public void handle() {
		XmlPullParserFactory factory;
		Log.i("hello","hh");
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(inputStream, "utf-8");
			
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					Log.i("hello", "start");
					break;
				case XmlPullParser.START_TAG:
					Log.i("hello", parser.getName());
					Log.i("hello", "" + parser.getAttributeCount());
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
