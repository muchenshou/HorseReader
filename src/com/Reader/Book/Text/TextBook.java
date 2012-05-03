package com.Reader.Book.Text;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


import android.util.Log;

import com.Reader.Book.Book;
import com.Reader.Book.CharInfo;

public class TextBook implements Book{
	private File file;
	public TextBook(File f){
		file = f;
	}
	
	public void closeBook() {
		
	}

	
	public void excuteCmd(int cmd) {
		
	}
	public Byte gbkGetByte(int mEnd){
/*
		if (isHave(mEnd)) {
			if (bookBuffer == null){
				Log.d("is null", "null");
				return null;
				
			}
			return bookBuffer.get(mEnd % (BUFLEN));
		} else {
			try {
				bookBuffer = null;
				bookBuffer = book.getContent((mEnd / BUFLEN) * BUFLEN, (mEnd
						/ BUFLEN + 1)
						* BUFLEN - 1);
				this.bufferlocal = mEnd / BUFLEN;
				if(bookBuffer == null){
					return null;
				}
				//Log.d("word", "something");
				return gbkGetByte(mEnd);
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("debufinfo", e.getMessage() + e.toString());
				
			}
		}*/
		return -1;
	}
	public CharInfo gbkGetChar(int mEnd){/*		
		CharInfo charinfo = new CharInfo();
		if (mEnd >= book.size()-3){
			return null;
		}
		if(gbkGetByte(mEnd) < 0){
			
			byte bytes[]=new byte[2];
			bytes[0] = gbkGetByte(mEnd);
			bytes[1] = gbkGetByte(mEnd+1);
			String str;
			try {
				str = new String(bytes, "gbk");
				
				
				charinfo.character = str.charAt(0);
				
				charinfo.length = 2;

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return charinfo;
		}else{
			
			try {
				byte bytes[]=new byte[1];
				bytes[0] = '\0';// = bookBuffer.get(mEnd % (BUFLEN));
				String str = new String(bytes, "gbk");
				
				charinfo.character = str.charAt(0);
				charinfo.length = 1;
				StringBuffer b = new StringBuffer();
				b.append(charinfo.character);
				return charinfo;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}*/
		return null;
	}
	
	public int getContent(int filePointer, ByteBuffer buffer) throws IOException {
		return 0;
		/*if (l >= file.length()){
			l = (int) (file.length() -1);
		}
		FileChannel fcin = new FileInputStream(file).getChannel();
		MappedByteBuffer buffer = fcin.map(FileChannel.MapMode.READ_ONLY, filePointer, l-filePointer +1);
		
		ByteOrder order = ByteOrder.LITTLE_ENDIAN;
		
		ByteBuffer bytebuffer = buffer.duplicate();
		bytebuffer.order(order);	
		fcin.close();
		bytebuffer.clear();
		return bytebuffer;*/
	}

	public void openBook() {
		// TODO Auto-generated method stub
		
	}
	public int size() {
		// TODO Auto-generated method stub
		return (int) file.length();
	}

	public CharInfo getChar(int start) {
		// TODO Auto-generated method stub
		return null;
	}

	public CharInfo getPreChar(int mStart) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
