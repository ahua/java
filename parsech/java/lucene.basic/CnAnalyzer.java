import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
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


public class CnAnalyzer {
	public static List<IndexableField> fields;

	public static void main(String[] args) throws Exception{
		Directory d = new RAMDirectory();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_40, new SmartChineseAnalyzer(Version.LUCENE_40));
		IndexWriter writer = new IndexWriter(d, conf);
		
//		Scanner userInput = new Scanner(System.in);
//		print("Please input xml file path: ");
//		String xmlFilePath = userInput.nextLine();
		String xmlFilePath = "/var/tmp/t.xml";
		print(xmlFilePath);
		
		
		writer.addDocument(getDoc(xmlFilePath));
		writer.close();
		print("finished");
		

		DirectoryReader reader = DirectoryReader.open(d);

		
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Query q = new MatchAllDocsQuery();
		
		ScoreDoc[] hits = searcher.search(q, reader.maxDoc()).scoreDocs;
		for(int i = hits.length - 1; i >= 0; --i){
			Document doc = searcher.doc(hits[i].doc);
			fields = doc.getFields();
			for(int j = fields.size() - 1; j >= 0; --j){
				IndexableField field = fields.get(j);
				System.out.println(field.name());
				System.out.println(field.stringValue());
				Terms terms = reader.getTermVector(hits[i].doc, field.name());
				
				System.out.println(terms);
			}
		}
	}
	
	public static void print(Object t){
		System.out.println(t);
	}
	
	public static Document getDoc(String path) throws Exception{
		Element rootElement = new SAXReader().read(new File(path)).getRootElement();
		Iterator elementIterator = rootElement.elementIterator();
		Document doc = new Document();
		while(elementIterator.hasNext()){
			Element element = (Element) elementIterator.next();
			doc.add(new TextField(element.getName(), element.getText(), Store.YES));
		}
		return doc;
	}
}
