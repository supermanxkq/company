package com.ccservice.b2b2c.atom.pay.handle;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

public class TrainOrderInterfaceMethod {
    public static void main(String[] args) {
        TrainOrderInterfaceMethod.train_confirm("T1604043A07B3E70DAB304D560B7720C0055E8B6A88",
                "T1604042730AB91083F604EBB0AAE90B96F8E46BC6A");//提交到接口确认出票
    }

    /**
     * 
     * 
     * @param orderid 使用放订单号
     * @param transactionid 交易单号
     * @return
     * @time 2015年10月20日 上午11:32:08
     * @author lubing
     */
    public static String train_confirm(String orderid, String transactionid) {
        String confirm_result = "";
        confirm_result = train_confirm_mothod(orderid, transactionid);
        return confirm_result;
    }

    /**
     * 
     * @param orderid
     * @param transactionid
     * @return
     * @time 2016年4月4日 上午10:23:38
     * @author chendong
     */
    private static String train_confirm_mothod(String orderid, String transactionid) {
        String partnerid = PropertyUtil.getValue("partnerid", "Train.b2b.properties");// "tianqutest";
        String key = PropertyUtil.getValue("partnerid_key", "Train.b2b.properties");
        String trainorderurl = PropertyUtil.getValue("trainorderurl", "Train.b2b.properties");
        String method = "train_confirm";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        //        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
        //                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
        //                + "\"}";
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("partnerid", partnerid);
        jsonStr.put("method", method);
        jsonStr.put("reqtime", reqtime);
        jsonStr.put("sign", sign);
        jsonStr.put("orderid", orderid);
        jsonStr.put("transactionid", transactionid);
        //        System.out.println("jsonStr=" + jsonStr.toJSONString());
        String resultString = "";
        String paramContent = "jsonStr=" + jsonStr.toJSONString();
        //        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        //        System.out.println("resultString=" + resultString);
        Long l1 = System.currentTimeMillis();
        WriteLog.write("TrainOrderInterfaceMethod_train_confirm", l1 + ":" + orderid + ":" + paramContent + "-"
                + trainorderurl);
        resultString = SendPostandGet.submitPost(trainorderurl, paramContent, "UTF-8").toString();
        WriteLog.write("TrainOrderInterfaceMethod_train_confirm", l1 + ":" + resultString);
        return resultString;
    }

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     * 
     * @time 2014年12月12日 下午2:44:31
     * @author chendong
     */
    public static String getsign(String partnerid, String method, String reqtime, String key) {
        System.out.println("=========key加密前=========");
        System.out.println(key);
        System.out.println("=========key加密后=========");
        key = MD5Util.MD5Encode(key, "UTF-8");
        System.out.println(key);
        String jiamiqian = partnerid + method + reqtime + key;
        System.out.println("=========sign加密前=========");
        System.out.println(jiamiqian);
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
        System.out.println("=========sign加密后=========");
        System.out.println(sign);
        return sign;
    }
}
