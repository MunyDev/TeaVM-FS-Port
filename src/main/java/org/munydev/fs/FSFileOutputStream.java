package org.munydev.fs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.munydev.fs.access.CompletionCallback;
import org.munydev.fs.access.FileSystem;

public class FSFileOutputStream extends OutputStream {
	private FSFileNativeStream internal;
	public FSFileOutputStream(FSFile f, Consumer<FSFileOutputStream> c) {
		super();
//		this.internal = internal;
		FSFileSystem.getInstance().getInternalStream(f, false, (fs)->{
			this.internal = fs;
			c.accept(this);
		});
	}
	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		write(b, ()->{});
	}
	public void write(int b, CompletionCallback cc) throws IOException {
		// TODO Auto-generated method stub
//		Client.log.info("writing");
		internal.write(b, cc);
	}

	@Override
	public void close() {
		close(()->{});
	}
	public void close(CompletionCallback cc) {
		internal.close(cc);
	}
	
}
