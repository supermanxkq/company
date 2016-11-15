package com.ccservice.b2b2c.framework;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

public class URLSupport extends TongchengSupplyMethod {

    public HttpURLConnection openConnection(String urlstr) {
        try {
            URL url;
            url = new URL(urlstr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "gb2312");
            connection.connect();
            return connection;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String request(String url) throws Exception {
        URL httpurl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) httpurl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "gb2312");
        connection.connect();
        BufferedInputStream buffer = new BufferedInputStream(connection.getInputStream());
        byte[] bytes = new byte[2048];
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        int i = 0;
        while ((i = buffer.read(bytes)) > 0) {
            byteout.write(bytes, 0, i);
        }
        buffer.close();
        connection.disconnect();

        return new String(byteout.toByteArray(), "gb2312");

    }

}
