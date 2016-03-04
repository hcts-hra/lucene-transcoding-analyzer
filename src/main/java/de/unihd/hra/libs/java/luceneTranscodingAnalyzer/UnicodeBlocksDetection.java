package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import org.apache.log4j.Logger;

public class UnicodeBlocksDetection {
	private final static Logger logger = Logger.getLogger(UnicodeBlocksDetection.class);

	public static Boolean detectSlp1Blocks(String input) {
		logger.debug("input = " + input);
		return input.matches("[ \\p{InBasic_Latin}]*");
	}

	public static Boolean detectDevanagariBlocks(String input) {
		logger.debug("input = " + input);
		return input.matches("[\\p{InDevanagari}\\p{InGeneral_Punctuation}\\*]*");
	}

	public static Boolean detectDevanagariTransliterationBlocks(String input) {
		logger.debug("input = " + input);
		return input.matches(
				"[\\p{InBasic_Latin}\\p{InLatin_Extended_A}\\p{InLatin_Extended_Additional}\\p{InGeneral_Punctuation}\\p{InLatin_1_Supplement}]*");
	}

}
