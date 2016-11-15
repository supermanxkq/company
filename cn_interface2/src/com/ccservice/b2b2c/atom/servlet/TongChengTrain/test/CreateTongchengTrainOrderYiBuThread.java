package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.List;
import java.util.Random;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class CreateTongchengTrainOrderYiBuThread extends Thread {
    public String partnerid;

    public String key;

    public String trainorderurl;

    public String orderid;

    public String checi;

    public String from_station_code;

    public String from_station_name;

    public String to_station_code;

    public String to_station_name;

    public String train_date;

    public String callbackurl;

    public String reqtoken;

    List<Trainpassenger> passengers;

    public CreateTongchengTrainOrderYiBuThread(String partnerid, String key, String trainorderurl, String orderid,
            String checi, String from_station_code, String from_station_name, String to_station_code,
            String to_station_name, String train_date, List<Trainpassenger> passengers, String callbackurl,
            String reqtoken) {
        super();
        this.partnerid = partnerid;
        this.key = key;
        this.trainorderurl = trainorderurl;
        this.orderid = orderid;
        this.checi = checi;
        this.from_station_code = from_station_code;
        this.from_station_name = from_station_name;
        this.to_station_code = to_station_code;
        this.to_station_name = to_station_name;
        this.train_date = train_date;
        this.passengers = passengers;
        this.callbackurl = callbackurl;
        this.reqtoken = reqtoken;
    }

    @Override
    public void run() {
        int r1 = new Random().nextInt(1000);
        System.out.println("run:" + r1);
        train_order(this.orderid, this.checi, this.from_station_code, this.from_station_name, this.to_station_code,
                this.to_station_name, this.train_date, this.passengers, r1, this.callbackurl, this.reqtoken);
    }

    /**
     * 4.5. 申请分配座位席别
     * 
     * 
     * @param orderid
     * @param checi T124
     * @param from_station_code CSQ
     * @param from_station_name 长沙
     * @param to_station_code CCT
     * @param to_station_name 长春
     * @param train_date 2014-12-18
     * @param passengers 乘客
     * @time 2014年12月12日 下午2:59:15
     * @author chendong
     * @param r1 
     * @param callbackurl 
     */
    private String train_order(String orderid, String checi, String from_station_code, String from_station_name,
            String to_station_code, String to_station_name, String train_date, List<Trainpassenger> passengers, int r1,
            String callbackurl, String reqtoken) {
        System.out.println("train_order:" + r1);
        String method = "train_order";
        String reqtime = TongchengTest.getreqtime() + "_" + new Random().nextInt(100) + "_" + new Random().nextInt(100)
                + "_" + new Random().nextInt(100) + "_" + new Random().nextInt(100);
        String sign = TongchengTest.getsign(partnerid, method, reqtime, key);
        //        String passengerJSON = "{\"passengerid\":\"1\",\"ticket_no\":\"\",\"passengersename\":\"陈栋\",\"passportseno\":\"412823198909298017\",\"passporttypeseid\":\"1\",\"passporttypeseidname\":\"二代身份证\",\"piaotype\":\"1\",\"piaotypename\":\"\",\"zwcode\":\"3\",\"zwname\":\"硬卧\",\"cxin\":\"\",\"price\":\"488.5\"},{\"passengerid\":\"1\",\"ticket_no\":\"\",\"passengersename\":\"王战朝\",\"passportseno\":\"410883199006281010\",\"passporttypeseid\":\"1\",\"passporttypeseidname\":\"二代身份证\",\"piaotype\":\"1\",\"piaotypename\":\"\",\"zwcode\":\"3\",\"zwname\":\"硬卧\",\"cxin\":\"\",\"price\":\"488.5\"}";
        String passengerJSON = "";
        for (int i = 0; i < passengers.size(); i++) {
            Trainpassenger trainpassenger = passengers.get(i);
            Trainticket trainticket = passengers.get(i).getTraintickets().get(0);
            String passporttypeseid = "1";
            String passporttypeseidname = "二代身份证";

            String zwcode = trainticket.getSeattype();
            String zwname = new TongchengTest().getzwcode(zwcode);
            Float price = trainticket.getPrice();
            int piaotype = trainticket.getTickettype();
            String piaotypename = trainticket.getTickettypestr();
            passengerJSON += "{\"passengerid\":\"" + i + "\",\"ticket_no\":\"\",\"passengersename\":\""
                    + trainpassenger.getName() + "\",\"passportseno\":\"" + trainpassenger.getIdnumber()
                    + "\",\"passporttypeseid\":\"" + passporttypeseid + "\",\"passporttypeseidname\":\""
                    + passporttypeseidname + "\",\"piaotype\":\"" + piaotype + "\",\"piaotypename\":\"" + piaotypename
                    + "\",\"zwcode\":\"" + zwcode + "\",\"zwname\":\"" + zwname + "\",\"cxin\":\"\",\"price\":\""
                    + price + "\",\"province_name\":\"test\",\"province_code\":\"test\","
                    + "\"school_code\":\"test\",\"school_name\":\"test\","
                    + "\"student_no\":\"test\",\"school_system\":\"test\","
                    + "\"enter_year\":\"test\",\"preference_from_station_name\":"
                    + "\"test\",\"preference_from_station_code\":\"test\","
                    + "\"preference_to_station_name\":\"test\"," + "\"preference_to_station_code\":\"test\"}";
            if (i < passengers.size() - 1) {
                passengerJSON += ",";
            }
        }
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"checi\":\"" + checi
                + "\",\"from_station_code\":\"" + from_station_code + "\",\"from_station_name\":\"" + from_station_name
                + "\",\"to_station_code\":\"" + to_station_code + "\",\"to_station_name\":\"" + to_station_name
                + "\",\"train_date\":\"" + train_date + "\",\"callbackurl\":\"" + callbackurl + "\",\"reqtoken\":\""
                + reqtoken + "\",\"passengers\":[" + passengerJSON + "]}";
        Long l1 = System.currentTimeMillis();
        System.out.println(r1 + ":" + TimeUtil.gettodaydate(4) + ":ceshi访问几次 :jsonStr=" + jsonStr);
        String resultString = "";
        resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println(r1 + ":" + TimeUtil.gettodaydate(4) + ":" + orderid + ":耗时:"
                + (System.currentTimeMillis() - l1) + ":======" + resultString);
        return resultString;
    }
}
