package org.fuwjax.minesweeper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public interface MineStrategy {
	public static MineStrategy random(int count){
		return random(count, System.nanoTime());
	}
	
	public static MineStrategy random(int count, long seed){
		return new RandomMineStrategy(count, seed);
	}
	
	public static FixedMineStrategy fixed(){
		return new FixedMineStrategy();
	}
	
	public Cell[][] layMines(Cell[][] cells);
	
	public int mines();
	
	public class RandomMineStrategy implements MineStrategy{
		private int mines;
		private Random rnd;
		
		public RandomMineStrategy(int mines, long seed) {
			this.mines = mines;
			rnd = new Random(seed);
		}
		
		@Override
		public Cell[][] layMines(Cell[][] cells) {
			List<Cell> list = Arrays.stream(cells).flatMap(Arrays::stream).collect(Collectors.toList());
			Collections.shuffle(list, rnd);
			list.subList(0, mines).forEach(Cell::makeMine);
			return cells;
		}
		
		@Override
		public int mines() {
			return mines;
		}
	}
	
	public class FixedMineStrategy implements MineStrategy{
		private Set<int[]> mines = new HashSet<>();
		
		public FixedMineStrategy at(int row, int column){
			mines.add(new int[]{row, column});
			return this;
		}
		
		@Override
		public int mines() {
			return mines.size();
		}
		
		@Override
		public Cell[][] layMines(Cell[][] cells) {
			mines.forEach(p -> cells[p[0]][p[1]].makeMine());
			return cells;
		}
	}
}
