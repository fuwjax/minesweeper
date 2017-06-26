package org.fuwjax.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextGameEngine {
	private static Pattern GESTURE = Pattern.compile("(?<right>f )?(?<row>\\d+) (?<column>\\d+)");
	private Game game;

	public TextGameEngine(final Game game) {
		this.game = game;
	}

	public String render() {
		final StringBuilder builder = new StringBuilder(game.status());
		for (int row = 0; row < game.rows(); row++) {
			builder.append(" \n");
			for (int column = 0; column < game.columns(); column++) {
				builder.append(' ').append(game.tile(row, column).rep());
			}
		}
		return builder.toString();
	}

	public void start() throws Exception {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
			do {
				System.out.println(render());
			} while (acceptGesture(reader.readLine()) == GameOver.PLAY);
			System.out.println(render());
		}
	}

	private GameOver acceptGesture(String line) {
		if (line == null) {
			return GameOver.LOSE;
		}
		Matcher match = GESTURE.matcher(line);
		if (!match.matches()) {
			System.out.println("Expected \"<row> <col>\" or \"f <row> <col>\"");
			return GameOver.PLAY;
		}
		int row = Integer.parseInt(match.group("row"));
		int column = Integer.parseInt(match.group("column"));
		if (match.group("right") == null) {
			return game.leftClick(row, column);
		}
		return game.rightClick(row, column);
	}
}
