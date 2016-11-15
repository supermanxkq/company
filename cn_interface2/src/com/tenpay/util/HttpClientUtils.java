package com.tenpay.util;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;



public class HttpClientUtils {
    private static RequestConfig defaultConfig;
    private static PoolingHttpClientConnectionManager cm;
    private static CloseableHttpClient threadSafeClient;
    private static CloseableHttpClient httpsClient;
    private static SSLContext sslContext;
    private static SSLConnectionSocketFactory sslsf;

    private HttpClientUtils() {
    }

    public static CloseableHttpClient createClientWithPool() {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(defaultConfig).setConnectionManager(cm).build();
        return client;
    }

    public static CloseableHttpClient getThreadSafeClient() {
        return threadSafeClient;
    }

    public static CloseableHttpClient getHTTPSClient() {
        return httpsClient;
    }

    static {
        try {
            sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
           sslsf = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        defaultConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(10000).build();
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(1024);
        cm.setDefaultMaxPerRoute(300);
        threadSafeClient = HttpClients.custom().setDefaultRequestConfig(defaultConfig).setConnectionManager(cm).build();

        try {
            SSLContext e = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(e);
            httpsClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException var2) {
            var2.printStackTrace();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        } catch (KeyStoreException var4) {
            var4.printStackTrace();
        }

    }
}