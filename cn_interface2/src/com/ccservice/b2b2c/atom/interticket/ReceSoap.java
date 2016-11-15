package com.ccservice.b2b2c.atom.interticket;

import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;


/**
 * 解析soap格式的xml
 * 
 * @author
 */
public class ReceSoap extends VisitorSupport {
	String op = "";
	Document doc1 = null;

	@Override
	public void visit(Element node) {
		Document doc = null;
		if (op.equals(node.getName())) {
			try {
				doc = DocumentHelper.parseText(node.getText());// str to xml
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 在这里返回xml
			
			this.doc1 = doc;
		}
	}
	public static void main(String[] args) throws DocumentException {//测试
		SAXReader reader = new SAXReader();
		Document doc = reader.read("WebRoot/App/test3.xml");
	//	System.out.println(doc.asXML());
		ReceSoap t = new ReceSoap();
		t.op = "StrXml";
	//	System.out.println("doc1:" + t.doc1);
		doc.accept(t);
		if(t.doc1 != null)
			System.out.println("reslut::"+doc.asXML());
		else
			System.out.println("reslut:: NULL");
	//	System.out.println("doc1:" + t.doc1.asXML());
	}
	public static Document openXmlDocument(InputStream in) {
		Document resDoc = null;
		SAXReader reader = new SAXReader();
		try {
			return reader.read(in);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return resDoc;
	}
}
