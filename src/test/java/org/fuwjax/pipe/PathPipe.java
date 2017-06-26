package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class PathPipe implements PipeTerminal {
	private Path target;
	private volatile boolean closed;
	private PrintStream writer;
	private Thread reader;

	public PathPipe(Path target) {
		this.target = target;
	}

	@Override
	public void close() {
		closed = true;
		if(writer != null){
			writer.close();
		}
	}
	
	@Override
	public void prepareWrite() {
		try{
			if(writer == null){
				writer = new PrintStream(Files.newOutputStream(target));
			}
		}catch(IOException e){
			// failed to open file, nothing to clean
			e.printStackTrace();
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
		try(BufferedReader reader = Files.newBufferedReader(target)){
			while(!closed){
				String line = reader.readLine();
				if(line == null){
					closed = true;
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
		if(reader == null){
			reader = new Thread(() -> readLoop(handler));
			reader.setDaemon(true);
			reader.start();
		}
	}
}
