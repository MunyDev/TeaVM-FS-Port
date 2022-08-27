package org.munydev.fs;

import org.teavm.jso.typedarrays.ArrayBuffer;

public interface FileFetchCallback {
	public void onComplete(ArrayBuffer ab, String mimeType);
}
