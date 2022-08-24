package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import org.apache.logging.log4j.LogManager;

public class UnicodeBlocksDetection {
	private final static LogManager LogManager = LogManager.getLogger(UnicodeBlocksDetection.class);

	public static Boolean detectSlp1Blocks(String input) {
		return input.matches("[ \\p{InBasic_Latin}]*");
	}

	public static Boolean detectDevanagariBlocks(String input) {
		return input.matches("[\\p{InDevanagari}\\p{InGeneral_Punctuation}\\*]*");
	}

	public static Boolean detectDevanagariTransliterationBlocks(String input) {
		return input.matches(
				"[\\p{InBasic_Latin}\\p{InLatin_Extended_A}\\p{InLatin_Extended_Additional}\\p{InGeneral_Punctuation}\\p{InLatin_1_Supplement}]*");
	}

}
