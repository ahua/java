package org.yhyan.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;
import org.yhyan.util.Util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import java.sql.ResultSet;

public class Indexer {

	private IndexWriter writer = null;
	
	public Indexer(String indexDir, IndexWriterConfig.OpenMode mode) 
			throws IOException {
		Directory d = FSDirectory.open(new File(indexDir));
		Analyzer a = new StandardAnalyzer(Version.LUCENE_35);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, a);
		iwc.setOpenMode(mode);
		writer = new IndexWriter(d, iwc); 
	}
	
	public int indexDocument(Document doc) throws Exception {
		writer.addDocument(doc);
		return writer.numDocs();
	}
	
	public int indexDocuments(List<Document> docs) throws Exception {
		for(Document doc : docs){
			this.indexDocument(doc);
		}
		return writer.numDocs();
	}
	
	// get documents from file each line is a json.
	public static List<Document> getDocuments(BufferedReader bufferedReader) throws Exception {
		List<Document> list = new ArrayList<Document>();
		String line;
		Gson gson = new Gson();
		while((line = bufferedReader.readLine()) != null) {
			Hotel hotel = gson.fromJson(line, Hotel.class);
			Document doc = new Document();
			
			doc.add(new Field("name", hotel.name, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
			doc.add(new Field("text", hotel.name, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("url", hotel.url, Field.Store.YES, Field.Index.NO));
			doc.add(new Field("id", Util.md5(hotel.url), Field.Store.YES, Field.Index.NO));
			
			list.add(doc);
		}
		return list;
	}
	
	// get documents from sql select result set.
	public static List<Document> getDocuments(ResultSet rs) throws Exception {
		List<Document> list = new ArrayList<Document>();
		while(rs.next()){
			Document doc = new Document();
			
			doc.add(new Field("name", rs.getString("keyword"), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
			doc.add(new Field("text", rs.getString("keyword"), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("category", rs.getString("category"), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("url", rs.getString("url"), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("id", String.valueOf(rs.getInt("id")), Field.Store.YES, Field.Index.NO));
			
			list.add(doc);
			
		}
		return list;
	}
	
	public void close() throws IOException {
		writer.close();
	}
}

class Hotel {
	public String url;
	public String name;
}
