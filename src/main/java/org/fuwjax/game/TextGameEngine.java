package org.fuwjax.game;

import static org.fuwjax.game.Action.NEW_GAME;
import static org.fuwjax.game.Action.TICK;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class TextGameEngine {
	private Game game;
	private Properties props;
	private Map<Action, Pattern> patterns;

	public static void main(String... args) throws Exception {
		Game game = loadGame(args[0]);
		Properties props = new Properties();
		try (InputStream in = ClassLoader.getSystemResourceAsStream(game.name() + "_text_ui.properties")) {
			props.load(in);
		}
		final TextGameEngine engine = new TextGameEngine(game, props);
		engine.start();
	}

	private static Game loadGame(String name) {
		ServiceLoader<Game> loader = ServiceLoader.load(Game.class);
		for (Game game : loader) {
			if (game.name().equals(name)) {
				return game;
			}
		}
		throw new RuntimeException("No game found for " + name);
	}

	public TextGameEngine(final Game game, Properties props) {
		this.game = game;
		this.props = props;
		patterns = new EnumMap<>(Action.class);
		for(Action action: Action.values()) {
			patterns.put(action, Pattern.compile(props.getProperty(action.name(), action.regex)));
		}
	}

	public void start() throws Exception {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			Grid grid = game.apply(new Gesture(NEW_GAME));
			while (grid.state() != GameState.QUIT) {
				System.out.println(display(grid));
				String line = reader.readLine();
				game.apply(new Gesture(TICK));
				Gesture gesture = createGesture(line);
				grid = game.apply(gesture);
			}
		}
	}

	public Gesture createGesture(String line) {
		for (Action action : Action.values()) {
			Matcher match = patterns.get(action).matcher(line);
			if (match.matches()) {
				return new Gesture(action, match);
			}
		}
		return new Gesture(Action.HELP);
	}

	public String display(Grid grid) {
		final StringBuilder builder = new StringBuilder();
		if(grid.status() != null) {
			builder.append(grid.status()).append("\n");
		}
		builder.append(header(grid.header()));
		Tile[][] tiles = grid.tiles();
		for (int row = 0; row < tiles.length; row++) {
			builder.append(" \n");
			for (int column = 0; column < tiles[row].length; column++) {
				builder.append(' ').append(value(tiles[row][column].name()));
			}
		}
		return builder.toString();
	}

	private String header(Tile[] header) {
		Object[] values = new Object[header.length];
		for (int i = 0; i < header.length; i++) {
			values[i] = value(header[i].name());
		}
		String format = props.getProperty("HEADER");
		if (format == null) {
			return Arrays.stream(values).map(String::valueOf).collect(Collectors.joining("  "));
		}
		return String.format(format, values);
	}

	private Object value(String name) {
		Object value = props.getProperty(name);
		if (value != null) {
			return value;
		}
		try {
			return Integer.parseInt(name);
		} catch (NumberFormatException e) {
			return name;
		}
	}
}
