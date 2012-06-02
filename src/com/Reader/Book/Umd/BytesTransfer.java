package com.Reader.Book.Umd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BytesTransfer {
	public static int toInt(byte[] bytes) {
		ByteBuffer shortBuf = ByteBuffer.wrap(bytes);
		shortBuf.order(ByteOrder.LITTLE_ENDIAN);
		return shortBuf.getInt();
	}

	public static short toShort(byte[] bytes) {
		ByteBuffer shortBuf = ByteBuffer.wrap(bytes);
		shortBuf.order(ByteOrder.LITTLE_ENDIAN);
		return shortBuf.getShort();
	}

	public static void byteAlign(byte[] array) {
		int i = array.length;
		if (i % 2 == 1)
			return;
		for (int j = 0; j < i; j += 2) {
			byte b = array[j];
			array[j] = array[j + 1];
			array[j + 1] = b;
		}
	}
}
