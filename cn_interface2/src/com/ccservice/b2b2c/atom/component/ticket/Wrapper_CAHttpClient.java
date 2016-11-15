package com.ccservice.b2b2c.atom.component.ticket;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.httpclient.*;

public class Wrapper_CAHttpClient extends HttpClient {

    private int TIME_OUT = 60000;

    private String charset = "UTF-8";

    public Wrapper_CAHttpClient(boolean ifAddConnectionCloseHeader, Long TIME_OUT) {

        init(ifAddConnectionCloseHeader, TIME_OUT);
    }

    public Wrapper_CAHttpClient(boolean ifAddConnectionCloseHeader, Long TIME_OUT, String charset) {
        this.charset = charset;
        init(ifAddConnectionCloseHeader, TIME_OUT);
    }

    private void init(boolean ifAddConnectionCloseHeader, Long timeout) {
        if (null != timeout) {
            this.TIME_OUT = timeout.intValue();
        }

        Collection<Header> headers = new ArrayList<Header>();

        headers.add(new Header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new Header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"));
        String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2421.0 Safari/537.36";
        headers.add(new Header("User-Agent", UserAgent));
        headers.add(new Header("UA-CPU", "x86"));
        headers.add(new Header("Accept-Encoding", "gzip, deflate"));
        //        headers.add(new Header("Content-Type", "application/x-www-form-urlencoded; charset=" + this.charset));
        headers.add(new Header("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new Header("Connection", "keep-alive"));

        if (ifAddConnectionCloseHeader) {
            headers.add(new Header("Connection", "close"));
        }
        this.getHttpConnectionManager().getParams().setConnectionTimeout(TIME_OUT);
        this.getHttpConnectionManager().getParams().setSoTimeout(TIME_OUT);

        this.getParams().setParameter("http.default-headers", headers);
    }

}
