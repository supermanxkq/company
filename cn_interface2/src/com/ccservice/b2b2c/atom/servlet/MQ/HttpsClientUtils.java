package com.ccservice.b2b2c.atom.servlet.MQ;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;

public class HttpsClientUtils {
    /**
     *  get访问url
     * 
     * @param search_url 访问的url
     * @param outtime 超时时间
     * @return
     * @time 2015年3月2日 上午11:22:34
     * @author chendong
     */
    public static String gethttpclientdata(String search_url, Long outtime) {
        String json = "-1";
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, outtime);
        get = new CCSGetMethod(search_url);
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        get.setFollowRedirects(false);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(get);
            json = get.getResponseBodyAsString();
        }
        catch (HttpException e) {
        }
        catch (IOException e) {
        }
        return json;
    }

    /**
     *  post访问url
     * 
     * @param search_url 访问的url
     * @param outtime 超时时间
     * @return
     * @time 2015年3月2日 上午11:22:34
     * @author chendong
     */
    public static String posthttpclientdata(String search_url, Map<String, String> listdata, Long outtime) {
        String json = "";
        CCSPostMethod post = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, outtime);
        post = new CCSPostMethod(search_url);
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        post.setFollowRedirects(false);
        for (String key : listdata.keySet()) {
            post.setParameter(key, listdata.get(key));
        }
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(post);
            json = post.getResponseBodyAsString();
        }
        catch (HttpException e) {
        }
        catch (IOException e) {
        }
        return json;
    }

    public static void main(String[] args) {
        Map m = new HashMap();
        //        String strte = "{'sign':'FF1E07242CE2C86A','username':'trainone','RePay':'true','paymode':'1','postdata':'https://mapi.alipay.com/gateway.do?body=sdfasdfsdf&subject=ceshiorder0tt4389wsd&notify_url=http%3A%2F%2Fwww.baidu.com%2Fcn_interface%2FAlipayNotifyHandle&out_trade_no=ceshiorder0tt438920150303125039&credit_card_pay=N&_input_charset=UTF-8&extra_common_param=ceshiorder0tt4389Fg&total_fee=0.1&credit_card_default_display=N&service=create_direct_pay_by_user&paymethod=directPay&partner=2088701454373226&seller_email=hyccservice%40126.com&payment_type=1&sign=d2b625062606744158cee98eaed8c225&sign_type=MD5','servid':'1','paytype':'1','ordernum':'T1510303070145873928117'}";
        String strte = "{'ordernum':'T1502081352498402496','cmd':'seachliushuihao','paytype':'1'}";
        m.put("data", strte);
        String str = posthttpclientdata("http://localhost:9013/ccs_paymodule/unionpay", m, 100000l);
        System.out.println("asdfasdf:" + str);
    }
}
