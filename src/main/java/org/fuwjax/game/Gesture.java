package org.fuwjax.game;

import java.util.regex.Matcher;

public class Gesture {
	private Action action;
	private Matcher matcher;

	public Gesture(Action action, Matcher matcher) {
		this.action = action;
		this.matcher = matcher;
	}
	
	public Gesture(Action action) {
		this(action, null);
	}

	public Action action() {
		return action;
	}
	
	public String get(String name, String defaultValue) {
		return matcher == null || matcher.group(name) == null ? defaultValue : matcher.group(name);
	}
	
	public int get(String name, int defaultValue) {
		try {
			return Integer.parseInt(matcher.group(name));
		}catch(Exception e) {
			return defaultValue;
		}
	}
	
	public long get(String name, long defaultValue) {
		try {
			return Long.parseLong(matcher.group(name));
		}catch(Exception e) {
			return defaultValue;
		}
	}
}
