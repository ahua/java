import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Participle {
	public static boolean ignoreText = false;

	public static String delimiter = " ";

	public static boolean ignoreFreq = false;
	
	public static boolean top = true; 
	
	public static int n = -1;
	
	public static int greate = -1;
	
	public static int less = -1;
	
	public static HashMap hmap = new HashMap();

	public static String usage = "$ prog [--ignore-text] [--ignore-freq] [-d|--delimiter string] [--reverser|-r] [-n number] " +
			"[--greate number] [--less number] file ...";
	
	public static List<String> parseParams(String[] args){
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < args.length; ++i){
			if(args[i].equals("--ignore-text")){
				ignoreText = true;
			}else if(args[i].equals("--ignore-freq")){
				ignoreFreq = true;
			}else if(args[i].equals("-d") || args[i].equals("--delimiter")){
				delimiter = args[++i];
			}else if(args[i].equals("--reverse") || args[i].equals("-r")){
				top = false;
			}else if(args[i].equals("-n")){
				n = Integer.parseInt(args[++i]);
			}else if(args[i].equals("--help") || args[i].equals("-h")){
				System.out.println(usage);
				System.exit(0);
			}else if(args[i].equals("--less")){
				less = Integer.parseInt(args[++i]);
			}else if(args[i].equals("--greate")){
				greate = Integer.parseInt(args[++i]);
			}else{
				list.add(args[i]);
			}
		}
		return list;
	}
	public static void main(String[] args) throws Exception {
		List<String> list = parseParams(args);
		
		for (int i = 0; i < list.size(); ++i) {
			parseText(list.get(i));
		}
		
		if(!ignoreFreq){
			printFreq();
		}
	}

	public static void printFreq(){
		if(n <= 0){
			n = hmap.size();
		}
		
		ArrayList<Entry<String, Integer>> l = new ArrayList<Entry<String, Integer>>(hmap.entrySet());
		Collections.sort(l, new Comparator<Entry<String, Integer>>(){
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
				return (o2.getValue() - o1.getValue());
			}
		});
		
		int total = 0;
		for(int i = 0; i < l.size(); ++i){
			Entry<String, Integer> e = l.get(i);
			String key = e.getKey();
			Integer value = e.getValue();
			
			if(greate > 0 && key.length() >= greate || less > 0 && key.length() <= less || greate < 0 && less < 0){
				System.out.println(value + " " + key);
				
				total = total + 1;
				if(total >= n){
					break;
				}
			}
		}
	}
	
	public static void parseText(String path) throws Exception {
		String content = getContent(path, "content");
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_40);
		TokenStream stream = analyzer.tokenStream(null, new StringReader(
				content));
		CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);

		while (stream.incrementToken()) {
			String token = cattr.toString();
			if (!ignoreText) {
				System.out.print(token + delimiter);
			}
			Integer value = hmap.get(token) == null ? new Integer(1)
					: (Integer) hmap.get(token) + 1;
			hmap.put(token, value);
		}
		if(!ignoreText && !delimiter.equals("\n")){
			System.out.println();
		}
		stream.end();
		stream.close();
	}

	public static String getContent(String path, String name) throws Exception {
		Element rootElement = new SAXReader().read(new File(path))
				.getRootElement();
		Iterator elementIterator = rootElement.elementIterator();
		while (elementIterator.hasNext()) {
			Element element = (Element) elementIterator.next();
			if (name.equals(element.getName())) {
				return element.getText();
			}
		}
		System.err.println("Can't find " + name + " in file " + path + ".");
		return "";
	}
}