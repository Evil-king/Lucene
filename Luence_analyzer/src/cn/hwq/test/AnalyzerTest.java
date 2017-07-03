package cn.hwq.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;

import cn.hwq.util.AnalyzerUtils;

public class AnalyzerTest {
	
	@Test
	public void test01(){
		Analyzer a1 = new StandardAnalyzer(Version.LUCENE_35);
		Analyzer a2 = new StopAnalyzer(Version.LUCENE_35);
		Analyzer a3 = new SimpleAnalyzer(Version.LUCENE_35);
		Analyzer a4 = new WhitespaceAnalyzer(Version.LUCENE_35);
		String txt = "this is a good boy,so he like fanbinbin";
		AnalyzerUtils.dispalyToken(txt, a1);
		AnalyzerUtils.dispalyToken(txt, a2);
		AnalyzerUtils.dispalyToken(txt, a3);
		AnalyzerUtils.dispalyToken(txt, a4);
	}
	
	@Test
	public void test02(){
		Analyzer a1 = new StandardAnalyzer(Version.LUCENE_35);
		Analyzer a2 = new StopAnalyzer(Version.LUCENE_35);
		Analyzer a3 = new SimpleAnalyzer(Version.LUCENE_35);
		Analyzer a4 = new WhitespaceAnalyzer(Version.LUCENE_35);
		String txt = "我来自世界的南端";
		AnalyzerUtils.dispalyToken(txt, a1);
		AnalyzerUtils.dispalyToken(txt, a2);
		AnalyzerUtils.dispalyToken(txt, a3);
		AnalyzerUtils.dispalyToken(txt, a4);
	}
	
	@Test
	public void test03(){
		Analyzer a1 = new StandardAnalyzer(Version.LUCENE_35);
		Analyzer a2 = new StopAnalyzer(Version.LUCENE_35);
		Analyzer a3 = new SimpleAnalyzer(Version.LUCENE_35);
		Analyzer a4 = new WhitespaceAnalyzer(Version.LUCENE_35);
		String txt = "how are you thank you";
		
		AnalyzerUtils.displayAllTokenInfo(txt, a1);
		System.out.println("-----------------------");
		AnalyzerUtils.displayAllTokenInfo(txt, a2);
		System.out.println("-----------------------");
		AnalyzerUtils.displayAllTokenInfo(txt, a3);
		System.out.println("-----------------------");
		AnalyzerUtils.displayAllTokenInfo(txt, a4);
	}
}
