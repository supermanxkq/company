package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;

public class TuNiuTuiPiaoTest {
    public static void main(String[] args) {
        //        String jsonString = "{\"timestamp\":\"1462587925\",\"sign\":\"a2262ef5256f45396077d5b670f85684\",\"returntickets\":[{\"returnsuccess\":false,\"returnmoney\":\"\",\"returnfailmsg\":\"%E5%B7%B2%E9%80%80%E7%A5%A8\"}],\"token\":\"1462587925251\",\"returnmoney\":\"\",\"vendorOrderId\":\"\",\"orderNumber\":\"\",\"returnmsg\":\"\",\"orderId\":\"\",\"returnstate\":true,\"returntype\":\"1\"}";
        //        SendPostandGet.submitGet2("http://localhost:8080/cn_interface/TuNiuTuiPiaoCallBackTestServlet", jsonString,
        //                "UTF-8");
        TuNiuTuiPiaoTest test = new TuNiuTuiPiaoTest();

        System.out.println(test.tuipiao());
        ;
    }

    public String account = "tuniu_basetest";

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    /**
     * 途牛 退票测试
     */
    JSONObject json = new JSONObject();

    private String tuipiao() {

        json.put("account", account);

        String result = "";
        //        executepool("E0104002221053148", "李四", "1", "341222197608254405", "123", "testVendorOrderId1",
        //                "T1603281113439877482", "123456", "{途牛URL}/train/returnTicketFeedback", "2015-08-03 00:00:00");
        executepool("E8360050601150022", "杨荣强", "1", "140622199210264251", "31404", "T1605171024180879859",
                "93e4ae77-1a0e-43b2-b5a8-ab14ddf794c6", "E836005060", "{tuniuURL}/train/returnTicketFeedback",
                "2016-05-09 19:52:13");

        json.put("sign", SignUtil.generateSign(json.toString(), key));
        //        result = SendPostandGet.submitPost(
        //                "http://120.26.83.131:9022/cn_interface/TuNiuTraintrainAccountReturnServlet", json.toString(), "UTF-8")
        //                .toString();
        //        result = SendPostandGet.submitPost("http://120.26.83.131:9022/cn_interface/TuNiuTraintrainAccountReturnServlet", json.toString(), "UTF-8").toString();
        result = SendPostandGet.submitGet2("http://120.26.83.131:9022/cn_interface/train/return", json.toString(),
                "UTF-8");
        return result;
    }

    private void executepool(String ticketNo, String passengerName, String passportTypeId, String passportNo,
            String refundId, String vendorOrderId, String orderId, String orderNumber, String callBackUrl,
            String timestamp) {
        JSONObject tickets = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        tickets.put("ticketNo", ticketNo);
        tickets.put("passengerName", passengerName);
        tickets.put("passportTypeId", passportTypeId);
        tickets.put("passportNo", passportNo);
        jsonArray.add(tickets);
        data.put("tickets", jsonArray);
        data.put("refundId", refundId);
        data.put("vendorOrderId", vendorOrderId);
        data.put("orderId", orderId);
        data.put("orderNumber", orderNumber);
        data.put("callBackUrl", callBackUrl);
        try {
            String datas = TuNiuDesUtil.encrypt(data.toString());
            json.put("data", datas);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        json.put("timestamp", timestamp);
    }

    private void executepool_yibu(String ticketNo, String passengerName, int passportTypeId, String passportNo,
            String returnSuccess, String returnFailId, String returnTime, String returnMoney, String refundId,
            String vendorOrderId, String orderNumber, Boolean returnState, String returnMsg, String timestamp,
            int returnCode, String errorMsg) {
        JSONObject tickets = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        tickets.put("ticketNo", ticketNo);
        tickets.put("passengerName", passengerName);
        tickets.put("passportTypeId", passportTypeId);
        tickets.put("passportNo", passportNo);
        tickets.put("returnSuccess", returnSuccess);
        tickets.put("returnFailId", returnFailId);
        tickets.put("returnTime", returnTime);
        tickets.put("returnMoney", returnMoney);
        jsonArray.add(tickets);
        data.put("refundId", refundId);
        data.put("vendorOrderId", vendorOrderId);
        data.put("orderNumber", orderNumber);
        data.put("returnTickets", jsonArray);
        data.put("returnState", returnState);
        data.put("returnMoney", returnMoney);
        data.put("returnMsg", returnMsg);

        try {
            String datas = TuNiuDesUtil.encrypt(data.toString());
            json_yibu.put("data", datas);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        json_yibu.put("timestamp", timestamp);
        json_yibu.put("returnCode", returnCode);
        json_yibu.put("errorMsg", errorMsg);
    }

    /**
     * 途牛退票测试
     */
    JSONObject json_yibu = new JSONObject();

    public void yibuchucan() {
        json_yibu.put("account", "testAccount");
        TuNiuTuiPiaoTest test = new TuNiuTuiPiaoTest();
        test.executepool_yibu("E0104002221053148", "张三", 1, "341222197608254405", "True", "0", "2015-08-29 10:05:54",
                "122.00", "123", "testVendorOrderId", "E010400222", true, "", "2015-08-03 00:00:00", 231000, "");

        tuipiaohuidiao(json_yibu.toString());
    }

    /**
     * 途牛退票回调测试
     */
    public String tuipiaohuidiao(String param) {
        String result = "";
        if (param != null & !"".equals(param)) {
            JSONObject json = new JSONObject();
            json.put("trainorderid", 3595);
            json.put("method", "train_order_callback");
            json.put("returnmsg", "");
            json.put("sign", SignUtil.generateSign(json.toString(), key));
            result = SendPostandGet.submitPost("http://localhost:8080/cn_interface/tcTrainCallBack", json.toString(),
                    "UTF-8").toString();
        }
        return result;
    }

}
