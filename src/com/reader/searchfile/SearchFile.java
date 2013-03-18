package com.reader.searchfile;

import java.io.File;
import java.io.FileFilter;

public abstract class SearchFile {
	public interface FindOneBehavior {
		public boolean accept(File pathname);
	}
	String RootDir;
	FindOneBehavior mFindOne;
	FileFilter mFilter = null;
	public SearchFile(FindOneBehavior findone, String rootdir) {
		RootDir = rootdir;
		mFindOne = findone;
	}
	public void setFilter(FileFilter filter){
		mFilter = filter;
	}
	abstract public void search();
}
