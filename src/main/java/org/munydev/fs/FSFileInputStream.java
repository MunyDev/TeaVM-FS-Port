package org.munydev.fs;

import java.io.IOException;
import java.io.InputStream;

import org.munydev.fs.access.CompletionCallback;
import org.munydev.fs.access.JSFile;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;

public class FSFileInputStream extends InputStream {
	public Uint8Array data = Uint8Array.create(0);
	private String url;
	int ptr = 0;
	public FSFileInputStream(FSFile ff, FSFileInputStreamIntitializationCallback cc) {
		FSFileSystem.getInstance().retrieveData(ff, (ab, mimeType)->{
			data = Uint8Array.create(ab);
			url = createBlobUrl(createBlob(ab));
			cc.onComplete(this);
		});
		
	}
	@JSBody(script = "return new Blob([ab], {type: mimeType});", params= {"ab", "mimeType"})
	private static native JSObject createBlob(ArrayBuffer ab, String mimeType);
	
	@JSBody(script = "return new Blob([ab], {type: \"\"});", params= {"ab"})
	private static native JSObject createBlob(ArrayBuffer ab);
	
	@JSBody(script = "return URL.createObjectURL(blob);", params = {"blob"})
	private static native String createBlobUrl(JSObject blob);
	
	@JSBody(script = "return URL.createObjectURL(file);", params = {"file"})
	private static native String createFileUrl(JSFile file);
	
	
	@JSBody(script = "URL.revokeObjectURL(url);", params= {"url"})
	private static native String releaseUrl(String url);
//	private static byte[] writeABToByteArray(ArrayBuffer ab) {
//		Uint8Array bytes = Uint8Array.create(ab);
//		byte[] result = new byte[ab.getByteLength()];
//		for (int i = 0; i < ab.getByteLength(); i++) {
//			result[i] = (byte) bytes.get(i);
//		}
//		return result;
//		
//	}
	
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		if (ptr >= data.getByteLength()) return -1;
		
		return data.get(ptr++);
	}
	public String getUrl() {
		return url;
	}
	@Override
	public void close() {
		releaseUrl(url);
	}
}
