package org.munydev.fs.access;


import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.typedarrays.ArrayBuffer;

public interface FileSystemWriteableFileStream extends JSObject {
	JSPromise seek(double num);
	
	JSPromise truncate(double size);
	
	JSPromise write(ArrayBuffer src);
	JSPromise write(String src);
	
	JSPromise close();
	
	
}
