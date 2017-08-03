package org.fuwjax.test;

import org.fuwjax.game.TextGameEngine;
import org.fuwjax.oss.console.ConsoleLog;
import org.fuwjax.oss.console.TestConsole;
import org.junit.Rule;
import org.junit.Test;

public class BoardWithRandomMinesTest {
	@Rule
	public TestConsole console = new TestConsole();
	
	@Test
	@ConsoleLog("win.log")
	public void testForTheWin() throws Exception {
		TextGameEngine.main("minesweeper");
	}
	
	@Test
	@ConsoleLog("fail.log")
	public void testEpicFail() throws Exception {
		TextGameEngine.main("minesweeper");
	}
	
	@Test
	@ConsoleLog("simple.log")
	public void testWhyNot() throws Exception {
		TextGameEngine.main("simplems");
	}
}
