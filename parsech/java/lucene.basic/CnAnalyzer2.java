import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class CnAnalyzer2 {
	public static List<IndexableField> fields;

	public static void main(String[] args) throws Exception{
		String xmlFilePath = "/var/tmp/t.xml";
		String content = getContent(xmlFilePath, "content");
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_40);
		TokenStream stream = analyzer.tokenStream(null, new StringReader(content)); 
		CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
		
		int c = 0;
		while(stream.incrementToken()){
			System.out.print(cattr.toString() + " ");
			c = c + 1;
			if(c % 10 == 0){
				System.out.println();
			}
		}
		stream.end();
		stream.close();
	}
	
	public static void print(Object t){
		System.out.println(t);
	}
	
	public static String getContent(String path, String name) throws Exception{
		Element rootElement = new SAXReader().read(new File(path)).getRootElement();
		Iterator elementIterator = rootElement.elementIterator();
		while(elementIterator.hasNext()){
			Element element = (Element) elementIterator.next();
			if(name.equals(element.getName())){
				return element.getText();
			}
		}
		return "";
	}
}
