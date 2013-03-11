package org.yhyan.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexViewer {
	public void printAllDoc(List<Document> list){
		for(int i = 0; i < list.size(); i++){
			Document d = list.get(i);
			List<Fieldable> l = d.getFields();
			for(int i1 = 0; i1 < l.size(); ++i1){
				String k = l.get(i1).name();
				String v = d.get(k);
				System.out.println(k + ":" + v + ";");
			}
		}
	}
	
	public List<Document> getDocuments(IndexReader reader){
		List<Document> list = new ArrayList<Document>();
		for(int i = 0; i < reader.maxDoc(); ++i){
			if(reader.isDeleted(i))
				continue;
			try {
	            list.add(reader.document(i));
            } catch (CorruptIndexException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
		
		return list;
	}
	
	public IndexReader getIndexReader(String indexPath) {
		try {
	        Directory d = FSDirectory.open(new File(indexPath));
	        
	        return IndexReader.open(d);
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return null;
	}
	
	public static void main(String[] args){
		IndexViewer iv = new IndexViewer();
		
		String indexPath = "/home/yhyan/ahua.git/java/Test/data/index1";
		IndexReader ir = iv.getIndexReader(indexPath);
		List<Document> list = iv.getDocuments(ir);
		
		iv.printAllDoc(list);
	}
}
