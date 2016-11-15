package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;

public class TuNiuQuXiaoTest {

    public static void main(String[] args) {
        //        String jsonString = "";
        //        SendPostandGet.submitGet2("http://120.26.83.131:9022/cn_interface/TuNiuQuXiaoCallBackTestServlet", jsonString, "UTF-8");

        TuNiuQuXiaoTest test = new TuNiuQuXiaoTest();
        System.out.println(test.tuniuquxiao());
    }

    public String account = "tuniu_basetest";

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    public String tuniuquxiao() {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        jsonObject.put("account", account);
        jsonObject.put("timestamp", "2016-05-10 12:55:52");
        json.put("vendorOrderId", "T1605171015114676882");
        json.put("orderId", "350f4f02-6339-489e-a466-88c98b15a5f5");
        json.put("callBackUrl", "http://120.26.83.131:9022/cn_interface/WebContent/tuniutestcallback.jsp");
        try {
            jsonObject.put("data", TuNiuDesUtil.encrypt(json.toString()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), key));
        String urlString = "http://120.26.83.131:9022/cn_interface/train/cancel";
        result = SendPostandGet.submitPost(urlString, jsonObject.toString(), "UTF-8").toString();
        return result;
    }

}
