package org.yhyan.spatial;

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.spatial.tier.DistanceQueryBuilder;
import org.apache.lucene.spatial.tier.DistanceFieldComparatorSource;
import org.apache.lucene.spatial.tier.projections.CartesianTierPlotter;
import org.apache.lucene.spatial.tier.projections.IProjector;
import org.apache.lucene.spatial.tier.projections.SinusoidalProjector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

// From chapter 9
public class SpatialLuceneExample {

	String latField = "lat";
	String lngField = "lon";
	String tierPrefix = "_localTier";

	private Directory directory;
	private IndexWriter writer;

	SpatialLuceneExample() throws IOException {
//		directory = FSDirectory.open(new File("/tmp/index1"));
		directory = new RAMDirectory();
		writer = new IndexWriter(directory, new WhitespaceAnalyzer(), MaxFieldLength.UNLIMITED);
	}

	private void addDocWithoutGes(IndexWriter writer, String name) throws IOException{
		Document doc = new Document();
		doc.add(new Field("name", name + " 1", Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("metafile", "doc", Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("no_ges", "no", Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
	}
	
	private void addLocation(IndexWriter writer, String name, double lat, double lng) throws IOException {

		//addDocWithoutGes(writer, name);
		
		Document doc = new Document();
		doc.add(new Field("name", name, Field.Store.YES, Field.Index.ANALYZED));

		doc.add(new Field(latField, NumericUtils.doubleToPrefixCoded(lat), // #A
		        Field.Store.YES, Field.Index.NOT_ANALYZED)); // #A
		doc.add(new Field(lngField, NumericUtils.doubleToPrefixCoded(lng), // #A
		        Field.Store.YES, Field.Index.NOT_ANALYZED)); // #A

		doc.add(new Field("metafile", "doc", Field.Store.YES, Field.Index.ANALYZED));

		IProjector projector = new SinusoidalProjector(); // #B

		int startTier = 0; // #C
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

	/*
	 * #A Encode lat/lng as doubles
	 * #B Use sinusoidal projection
	 * #C Index around 1 to 1000 miles
	 * #D Compute bounding box ID
	 * #E Add tier field
	 */

//	public void findNoGes(String what) throws CorruptIndexException, IOException{
//		IndexSearcher is = new IndexSearcher(directory);
//		Query q1 = new TermQuery(new Term("no_ges", "no"));
//		Query q2 = new TermQuery(new Term("name", what));
//		BooleanQuery bq = new BooleanQuery();
//		bq.add(q1, BooleanClause.Occur.MUST);
//		bq.add(q2, BooleanClause.Occur.MUST);
//		TopDocs hits = is.search(bq, null, 10);
//		
//		List<Document> list = new ArrayList<Document>();
//		for(ScoreDoc scoreDoc : hits.scoreDocs) {
//			list.add(is.doc(scoreDoc.doc));
//		}
//		
//		printAllDoc(list);
//	}
	
	public void findNear(String what, double latitude, double longitude, double radius) throws CorruptIndexException, IOException {
		IndexSearcher searcher = new IndexSearcher(directory);

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

//		DistanceFieldComparatorSource dsort; // #C
//		dsort = new DistanceFieldComparatorSource( // #C
//		        dq.getDistanceFilter()); // #C
//		Sort sort = new Sort(new SortField("foo", dsort)); // #C

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

	/*
	 * #A Create distance query
	 * #B Match all documents
	 * #C Create distance sort
	 * #D Get distances map
	 */
	
	public void printAllDoc(List<Document> list){
		for(int i = 0; i < list.size(); i++){
			Document d = list.get(i);
			List<Fieldable> l = d.getFields();
			for(int i1 = 0; i1 < l.size(); ++i1){
				String k = l.get(i1).name();
				String v = d.get(k);
				System.out.println(k + ":" + v + ";");
			}
		}
	}
	
	public void printAllDoc() throws CorruptIndexException, IOException {
		IndexReader r = IndexReader.open(directory);
		int num = r.numDocs();
		System.out.println("Total Document: " + num);
		for (int i = 0; i < num; i++) {
			if (!r.isDeleted(i)) {
				Document d = r.document(i);
				List<Fieldable> l = d.getFields();
				for(int i1 = 0; i1 < l.size(); ++i1){
					String fieldName = l.get(i1).name();
					String fieldValue = d.get(fieldName);
					System.out.print(fieldName + ";");
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		SpatialLuceneExample spatial = new SpatialLuceneExample();
		spatial.addData();
		spatial.findNear("Hotel",39.943901, 116.399673, 8.0);
		spatial.findNear("Hotel",40.74207, -73.98491, 8.0);
		spatial.findNear("Hotel",19.76001, -70.5116, 8.0);
		spatial.findNear("Hotel",45.43495, 12.342245, 8.0);
		spatial.findNear("Hotel",26.435791, 127.78788, 8.0);
		spatial.findNear("Hotel",22.30043, 114.17955, 8.0);
		spatial.findNear("Hotel",41.391, 2.16668, 8.0);
		spatial.findNear("Hotel",48.135593, 11.567228, 8.0);
		spatial.findNear("Hotel",40.41972, -3.707067, 8.0);
		spatial.findNear("Hotel",25.783945, -80.26212, 8.0);
		System.out.println("Another Search Test:");
	//	spatial.findNoGes("Restaurant");
	//	spatial.printAllDoc();
	}

	private void addData() throws IOException {
//		addLocation(writer, "McCormick & Schmick's Seafood Restaurant", 38.9579000, -77.3572000);
//		addLocation(writer, "Jimmy's Old Town Tavern", 38.9690000, -77.3862000);
//		addLocation(writer, "Ned Devine's", 38.9510000, -77.4107000);
//		addLocation(writer, "Old Brogue Irish Pub", 38.9955000, -77.2884000);
//		addLocation(writer, "Alf Laylah Wa Laylah", 38.8956000, -77.4258000);
//		addLocation(writer, "Sully's Restaurant & Supper", 38.9003000, -77.4467000);
//		addLocation(writer, "TGIFriday", 38.8725000, -77.3829000);
//		addLocation(writer, "Potomac Swing Dance Club", 38.9027000, -77.2639000);
//		addLocation(writer, "White Tiger Restaurant", 38.9027000, -77.2638000);
//		addLocation(writer, "Jammin' Java", 38.9039000, -77.2622000);
//		addLocation(writer, "Potomac Swing Dance Club", 38.9027000, -77.2639000);
//		addLocation(writer, "WiseAcres Comedy Club", 38.9248000, -77.2344000);
//		addLocation(writer, "Glen Echo Spanish Ballroom", 38.9691000, -77.1400000);
//		addLocation(writer, "Whitlow's on Wilson", 38.8889000, -77.0926000);
//		addLocation(writer, "Iota Club and Cafe", 38.8890000, -77.0923000);
//		addLocation(writer, "Hilton Washington Embassy Row", 38.9103000, -77.0451000);
//		addLocation(writer, "HorseFeathers, Bar & Grill", 39.01220000000001, -77.3942);
		addLocation(writer, "The Orchid Hotel",39.943901, 116.399673);
		addLocation(writer, "Hotel Giraffe",40.74207, -73.98491);
		addLocation(writer, "Tropix Hotel",19.76001, -70.5116);
		addLocation(writer, "Hotel Le Isole",45.43495, 12.342245);
		addLocation(writer, "Renaissance Okinawa Resort",26.435791, 127.78788);
		addLocation(writer, "Hotel ICON",22.30043, 114.17955);
		addLocation(writer, "Mandarin Oriental, Barcelona",41.391, 2.16668);
		addLocation(writer, "Herzog Wilhelm Hotel",48.135593, 11.567228);
		addLocation(writer, "Hotel Preciados",40.41972, -3.707067);
		addLocation(writer, "Residence Inn by Marriott Miami Airport",25.783945, -80.26212);
		writer.close();
	}
}
