package com.hwq.indexLucene;

import org.junit.Test;

public class TestLucene {

	@Test
	public void testIndex(){
		IndexUtil iu = new IndexUtil();
		iu.index();
	}
	
	@Test
	public void testQuery(){
		IndexUtil iu = new IndexUtil();
		iu.query();
	}
	
	@Test
	public void testDelete(){
		IndexUtil iu = new IndexUtil();
		iu.delete();
	}
	
	@Test
	public void unDelete(){
		IndexUtil iu = new IndexUtil();
		iu.undelete();
	}
	
	@Test
	public void testMerae(){
		IndexUtil iu = new IndexUtil();
		iu.merae();
	}
	
	
	@Test
	public void forceDelete(){
		IndexUtil iu = new IndexUtil();
		iu.fourceDelete();
	}
	
	@Test
	public void testUpdate(){
		IndexUtil iu = new IndexUtil();
		iu.update();
	}
	
	@Test
	public void testSearch(){
		IndexUtil iu = new IndexUtil();
		iu.search();
	}
}
