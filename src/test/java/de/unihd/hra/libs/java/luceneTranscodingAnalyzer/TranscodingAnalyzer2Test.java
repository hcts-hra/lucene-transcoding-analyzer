package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
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
public class TranscodingAnalyzer2Test {

	private Version matchVersion = Version.LUCENE_44;
	private TranscodingAnalyzer analyzer = new TranscodingAnalyzer(matchVersion);
	private Directory index;
	private IndexReader reader;
	private final static Logger logger = Logger.getLogger(TranscodingFilter.class);

	int limit = 10;
	String fieldName = "title";

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
			document1.add(new TextField(fieldName, "सुखेन", Store.YES));

			Document document2 = new Document();
			document2.add(new TextField(fieldName, "sukhena", Store.YES));

			writer.addDocument(document1);
			writer.addDocument(document2);

			writer.commit();
			writer.close();

			reader = DirectoryReader.open(index);

			index.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void termQuery() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		
		Query query = queryParser.parse("sukhena");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void leadingWildcardQuery() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setAllowLeadingWildcard(true);
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("*uKena");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void intermediateWildcardQuery() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("suK*na");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	@Test
	public void trailingWildcardQuery1() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("suKen*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}
	
	@Test
	public void trailingWildcardQuery2() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("suKena*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}	

	@Test
	public void allWildcardQuery() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setAllowLeadingWildcard(true);
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("*uK*n*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	private int executeSearch(final int limit, final Query query, final IndexReader reader) throws IOException {
		int totalHits;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, limit);
		totalHits = docs.totalHits;

		logger.debug("");
		logger.debug(totalHits + " found for query: " + query);

		for (final ScoreDoc scoreDoc : docs.scoreDocs) {
			logger.debug(searcher.doc(scoreDoc.doc));
		}

		return totalHits;
	}
}
