package org.yhyan.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Searcher {
	public static int TOP_N = 10;
	public static String searchField = "name";
	
	public static String processQueryString(String queryString) {
		queryString = queryString.trim().replace("*", "");
		
		if(queryString.contains(" ")){
			return "\"" + queryString + "*\"" + "~3";
		}
		return queryString + "*";
	}
	
	public static List<Document> search(String indexDir, String q)
	throws IOException, ParseException{
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexReader ir = IndexReader.open(dir); 
		IndexSearcher is = new IndexSearcher(ir);
		is.setSimilarity(new SuggestionSimilarity());
		
		QueryParser parser = new QueryParser(Version.LUCENE_35, 
				searchField,
				new StandardAnalyzer(Version.LUCENE_35));
		Query query = parser.parse(q);
		
		TopDocs hits = is.search(query, null, TOP_N);
		
		List<Document> list = new ArrayList<Document>();
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			list.add(is.doc(scoreDoc.doc));
		}
		return list;
	}
}

class SuggestionSimilarity extends DefaultSimilarity {

	private static final long serialVersionUID = 2058561242197190886L;

	@Override
	public float tf(float freq) {
		return freq == 0f ? 0f : 1f;
	}
}
