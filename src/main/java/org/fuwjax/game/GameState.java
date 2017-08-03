package org.fuwjax.game;

public enum GameState implements Tile, GameStatus {
	WIN, LOSE, PLAY, QUIT;
	
	@Override
	public String message() {
		return null;
	}
	
	@Override
	public GameState state() {
		return this;
	}
	
	public GameStatus status(String message) {
		return new GameStatus() {
			@Override
			public GameState state() {
				return GameState.this;
			}

			@Override
			public String message() {
				return message;
			}
		};
	}
}
