package org.munydev.fs.access;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSArray;

public interface FileSystemDirectoryHandle extends FileSystemHandle {
	
	JSArray<FileSystemHandle> values();
	
	void removeEntry(String name, FileSystemRemoveOptions opt);
	JSPromise getDirectoryHandle(String name);
	JSPromise getDirectoryHandle(String name, FileSystemHandleOptions fsho);
	JSPromise getFileHandle(String name);
	JSPromise getFileHandle(String name, FileSystemHandleOptions fsho);
}
