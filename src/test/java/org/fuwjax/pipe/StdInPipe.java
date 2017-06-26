package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class StdInPipe implements PipeTerminal {
	private InputStream original;
	private AtomicBoolean closed = new AtomicBoolean(true);
	private PrintStream writer;
	private Thread reader;

	public StdInPipe(InputStream original) {
		this.original = original;
	}
	
	@Override
	public void prepareWrite() {
		InputOutputStream pipe = new InputOutputStream();
		writer = new PrintStream(pipe.output());
		System.setIn(pipe.input());
	}

	@Override
	public void close() {
		closed.set(true);
		System.setIn(original);
		writer = null;
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
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(original))){
			while(!closed.get()){
				String line = reader.readLine();
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
