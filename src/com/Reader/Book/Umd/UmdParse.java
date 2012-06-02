package com.Reader.Book.Umd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UmdParse extends RandomAccessFile {

	public static int UMDFLAG = (int) 0x899b9ade;
	public static final int ISTEXT = 1;
	public static final int ISIMAGE = 0;
	public static final int BLOCKSIZE = 32768;

	public static final int TEXT_OR_IMAGE = 1;
	public static final int NAME = 2;
	public static final int AUTHOR = 3;
	public static final int YEAR = 4;
	public static final int MONTH = 5;
	public static final int DAY = 6;
	public static final int GENDOR = 7;
	public static final int PUBLISHER = 8;
	public static final int VENDOR = 9;
	public static final int SIZE = 0x0B;
	public static final int CHAPTERS = 0x83;
	public static final int CHAPTERTITLES = 0x84;

	public UmdParse(File file, String per) throws FileNotFoundException {
		super(file, per);
	}

	public int getInt() throws IOException {
		byte[] buf = new byte[4];
		this.read(buf);
		return BytesTransfer.toInt(buf);
	}

	public short getShort() throws IOException {
		byte[] buf = new byte[2];
		this.read(buf);
		return BytesTransfer.toShort(buf);
	}

	public int getSeperator() throws IOException {//#ºÅ
		return (int) this.readByte();
	}

	public short getDataType() throws IOException {
		return this.getShort();
	}

	public byte[] getInfo() throws IOException {
		byte numOfByte[] = new byte[2];
		this.read(numOfByte);
		byte tmp[] = new byte[(int) numOfByte[1] - 5];
		System.out.println("this is read " + tmp.length);
		this.read(tmp);
		return tmp;
	}
}
