package org.fuwjax.pipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class Pipe implements PipeSource, PipeSink {
	public static final PipeSource STDIN = new StdInSource();
	public static final PipeSink STDOUT = new OutputStreamSink(HijackPrintStream.originalStdOut());
	public static final PipeSink STDERR = new OutputStreamSink(HijackPrintStream.originalStdErr());
	public static final PipeSink CAPIN = new StdInRedirectSink();
	public static final PipeSource CAPOUT = new StdOutCaptureSource(HijackPrintStream.Target.stdout);
	public static final PipeSource CAPERR = new StdOutCaptureSource(HijackPrintStream.Target.stderr);

	private Function<String, String> transform = Function.identity();
	private List<PipeSink> sinks = Collections.emptyList();
	private List<PipeSource> sources = Collections.emptyList();
	private AtomicBoolean closing = new AtomicBoolean();

	public static Pipe pipe() {
		return new Pipe();
	}

	public Pipe to(Path sink) {
		try{
			return teeTo(new OutputStreamSink(Files.newOutputStream(sink)));
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	public Pipe teeTo(PipeSink... targets) {
		assert this.sinks.isEmpty();
		this.sinks = Arrays.asList(targets);
		sinks.forEach(s -> s.setSource(this));
		checkTermination();
		return this;
	}

	public Pipe to(PipeSink sink) {
		return teeTo(sink);
	}

	@Override
	public void setSink(Pipe sink){
		try{
			sinks.add(sink);
		}catch(UnsupportedOperationException e){
			assert sinks.isEmpty();
			sinks = new ArrayList<>();
			sinks.add(sink);
		}
	}

	public Pipe transformLine(Function<String, String> f) {
		transform = transform.andThen(f);
		return this;
	}
	
	@Override
	public void writeLine(String line) {
		String l = transform.apply(line);
		for(PipeSink sink: sinks){
			sink.writeLine(l);
		}
	}
	
	@Override
	public void setSource(Pipe source){
		try{
			sources.add(source);
		}catch(UnsupportedOperationException e){
			assert sources.isEmpty();
			sources = new ArrayList<>();
			sources.add(source);
		}
	}

	public Pipe from(PipeSource source) {
		return mergeFrom(source);
	}

	public Pipe mergeFrom(PipeSource... pubs) {
		assert this.sources.isEmpty();
		this.sources = Arrays.asList(pubs);
		sources.forEach(s -> s.setSink(this));
		checkTermination();
		return this;
	}

	public Pipe from(Path source) {
		return mergeFrom(new PathSource(source));
	}
	
	private void checkTermination() {
		if(!sources.isEmpty() && !sinks.isEmpty()){
			readLines(this::writeLine);
		}
	}
	
	@Override
	public void readLines(Consumer<String> handler) {
		for(PipeSource source: sources){
			source.readLines(handler);
		}
	}
	
	@Override
	public void close() {
		if(closing.compareAndSet(false, true)){
			for(PipeSource source: sources){
				source.close();
			}
			for(PipeSink sink: sinks){
				sink.close();
			}
		}
	}
}
