package org.fuwjax.minesweeper;

import org.fuwjax.game.Tile;

public enum Cover {
	PLAIN {
		@Override
		int uncover(Cell cell) {
			return cell.reveal();
		}

		@Override
		int toggleFlag(Cell cell) {
			cell.setCover(FLAG);
			return 1;
		}

		@Override
		Tile tile(Content content) {
			return UiTile.COVERED;
		}
	},
	FLAG {
		@Override
		int toggleFlag(Cell cell) {
			cell.setCover(PLAIN);
			return -1;
		}

		@Override
		Tile tile(Content content) {
			return UiTile.FLAGGED;
		}
	},
	NONE {
		@Override
		int toggleFlag(Cell cell) {
			return 0;
		}

		@Override
		Tile tile(Content content) {
			return content.tile();
		}
	};

	int uncover(Cell cell){
		return 0;
	}

	abstract int toggleFlag(Cell cell);

	abstract Tile tile(Content content);
}