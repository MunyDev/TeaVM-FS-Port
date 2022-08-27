package org.munydev.fs.access;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface FileSystemHandle extends JSObject {
	@JSProperty
	public String getKind();
	
	@JSProperty
	public String getName();
	
	@JSMethod
	public boolean isSameEntry(FileSystemHandle fs);
}
