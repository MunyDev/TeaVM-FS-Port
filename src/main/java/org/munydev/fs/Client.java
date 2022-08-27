package org.munydev.fs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.munydev.fs.access.FileSystem;
import org.munydev.fs.idb.FileSystemIDB;
import org.slf4j.Logger;
import org.teavm.extras.slf4j.TeaVMLoggerFactory;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSDate;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLTextAreaElement;

public class Client {
	public static FSFileSystem ffs;
	public static Logger log;
	public static JSObject realThread;
	@JSBody(script = "window.searchp = new URLSearchParams(document.location.search);")
	public static native void initProperties();
	@JSBody(script = "return window.searchp.has(param);",  params= {"param"})
	public static native boolean propExists(String param);
	@JSBody(script = "return window.searchp.get(param);", params= {"param"})
	public static native boolean getProperty(String param);
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HTMLDocument doc = HTMLDocument.current();
		TeaVMLoggerFactory f = new TeaVMLoggerFactory();
		initProperties();
		log =  f.getLogger("");
		
		log.info("INTITIATING FILE SYSTEM");
		
//		doc.getBody().setInnerHTML(doc.getBody().getInnerHTML() + "<br />");
		HTMLTextAreaElement htae = (HTMLTextAreaElement) doc.createElement("textarea");
//		htae.getStyle().setProperty("resize", "none");
		htae.setPlaceholder("Content of reading.txt");
		
//		doc.getBody().setInnerHTML(doc.getBody().getInnerHTML() + "<br />");
		HTMLButtonElement hbe = (HTMLButtonElement) doc.createElement("button");
		hbe.setInnerHTML("Save");
		
		hbe.addEventListener("click", new EventListener<MouseEvent>() {

			@SuppressWarnings("resource")
			@Override
			public void handleEvent(MouseEvent evt) {
				// TODO Auto-generated method stub
//				log.info("error");
				new FSFileOutputStream(new FSFile("librandom/socket/reading.txt"), (out)->{
					
					PrintWriter pw = new PrintWriter(out);
					pw.print(htae.getValue());
					pw.close();
					
				});
				evt.preventDefault();
				
			}
			
		});
		doc.getBody().appendChild(htae);
//		doc.getBody().setInnerHTML(doc.getBody().getInnerHTML() + "<br />");
		doc.getBody().appendChild(hbe);
//		doc.getBody().setInnerHTML(doc.getBody().getInnerHTML() + "<br />");
		hbe.setDisabled(true);
		HTMLButtonElement be = (HTMLButtonElement) doc.createElement("button");
		be.setId("rootSelectorButton");
		
		be.addEventListener("click", new EventListener<Event>() {

			@Override
			public void handleEvent(Event evt) {
				// TODO Auto-generated method stub
				
//				ffs = FSFileSystem.getInstance();
//				ffs.mkdirs(new FSFile("/librandom/fsck/drawfromfile/stikc"), ()->{
//					ffs.createNewFile(new FSFile("ReadME234435345.txt"), (a)->{
//						ffs.createNewFile(new FSFile("librandom/helloworld.txt"), (ab)->{
//							System.out.println("Created File 1");
//							ffs.createNewFile(new FSFile("librandom/root.txt"), (e)->{
//								System.out.println("Created File 2");
//								
//								
//									try {
//										new FSFileInputStream(new FSFile("ReadME234435345.txt"), (fsf)->{
//											BufferedReader br = new BufferedReader(new InputStreamReader(fsf));
//											String line;
//											try {
//												while ((line = br.readLine()) != null) {
//													log.info(">"+ line);
//												}
//											} catch (IOException e1) {
//												// TODO Auto-generated catch block
//												e1.printStackTrace();
//											}
//										}).close();
//										FSFileOutputStream fos = new FSFileOutputStream(new FSFile("librandom/root.txt"), (s)->{
//											PrintWriter pw = new PrintWriter(s);
//											pw.println("Subwoofer-C418");
//											pw.print("Line 2");
//											pw.close();
//											
//										});
//										Window.setTimeout(()->{
//											new FSFileInputStream(new FSFile("librandom/root.txt"), (in)->{
//												BufferedReader br = new BufferedReader(new InputStreamReader(in));
//												String line;
//												
//												try {
//													while ((line = br.readLine()) != null) {
//														log.info("2> "+line);
//													}
//													in.close();
//												} catch (IOException e1) {
//													// TODO Auto-generated catch block
//													e1.printStackTrace();
//												}
//
//											});
//										}, 5000);
//										
//									} catch (IOException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
//									
//									
//									
//							});
//						});
//					});
//				});
				// Recommended api
				hbe.setDisabled(false);
				System.setProperty("line.seperator", "\n");
				new FSFile("/librandom/socket/test").mkdirs(()->{
					FSFile fd = new FSFile("librandom/socket/reading.txt");
					fd.exists((exists)->{
						if (!exists) {
							fd.createNewFile();
							log.info("creating file reading");
						} else {
							new FSFileInputStream(fd, (in)->{
								BufferedReader br = new BufferedReader(new InputStreamReader(in));
								String line;
								
								try {
									while ((line = br.readLine()) != null) {
										htae.setValue(htae.getValue() + line + "\n");
									}
									htae.setValue(htae.getValue().substring(0, htae.getValue().length() - 1));
									in.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							});
						}
					});
					new FSFile("testing.txt").createNewFile(()->{
						new FSFileOutputStream(new FSFile("testing.txt"), (in)->{
							PrintWriter pw = new PrintWriter(in, false);
							pw.println("Testing 1");
							pw.print("Testing 2");
							pw.close();
							
						});
						
						Window.setTimeout(()->{
							new FSFileInputStream(new FSFile("librandom/socket/reading.txt"), (in2)->{
								BufferedReader br = new BufferedReader(new InputStreamReader(in2));
								String line;
								try {
									while ((line = br.readLine()) != null) {
										log.info("> " + line);
									}
									in2.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							});
						}, 2000);
					});
				});
				evt.preventDefault();
				evt.stopPropagation();
				
				
				be.setDisabled(true);
				be.setInnerText("If failed reload the page");
				
			}
		
		});
		
		if (!FileSystem.isFeatureAvailable()) {
			log.warn("FILE SYSTEM ACCESS API is not available. Falling back to indexed db");
			
		}
		be.setInnerText("Click to set root directory");
		doc.getBody().appendChild(be);
		
		HTMLButtonElement elem = (HTMLButtonElement) doc.createElement("button");
		elem.setInnerText("Reset IDB");
		elem.addEventListener("click", (evt)->{
			FileSystemIDB.reset();
			evt.preventDefault();
			evt.stopPropagation();
		});
		doc.getBody().appendChild(elem);
	}

}
