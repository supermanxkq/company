package com.ccservice.b2b2c.atom.servlet.yl;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.test.TongchengTest;
import com.ccservice.b2b2c.base.customeruser.Customeruser;

/**
 * 艺龙接口测试类
 * 
 * @time 2015年12月8日 上午11:36:31
 * @author chendong
 */
public class YiLongTest {
    public static String key = "72d97c2acb5923234b2f80a7dbe8d85c";

    public static void main(String[] args) {
        YiLongTest yiLongTest = new YiLongTest();
        String urlYiLong1 = "http://120.26.83.131:9022/cn_interface/YiLongTrainOrder";
        String urlYiLong2 = "http://120.26.83.131:9022/cn_interface/YiLongTrainOrderHuangniu";
        String Elongurl = "http://120.26.83.131:9022/cn_interface/ElongPayMessageServlet";
        String TuiPiao = "http://120.26.83.131:9022/cn_interface/ElongRefundTicketRequest";
        String quxiaozhanzuo = "http://120.26.83.131:9022/cn_interface/ElongCancelOrder";
        /* String sqlTemp = "SELECT top 1 tor.C_ORDERNUMBER C_ORDERNUMBER from T_TRAINORDER as tor with(nolock) "
                 + "where tor.C_QUNARORDERNUMBER='20151222165044195'";
         List list1 = Server.getInstance().getSystemService().findMapResultBySql(sqlTemp, null);
         WriteLog.write("createYlTrainOrder", sqlTemp + "sqlTemp" + "list1.size()" + list1.size());
         if (list1.size() > 0) {
             System.out.println("ok");
         }
         else {
             System.out.println("no");
         }*/
        // huidiao();
        /* *//** try {
                                                                                                                                                                                         payOrderMethod(Elongurl);
                                                                                                                                                                                        }
                                                                                                                                                                                        catch (Exception e) {
                                                                                                                                                                                         e.printStackTrace();
                                                                                                                                                                                        }*/

        try {
            int type = 2;//1、先占座模式  2先支付模式就是黄牛模式 
            yiLongTest.createOrder(type, urlYiLong1, urlYiLong2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        /*tuikuanhuidiao();*/
        //  TuiPiaoTest(TuiPiao);
        //  xianxia();
        // tuiPiaoJieGuo();
        /*   try {
               cancelOrderMethod(quxiaozhanzuo);
           }
           catch (Exception e) {
               e.printStackTrace();
           }
        */
    }

    /*   public static void chupiaohuidiao() {
           String merchantCode = "";
           String orderId = "";
           String jilu = "";
           String failReason = "";
           String failReasonDesc = "";
           String result = "";

           String jsons = URLDecoder.decode(json.toJSONString(), "UTF-8");
           failReason = "0";
           failReasonDesc = msg;
           if (failReasonDesc.contains("已满")) {
               failReasonDesc = "已取纸质车票";
           }
           jilu = "FAIL";
           String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                   "tickets=" + jsons, "failReasonDesc=" + failReasonDesc, "failReason=" + failReason };
           sign = sort(parameters) + key;

       }*/

    /**
     * tickets参数格式
    {
    "ticketNo": "E987654321",
    "orderId":”2015121211252354”,
    “holdingSeatSuccessTime”:”yyyy-MM-dd hh:mm:ss”   占座成功时间
    “payTimeDeadLine”:”yyyy-MM-dd hh:mm:ss”  用户支付截止时间
    "tickets": [
        {
             "orderItemId”:”2222222”,
            "seatType": "9",
            "seatNo": "13车08号",
            "price": 263,
            "passengerName": "曹啸博",
            "certNo": "370724198704210751",
            "ticketType": "1"
        },
    */
    public static void xianxia() {
        String orderItemId = "20160108252706837";
        String merchantCode = "hangtian111";
        String note = "1";
        String orderId = "20160108252706836";
        String times = new Timestamp(System.currentTimeMillis()).toString();
        String type = "1";
        String localSign = "merchantId=" + merchantCode + "&timeStamp=" + times + "&orderId=" + orderId
                + "&orderItemId=" + orderItemId + "&note=" + note + "&type=" + type;
        localSign = getSignMethod(localSign) + key;
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(localSign);
        String paramContent2 = "merchantId=" + merchantCode + "&timeStamp=" + times + "&orderId=" + orderId
                + "&orderItemId=" + orderItemId + "&note=" + note + "&type=" + type + "&sign=" + localSign;
        String results = SendPostandGet.submitPost("http://120.26.83.131:9022/cn_interface/YiLongReqChange",
                paramContent2, "utf-8").toString();
        System.out.println(results);
    }

    public static void zhanzuohuidiao() {
        /**
         * 代理商code  merchantCode    必填  由艺龙分配   
        订单号 orderId 必填  订单号 
        占座结果    result  必填  SUCCESS:成功
        FAIL:失败 
        占座信息    tickets result= SUCCESS时必填  出票信息    出票成功时必填
        失败原因码   failReason  result=FAIL时必填  出票失败原因错误代码

        失败原因描述  failReasonDesc  result=FAIL时必填  出票失败原因描述    
        备注  comment 非必填 备注  
        签名  sign    必填  数据签名
        参照签名机
         * */
        String merchantCode = "";
        String orderId = "";
        String result = "SUCCESS";
        String failReason = "";
        String failReasonDesc = "";
        String comment = "";
        String sign = "";
        String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + result,
                "comment=" + comment, "sign=" + sign };
        sign = sort(parameters);
        String parm = "&merchantCode=" + merchantCode + "&orderId=" + orderId + "&result=" + result + "&comment="
                + comment + "&sign=" + sign;
        String results = SendPostandGet.submitPost("http://121.79.134.36:8100/open_api/process_refund", parm, "utf-8")
                .toString();

    }

    public static void tuikuanhuidiao() {
        /*
         * 代理商code  merchantCode
        订单号 orderId
        订单项号    orderItemId
        退款流水号   tradeNo
        退款金额    amount
        退款说明    comment
        签名  sign

         * */
        String orderId = "20160106252445194";
        String orderItemId = "20160106252445195";
        String tradeNo = "E8927386521060173";
        String merchantCode = "hangtian111";
        String amount = "1";
        String comment = "退款测试";
        //String tradeNo = returnmsg;
        //String type = "";
        String singmd = "";
        String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "tradeNo=" + tradeNo,
                "orderItemId=" + orderItemId, "amount=" + amount, "comment=" + comment };
        String sign = sort(parameters);

        try {
            singmd = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String parm = "&merchantCode=" + merchantCode + "&orderId=" + orderId + "&tradeNo=" + tradeNo + "&comment="
                + comment + "&amount=" + amount + "&orderItemId=" + orderItemId + "&sign=" + singmd;
        String results = SendPostandGet.submitPost("http://121.79.134.36:8100/open_api/process_refund", parm, "utf-8")
                .toString();
        System.out.println(results);
    }

    public static void tuiPiaoJieGuo() {
        //Boolean result = false;
        String orderItemId = "20160115264124321";
        String merchantCode = "hangtian111";
        String amount = "478.5";
        String orderId = "20160115264124319";
        String jilu = "";
        String sign = "";
        String failReasonDesc = "";
        String failReason = "";
        // if (result == true) {

        /*   jilu = "SUCCESS";
           String[] parameters = { "merchantCode=hangtian111", "orderId=20160114264110364", "comment=成功",
                   "orderItemId=20160114264110365", "amount=171", "tradeNo=EC752052971100043" };*/
        sign = "amount=0&comment=成功&merchantCode=hangtian111&orderId=20160119264451477&orderItemId=20160119264451478&tradeNo=E921207853102011C";
        /* failReason = "0";
         failReasonDesc = "已取票";
         jilu = "FAIL";
         String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                 "failReasonDesc=" + failReasonDesc, "failReason=" + failReason, "orderItemId=" + orderItemId };
         sign = sort(parameters);
         System.out.println("sign：：" + sign);*/
        //amount=196&comment=成功&merchantCode=hangtian111&orderId=20160113262400885&orderItemId=20160113262400886&tradeNo=T1601131640367408772
        //amount=196&comment=成功&merchantCode=hangtian111&orderId=20160113262400885&orderItemId=20160113262400886&tradeNo=T1601131640367408772
        /* // }
          else if (result == false) {
              failReason = failReasonDescerr(returnmsg);
              failReasonDesc = returnmsg;
              jilu = "FAIL";
              String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                      "failReasonDesc=" + failReasonDesc, "failReason=" + failReason };
              sign = sort(parameters);
              
          }*/
        String singmd = "";
        try {
            singmd = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*  String parm = "&merchantCode=" + merchantCode + "&orderId=" + orderId + "&result=" + jilu + "&failReasonDesc="
                  + failReasonDesc + "&failReason=" + failReason + "&amount=" + amount + "&orderItemId=" + orderItemId
                  + "&sign=" + singmd;*/

        String parm = "merchantCode=hangtian111&orderId=20160119264451477&orderItemId=20160119264451478&comment=%E6%88%90%E5%8A%9F&amount=0&tradeNo=E921207853102011C&sign="
                + singmd + "";
        String results = SendPostandGet.submitPost("http://trainapi.elong.com/open_api/process_refund", parm, "utf-8")
                .toString();
        System.out.println(results);
        WriteLog.write("艺龙火车票接口_4.10退票回调通知", "" + ":parm:" + parm + "|sign" + "" + "|results" + results);

    }

    public static void huidiao() {

        /* String failReason = "";
         String failReasonDesc = "";
            failReason = "0";
            failReasonDesc = "已取票";
         String jilu = "SUCCESS";
         String[] parameters = { "merchantCode=hangtian111", "orderId=20160118264219138", "result=" + jilu,
                 "orderItemId=20160118264219140", "amount=0" };
         String sign = sort(parameters);

         try {
             sign = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
         }
         catch (Exception e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }*/
        //{’timestamp’:’1454294526’,’sign’:’cc883cd988ce784d39800781ef65acb7’,’reqtoken’:’meituan14541375860001’,’returntickets’:[{’returnsuccess’:true,’returnfailid’:’’,’passengername’:’%E5%91%A8%E5%9D%87%E7%A4%BC’,’ticket_no’:’E5488422931180043’,’returnmoney’:’229’,’returnfailmsg’:’’,’passportseno’:’532224196708143715’,’passporttypeseid’:’1’,’returntime’:’2016-02-01 10:42:06’}],’apiorderid’:’14541375860001’,’token’:’1454294526063’,’returnmoney’:’229’,’trainorderid’:’E548842293’,’returnmsg’:’’,’returnstate’:true,’returntype’:’1’}
        /*String parm = "merchantCode=hangtian111&orderId=20160118264219138&result=" + jilu + "&failReasonDesc="
                + failReasonDesc + "&failReason=" + failReason + "&sign=" + sign + "&amount=0"
                + "&orderItemId=20160118264219140" + "&comment=";*/

        //String parm = "data={timestamp:1454294526 , sign : cc883cd988ce784d39800781ef65acb7 , reqtoken : meituan14541375860001 , returntickets :[{ returnsuccess :true, returnfailid :  , passengername : %E5%91%A8%E5%9D%87%E7%A4%BC , ticket_no : E5488422931180043 , returnmoney : 229 , returnfailmsg :  , passportseno : 532224196708143715 , passporttypeseid : 1 , returntime : 2016-02-01 10:42:06 }], apiorderid : 14541375860001 , token : 1454294526063 , returnmoney : 229 , trainorderid : E548842293 , returnmsg :  , returnstate :true, returntype : 1 }";
        String parm = "data={\"timestamp\":\"1454294526\",\"sign\":\"cc883cd988ce784d39800781ef65acb7\",\"reqtoken\":\"meituan14541375860001\",\"returntickets\":[{\"returnsuccess\":true,\"returnfailid\":\"\",\"passengername\":\"%E5%91%A8%E5%9D%87%E7%A4%BC\",\"ticket_no\":\"E5488422931180043\",\"returnmoney\":\"229\",\"returnfailmsg\":\"\",\"passportseno\":\"532224196708143715\",\"passporttypeseid\":\"1\",\"returntime\":\"2016-02-01 10:42:06\"}],\"apiorderid\":\"14541375860001\",\"token\":\"1454294526063\",\"returnmoney\":\"229\",\"trainorderid\":\"E548842293\",\"returnmsg\":\"\",\"returnstate\":true,\"returntype\":\"1\"}";
        String re = SendPostandGet.submitPost("http://i.meituan.com/uts/train/agentht/returnticketnotify", parm,
                "utf-8").toString();
        System.out.println(re);

    }

    public static void TuiPiaoTest(String url) {
        /**
         *  "arrStation": "三亚",                        始发站
        "cancelTime": "2014-08-07 14:52:32.281",     退票时间
        "dptStation": "海口东",                       到达站

         * */
        JSONObject paramJson = new JSONObject();
        paramJson.put("arrStation", "三亚");
        paramJson.put("dptStation", "海口东");
        String times = new Timestamp(System.currentTimeMillis()).toString();
        String localSign = "merchantId=hangtian111&timeStamp=" + times
                + "&orderId=20160108252706836&orderItemId=20160108252706837&paramJson=" + paramJson;
        localSign = getSignMethod(localSign) + key;
        System.out.println("localSign编译前" + localSign);

        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            System.out.println("localSign编译后" + localSign);
            String paramContent2 = "merchantId=hangtian111&timeStamp=" + times
                    + "&orderId=20160108252706836&orderItemId=20160108252706837&paramJson=" + paramJson + "&sign="
                    + localSign;
            System.out.println("paramContent2" + paramContent2);
            String resultString = SendPostandGet.submitPost(url, paramContent2, "UTF-8").toString();
            System.out.println(resultString);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void cancelOrderMethod(String url) throws Exception {

        String times = new Timestamp(System.currentTimeMillis()).toString();
        String paramContent = "merchantId=hangtian111&timeStamp=" + times + "&orderId=20160107252445390";
        //        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
        //                + "&ticketPrice=" + ticketPrice + "&result=" + result;

        String signStr = getSignMethod(paramContent) + key;
        System.out.println(signStr);
        signStr = ElongHotelInterfaceUtil.MD5(signStr).toUpperCase();
        System.out.println(signStr);
        String paramContent2 = "merchantId=hangtian111&timeStamp=" + times + "&orderId=20160107252445390" + "&sign="
                + signStr;
        String resultString = SendPostandGet.submitPost(url, paramContent2, "UTF-8").toString();
        System.out.println(resultString);

    }

    /**
     * 确认出票
     * 
     * @param url
     * @throws Exception
     * @time 2015年12月24日 下午9:44:44
     * @author Administrator
     */
    private static void payOrderMethod(String url) throws Exception {
        String times = new Timestamp(System.currentTimeMillis()).toString();

        String paramContent = "merchantId=yilong_test&timeStamp=" + times + "&orderId=455122175661333020"
                + "&ticketPrice=2&result=SUCCESS";
        //        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
        //                + "&ticketPrice=" + ticketPrice + "&result=" + result;

        String signStr = getSignMethod(paramContent) + key;
        System.out.println(signStr);
        signStr = ElongHotelInterfaceUtil.MD5(signStr).toUpperCase();
        System.out.println(signStr);
        String paramContent2 = "merchantId=hangtian111&timeStamp=" + times + "&orderId=455122175661333020"
                + "&ticketPrice=2&sign=" + signStr + "&result=SUCCESS";
        String resultString = SendPostandGet.submitPost(url, paramContent2, "UTF-8").toString();
        System.out.println(resultString);
    }

    /**
     * 1、先占座模式  2先支付模式就是黄牛模式 
     * @time 2015年12月8日 下午2:56:37
     * @author chendong
     */
    private void createOrder(int type, String urlYiLong1, String urlYiLong2) {
        String trainorderurl = "";
        if (type == 1) {
            trainorderurl = urlYiLong1;
        }
        else {
            trainorderurl = urlYiLong2;
        }
        JSONObject jsonObject = new JSONObject();
        List<Integer> extSeats = new ArrayList<Integer>();
        String timeStamp = TongchengTest.getreqtime();
        String orderId = timeStamp;
        jsonObject.put("acceptStand", "2");
        jsonObject.put("arrStation", "包头东");
        jsonObject.put("dptStation", "包头");
        jsonObject.put("contactMobile", "15987415987");
        jsonObject.put("contactName", "自动化");
        jsonObject.put("orderDate", "2016-04-25 10:48:05");
        jsonObject.put("orderId", orderId);
        jsonObject.put("seatType", "15");
        jsonObject.put("ticketPrice", 1);
        jsonObject.put("trainEndTime", "2016-04-28 18:34:00");
        jsonObject.put("trainNo", "K56");
        jsonObject.put("trainStartTime", "2016-04-25 18:19:00");
        jsonObject.put("extSeats", extSeats);
        JSONArray jsonArrayPassengers = new JSONArray();
        JSONObject jsonObject_p1 = new JSONObject();
        jsonObject_p1.put("certNo", "340826197909170026");
        jsonObject_p1.put("certType", "1");
        jsonObject_p1.put("name", "徐丽");
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
        localSign = getSignMethod(localSign) + key;
        System.out.println("1:" + localSign);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            System.out.println("2加密后:" + localSign);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String paramContent = "merchantId=hangtian111&timeStamp=" + timeStamp + "&orderId=" + orderId + "&paramJson="
                + jsonObject.toJSONString() + "&sign=" + localSign;

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

    /**
     * 
     * 
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
        return "";
    }

    /**
     * 
     * 
     * @param str
     * @time 2015年12月10日 上午11:39:31
     * @author Mr.Wang
     */
    private static String sort(String[] str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            String maxStr = str[i];
            int index = i;
            //  System.out.println(str.length + ":第" + i + "次循环");
            for (int j = i + 1; j < str.length; j++) {
                if (maxStr.compareTo(str[j]) >= 0) {
                    maxStr = str[j];
                    index = j;
                }
            }
            str[index] = str[i];
            str[i] = maxStr;
            // System.out.println(i + ":" + maxStr);
            sb.append(maxStr + "&");
        }
        String sign = sb.toString();
        if (sign.endsWith("&")) {
            sign = sign.substring(0, sb.toString().length() - 1);
        }
        return sign;
    }

}
