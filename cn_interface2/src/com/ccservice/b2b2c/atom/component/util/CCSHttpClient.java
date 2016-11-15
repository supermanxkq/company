package com.ccservice.b2b2c.atom.component.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.httpclient.*;

public class CCSHttpClient extends HttpClient {

    private int TIME_OUT = 60000;

    private String charset = "UTF-8";

    public CCSHttpClient(boolean ifAddConnectionCloseHeader, Long TIME_OUT) {

        init(ifAddConnectionCloseHeader, TIME_OUT);
    }

    public CCSHttpClient(boolean ifAddConnectionCloseHeader, Long TIME_OUT, String charset) {
        this.charset = charset;
        init(ifAddConnectionCloseHeader, TIME_OUT);
    }

    private void init(boolean ifAddConnectionCloseHeader, Long timeout) {
        if (null != timeout) {
            this.TIME_OUT = timeout.intValue();
        }

        Collection<Header> headers = new ArrayList<Header>();
        headers.add(new Header("Accept", "*/*"));
        headers.add(new Header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"));
        headers.add(new Header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0"));
        headers.add(new Header("UA-CPU", "x86"));
        headers.add(new Header("Accept-Encoding", "gzip, deflate"));
        headers.add(new Header("Content-Type", "application/x-www-form-urlencoded; charset=" + this.charset));

        if (ifAddConnectionCloseHeader) {
            headers.add(new Header("Connection", "close"));
        }
        this.getHttpConnectionManager().getParams().setConnectionTimeout(TIME_OUT);
        this.getHttpConnectionManager().getParams().setSoTimeout(TIME_OUT);

        this.getParams().setParameter("http.default-headers", headers);
    }

}
