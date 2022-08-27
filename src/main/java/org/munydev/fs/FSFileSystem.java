package org.munydev.fs;

import java.util.function.Consumer;

import org.munydev.fs.access.CompletionCallback;
import org.munydev.fs.access.FileSystem;
import org.munydev.fs.idb.FileSystemIDB;
import org.teavm.jso.JSBody;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;

public abstract class FSFileSystem {
	private static FSFileSystem cachedInstance = null;
	private static boolean forceIDB;
	protected abstract ArrayBuffer retrieveData(FSFile f, FileFetchCallback ffc);
	protected abstract boolean mkdirs(FSFile f, CompletionCallback call);
	protected abstract boolean createNewFile(FSFile f, FileFetchCallback ffc);
	protected abstract boolean delete(FSFile f, CompletionCallback call);
	protected abstract void getInternalStream(FSFile f, boolean append, Consumer<FSFileNativeStream> cc);
	
	protected abstract void exists(FSFile f, Consumer<Boolean> cc);
	@JSBody(script = "window.te = new TextEncoder(); window.td = new TextDecoder();")
	protected static native void initJS();
	
	@JSBody(script = "return window.te.encode(s);", params= {"s"})
	public native static Uint8Array stringToBinary(String s);
	
	@JSBody(script = "return window.td.decode(ab);", params= {"ab"})
	public native static String binaryToString(ArrayBuffer ab);
	
	protected static FSFileSystem getInstance() {
		return getInstance(()->{}, ()->{});
	}
	protected static FSFileSystem getInstance(CompletionCallback cc, CompletionCallback err) {
		FSFileSystem target;
		
		initJS();
		if (cachedInstance == null) {
			if (FileSystem.isFeatureAvailable()) {
				cachedInstance = Client.propExists("forceIDB") || forceIDB ? FileSystemIDB.getInstance(cc, err) : FileSystem.getInstance(cc, err);
				
			}else {
				cachedInstance = FileSystemIDB.getInstance();
			}
		}
		return cachedInstance;
	}
	protected static void setForceIDB(boolean newValue) {
		FSFileSystem.forceIDB = newValue;
	}
}
