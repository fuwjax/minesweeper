package org.fuwjax.minesweeper;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.fuwjax.game.Action;
import org.fuwjax.game.Game;
import org.fuwjax.game.GameState;
import org.fuwjax.game.Gesture;
import org.fuwjax.game.Grid;
import org.fuwjax.game.GameStatus;
import org.fuwjax.game.Tile;

public class Minesweeper implements Game, Grid {
	private Cell[][] cells;
	private int covered;
	private int flags;
	private GameState state;
	private Config config = new Config();
	private Map<Action, Function<Gesture, GameStatus>> response = new EnumMap<>(Action.class);
	private int score;
	{
		response.put(Action.LEFT_CLICK, this::leftClick);
		response.put(Action.RIGHT_CLICK, this::rightClick);
		response.put(Action.NEW_GAME, this::newGame);
		response.put(Action.QUIT, this::quit);
		response.put(Action.HELP, this::help);
	}
	
	private GameStatus leftClick(Gesture gesture) {
		assert state == GameState.PLAY;
		try{
			int revealed = cell(gesture.get("row", -1), gesture.get("column", -1)).uncover();
			covered -= revealed;
			return covered > 0 ? GameState.PLAY : GameState.WIN;
		}catch(LostGameException e){
			return GameState.LOSE;
		}
	}
	
	private GameStatus rightClick(Gesture gesture) {
		assert state == GameState.PLAY;
		flags += cell(gesture.get("row", -1), gesture.get("column", -1)).flag();
		return state;
	}
	
	private GameStatus newGame(Gesture gesture) {
		config.init(gesture);
		cells = config.createBoard();
		covered = config.columns()*config.rows() - config.mines();
		flags = 0;
		return GameState.PLAY;
	}
	
	private GameStatus quit(Gesture gesture) {
		return GameState.QUIT;
	}
	
	private GameStatus help(Gesture gesture) {
		return state.status("Expected \"<row> <col>\" or \"f <row> <col>\"");
	}
	
	@Override
	public String name() {
		return "minesweeper";
	}
	
	@Override
	public GameStatus apply(Gesture gesture) {
		GameStatus status = response.get(gesture.action()).apply(gesture);
		state = status.state();
		return status;
	}
	
	private static Tile intTile(Integer value) {
		return value::toString;
	}
	
	@Override
	public Grid grid(int tick) {
		this.score = tick;
		return this;
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
}
