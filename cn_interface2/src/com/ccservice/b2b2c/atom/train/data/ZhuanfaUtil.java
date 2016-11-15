package com.ccservice.b2b2c.atom.train.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;

public class ZhuanfaUtil {

    public static String zhongzhuanurl = "http://12306.hangtian123.com/";

    /**
     * https post方式请求服务器
     * 
     * @param url
     * @param content 参数a=1&b=2
     * @param charset  null|utf-8
     * @param cookiestring cookiestring
     * @param method post|get
     * @return
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     * @time 2014年12月17日 下午3:30:01
     * @author wangzhanchao
     */
    public static String zhuanfa(String url, String content, String charset, String cookiestring, String method,
            Integer outtime) {
        String urlstr = "";
        String contentstr = "";
        String cookiestr = "";
        try {
            urlstr = URLEncoder.encode(url, "utf-8");
            contentstr = URLEncoder.encode(content, "utf-8");
            cookiestr = URLEncoder.encode(cookiestring, "utf-8");
        }
        catch (Exception e) {

            //            e.printStackTrace();
        }
        if ("".equals(urlstr)) {
            return "-1";
        }
        String geturl = zhongzhuanurl + "?cmd=&url=" + urlstr + "&param=" + contentstr + "&method=" + method
                + "&cookie=" + cookiestr;
        //        String str = httpget(geturl, "utf-8", outtime);//老的由于超时，暂时弃用
        String str = gethttpclientdata(geturl, outtime.longValue());
        //                WriteLog.write("URL转发", geturl);
        if (str != null && str.contains("@@@@@")) {
            return str.split("@@@@@")[0];
        }
        else {
            return "-1";
        }
    }

    /**
     * 测试转发是否可用
     * 
     * @time 2015年1月14日 上午8:29:49
     * @author chendong
     */
    public static void testzhuanfaisenable() {
        String resultstring = "";
        String date = "2015-03-22";
        String url = "http://12306.hangtian123.com/?cmd=&url=https%3A%2F%2Fkyfw.12306.cn%2Fotn%2FleftTicket%2FqueryTicketPrice%3Ftrain_no%3D2400000D290G%26from_station_no%3D01%26to_station_no%3D11%26seat_types%3DOMO%26train_date%3D"
                + date + "&param=&method=get&cookie=";
        Long l1 = System.currentTimeMillis();
        String json1 = ZhuanfaUtil.zhuanfa(url, "", "", "", "get", 2000);
        String train_no = "240000K14708";
        String from_station_no = "01";
        String to_station_no = "14";
        String seat_types = "1413";
        String time = "2015-03-15";
        String strUrl = "http://localhost:9013/search12306data/search12306data?type=2&train_no=" + train_no
                + "&from_station_no=" + from_station_no + "&to_station_no=" + to_station_no + "&seat_types="
                + seat_types + "&time=" + time;
        System.out.println((System.currentTimeMillis() - l1) + ":" + strUrl);
        resultstring = SendPostandGet.submitGet(strUrl);
        System.out.println((System.currentTimeMillis() - l1) + ":" + resultstring);
    }

    public static void main(String[] args) {
        Long l1 = System.currentTimeMillis();
        //        String json = HttpUtils.Get_https("https://kyfw.12306.cn/otn/login/init", 20);//老方法
        //        String json = gethttpclientdata("https://kyfw.12306.cn/otn/login/init", 210L);
        //        String json = gethttpclientdata("http://localhost:9004/cn_home/login!login.action", 1100L);

        String search_url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2015-03-22&from_station=XAY&to_station=WMR";
        String json = ZhuanfaUtil.zhuanfa(search_url, "", "", "", "get", 1);//王战朝的转发的方法
        System.out.println("耗时:" + (System.currentTimeMillis() - l1) + ":" + json);
    }

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
        Long l1 = System.currentTimeMillis();
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
            //            e.printStackTrace();
        }
        catch (IOException e) {
            //            e.printStackTrace();
        }
        //        System.out.println("耗时1:" + (System.currentTimeMillis() - l1));
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
        Long l1 = System.currentTimeMillis();
        String json = "-1";
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
            //            e.printStackTrace();
        }
        catch (IOException e) {
            //            e.printStackTrace();
        }
        //        System.out.println("耗时1:" + (System.currentTimeMillis() - l1));
        return json;
    }

    public static byte[] httpget(String url, Integer outtime) {
        try {
            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            if (outtime <= 0) {
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
            }
            else {
                conn.setConnectTimeout(outtime);
                conn.setReadTimeout(outtime);
            }
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[2046];
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int len = 0;
            int size = 0;
            while ((len = in.read(buf)) > 0) {
                bout.write(buf, 0, len);
                size += len;
            }
            in.close();
            conn.disconnect();
            return bout.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpget(String url, String encode, Integer outtime) {
        String result = "-1";
        try {
            result = new String(httpget(url, outtime), encode);
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 
     * 
     * @param time
     * @param startcity
     * @param endcity
     * @param outtime
     * @param search_url  http://12306searchrep.hangtian123.net/Reptile/traininit?datatypeflag=103
     * @return
     * @time 2015年4月21日 下午4:53:55
     * @author chendong
     */
    public static String zhuanfa1(String time, String startcity, String endcity, long outtime, String rep_url) {
        String json = "-1";
        Long l1 = System.currentTimeMillis();
        //        rep_url += "http://12306searchrep.hangtian123.net:8080/Reptile/traininit?datatypeflag=103";
        rep_url += "http://121.43.155.236:8080/Reptile/traininit?datatypeflag=103";

        //        CCSPostMethod post = null;
        //        CCSHttpClient httpClient = new CCSHttpClient(false, outtime);
        //        post = new CCSPostMethod(rep_url);
        //        post.setFollowRedirects(false);
        //        NameValuePair NameValuePair1 = new NameValuePair("params", "{\"from_station\":\"" + startcity
        //                + "\",\"to_station\":\"" + endcity + "\",\"queryDate\":\"" + time + "\"}");
        //        NameValuePair[] names = { NameValuePair1 };
        //        post.setRequestBody(names);
        //        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            //            httpClient.executeMethod(post);
            //            json = post.getResponseBodyAsString();
            String p = "params={\"from_station\":\"" + startcity + "\",\"to_station\":\"" + endcity
                    + "\",\"queryDate\":\"" + time + "\"}";
            //            System.out.println(rep_url);
            //            System.out.println(p);
            json = SendPostandGet.submitPost(rep_url, p, "utf-8").toString();
            //            json = SendPostandGet.submitPostTimeOut(
            //                    rep_url,
            //                    "params={\"from_station\":\"" + startcity + "\",\"to_station\":\"" + endcity
            //                            + "\",\"queryDate\":\"" + time + "\"}", "utf-8", 5000).toString();
        }
        catch (Exception e) {
        }
        return json;

    }
}
