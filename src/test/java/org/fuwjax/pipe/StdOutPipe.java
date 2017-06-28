package org.fuwjax.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.fuwjax.pipe.HijackPrintStream.Target;

public class StdOutPipe implements PipeTerminal {
	private PrintStream original;
	private AtomicBoolean closed = new AtomicBoolean(true);
	private Thread reader;
	private Target target;

	public StdOutPipe(HijackPrintStream.Target target) {
		this.target = target;
		original = target.original();
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

	private void readLoop(Consumer<String> handler) {
		InputOutputStream pipe = new InputOutputStream();
		try (BufferedReader input = new BufferedReader(new InputStreamReader(pipe.input()));) {
			target.hijack(new PrintStream(pipe.output()));
			while (!closed.get()) {
				String line = input.readLine();
				if (line == null) {
					closed.set(true);
				} else {
					handler.accept(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			target.close();
			close();
		}
	}

	@Override
	public void readLines(Consumer<String> handler) {
		if (closed.compareAndSet(true, false)) {
			reader = new Thread(() -> readLoop(handler));
			reader.setDaemon(true);
			reader.start();
		}
	}
}
