package com.hyr.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PolPrintTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void changeNumFullToHalfTest() {
		// 期待値
		String expected1 = "１２３４５６７８９０";
		String expected2 = "abcde";
		String expected3 = null;

		// 準備
		String param1 = "1234567890";
		String param2 = "abcde";

		// 実行
		String result1 = PolPrint.changeNumHalfToFull(param1);
		String result2 = PolPrint.changeNumHalfToFull(param2);
		String result3 = PolPrint.changeNumHalfToFull(null);

		// 検証
	    assertEquals("文字列1が一致していません。", expected1, result1);
	    assertEquals("文字列2が一致していません。", expected2, result2);
	    assertEquals("文字列3が一致していません。", expected3, result3);
	}

}
