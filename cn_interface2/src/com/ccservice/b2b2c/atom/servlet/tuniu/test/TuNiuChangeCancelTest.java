package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;

/**
 * 途牛取消改签测试
 */
public class TuNiuChangeCancelTest {
    public String account = "tuniu_basetest";

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    JSONObject jsonObject = new JSONObject();

    public static void main(String[] args) {
        TuNiuChangeCancelTest test = new TuNiuChangeCancelTest();
        test.quxiaogaiqian();
    }

    public String quxiaogaiqian() {
        String resultString = "";

        jsonObject.put("account", account);
        executepool1("{途牛URL}/train /cancelOrderFeedback", "6b59792b-8fb2-4d37-9828-6248f89ea9ea",
                "7d4bff90-a267-4b09-85ba-e84b727b73aa", "T1605121847195807634", "2015-08-03 00:00:00");
        jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), key));
        resultString = SendPostandGet.submitPost(
                "http://120.26.83.131:9022/cn_interface/TuNiuTraintrainAccountChangeCancelServlet",
                jsonObject.toString(), "UTF-8").toString();
        System.out.println(resultString);
        return resultString;
    }

    private void executepool1(String callBackUrl, String changeId, String orderId, String vendorOrderId,
            String timestamp) {
        JSONObject data = new JSONObject();

        data.put("callBackUrl", callBackUrl);
        data.put("changeId", changeId);
        data.put("orderId", orderId);
        data.put("vendorOrderId", vendorOrderId);
        String satas = data.toString();
        //        try {
        //            satas = TuNiuDesUtil.encrypt(satas);
        //        }
        //        catch (Exception e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        jsonObject.put("data", satas);
        jsonObject.put("timestamp", timestamp);
    }

}
