package org.munydev.fs;

import java.util.function.Consumer;

import org.munydev.fs.access.CompletionCallback;
import org.munydev.fs.access.FileSystem;
import org.teavm.jso.typedarrays.ArrayBuffer;

public class FSFile {
	private String path;
	public static String seperator = "/";
	public static char seperatorChar = '/';
	
	public FSFile(String path) {
		this.setPath(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public void delete() {
		FSFileSystem.getInstance().delete(this, ()->{});
	}
	public void delete(CompletionCallback cc) {
		FSFileSystem.getInstance().delete(this, cc);
	}
	public void exists(Consumer<Boolean> result) {
		FSFileSystem.getInstance().exists(this, result);
	}
	public void mkdirs() {
		FSFileSystem.getInstance().mkdirs(this, ()->{});
	}
	public void mkdirs(CompletionCallback cc) {
		FSFileSystem.getInstance().mkdirs(this, cc);
	}
	public void createNewFile() {
		FSFileSystem.getInstance().createNewFile(this, (d, mt)->{});
	}
	public void createNewFile(CompletionCallback cc) {
		FSFileSystem.getInstance().createNewFile(this, (d, mt)->{cc.onComplete();});
	}
}
