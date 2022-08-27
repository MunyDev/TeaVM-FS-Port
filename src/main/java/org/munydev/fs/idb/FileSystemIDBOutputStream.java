package org.munydev.fs.idb;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.teavm.jso.JSBody;
import org.munydev.fs.FSFile;
import org.munydev.fs.FSFileNativeStream;
import org.munydev.fs.FSFileSystem;
import org.munydev.fs.access.CompletionCallback;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSString;
import org.teavm.jso.indexeddb.IDBDatabase;
import org.teavm.jso.indexeddb.IDBGetRequest;
import org.teavm.jso.indexeddb.IDBObjectStore;
import org.teavm.jso.indexeddb.IDBTransaction;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;

import static org.munydev.fs.idb.FileSystemInstructionQueue.*;
public class FileSystemIDBOutputStream extends FSFileNativeStream {
	int ptr;
	private IDBDatabase idb;
	private FileSystemInstructionQueue queue;
	private FSFile file;
	private ArrayBuffer origBuf;
	private boolean started = false;
	public FileSystemIDBOutputStream(FSFile f, Consumer<FileSystemIDBOutputStream> cc) {
		this.file = f;
		this.queue = new FileSystemInstructionQueue(this);
		FileSystemIDB.openIndexedDB((idb2)->{
			this.idb = idb2;
			IDBTransaction idbt = idb.transaction("filesystem", "readwrite");
			IDBObjectStore objStore = idbt.objectStore("filesystem");
			IDBGetRequest idbg = objStore.get(JSString.valueOf(f.getPath()));
			idbg.setOnSuccess(()->{
				FileDataIDB fdi = (FileDataIDB) idbg.getResult();
				origBuf = ArrayBuffer.create(0);
				started = true;
				queue.flush();
				cc.accept(this);
			});
		});
	}
	public FileSystemIDBOutputStream(FSFile f,boolean append,  Consumer<FileSystemIDBOutputStream> cc) {
		this.file = f;
		this.queue = new FileSystemInstructionQueue(this);
		FileSystemIDB.openIndexedDB((idb2)->{
			this.idb = idb2;
			IDBTransaction idbt = idb.transaction("filesystem", "readwrite");
			IDBObjectStore objStore = idbt.objectStore("filesystem");
			IDBGetRequest idbg = objStore.get(JSString.valueOf(f.getPath()));
			idbg.setOnSuccess(()->{
				FileDataIDB fdi = (FileDataIDB) idbg.getResult();
				origBuf = ArrayBuffer.create(0);
				if (append) {
					origBuf = fdi.getData();
					ptr = fdi.getData().getByteLength();
				}
				
				started = true;
//				queue.flush();
				cc.accept(this);
			});
		});
	}
	@Override
	public void write(int b) {
		// TODO Auto-generated method stub
		write(b, null);
	}
	public void onQueueFlush(FileSystemInstruction inst) {
		Object[] params = inst.params;
		CompletionCallback cc = inst.cc;
		switch (inst.type) {
		case WRITE:
			write((byte) params[0], cc);
			break;
		case WRITE_INT:
			write((int) params[0], cc);
			break;
		case CLOSE:
			close(cc);
			break;
		case TRUNCATE:
			truncate((double) params[0], cc);
			break;
		case WRITEUTF:
			writeUTF((String) params[0], cc);
			break;
		case SEEK:
			seek((double) params[0], cc);
			break;
		
		default:
			System.out.println("Unknown instruction(IDB FileSystem)");
			break;
		}
	}
	@Override
	public void write(int b, CompletionCallback cc) {
		// TODO Auto-generated method stub
		//Avoid null ptr exception
		CompletionCallback call = cc != null ? cc: ()->{};
//		log(JSString.valueOf("write int"));
		if (ptr + 1 >= origBuf.getByteLength()) {
//			log(JSString.valueOf("resizing"));
			resizeTo(origBuf.getByteLength() + 1);
		}
		Uint8Array buf = Uint8Array.create(origBuf);
		buf.set((int) ptr, (short) b);
		ptr++;
		call.onComplete();
	}

	@Override
	public void write(byte b) {
		// TODO Auto-generated method stub
		write(b, null);
	}

	@Override
	public void write(byte b, CompletionCallback cc) {
		// TODO Auto-generated method stub
		
		CompletionCallback call = cc != null ? cc: ()->{};
//		log(JSString.valueOf("write int"));
		if (ptr + 1 >= origBuf.getByteLength()) {
			resizeTo(origBuf.getByteLength()+1);
		}
		Uint8Array buf = Uint8Array.create(origBuf);
		buf.set((int) ptr, (short) b);
		ptr++;
		call.onComplete();
	}

	@Override
	public void writeUTF(String s) {
		// TODO Auto-generated method stub
		writeUTF(s, null);
	}
	 
	public void resizeTo(int s) {
		ArrayBuffer newLength = ArrayBuffer.create(s);
		Uint8Array.create(newLength).set(Uint8Array.create(origBuf));
		origBuf = newLength;
	}
	@Override
	public void writeUTF(String s, CompletionCallback cc) {
		// TODO Auto-generated method stub
		CompletionCallback call = cc != null ? cc: ()->{};
		if (!started) {
			queue.push(WRITEUTF, cc, s);
		}
		Uint8Array buf = Uint8Array.create(origBuf);
		Uint8Array stb = FSFileSystem.stringToBinary(s);
		ptr+= stb.getByteLength();
		if (ptr >= origBuf.getByteLength()) {
			resizeTo(ptr);
		}
		buf.set(stb, ptr);
		
		call.onComplete();
	}
	public void writeArrayBuffer(ArrayBuffer ab) {
		
	}
	public void writeArrayBuffer(ArrayBuffer ab, CompletionCallback cc) {
		CompletionCallback real = cc != null ? cc : ()->{};
		
		Uint8Array buf = Uint8Array.create(origBuf);
		Uint8Array stb = Uint8Array.create(ab);
		
		ptr+= stb.getByteLength();
		if (ptr >= buf.getByteLength()) {
			resizeTo(ptr);
		}
		buf.set(stb, ptr);
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		close(null);
	}
	@JSBody(script = "window.console.log(params);", params= {"params"})
	private static native void log(JSObject params);
	
	@Override
	public void close(CompletionCallback cc) {
		// TODO Auto-generated method stub
		CompletionCallback call = cc != null ? cc: ()->{};
		if (!started) {
			queue.push(CLOSE, call);
			return;
		}
		
		log(origBuf);
		FileSystemIDB.openIndexedDB((idb)->{
			IDBObjectStore idbo = idb.transaction("filesystem", "readwrite").objectStore("filesystem");
			IDBGetRequest idbg = idbo.get(JSString.valueOf(file.getPath()));
			idbg.setOnSuccess(()->{
				FileDataIDB fdi = (FileDataIDB) idbg.getResult();
				fdi.setData(origBuf);
				fdi.setKind("file");
				fdi.setName(file.getPath());
				
				idbo.put(fdi, JSString.valueOf(file.getPath()));
				
				
			});
			idb.close();
		});
	}

	@Override
	public void seek(double offset) {
		// TODO Auto-generated method stub
		seek(offset, null);
	}

	@Override
	public void seek(double offset, CompletionCallback cc) {
		// TODO Auto-generated method stub
		ptr = (int) offset;
	}

	@Override
	public void truncate(double size) {
		// TODO Auto-generated method stub
		
		
		
		truncate(size, null);
	}

	@Override
	public void truncate(double size, CompletionCallback cc) {
		// TODO Auto-generated method stub
		if (!started) {
			queue.push(TRUNCATE, cc, size);
			return;
		}
		CompletionCallback call = cc != null ? cc: ()->{};
		Uint8Array ua = Uint8Array.create((int)size);
		Uint8Array original = Uint8Array.create(origBuf);
		int len0 = ua.getLength();
		int len1 = original.getLength();
		
		if (len0 < len1) {
//			ArrayBuffer og = original.getBuffer().slice(0, ua.getByteLength());
			
		}
		else {
			
		}
		
		this.origBuf = ua.getBuffer();
		call.onComplete();
	}
	

}
