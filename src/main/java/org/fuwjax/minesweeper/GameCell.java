package org.fuwjax.minesweeper;

import org.fuwjax.game.Tile;

public interface GameCell {
	static GameCell NULL = new GameCell(){};
	
	default int uncover(){
		return 0;
	}
	
	default int flag(){
		return 0;
	}
	
	default Tile tile(){
		return Tile.NULL;
	}
}
