package org.fuwjax.game;

public interface Game {
	Grid apply(Gesture gesture);
	
	String name();
}
