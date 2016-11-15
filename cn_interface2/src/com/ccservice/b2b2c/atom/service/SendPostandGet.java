package com.ccservice.b2b2c.atom.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * 2012年7月23日9:28:05
 * @author 陈栋
 *
 */
public class SendPostandGet {
    /**
     * java.net实现 HTTP POST方法提交
     * 
     * @param url
     * @param paramContent
     * @return 
     * @throws Exception 
     */
    public static StringBuffer submitPost(String url, String paramContent, String codetype) throws Exception {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        String param = paramContent;
        try {
            // System.out.println("url=" + url + "?" + paramContent + "\n");
            // System.out.println("===========post method start=========");
            responseMessage = new StringBuffer();
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setDoOutput(true);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.write(param);
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(in, codetype));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
            // System.out.println(responseMessage);
            // System.out.println("===========post method end=========");
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            try {
                br.close();
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("paramContent=" + paramContent + "|err=" + Arrays.toString(e.getStackTrace()));
            }
        }
        return responseMessage;
    }

    /**
     * java.net实现 HTTP POST方法提交
     * 
     * @param url
     * @param paramContent
     * @return
     * @throws Exception 
     */
    public static StringBuffer submitPost(String url, String paramContent) throws Exception {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        String param = paramContent;
        try {

            //          System.out.println("url=" + url + "?" + paramContent + "\n");
            //          System.out.println("===========post method start=========");
            responseMessage = new StringBuffer();
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setDoOutput(true);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.write(param);
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
            //          System.out.println(responseMessage);
            //          System.out.println("===========post method end=========");
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            try {
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
                System.out.println("paramContent=" + paramContent + "|err=" + e);
            }
        }
        return responseMessage;
    }

    /**
     * java.net实现 HTTP或HTTPs GET方法提交
     * 
     * @param strUrl
     *            提交的地址及参数
     * @return 返回的response信息
     * @throws Exception 
     */
    public static String submitGet(String strUrl) throws Exception {
        URLConnection connection = null;
        BufferedReader reader = null;
        String str = null;
        try {
            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String lines;
            StringBuffer linebuff = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                linebuff.append(lines);
            }
            str = linebuff.toString();
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String get1(String url) throws Exception {
        StringBuffer res = new StringBuffer();
        InputStream in = null;
        HttpsURLConnection con = null;
        BufferedReader reader = null;
        try {
            con = (HttpsURLConnection) (new URL(url)).openConnection();
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.connect();
            in = con.getInputStream();
            //判断是否压缩
            String ContentEncoding = con.getHeaderField("Content-Encoding");
            if ("gzip".equalsIgnoreCase(ContentEncoding)) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(in), "UTF-8"));
            }
            else {
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            }
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                res.append(lineTxt);
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e) {
            }
        }
        return res.toString();
    }

    /**
     * 设置超时时间
     * 
     * @param url
     * @param paramContent
     * @param codetype
     * @param timeout
     *            超时时间
     * @return
     * @throws Exception 
     * @throws IOException 
     */
    public static StringBuffer submitPostTimeOutFiend(String url, String paramContent, String codetype, int timeout)
            throws Exception {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        String param = paramContent;
        responseMessage = new StringBuffer();
        try {
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setDoOutput(true);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.write(param);
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, codetype));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
        }
        catch (Exception e) {
            throw e;
        }
        try {
            if (in != null) {
                in.close();
            }
            if (reqOut != null) {
                reqOut.close();
            }
        }
        catch (Exception e) {
            System.out.println("paramContent=" + paramContent + "|err=" + e);
        }
        return responseMessage;

    }

    /**
     * 设置超时时间
     * 
     * @param url
     * @param paramContent
     * @param codetype
     * @param timeout
     *            超时时间
     * @return
     * @throws Exception 
     */
    public static StringBuffer submitPostTimeOut(String url, String paramContent, String codetype, int timeout)
            throws Exception {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        String param = paramContent;
        try {
            responseMessage = new StringBuffer();
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setDoOutput(true);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.write(param);
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, codetype));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            try {
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("paramContent=" + paramContent + "|err=" + e);
            }
        }
        return responseMessage;

    }

    /**
     * java.net实现 HTTP或HTTPs GET方法提交
     * 
     * @param strUrl
     *            提交的地址及参数 code 编码格式
     * @return 返回的response信息
     * @throws Exception 
     */
    public static String submitGet(String strUrl, String code) throws Exception {
        URLConnection connection = null;
        BufferedReader reader = null;
        String str = null;
        try {
            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("Content-Type", "application/json");
            // 取得输入流，并使用Reader读取
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), code));
            String lines;
            StringBuffer linebuff = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                linebuff.append(lines);
            }
            str = linebuff.toString();
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }
}
