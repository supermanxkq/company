package com.ccservice.b2b2c.atom.hotel;

import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.io.ByteArrayOutputStream;
import com.ccservice.b2b2c.atom.qunar.PHUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class JLHotel {
    public static String postMiddleData(String xml) throws Exception {
        //请求XML
        xml = URLEncoder.encode(xml.toString(), "utf-8");
        //中转地址
        String url = "http://" + PropertyUtil.getValue("jlOrderMiddleServer") + "/cn_interface/JlTourOrder.jsp";
        //POST
        String ret = PHUtil.submitPost(url, xml).toString();
        //RETURN
        return ret;
    }

    public static String postSendJL(String xml) throws Exception {
        URL url = new URL(PropertyUtil.getValue("jlOrderUrl").trim());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "text/xml");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
        byte[] bypes = xml.getBytes("utf-8");
        conn.getOutputStream().write(bypes);
        InputStream inStream = conn.getInputStream();
        String output = new String(readInputStream(inStream), "utf-8");
        return output;

    }

    public static String postSendLH(String xml) throws Exception {
        URL url = new URL("http://123.196.114.122:8090/cn_interface/UpdateState.jspx");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "text/xml");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
        byte[] bypes = xml.getBytes("utf-8");
        conn.getOutputStream().write(bypes);
        InputStream inStream = conn.getInputStream();
        String output = new String(readInputStream(inStream), "utf-8");
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
