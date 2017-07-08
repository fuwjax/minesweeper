package org.fuwjax.minesweeper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.fuwjax.game.Gesture;

public class Config {
	private int rows = 10;
	private int columns = 10;
	private int mines = 10;
	private Random rnd = new Random();

	public Config() {
	}

	public void init(Gesture gesture) {
		this.rows = gesture.get("rows", rows);
		this.columns = gesture.get("columns", columns);
		this.mines = gesture.get("mines", mines);
		rnd = new Random(gesture.get("seed", System.nanoTime()));
		assert mines <= rows * columns;
		assert rows > 0;
		assert columns > 0;
		assert mines >= 0;
	}

	public int rows() {
		return rows;
	}

	public int columns() {
		return columns;
	}

	public int mines() {
		return mines;
	}

	public Cell[][] createBoard() {
		Cell[][] cells = new Cell[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				cells[row][col] = new Cell();
			}
		}
		for (int row = 0; row < rows; row++) {
			for (int col = 1; col < columns; col++) {
				cells[row][col].addAdjacent(cells[row][col - 1]); // left
				cells[row][col - 1].addAdjacent(cells[row][col]); // right
			}
		}
		for (int row = 1; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				cells[row][col].addAdjacent(cells[row - 1][col]); // up
				cells[row - 1][col].addAdjacent(cells[row][col]); // down
			}
		}
		for (int row = 1; row < rows; row++) {
			for (int col = 1; col < columns; col++) {
				cells[row][col].addAdjacent(cells[row - 1][col - 1]); // up left
				cells[row][col - 1].addAdjacent(cells[row - 1][col]); // up right
				cells[row - 1][col - 1].addAdjacent(cells[row][col]); // down right
				cells[row - 1][col].addAdjacent(cells[row][col - 1]); // down left
			}
		}
		List<Cell> list = Arrays.stream(cells).flatMap(Arrays::stream).collect(Collectors.toList());
		Collections.shuffle(list, rnd);
		list.subList(0, mines).forEach(Cell::makeMine);
		return cells;
	}
}
