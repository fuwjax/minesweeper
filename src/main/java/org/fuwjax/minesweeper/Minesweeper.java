package org.fuwjax.minesweeper;

import static org.fuwjax.game.Tile.intTile;

import org.fuwjax.game.Game;
import org.fuwjax.game.GameState;
import org.fuwjax.game.Gesture;
import org.fuwjax.game.Grid;
import org.fuwjax.game.Tile;

public class Minesweeper implements Game, Grid {
	private Cell[][] cells;
	private GameState state;
	private int covered;
	private int flags;
	private Config config = new Config();
	private String status;
	private int score;
	
	@Override
	public String name() {
		return "minesweeper";
	}
	
	@Override
	public Grid apply(Gesture gesture) {
		status = null;
		switch(gesture.action()) {
			case LEFT_CLICK:
				try{
					int revealed = cell(gesture.get("row", -1), gesture.get("column", -1)).uncover();
					covered -= revealed;
					state = covered > 0 ? GameState.PLAY : GameState.WIN;
				}catch(LostGameException e){
					state = GameState.LOSE;
				}
				break;
			case RIGHT_CLICK:
				flags += cell(gesture.get("row", -1), gesture.get("column", -1)).flag();
				break;
			case NEW_GAME:
				config.init(gesture);
				state = GameState.PLAY;
				cells = config.createBoard();
				covered = config.columns()*config.rows() - config.mines();
				flags = 0;
				score = 0;
				break;
			case QUIT:
				state = GameState.QUIT;
				break;
			case TICK:
				score++;
				break;
			default:
				status = "Expected \"<row> <col>\" or \"f <row> <col>\"";
		}
		return this;
	}
	
	@Override
	public String status() {
		return status ;
	}
	
	@Override
	public Tile[] header() {
		return new Tile[] {intTile(flags), intTile(config.mines()), state, intTile(score)};
	}

	private GameCell cell(int row, int column){
		if(row < 0 || row >= cells.length || column < 0 || column >= cells[0].length){
			return GameCell.NULL;
		}
		return cells[row][column];
	}

	@Override
	public Tile[][] tiles() {
		return cells;
	}

	@Override
	public GameState state() {
		return state;
	}
}
