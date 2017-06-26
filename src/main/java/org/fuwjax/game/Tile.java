package org.fuwjax.game;

public interface Tile {
	public static Tile NULL = new Tile(){};
	
	default String rep(){
		return "";
	}
}
