package org.munydev.fs;

import org.munydev.fs.access.CompletionCallback;
import org.teavm.extras.slf4j.TeaVMLoggerFactory;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class FSButton {
	private HTMLButtonElement hbe;
	public FSButton(HTMLButtonElement hbe) {
		super();
		this.hbe = hbe;
	}


	public static FSButton createButton(CompletionCallback fsh, boolean forceIDB, String buttonName) {
		return createButton(()->{}, fsh, forceIDB, buttonName);
	}
	public static FSButton createButton(CompletionCallback err,CompletionCallback fsh, boolean forceIDB, String buttonName) {
		TeaVMLoggerFactory f = new TeaVMLoggerFactory();
		Client.initProperties();
		Client.log =  f.getLogger("");
		
		Client.log.info("INTITIATING FILE SYSTEM");
		HTMLButtonElement hbe = (HTMLButtonElement) HTMLDocument.current().createElement("button");
		
		hbe.addEventListener("click", (ev)->{
			FSFileSystem.setForceIDB(forceIDB);
			FSFileSystem.getInstance(fsh, err);
			
//			fsh.onComplete();
			ev.preventDefault();
			ev.stopPropagation();
		});
		hbe.setInnerText(buttonName);
//		FSButton fsb = new FSButton(hbe);
		return new FSButton(hbe);
	}
	
	
	public void append() {
		HTMLDocument.current().getBody().appendChild(hbe);
	}
	public void append(HTMLElement elem) {
		elem.appendChild(hbe);
	}
	public void setId(String id) {
		this.hbe.setId(id);
		
	}
	public void destroy() {
		hbe.delete();
	}
	public void hide() {
		hbe.getStyle().setProperty("display", "none");
	}
	public void show() {
		hbe.getStyle().setProperty("display", "block");
	}
	public void setEnabled(boolean value) {
		hbe.setDisabled(!value);
	}
}
