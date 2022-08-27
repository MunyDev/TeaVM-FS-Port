package org.munydev.fs.idb;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.munydev.fs.FSFile;
import org.munydev.fs.FSFileInputStream;
import org.munydev.fs.FSFileInputStreamIntitializationCallback;
import org.munydev.fs.access.CompletionCallback;
import org.teavm.jso.core.JSString;
import org.teavm.jso.indexeddb.IDBDatabase;
import org.teavm.jso.indexeddb.IDBGetRequest;
import org.teavm.jso.indexeddb.IDBObjectStore;
import org.teavm.jso.indexeddb.IDBTransaction;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;

public class FileSystemIDBInputStream extends FSFileInputStream {
	private double ptr = 0;
	private IDBDatabase idb;
	private ArrayBuffer fullData;
	public FileSystemIDBInputStream(FSFile f,FSFileInputStreamIntitializationCallback cc) {
		super(f, cc);
		FileSystemIDB.openIndexedDB((idb)->{
			this.idb = idb;
			IDBTransaction idbt = idb.transaction("filesystem");
			IDBObjectStore objStore = idbt.objectStore("filesystem");
			IDBGetRequest req = objStore.get(JSString.valueOf(f.getPath()));
			req.setOnSuccess(()->{
				FileDataIDB fdi = (FileDataIDB) req.getResult();
				this.fullData = fdi.getData();
				cc.onComplete(this);
			});
			
		});
	}
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return Uint8Array.create(fullData).get((int) ptr++);
	}
	
	@Override
	public void close() {
		idb.close();
	}

}
