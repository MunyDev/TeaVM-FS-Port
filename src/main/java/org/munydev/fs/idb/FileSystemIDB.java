package org.munydev.fs.idb;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.munydev.fs.Client;
import org.munydev.fs.FSFile;
import org.munydev.fs.FSFileNativeStream;
import org.munydev.fs.FSFileSystem;
import org.munydev.fs.FileFetchCallback;
import org.munydev.fs.access.CompletionCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.indexeddb.*;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;
public class FileSystemIDB extends FSFileSystem {
	public static IDBDatabase idb = null;

	private static int idbVersion = 350;
	public static void reset() {
		IDBFactory.getInstance().deleteDatabase("fs");
	}
	public void initialize(CompletionCallback cc, CompletionCallback err) {
		initJS();
		IDBOpenDBRequest req = IDBFactory.getInstance().open("fs", idbVersion);
		req.addEventListener("success", (ev)->{
			idb = req.getResult();
			IDBObjectStore os = idb.transaction("filesystem","readwrite").objectStore("filesystem");
			String s = "ReadME234435345.txt";
			String data = "This file is created from TeaVM-FS-Port.";
			
			FileDataIDB fd = (FileDataIDB) JSObjects.create();
			ArrayBuffer ab = ArrayBuffer.create(data.getBytes(StandardCharsets.UTF_8).length);
			Uint8Array.create(ab).set(s.getBytes(StandardCharsets.UTF_8));
//			writeStringToArrayBuffer(data, ab);
			fd.setData(stringToBinary(data).getBuffer());
			
			fd.setKind("file");
			
			fd.setName(s);
			logParam(fd);
			os.put(fd, JSString.valueOf(s));
			cc.onComplete();
			idb.close();
//			FileSystemIDB.idb = req.getResult();
			
//			idb.addEventListener("upgradeneeded", new EventListener<Event>() {
//
//				@Override
//				public void handleEvent(Event evt) {
//					// TODO Auto-generated method stub
//					IDBObjectStore os = idb.createObjectStore("filesystem");
//					String s = "ReadME.txt";
//					String data = "This file is created from TeaVM-FS-Port.";
//					
//					FileDataIDB fd = (FileDataIDB) JSObjects.create();
//					
//					
//					fd.setData(stringToBinary(data));
//					fd.setKind("file");
//					fd.setName(s);
//					
//					os.add(fd);
//					
//				}
//				
//			});
			
		});
		
		req.addEventListener("upgradeneeded", new EventListener<Event>() {

			@Override
			public void handleEvent(Event evt) {
				// TODO Auto-generated method stub
				FileSystemIDB.idb = req.getResult();
				idb.createObjectStore("filesystem");
				evt.preventDefault();
			}
			
		});
		
	}
	@JSBody(script = "window.console.log(param);", params= {"param"})
	protected static native void logParam(JSObject param);
	protected static void openIndexedDB(Consumer<IDBDatabase> r) {
		IDBOpenDBRequest odb = IDBFactory.getInstance().open("fs", idbVersion);
		odb.setOnSuccess(new EventHandler() {

			

			@Override
			public void handleEvent() {
				// TODO Auto-generated method stub
				r.accept(odb.getResult());
				
			}
			
		});
	}
	
	public void writeStringToArrayBuffer(String s, ArrayBuffer ab) {
		Uint8Array ua = Uint8Array.create(ab);
		ua.set(s.getBytes(StandardCharsets.UTF_8));
	}
	private FileSystemIDB(CompletionCallback cc, CompletionCallback err) {initialize(cc, err);}
	//Utility Functions
	
	
	public boolean createNewFile(FSFile f, FileFetchCallback ffc) {
		openIndexedDB((idb)->{
			IDBTransaction trans = idb.transaction(new String[] {"filesystem"}, "readwrite");
			trans.setOnComplete(()->{
				System.out.println("File Creation successful");
				ffc.onComplete(null, "");
			});
			IDBObjectStore obj = trans.objectStore("filesystem");
			FileDataIDB fd = (FileDataIDB) JSObjects.create();
			
			fd.setName(f.getPath());
			fd.setKind("file");
			fd.setData(ArrayBuffer.create(1));
			
			obj.put(fd, JSString.valueOf(f.getPath())).setOnSuccess(()->{
		
				
			});
			
			idb.close();
			
			
			
		});
		return true;
		
	}

	public static FSFileSystem getInstance(CompletionCallback cc, CompletionCallback err) {
		// TODO Auto-generated method stub
		FileSystemIDB target = new FileSystemIDB(cc, err);
		
		return target;
	}
	ArrayBuffer result;
	@Override
	protected ArrayBuffer retrieveData(FSFile f, FileFetchCallback ffc) {
		// TODO Auto-generated method stub
		openIndexedDB((idb)->{
			IDBTransaction it = idb.transaction("filesystem", "readwrite");
			IDBObjectStore idbo = it.objectStore("filesystem");
			IDBGetRequest igr = idbo.get(JSString.valueOf(f.getPath()));
			igr.setOnSuccess(new EventHandler() {

				@Override
				public void handleEvent() {
					// TODO Auto-generated method stub
					ffc.onComplete(((FileDataIDB) igr.getResult()).getData(), "");
					
				}
				
			});
			igr.setOnError(()->{
				System.out.println("oof");
			});
			idb.close();
		});
		return result;
	}

	@Override
	protected boolean mkdirs(FSFile f, CompletionCallback cc) {
		// TODO Auto-generated method stub
		openIndexedDB((idb)->{
			IDBTransaction it = idb.transaction("filesystem", "readwrite");
			IDBObjectStore idbo = it.objectStore("filesystem");
			FileDataIDB fd = (FileDataIDB)JSObjects.create();
			String mainPath = f.getPath();
			String[] paths = mainPath.replace('\\', '/').split("/");
			String[] temp = new String[paths.length - 1];
			System.arraycopy(paths, 0, temp, 0, paths.length -1);
			String pathToDir = String.join("/", temp);
			String dirName = paths[paths.length -1];
			fd.setName(dirName);
			fd.setData(null);
			fd.setKind("directory");
			idbo.put(fd, JSString.valueOf(pathToDir)).setOnSuccess(()->{
				Client.log.info("Successfully created directory: "+f.getPath());
				
			});
			it.setOnComplete(()->{
				cc.onComplete();
			});
			it.setOnError(()->{
				cc.onComplete();
			});
			idb.close();
		});
		return true;
	}
	@Override
	protected boolean delete(FSFile f, CompletionCallback call) {
		// TODO Auto-generated method stub
		openIndexedDB((db)->{
			IDBTransaction it = db.transaction("filesystem", "readwrite");
			IDBObjectStore idbo = it.objectStore("filesystem");
			idbo.delete(JSString.valueOf(f.getPath())).setOnSuccess(()->{
				Client.log.info("Successfully delted File/Directory"+f.getPath());
			});
			
			it.setOnComplete(()->{
				call.onComplete();
			});
			
		});
		return false;
	}
	@Override
	protected void getInternalStream(FSFile f, boolean append, Consumer<FSFileNativeStream> cc) {
		// TODO Auto-generated method stub
		new FileSystemIDBOutputStream(f,append, (a)->{
			cc.accept(a);
		});
	}
	@Override
	protected void exists(FSFile f, Consumer<Boolean> cc) {
		// TODO Auto-generated method stub
		openIndexedDB((db)->{
			IDBTransaction it = db.transaction("filesystem", "readwrite");
			IDBObjectStore idbo = it.objectStore("filesystem");
			IDBCountRequest req = idbo.count(JSString.valueOf(f.getPath()));
			req.setOnSuccess(()->{
				cc.accept((req.getResult() > 0));
				
			});
			
			db.close();
		});
		
	}
	
}
