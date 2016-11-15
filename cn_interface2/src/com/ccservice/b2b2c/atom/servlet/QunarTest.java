package com.ccservice.b2b2c.atom.servlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class QunarTest {

    public final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void main(String[] args) throws Exception {
        zhanzuohuidiao();

    }

    //占座
    public static void zhanzuo() throws Exception {

        String trainorderurl = "http://hcpzs.qicailvtu.com/cn_interface/qunarTrain";

        JSONObject json = new JSONObject();

        String orderNo = "hangt151514534621121";
        String reqFrom = "qunar";
        String reqTime = getreqtime();
        String trainNo = "K7042";
        String from = "哈尔滨";
        String to = "哈尔滨东";
        String date = "2015-08-02";
        String retUrl = "asgtrrh123";

        JSONObject jsons = new JSONObject();
        jsons.put("name", "罗庆鑫");//姓名
        jsons.put("certNo", "232103199703294376");//证件号码
        jsons.put("certType", "1");//证件类型
        jsons.put("certName", "二代身份证");//证件姓名 
        jsons.put("ticketType", "1");//票种
        jsons.put("ticketPrice", "8.0");//票价
        jsons.put("ticketName", "成人票");//票种名称
        jsons.put("seatCode", "1");//座位编码
        jsons.put("seatName", "硬座");//座位名称
        jsons.put("status", "0");//身份核验状态

        JSONArray array = new JSONArray();
        array.add(jsons);
        json.put("passengers", array);//乘客

        String key = "0C13D7C3566147EB90D1E273278DCDD9";
        String HMACflag = ElongHotelInterfaceUtil.MD5(
                key + orderNo + reqFrom + reqTime + trainNo + from + to + date + retUrl + array.toString())
                .toUpperCase();
        json.put("HMAC", HMACflag);//md5

        System.out.println(json.toString());

        String resultString = SendPostandGet.submitPost(
                trainorderurl,
                "orderNo=" + orderNo + "&reqFrom=" + reqFrom + "&reqTime=" + reqTime + "&trainNo=" + trainNo + "&from="
                        + from + "&to=" + to + "&date=" + date + "&retUrl=" + retUrl + "&passengers="
                        + array.toString() + "&HMAC=" + HMACflag, "UTF-8").toString();

        System.out.println(resultString);
    }

    //占座回调
    public static void zhanzuohuidiao() throws Exception {

        String trainorderurl = "http://192.168.0.12:9004/cn_interface/qunarTrainCallBack";
        String key = "A5A202F7D627447EAB7199DCE0E2211F";
        JSONObject json = new JSONObject();
        json.put("merchantCode", "");
        json.put("orderNo", "xcslw150730214710008");
        json.put("result", "");
        json.put("status", "");
        json.put("code", getreqtime());
        json.put("msg", "");
        json.put("comment", getreqtime());

        String merchantCode = json.getString("merchantCode");
        String orderNo = json.getString("orderNo");
        String result = json.getString("result");
        String status = json.getString("status");
        String code = json.getString("code");
        String msg = json.getString("msg");
        String comment = json.getString("comment");

        String HMACflag = ElongHotelInterfaceUtil.MD5(
                key + merchantCode + orderNo + result + status + code + msg + comment).toLowerCase();
        json.put("HMAC", HMACflag);//md5

        System.out.println(json.toString());
        String ss = "{\"trainorderid\":45257,\"method\":\"train_order_callback\",\"returnmsg\":\"%E5%BA%A7%E5%B8%AD%E7%BC%96%E7%A0%81%E4%B8%BA%E7%A9%BA%E3%80%82\",\"merchantCode\":\"xcslw\"}";

        String resultString = SendPostandGet.submitPost(trainorderurl, ss, "UTF-8").toString();

        System.out.println(resultString);

    }

    //取消占座
    public static void quxiaozhanzuo() throws Exception {

        String trainorderurl = "http://hcpzs.qicailvtu.com/cn_interface/qunarTrainCancel";

        String key = "A4490303F7114720B6596B7568B69E51";
        JSONObject json = new JSONObject();
        json.put("orderNo", "hcpzs151514534621122");//订单号
        json.put("reqFrom", "qunar");//请求来源
        json.put("reqTime", getreqtime());//请求时间
        String orderNo = json.getString("orderNo");
        String reqFrom = json.getString("reqFrom");
        String reqTime = json.getString("reqTime");

        String HMACflag = ElongHotelInterfaceUtil.MD5(key + orderNo + reqFrom + reqTime).toUpperCase();

        json.put("HMAC", HMACflag);//md5

        System.out.println(json.toString());

        String resultString = SendPostandGet.submitPost(trainorderurl,
                "orderNo=" + orderNo + "&reqFrom=" + reqFrom + "&reqTime=" + reqTime + "&HMAC=" + HMACflag, "UTF-8")
                .toString();

        System.out.println(resultString);

    }

    //出票
    public static void chuqiao() throws Exception {
        String trainorderurl = "http://hcpzs.qicailvtu.com/cn_interface/qunarInformTicket";
        String key = "A4490303F7114720B6596B7568B69E51";
        JSONObject json = new JSONObject();
        json.put("orderNo", "hcpzs151514534621121");//订单号
        json.put("Type", "1");//通知类型
        json.put("reqFrom", "qunar");//请求来源
        json.put("reqTime", getreqtime());//请求时间

        String orderNo = json.getString("orderNo");
        String Type = json.getString("Type");
        String reqFrom = json.getString("reqFrom");
        String reqTime = json.getString("reqTime");
        String HMACflag = ElongHotelInterfaceUtil.MD5(key + orderNo + Type + reqFrom + reqTime).toUpperCase();

        json.put("HMAC", HMACflag);//md5
        System.out.println("orderNo=" + orderNo + "&Type=" + Type + "&reqFrom=" + reqFrom + "&reqTime=" + reqTime
                + "&HMAC=" + HMACflag);
        String resultString = SendPostandGet.submitPost(
                trainorderurl,
                "orderNo=" + orderNo + "&Type=" + Type + "&reqFrom=" + reqFrom + "&reqTime=" + reqTime + "&HMAC="
                        + HMACflag, "UTF-8").toString();
        System.out.println(resultString);

    }

    public static String getreqtime() {
        return yyyyMMddHHmmss.format(new Date());
    }

}
