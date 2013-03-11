//package org.yhyan.geosearch;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.search.BooleanClause;
//import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.CachingWrapperFilter;
//import org.apache.lucene.search.ConstantScoreRangeQuery;
//import org.apache.lucene.search.Hits;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.QueryWrapperFilter;
//import org.apache.lucene.search.Sort;
//import org.apache.lucene.search.SortField;
//import org.apache.lucene.search.TermQuery;
//import org.apache.lucene.search.TopFieldDocs;
//import org.apache.lucene.search.BooleanClause.Occur;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//
///**
// * Searcher to return documents that whose latitude and longitude falls within
// * the specified distance (in miles).
// */
//public class GeoSearcher {
//
//	private static final long serialVersionUID = -6301888193164748995L;
//
//	private static final String LATITUDE_FIELD_NAME = "normlat";
//	private static final String LONGITUDE_FIELD_NAME = "normlon";
//	private static final String FILTER_FIELD_NAME = "category";
//	private static final int MAX_RESULTS = 10;
//
//	private static final double KILOMETERS_PER_DEGREE = 111.3171;
//
//	private final Log log = LogFactory.getLog(getClass());
//
//	private IndexSearcher geoIndexSearcher;
//
//	public GeoSearcher(String indexDir) throws IOException {
//		Directory d = FSDirectory.open(new File(indexDir));
//		IndexReader ir = IndexReader.open(d);
//		this.geoIndexSearcher = new IndexSearcher(ir);
//	}
//
//	public List<GeoResult> naiveSearch(final GeoPoint origin, int distanceKms, String categoryFilter) throws IOException {
//		List<GeoResult> results = new ArrayList<GeoResult>();
//		Query query = buildQuery(origin, distanceKms);
//		// category is filtered using cached query filters. Since categories are
//		// going to be a finite set of values in a given application, it makes 
//		// sense to have them as query filters, since they are cached.
//		CachingWrapperFilter queryFilter = null;
//		if (StringUtils.isNotEmpty(categoryFilter)) {
//			queryFilter = new CachingWrapperFilter(new QueryWrapperFilter(new TermQuery(new Term(FILTER_FIELD_NAME, categoryFilter))));
//		}
//		Hits hits = geoIndexSearcher.search(query, queryFilter);
//		int numHits = hits.length();
//		for (int i = 0; i < numHits; i++) {
//			Document doc = hits.doc(i);
//			GeoPoint point = new GeoPoint(Double.valueOf(doc.get("lon")), Double.valueOf(doc.get("lat")));
//			double distanceKmFromOrigin = point.distanceFrom(origin) / KILOMETERS_PER_DEGREE;
//			if (distanceKmFromOrigin > distanceKms) {
//				// enforce that all results within a circular area
//				continue;
//			}
//			results.add(buildGeoResultFromDocument(doc, point, distanceKmFromOrigin));
//		}
//		// sort by distance, closest result to origin first
//		Collections.sort(results, new Comparator<GeoResult>() {
//			public int compare(GeoResult result1, GeoResult result2) {
//				double distance1 = result1.getDistanceKmFromOrigin();
//				double distance2 = result2.getDistanceKmFromOrigin();
//				if (distance1 == distance2) {
//					return 0;
//				} else if (distance1 < distance2) {
//					return -1;
//				} else {
//					return 1;
//				}
//			}
//		});
//		return results;
//	}
//
//	public List<GeoResult> recommendedSearch(final GeoPoint origin, int distanceKms, String categoryFilter) throws IOException {
//		List<GeoResult> results = new ArrayList<GeoResult>();
//		Sort sort = new Sort(new SortField(LATITUDE_FIELD_NAME, new GeoSortComparatorSource(origin)));
//		Query query = buildQuery(origin, distanceKms);
//		TopFieldDocs topFieldDocs = geoIndexSearcher.search(query, null, MAX_RESULTS, sort);
//		// we just ask for the top MAX_RESULTS, so limit it
//		int totalHits = Math.min(topFieldDocs.totalHits, MAX_RESULTS);
//		for (int i = 0; i < totalHits; i++) {
//			Document doc = geoIndexSearcher.doc(topFieldDocs.scoreDocs[i].doc);
//			GeoPoint point = new GeoPoint(Double.valueOf(doc.get("lon")), Double.valueOf(doc.get("lat")));
//			double distanceKmFromOrigin = point.distanceFrom(origin) / KILOMETERS_PER_DEGREE;
//			if (distanceKmFromOrigin > distanceKms) {
//				// enforce that all results within a circular area
//				continue;
//			}
//			results.add(buildGeoResultFromDocument(doc, point, distanceKmFromOrigin));
//		}
//		return results;
//	}
//
//	/**
//	 * Method to close the searcher from client code.
//	 * 
//	 * @exception IOException
//	 *                if one is thrown.
//	 */
//	public void close() throws IOException {
//		geoIndexSearcher.close();
//	}
//
//	/**
//	 * Build a Range Query from the origin and the distance in kilometers to search
//	 * within. The RangeQuery will return all documents that are in a square area
//	 * around the origin.
//	 * 
//	 * @param origin
//	 *            the GeoPoint object corresponding to the origin.
//	 * @param distanceKms
//	 *            the distance in kilometers on each side of the origin to search.
//	 * @return a BooleanQuery containing two RangeQueries.
//	 * @throws IOException
//	 *             if one is thrown.
//	 */
//	private Query buildQuery(GeoPoint origin, int distanceKms) throws IOException {
//		double spreadOnLongitude = distanceKms / calculateKilometersPerLongitudeDegree(origin.getLatitude());
//		double spreadOnLatitude = distanceKms / KILOMETERS_PER_DEGREE;
//		GeoPoint topLeft = new GeoPoint(origin.getLongitude() - spreadOnLongitude, origin.getLatitude() - spreadOnLatitude);
//		GeoPoint bottomRight = new GeoPoint(origin.getLongitude() + spreadOnLongitude, origin.getLatitude() + spreadOnLatitude);
//		BooleanQuery query = new BooleanQuery();
//		ConstantScoreRangeQuery latitudeQuery = new ConstantScoreRangeQuery(LATITUDE_FIELD_NAME, topLeft.getNormalizedLatitude(),
//		        bottomRight.getNormalizedLatitude(), true, true);
//		query.add(new BooleanClause(latitudeQuery, Occur.MUST));
//		ConstantScoreRangeQuery longitudeQuery = new ConstantScoreRangeQuery(LONGITUDE_FIELD_NAME, topLeft.getNormalizedLongitude(),
//		        bottomRight.getNormalizedLongitude(), true, true);
//		query.add(new BooleanClause(longitudeQuery, Occur.MUST));
//		log.debug("query:" + query.toString());
//		return query;
//	}
//
//	/**
//	 * The kilometers per longitude degree will decrease as we move up from
//	 * the equator to the poles, but for simplicity (and until I figure out
//	 * the calculation for this, we just return the same value as the
//	 * predefined KILOMETERS_PER_DEGREE (which is the kilometers per degree
//	 * of latitude).
//	 * 
//	 * @param latitude
//	 *            the original latitude.
//	 * @return the kilometers per degree between longitudes at that latitude.
//	 */
//	private double calculateKilometersPerLongitudeDegree(double latitude) {
//		return KILOMETERS_PER_DEGREE;
//	}
//
//	/**
//	 * Convenience method to build a GeoResult object from a Lucene document.
//	 * 
//	 * @param doc
//	 *            the Lucene document object.
//	 * @param point
//	 *            the GeoPoint object for this result.
//	 * @param distanceKmFromOrigin
//	 *            the calculated distance from the origin.
//	 * @return a populated GeoResult object.
//	 */
//	private GeoResult buildGeoResultFromDocument(Document doc, GeoPoint point, Double distanceKmFromOrigin) {
//		GeoResult result = new GeoResult();
//		result.setName(doc.get("name"));
//		result.setAddress(doc.get("address"));
//		result.setPhone(doc.get("phone"));
//		result.setCategory(doc.get("occupation"));
//		result.setDistanceKmFromOrigin(distanceKmFromOrigin);
//		result.setLatitude(point.getLatitude());
//		result.setLongitude(point.getLongitude());
//		return result;
//	}
//}