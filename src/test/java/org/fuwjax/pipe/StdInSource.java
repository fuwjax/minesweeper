package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class StdInSource implements PipeSource {
	private AtomicBoolean closed = new AtomicBoolean(true);
	private Thread reader;
	private Pipe pipe;
	
	@Override
	public void setSink(Pipe pipe) {
		this.pipe = pipe;
	}

	@Override
	public void close() {
		closed.set(true);
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
		try(BufferedReader input = new BufferedReader(new InputStreamReader(HijackInputStream.original()))){
			while(!Thread.currentThread().isInterrupted()){
				if(input.ready()){
					String line = input.readLine();
					handler.accept(line);
				}else if(closed.get()){
					break;
				}else{
					Thread.yield();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
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
