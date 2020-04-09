package edu.hanyang.submit;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.PorterStemmer;

import edu.hanyang.indexer.Tokenizer;

public class TinySETokenizer implements Tokenizer {
	static PorterStemmer stemmer;
	static SimpleAnalyzer analyzer;
	public void setup() {
		stemmer = new PorterStemmer();
		analyzer = new SimpleAnalyzer();
	}
	public List<String> split(String text) {
		List<String> stringlist = new ArrayList<String>();
		
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
			stream.reset();
			CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
			
					
			while(stream.incrementToken()) {
				stemmer.setCurrent(term.toString());
				stemmer.stem();
				stringlist.add(stemmer.getCurrent());
			}
			stream.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
		return stringlist;
	}

	public void clean() {
		analyzer.close();
	}

}