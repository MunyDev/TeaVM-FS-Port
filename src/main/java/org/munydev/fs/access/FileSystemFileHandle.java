package org.munydev.fs.access;

public interface FileSystemFileHandle extends FileSystemHandle {
	JSPromise getFile();
	
	JSPromise createWritable();
}
