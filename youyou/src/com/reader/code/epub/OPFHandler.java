package com.reader.code.epub;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class OPFHandler extends DefaultHandler{
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		Log.i("hello", uri+"[]"+localName+"[]"+qName+"[]");
		super.startElement(uri, localName, qName, attributes);
	}

}
