package org.fuwjax.game;

public enum Action {
	LEFT_CLICK("(?:l )?(?<row>\\d+) (?<column>\\d+)"),
	RIGHT_CLICK("r (?<row>\\d+) (?<column>\\d+)"),
	HELP("h"),
	QUIT("q"),
	NEW_GAME("n"),
	TICK("x");
	
	final String regex;

	private Action(String regex) {
		this.regex = regex;
	}
}
