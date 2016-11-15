package com.ccservice.b2b2c.atom.interticket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HttpClient {

    public static int START = 0;

    public static String getString(String str, String start, String end, int st) {

        int s = str.indexOf(start, st);
        if (s < 0) {
            START = s;
            return null;
        }
        int e = str.indexOf(end, s + start.length());
        START = e;
        return str.substring(s + start.length(), e);

    }

    public static byte[] httpget(String url) {
        try {

            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();

            conn.setDoInput(true);

            conn.connect();

            InputStream in = conn.getInputStream();

            byte[] buf = new byte[2046];
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int len = 0;
            int size = 0;
            while ((len = in.read(buf)) > 0) {
                bout.write(buf, 0, len);
                size += len;
            }

            //			System.out.println(new String(content,0,size));
            in.close();
            conn.disconnect();

            return bout.toByteArray();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void httpgetfile(String url, File file) {

        try {

            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();

            conn.setDoInput(true);

            conn.connect();

            InputStream in = conn.getInputStream();

            byte[] buf = new byte[2046];
            FileOutputStream bout = new FileOutputStream(file);
            int len = 0;
            int size = 0;
            while ((len = in.read(buf)) > 0) {
                bout.write(buf, 0, len);
                size += len;
            }

            //			System.out.println(new String(content,0,size));
            in.close();
            conn.disconnect();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String httpget(String url, String encode) {

        try {
            return new String(httpget(url), encode);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void BytetoFile(String name, byte[] content) {
        if (content == null) {
            new File(name).mkdir();
            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(name));
            out.write(content);
            out.close();
        }
        catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static void StringtoFile(String name, String content, String encode) {
        if (content == null) {
            new File(name).mkdir();
            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(name));
            out.write(content.getBytes(encode));
            out.close();
        }
        catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static byte[] filetoString(File f) {
        try {
            FileInputStream fin = new FileInputStream(f);

            byte[] bs = new byte[fin.available()];
            fin.read(bs);
            fin.close();

            return bs;
        }
        catch (Exception e) {

            e.printStackTrace();
            return null;

        }
    }

    public static String MD5(String input) throws NoSuchAlgorithmException {
        if (input == null || input.length() == 0)
            return "";

        String out = "";
        byte[] output;
        MessageDigest m = MessageDigest.getInstance("MD5");
        output = m.digest(input.getBytes());
        int len = output.length;

        for (int i = 0; i < len; i++) {

            String t = Integer.toHexString(output[i] & 0x00FF);

            out += (t.length() == 1) ? ("0" + t) : t;

        }

        return out.toLowerCase();
    }

}
