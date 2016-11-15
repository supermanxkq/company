package com.ccservice.b2b2c.atom.servlet.tuniu.thread;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TuNiuCancelChangeCallBackThread extends Thread {
    
    private String account;

    private String vendorOrderId;

    private String key;

    private int returnCode;

    private String errorMsg;

    private String orderId;

    private String changeId;

    private String callBackUrl;

    private TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    private String logName = "tuniu_3.18.1_途牛取消改签回调";

    public TuNiuCancelChangeCallBackThread(String account, String vendorOrderId, String key, int returnCode,
            String errorMsg, String orderId, String changeId, String callBackUrl) {
        this.account = account;
        this.vendorOrderId = vendorOrderId;
        this.key = key;
        this.returnCode = returnCode;
        this.errorMsg = errorMsg;
        this.orderId = orderId;
        this.changeId = changeId;
        this.callBackUrl = callBackUrl;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e) {
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("account", account);
            jsonObject.put("timestamp", tuNiuServletUtil.getCurrTime());
            jsonObject.put("returnCode", returnCode);
            jsonObject.put("errorMsg", errorMsg);
            JSONObject object = new JSONObject();
            object.put("vendorOrderId", vendorOrderId);
            object.put("orderId", orderId);
            object.put("changeId", changeId);
            jsonObject.put("data", TuNiuDesUtil.encrypt(object.toString()));
            jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), key));
            String result = SendPostandGet.submitPostTimeOut(callBackUrl, jsonObject.toString(), "UTF-8", 30000)
                    .toString();
            WriteLog.write(logName, vendorOrderId + "--->" + result);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "_Exception", e, vendorOrderId);
        }
    }

}
