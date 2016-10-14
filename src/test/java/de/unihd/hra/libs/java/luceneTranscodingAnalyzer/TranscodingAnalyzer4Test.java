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
public class TranscodingAnalyzer4Test {

	private Version matchVersion = Version.LUCENE_44;
	private TranscodingAnalyzer analyzer = new TranscodingAnalyzer(matchVersion);
	private Directory index;
	private IndexReader reader;

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
			document1.add(new TextField(fieldName, "paramā", Store.YES));
			System.out.println("document1 = " + document1);

			Document document2 = new Document();
			document2.add(new TextField(fieldName, "paramārtha", Store.YES));
			System.out.println("document2 = " + document2);
			
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
	public void trailingWildcardQuery() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("paramā*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 0);
	}
	
	@Test
	public void trailingWildcardTranscodedQuery() throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(matchVersion, fieldName, new TranscodingAnalyzer(matchVersion));
		queryParser.setLowercaseExpandedTerms(false);

		Query query = queryParser.parse("paramA*");

		int totalHits = executeSearch(limit, query, reader);

		assertEquals(totalHits, 2);
	}

	private int executeSearch(final int limit, final Query query, final IndexReader reader) throws IOException {
		int totalHits;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, limit);
		totalHits = docs.totalHits;

		System.out.println(totalHits + " found for query: " + query);

//		for (final ScoreDoc scoreDoc : docs.scoreDocs) {
//			System.out.println(searcher.doc(scoreDoc.doc));
//		}

		return totalHits;
	}
}
