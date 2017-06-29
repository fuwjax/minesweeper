package org.fuwjax.console;

import static org.fuwjax.pipe.Pipe.CAPERR;
import static org.fuwjax.pipe.Pipe.CAPIN;
import static org.fuwjax.pipe.Pipe.CAPOUT;
import static org.fuwjax.pipe.Pipe.STDERR;
import static org.fuwjax.pipe.Pipe.STDIN;
import static org.fuwjax.pipe.Pipe.STDOUT;
import static org.fuwjax.pipe.Pipe.pipe;
import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import org.fuwjax.pipe.Pipe;
import org.fuwjax.pipe.PipeReader;

public abstract class ConsoleTestRunner{
	private static final String inputPrefix = "> ";

	public abstract void daemon() throws Exception;
	
	public void test(Path test) throws Exception {
		test(test, false);
	}

	public void test(Path test, boolean echo) throws Exception {
		if(!Files.exists(test)){
			System.out.println("Recording test at "+test);
			record(test);
			return;
		}
		final AtomicInteger errors = new AtomicInteger();
		try (Pipe log = pipe().to(Paths.get("target/console.log"));
				Pipe console = pipe().teeTo(pipe().transformLine(l -> inputPrefix+l).to(log), CAPIN);
				PipeReader monitor = new PipeReader();
				Pipe capture = pipe().teeTo(log, monitor).mergeFrom(CAPOUT, CAPERR);
				Pipe stdout = pipe().to(STDOUT);
				Pipe stderr = pipe().to(STDERR);
				Pipe feed = pipe().from(test)){
			feed.readLines(line -> {
				if(line.startsWith(inputPrefix)){
					console.writeLine(line.substring(2));
					if(echo){
						stdout.writeLine(line);
					}
				}else{
					boolean match = true;
					String actual = monitor.readLine();
					if(line.startsWith("/ ")){
						match = actual.matches(line.substring(2));
					}else{
						match = line.equals(actual);
					}
					if(!match){
						errors.incrementAndGet();
						stderr.writeLine("X "+actual + " X "+line + " X");
					}else if(echo){
						stdout.writeLine(actual);
					}
				}
			});
			daemon();
		}
		assertEquals("errors", 0, errors.get());
	}

	public void record(Path test) throws Exception {
		try (Pipe log = pipe().to(test);
				Pipe stdin = pipe().teeTo(pipe().transformLine(l -> inputPrefix+l).to(log), CAPIN).from(STDIN);
				Pipe stdout = pipe().teeTo(log, STDOUT).from(CAPOUT);
				Pipe stderr = pipe().teeTo(log, STDERR).from(CAPERR)){
			daemon();
		}
	}
}
