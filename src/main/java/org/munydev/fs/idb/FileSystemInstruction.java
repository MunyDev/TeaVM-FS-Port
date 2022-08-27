package org.munydev.fs.idb;

import org.munydev.fs.access.CompletionCallback;
//import org.munydev.fs.idb.FileSystemInstructionQueue.InstructionType;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSArray;

public class FileSystemInstruction  {
	public CompletionCallback cc;
	public int type;
	public Object[] params;
	public FileSystemInstruction(CompletionCallback cc, int type, Object...objects) {
		super();
		this.cc = cc;
		this.type = type;
		this.params = objects.clone();
	}
	
	
	
}
