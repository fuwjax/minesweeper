package org.fuwjax.pipe;

import java.io.OutputStream;
import java.io.PrintStream;

public class OutputStreamSink implements PipeSink {
	private PrintStream writer;
	private Pipe pipe;
	private boolean closeableWriter;

	public OutputStreamSink(OutputStream target) {
		writer = new PrintStream(target);
		closeableWriter = true;
	}

	public OutputStreamSink(PrintStream target) {
		writer = new PrintStream(target);
		closeableWriter = false;
	}

	@Override
	public void close() {
		try{
			pipe.close();
		}finally{
			if(closeableWriter){
				writer.close();
			}
		}
	}
	
	@Override
	public void writeLine(String line) {
		writer.println(line);
	}

	@Override
	public void setSource(Pipe pipe) {
		this.pipe = pipe;
	}
}
