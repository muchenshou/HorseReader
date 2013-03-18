package com.reader.searchfile;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchFileSingleThread extends SearchFile{
	private String mSearchFile;
	LinkedBlockingQueue<String> mDirs = new LinkedBlockingQueue<String>();
	public SearchFileSingleThread(FindOneBehavior findone, String rootdir) {
		super(findone, rootdir);
		mSearchFile = rootdir;
		mDirs.add(mSearchFile);
	}

	@Override
	public void search() {
		String dir;
		while (true) {
			dir = mDirs.poll();
			if (dir != null) {
				File file = new File(dir);
				File[] files = file.listFiles(mFilter);
				if (files != null) {
					for (File f : files) {
						if (f.isDirectory()) {
							mDirs.add(f.getAbsolutePath());
						}
						if (f.isFile() && mFindOne != null) {
							mFindOne.accept(f);
						}
					}
				} 
			} else {
				// 搜索完毕
				// 判断其他线程是否也完毕
				return;
			}
		}
	}

}
