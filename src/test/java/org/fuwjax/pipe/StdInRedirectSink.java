package org.fuwjax.pipe;

import java.io.IOException;
import java.io.PrintStream;

public class StdInRedirectSink implements PipeSink {
	private PrintStream writer;
	private Pipe pipe;
	
	@Override
	public void setSource(Pipe pipe) {
		this.pipe = pipe;
		InputOutputStream bridge = new InputOutputStream();
		writer = new PrintStream(bridge.output());
		HijackInputStream.hijackStdIn(bridge.input());
	}

	@Override
	public void close() {
		try{
			pipe.close();
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
				HijackInputStream.closeHijack();
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void writeLine(String line) {
		writer.println(line);
	}
}
