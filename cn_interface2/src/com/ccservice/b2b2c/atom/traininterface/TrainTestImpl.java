package com.ccservice.b2b2c.atom.traininterface;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.tenpay.util.MD5Util;

public class TrainTestImpl extends TongchengSupplyMethod implements ITrainTestDao {
    // http://trainorder.test.hangtian123.net/cn_interface/tcTrain
    // public String trainsearchurl =
    // "http://tcsearchtrain.hangtian123.net/trainSearch";

    public String trainorderurl = "http://trainorder.test.hangtian123.net/cn_interface/tcTrain";

    public String trainticketurl = "http://trainorder.test.hangtian123.net/cn_interface/trainSearch";

    public String traincheciurl = "http://121.40.90.249:19004/cn_interface/TrainEnquiriesServlet";

    // 接口账号
    public String partnerid = "jiekoutest";

    public String key = "2pUjUHRFSvWLWoUrfiWiZ813Be8f0IQI";

    // 占座成功回调地址
    public String callbackurl = "";

    public final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * 创建乘客信息
     **/
    @Override
    public Trainpassenger getTrainpassenger(String name, String idnumber, int idtype, String seattype, Float price,
            String ticketno, int tickettype) {
        Trainpassenger Trainpassenger = new Trainpassenger();
        Trainpassenger.setName(name);
        Trainpassenger.setIdnumber(idnumber);
        Trainpassenger.setIdtype(idtype);// 1:"二代身份证"; 3:"护照";4:"港澳通行证";5:
                                         // "台湾通行证";
        List<Trainticket> traintickets = new ArrayList<Trainticket>();
        Trainticket e = new Trainticket();
        e.setSeattype(seattype);
        e.setPrice(price);
        e.setTicketno(ticketno);
        e.setTickettype(tickettype);
        traintickets.add(e);
        Trainpassenger.setTraintickets(traintickets);
        return Trainpassenger;
    }

    /**
     * 申请分配座位席别
     **/
    @Override
    public String train_order(String orderid, String checi, String from_station_code, String from_station_name,
            String to_station_code, String to_station_name, String train_date, List<Trainpassenger> passengers,
            String callbackurl, String partnerid, String reqtoken, String hasseat, String waitfororder, String shoudan) {
        String method = "train_order";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String passengerJSON = "";
        String jsonStr = "";
        for (int i = 0; i < passengers.size(); i++) {
            Trainpassenger trainpassenger = passengers.get(i);
            Trainticket trainticket = passengers.get(i).getTraintickets().get(0);
            String passporttypeseid = "1";
            String passporttypeseidname = "二代身份证";
            String zwcode = trainticket.getSeattype();
            String zwname = getzwcode(zwcode);
            Float price = trainticket.getPrice();
            int piaotype = trainticket.getTickettype();
            String piaotypename = trainticket.getTickettypestr();
            try {
                passengerJSON += "{\"passengerid\":\"" + i + "\",\"ticket_no\":\"\",\"passengersename\":\""
                        + URLEncoder.encode(trainpassenger.getName(), "UTF-8") + "\",\"passportseno\":\""
                        + trainpassenger.getIdnumber() + "\",\"passporttypeseid\":\"" + passporttypeseid
                        + "\",\"passporttypeseidname\":\"" + URLEncoder.encode(passporttypeseidname, "UTF-8")
                        + "\",\"piaotype\":\"" + piaotype + "\",\"piaotypename\":\""
                        + URLEncoder.encode(piaotypename, "UTF-8") + "\",\"zwcode\":\"" + zwcode + "\",\"zwname\":\""
                        + URLEncoder.encode(zwname, "UTF-8") + "\",\"cxin\":\"\",\"price\":\"" + price + "\"}";
                if (i < passengers.size() - 1) {
                    passengerJSON += ",";
                }
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            jsonStr += "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                    + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"checi\":\"" + checi
                    + "\",\"from_station_code\":\"" + from_station_code + "\",\"from_station_name\":\""
                    + URLEncoder.encode(from_station_name, "UTF-8") + "\",\"to_station_code\":\"" + to_station_code
                    + "\",\"to_station_name\":\"" + URLEncoder.encode(to_station_name, "UTF-8")
                    + "\",\"train_date\":\"" + train_date + "\",\"callbackurl\":\"" + callbackurl
                    + "\",\"reqtoken\":\"" + reqtoken + "\",\"hasseat\":\"" + hasseat + "\",\"waitfororder\":\""
                    + waitfororder + "\",\"shoudan\":\"" + shoudan + "\",\"passengers\":[" + passengerJSON + "]}";
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Long temp1 = System.currentTimeMillis();
        System.out.println("test请求开始时间:" + temp1);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("test请求结束到开始时间差:" + (System.currentTimeMillis() - temp1));
        return resultString;
    }

    /**
     * 火车票确认出票
     **/
    @Override
    public String train_confirm(String orderid, String transactionid) {
        String method = "train_confirm";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * 查询订单详情
     **/
    @Override
    public String train_query_info(String orderid, String transactionid) {
        String method = "train_query_info";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "";

        jsonStr += "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "GB2312").toString();
        // try {
        // resultString=URLDecoder.decode(resultString, "UTF-8");
        // } catch (UnsupportedEncodingException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        return resultString;
    }

    /**
     * 取消火车票订单
     **/
    @Override
    public String train_cancel(String orderid, String transactionid) {
        String method = "train_cancel";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println(resultString);
        return resultString;
    }

    /**
     * 在线退票
     **/
    @Override
    public String return_ticket(String orderid, String transactionid, String ordernumber, String reqtoken,
            String callbackurl, Trainorder trainorder) {
        String method = "return_ticket";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String passengerJSON = "";
        for (int i = 0; i < trainorder.getPassengers().size(); i++) {
            Trainpassenger trainpassenger = trainorder.getPassengers().get(i);

            try {
                passengerJSON += "{\"ticket_no\":\"" + trainpassenger.getTraintickets().get(0).getTicketno()
                        + "\",\"passengername\":\"" + URLEncoder.encode(trainpassenger.getName(), "UTF-8")
                        + "\",\"passportseno\":\"" + trainpassenger.getIdnumber() + "\",\"passporttypeseid\":\"1\"}";
                if (i < trainorder.getPassengers().size() - 1) {
                    passengerJSON += ",";
                }
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\",\"ordernumber\":\"" + ordernumber + "\",\"reqtoken\":\"" + reqtoken + "\",\"callbackurl\":\""
                + callbackurl + "\",,\"tickets\":[" + passengerJSON + "]}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "utf-8").toString();
        return resultString;
    }

    /**
     * 请求改签
     **/
    @Override
    public String train_request_change(String orderid, String transactionid, String ordernumber, String change_checi,
            String change_datetime, String change_zwcode, String old_zwcode, String passengersename,
            String passporttypeseid, String passportseno, String piaotype, String old_ticket_no) {
        String method = "train_request_change";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "";

        try {
            jsonStr += "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                    + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\""
                    + transactionid + "\",\"ordernumber\":\"" + ordernumber + "\",\"change_checi\":\"" + change_checi
                    + "\",\"change_datetime\":\"" + change_datetime + "\",\"change_zwcode\":\"" + change_zwcode
                    + "\",\"old_zwcode\":\"" + old_zwcode + "\",\"ticketinfo\":[{\"passengersename\":\""
                    + URLEncoder.encode(passengersename, "UTF-8") + "\",\"passporttypeseid\":\"" + passporttypeseid
                    + "\",\"passportseno\":\"" + passportseno + "\",\"piaotype\": \"" + piaotype
                    + "\",\"old_ticket_no\":\"" + old_ticket_no + "\"}]}";
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    /**
     * 取消改签
     **/
    @Override
    public String train_cancel_change(String orderid, String transactionid) {
        String method = "train_cancel_change";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * 确认改签
     **/
    @Override
    public String train_confirm_change(String orderid, String transactionid) {
        String method = "train_confirm_change";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * 余票查询（有价格）
     **/
    @Override
    public String train_query(String train_date, String from_station, String to_station, String purpose_codes,
            String needdistance) {
        String method = "train_query";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"train_date\":\"" + train_date + "\",\"from_station\":\""
                + from_station + "\",\"to_station\":\"" + to_station + "\",\"purpose_codes\":\"" + purpose_codes
                + "\",\"needdistance\":\"" + needdistance + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainticketurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * 余票查询（无价格）
     **/
    @Override
    public String train_query_remain(String train_date, String from_station, String to_station, String purpose_codes,
            String needdistance) {
        String method = "train_query_remain";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"train_date\":\"" + train_date + "\",\"from_station\":\""
                + from_station + "\",\"to_station\":\"" + to_station + "\",\"purpose_codes\":\"" + purpose_codes
                + "\",\"needdistance\":\"" + needdistance + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainticketurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * 车次查询
     **/
    @Override
    public String get_train_info(String train_date, String from_station, String to_station, String train_no,
            String train_code) {
        String method = "get_train_info";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"train_date\":\"" + train_date + "\",\"from_station\":\""
                + from_station + "\",\"to_station\":\"" + to_station + "\",\"train_no\":\"" + train_no
                + "\",\"train_code\":\"" + train_code + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(traincheciurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))
     **/
    public static String getsign(String partnerid, String method, String reqtime, String key) {
        return MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8"), "UTF-8");
    }

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getcurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }

}
