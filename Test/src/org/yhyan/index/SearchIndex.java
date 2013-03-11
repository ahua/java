package org.yhyan.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.tier.DistanceFieldComparatorSource;
import org.apache.lucene.spatial.tier.DistanceQueryBuilder;
import org.apache.lucene.spatial.tier.projections.CartesianTierPlotter;
import org.apache.lucene.spatial.tier.projections.IProjector;
import org.apache.lucene.spatial.tier.projections.SinusoidalProjector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.NumericUtils;

import org.yhyan.util.Util;

import com.google.gson.Gson;

public class SearchIndex {
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	private IndexWriter writer = null;
	private Directory d = null;
	
	String latField = "lat";
	String lngField = "lon";
	String tierPrefix = "_localTier";

	public void getDocuments(String host, String db, String table, String user, String passwd) {
		try {
			Class.forName(JDBC_DRIVER);
			String url = "jdbc:mysql://" + host + "/" + db + "?autoReconnect=true&characterEncoding=utf8";
			Connection conn = DriverManager.getConnection(url, user, passwd);

			Statement stmt = conn.createStatement();

			String sql = "select * from " + table + " limit 10;";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String[] loc = rs.getString("longitude_latitude").split(",");
				double lat = Double.valueOf(loc[0]);
				double lon = Double.valueOf(loc[1]);
				String name = rs.getString("name");
				
				System.out.println("results = Searcher.getInstance().search(\"Hotel\"," + lat + ", " + lon + ", 8.0);");
				//System.out.println("addLocation(writer, \"" + name + "\"" + "," + lat + ", " + lon + ");");

				try {
					addLocation(writer, name, lat, lon);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addLocation(IndexWriter writer, String name, double lat, double lng) throws IOException {

		Document doc = new Document();
		doc.add(new Field("name", name, Field.Store.YES, Field.Index.ANALYZED));

		doc.add(new Field(latField, NumericUtils.doubleToPrefixCoded(lat), // #A
		        Field.Store.YES, Field.Index.NOT_ANALYZED)); // #A
		doc.add(new Field(lngField, NumericUtils.doubleToPrefixCoded(lng), // #A
		        Field.Store.YES, Field.Index.NOT_ANALYZED)); // #A


		IProjector projector = new SinusoidalProjector(); // #B

		int startTier = 1; // #C
		int endTier = 1; // #C

		for (; startTier <= endTier; startTier++) {
			CartesianTierPlotter ctp;
			ctp = new CartesianTierPlotter(startTier, // #D
			        projector, tierPrefix); // #D

			double boxId = ctp.getTierBoxId(lat, lng); // #D
//			System.out.println("Adding field " + ctp.getTierFieldName() + ":" + boxId);
			doc.add(new Field(ctp.getTierFieldName(), NumericUtils // #E
			        .doubleToPrefixCoded(boxId), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

		}

		writer.addDocument(doc);
//		System.out.println("===== Added Doc to index ====");
	}

	public void findNear(String what, double latitude, double longitude, double radius) throws CorruptIndexException, IOException {
		IndexSearcher searcher = new IndexSearcher(d);

		DistanceQueryBuilder dq;
		dq = new DistanceQueryBuilder(latitude, // #A
		        longitude, // #A
		        radius, // #A
		        latField, // #A
		        lngField, // #A
		        tierPrefix, // #A
		        true,
		        1,
		        1); // #A

		Query tq;
		if (what == null)
			tq = new TermQuery(new Term("metafile", "doc")); // #B
		else
			tq = new TermQuery(new Term("name", what));

		DistanceFieldComparatorSource dsort; // #C
		dsort = new DistanceFieldComparatorSource( // #C
		        dq.getDistanceFilter()); // #C
		Sort sort = new Sort(new SortField("foo", dsort)); // #C

//		TopDocs hits = searcher.search(tq, dq.getFilter(), 10, sort);
		TopDocs hits = searcher.search(tq, dq.getFilter(), 10);

		Map<Integer, Double> distances = // #D
		dq.getDistanceFilter().getDistances(); // #D

//		Iterator it = distances.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry<Integer, Double> pairs = (Entry<Integer, Double>) it.next();
//			System.out.println(pairs.getKey() + " = " + pairs.getValue());
//		}

//		System.out.println("Number of results: " + hits.totalHits);
//		System.out.println("Found:");
//		for (ScoreDoc sd : hits.scoreDocs) {
//			int docID = sd.doc;
//			Document d = searcher.doc(docID);
//
//			String name = d.get("name");
//			double rsLat = NumericUtils.prefixCodedToDouble(d.get(latField));
//			double rsLng = NumericUtils.prefixCodedToDouble(d.get(lngField));
//			Double geo_distance = distances.get(docID);
//
//			System.out.printf(name + ": %.2f Miles\n", geo_distance);
//			System.out.println("\t\t(" + rsLat + "," + rsLng + ")");
//		}
		if(hits.totalHits <= 0){
			System.out.println(longitude + ";" + latitude);
		}
	}
	
	public SearchIndex(String indexDir) throws IOException {
		d = FSDirectory.open(new File(indexDir));
		Analyzer a = new StandardAnalyzer(Version.LUCENE_35);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, a);
		this.writer = new IndexWriter(d, iwc);
	}

	public void addListDocument(List<Document> list) throws CorruptIndexException, IOException {
		for (int i = 0; i < list.size(); ++i) {
			this.writer.addDocument(list.get(i));
		}
	}

	public void close() throws CorruptIndexException, IOException {
		this.writer.close();
	}

	public void printAllDoc() throws CorruptIndexException, IOException {
		IndexReader r = IndexReader.open(d);
		int num = r.numDocs();
		System.out.println("Total Document: " + num);
		for (int i = 0; i < num; i++) {
			if (!r.isDeleted(i)) {
				Document d = r.document(i);
				List<Fieldable> l = d.getFields();
				for (int i1 = 0; i1 < l.size(); ++i1) {
					String fieldName = l.get(i1).name();
					String fieldValue = d.get(fieldName);
					//if (!(fieldName.equals("lat") || fieldName.equals("lon")))
					if (fieldName.startsWith("_dolphin"))
						System.out.print(fieldName + ":" + fieldValue + ";");
					//System.out.print(fieldName);
				}
				System.out.println();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexDir = "/tmp/index1";
		try {
			SearchIndex si = new SearchIndex(indexDir);
			si.getDocuments("localhost", "dolphin_search", "tripadvisor", "root", "123456");
			si.close();
		//	si.printAllDoc();
			si.findNear("Hotel", 39.943901, 116.399673, 8.0);
			si.findNear("Hotel", 40.74207, -73.98491, 8.0);
			si.findNear("Hotel", 19.76001, -70.5116, 8.0);
			si.findNear("Hotel", 45.43495, 12.342245, 8.0);
			si.findNear("Hotel", 26.435791, 127.78788, 8.0);
			si.findNear("Hotel", 22.30043, 114.17955, 8.0);
			si.findNear("Hotel", 41.391, 2.16668, 8.0);
			si.findNear("Hotel", 48.135593, 11.567228, 8.0);
			si.findNear("Hotel", 40.41972, -3.707067, 8.0);
			si.findNear("Hotel", 25.783945, -80.26212, 8.0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
