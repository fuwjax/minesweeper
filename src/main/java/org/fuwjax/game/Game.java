package org.fuwjax.game;

public interface Game {
	GameOver leftClick(int row, int column);

	GameOver rightClick(int row, int column);

	int rows();

	int columns();

	Tile tile(int row, int column);

	String status();
}
