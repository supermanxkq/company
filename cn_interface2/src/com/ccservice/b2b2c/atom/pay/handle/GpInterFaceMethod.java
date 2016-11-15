package com.ccservice.b2b2c.atom.pay.handle;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

/**
 * 支付成功接口处理方法
 * @author wzc
 *
 */
public class GpInterFaceMethod {
    /**
     * 支付成功后发送支付信息
     * @param orderid 订单ID
     * @param OrderNumber 机票订单号
     * @param tradeno 支付宝交易号
     */
    public static void sendDayShouPayInfo(long orderid, String OrderNumber, String tradeno, String payprice) {
        String partnerid = PropertyUtil.getValue("partnerid", "GpAir.properties");
        String key = PropertyUtil.getValue("partnerid_key", "GpAir.properties");
        String Airorderurl = PropertyUtil.getValue("airpayurl", "GpAir.properties");
        String method = "zhifu_callback";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("partnerid", partnerid);
        jsonStr.put("method", method);
        jsonStr.put("reqtime", reqtime);
        jsonStr.put("sign", sign);
        jsonStr.put("orderid", orderid);//平台订单ID
        jsonStr.put("ordernumber", OrderNumber);//平台订单号
        jsonStr.put("payresult", "s");//支付成功
        jsonStr.put("Tradeno", tradeno);//块钱支付交易号
        jsonStr.put("payprice", payprice);//块钱支付金额
        jsonStr.put("DaiShouFlag", true);//代收款标识
        String resultString = "";
        String paramContent = "jsonStr=" + jsonStr.toJSONString();
        Long l1 = System.currentTimeMillis();
        WriteLog.write("GpInterFaceMethod", l1 + ":" + orderid + ":" + paramContent + "-" + Airorderurl);
        resultString = SendPostandGet.submitPost(Airorderurl, paramContent, "UTF-8").toString();
        WriteLog.write("GpInterFaceMethod", l1 + ":" + resultString);
    }

    /**
     * 支付成功后发送支付信息
     * @param orderid 订单ID
     * @param OrderNumber 机票订单号
     * @param tradeno 支付宝交易号
     */
    public static void sendPayInfo(long orderid, String OrderNumber, String tradeno) {
        String partnerid = PropertyUtil.getValue("partnerid", "GpAir.properties");
        String key = PropertyUtil.getValue("partnerid_key", "GpAir.properties");
        String Airorderurl = PropertyUtil.getValue("airpayurl", "GpAir.properties");
        String method = "zhifu_callback";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("partnerid", partnerid);
        jsonStr.put("method", method);
        jsonStr.put("reqtime", reqtime);
        jsonStr.put("sign", sign);
        jsonStr.put("orderid", orderid);//平台订单ID
        jsonStr.put("ordernumber", OrderNumber);//平台订单号
        jsonStr.put("payresult", "s");//支付成功
        jsonStr.put("Tradeno", tradeno);//块钱支付交易号
        String resultString = "";
        String paramContent = "jsonStr=" + jsonStr.toJSONString();
        Long l1 = System.currentTimeMillis();
        WriteLog.write("GpInterFaceMethod", l1 + ":" + orderid + ":" + paramContent + "-" + Airorderurl);
        resultString = SendPostandGet.submitPost(Airorderurl, paramContent, "UTF-8").toString();
        WriteLog.write("GpInterFaceMethod", l1 + ":" + resultString);
    }

    public static String getreqtime() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     */
    public static String getsign(String partnerid, String method, String reqtime, String key) {
        key = MD5Util.MD5Encode(key, "UTF-8");
        String jiamiqian = partnerid + method + reqtime + key;
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
        return sign;
    }
}
