package com.ccservice.b2b2c.atom.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * 2012年7月23日9:28:05
 * @author 陈栋
 *
 */
public class SendPostandGet2 {

    /** 
     * 连接超时 
     */
    private static int connectTimeOut = 5000;

    /** 
     * 读取数据超时 
     */
    private static int readTimeOut = 10000;

    /** 
     * 请求编码 
     */
    private static String requestEncoding = "GBK";

    private static Logger logger = Logger.getLogger(SendPostandGet2.class);

    /** 
     * @return 连接超时(毫秒) 
     * @see com.hengpeng.common.web.SendPostandGet2#connectTimeOut 
     */
    public static int getConnectTimeOut() {
        return SendPostandGet2.connectTimeOut;
    }

    /** 
     * @return 读取数据超时(毫秒) 
     * @see com.hengpeng.common.web.SendPostandGet2#readTimeOut 
     */
    public static int getReadTimeOut() {
        return SendPostandGet2.readTimeOut;
    }

    /** 
     * @return 请求编码 
     * @see com.hengpeng.common.web.SendPostandGet2#requestEncoding 
     */
    public static String getRequestEncoding() {
        return requestEncoding;
    }

    /** 
     * @param connectTimeOut 连接超时(毫秒) 
     * @see com.hengpeng.common.web.SendPostandGet2#connectTimeOut 
     */
    public static void setConnectTimeOut(int connectTimeOut) {
        SendPostandGet2.connectTimeOut = connectTimeOut;
    }

    /** 
     * @param readTimeOut 读取数据超时(毫秒) 
     * @see com.hengpeng.common.web.SendPostandGet2#readTimeOut 
     */
    public static void setReadTimeOut(int readTimeOut) {
        SendPostandGet2.readTimeOut = readTimeOut;
    }

    /** 
     * @param requestEncoding 请求编码 
     * @see com.hengpeng.common.web.SendPostandGet2#requestEncoding 
     */
    public static void setRequestEncoding(String requestEncoding) {
        SendPostandGet2.requestEncoding = requestEncoding;
    }

    /** 
     * <pre> 
     * 发送带参数的GET的HTTP请求 
     * </pre> 
     *  
     * @param reqUrl HTTP请求URL 
     * @param parameters 参数映射表 
     * @return HTTP响应的字符串 
     */
    public static String doGet(String reqUrl, Map<?, ?> parameters, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Iterator<?> iter = parameters.entrySet().iterator(); iter.hasNext();) {
                Entry<?, ?> element = (Entry<?, ?>) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(), SendPostandGet2.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(SendPostandGet2.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时  
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(SendPostandGet2.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时  
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk  
            // 1.5换成这个,连接超时  
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时  
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer temp = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        }
        catch (IOException e) {
            logger.error("网络故障", e);
        }
        finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    /** 
     * <pre> 
     * 发送不带参数的GET的HTTP请求 
     * </pre> 
     *  
     * @param reqUrl HTTP请求URL 
     * @return HTTP响应的字符串 
     */
    public static String doGet(String reqUrl, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            String queryUrl = reqUrl;
            int paramIndex = reqUrl.indexOf("?");

            if (paramIndex > 0) {
                queryUrl = reqUrl.substring(0, paramIndex);
                String parameters = reqUrl.substring(paramIndex + 1, reqUrl.length());
                String[] paramArray = parameters.split("&");
                for (int i = 0; i < paramArray.length; i++) {
                    String string = paramArray[i];
                    int index = string.indexOf("=");
                    if (index > 0) {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1, string.length());
                        params.append(parameter);
                        params.append("=");
                        params.append(URLEncoder.encode(value, SendPostandGet2.requestEncoding));
                        params.append("&");
                    }
                }

                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(queryUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(SendPostandGet2.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时  
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(SendPostandGet2.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时  
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk  
            // 1.5换成这个,连接超时  
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时  
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer temp = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        }
        catch (IOException e) {
            logger.error("网络故障", e);
        }
        finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    /** 
     * <pre> 
     * 发送带参数的POST的HTTP请求 
     * </pre> 
     *  
     * @param reqUrl HTTP请求URL 
     * @param parameters 参数映射表 
     * @return HTTP响应的字符串 
     */
    public static String doPost(String reqUrl, Map<String, String> parameters, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Iterator<?> iter = parameters.entrySet().iterator(); iter.hasNext();) {
                Entry<?, ?> element = (Entry<?, ?>) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(), SendPostandGet2.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(SendPostandGet2.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时  
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(SendPostandGet2.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时  
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk  
            // 1.5换成这个,连接超时  
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时  
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        }
        catch (IOException e) {
            logger.error("网络故障", e);
        }
        finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    /**
     * 
     * @param url
     * @param paramContent
     * @param codetype
     * @param keymapcookie
     * @return
     */
    public static StringBuffer submitPost(String url, String paramContent, String codetype,
            Map<String, String> keymapcookie) {
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

            //            connection.addRequestProperty("Cookie", "JSESSIONID=" + cookies);

            String cookies = "";
            for (String key : keymapcookie.keySet()) {
                //                connection.addRequestProperty(key, keymapcookie.get(key));
                cookies += key + "=" + keymapcookie.get(key);
            }
            connection.addRequestProperty("Cookie", cookies);
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
     * 
     * @param url
     * @param paramContent
     * @param codetype
     * @param keymapcookie
     * @return
     */
    public static StringBuffer submitGet(String url, String codetype, Map<String, String> keymapcookie) {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        try {
            responseMessage = new StringBuffer();
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setDoOutput(true);
            String cookies = "";
            for (String key : keymapcookie.keySet()) {
                //                connection.addRequestProperty(key, keymapcookie.get(key));
                cookies += key + "=" + keymapcookie.get(key);
            }
            connection.addRequestProperty("Cookie", cookies);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, codetype));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
        }
        catch (Exception ex) {
        }
        finally {
            try {
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
            }
        }
        return responseMessage;
    }
}
