package org.fuwjax.pipe;

public interface PipeSink extends AutoCloseable{
	public void writeLine(String line);

	@Override
	public void close(); 
	// pipe.close(); closeSink();
	
	public void setSource(Pipe pipe);
}
