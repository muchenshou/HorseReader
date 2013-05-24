package com.reader.searchfile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SearchFileMultiThread extends SearchFile {
	LinkedBlockingQueue<String> mDirs = new LinkedBlockingQueue<String>();
	ExecutorService executor = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors());
	private final int THREAD_NUM = 4;

	class searchFunc implements Runnable {
		String dir;

		@Override
		public void run() {
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
	};

	public SearchFileMultiThread(FindOneBehavior findOneBehavior, String dir) {
		super(findOneBehavior, dir);
		mDirs.clear();
		mDirs.add(RootDir);
	}

	@Override
	public void search() {
		for (int i = 0; i < THREAD_NUM; i++) {
			executor.execute(new searchFunc());
		}
		try {
			executor.shutdown();
			executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// ignore
			// e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SearchFileMultiThread sf = new SearchFileMultiThread(
				new FindOneBehavior() {
					@Override
					public boolean accept(File pathname) {
						System.out.println(pathname.getAbsolutePath());
						return false;
					}
				}, "C:\\");// GHOST
		sf.search();
	}
}
