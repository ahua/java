import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlTest {
	public static void main(String[] args) throws Exception {
		SAXReader xmlReader = new SAXReader();
		Document doc = xmlReader.read(new File("/tmp/t.xml"));
		Element rootElement = doc.getRootElement();
		Iterator elementIterator = rootElement.elementIterator();
		while(elementIterator.hasNext()){
			Element element = (Element) elementIterator.next();
			System.out.println(element.getName());
			System.out.println(element.getText());
		}
	}
}
