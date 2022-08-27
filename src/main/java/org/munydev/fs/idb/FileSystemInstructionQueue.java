package org.munydev.fs.idb;

import org.munydev.fs.access.CompletionCallback;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSObjects;

public class FileSystemInstructionQueue {
	
	public static final int 	WRITE= 		0x0;
	public static final int 	WRITE_INT= 	0x1;
	public static final int 	SEEK = 		0x2;
	public static final int 	CLOSE = 	0x3;
	public static final int 	WRITEUTF = 	0x4;
	public static final int 	TRUNCATE =	0x5;
	
	
	
	private FileSystemInstruction[] fsis = new FileSystemInstruction[255];
	private FileSystemIDBOutputStream fsidb;
	private int idx = 0;
	public FileSystemInstructionQueue(FileSystemIDBOutputStream fsidbStream) {
		this.fsidb = fsidbStream;
	}
	
	public void push(int type,CompletionCallback cc, Object... objs) {
		fsis[idx++] = new FileSystemInstruction(null, type, objs);
		
	}


	public void flush()
	{
		int ptr = 0;
		for (FileSystemInstruction fsi:fsis) {
			if (fsi == null) {
				break;
			}
			fsidb.onQueueFlush(fsi);
			fsis[ptr] = null;
			ptr++;
			
			
			
		}
	}
}
