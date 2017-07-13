package com.hwq.lucene.test;

import org.junit.Before;
import org.junit.Test;

import com.hwq.lucene.util.SearchTest;

public class TestSearcher {
	private SearchTest st = null;
	
	@Before
	public void init() {
		st = new SearchTest();
	}
	
	@Test
	public void test01() {
		st.searcher("java", null);
	}
}
