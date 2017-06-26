package org.fuwjax.console;

import static org.fuwjax.pipe.Pipe.STDERR;
import static org.fuwjax.pipe.Pipe.STDIN;
import static org.fuwjax.pipe.Pipe.STDOUT;
import static org.fuwjax.pipe.Pipe.pipe;
import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import org.fuwjax.pipe.Pipe;
import org.fuwjax.pipe.PipeReader;

public abstract class ConsoleTestRunner{
	private String prefix = "> ";

	public abstract void daemon() throws Exception;

	public void test(Path test) throws Exception {
		final AtomicInteger errors = new AtomicInteger();
		try (Pipe log = pipe().to(Paths.get("target/console.log"));
				Pipe console = pipe().teeTo(pipe().transformLine(l -> prefix+l).to(log), STDIN);
				PipeReader monitor = new PipeReader();
				Pipe stdout = pipe().teeTo(log, monitor).mergeFrom(STDOUT, STDERR);
				Pipe feed = pipe().from(test)){
			feed.readLines(line -> {
				if(line.startsWith(prefix)){
					console.writeLine(line.substring(2));
				}else if(line.startsWith("/ ")){
					if(!monitor.readLine().matches(line.substring(2))){
						errors.incrementAndGet();
					}
				}else{
					if(!line.equals(monitor.readLine())){
						errors.incrementAndGet();
					}
				}
			});
			daemon();
			feed.finishRead();
		}
		assertEquals("errors", 0, errors.get());
	}

	public void record(Path test) throws Exception {
		try (Pipe log = pipe().to(test);
				Pipe stdin = pipe().teeTo(pipe().transformLine(l -> prefix+l).to(log), STDIN).from(STDIN);
				Pipe stdout = pipe().teeTo(log, STDOUT).from(STDOUT);
				Pipe stderr = pipe().teeTo(log, STDERR).from(STDERR)){
			daemon();
		}
	}
}
