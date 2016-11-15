package com.ccservice.b2b2c.atom.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;

public class testRefundTicket {

    public static void main(String[] args) {
        test();
    }

    public static void test() {

        JSONArray returntickets = new JSONArray();
        JSONObject o1 = new JSONObject();
        o1.put("ticket_no", "E309482686");
        returntickets.add(o1);

        JSONObject obj = new JSONObject();
        obj.put("returntype", "1");
        obj.put("apiorderid", "930454589406");
        obj.put("sign", "");
        obj.put("trainorderid", "T1608221019117634325");
        obj.put("reqtoken", "");
        obj.put("returntickets", returntickets);
        obj.put("returnstate", "true");
        obj.put("returnmoney", "3");
        String ret = SendPostandGet.submitPost(
                "http://121.40.226.72:51126/cn_interface/RefundTicketMoneyToAlipayServlet", "data=" + obj.toString(),
                "utf-8").toString();
        System.out.println(ret);
    }
}
