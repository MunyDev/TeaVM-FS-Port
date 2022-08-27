package org.munydev.fs.access;

import org.munydev.fs.FSFileNativeStream;
import org.teavm.interop.Async;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Uint8Array;
/**
 * Just gonna fake synchronization until we call close lol
 * @author Avadhut
 *
 */
public class FileSystemNativeStream extends FSFileNativeStream {
	JSArray<JSPromise> promises = JSArray.create();
	protected FileSystemWriteableFileStream internal;
	
	public FileSystemNativeStream(FileSystemWriteableFileStream internal) {
		this.internal = internal;
	}
	protected FileSystemNativeStream(FileSystemWriteableFileStream internal, boolean append, ArrayBuffer originalData) {
		this.internal = internal;
		if (!append) {
			internal.write(originalData);
		}
	}
	public ArrayBuffer intToAB(int b) {
		Uint8Array ua = Uint8Array.create(1);
		ua.set(new int[] {b});
		return ua.getBuffer();
	}
	public ArrayBuffer byteToAB(byte b) {
		Uint8Array ia = Uint8Array.create(1);
		ia.set(new int[] {b});
		return ia.getBuffer();
	}
	@Override
	public void write(int b) {
		// TODO Auto-generated method stub
		internal.write(intToAB(b));
	}

	@Override
	public void write(byte b) {
		// TODO Auto-generated method stub
		internal.write(byteToAB(b));
	}

	@Override
	public void writeUTF(String s) {
		// TODO Auto-generated method stub
		internal.write(s);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		internal.close();
	}

	@Override
	public void seek(double offset) {
		// TODO Auto-generated method stub
		internal.seek(offset);
	}

	@Override
	public void truncate(double size) {
		// TODO Auto-generated method stub
		internal.truncate(size);
	}
	@Override
	public void write(int b, CompletionCallback cc) {
		// TODO Auto-generated method stub
		internal.write(intToAB(b)).then((obj)->{
			cc.onComplete();
		});
	}
	@Override
	public void write(byte b, CompletionCallback cc) {
		// TODO Auto-generated method stub
		internal.write(byteToAB(b)).then((obj)->{
			cc.onComplete();
		});
	}
	@Override
	public void writeUTF(String s, CompletionCallback cc) {
		// TODO Auto-generated method stub
		internal.write(s).then((obj)->{
			cc.onComplete();
		});
	}
	@Override
	public void close(CompletionCallback cc) {
		// TODO Auto-generated method stub
		internal.close().then((object)->{
			cc.onComplete();
		});
	}
	@Override
	public void seek(double offset, CompletionCallback cc) {
		// TODO Auto-generated method stub
		internal.seek(offset).then((a)->{
			cc.onComplete();
		});
	}
	@Override
	public void truncate(double size, CompletionCallback cc) {
		// TODO Auto-generated method stub
		internal.truncate(size).then((obj)->{
			cc.onComplete();
		});
	}

}
