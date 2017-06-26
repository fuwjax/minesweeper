package org.fuwjax.pipe;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Pipe implements PipeTerminal {
	public static final PipeTerminal STDIN = new StdInPipe(System.in);
	public static final PipeTerminal STDOUT = new StdOutPipe(System.out, System::setOut);
	public static final PipeTerminal STDERR = new StdOutPipe(System.err, System::setErr);

	private Function<String, String> transform = Function.identity();
	private List<? extends PipeTerminal> sinks = Collections.emptyList();
	private List<? extends PipeTerminal> sources = Collections.emptyList();

	public static Pipe pipe() {
		return new Pipe();
	}

	public Pipe to(Path sink) {
		assert sinks.isEmpty();
		sinks = Collections.singletonList(new PathPipe(sink));
		prepareWrite();
		checkTermination();
		return this;
	}

	public Pipe teeTo(PipeTerminal... sinks) {
		assert this.sinks.isEmpty();
		this.sinks = Arrays.asList(sinks);
		prepareWrite();
		checkTermination();
		return this;
	}

	public Pipe to(PipeTerminal sink) {
		assert sinks.isEmpty();
		sinks = Collections.singletonList(sink);
		prepareWrite();
		checkTermination();
		return this;
	}

	public Pipe transformLine(Function<String, String> f) {
		transform = transform.andThen(f);
		return this;
	}

	public void writeLine(String line) {
		String l = transform.apply(line);
		for(PipeTerminal sink: sinks){
			sink.writeLine(l);
		}
	}

	public Pipe from(PipeTerminal source) {
		assert sources.isEmpty();
		sources = Collections.singletonList(source);
		checkTermination();
		return this;
	}

	public Pipe mergeFrom(PipeTerminal... sources) {
		assert this.sources.isEmpty();
		this.sources = Arrays.asList(sources);
		checkTermination();
		return this;
	}

	public Pipe from(Path source) {
		assert sources.isEmpty();
		sources = Collections.singletonList(new PathPipe(source));
		checkTermination();
		return this;
	}
	
	private void checkTermination() {
		if(!sources.isEmpty() && !sinks.isEmpty()){
			readLines(this::writeLine);
		}
	}
	
	@Override
	public void prepareWrite() {
		sinks.forEach(PipeTerminal::prepareWrite);
	}

	@Override
	public void readLines(Consumer<String> handler) {
		for(PipeTerminal source: sources){
			source.readLines(handler);
		}
	}
	
	@Override
	public void finishRead() {
		sources.forEach(s -> s.finishRead());
	}
	
	@Override
	public void close() {
		for(PipeTerminal source: sources){
			source.close();
		}
		for(PipeTerminal sink: sinks){
			sink.close();
		}
	}
}
