package com.ccservice.b2b2c.atom.hotel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTCHotel {

	public static String postSendHTC(String xml) throws Exception {
		URL url = new URL("http://sw.hubs1.net/servlet/SwitchReceiveServlet");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");// 提交模式
		// conn.setConnectTimeout(10000);//连接超时 单位毫秒
		// conn.setReadTimeout(2000);//读取超时 单位毫秒
		conn.setDoOutput(true);// 是否输入参数
		conn.setRequestProperty("Content-Type", "text/xml");
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
		System.out.println("xml:" + xml);
		byte[] bypes = xml.getBytes("utf-8");                           
		conn.getOutputStream().write(bypes);// 输入参数
		InputStream inStream = conn.getInputStream();
		String output = new String(readInputStream(inStream), "utf-8");
		System.out.println("output:" + output);
		return output;

	}
	public static String postSendSerchHotel(String xml) throws Exception {
		URL url = new URL("http://swsearch.hubs1.net/servlet/SwitchReceiveServlet");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");// 提交模式
		// conn.setConnectTimeout(10000);//连接超时 单位毫秒
		// conn.setReadTimeout(2000);//读取超时 单位毫秒
		conn.setDoOutput(true);// 是否输入参数
		conn.setRequestProperty("Content-Type", "text/xml");
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
		//System.out.println("xml:" + xml);
		byte[] bypes = xml.getBytes("utf-8");
		conn.getOutputStream().write(bypes);// 输入参数
		InputStream inStream = conn.getInputStream();
		String output = new String(readInputStream(inStream), "utf-8");
		//System.out.println("output:" + output);
		return output;

	}
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();// 网页的二进制数据
		outStream.close();
		inStream.close();
		return data;
	}
}
