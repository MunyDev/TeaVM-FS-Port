package org.munydev.fs.idb;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.typedarrays.ArrayBuffer;

public interface FileDataIDB extends JSObject {
	
	
	@JSProperty
	void setKind(String name);
	
	@JSProperty
	String getKind();
	
	@JSProperty
	void setData(ArrayBuffer ab);
	
	@JSProperty
	ArrayBuffer getData();
	
	@JSProperty
	void setName(String newName);
	
	@JSProperty 
	String getName();
	

}
