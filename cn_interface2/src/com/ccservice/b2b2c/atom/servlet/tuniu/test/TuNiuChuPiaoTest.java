package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;

public class TuNiuChuPiaoTest {
    public static void main(String[] args) {
        //
        //        String jsonString = "{\"sign\":\"cc24b76aa4abf439b7ac03ee111d6a34\",\"timestamp\":\"20160506131814\",\"returnCode\":100,\"data\":{\"vendorOrderId\":\"T1605061150268108245\",\"orderId\":\"25308\"},\"account\":\"\",\"errorMsg\":\"支付成功\"}";
        //        SendPostandGet.submitGet2("http://localhost:8080/cn_interface/TuNiuChuPiaoCallBackTestServlet", jsonString,
        //                "UTF-8");

        try {
            TuNiuChuPiaoTest test = new TuNiuChuPiaoTest();
            System.out.println(test.chupiao());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    /**
     * 途牛出票测试
     */
    JSONObject json = new JSONObject();

    private String chupiao() {

        json.put("account", "tuniu_basetest");
        executepool("T1605171024180879859", "93e4ae77-1a0e-43b2-b5a8-ab14ddf794c6",
                "{途牛URL}/train /confirmOrderFeedback", "2016-05-10 11:38:48");
        json.put("sign", SignUtil.generateSign(json.toString(), key));
        System.out.println(json.toString());
        String resultString = SendPostandGet.submitGet2("http://120.26.83.131:9022/cn_interface/train/confirm",
                json.toString(), "UTF-8");
        return resultString;
    }

    private void executepool(String vendorOrderId, String orderId, String callBackUrl, String timestamp) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("vendorOrderId", vendorOrderId);
        jsonObject.put("orderId", orderId);
        jsonObject.put("callBackUrl", callBackUrl);
        json.put("data", jsonObject);

        json.put("timestamp", timestamp);
    }

    /**
     * 异步出票测试
     */

    public void yibucanshu() {
        JSONObject json_yibu = new JSONObject();
        json_yibu.put("account", "testAccount");
        json_yibu.put("timestamp", "2015-08-03 00:00:00");
        json_yibu.put("returnCode", 231000);
        json_yibu.put("errorMsg", "");
        JSONObject data = new JSONObject();
        json.put("vendorOrderId", "testVendorOrderId");
        json.put("orderId", "AEME3TJS1H00");
        json_yibu.put("data", data);
        chupiaohuidao(json_yibu.toString());
    }

    /**
     *  
     * 途牛出票回调测试
     */
    public String chupiaohuidao(String param) {
        String resultString = "";
        if (param != null && !"".equals(param)) {
            JSONObject json = new JSONObject();
            json.put("trainorderid", 3595);
            json.put("method", "train_order_callback");
            json.put("returnmsg", "");
            json.put("sign", SignUtil.generateSign(json.toString(), key));
            resultString = SendPostandGet.submitGet2("http://localhost:8088/cn_interface/tcTrainCallBack",
                    json.toString(), "UTF-8");
        }
        return resultString;
    }
}
