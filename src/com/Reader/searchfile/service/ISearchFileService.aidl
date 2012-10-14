package com.reader.searchfile.service;
import com.reader.searchfile.service.ISearchFileServiceCallBack;
interface ISearchFileService {
    void searchFileForExts(in String []exts);
    void registerCallback(in ISearchFileServiceCallBack cb);
    void unregisterCallback(in ISearchFileServiceCallBack cb);
}
