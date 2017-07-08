package org.fuwjax.game;

public interface Grid {
	Tile[] header();
	
	Tile[][] tiles();

	String status();
	
	GameState state();
}
