package com.ccservice.b2b2c.atom.pay.test;

import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;

public class GpGetUrlTets {
    public static void main(String[] args) {
        //        getUrl();
        //        confimOrder();
        //        refuseOrder();
        //        signStr();
        //        getDaiKouUrl();
        //        getWapUrl();
        //        getChangeUrl();
        getNoGpWapUrl();// 获取非公务票wap支付链接
        //        PosCreateOrder();//Pos机创建订单
        //        getGpWapChangeAlipay();//获取支付宝改签wap
        //        getWapChangePay();//获取快钱改签支付链接
    }

    /**
     * Pos机创建订单
     */
    public static void PosCreateOrder() {
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("orderNumber", "YDXA201606281114364");//保留两位小数
        obj.put("cmd", "YeePosCreateWithHoldMoney");//商品名称
        obj.put("Money", "1705");
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    public static void signStr() {
        //        String postUrl = "http://localhost:9004/cn_interface/PayUtilHttp";
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("cmd", "signTest");//商品名称
        obj.put("GoodDesc", "机票款");//商品名称
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    public static void refuseOrder() {
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("cmd", "RefuseOrder");//商品名称
        obj.put("orderid", "1657");
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    public static void confimOrder() {
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("OrderNumber", "YDXA201605262130106");//保留两位小数
        obj.put("cmd", "ConfirmOrder");//商品名称
        obj.put("orderid", "1705");
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * 获取支付链接
     */
    public static void getUrl() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        //        String postUrl = "http://localhost:9004/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "100");//保留两位小数
        obj.put("GoodDesc", "你现实删了是快递放假了可是大家啊");//商品名称
        //        obj.put("cmd", "PayTest");
        obj.put("cmd", "GetPayUrl");
        obj.put("orderid", "0");
        obj.put("orderNumber", "YDXA201606061842078");//订单号
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * 获取wap支付链接
     */
    public static void getWapUrl() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "900");//保留两位小数
        obj.put("GoodDesc", "商品名称");//商品名称
        obj.put("cmd", "Wappay");
        obj.put("orderid", "0");
        obj.put("orderNumber", "YDXA201606201641511");//订单号
        obj.put("bankCode", "628288");//GpBankCode 表的code 可不填
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * 获取非公务票wap支付链接
     */
    public static void getNoGpWapUrl() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "100");//保留两位小数
        obj.put("GoodDesc", "商品名称");//商品名称
        obj.put("cmd", "GpWapAlipay");
        obj.put("orderid", "0");
        obj.put("orderNumber", "YDXA201606201641511");//订单号
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * 获取支付链接
     */
    public static void getDaiKouUrl() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        //        String postUrl = "http://localhost:9004/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "100");//保留两位小数
        obj.put("GoodDesc", "你现实删了是快递放假了可是大家啊");//商品名称
        obj.put("cmd", "WithHoldMoneyUrl");
        obj.put("orderid", "");
        obj.put("orderNumber", "YDXA201606061842078");//订单号
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * 获取改签链接
     */
    public static void getChangeUrl() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "100");//保留两位小数
        obj.put("GoodDesc", "商品描述");//商品名称
        obj.put("cmd", "GetChangePayUrl");
        obj.put("orderid", "0");//Gp订单ID 
        obj.put("orderNumber", "TC16060810226");//Gp改签单号【与订单ID必填1个】
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * Gp非公务票改签获取支付链
     */
    public static void getGpWapChangeAlipay() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "100");//保留两位小数
        obj.put("GoodDesc", "商品描述");//商品名称
        obj.put("cmd", "GpWapChangeAlipay");
        obj.put("orderid", "0");//Gp订单ID 
        obj.put("orderNumber", "TC16060810226");//Gp改签单号【与订单ID必填1个】
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }

    /**
     * 获取快钱改签支付链接
     */
    public static void getWapChangePay() {
        Random r = new Random();
        String postUrl = "http://gppay.hangtian123.com/cn_interface/PayUtilHttp";
        JSONObject obj = new JSONObject();
        obj.put("Money", "100");//保留两位小数
        obj.put("GoodDesc", "商品描述");//商品名称
        obj.put("cmd", "WapChangePay");
        obj.put("orderid", "0");//Gp订单ID 
        obj.put("orderNumber", "TC16060810226");//Gp改签单号【与订单ID必填1个】
        String msg = SendPostandGet.submitPost(postUrl, "jsonStr=" + obj.toJSONString(), "UTF-8").toString();
        System.out.println(msg);
    }
}
