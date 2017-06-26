package org.fuwjax.minesweeper;

import org.fuwjax.game.Game;
import org.fuwjax.game.GameOver;
import org.fuwjax.game.TextGameEngine;
import org.fuwjax.game.Tile;

public class Minesweeper implements Game {
	private Cell[][] cells;
	private GameOver state = GameOver.PLAY;
	private int covered;
	private int flags;
	private long start;
	private Config config;

	public Minesweeper(Config config) {
		this.config = config;
		cells = config.createBoard();
		covered = config.columns()*config.rows() - config.mines();
		start = System.currentTimeMillis();
	}
	
	private static int arg(String[] args, int index, int defaultValue){
		try{
			return Integer.parseInt(args[index]);
		}catch(Exception e){
			return defaultValue;
		}
	}

	public static void main(String... args) throws Exception {
		int rows = arg(args, 0, 10);
		int cols = arg(args, 1, 10);
		int mines = arg(args, 2, Math.max(rows, cols));
		int seed = arg(args, 3, (int)System.nanoTime());
		final TextGameEngine engine = new TextGameEngine(new Minesweeper(new Config(rows, cols, MineStrategy.random(mines, seed))));
		engine.start();
	}
	
	@Override
	public int rows() {
		return config.rows();
	}

	@Override
	public String status() {
		return String.format("%d/%d  %s  %d", flags, config.mines(), UiTile.valueOf(state.name()).rep(), score());
	}

	private int score() {
		return (int)(System.currentTimeMillis() - start)/1000;
	}
	
	@Override
	public GameOver leftClick(int row, int column) {
		try{
			int revealed = cell(row, column).uncover();
			covered -= revealed;
			state = covered > 0 ? GameOver.PLAY : GameOver.WIN;
		}catch(LostGameException e){
			state = GameOver.LOSE;
		}
		return state;
	}

	@Override
	public GameOver rightClick(int row, int column) {
		flags += cell(row, column).flag();
		return state;
	}
	
	@Override
	public Tile tile(int row, int column) {
		return cell(row, column).tile();
	}
	
	private GameCell cell(int row, int column){
		if(row < 0 || row >= rows() || column < 0 || column >= columns()){
			return GameCell.NULL;
		}
		return cells[row][column];
	}

	@Override
	public int columns() {
		return config.columns();
	}
}
