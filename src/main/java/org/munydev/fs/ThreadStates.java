package org.munydev.fs;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class ThreadStates {
	
	@JSBody(script = "$rt_currentNativeThread = param;", params= {"thread"})
	public static native void setCurrentThread(JSObject thread);
	
	@JSBody(script = "return $rt_currentNativeThread;")
	public static native JSObject getCurrentThread();
}
