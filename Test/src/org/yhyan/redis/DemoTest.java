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
import org.yhyan.index.*;
import org.yhyan.search.*;


public class DemoTest {
	public static String datafile = "data/hotels.json";
	
	public static void jedisTest(){
		Jedis jedis = new Jedis("localhost");
		jedis.connect();
		jedis.set("foo", "bar");

		String v = jedis.get("foo");
		System.out.println("Value:" + v);

		jedis.hincrBy("hset", "bar", 1l);
		System.out.println(jedis.hget("hset", "bar"));
		
		Map<String, String> map = jedis.hgetAll("hset");
		
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> m = (Map.Entry<String, String>)it.next();
			
			String key = m.getKey();
			String value = m.getValue();
			
			System.out.println("Key :" + key + " Value :" + value);
		}		
	}
	public static int indexTest(String indexDir) throws Exception{
		Indexer indexer = new Indexer(indexDir, OpenMode.CREATE);
		
		List<Document> list = indexer.getDocuments(new BufferedReader(new FileReader(datafile)));
		int numIndexed = indexer.indexDocuments(list);
		indexer.close();
		
		return numIndexed;
		
	}
	public static void searchTest(){
		
	}
	public static void main(String[] args) {
		try {
	        System.out.println("Number Indexed: " + indexTest("data/index1"));
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
}
