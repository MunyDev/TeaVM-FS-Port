package org.munydev.fs.access;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface FileSystemHandleOptions extends JSObject {
	@JSProperty
	void setCreate(boolean b);
	
	@JSProperty
	boolean getCreate();
}
