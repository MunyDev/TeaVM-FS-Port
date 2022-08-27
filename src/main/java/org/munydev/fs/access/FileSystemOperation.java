package org.munydev.fs.access;

import org.munydev.fs.FSFile;
import org.munydev.fs.FileFetchCallback;

public class FileSystemOperation {
	public enum OperationType {
		MKDIRS,
		CREATE_NEW_FILE,
		FETCH_DATA
		
	}
	
	public OperationType optype;
	public FSFile file;
	public FileFetchCallback ffc;
	public CompletionCallback cc;
	public FileSystemOperation(OperationType optype, FSFile file) {
		super();
		this.optype = optype;
		this.file = file;
	}
	public FileSystemOperation(OperationType optype, FSFile file, FileFetchCallback ffc) {
		super();
		this.optype = optype;
		this.file = file;
		this.ffc = ffc;
	}
	public FileSystemOperation(OperationType optype, FSFile file, CompletionCallback ffc) {
		super();
		this.optype = optype;
		this.file = file;
		this.cc = ffc;
	}
	
}

