package com.reader.config;

public class ConfigItem implements Comparable<ConfigItem>{
	public static final String TYPE_INT = "type_int";
	public static final String TYPE_TEXT = "type_text";
	public static final String TYPE_FLOAT = "type_float";
	public String name;
	public String type;
	public String value;
	@Override
	public int compareTo(ConfigItem another) {
		// TODO Auto-generated method stub
		return 0;
	}
}
