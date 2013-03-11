//package org.yhyan.geosearch;
//
//import java.io.IOException;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.ScoreDocComparator;
//import org.apache.lucene.search.SortComparatorSource;
//import org.apache.lucene.search.SortField;
//
///**
// * Custom Sorting for Distance calculations.
// */
//public class GeoSortComparatorSource implements SortComparatorSource {
//
//	private static final long serialVersionUID = -4338638868770017111L;
//	private final Log log = LogFactory.getLog(getClass());
//	private GeoPoint origin;
//
//	public GeoSortComparatorSource(GeoPoint origin) {
//		this.origin = origin;
//	}
//
//	public ScoreDocComparator newComparator(final IndexReader reader, final String fieldname) throws IOException {
//		return new ScoreDocComparator() {
//			public int compare(ScoreDoc i, ScoreDoc j) {
//				try {
//					Document doc1 = reader.document(i.doc);
//					Document doc2 = reader.document(j.doc);
//					GeoPoint point1 = new GeoPoint(Double.valueOf(doc1.get("lon")), Double.valueOf(doc1.get("lat")));
//					GeoPoint point2 = new GeoPoint(Double.valueOf(doc2.get("lon")), Double.valueOf(doc2.get("lat")));
//					if (point1.distanceFrom(origin) < point2.distanceFrom(origin)) {
//						return -1;
//					} else if (point1.distanceFrom(origin) > point2.distanceFrom(origin)) {
//						return 1;
//					} else {
//						return 0;
//					}
//				} catch (Exception e) {
//					log.error(e);
//					return 0;
//				}
//			}
//
//			public int sortType() {
//				return SortField.DOUBLE;
//			}
//
//			public Comparable sortValue(ScoreDoc i) {
//				try {
//					Document doc = reader.document(i.doc);
//					GeoPoint point = new GeoPoint(Double.valueOf(doc.get("lon")), Double.valueOf(doc.get("lat")));
//					return new Double(point.distanceFrom(origin));
//				} catch (Exception e) {
//					log.error(e);
//					return new Double(0D);
//				}
//			}
//		};
//	}
//}
