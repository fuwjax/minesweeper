package org.fuwjax.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.fuwjax.game.TextGameEngine;
import org.fuwjax.minesweeper.Config;
import org.fuwjax.minesweeper.MineStrategy;
import org.fuwjax.minesweeper.Minesweeper;
import org.junit.Before;
import org.junit.Test;

public class SpecificBoardTest {
	private Minesweeper game;
	private TextGameEngine engine;
	
	@Before
	public void setup(){
		game = new Minesweeper(new Config(3, 3, MineStrategy.fixed().at(0, 0).at(2, 2)));
		engine = new TextGameEngine(game);
	}

	@Test
	public void testTimer() throws InterruptedException {
		Thread.sleep(750);
		game.leftClick(0, 2);
		Thread.sleep(750);
		assertThat(engine.render(), is("0/2  ?  1 \n - 1 0 \n - 2 1 \n - - -"));
		game.leftClick(1, 0);
		Thread.sleep(600);
		assertThat(engine.render(), is("0/2  ?  2 \n - 1 0 \n 1 2 1 \n - - -"));
	}

	@Test
	public void testFlagMine() {
		game.rightClick(0, 0);
		assertThat(engine.render(), is("1/2  ?  0 \n P - - \n - - - \n - - -"));
	}

	@Test
	public void testUncoverBlank() {
		game.leftClick(0, 2);
		assertThat(engine.render(), is("0/2  ?  0 \n - 1 0 \n - 2 1 \n - - -"));
	}

	@Test
	public void testUncoverMine() {
		game.leftClick(2, 2);
		assertThat(engine.render(), is("0/2  X  0 \n - - - \n - - - \n - - *"));
	}

	@Test
	public void testUncoverNumber() {
		game.leftClick(1, 2);
		assertThat(engine.render(), is("0/2  ?  0 \n - - - \n - - 1 \n - - -"));
	}
}
