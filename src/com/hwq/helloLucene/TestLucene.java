package com.hwq.helloLucene;

import org.junit.Test;

public class TestLucene {
	
	@Test
	public void testLucene(){
		HelloLucene hi = new HelloLucene();
				hi.index();
	}
	
	@Test
	public void testSearch(){
		HelloLucene hi = new HelloLucene();
		hi.search();
	}
}
