package com.reader.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.reader.app.YouYouApplication;

public class Configs {
	private static ArrayList<ConfigItem> mConfigs = null;//

	public static ConfigItem getConfig(String name, String type,
			String defaultval) {
		try {
			if (mConfigs == null) {
				mConfigs = new ArrayList<ConfigItem>();
				readConfigs();
			}
			for (ConfigItem item : mConfigs) {
				if (item.name.equals(name) && item.type.equals(type)) {
					return item;
				}
			}
			ConfigItem item = new ConfigItem();
			item.name = name;
			item.type = type;
			item.value = defaultval;
			mConfigs.add(item);
			saveConfigs();
			return item;
		} catch (Exception e) {
			e.printStackTrace();
			new File(YouYouApplication.configPath).delete();
		}

		return null;
	}

	private static void readConfigs() throws Exception {
		mConfigs.clear();
		File config = new File(YouYouApplication.configPath);
		if (!config.exists()) {
			config.createNewFile();
			config.setWritable(true);
			config.setReadable(true);
			saveConfigs();
		}
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		InputStream is = new FileInputStream(YouYouApplication.configPath);
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		ConfigItem item = null;
		while (XmlPullParser.END_DOCUMENT != eventType) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (!parser.getName().equals("root")) {
					item = new ConfigItem();
					item.name = parser.getAttributeValue(0);
					item.type = parser.getAttributeValue(1);
				}
				break;
			case XmlPullParser.TEXT:
				item.value = parser.getText();
				break;
			}
			eventType = parser.next();
		}
		is.close();
	}

	private static synchronized void saveConfigs() throws Exception {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlSerializer serial = factory.newSerializer();
		OutputStream os = new FileOutputStream(YouYouApplication.configPath);
		serial.setOutput(os, "UTF-8");
		serial.startDocument("UTF-8", true);
		serial.startTag("", "root");
		for (ConfigItem item : mConfigs) {
			serial.startTag("", "item");
			serial.attribute("", "name", item.name);
			serial.attribute("", "type", item.type);
			serial.text(item.value);
			serial.endTag("", "item");
		}
		serial.endTag("", "root");
		serial.endDocument();
		os.flush();
		os.close();
	}

	public static int getInt(String name, int defVal) {
		return Integer.decode(getConfig(name, ConfigItem.TYPE_INT,
				Integer.toString(defVal)).value);
	}

	public static void putInt(String name, int val) {
		getConfig(name, ConfigItem.TYPE_INT, Integer.toString(val));
	}
}
