package ro.kuberam.libs.java.luceneTranscodingAnalyzer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class UnicodeBlocksDetectionTest {

	@Test
	public void testDetectSlp1() throws IOException {
		String input = "tasmAt";

		assertTrue(UnicodeBlocksDetection.detectSlp1Blocks(input));
	}

	@Test
	public void testDetectSlp1AndTransliteration() throws IOException {
		String input = "tasmAñ";

		assertFalse(UnicodeBlocksDetection.detectSlp1Blocks(input));
	}

	@Test
	public void testDetectSlp1AndWildcard1() throws IOException {
		String input = "tasmA*";

		assertTrue(UnicodeBlocksDetection.detectSlp1Blocks(input));
	}
	
	@Test
	public void testDetectSlp1AndWildcard2() throws IOException {
		String input = "*asmAt";

		assertTrue(UnicodeBlocksDetection.detectSlp1Blocks(input));
	}	
	
	@Test
	public void testDetectSlp1AndWildcard3() throws IOException {
		String input = "ta*mAt";

		assertTrue(UnicodeBlocksDetection.detectSlp1Blocks(input));
	}	
	
	@Test
	public void testDetectSlp1AndWildcard4() throws IOException {
		String input = "*a*mA*";

		assertTrue(UnicodeBlocksDetection.detectSlp1Blocks(input));
	}	

	@Test
	public void testDetectDevanagari() throws IOException {
		String input = "ऽगन्तवो․";

		assertTrue(UnicodeBlocksDetection.detectDevanagariBlocks(input));
	}

	@Test
	public void testDetectDevanagariAndWildcard() throws IOException {
		String input = "*ऽगन्त*वो․*";

		assertTrue(UnicodeBlocksDetection.detectDevanagariBlocks(input));
	}

	@Test
	public void testDetectDevanagariAndLatin() throws IOException {
		String input = "ऽगन्तवोa․";

		assertFalse(UnicodeBlocksDetection.detectDevanagariBlocks(input));
	}

	@Test
	public void testDetectRomanized() throws IOException {
		String input = "ñna cāsaṃyukte dravye sayogajanyasya guṇasyotpattir iti jnānotpattidarśanād ātmamanaḥsannikarṣaḥ kāraṇam/";

		assertTrue(UnicodeBlocksDetection
				.detectDevanagariTransliterationBlocks(input));
	}

	@Test
	public void testDetectRomanizedAndDevanagari() throws IOException {
		String input = "rephavarṇotpādakamoṣṭhapadmaṃ ṣoḍaśasirāvṛtamन्․";

		assertFalse(UnicodeBlocksDetection
				.detectDevanagariTransliterationBlocks(input));
	}
}
