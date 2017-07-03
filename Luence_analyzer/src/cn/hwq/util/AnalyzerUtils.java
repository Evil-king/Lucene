package cn.hwq.util;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.AttributeSet.CharacterAttribute;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;


public class AnalyzerUtils {
	
	public static void dispalyToken(String str,Analyzer a){
		try {
			TokenStream stream =  a.tokenStream("content", new StringReader(str));
			//创建一个属性，这个属性会添加到流中，随着这个TokenStream增加
			CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
			while(stream.incrementToken()){
				System.out.print("["+cta+"]");
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void displayAllTokenInfo(String str,Analyzer a){
		try {
			TokenStream stream =  a.tokenStream("content", new StringReader(str));
			PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
			OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
			CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
			TypeAttribute tab = stream.addAttribute(TypeAttribute.class);
			for(;stream.incrementToken();){
				System.out.print(pia.getPositionIncrement()+":");
				System.out.print(cta+"["+oa.startOffset()+"--"+oa.endOffset()+"]-->"+tab.toString()+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
