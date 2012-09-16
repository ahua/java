package org.yhyan.redis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.yhyan.index.*;
import org.yhyan.search.*;
import org.yhyan.util.Util;


public class DemoTest {
	public static String datafile = "data/hotels.json";
	
	public static void jedisTest(){
		Jedis jedis = new Jedis("localhost");
		jedis.connect();
		
		if(jedis.exists("k1")){
			System.out.println("Exist key hello.");	
			jedis.hincrBy("k1", "f1", 1);
		}else{
			jedis.hincrBy("k1", "f1", 1);
			jedis.expire("k1", 30);			
			System.out.println("Not Exist key hello.");
		}
		

		
//		jedis.set("foo", "bar");
//
//		String v = jedis.get("foo");
//		System.out.println("Value:" + v);
//
//		jedis.hincrBy("hset", "bar", 1l);
//		System.out.println(jedis.hget("hset", "bar"));
//		
//		Map<String, String> map = jedis.hgetAll("hset");
//		
//		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
//		while(it.hasNext()){
//			Map.Entry<String, String> m = (Map.Entry<String, String>)it.next();
//			
//			String key = m.getKey();
//			String value = m.getValue();
//			
//			System.out.println("Key :" + key + " Value :" + value);
//		}		
	}
	public static int indexTest(String indexDir) throws Exception{
		Indexer indexer = new Indexer(indexDir, OpenMode.CREATE);
		
		List<Document> list = indexer.getDocuments(new BufferedReader(new FileReader(datafile)));
		int numIndexed = indexer.indexDocuments(list);
		indexer.close();
		if(Util.debug){
	        System.out.println("Number Indexed: " + numIndexed);
		}
		return numIndexed;
		
	}
	public static List<Document> searchTest(String indexDir, String q) 
			throws Exception, ParseException{
		List<Document> list = Searcher.search(indexDir, q);
		if(Util.debug){
			System.out.println("Start search: indexDir=" + indexDir + ", q=" + q);
			Util.printDocuments(list);
		}
		return list;
	}
	public static void main(String[] args) {
		try {
//			String indexDir = "data/index3";
//			indexTest(indexDir);
//	        searchTest(indexDir, "four");
//	        searchTest(indexDir, "four seas");
//	        searchTest(indexDir, "four reasons");
//	        searchTest(indexDir, "four resort");
//	        searchTest(indexDir, "four maui");
//	        searchTest(indexDir, "four seasons maui");
//	        searchTest(indexDir, "seasons new");
	        jedisTest();
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
}
