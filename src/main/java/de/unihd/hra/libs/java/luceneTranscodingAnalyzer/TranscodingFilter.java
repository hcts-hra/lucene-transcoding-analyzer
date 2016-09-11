package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.sanskritlibrary.webservice.TransformMap;
import org.sanskritlibrary.webservice.WebServices;

public class TranscodingFilter extends TokenFilter {

	private final Version matchVersion;
	public final HashMap<String, TransformMap> transformMaps;
	protected CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);
	private final static Logger logger = Logger.getLogger(TranscodingFilter.class);

	// this is for Lucene 5.3.1.
	// protected TransliterationFilter(TokenStream input) {
	// super(input);
	// }

	// this is for Lucene 4.4.0
	public TranscodingFilter(Version matchVersion, TokenStream in, HashMap<String, TransformMap> transformsMaps) {
		super(in);
		this.matchVersion = matchVersion;
		this.transformMaps = transformsMaps;
	}

	@Override
	final public boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String currentToken = this.input.getAttribute(CharTermAttribute.class).toString().trim();
			logger.debug("currentToken = " + currentToken);

			String transcodedCurrentToken = currentToken;

			try {
				if (UnicodeBlocksDetection.detectDevanagariBlocks(currentToken)) {
					transcodedCurrentToken = WebServices.transformString(currentToken, transformMaps.get("deva2slp1"));
					logger.debug("transcodedCurrentToken = " + transcodedCurrentToken);
				} else {
					transcodedCurrentToken = WebServices.transformString(currentToken, transformMaps.get("roman2slp1"));
					logger.debug("transcodedCurrentToken = " + transcodedCurrentToken);
				}

				// if (UnicodeBlocksDetection.detectSlp1Blocks(currentToken)) {
				// } else if
				// (UnicodeBlocksDetection.detectDevanagariTransliterationBlocks(currentToken))
				// {
				// transcodedCurrentToken =
				// WebServices.transformString(currentToken,
				// transformMaps.get("roman2slp1"));
				// logger.debug("transcodedCurrentToken = " +
				// transcodedCurrentToken);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.charTermAttribute.setEmpty().append(transcodedCurrentToken);

			return true;
		} else
			return false;
	}
}
