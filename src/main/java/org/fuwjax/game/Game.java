package org.fuwjax.game;

public interface Game {
	GameStatus apply(Gesture gesture);
	
	Grid grid(int tick);
	
	String name();
}
