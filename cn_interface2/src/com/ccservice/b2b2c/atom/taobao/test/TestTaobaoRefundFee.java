package com.ccservice.b2b2c.atom.taobao.test;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;

public class TestTaobaoRefundFee {
    public static void main(String[] args) {
        try {
            String urlString = "http://192.168.0.12:29004/TestRefundFee.jsp";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("main_biz_order_id", "TC_20150311_WH_1426046110006");
            jsonObject.put("user_id", "123421");
            jsonObject.put("sub_biz_order_id", "TC_20150311_FIEND_1426046110006");
            jsonObject.put("extra", "111111");
            jsonObject.put("description", "autorefund-2015072221001004360075313925-1200-W2015082974636639");
            SendPostandGet.submitPost(urlString, "json=" + jsonObject.toString(), "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
