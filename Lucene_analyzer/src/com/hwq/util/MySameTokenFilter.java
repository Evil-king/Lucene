package com.hwq.util;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class MySameTokenFilter extends TokenFilter{
	private CharTermAttribute cta = null ;
	
	protected MySameTokenFilter(TokenStream input) {
		super(input);
		cta = this.addAttribute(CharTermAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if(!input.incrementToken()) return false;
		if(cta.toString().equals("中国")){
			cta.setEmpty();
			cta.append("大陆");
		}
		return true;
	}

}
