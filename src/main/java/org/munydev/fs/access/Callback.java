package org.munydev.fs.access;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface Callback extends JSObject{
	
	void then(JSObject obj);
}
