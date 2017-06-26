package org.fuwjax.pipe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class PipeReader implements PipeTerminal{
	private final Lock lock = new ReentrantLock();
	private final Condition toggle = lock.newCondition();
	private volatile boolean isSet;
	private volatile String line;
	private volatile boolean closed;

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
			lock.unlock();
		}
	}
	
	@Override
	public void close() {
		closed = true;
	}

	@Override
	public void writeLine(String line) {
		lock.lock();
		try{
			while(isSet && !closed){
				toggle.await(100, TimeUnit.MILLISECONDS);
			}
			isSet = true;
			this.line = line;
			toggle.signalAll();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void readLines(Consumer<String> handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void finishRead() {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void prepareWrite() {
		// nothing to do
	}
}
