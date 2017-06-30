package com.hwq.searchTest;

import java.io.File;
import java.io.IOException;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import com.hwq.searchLucene.FileIndexUtils;
import com.hwq.searchLucene.SearchaUtil;

public class searchTest {
	
	private SearchaUtil su;
	private FileIndexUtils f;
	
	@Before
	public void init(){
		su = new SearchaUtil();
		f = new FileIndexUtils();
	}
	
	/**
	 * 复制文件夹中所有文件
	 */
	@Test
	public void testCopyFiles(){
		try {
			File file = new File("/Users/Macx/Desktop/lucene/example");
			for(File f:file.listFiles()){
				String destFileName = FilenameUtils.getFullPath(f.getAbsolutePath())+
						FilenameUtils.getBaseName(f.getName())+".she";
				FileUtils.copyFile(f, new File(destFileName));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void searchaTrem(){
		su.searchByTerm("names", "mike", 3);
	}
	@Test
	public void searchaByTremQuery(){
		su.searchByTermRange("names", "a","s", 10);
	}
	@Test
	public void searchByNumTermRange(){
		//查询name以a开头和s结尾的
//		su.searchByTermRange("names","a","s",10);
		//由于attachs是数字类型，使用TermRange无法查询
		su.searchByTermRange("attachs","2","10", 5);
	}
	
	@Test
	public void searchByPrefix(){
		su.searchByPrefix("names", "j", 3);
	}
	@Test
	public void searchByWildcard(){
		//匹配@itat.org结尾的所有字符
		su.searchByWildcard("emails", "*@itat.org", 10);
		//匹配j开头的有三个字符的name
		su.searchByWildcard("names", "j???", 10);
	}
	
	@Test
	public void searchByBoolean(){
		su.searchByBoolean(3);
	}
	
	@Test
	public void indexFile(){
		FileIndexUtils.index(true);
	}
	
	@Test
	public void searcheIndex01(){
		f.searchPage("java", 2, 5);
		System.out.println("------------------------");
		f.searcherAfter("java", 2, 5);
//		f.searchNoPage("java");
	}
}
