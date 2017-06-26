package org.fuwjax.minesweeper;

import org.fuwjax.game.Tile;

public enum UiTile implements Tile {
	WIN("!"),
	LOSE("X"),
	PLAY("?"),
	COVERED("-"),
	FLAGGED("P"),
	MINED("*"),
	ADJ0("0"),
	ADJ1("1"),
	ADJ2("2"),
	ADJ3("3"),
	ADJ4("4"),
	ADJ5("5"),
	ADJ6("6"),
	ADJ7("7"),
	ADJ8("8");
	
	private String rep;

	private UiTile(String rep){
		this.rep = rep;
	}
	
	@Override
	public String rep() {
		return rep;
	}
}
