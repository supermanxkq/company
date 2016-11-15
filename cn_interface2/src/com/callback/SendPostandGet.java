package com.callback;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * 2012�?7�?23�?9:28:05
 * 
 * @author 陈栋
 *
 */
public class SendPostandGet {

    /**
     * 设置超时时间
     * 
     * @param url
     * @param paramContent
     * @param codetype
     * @param timeout
     *            超时时间
     * @return
     */
    public static StringBuffer submitPostTimeOut(String url, String paramContent, String codetype, int timeout) {
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
            ex.printStackTrace();
            System.out.println("url=" + url + "?" + paramContent + "\n e=" + ex);
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
     * java.net实现 HTTP POST方法提交
     * 
     * @param url
     * @param paramContent
     * @return
     */
    public static StringBuffer submitPost(String url, String paramContent, String codetype) {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        try {
            String param = paramContent;
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
        }
        finally {
            try {
                br.close();
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
                System.out.println("paramContent=" + paramContent + "|err=" + e);
            }
        }
        return responseMessage;
    }

    private static InputStream connmethod(String urlstr, String paramstr, String code) {
        try {
            System.out.println(paramstr);
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), code);
            out.write(paramstr);
            out.flush();
            out.close();
            return conn.getInputStream();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String submitGet2(String urlstr, String paramstr, String code) {
        BufferedReader reader = null;
        String str = "";
        try {
            reader = new BufferedReader(new InputStreamReader(connmethod(urlstr, paramstr, code), code));
            String lines;
            StringBuffer linebuff = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                linebuff.append(lines);
            }
            str = linebuff.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * java.net实现 HTTP或HTTPs GET方法提交
     * 
     * @param strUrl
     *            提交的地�?及参�? code 编码格式
     * @return 返回的response信息
     */
    public static String submitGet(String strUrl, String code) {
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
            e.printStackTrace();
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

    /**
     * 作�?�：邹远�? 日期�?2014�?8�?29�? 说明：java.net实现 HTTP POST方法提交 用于国际机票
     * 
     * @param url
     * @param paramContent
     * @param codetype
     * @param cookie
     * @return
     */
    public static StringBuffer submitPost2(String url, String paramContent, String codetype, String cookie) {
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
            connection.setDoOutput(true);
            connection.addRequestProperty("Cookie", cookie);
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
            ex.printStackTrace();
            System.out.println("url=" + url + "?" + paramContent + "\n e=" + ex);
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
     *            提交的地�?及参�?
     * @return 返回的response信息
     */
    public static String submitGet(String strUrl) {
        URLConnection connection = null;
        BufferedReader reader = null;
        String str = null;
        try {
            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            // 取得输入流，并使用Reader读取
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer linebuff = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                linebuff.append(lines);
            }
            str = linebuff.toString();
        }
        catch (Exception e) {
            System.out.println(strUrl);
            e.printStackTrace();
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

    public static String submitPostDama(String url, byte[] bytes) {
        StringBuffer res = new StringBuffer();
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection con = null;
        BufferedReader reader = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "multipart/form-data");
            out = con.getOutputStream();
            out.write(bytes);
            in = con.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                res.append(lineTxt);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            res = new StringBuffer();
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
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
            catch (Exception e) {
            }
        }
        return res.toString();
    }

}
