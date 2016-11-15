package com.ccservice.b2b2c.atom.servlet.tuniu;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.util.DesUtil;

public class TuniuTest {
    public static void main(String[] args) {
        String pid = "tuniulvyou_bespeak";
        String key = "v66r9ogtcvtxv3v4xq3gog8fqdbhwmt0";
        JSONArray passengers = new JSONArray();
        JSONObject passenger = new JSONObject();
        passenger.put("passengerId", "1");
        passenger.put("ticketNo", "");
        passenger.put("passengerName", "王成亮");
        passenger.put("passportNo", "413026199310010970");
        passenger.put("passportTypeId", "1");
        passenger.put("passportTypeName", "二代身份证");
        passenger.put("piaoType", "3");
        passenger.put("piaoTypeName", "学生票");
        passenger.put("zwCode", "1");
        passenger.put("zwName", "硬座");
        passenger.put("cxin", "");
        passenger.put("price", "6");
        passenger.put("reason", "");
        passenger.put("provinceName", "");
        passenger.put("provinceCode", "11");
        passenger.put("schoolCode", "10001");
        passenger.put("schoolName", "北京大学");
        passenger.put("studentNo", "123456");
        passenger.put("schoolSystem", "1");
        passenger.put("enterYear", "2015");
        passenger.put("preferenceFromStationName", "");
        passenger.put("preferenceFromStationCode", "0357");
        passenger.put("preferenceToStationName", "");
        passenger.put("preferenceToStationCode", "0712");
        passengers.add(passenger);

        //        JSONObject passenger2 = new JSONObject();
        //        passenger2.put("passengerId", "2");
        //        passenger2.put("ticketNo", "");
        //        passenger2.put("passengerName", "王成亮");
        //        passenger2.put("passportNo", "413026199310010970");
        //        passenger2.put("passportTypeId", "1");
        //        passenger2.put("passportTypeName", "二代身份证");
        //        passenger2.put("piaoType", "2");
        //        passenger2.put("piaoTypeName", "儿童票");
        //        passenger2.put("zwCode", "1");
        //        passenger2.put("zwName", "硬座");
        //        passenger2.put("cxin", "");
        //        passenger2.put("price", "6");
        //        passenger2.put("reason", "");
        //        passengers.add(passenger2);
        JSONObject data = new JSONObject();
        String dataStr = "";
        String orderid = "C_" + System.currentTimeMillis();
        data.put("orderId", orderid);
        data.put("cheCi", "2672");
        data.put("fromStationCode", "TYV");
        data.put("fromStationName", "太原");
        data.put("toStationCode", "TDV");
        data.put("toStationName", "太原东");
        data.put("trainDate", "2016-03-01");
        data.put("callBackUrl", "http://124.254.60.66:6666/HuiDiao/ZhanZuo");
        data.put("contact", "");
        data.put("phone", "");
        data.put("trainAccount", "");
        data.put("pass", "");
        data.put("insureCode", "");
        data.put("passengers", passengers);
        try {
            //            dataStr = new String(Base64.encodeBase64URLSafeString(DesUtil.encrypt(data.toString().getBytes("UTF-8"),
            //                    key.getBytes("UTF-8"))));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.put("account", pid);
        json.put("sign", "");
        json.put("timestamp", "2015-02-15 16:00:00");
        json.put("data", dataStr);
        String sign = SignUtil.generateSign(json.toString(), key);
        json.put("sign", sign);
        String result = SendPostandGet.submitPost("http://bespeak.kt.hangtian123.net/cn_interface/trainAccount/grab",
                json.toJSONString(), "UTF-8").toString();
        System.out.println(result);

    }
}
