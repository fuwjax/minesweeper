package org.fuwjax.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.fuwjax.game.TextGameEngine;
import org.fuwjax.minesweeper.Config;
import org.fuwjax.minesweeper.MineStrategy;
import org.fuwjax.minesweeper.Minesweeper;
import org.junit.Before;
import org.junit.Test;

public class BoardWithNoMinesTest{
	private Minesweeper game;
	private TextGameEngine engine;
	
	@Before
	public void setup(){
		game = new Minesweeper(new Config(3, 3, MineStrategy.fixed()));
		engine = new TextGameEngine(game);
	}

   @Test
   public void testBoardInit(){
      assertThat(engine.render(), is("0/0  ?  0 \n - - - \n - - - \n - - -"));
   }

   @Test
   public void testFlag(){
      game.rightClick(1, 1);
      assertThat(engine.render(), is("1/0  ?  0 \n - - - \n - P - \n - - -"));
   }

   @Test
   public void testFlagUncovered(){
      game.leftClick(1, 1);
      game.rightClick(1, 1);
      assertThat(engine.render(), is("0/0  !  0 \n 0 0 0 \n 0 0 0 \n 0 0 0"));
   }

   @Test
   public void testUncover(){
      game.leftClick(2, 2);
      assertThat(engine.render(), is("0/0  !  0 \n 0 0 0 \n 0 0 0 \n 0 0 0"));
   }

   @Test
   public void testUncoverFlag(){
      game.rightClick(1, 1);
      game.leftClick(1, 1);
      assertThat(engine.render(), is("1/0  ?  0 \n - - - \n - P - \n - - -"));
   }

   @Test
   public void testUnFlag(){
      game.rightClick(1, 1);
      game.rightClick(1, 1);
      assertThat(engine.render(), is("0/0  ?  0 \n - - - \n - - - \n - - -"));
   }
}
