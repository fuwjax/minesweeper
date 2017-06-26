package org.fuwjax.minesweeper;

import static org.fuwjax.minesweeper.UiTile.MINED;
import static org.fuwjax.minesweeper.UiTile.ADJ0;

import org.fuwjax.game.Tile;

public enum Content {
	MINE(MINED){
		public Content adjacent(){
			return MINE;
		}
		
		@Override
		public int reveal(Cell cell) {
			throw new LostGameException();
		}
	},
	EMPTY(ADJ0){
		@Override
		public int reveal(Cell cell) {
			return 1 + cell.uncoverAdjacent();
		}
	},
	ADJ1(UiTile.ADJ1),
	ADJ2(UiTile.ADJ2),
	ADJ3(UiTile.ADJ3),
	ADJ4(UiTile.ADJ4),
	ADJ5(UiTile.ADJ5),
	ADJ6(UiTile.ADJ6),
	ADJ7(UiTile.ADJ7),
	ADJ8(UiTile.ADJ8);
	
	private Tile tile;

	private Content(Tile tile){
		this.tile = tile;
	}
	
	public Tile tile(){
		return tile;
	}
	
	public Content adjacent(){
		assert this != MINE && this != ADJ8;
		return values()[ordinal()+1];
	}
	
	public int reveal(Cell cell){
		return 1;
	}
}