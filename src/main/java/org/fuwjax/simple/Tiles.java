package org.fuwjax.simple;

import org.fuwjax.game.Tile;

public enum Tiles implements Tile{
	PLAIN,
	FLAG,
	MINE,
	EMPTY, ADJ1, ADJ2, ADJ3, ADJ4, ADJ5, ADJ6, ADJ7, ADJ8;
	
	public static Tiles adjacent(int count) {
		return values()[EMPTY.ordinal() + count];
	}
}
