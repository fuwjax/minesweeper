package org.fuwjax.minesweeper;

public class Config {
	private int rows;
	private int columns;
	private MineStrategy minelayer;
	
	public Config(int rows, int columns, MineStrategy minelayer){
		assert minelayer.mines() <= rows * columns;
		assert rows > 0;
		assert columns > 0;
		assert minelayer.mines() >= 0;
		this.rows = rows;
		this.columns = columns;
		this.minelayer = minelayer;
	}
	
	public int rows(){
		return rows;
	}

	public int columns(){
		return columns;
	}
	
	public int mines(){
		return minelayer.mines();
	}

	public Cell[][] createBoard(){
		Cell[][] cells = new Cell[rows][columns];
		for (int row = 0; row < rows; row++) {
			for(int col = 0; col < columns; col++){
				cells[row][col] = new Cell();
			}
		}
		for (int row = 0; row < rows; row++) {
			for(int col = 1; col < columns; col++){
				cells[row][col].addAdjacent(cells[row][col-1]); //left
				cells[row][col-1].addAdjacent(cells[row][col]); //right
			}
		}
		for (int row = 1; row < rows; row++) {
			for(int col = 0; col < columns; col++){
				cells[row][col].addAdjacent(cells[row-1][col]); //up
				cells[row-1][col].addAdjacent(cells[row][col]); //down
			}
		}
		for (int row = 1; row < rows; row++) {
			for(int col = 1; col < columns; col++){
				cells[row][col].addAdjacent(cells[row-1][col-1]); //up left
				cells[row][col-1].addAdjacent(cells[row-1][col]); //up right
				cells[row-1][col-1].addAdjacent(cells[row][col]); //down right
				cells[row-1][col].addAdjacent(cells[row][col-1]); //down left
			}
		}
		return minelayer.layMines(cells);
	}
}

