package org.munydev.fs.access;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface JSPromise extends JSObject {
	JSObject then(Callback s);
	JSObject then(Callback s, Callback error);
	
	@JSMethod("catch")
	void error(Callback s);
}
