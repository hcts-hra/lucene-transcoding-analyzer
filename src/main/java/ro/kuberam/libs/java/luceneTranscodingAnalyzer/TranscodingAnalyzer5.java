package ro.kuberam.libs.java.luceneTranscodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class TranscodingAnalyzer5 {

//	@Override
//	protected TokenStreamComponents createComponents(String fieldName) {
//		StandardTokenizer tokenizer = new StandardTokenizer();
//
//		TokenStream tokenStream = new StandardFilter(tokenizer);
//		tokenStream = new TransliterationFilter(tokenStream);
//
//		return new TokenStreamComponents(tokenizer, tokenStream);
//	}
}
