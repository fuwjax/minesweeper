package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class StdOutPipe implements PipeTerminal {
	private PrintStream original;
	private Consumer<PrintStream> setter;
	private AtomicBoolean closed = new AtomicBoolean(true);
	private Thread reader;

	public StdOutPipe(PrintStream original, Consumer<PrintStream> setter) {
		this.original = original;
		this.setter = setter;
	}

	@Override
	public void close() {
		closed.set(true);
	}

	@Override
	public void writeLine(String line) {
		original.println(line);
	}
	
	@Override
	public void prepareWrite() {
		// do nothing
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
		try{
			InputOutputStream pipe = new InputOutputStream();
			setter.accept(new PrintStream(pipe.output()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(pipe.input()));
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
			setter.accept(original);
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
