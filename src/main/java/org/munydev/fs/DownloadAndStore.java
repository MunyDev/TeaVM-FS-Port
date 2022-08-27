package org.munydev.fs;

import java.io.IOException;

import org.munydev.fs.access.CompletionCallback;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;

public class DownloadAndStore {
	
	@SuppressWarnings("unused")
	public static void downloadAndStore(FSFile f, String url, CompletionCallback cc) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setResponseType("arraybuffer");
		xhr.setOnReadyStateChange(()->{
//			System.out.println("state changed"+xhr.getReadyState());
			if (xhr.getReadyState() == 4) {
//				System.out.println("state finished");
				ArrayBuffer ab = (ArrayBuffer) xhr.getResponse();
				Uint8Array ua = Uint8Array.create(ab);
				
				FSFileOutputStream fis = new FSFileOutputStream(f, (s)->{
					for (int i = 0; i < ua.getLength(); i++) {
						try {
							s.write((char)ua.get(i));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					s.close();
					cc.onComplete();
				});
				
			}
		});
		xhr.open("GET", url, true);
		xhr.send();
	}
}
