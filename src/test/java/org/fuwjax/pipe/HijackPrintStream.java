package org.fuwjax.pipe;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class HijackPrintStream extends PrintStream {
	public enum Target {
		stdout {
			@Override
			public PrintStream get() {
				return System.out;
			}

			@Override
			public void set(PrintStream capture) {
				System.setOut(capture);
			}
		},
		stderr {
			@Override
			public PrintStream get() {
				return System.err;
			}

			@Override
			public void set(PrintStream capture) {
				System.setErr(capture);
			}
		};

		public abstract PrintStream get();

		public abstract void set(PrintStream capture);

		public void close() {
			while (get() instanceof HijackPrintStream) {
				get().close();
			}
		}
		
		public PrintStream original(){
			PrintStream original = get();
			while(original instanceof HijackPrintStream){
				original = ((HijackPrintStream)original).original;
			}
			return original;
		}

		public PrintStream hijack(PrintStream capture) {
			PrintStream original = get();
			set(new HijackPrintStream(this, capture, original));
			return original;
		}
	}

	private PrintStream capture;
	private PrintStream original;
	private Target target;

	public HijackPrintStream(Target target, PrintStream capture, PrintStream original) {
		super(new PipedOutputStream());
		this.target = target;
		this.capture = capture;
		this.original = original;
	}

	@Override
	public void println(String x) {
		capture.println(x);
	}

	@Override
	public void println(Object x) {
		capture.println(x);
	}

	@Override
	public void println() {
		capture.println();
	}

	@Override
	public void println(boolean x) {
		capture.println(x);
	}

	@Override
	public void println(char x) {
		capture.println(x);
	}

	@Override
	public void println(char[] x) {
		capture.println(x);
	}

	@Override
	public void println(double x) {
		capture.println(x);
	}

	@Override
	public void println(float x) {
		capture.println(x);
	}

	@Override
	public void println(int x) {
		capture.println(x);
	}

	@Override
	public void println(long x) {
		capture.println(x);
	}

	@Override
	public void close() {
		target.set(original);
		capture.close();
	}

	@Override
	public void flush() {
		capture.flush();
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		capture.write(buf, off, len);
	}

	@Override
	public void print(boolean b) {
		capture.print(b);
	}

	@Override
	public void print(char[] s) {
		capture.print(s);
	}

	@Override
	public void print(double d) {
		capture.print(d);
	}

	@Override
	public void print(char c) {
		capture.print(c);
	}

	@Override
	public void print(float f) {
		capture.print(f);
	}

	@Override
	public void print(int i) {
		capture.print(i);
	}

	@Override
	public void print(long l) {
		capture.print(l);
	}

	@Override
	public void print(Object obj) {
		capture.print(obj);
	}

	@Override
	public void print(String s) {
		capture.print(s);
	}

	@Override
	public void write(int b) {
		capture.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		capture.write(b);
	}

	@Override
	public boolean checkError() {
		return capture.checkError();
	}

	@Override
	public PrintStream append(char c) {
		return capture.append(c);
	}

	@Override
	public PrintStream append(CharSequence csq) {
		return capture.append(csq);
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		return capture.append(csq, start, end);
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		return capture.format(l, format, args);
	}

	@Override
	public PrintStream format(String format, Object... args) {
		return capture.format(format, args);
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		return capture.printf(l, format, args);
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		return capture.printf(format, args);
	}
}
