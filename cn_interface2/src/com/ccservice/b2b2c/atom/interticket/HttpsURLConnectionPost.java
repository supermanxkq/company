package com.ccservice.b2b2c.atom.interticket;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import java.security.cert.*;
/**
 * @Caless:HttpsURLConnectionPost
 * @ClassDesc:Https Request
 * @Date:2013-1-15 下午12:09:23
 * @Company: 航天华有(北京)科技有限公司
 * @Copyright: Copyright (c) 2013
 * @version: 2.0
 */
public class HttpsURLConnectionPost {

    private myX509TrustManager xtm = new myX509TrustManager();

    private myHostnameVerifier hnv = new myHostnameVerifier();

    public HttpsURLConnectionPost() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL"); //或SSL
            X509TrustManager[] xtmArray = new X509TrustManager[] {xtm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hnv);
    }
    
    class myX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    
    class myHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    
    /**
     * Https Post
     */
    public StringBuffer submitPost(String url, String paramContent,String user,String password) {
    	StringBuffer responseMessage = null;
        HttpsURLConnection urlCon = null;
        try {
        	responseMessage = new StringBuffer();
            urlCon = (HttpsURLConnection)(new URL(url)).openConnection();
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-Length", "1024");
            for(int i=0;i<2;i++){
	            if(i==0){
	              urlCon.setRequestProperty("user",user);
	            }else{
	              urlCon.setRequestProperty("password", password);
	            }
            }
            urlCon.setUseCaches(false);
            urlCon.setDoInput(true);
            urlCon.getOutputStream().write(paramContent.getBytes("UTF-8"));
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "UTF-8"));
			int charCount = -1;
			while ((charCount = in.read()) != -1) {
				responseMessage.append((char) charCount);
			}
        }catch (Exception e) {
        	System.out.println("url=" + url + "?" + paramContent + "\n e=" + e);
        }
		return responseMessage;
    }

    /**
     * Https Request
     */
    public StringBuffer submitPost(String url, String paramContent,String codetype){
    	StringBuffer res = new StringBuffer();
    	URLConnection con = null;
    	OutputStreamWriter out = null;
    	InputStream in = null;
    	BufferedReader reader = null;
    	try {
    		con = (new URL(url)).openConnection();
    		con.setDoOutput(true);
    		con.setUseCaches(false);
    		out = new OutputStreamWriter(con.getOutputStream());
    		out.write(paramContent);
			in = con.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in, codetype));
            int charCount = -1;
            while ((charCount = reader.read()) != -1) {
				res.append((char) charCount);
			}
		} catch (Exception e) {
			res.append(e.getMessage());
		} finally{
			try { if(reader!=null) reader.close(); } catch (Exception e) {}
			try { if(in!=null) in.close(); } catch (Exception e) {}
			try { if(out!=null) out.flush();out.close(); } catch (Exception e) {}
		}
    	return res;
    }
}