package org.fuwjax.test;

import java.nio.file.Paths;

import org.fuwjax.console.ConsoleTestRunner;
import org.fuwjax.minesweeper.Minesweeper;
import org.junit.Test;

public class BoardWithRandomMinesTest {
	@Test
	public void testForTheWin() throws Exception {
		new ConsoleTestRunner(){
			@Override
			public void daemon() throws Exception {
				Minesweeper.main("10", "10", "10", "134");
			}
		}.test(Paths.get("src/test/resources/win.log"));
	}
	
	@Test
	public void testEpicFail() throws Exception {
		new ConsoleTestRunner(){
			@Override
			public void daemon() throws Exception {
				Minesweeper.main("10", "10", "10", "666");
			}
		}.test(Paths.get("src/test/resources/fail.log"));
	}
}
