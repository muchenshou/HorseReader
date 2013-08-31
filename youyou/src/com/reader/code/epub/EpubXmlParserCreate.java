package com.reader.code.epub;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class EpubXmlParserCreate {
	public static ContainerHandler createContainerHandler(InputStream in) {
		ContainerHandler handler;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader;

			reader = parser.getXMLReader();

			handler = new ContainerHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(in));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return handler;
	}

	public static OPFHandler createOPFHandler(InputStream in) {
		OPFHandler handler;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader;

			reader = parser.getXMLReader();

			handler = new OPFHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(in));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return handler;
	}
}
