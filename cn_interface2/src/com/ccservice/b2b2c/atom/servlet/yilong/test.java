package com.ccservice.b2b2c.atom.servlet.yilong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;

import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.test.Test;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.test.TongchengTest;
import com.ccservice.b2b2c.atom.servlet.yl.YiLongTrainOrderMethodNoSeat;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;

public class test {
    //    public static String key = "72d97c2acb5923234b2f80a7dbe8d85c";
    public static String key = "72d97c2acb5923234b2f80a7dbe8d85c";

    public static void main(String[] args) {
        //        test test = new test();
        //        String urlYiLong1 = "";
        //        test.createOrder(urlYiLong1);
        //        System.out.println(test.payInform());
        //        YiLongZhanZuoCallBack yiLong = new YiLongZhanZuoCallBack();
        String s = "工信部";
        try {
            JSONObject.parse(s);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("123456");
//        test test = new test();
//        String urlYiLong1 = "";
//        //        test.createOrder(urlYiLong1);
//        System.out.println(test.payInform());
//        //        YiLongZhanZuoCallBack yiLong = new YiLongZhanZuoCallBack();
        noSeat();

    }

    public static void noSeat(){
        Trainticket trainticket = new Trainticket(); 
        trainticket.setSeattype("无座");
        trainticket.setTrainno("G651");
        trainticket.setDeparttime("2016-06-04");
        trainticket.setDeparture("北京西");
        trainticket.setArrival("西安北");
        trainticket.setPrice(155.5f);
        Trainorder trainorder = new Trainorder();
        trainticket.setId(40821);
        trainorder.setId(0l);
        new YiLongTrainOrderMethodNoSeat(trainticket, trainorder).start();
    }
    
    private void createOrder(String urlYiLong1) {
        String trainorderurl = "http://120.26.83.131:9022/cn_interface/YiLongTrainOrder";
        //        trainorderurl = urlYiLong1;

        JSONObject jsonObject = new JSONObject();
        List<Integer> extSeats = new ArrayList<Integer>();
        String timeStamp = TongchengTest.getreqtime();
        String orderId = timeStamp;
        jsonObject.put("acceptStand", "1");
        jsonObject.put("arrStation", "包头东");
        jsonObject.put("dptStation", "包头");
        jsonObject.put("contactMobile", "15987415987");
        jsonObject.put("contactName", "杨荣强");
        jsonObject.put("orderDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jsonObject.put("orderId", orderId);
        jsonObject.put("seatType", "15");
        jsonObject.put("ticketPrice", 1);
        jsonObject.put("trainEndTime", "2016-05-30 18:34:00");
        jsonObject.put("trainNo", "K56");
        jsonObject.put("trainStartTime", "2016-05-30 18:19:00");
        jsonObject.put("extSeats", extSeats);
        JSONArray jsonArrayPassengers = new JSONArray();
        JSONObject jsonObject_p1 = new JSONObject();
        jsonObject_p1.put("certNo", "140622199210264251");
        jsonObject_p1.put("certType", "1");
        jsonObject_p1.put("name", "杨荣强");
        jsonObject_p1.put("orderItemId", "201601082527022211");
        jsonObject_p1.put("ticketType", "1");
        // wuguchuvlmt2kaf6
        /* JSONObject jsonObject_p2 = new JSONObject();
         jsonObject_p2.put("certNo", "429004199110072973");
         jsonObject_p2.put("certType", "1");
         jsonObject_p2.put("name", "王琳");
         jsonObject_p2.put("orderItemId", "20140801117702187");
         jsonObject_p2.put("ticketType", "1");*/
        //   JSONObject jsonObject_p3 = new JSONObject();
        //  jsonObject_p3.put("certNo", "342222199901191212");
        //  jsonObject_p3.put("certType", "1");
        // jsonObject_p3.put("name", "苏镖");
        //  jsonObject_p3.put("orderItemId", "20140807212401998");
        // jsonObject_p3.put("ticketType", "1");
        jsonArrayPassengers.add(jsonObject_p1);
        //   jsonArrayPassengers.add(jsonObject_p2);
        // jsonArrayPassengers.add(jsonObject_p3);
        jsonObject.put("passengers", jsonArrayPassengers);

        orderId = timeStamp;
        String localSign = "merchantId=hangtian111&timeStamp=" + timeStamp + "&orderId=" + orderId + "&paramJson="
                + jsonObject.toJSONString();
        //        String localSign = "merchantId=hangtian111&timeStamp=" + timeStamp + "&orderId=" + orderId + "&paramJson="
        //                + jsonObject.toJSONString();
        localSign = getSignMethod(localSign) + key;
        System.out.println("1:" + localSign);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            System.out.println("2加密后:" + localSign);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String paramContent = "merchantId=hangtian111&timeStamp=" + timeStamp + "&orderId=" + orderId + "&paramJson="
                + jsonObject.toJSONString() + "&sign=" + localSign;
        //        String paramContent = "merchantId=hangtian111&timeStamp=" + timeStamp + "&orderId=" + orderId + "&paramJson="
        //                + jsonObject.toJSONString() + "&sign=" + localSign;

        System.out.println("最后的值：" + paramContent);
        String resultString = SendPostandGet.submitPost(trainorderurl, paramContent, "UTF-8").toString();
        System.out.println(resultString);
        //        {
        //            "acceptStand": "1",
        //            "arrStation": "北京南",
        //            "contactMobile": "13787484435",
        //            "contactName": "自动化",
        //            "dptStation": "杭州",
        //            "orderDate": "2014-08-07 10:52:05",
        //            "orderId": "20140807212402185",
        //            "passengers": [
        //              {
        //                "certNo": "370724198704210751",
        //                "certType": "1",
        //                "name": "张玉朋",
        //                "orderItemId": "20140807212402186",
        //                "ticketType": "2"
        //              },
        //              {
        //                "certNo": "46000319871214184X",
        //                "certType": "1",
        //                "name": "李月华",
        //                "orderItemId": "20140807212402187",
        //                "ticketType": "2"
        //              }
        //            ],
        //            "seatType": "10",
        //            "ticketPrice": 629,
        //            "trainEndTime": "2014-08-09 16:17:00",
        //            "trainNo": "G41",
        //            "trainStartTime": "2014-08-09 09:33:00"
        //          }

    }

    //
    //    private static String getSignMethod(String sign) {
    //        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
    //            String[] signParam = sign.split("&");
    //            sign = ElongHotelInterfaceUtil.sort(signParam);
    //            return sign;
    //        }
    //        return "";
    //        //        JSONObject jsonObject = new JSONObject();
    //        //        jsonObject.put("A", 123);
    //        //        jsonObject.put("B", 234);
    //        //        System.out.println(jsonObject.toJSONString());
    ////        test test = new test();
    ////        System.out.println(test.payInform());
    //    }

   
    public static String payInform() {
        String merchantId = "hangtian111";
        String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
        String orderId = "20160526200201553";
        //        String ticketPrice = "78.0";
        //        String result = "SUCCESS";
        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
        /*  + "&ticketPrice=" + ticketPrice + "&result=" + result*/;
        localSign = getSignMethod(localSign) + key;
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String paramContent2 = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
        /*+ "&ticketPrice=" + ticketPrice + "&result=" + result*/+ "&sign=" + localSign;
        String results = SendPostandGet.submitPost("http://120.26.83.131:9022/cn_interface/ElongCancelOrder",
                paramContent2, "utf-8").toString();
        System.out.println(results);
        return results;
    }

    /**
     * @param json
     * @return
     * @time 2015年12月10日 上午11:47:17
     * @author Mr.Wang
     */
    private static String getSignMethod(String sign) {
        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
            String[] signParam = sign.split("&");
            sign = ElongHotelInterfaceUtil.sort(signParam);
            return sign;
        }
        return sign;

    }
}
