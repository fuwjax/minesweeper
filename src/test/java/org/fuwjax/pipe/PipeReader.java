package org.fuwjax.pipe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PipeReader implements PipeSink{
	private final Lock lock = new ReentrantLock();
	private final Condition toggle = lock.newCondition();
	private volatile boolean isSet;
	private volatile String line;
	private volatile boolean closed;
	private Pipe pipe;
	
	@Override
	public void setSource(Pipe pipe) {
		assert this.pipe == null;
		assert pipe != null;
		this.pipe = pipe;
	}

	public String readLine() {
		lock.lock();
		try{
			while(!isSet && !closed){
				toggle.await(100, TimeUnit.MILLISECONDS);
			}
			isSet = false;
			toggle.signalAll();
			return line;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}finally{
			line = null;
			lock.unlock();
		}
	}
	
	@Override
	public void close() {
		try{
			pipe.close();
		}finally{
			lock.lock();
			try{
				while(isSet && !closed){
					toggle.await(100, TimeUnit.MILLISECONDS);
				}
				closed = true;
				toggle.signalAll();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}finally{
				lock.unlock();
			}
		}
	}

	@Override
	public void writeLine(String output) {
		lock.lock();
		try{
			while(isSet && !closed){
				toggle.await(100, TimeUnit.MILLISECONDS);
			}
			isSet = true;
			this.line = output;
			toggle.signalAll();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}finally{
			lock.unlock();
		}
	}
}
