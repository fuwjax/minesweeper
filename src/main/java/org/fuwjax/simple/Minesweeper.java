package org.fuwjax.simple;

import java.util.Random;

import org.fuwjax.game.Game;
import org.fuwjax.game.GameState;
import org.fuwjax.game.GameStatus;
import org.fuwjax.game.Gesture;
import org.fuwjax.game.Grid;
import org.fuwjax.game.Tile;

public class Minesweeper implements Game {
	private boolean[][] mined;
	private boolean[][] revealed;
	private boolean[][] flagged;
	private int rows = 10;
	private int columns = 10;
	private int mines = 10;

	@Override
	public String name() {
		return "simplems";
	}

	@Override
	public GameStatus apply(Gesture gesture) {
		int row = gesture.get("row", 0);
		int column = gesture.get("column", 0);
		assert row >= 0 && row < rows + 1;
		assert column >= 0 && column < columns + 1;
		switch (gesture.action()) {
		case QUIT:
			return GameState.QUIT;
		case NEW_GAME:
			newGame(gesture);
			return GameState.PLAY;
		case RIGHT_CLICK:
			assert state() == GameState.PLAY;
			if (!revealed[row][column]) {
				flagged[row][column] = !flagged[row][column];
			}
			return GameState.PLAY;
		case LEFT_CLICK:
			assert state() == GameState.PLAY;
			reveal(row, column);
			return state();
		case HELP:
		default:
			return state().status("Expected \"<row> <col>\" or \"f <row> <col>\"");
		}
	}

	private void newGame(Gesture gesture) {
		this.rows = gesture.get("rows", rows);
		this.columns = gesture.get("columns", columns);
		this.mines = gesture.get("mines", mines);
		Random rnd = new Random(gesture.get("seed", System.nanoTime()));

		assert mines <= rows * columns;
		assert rows > 0;
		assert columns > 0;
		assert mines >= 0;

		mined = new boolean[rows + 2][columns + 2];
		revealed = new boolean[rows + 2][columns + 2];
		flagged = new boolean[rows + 2][columns + 2];

		for (int r = 0; r < rows + 2; r++) {
			revealed[r][0] = true;
			revealed[r][columns + 1] = true;
		}
		for (int c = 0; c < columns + 2; c++) {
			revealed[0][c] = true;
			revealed[rows + 1][c] = true;
		}

		int m = 0;
		while (m < mines) {
			int r = 1 + rnd.nextInt(rows);
			int c = 1 + rnd.nextInt(columns);
			if (!mined[r][c]) {
				m++;
				mined[r][c] = true;
			}
		}
	}

	private GameState state() {
		GameState state = GameState.WIN;
		for (int row = 1; row <= rows; row++) {
			for (int column = 1; column <= columns; column++) {
				if (mined[row][column] && revealed[row][column]) {
					return GameState.LOSE;
				}
				if (!mined[row][column] && !revealed[row][column]) {
					state = GameState.PLAY;
				}
			}
		}
		return state;
	}

	private int countFlags() {
		int flags = 0;
		for (int row = 1; row <= rows; row++) {
			for (int column = 1; column <= columns; column++) {
				flags += flagged[row][column] ? 1 : 0;
			}
		}
		return flags;
	}

	private void reveal(int row, int column) {
		if (!revealed[row][column] && !flagged[row][column]) {
			revealed[row][column] = true;
			if (adjacentMines(row, column) == 0) {
				for (int r = -1; r <= +1; r++) {
					for (int c = -1; c <= +1; c++) {
						reveal(row + r, column + c);
					}
				}
			}
		}
	}

	private int adjacentMines(int row, int column) {
		int count = 0;
		for (int r = -1; r <= +1; r++) {
			for (int c = -1; c <= +1; c++) {
				count += mined[row + r][column + c] ? 1 : 0;
			}
		}
		return count;
	}

	@Override
	public Grid grid(int tick) {
		return new Grid() {
			@Override
			public Tile[] header() {
				return new Tile[] { intTile(countFlags()), intTile(mines), state(), intTile(tick) };
			}

			@Override
			public Tile[][] tiles() {
				Tile[][] cells = new Tile[rows][columns];
				for (int row = 0; row < rows; row++) {
					for (int column = 0; column < columns; column++) {
						cells[row][column] = tile(row + 1, column + 1);
					}
				}
				return cells;
			}
		};
	}

	private static Tile intTile(Integer value) {
		return value::toString;
	}

	protected Tile tile(int row, int column) {
		if (flagged[row][column]) {
			return Tiles.FLAG;
		}
		if (!revealed[row][column]) {
			return Tiles.PLAIN;
		}
		if (mined[row][column]) {
			return Tiles.MINE;
		}
		return Tiles.adjacent(adjacentMines(row, column));
	}
}
