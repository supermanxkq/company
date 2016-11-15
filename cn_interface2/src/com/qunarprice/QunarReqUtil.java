package com.qunarprice;

import java.net.URL;
import java.net.Proxy;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URLConnection;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.ccservice.huamin.WriteLog;

import java.util.zip.GZIPInputStream;

import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 去哪儿酒店请求工具类
 */

public class QunarReqUtil {

    private static class myX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static class myHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * @param url 请求链接
     * @param AgentIp 代理IP，为空表示不用代理
     * @param Cookie 请求Cookie
     * @param Referer 访问页面
     * @param type 类型，vcodeImage：去哪儿酒店验证码图片，checkVcode：校验验证码
     * @param reqType 请求类型，post或get
     */
    public static String submit(String url, String AgentIp, String Cookie, String Referer, String type, String reqType) {
        //HTTPS SSL
        if (url.startsWith("https")) {
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL"); //或SSL
                X509TrustManager[] xtmArray = new X509TrustManager[] { new myX509TrustManager() };
                sslContext.init(null, xtmArray, new java.security.SecureRandom());
            }
            catch (GeneralSecurityException e) {
            }
            if (sslContext != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            }
            HttpsURLConnection.setDefaultHostnameVerifier(new myHostnameVerifier());
        }
        //是否使用代理
        boolean proxy = ElongHotelInterfaceUtil.StringIsNull(AgentIp) || "localhost".equals(AgentIp) ? false : true;
        //POST
        StringBuffer res = new StringBuffer();
        URLConnection con = null;
        OutputStreamWriter out = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            if (proxy) {
                String ip = AgentIp.split(":")[0];
                int port = Integer.parseInt(AgentIp.split(":")[1]);
                con = (new URL(url)).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
            }
            else {
                con = (new URL(url)).openConnection();
            }
            con.setDoOutput(true);
            con.setUseCaches(false);
            //Request Headers Start
            String Accept = "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01";
            //验证码图片
            boolean isVcodeImage = "vcodeImage".equals(type) ? true : false;
            boolean isCheckVcode = "checkVcode".equals(type) ? true : false;
            if (isVcodeImage) {
                Accept = "image/webp,*/*;q=0.8";
            }
            String UserAgent = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1976.2 Safari/537.36";
            con.setRequestProperty("Accept", Accept);
            con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
            con.setRequestProperty("Connection", "keep-alive");
            if (!ElongHotelInterfaceUtil.StringIsNull(Cookie) && !isVcodeImage) {
                con.setRequestProperty("Cookie", Cookie);
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(Referer)) {
                con.setRequestProperty("Referer", Referer);
            }
            con.setRequestProperty("User-Agent", UserAgent);
            if (isVcodeImage) {
                con.setRequestProperty("Cache-Control", "max-age=0");
            }
            else {
                con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            }
            //Request Headers End
            //GET请求
            if ("get".equals(reqType)) {
                con.connect();
            }
            else {
                out = new OutputStreamWriter(con.getOutputStream());
                out.write("");
            }
            in = con.getInputStream();
            //验证码图片、校验验证码
            if (isVcodeImage || isCheckVcode) {
                WriteLog.write("去哪儿验证码", con.getHeaderFields().toString());
                //获取Cookie
                String SetCookie = con.getHeaderField("Set-Cookie");
                if (!ElongHotelInterfaceUtil.StringIsNull(SetCookie)) {
                    //下载图片
                    if (isVcodeImage) {
                        QunarCookieUtil.downVcode(in);
                    }
                    //返回Cookie
                    res.append(SetCookie);
                }
            }
            else {
                //判断是否压缩
                String ContentEncoding = con.getHeaderField("Content-Encoding");
                if ("gzip".equals(ContentEncoding)) {
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
        }
        catch (Exception e) {
            res = new StringBuffer();
        }
        finally {
            try {
                if (reader != null)
                    reader.close();
            }
            catch (Exception e) {
            }
            try {
                if (in != null)
                    in.close();
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