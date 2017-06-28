package org.fuwjax.pipe;

import java.util.function.Consumer;

public interface PipeTerminal extends AutoCloseable {
	public void writeLine(String line);

	public void readLines(Consumer<String> handler);

	@Override
	public void close();

	public void finishRead();

	public void prepareWrite();
}
