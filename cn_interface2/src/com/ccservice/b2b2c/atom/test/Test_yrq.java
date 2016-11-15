package com.ccservice.b2b2c.atom.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.service12306.Create12306OrderService;
import com.ccservice.b2b2c.atom.service12306.bean.TrainOrderReturnBean;
import com.ccservice.b2b2c.atom.servlet.account.method.TrainAccountOperateMethod;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;
import com.ccservice.b2b2c.atom.servlet.yilong.test;
import com.ccservice.b2b2c.base.customeruser.Customeruser;

public class Test_yrq {
    static String repUrl = "http://localhost:8888/Reptile/traininit";

    public static void main(String[] args) {
        //        new Test_yrq().rep12Method("yrq152", "yrq101010");
        //        new Test_yrq().check110();
        new Test_yrq().check13();
    }

    /**
     * 全自动登录,返回cookie
     */
    public static String rep12Method(String logname, String logpassword) {
        String paramContent = "";
        paramContent = "logname=" + logname + "&logpassword=" + logpassword + "&datatypeflag=12";
        String resultString = "";
        System.out.println(paramContent);
        resultString = SendPostandGet.submitPost(repUrl, paramContent, "utf-8").toString();
        System.out.println(resultString);
        return resultString;
    }

    /**
     * 测试下单过程中添加常旅     datatypeflag=13   Create12306OrderService类
     */
    public void check13() {
        Customeruser user = new Customeruser();
        user.setCardnunber("JSESSIONID=25BF17E0BC79E18EF5A0B0F322CA76FD; BIGipServerotn=1691943178.24610.0000; current_captcha_type=Z");
        user.setLoginname("yrq152");
        user.setLogpassword("yrq101010");
        //        String name = "卓寅";
        //        String idcode = "G24162635";
        //        String oldPassengerStr = "" + name + ",B," + idcode + ",1_";
        //        String passengerTicketStr = "1,0,1," + name + ",B," + idcode + ",15142541245,N";
        //        String passinfo = "{\"oldPassengerStr\":\"" + oldPassengerStr + "\",\"prices\":\"" + "21.5"
        //                + "\",\"zwcodes\":\"" + "1" + "\",\"passengerTicketStr\":\"" + passengerTicketStr + "\"}";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ticket_type", "1");
        jsonObject.put("price", 21.5);
        jsonObject.put("zwcode", "1");
        jsonObject.put("passenger_id_type_code", "B");
        jsonObject.put("passenger_name", "卓寅");
        jsonObject.put("passenger_id_no", "G24162635");
        jsonArray.add(jsonObject);
        TrainOrderReturnBean Bean = new Create12306OrderService().operate(0, "2016-07-30", "DHD", "SBT", "大虎山", "沈阳北",
                "K1055", jsonArray.toString(), user);
        System.out.println("=============>>>>>||" + JSONArray.toJSONString(Bean));

    }

    /**
     * 测试添加常旅接口       datatypeflag=110   TrainAccountOperateMethod类
     */
    public void check110() {
        String reqtime = getreqtime();
        String url = "http://localhost:9007/cn_interface/trainAccount/contact/saveOrUpdate";
        JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject json_1 = new JSONObject();
        jsonObject.put("account", "tuniulvyou");
        jsonObject.put("timestamp", getreqtime());
        json.put("trainAccount", "yrq152");
        json.put("pass", "yrq101010");
        json_1.put("id", 0);
        json_1.put("name", "卓寅");
        json_1.put("sex", 0);
        json_1.put("birthday", "");
        json_1.put("country", "US");
        json_1.put("identyType", "B");
        json_1.put("identy", "G24162635");
        json_1.put("personType", 0);
        json_1.put("phone", "");
        json_1.put("tel", "");
        json_1.put("email", "");
        json_1.put("address", "");
        jsonArray.add(json_1);
        json.put("contacts", jsonArray);
        String jsonString = null;
        try {
            jsonString = TuNiuDesUtil.encrypt(json.toString());
            jsonObject.put("data", jsonString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), "v66r9ogtcvtxv3v4xq3gog8fqdbhwmt0"));
        String resultString = SendPostandGet.submitPost(url, jsonObject.toString(), "UTF-8").toString();
        System.out.println("========>" + resultString);
    }

    public static String getreqtime() {
        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssSSS");
        return yyyyMMddHHmmss.format(new Date());
    }
}
