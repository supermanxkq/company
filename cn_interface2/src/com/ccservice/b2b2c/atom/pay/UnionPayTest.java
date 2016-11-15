package com.ccservice.b2b2c.atom.pay;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.MQ.HttpsClientUtils;

public class UnionPayTest {

    public static void main(String[] args) {
        String url = "http://124.254.60.66:8082/ccs_paymodule/unionpay";
        String payurl = "https://unionpaysecure.com/api/Pay.action?version=1.0.0&charset=UTF-8&merId=104110548991069&acqCode=&merCode=&merAbbr=%E5%8C%97%E4%BA%AC%E5%AE%A2%E8%BF%90%E4%BF%A1%E6%81%AF%E7%BD%91&transType=01&commodityUrl=http%3A%2F%2Fwww.e2go.com.cn%2FTicketOrder%2FPaySuccess%2F698654&commodityName=%E8%BD%A6%E7%A5%A8&commodityUnitPrice=2300&commodityQuantity=1&orderNumber=150925194015010805056&orderAmount=2300&orderCurrency=156&orderTime=20150925194015&customerIp=124.254.60.69&frontEndUrl=http%3A%2F%2Fwww.e2go.com.cn%2FUnionPay%2FFrontCallback%2F698654&backEndUrl=http%3A%2F%2Fwww.e2go.com.cn%2FUnionPay%2FBackCallback%2F698654&signature=c8e5dd80f79c16622e59643b41bee11d&origQid=&commodityDiscount=&transferFee=&customerName=&defaultPayType=&defaultBankNumber=&transTimeout=&merReserved=%7BorderTimeoutDate%3D20150925200513%7D&signMethod=MD5";
        System.out.println(payurl);
        uniontest("tes1111111t024", payurl, 0, url);
    }

    /**
     * 银联测试
     * @return
     */
    public static String uniontest(String ordernumber, String payurl, int times, String url) {
        JSONObject jso = new JSONObject();
        jso.put("username", "trainone");
        jso.put("sign", "FF1E07242CE2C86A");
        jso.put("paymode", "1");
        jso.put("servid", "1");
        jso.put("ordernum", ordernumber);
        jso.put("paytype", "2");
        try {
            jso.put("postdata", payurl);
            try {
                if (times > 0) {
                    jso.put("RePay", "true");
                    WriteLog.write("12306银联自动支付", ":" + times + "次支付:" + jso.getString("RePay"));
                }
            }
            catch (Exception e1) {
            }
            WriteLog.write("12306银联自动支付", "获取银联支付链接:" + jso.getString("postdata"));
        }
        catch (Exception e2) {
            WriteLog.write("12306银联自动支付", "获取银联支付链接异常" + e2.getMessage());
            e2.printStackTrace();
        }
        Map listdata = new HashMap();
        listdata.put("data", jso.toString());
        WriteLog.write("12306银联自动支付", "订单号：" + ordernumber);
        String result = HttpsClientUtils.posthttpclientdata(url, listdata, 70000l);
        WriteLog.write("12306银联自动支付", "支付结果：" + result);
        System.out.println(result);
        return "";
    }
}
