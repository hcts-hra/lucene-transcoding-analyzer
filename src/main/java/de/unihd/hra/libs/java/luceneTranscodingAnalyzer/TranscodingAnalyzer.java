package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.sanskritlibrary.ResourceInputStream;
import org.sanskritlibrary.webservice.TransformMap;

public class TranscodingAnalyzer extends Analyzer {

	private final static Logger logger = Logger.getLogger(TranscodingAnalyzer.class);

	private Version matchVersion = Version.LUCENE_44;

	static {
		ResourceInputStream ris = new TransformMaps.Transcoders();

		try {
			TransformMaps.transformMaps.put("deva2slp1", new TransformMap("*:deva->slp1", ris, TransformMaps.fsmh));
			TransformMaps.transformMaps.put("roman2slp1", new TransformMap("*:roman->slp1", ris, TransformMaps.fsmh));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TranscodingAnalyzer(Version matchVersion) {
		super();
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
		logger.debug("fieldName = " + fieldName);

		StandardTokenizer tokenizer = new StandardTokenizer(matchVersion, reader);

		TokenStream tokenStream = new TranscodingFilter(matchVersion, tokenizer, TransformMaps.transformMaps);

		return new TokenStreamComponents(tokenizer, tokenStream);
	}
}
