package com.ccservice.b2b2c.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// http通讯类
public class HttpUtils {

    // url中每组参数间的分隔符
    private static final String PARAM_CONNECT_FLAG = "&";

    // url中地址与参数间的分隔符
    private static final String URL_PARAM_CONNECT_FLAG = "?";

    // url中每组参数中键与值间的分隔符
    private static final String KEY_VALUE_CONNECT_FLAG = "=";

    // 取文本流时的缓存大小
    private static final int tempLength = 1024;

    private static final String encodeCode = "iso-8859-1";

    private static final String reqEncodeCode = "gbk";

    private Log log = LogFactory.getLog(HttpUtils.class);

    private HttpUtils() {
    }

    private static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String arg0, SSLSession arg1) {
            System.out.println("Warning: URL Host: " + arg0 + " vs. " + arg1.getPeerHost());
            return true;
        }
    };

    public static StringBuffer URLGet(String strUrl, Map parameterMap, String encodeCode) throws IOException {
        String strTotalURL = "";
        strTotalURL = getTotalURL(strUrl, parameterMap);
        //URL url = new URL(strTotalURL);
        URL url = new URL(null, strTotalURL, new sun.net.www.protocol.https.Handler());
        if (strUrl.trim().startsWith("https")) {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getStringBufferFormBufferedReader(in);
        }
        else {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getStringBufferFormBufferedReader(in);
        }
    }

    public static StringBuffer URLGet(String strUrl, String content) throws IOException {
        String strTotalURL = "";
        strTotalURL = getTotalURL(strUrl, content);
        //URL url = new URL(strTotalURL);
        URL url = new URL(null, strTotalURL, new sun.net.www.protocol.https.Handler());

        if (strUrl.trim().startsWith("https")) {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getStringBufferFormBufferedReader(in);
        }
        else {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getStringBufferFormBufferedReader(in);
        }
    }

    public static InputStream URLPost(String strUrl, Map map) throws IOException {
        String content = getContentURL(map);
        //URL url = new URL(strUrl);
        URL url = new URL(null, strUrl, new sun.net.www.protocol.https.Handler());
        if (strUrl.trim().startsWith("https")) {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setAllowUserInteraction(false);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
            BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            bout.write(content);
            bout.flush();
            bout.close();
            return con.getInputStream();
            ////return getStringBufferFormBufferedReader(in);
        }
        else {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int length = con.getContentLength();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setAllowUserInteraction(false);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
            BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            bout.write(content);
            bout.flush();
            bout.close();
            return con.getInputStream();
            ///return getStringBufferFormBufferedReader(in);
        }
    }

    public static StringBuffer URLPost(String strUrl, String content) throws IOException {

        //URL url = new URL(strUrl);
        URL url = new URL(null, strUrl, new sun.net.www.protocol.https.Handler());
        if (strUrl.trim().startsWith("https")) {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            int length = con.getContentLength();
            con.setDoInput(true);
            //			con.setDoOutput(true);
            con.setAllowUserInteraction(false);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
            BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            bout.write(content);
            bout.flush();
            bout.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getStringBufferFormBufferedReader(in);
        }
        else {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int length = con.getContentLength();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setAllowUserInteraction(false);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
            BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            bout.write(content);
            bout.flush();
            bout.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getStringBufferFormBufferedReader(in);
        }
    }

    public static StringBuffer getStringBufferFormBufferedReader(BufferedReader in) throws IOException {
        StringBuffer returnStringBuffer = new StringBuffer();
        char[] tmpbuf = new char[tempLength];
        int num = in.read(tmpbuf);
        while (num != -1) {
            returnStringBuffer.append(tmpbuf, 0, num);
            num = in.read(tmpbuf);
        }
        in.close();
        return returnStringBuffer;

    }

    public static String getTotalURL(String strUrl, Map parameterMap) {
        String content = getContentURL(parameterMap);
        return getTotalURL(strUrl, content);
    }

    public static String getTotalURL(String strUrl, String content) {
        String totalURL = strUrl;
        if (totalURL.indexOf(URL_PARAM_CONNECT_FLAG) == -1) {
            totalURL += URL_PARAM_CONNECT_FLAG;
        }
        else {
            totalURL += PARAM_CONNECT_FLAG;
        }
        totalURL += content;
        return totalURL;
    }

    public static String getContentURL(Map parameterMap) {

        if (null == parameterMap || parameterMap.keySet().size() == 0) {
            return ("");
        }
        StringBuffer url = new StringBuffer();
        Set keys = parameterMap.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = String.valueOf(i.next());
            if (parameterMap.containsKey(key)) {
                Object val = parameterMap.get(key);
                String str = val != null ? val.toString() : "";
                try {
                    str = URLEncoder.encode(str, reqEncodeCode);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url.append(key).append(KEY_VALUE_CONNECT_FLAG).append(str).append(PARAM_CONNECT_FLAG);
            }
        }
        String strURL = "";
        strURL = url.toString();
        if (PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
            strURL = strURL.substring(0, strURL.length() - 1);
        }
        return strURL;
    }

    private static void trustAllHttpsCertificates() {
        try {
            // Create a trust manager that does not validate certificate chains:
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
            javax.net.ssl.TrustManager tm = new MyTrustManager();
            trustAllCerts[0] = tm;
            javax.net.ssl.SSLContext sc;
            sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //log.debug(e);
        }
        catch (KeyManagementException e) {
            e.printStackTrace();
            ///log.debug(e);
        }
        catch (Exception e) {
            e.printStackTrace();
            ////log.debug(e);
        }
    }

    /**
     * java.net实现 HTTP或HTTPs GET方法提交,变体
     * @param strUrl
     * @return
     * @time 2014年8月30日 下午2:59:53
     * @author yinshubin
     */
    public static String Get_https(String strUrl) {
        StringBuffer responseMessage = null;
        URLConnection connection = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            HttpsURLConnection conn = (HttpsURLConnection) connection;
            conn.setSSLSocketFactory(sc.getSocketFactory());

            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setDoOutput(true);
            InputStream is = conn.getInputStream();
            if (is != null) {
                int charCount = -1;
                BufferedReader br = null;
                responseMessage = new StringBuffer();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((charCount = br.read()) != -1) {
                    responseMessage.append((char) charCount);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return responseMessage.toString();
    }

    /**
     * java.net实现 HTTP或HTTPs GET方法提交,变体
     * @param strUrl
     * @return
     * @time 2014年8月30日 下午2:59:53
     * @author yinshubin
     */
    public static String Get_https(String strUrl, Integer outtime) {
        StringBuffer responseMessage = null;
        URLConnection connection = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            HttpsURLConnection conn = (HttpsURLConnection) connection;
            conn.setSSLSocketFactory(sc.getSocketFactory());
            if (outtime == null || outtime <= 0) {
                conn.setConnectTimeout(40000);
                conn.setReadTimeout(40000);
            }
            else {
                conn.setConnectTimeout(outtime);
                conn.setReadTimeout(outtime);
            }
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setDoOutput(true);
            InputStream is = conn.getInputStream();
            if (is != null) {
                int charCount = -1;
                BufferedReader br = null;
                responseMessage = new StringBuffer();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((charCount = br.read()) != -1) {
                    responseMessage.append((char) charCount);
                }
            }
        }
        catch (Exception e) {
            //            e.printStackTrace();
        }
        return responseMessage == null ? "" : responseMessage.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}

class MyTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }

    public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

}
