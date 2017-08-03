package org.fuwjax.minesweeper;

public enum Content {
	MINE {
		@Override
		public Content adjacent() {
			return MINE;
		}

		@Override
		public int reveal(Cell cell) throws LostGameException {
			throw new LostGameException();
		}
	},
	EMPTY {
		@Override
		public int reveal(Cell cell) throws LostGameException {
			return 1 + cell.uncoverAdjacent();
		}
	},
	ADJ1, ADJ2, ADJ3, ADJ4, ADJ5, ADJ6, ADJ7, ADJ8;

	public Content adjacent() {
		assert this != MINE && this != ADJ8;
		return values()[ordinal() + 1];
	}

	public int reveal(Cell cell) throws LostGameException {
		return 1;
	}
}