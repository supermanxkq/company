package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;

/**
 * 途牛确认改签测试
 */
public class TuNiuChangeConfirmTest {
    public String account = "tuniu_basetest";

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    JSONObject jsonObject = new JSONObject();

    public static void main1(String[] args) {
        System.out
                .println(SignUtil
                        .generateSign(
                                "{\"timestamp\":\"2016-05-09 11:28:29\",\"sign\":\"\",\"data\":{\"vendorOrderId\":\"T1605121328568409487\",\"changeId\":\"da718563-c889-42c8-97b1-434489d713a1\",\"orderId\":\"eb3ca157-4711-41ba-9f19-1980594d673d\",\"callBackUrl\":\"{tuniuURL}/train /confirmOrderFeedback\"},\"account\":\"tuniu_basetest\"}",
                                "ix7xk7exkt4c7nd2u62254n51k2vnuzm"));
    }

    public static void main(String[] args) {
        TuNiuChangeConfirmTest test = new TuNiuChangeConfirmTest();
        test.querengaiqian();
    }

    public String querengaiqian() {
        String resultString = "";
        jsonObject.put("account", account);

        executepool2("T1605121847195807634", "7d4bff90-a267-4b09-85ba-e84b727b73aa",
                "6b59792b-8fb2-4d37-9828-6248f89ea9ea", "{tuniuURL}/train /confirmOrderFeedback", "2016-05-09 11:28:29");
        System.out.println(jsonObject.toString());
        System.out.println(key);
        jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), key));
        resultString = SendPostandGet.submitGet2(
                "http://120.26.83.131:9022/cn_interface/TuNiuTraintrainAccountChangeConfirmServlet",
                jsonObject.toString(), "UTF-8");
        System.out.println(resultString);
        return resultString;

    }

    private void executepool2(String vendorOrderId, String orderId, String changeId, String callBackUrl,
            String timestamp) {
        JSONObject data = new JSONObject();
        data.put("vendorOrderId", vendorOrderId);
        data.put("orderId", orderId);
        data.put("changeId", changeId);
        data.put("callBackUrl", callBackUrl);
        String satas = data.toString();
        try {
            satas = TuNiuDesUtil.encrypt(satas);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        jsonObject.put("data", satas);
        jsonObject.put("timestamp", timestamp);
    }
}
