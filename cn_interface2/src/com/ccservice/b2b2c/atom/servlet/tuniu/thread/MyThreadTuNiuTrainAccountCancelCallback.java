package com.ccservice.b2b2c.atom.servlet.tuniu.thread;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MyThreadTuNiuTrainAccountCancelCallback extends Thread {
    
    private String account;

    private String vendorOrderId;

    private String key;

    private int returnCode;

    private String errorMsg;

    private TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    private String logName = "tuniu_3_4_6_2_途牛取消订单回调接口";

    public MyThreadTuNiuTrainAccountCancelCallback(String account, String vendorOrderId, String key, int returnCode,
            String errorMsg) {
        this.account = account;
        this.vendorOrderId = vendorOrderId;
        this.key = key;
        this.returnCode = returnCode;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e) {
        }
        try {
            String callbackUrl = callBackUrl(account);
            JSONObject dataJsonObject = new JSONObject();
            dataJsonObject.put("vendorOrderId", vendorOrderId);
            JSONObject callbackJsonObject = new JSONObject();
            callbackJsonObject.put("account", account);
            callbackJsonObject.put("timestamp", tuNiuServletUtil.getCurrTime());
            callbackJsonObject.put("returnCode", returnCode);
            callbackJsonObject.put("errorMsg", errorMsg);
            callbackJsonObject.put("data", TuNiuDesUtil.encrypt(dataJsonObject.toString()));
            callbackJsonObject.put("sign", SignUtil.generateSign(callbackJsonObject.toString(), key));
            WriteLog.write(logName, vendorOrderId + "--->" + callbackUrl + "?" + callbackJsonObject);
            String result = SendPostandGet
                    .submitPostTimeOut(callbackUrl, callbackJsonObject.toString(), "UTF-8", 30000).toString();
            WriteLog.write(logName, vendorOrderId + "--->" + result);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "_Exception", e, vendorOrderId);
        }
    }

    @SuppressWarnings("rawtypes")
    private String callBackUrl(String account) {
        Map map = getAccountInformation(account);
        return map.containsKey("CancelCallBackUrl") ? map.get("CancelCallBackUrl").toString() : "";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map getAccountInformation(String account) {
        try {
            return (Map) Server
                    .getInstance()
                    .getSystemService()
                    .findMapResultBySql(
                            "select CancelCallBackUrl from T_INTERFACEACCOUNT with (nolock) where C_USERNAME='"
                                    + account + "'", null).get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            Map map = new HashMap<String, Object>();
            map.put("CancelCallBackUrl", "");
            return map;
        }
    }
}
