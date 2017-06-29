package org.fuwjax.pipe;

import java.util.function.Consumer;

public interface PipeSource extends AutoCloseable {
	public void readLines(Consumer<String> handler);

	@Override
	public void close();
	// closeSource(); pipe.close();
	
	public void setSink(Pipe pipe);
}
