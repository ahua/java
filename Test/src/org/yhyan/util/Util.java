package org.yhyan.util;

import org.apache.lucene.document.Document;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Util {
	
	public static boolean debug = true;
	
	public static String md5(String input) throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bytes = md.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < bytes.length; ++i){
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
	
	public static void printDocument(Document doc){
		System.out.println(doc.toString());
	}
	
	public static void printDocuments(List<Document> list){
		for(Document doc : list){
			printDocument(doc);
		}
	}
}
