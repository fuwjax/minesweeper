package org.fuwjax.minesweeper;

import org.fuwjax.game.Tile;

public interface GameCell extends Tile {
	static GameCell NULL = () -> "NULL";
	
	default int uncover() throws LostGameException{
		return 0;
	}
	
	default int flag(){
		return 0;
	}
	
	@Override
	String name();
}
