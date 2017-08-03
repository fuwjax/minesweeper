package org.fuwjax.minesweeper;

public enum Cover {
	PLAIN {
		@Override
		int uncover(Cell cell) throws LostGameException {
			return cell.reveal();
		}

		@Override
		int toggleFlag(Cell cell) {
			cell.setCover(FLAG);
			return 1;
		}
	},
	FLAG {
		@Override
		int toggleFlag(Cell cell) {
			cell.setCover(PLAIN);
			return -1;
		}
	},
	NONE {
		@Override
		String name(Content content) {
			return content.name();
		}
	};

	int uncover(Cell cell) throws LostGameException{
		return 0;
	}

	int toggleFlag(Cell cell) {
		return 0;
	}

	String name(Content content) {
		return name();
	}
}