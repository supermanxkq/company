package com.ccservice.b2b2c.atom.interticket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * 发送soap格式的xml请求
 * 
 */
public class SendSoap {
	public Document send(String url1, String SOAPAction, String soap) {
		// TODO Auto-generated method stub
		Document reqDoc = null;
		try {
			URI uri = new URI(url1);   
			URL url = uri.toURL();      
			// 打开连接   
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			// 可读取   
			httpUrlConnection.setRequestMethod("POST");
			
			
			// set request header   
			httpUrlConnection.setRequestProperty("SOAPAction", "http://intf.atm86.com/inter-soap.pl/inter_price_ow");   
			httpUrlConnection.setRequestProperty("Content-Length", soap.length() + "");   
			
			httpUrlConnection.setDoInput(true);   
			httpUrlConnection.setDoOutput(true);
			//httpUrlConnection.connect();
			httpUrlConnection.setUseCaches(false);
			
			OutputStream os = httpUrlConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(soap);
            osw.flush();
            osw.close();
            System.out.println("After flushing output stream. ");
            System.out.println("Getting an input stream...");
            InputStream is = httpUrlConnection.getInputStream();
            // any response?
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null)
            {
                System.out.println("line: " + line);
            }
            httpUrlConnection.disconnect(); 

		} catch (Exception e) {
			e.printStackTrace();
			return reqDoc;
			
		}
		
		return reqDoc;
	}
	
	/**
	 * 从InputStream中读取Document对象
	 * 
	 * @param in
	 * @return
	 */
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
