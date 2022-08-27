package org.munydev.fs.access;

import java.io.File;
import java.util.function.Consumer;

import org.munydev.fs.Client;
import org.munydev.fs.FSFile;
import org.munydev.fs.FSFileNativeStream;
import org.munydev.fs.FSFileSystem;
import org.munydev.fs.FileFetchCallback;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.typedarrays.ArrayBuffer;
import static org.munydev.fs.access.FileSystemOperation.OperationType.*;
public class FileSystem extends FSFileSystem {
	private FileSystemDirectoryHandle fsRootHandle;
	
	/**
	 * Stacks when promise not resolved.
	 */
	private FileSystemOperation[] fsOps = new FileSystemOperation[4096];
	
	private int stckCount = 0;
	private boolean started = false;
	/**
	 * Shows the selector for the root directory the file system will be working in
	 * @return Returns the root directory handle. If feature is not supported, returns null.
	 */
	public FileSystemDirectoryHandle showRootDirSelector(CompletionCallback cc, CompletionCallback error) {
		
		if (!isFeatureAvailable()) return null;
		
		nshowRootDirSelectorRW().then(new Callback() {

			@Override
			public void then(JSObject obj) {
				// TODO Auto-generated method stub
				fsRootHandle = (FileSystemDirectoryHandle) obj;
				Client.log.info("File System Available (in promise)");
				started = true;
				updatePushing();
				cc.onComplete();
			}
			
		}, (err)->{
			error.onComplete();
		});
		return fsRootHandle;
	}
	private FileSystem() {};
	private void updatePushing() {
		for (int i = 0; i < fsOps.length; i ++) {
			if (fsOps[i] == null) break;
			FileSystemOperation fsop = fsOps[i];
			fsOps[i] = null;
			
			handleOperation(fsop);
		}
	}
	private void handleOperation(FileSystemOperation fo) {
		switch (fo.optype) {
		case CREATE_NEW_FILE:
			createNewFile(fo.file, fo.ffc);
			break;
		
		case MKDIRS:
			mkdirs(fo.file, fo.cc);
			break;
		default:
			break;
		
		}
	}
	@Override
	public boolean createNewFile(FSFile ff, FileFetchCallback ffc) {
		if (!started) {
			fsOps[stckCount++] = new FileSystemOperation(CREATE_NEW_FILE, ff, ffc);
			return false;
		}
		String path = ff.getPath();
		path = path.replace('\\', '/');
		String[] s = path.split("/");
		String[] dirs = new String[s.length-1];
		System.arraycopy(s, 0, dirs, 0, s.length-1);
		prevHandle = fsRootHandle;
		curHandle = fsRootHandle;
		recursionState = 0;
		FileSystemHandleOptions params = JSObjects.<FileSystemHandleOptions>create();
		
		params.setCreate(false);
FileSystemHandleOptions params2 = JSObjects.<FileSystemHandleOptions>create();
		
		params2.setCreate(true);
		resetStates();

		cdRecursion(formatFilePath(ff.getPath()), params, (handle)->{
			
			handle.getFileHandle(s[s.length-1], params2).then((a)->{
				ffc.onComplete(null, "");
			});
			
		});
		return true;
	}
	
	
	
	public static FileSystem getInstance(CompletionCallback cc) {
		FileSystem f = new FileSystem();
		f.showRootDirSelector(cc, ()->{});
		
		return FileSystem.isFeatureAvailable() ? f : null;
	}
	public static FileSystem getInstance(CompletionCallback cc, CompletionCallback error) {
		FileSystem f = new FileSystem();
		f.showRootDirSelector(cc, error);
		
		return FileSystem.isFeatureAvailable() ? f : null;
	}
	
//	@Async
//	public static native FileSystemDirectoryHandle getRootHandleRW();
//	
//	public static void getRootHandleRW(AsyncCallback<FileSystemDirectoryHandle> ac) {
//		
//	};
	
	@JSBody(script = "return window.showDirectoryPicker({mode: \"readwrite\"});")
	private static native JSPromise nshowRootDirSelectorRW();
	
	@JSBody(script = "return \"FileSystemFileHandle\" in self;")
	public static native boolean isFeatureAvailable();

	@Override
	public ArrayBuffer retrieveData(FSFile f, FileFetchCallback ffc) {
		// TODO Auto-generated method stub
		
		String[] parsed = f.getPath().replace('\\', '/').split("/");
		String[] temp = new String[parsed.length -1];
		System.arraycopy(parsed, 0, temp, 0, parsed.length -1);
		FileSystemHandleOptions params = JSObjects.<FileSystemHandleOptions>create();
		params.setCreate(false);
		recursionState = 0;
		resetStates();
		cdRecursion(temp, params, (handle)->{
			handle.getFileHandle(parsed[parsed.length - 1], params).then((obj)->{
				FileSystemFileHandle fi = (FileSystemFileHandle) obj;
				fi.getFile().then((obj2)->{
					JSFile file = (JSFile) obj2;
					file.arrayBuffer().then((ab)->{
						ffc.onComplete((ArrayBuffer) ab, "");
					});
				});
			});
		});
		return null;
	}
	private void resetStates() {
		prevHandle = fsRootHandle;
		curHandle = fsRootHandle;
		recursionState = 0;
	}
	private FileSystemDirectoryHandle prevHandle = fsRootHandle;
	private FileSystemDirectoryHandle curHandle = fsRootHandle;
	@Override
	protected boolean mkdirs(FSFile f,CompletionCallback cc) {
		// TODO Auto-generated method stub
		
		if (!started) {
			fsOps[stckCount++] = new FileSystemOperation(MKDIRS, f, cc);
			return false;
		}
		String path = f.getPath();
		
		path = path.replaceAll("\\\\", "/");
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] a = path.split("/");
		Client.log.info(path);
		
		prevHandle = fsRootHandle;
		curHandle = fsRootHandle;
//		int fileIndex = 0;
		FileSystemHandleOptions params = JSObjects.<FileSystemHandleOptions>create();
		recursionState = 0;
		params.setCreate(true);
		cdRecursion(a, params, (handle)->{
			cc.onComplete();
		});
		
		return true;
	}
	private static int recursionState = 0;
	protected boolean cdRecursion(String[] s, FileSystemHandleOptions params, Consumer<FileSystemDirectoryHandle> lastHandle) {
		if (s.length > 150) return false;
		if (recursionState >= s.length) {
			lastHandle.accept(prevHandle);
			
			return true;
		}
		prevHandle.getDirectoryHandle(s[recursionState], params).then((obj)->{
			curHandle = (FileSystemDirectoryHandle) obj;
			prevHandle = curHandle;
			recursionState++;
			cdRecursion(s, params, lastHandle);
		});
		return true;
	}
	protected String[] formatDirectoryPath(String path) {
		return (path.replace('\\', '/').startsWith("/") ? path.substring(1) : path).replace('\\', '/').split("/");
	}
	protected String[] formatFilePath(String path) {
		String[] original = formatDirectoryPath(path);
		String[] newz = new String[original.length - 1];
		
		System.arraycopy(original, 0, newz, 0, original.length - 1);
		return newz;
	}
	protected String getFileName(String[] formatted) {
		return formatted[formatted.length -1 ];
	}
	@Override
	protected boolean delete(FSFile f, CompletionCallback call) {
		// TODO Auto-generated method stub
		FileSystemHandleOptions params = (FileSystemHandleOptions) JSObjects.create();
		resetStates();
		cdRecursion(formatFilePath(f.getPath()), params, (handle)->{
			String x = getFileName(formatDirectoryPath(f.getPath()));
			FileSystemRemoveOptions opt = JSObjects.create();
			opt.setRecursive(true);
			handle.removeEntry(x, opt);
		});
		
		return true;
	}
	@Override
	public void getInternalStream(FSFile f,boolean append, Consumer<FSFileNativeStream> cc) {
		// TODO Auto-generated method stub
		FileSystemHandleOptions params = (FileSystemHandleOptions) JSObjects.create();
		resetStates();
		cdRecursion(formatFilePath(f.getPath()), params, (handle)->{
			handle.getFileHandle(getFileName(formatDirectoryPath(f.getPath()))).then((obj)->{
				FileSystemFileHandle fsf = (FileSystemFileHandle) obj;
				fsf.createWritable().then((obj2)->{
					FileSystemWriteableFileStream fswf = (FileSystemWriteableFileStream) obj2;
					if (append) {
					 fsf.getFile().then((file)->{
							JSFile f1 = (JSFile) file;
							f1.arrayBuffer().then((ab)->{
								cc.accept(new FileSystemNativeStream(fswf, true,(ArrayBuffer) ab));
							});
					 });
					}else {
						cc.accept(new FileSystemNativeStream(fswf));
					}
					
				});
			});
		});
	}
	@Override
	protected void exists(FSFile f, Consumer<Boolean> cc) {
		// TODO Auto-generated method stub
		FileSystemHandleOptions params = (FileSystemHandleOptions) JSObjects.create();
		resetStates();
		cdRecursion(formatFilePath(f.getPath()), params, (handle)->{
			handle.getFileHandle(getFileName(formatDirectoryPath(f.getPath()))).then((obj)->{
				cc.accept(true);
			}, (err)->{
				cc.accept(false);
			});
			
		});
		return;
	}
	
	
}
