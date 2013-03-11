package org.yhyan.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ABTest {
	public static void main(String[] args) throws IOException{
		URL url = new URL("http://54.245.29.2:8088/abtest");
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "Dolphin xxxx");
		conn.setDoOutput(true);

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		
		String data = "abtest=suggestion&cid=xxxxx";
		wr.write(data);
		wr.flush();
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while((line = rd.readLine()) != null){
			System.out.println(line);
		}
		
		wr.close();
		rd.close();
	}
}
