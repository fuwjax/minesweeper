package org.fuwjax.pipe;

import java.io.IOException;
import java.io.InputStream;

public class HijackInputStream extends InputStream {
	private InputStream input;
	private InputStream stdIn;

	public static void hijackStdIn(InputStream input) {
		System.setIn(new HijackInputStream(input, original()));
	}
	
	public static InputStream original() {
		InputStream original = System.in;
		while(original instanceof HijackInputStream){
			original = ((HijackInputStream)original).stdIn;
		}
		return original;
	}
	
	public static void closeHijack() throws IOException{
		while (System.in instanceof HijackInputStream) {
			System.in.close();
		}		
	}

	private HijackInputStream(InputStream input, InputStream stdIn) {
		this.input = input;
		this.stdIn = stdIn;
	}

	@Override
	public int read() throws IOException {
		return input.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return input.read(b, off, len);
	}
	
	@Override
	public int available() throws IOException {
		return input.available();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return input.skip(n);
	}
	
	@Override
	public boolean markSupported() {
		return input.markSupported();
	}
	
	@Override
	public void mark(int readlimit) {
		input.mark(readlimit);
	}
	
	@Override
	public void reset() throws IOException {
		input.reset();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return input.read(b);
	}
	
	@Override
	public void close() throws IOException {
		System.setIn(stdIn);
		input.close();
	}
}
