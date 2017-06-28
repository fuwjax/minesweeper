package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class StdInPipe implements PipeTerminal {
	private AtomicBoolean closed = new AtomicBoolean(true);
	private PrintStream writer;
	private Thread reader;
	private InputStream original;

	@Override
	public void prepareWrite() {
		try{
			HijackInputStream.closeHijack();
			InputOutputStream pipe = new InputOutputStream();
			writer = new PrintStream(pipe.output());
			original = HijackInputStream.hijackStdIn(pipe.input());
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try{
			closed.set(true);
			writer.close();
			writer = null;
			HijackInputStream.closeHijack();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void writeLine(String line) {
		writer.println(line);
	}
	
	@Override
	public void finishRead() {
		try {
			reader.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private void readLoop(Consumer<String> handler){
		try(BufferedReader input = new BufferedReader(new InputStreamReader(original))){
			while(!closed.get()){
				String line = input.readLine();
				if(line == null){
					closed.set(true);
				}else{
					handler.accept(line);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			close();
		}
	}

	@Override
	public void readLines(Consumer<String> handler){
		if(closed.compareAndSet(true, false)){
			reader = new Thread(() -> readLoop(handler));
			reader.setDaemon(true);
			reader.start();
		}
	}
}
