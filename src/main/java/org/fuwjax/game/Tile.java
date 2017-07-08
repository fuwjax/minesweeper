package org.fuwjax.game;

public interface Tile {
	String name();

	static Tile intTile(Integer value) {
		return value::toString;
	}
}
