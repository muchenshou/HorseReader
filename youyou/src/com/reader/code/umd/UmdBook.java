/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.code.umd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.book.model.MarkupElement;
import com.reader.book.model.UmdParagraphElement;

public class UmdBook extends Book {
	public UmdInfo umdInfo = null;

	public UmdBook(File umd) throws IOException {
		bookFile = umd;
		UmdParse umdStream = new UmdParse(this, "r");
		umdInfo = umdStream.parseBook();
	}

	public File getFile() {
		return bookFile;
	}

	@Override
	public String getName() {
		return umdInfo.getName();
	}

	@Override
	public int getContent(int start, ByteBuffer contentBuffer) {
		int length = contentBuffer.capacity();
		byte[] content;
		try {
			if (this.getPointerInWhichBlock(start) == this
					.getPointerInWhichBlock(start + length - 1)) {

				content = getContentBlock(this.getPointerInWhichBlock(start),
						this.getPointerInBlockLocal(start), length);
				contentBuffer.put(content);
			} else {
				content = getContentBlock(this.getPointerInWhichBlock(start),
						this.getPointerInBlockLocal(start), UmdParse.BLOCKSIZE
								- this.getPointerInBlockLocal(start));
				if (content == null) {
					System.out.println(" Is null\t"
							+ this.getPointerInWhichBlock(start) + "\t"
							+ this.getPointerInBlockLocal(start) + "\t"
							+ UmdParse.BLOCKSIZE);
				}
				contentBuffer.put(content);
				for (int i = this.getPointerInWhichBlock(start) + 1; i < this
						.getPointerInWhichBlock(start + length - 1); i++) {
					content = getContentBlock(
							this.getPointerInWhichBlock(start), 0,
							UmdParse.BLOCKSIZE);
					contentBuffer.put(content);
				}
				content = getContentBlock(
						this.getPointerInWhichBlock(start + length - 1),
						0,
						UmdParse.BLOCKSIZE
								- this.getPointerInBlockLocal(start + length
										- 1));
				contentBuffer.put(content);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.EOFBOOK = true;
			return -1;
		}
		ByteOrder order = ByteOrder.LITTLE_ENDIAN;
		contentBuffer.order(order);
		return contentBuffer.limit();
	}

	public byte[] getContentBlock(int index, int start, int length)
			throws IOException {
		byte[] content = umdInfo.getBlock(index).content();
		ByteBuffer buf = ByteBuffer.wrap(content);
		byte[] data = new byte[length];

		buf.order(ByteOrder.LITTLE_ENDIAN);
		System.arraycopy(content, start, data, 0, length);
		return data;
	}

	private int getPointerInWhichBlock(int pointer) {
		return pointer / (UmdParse.BLOCKSIZE);
	}

	private int getPointerInBlockLocal(int pointer) {
		return pointer % (UmdParse.BLOCKSIZE);
	}

	@Override
	public void openBook() {

		// try {
		// this.umdStream = new UmdParse(this.bookFile, "r");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void closeBook() {
		// if (umdStream != null) {
		// try {
		// umdStream.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public void excuteCmd(int cmd) {

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.umdInfo.getSize();
	}

	@Override
	public void pushIntoList(BlockingQueue<MarkupElement> elements,List<Page> pages,LinkedList<AreaDraw> lines) {

		try {
			InputStream input = new BufferedInputStream(new UmdInputStream());
//			Charset charset = Charset.forName("gbk");
			MarkupElement element = new UmdParagraphElement(this);
			int read = 0;
			long size = bookFile.length();
			int ch = 0;
			
			element.getElementCursor().setRealFileStart(read);
			while ((ch = input.read()) !=-1) {
				read++;
				if (ch != 0x29 ) {
					if (element == null) {
						element = new UmdParagraphElement(this);
						element.getElementCursor().setRealFileStart(read - 1);
					}
					continue;
				}
				if (element != null) {
					element.getElementCursor().setRealFileLast(read - 2);
					elements.add(element);
					element.pushIntoLines(lines, pages);
				}
				element = null;
				ch = input.read(); // ch should be equal to 0x29 here
				read++;
				
			}
			if (element != null) {
				element.getElementCursor().setRealFileLast((int)size - 1);
				elements.add(element);
				element.pushIntoLines(lines, pages);
			}
			input.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class UmdInputStream extends InputStream {
		int mCursor = 0;

		@Override
		public int read() throws IOException {
			int index = mCursor / UmdParse.BLOCKSIZE;
			if (index >= umdInfo.blockList.size())
				return -1;
			byte[] content = umdInfo.getBlock(index).content();
			return content[mCursor++ % UmdParse.BLOCKSIZE] & 0x000000ff;
		}

	}
}
