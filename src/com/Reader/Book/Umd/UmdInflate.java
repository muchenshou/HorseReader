/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Book.Umd;

import java.io.IOException;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

class UmdInflate {
	private UmdBook umdbook;

	public UmdInflate(UmdBook u) {
		umdbook = u;
	}

	public byte[] Inflate(byte[] content) {
		int err;
		int uncomprLen = 40000;
		byte[] uncompr;
		uncompr = new byte[uncomprLen];

		ZStream d_stream = new ZStream();

		d_stream.next_in = content;
		d_stream.next_in_index = 0;
		d_stream.next_out = uncompr;
		d_stream.next_out_index = 0;

		err = d_stream.inflateInit();
		CHECK_ERR(d_stream, err, "inflateInit");

		while (d_stream.total_out < uncomprLen
				&& d_stream.total_in < content.length) {
			d_stream.avail_in = d_stream.avail_out = 1; /* force small buffers */
			err = d_stream.inflate(JZlib.Z_NO_FLUSH);
			if (err == JZlib.Z_STREAM_END)
				break;
			CHECK_ERR(d_stream, err, "inflate");
		}

		err = d_stream.inflateEnd();
		CHECK_ERR(d_stream, err, "inflateEnd");
		return uncompr;
	}

	public byte[] getContentBlock(int index, int start, int length)
			throws IOException {
		byte[] content = null;

		int err;
		content = new byte[length];
		byte[] in = umdbook.getBlockData(index);
		ZStream d_stream = new ZStream();
		d_stream.next_in = in;
		d_stream.next_in_index = 0;
		d_stream.next_out = content;
		d_stream.next_out_index = 0;

		err = d_stream.inflateInit();
		CHECK_ERR(d_stream, err, "inflateInit");
		while (d_stream.total_in < in.length) {
			d_stream.avail_in = d_stream.avail_out = 1; /* force small buffers */
			if (d_stream.total_out <= start) {
				d_stream.next_out_index = 0;
			}
			if (d_stream.total_out > start+length-1)
				break;
			err = d_stream.inflate(JZlib.Z_NO_FLUSH);
			if (err == JZlib.Z_STREAM_END) {
				System.out.println("z-stream-end");
				break;
			}
			CHECK_ERR(d_stream, err, "inflate2");
		}

		err = d_stream.inflateEnd();
		CHECK_ERR(d_stream, err, "inflateEnd");

		return content;
	}

	static void CHECK_ERR(ZStream z, int err, String msg) {
		if (err != JZlib.Z_OK) {
			if (z.msg != null)
				System.out.print(z.msg + " ");
			System.out.println(msg + " error: " + err);

			System.exit(1);
		}
	}
}