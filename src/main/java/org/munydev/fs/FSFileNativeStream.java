package org.munydev.fs;

import org.munydev.fs.access.CompletionCallback;
import org.munydev.fs.access.FileSystem;
import org.munydev.fs.access.FileSystemNativeStream;

public abstract class FSFileNativeStream {
	
	public abstract void write(int b);
	public abstract void write(int b, CompletionCallback cc);
	public abstract void write(byte b);
	public abstract void write(byte b, CompletionCallback cc);
	public abstract void writeUTF(String s);
	public abstract void writeUTF(String s, CompletionCallback cc);
	public abstract void close();
	public abstract void close(CompletionCallback cc);
	public abstract void seek(double offset);
	public abstract void seek(double offset, CompletionCallback cc);
	public abstract void truncate(double size);
	public abstract void truncate(double size,CompletionCallback cc);
	
	
	public static FSFileNativeStream getInstance(FSFile f, CompletionCallback cc, FileSystem fs) {
		
		return null;
	}
}
