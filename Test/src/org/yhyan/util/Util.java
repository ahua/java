package org.yhyan.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	public static String md5(String input) throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bytes = md.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < bytes.length; ++i){
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
