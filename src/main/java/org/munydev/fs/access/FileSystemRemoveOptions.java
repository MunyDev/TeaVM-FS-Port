package org.munydev.fs.access;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface FileSystemRemoveOptions extends JSObject {
	
	@JSProperty
	void setRecursive(boolean val);
	
	@JSProperty
	boolean getRecursive();
}
