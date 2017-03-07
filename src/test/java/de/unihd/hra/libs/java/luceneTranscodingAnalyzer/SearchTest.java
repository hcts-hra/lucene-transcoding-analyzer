package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class SearchTest {

	private Version matchVersion = Version.LUCENE_44;
	private TranscodingAnalyzer analyzer = new TranscodingAnalyzer(matchVersion);
	private Directory index;
	private IndexReader reader;

	int limit = 10;
	String fieldName = "p";

	@Before
	public void initialize() {
		index = new RAMDirectory();

		// IndexWriterConfig config = new IndexWriterConfig(new
		// TransliterationAnalyzer())
		// .setOpenMode(OpenMode.CREATE);
		IndexWriterConfig config = new IndexWriterConfig(matchVersion, analyzer).setOpenMode(OpenMode.CREATE);

		IndexWriter writer = null;
		try {
			writer = new IndexWriter(index, config);

			Document document1 = new Document();
			document1.add(new TextField(fieldName, "एवं मया बहुषु दुर्मतिनिर्मितेषु", Store.YES));
			System.out.println("document1 = " + document1);

			Document document2 = new Document();
			document2.add(new TextField(fieldName, "प्रत्युद्धृतेषु खलु दूषणकण्टकेषु ।", Store.YES));
			System.out.println("document2 = " + document2);

			Document document3 = new Document();
			document3.add(new TextField(fieldName, "आचार्यनीतिपथ एव विशोधितोऽय-", Store.YES));
			System.out.println("document2 = " + document3);

			Document document4 = new Document();
			document4.add(new TextField(fieldName, "मुत्सार्य मत्सरमनेन जनः प्रयातु ॥", Store.YES));
			System.out.println("document2 = " + document4);

			Document document5 = new Document();
			document5.add(new TextField(fieldName, "evaṃ mayā bahuṣu durmatinirmiteṣu", Store.YES));
			System.out.println("document2 = " + document5);

			Document document6 = new Document();
			document6.add(new TextField(fieldName, "pratyuddhṛteṣu khalu dūṣaṇakaṇṭakeṣu ।", Store.YES));
			System.out.println("document2 = " + document6);

			Document document7 = new Document();
			document7.add(new TextField(fieldName, "ācāryanītipatha eva viśodhito'ya-", Store.YES));
			System.out.println("document2 = " + document7);

			Document document8 = new Document();
			document8.add(new TextField(fieldName, "mutsārya matsaramanena janaḥ prayātu ॥", Store.YES));
			System.out.println("document2 = " + document8);

			writer.addDocument(document1);
			writer.addDocument(document2);
			writer.addDocument(document3);
			writer.addDocument(document4);
			writer.addDocument(document5);
			writer.addDocument(document6);
			writer.addDocument(document7);
			writer.addDocument(document8);
			writer.commit();
			writer.close();

			reader = DirectoryReader.open(index);

			index.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test1() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("khalu");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test2() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("eva");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test3() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("mutsārya");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test4() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("खलु");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test5() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("e?a*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 4);
	}

	@Test
	public void test6() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("pratyu*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test7() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("kha*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test8() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("ख*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test9() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("आचार्यनी*");
		
		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test10() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);
		queryParser.setAllowLeadingWildcard(true);
		
		Query query = queryParser.parse("ācāryanī*");
		System.out.println(query.toString());

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test11() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("evaṃ AND bahuṣu");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test12() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("eva AND viśodhito'ya");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test13() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("mutsārya OR (janaḥ AND prayātu)");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test14() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("खलु AND pratyuddhṛteṣu");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test15() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("eva* AND bahu*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test16() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("e?a AND viśodhi*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test17() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("muts?ry* OR (ja?aḥ AND pray*)");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void test18() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("ख* AND pratyu*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}
	
	@Test
	public void test19() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);
		queryParser.setAllowLeadingWildcard(true);

		Query query = queryParser.parse("*pratyu* OR *pratyū*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}	

	private int executeSearch(final int limit, final Query query, final IndexReader reader) throws IOException {
		int totalHits;
		
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, limit);
		totalHits = docs.totalHits;
		
		System.out.println("Type of query: " + query.getClass().getSimpleName());
		System.out.println(totalHits + " found for query: " + query);

		// for (final ScoreDoc scoreDoc : docs.scoreDocs) {
		// System.out.println(searcher.doc(scoreDoc.doc));
		// }

		return totalHits;
	}
}
