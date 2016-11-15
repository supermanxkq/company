package com.ccservice.b2b2c.atom.servlet.yilong;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;

public class tests {
    public static final String tongchengurl = "http://120.26.83.131:9022/cn_interface/tcTrainCallBack";

    public static void main(String[] args) {
        tests tests = new tests();
        tests.chupiao(25624, "N", "20160527261754068", "T1605271751457277639");

    }

    /**
     * 同程手工调用出票
     * 
     * @param trainorderid  订单ID
     * @param isSuccess Y：成功  N：失败
     * @param orderid   同城订单号
     * @param transactionid 咱们订单号
     * @time 2015年9月16日 下午4:48:34
     * @author fiend
     */
    private static void chupiao(long trainorderid, String isSuccess, String orderid, String transactionid) {
        String result = "false";
        String returnmsg = "true";
        try {
            returnmsg = URLEncoder.encode(returnmsg, "utf-8");
        }
        catch (Exception e) {
        }
        JSONObject jso = new JSONObject();
        jso.put("trainorderid", trainorderid);
        jso.put("method", "train_pay_callback");
        jso.put("orderid", orderid);
        jso.put("transactionid", transactionid);
        jso.put("isSuccess", isSuccess);
        jso.put("iskefu", "1");
        jso.put("agentid", "1217");
        try {
            result = SendPostandGet.submitPost(tongchengurl, jso.toString(), "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(trainorderid + "--->" + result);
        WriteLog.write("aaaaaa", trainorderid + "--->" + result);
    }

}
