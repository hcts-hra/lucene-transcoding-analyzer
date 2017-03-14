package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
import org.apache.lucene.search.ScoreDoc;
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
	String textFieldName = "p";
	String idFieldName = "id";

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
			document1.add(new TextField(textFieldName, "एवं मया बहुषु दुर्मतिनिर्मितेषु", Store.YES));
			document1.add(new TextField(idFieldName, "adeva", Store.YES));

			Document document2 = new Document();
			document2.add(new TextField(textFieldName, "प्रत्युद्धृतेषु खलु दूषणकण्टकेषु ।", Store.YES));
			document2.add(new TextField(idFieldName, "bdeva", Store.YES));

			Document document3 = new Document();
			document3.add(new TextField(textFieldName, "आचार्यनीतिपथ एव विशोधितोऽय-", Store.YES));
			document3.add(new TextField(idFieldName, "cdeva", Store.YES));

			Document document4 = new Document();
			document4.add(new TextField(textFieldName, "मुत्सार्य मत्सरमनेन जनः प्रयातु ॥", Store.YES));
			document4.add(new TextField(idFieldName, "ddeva", Store.YES));

			Document document5 = new Document();
			document5.add(new TextField(textFieldName, "evaṃ mayā bahuṣu durmatinirmiteṣu", Store.YES));
			document5.add(new TextField(idFieldName, "aiast", Store.YES));

			Document document6 = new Document();
			document6.add(new TextField(textFieldName, "pratyuddhṛteṣu khalu dūṣaṇakaṇṭakeṣu ।", Store.YES));
			document6.add(new TextField(idFieldName, "biast", Store.YES));

			Document document7 = new Document();
			document7.add(new TextField(textFieldName, "ācāryanītipatha eva viśodhito'ya-", Store.YES));
			document7.add(new TextField(idFieldName, "ciast", Store.YES));

			Document document8 = new Document();
			document8.add(new TextField(textFieldName, "mutsārya matsaramanena janaḥ prayātu ॥", Store.YES));
			document8.add(new TextField(idFieldName, "diast", Store.YES));

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
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("khalu");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test2() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("eva");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("cdeva", "ciast"), resultIds);
	}

	@Test
	public void test3() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("mutsārya");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("ddeva", "diast"), resultIds);
	}

	@Test
	public void test4() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("खलु");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test5() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("e?a*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("adeva", "cdeva", "aiast", "ciast"), resultIds);
	}

	@Test
	public void test6() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("pratyu*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test7() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("kha*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test8() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("ख*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test9() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("आचार्यनी*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("cdeva", "ciast"), resultIds);
	}

	@Test
	public void test10() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);
		queryParser.setAllowLeadingWildcard(true);

		Query query = queryParser.parse("ācāryanī*");
		System.out.println(query.toString());

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("cdeva", "ciast"), resultIds);
	}

	@Test
	public void test11() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("evaṃ AND bahuṣu");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("adeva", "aiast"), resultIds);
	}

	@Test
	public void test12() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("eva AND viśodhito'ya");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("cdeva", "ciast"), resultIds);
	}

	@Test
	public void test13() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("mutsārya OR (janaḥ AND prayātu)");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("ddeva", "diast"), resultIds);
	}

	@Test
	public void test14() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("खलु AND pratyuddhṛteṣu");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test15() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("eva* AND bahu*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("adeva", "aiast"), resultIds);
	}

	@Test
	public void test16() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("e?a AND viśodhi*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("cdeva", "ciast"), resultIds);
	}

	@Test
	public void test17() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("muts?ry* OR (ja?aḥ AND pray*)");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("ddeva", "diast"), resultIds);
	}

	@Test
	public void test18() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("ख* AND pratyu*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	@Test
	public void test19() throws IOException, ParseException {
		QueryParser queryParser = new AnalyzingQueryParser(matchVersion, textFieldName,
				new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);
		queryParser.setAllowLeadingWildcard(true);

		Query query = queryParser.parse("*pratyu* OR *pratyū*");

		ArrayList<String> resultIds = executeSearch(limit, query, reader);

		assertEquals(Arrays.asList("bdeva", "biast"), resultIds);
	}

	private ArrayList<String> executeSearch(final int limit, final Query query, final IndexReader reader) throws IOException {
		ArrayList<String> resultIds = new ArrayList<String>();

		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, limit);

		for (final ScoreDoc scoreDoc : docs.scoreDocs) {
			resultIds.add(searcher.doc(scoreDoc.doc).get(idFieldName));
		}

		return resultIds;
	}
}
