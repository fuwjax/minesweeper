package org.fuwjax.test;

import java.nio.file.Paths;

import org.fuwjax.console.ConsoleTestRunner;
import org.fuwjax.game.TextGameEngine;
import org.junit.Test;

public class BoardWithRandomMinesTest {
	@Test
	public void testForTheWin() throws Exception {
		new ConsoleTestRunner(){
			@Override
			public void daemon() throws Exception {
				TextGameEngine.main("minesweeper");
			}
		}.test(Paths.get("src/test/resources/win.log"));
	}
	
	@Test
	public void testEpicFail() throws Exception {
		new ConsoleTestRunner(){
			@Override
			public void daemon() throws Exception {
				TextGameEngine.main("minesweeper");
			}
		}.test(Paths.get("src/test/resources/fail.log"));
	}
}
