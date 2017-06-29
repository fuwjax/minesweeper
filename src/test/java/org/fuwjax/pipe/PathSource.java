package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class PathSource implements PipeSource{
	private Path target;
	private Thread reader;
	private Pipe pipe;

	public PathSource(Path target) {
		assert target != null;
		this.target = target;
	}
	
	@Override
	public void setSink(Pipe pipe) {
		assert this.pipe == null;
		assert pipe != null;
		this.pipe = pipe;
	}

	@Override
	public void close() {
		try{
			if(reader != null){
				reader.join();
			}
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}finally{
			pipe.close();
		}
	}
	
	private void readLoop(Consumer<String> handler){
		try(BufferedReader input = Files.newBufferedReader(target)){
			while(!Thread.currentThread().isInterrupted()){
				String line = input.readLine();
				if(line == null){
					break;
				}
				handler.accept(line);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void readLines(Consumer<String> handler){
		assert pipe != null;
		assert handler != null;
		reader = new Thread(() -> readLoop(handler));
		reader.setDaemon(true);
		reader.start();
	}
}
