package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.tenpay.util.MD5Util;

/**
 * 同程接口测试类
 * 
 * @time 2014年12月12日 下午2:46:05
 * @author chendong
 */
public class TongchengTest extends TongchengSupplyMethod {
    //    String parterid_key = "tuniu_test|0q5zrtnfj02xms8qevs1293ltp49qv3b";//途牛测试

    String parterid_key = "tianqu|ydsszfvupijrdeojsgrawumvlqdekdss";

    //    String parterid_key = "jiekoutest|ydsszfvupijrdeojsgrawumvlqdekdss";

    //    String parterid_key = "tongcheng_train_test|lmh46c63ubh1h8oj6680wbtgfi40btqh";//测试

    //    String parterid_key = "shanghu_test|sqduti8wxna5b1vms3kadwewastpsbex";
    //    String parterid_key = "huixin_test|2yz2va00qbt9w4xptdjr5yeomeamuu9z";

    //    正式开始
    //    String parterid_key = "tongcheng_train|x3z5nj8mnvl14nirtwlvhvuialo0akyt";//同程正式

    public String partnerid;

    public String key;

    public String trainorderurl;

    /**
     * 查询url
     */
    //    public String trainsearchurl = "http://tcsearchtrain.hangtian123.net/trainSearch";

    public TongchengTest() {
        super();
        this.partnerid = parterid_key.split("[|]")[0];
        this.key = parterid_key.split("[|]")[1];
        //        this.trainorderurl = "http://trainorder.test.hangtian123.net/cn_interface/tcTrain";
        this.trainorderurl = "http://trainorder.ws.hangtian123.com/cn_interface/tcTrain";
        //火车票订单接口测试地址url
        //        this.trainorderurl = "http://localhost:18080/cn_interface/tcTrain";
    }

    /**
     * 订单的接口地址
     */
    //    public String trainorderurl = "http://trainorder.test.hangtian123.net/cn_interface/tcTrain";
    //    public String trainorderurl = "http://120.26.100.206:49016/cn_interface/tcTrain";
    //    public String trainorderurl = "http://120.26.100.206:28816/cn_interface/tcTrain";

    //    public String trainorderurl = "http://tctrainorder.hangtian123.net/cn_interface/tcTrain";

    //    public String trainorderurl = "http://trainorder.ws.hangtian123.com:28816/cn_interface/tcTrain";

    public final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static void main(String[] args) {
        TongchengTest test = new TongchengTest();
        String resultString = "";
        //        gaotie_manager     key:72d97c2acb5923234b2f80a7dbe8d85c
        try {
            //            String partnerid = "gaotie_manager";
            //            String key = "72d97c2acb5923234b2f80a7dbe8d85c";
            //            System.out.println("partnerid:" + partnerid);
            //            System.out.println("key:" + key);
            //            String sign = ElongHotelInterfaceUtil.MD5(key);
            //            System.out.println("key加密:" + sign);
            //            sign = "gaotie_manager20160106151035580" + sign;
            //            System.out.println("sign加密前:" + sign);
            //            sign = ElongHotelInterfaceUtil.MD5(sign);
            //            System.out.println("sign加密后:" + sign);
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        //        String sign = ElongHotelInterfaceUtil.MD5(this.key);
        //        sign = this.partnerid + time + sign;
        //        sign = ElongHotelInterfaceUtil.MD5(sign);

        //=================4.5. 申请分配座位席别(火车票创建订单)
        //        resultString = test.train_order();
        //                Trainorder trainorder = new Trainorder();
        //                resultString = test.train_orderMobile(orderid, checi, from_station_code, from_station_name, to_station_code,
        //                        to_station_name, train_date, passengers, "15811173432", "15811173432", "wzc", "13521396647", "false",
        //                        "1");
        //=================4.6. 火车票确认出票
        //        resultString = test.train_confirm("20151109164054618_cd_1447058454623", "T1511091640517173963");
        //=================4.7. 取消火车票订单
        //                resultString = test.train_cancel("TC_2015-08-16_CD_1439711829244", getcurrentTimeMillis() + "t");
        //        resultString = test.train_cancel("TC_20150103_cdtest_1420289322312_8_82_39_75", getcurrentTimeMillis() + "t");
        //        resultString = test.train_cancel("TC_20150103_cdtest_1420289322361_19_3_52_5", getcurrentTimeMillis() + "t");
        //        resultString = test.train_cancel("TC_20150103_cdtest_1420289322360_54_6_30_83", getcurrentTimeMillis() + "t");
        //        resultString = test.train_cancel("10817070T45136", "T1510311008258139755");
        //=================
        //        resultString = test.train_query_status("TC_1418792969124");
        //=================4.9. 查询订单详情
        //        resultString = test.train_query_info("767116503530", "");
        //=================
        //=================4.10. 在线退票
        //        Trainorder trainorder = new Trainorder();
        //        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        //        passengers.add(test.getTrainpassenger("王飞", "130682199108203190", 1, "1", 1f, "E882937963111002a", 1));
        //        trainorder.setPassengers(passengers);
        //        String orderid = "10817056T45042";
        //        String transactionid = "T1510291648113535631";
        //        String ordernumber = "E882937963";
        //        String reqtoken = getcurrentTimeMillis() + "";
        //        String callbackurl = "";
        //        resultString = test.return_ticket(orderid, transactionid, ordernumber, reqtoken, callbackurl, trainorder);
        //=================
        //========在线改签
        //        resultString = test.train_request_change();
        //========确认改签
        //        resultString = test.train_confirm_change("TEST_WH_1432543221359", "T1505251639411077311");

        //TODO  测试开始
        //=================批量给同程订单！！！！！！！！！！！！！！！！！！！！
        //                test.testhaoduotongchengdingdan();
        //=================同程批量确认出票
        //        test.tongchengpiliangzhifu(new String[] { "TC_20150103_cdtest_1420287811501_66_25_7_83",
        //                "TC_20150103_cdtest_1420287811481_36_95_57_33", "TC_20150103_cdtest_1420287811498_94_30_49_50",
        //                "TC_20150103_cdtest_1420287811493_37_80_57_80", "TC_20150103_cdtest_1420287811502_65_90_68_8",
        //                "TC_20150103_cdtest_1420287811471_97_49_9_5", "TC_20150103_cdtest_1420287811470_63_82_6_82",
        //                "TC_20150103_cdtest_1420287811476_12_53_31_18", "TC_20150103_cdtest_1420287811493_86_99_94_94",
        //                "TC_20150103_cdtest_1420287811497_81_5_15_24", "TC_20150103_cdtest_1420287811499_27_20_87_18",
        //                "TC_20150103_cdtest_14 20287811495_61_9_96_7", "TC_20150103_cdtest_1420287811471_27_70_56_82",
        //                "TC_20150103_cdtest_1420287811492_79_9_90_50", "TC_20150103_cdtest_1420287811471_58_51_70_66",
        //                "TC_20150103_cdtest_1420287811423_22_0_53_13", "TC_20150103_cdtest_1420287811495_93_4_65_83",
        //                "TC_20150103_cdtest_1420287811470_26_15_17_70", "TC_20150103_cdtest_1420287811478_66_2_78_69",
        //                "TC_20150103_cdtest_1420287811496_74_5_92_59" });
        //        resultString = test.getsign("huaqishangcheng", "train_order", "20150526055935",
        //                "24cgxv34jv7b8zha5ou1o1dmvtcr4f0r");
        //        test.getInterfacedata();
        //        test.editCacheCustomeruser(1, "gaotie_train", "");
        //        test.editCacheCustomeruser(1, "zggj_train", "");
        test.editCacheCustomeruser(1, "zls_train", "");
        //        System.out.println(resultStrinzggj_traing);
    }

    /**
     * 4.5. 申请分配座位席别(火车票创建订单)
     * @return
     * @time 2015年10月23日 下午2:13:50
     * @author chendong
     */
    private String train_order() {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        //        passengers.add(getTrainpassenger("陈新福", "362322198210167517", 1, "1", 1f, "", 1));
        //                passengers.add(getTrainpassenger("陈栋", "412823198909298017", 1, "1", 1f, "", 1));
        //                passengers.add(getTrainpassenger("王成亮", "413026199310010970", 1, "1", 156.5f, "", 1));
        //        passengers.add(getTrainpassenger("王娇", "320830199006242420", 1, "1", 156.5f, "", 1));

        //                passengers.add(getTrainpassenger("殷树斌", "230303199103304015", 1, "1", 1f, "", 1));
        passengers.add(getTrainpassenger("张全贵", "510781198802064130", 1, "1", 1f, "", 1));
        //        passengers.add(getTrainpassenger("罗庆鑫", "232103199703294376", 1, "1", 156.5f, "", 1));
        //        passengers.add(getTrainpassenger("陶蕾", "341222198912259192", 1, "1", 1f, "", 1));
        //"zwname":"二等座","price":"1000.03","piaotypename":"儿童票","zwcode":"O","piaotype":"2","passengersename":"陈娅慧","passporttypeseidname":"二代身份证","passportseno":"361128200806017129","passporttypeseid":"1","cxin":"","passengerid":1
        //        passengers.add(getTrainpassenger("晏凯", "510304198103050536", 1, "1", 1f, "", 1));
        String orderid = "";
        orderid = TimeUtil.gettodaydate(15) + "_cd_" + getcurrentTimeMillis();// "1938927T1T80351413";//
        //        orderid = "192555200284046";
        //        String checi = "K7802";
        //        String stationString = "TYV|太原东|TBV|太原北|K7058";
        String stationString = "HBB|哈尔滨|VBB|哈尔滨东|K7058";
        //        String stationString = "HBB|哈尔滨|VBB|哈尔滨东|K7058";
        String from_station_code = stationString.split("[|]")[0];
        String from_station_name = stationString.split("[|]")[1];
        String to_station_code = stationString.split("[|]")[2];
        String to_station_name = stationString.split("[|]")[3];
        String checi = stationString.split("[|]")[4];
        String train_date = TimeUtil.gettodaydatebyfrontandback(10, 10);
        String LoginUserName = "5446679@qq.com";
        LoginUserName = "";
        String LoginUserPassword = "q1046590633";
        LoginUserPassword = "";
        String cookie = "";
        JSONArray jsonArrayPassengers = getJsonArray(passengers);
        String resultString = train_order(orderid, checi, from_station_code, from_station_name, to_station_code,
                to_station_name, train_date, jsonArrayPassengers, LoginUserName, LoginUserPassword, cookie);
        return resultString;
    }

    /**
     * 
     * @time 2015年10月23日 下午3:50:24
     * @author chendong
     * @param passengers 
     * @return 
     */
    private JSONArray getJsonArray(List<Trainpassenger> passengers) {
        JSONArray jsonArrayPassengers = new JSONArray();
        for (int i = 0; i < passengers.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            Trainpassenger trainpassenger = passengers.get(i);
            Trainticket trainticket = passengers.get(i).getTraintickets().get(0);
            String passporttypeseid = "1";
            String passporttypeseidname = "二代身份证";
            String zwcode = trainticket.getSeattype();
            String zwname = getzwcode(zwcode);
            Float price = trainticket.getPrice();
            int piaotype = trainticket.getTickettype();
            String piaotypename = trainticket.getTickettypestr();
            jsonObject.put("passengerid", i);
            jsonObject.put("ticket_no", "");
            jsonObject.put("passengersename", trainpassenger.getName());
            jsonObject.put("passportseno", trainpassenger.getIdnumber());
            jsonObject.put("passporttypeseid", passporttypeseid);
            jsonObject.put("passporttypeseidname", passporttypeseidname);
            jsonObject.put("piaotype", piaotype);
            jsonObject.put("piaotypename", piaotypename);
            jsonObject.put("zwcode", zwcode);
            jsonObject.put("zwname", zwname);
            jsonObject.put("identitystatusid", "315");

            jsonObject.put("cxin", "");
            jsonObject.put("price", price);
            //            passengerJSON += "{\"passengerid\":\"" + i + "\",\"ticket_no\":\"\",\"passengersename\":\""
            //                    + trainpassenger.getName() + "\",\"passportseno\":\"" + trainpassenger.getIdnumber()
            //                    + "\",\"passporttypeseid\":\"" + passporttypeseid + "\",\"passporttypeseidname\":\""
            //                    + passporttypeseidname + "\",\"piaotype\":\"" + piaotype + "\",\"piaotypename\":\"" + piaotypename
            //                    + "\",\"zwcode\":\"" + zwcode + "\",\"zwname\":\"" + zwname + "\",\"cxin\":\"\",\"price\":\""
            //                    + price + "\"}";
            jsonArrayPassengers.add(jsonObject);
        }
        return jsonArrayPassengers;

    }

    /**
     * 对账号缓存做操作
     * 
     * @param methodtype 1 (updateInterfaceAccount更新接口账号的话就做移出操作)
     * @param partnerid
     * @param sign
     * @time 2015年7月31日 上午10:52:17
     * @author chendong
     */
    private void editCacheCustomeruser(int methodtype, String partnerid, String key) {
        String method = "updateInterfaceAccount";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        if (methodtype == 1) {
            method = "updateInterfaceAccount";
        }
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("partnerid", partnerid);
        jsonobject.put("method", method);
        jsonobject.put("reqtime", reqtime);
        jsonobject.put("sign", sign);
        String paramContent = "jsonStr=" + jsonobject.toJSONString();
        String resultString = "";
        resultString = SendPostandGet.submitPost(trainorderurl, paramContent, "utf-8").toString();
        System.out.println("======" + resultString);
    }

    /**
     * 同程测试批量下单
     * 
     * @time 2014年12月28日 下午7:34:54
     * @author chendong
     */
    public void testhaoduotongchengdingdan() {
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(80);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!姓名,证件号,证件类型,座位编码,价格,票号,乘客类型
        // 关闭线程池
        pool.shutdown();
    }

    public void tongchengpiliangtuipiao() {

    }

    private void executepool(String passengerstring, String checi, String from_station_code, String from_station_name,
            String to_station_code, String to_station_name, String train_date, Thread t1, ExecutorService pool) {
        Long l1 = System.currentTimeMillis();

        String orderid = "TC_20150103_cdtest_" + l1 + "_" + new Random().nextInt(100) + "_" + new Random().nextInt(100)
                + "_" + new Random().nextInt(100) + "_" + new Random().nextInt(100);
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        String[] passs = passengerstring.split("[|]");
        for (int i = 0; i < passs.length; i++) {
            String passengerstringss = passs[i];
            String[] passengerstringssss = passengerstringss.split(",");
            passengers.add(getTrainpassenger(passengerstringssss[0], passengerstringssss[1],
                    Integer.parseInt(passengerstringssss[2]), passengerstringssss[3],
                    Float.parseFloat(passengerstringssss[4]), passengerstringssss[5],
                    Integer.parseInt(passengerstringssss[6])));
        }
        new CreateTongchengTrainOrderThread(partnerid, key, trainorderurl, orderid, checi, from_station_code,
                from_station_name, to_station_code, to_station_name, train_date, passengers).run();
        //        pool.execute(t1);

    }

    public void tongchengpiliangzhifu(String[] orderidarray) {
        for (int i = 0; i < orderidarray.length; i++) {
            train_confirm(orderidarray[i], "zf" + System.currentTimeMillis());
        }
    }

    /**
     * 
     * @param passengers
     * @param checi
     * @param from_station_code
     * @param from_station_name
     * @param to_station_code
     * @param to_station_name
     * @param train_date
     * @return
     * @time 2014年12月27日 下午8:02:52
     * @author chendong
     */
    public Trainorder getTrainorder(List<Trainpassenger> passengers, String checi, String from_station_code,
            String from_station_name, String to_station_code, String to_station_name, String train_date) {
        Trainorder trainorder = new Trainorder();
        //        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        //        passengers.add(test.getTrainpassenger("陈新福", "362322198210167517", 1, "1", 1f, "", 1));
        //        passengers.add(test.getTrainpassenger("王战朝", "410883199006281010", 1, "1", 1f, "EC509466651010012", 1));

        String orderid = "TC_20141227_" + getcurrentTimeMillis();
        trainorder.setQunarOrdernumber(orderid);
        Trainticket trainticket = new Trainticket();
        //        String checi = "6801";
        trainticket.setTrainno(checi);

        //        String from_station_code = "TYV";
        trainticket.setDeparture(from_station_code);
        //        String from_station_name = "太原";
        //        String to_station_code = "TBV";
        trainticket.setArrival(to_station_code);
        //        String to_station_name = "太原北";
        //        String train_date = "2015-02-01";
        trainticket.setDeparttime(train_date);
        passengers.get(0).setTrainticket(trainticket);
        trainorder.setPassengers(passengers);
        return trainorder;
    }

    /**
     * 
     * 姓名,证件号,证件类型,座位编码,价格,乘客类型
     * @param name 姓名
     * @param idnumber 证件号
     * @param idtype 证件类型 1:"二代身份证"; 3:"护照";4:"港澳通行证";5: "台湾通行证";
     * @param seattype 座位编码。与座位名称对应关系：9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
     *                  4:软卧，3:硬卧，2:软座，1:硬座 注意：当最低的一种座位，无票时，购买选择该座位种类， 
     *                  买下的就是无座(也就说买无座的席别编码就是该车次的 最低席别的编码)，另外，当最低席别的票卖完了的时候 才可以卖无座的票。
     * @param price 价格
     * @param ticketno 票号
     * @param tickettype 乘客类型 1:成人票，2:儿童票，3:学生票，4:残军票
     * @return
     * @time 2014年12月27日 上午11:30:35
     * @author chendong
     */
    private Trainpassenger getTrainpassenger(String name, String idnumber, int idtype, String seattype, Float price,
            String ticketno, int tickettype) {
        Trainpassenger Trainpassenger = new Trainpassenger();
        Trainpassenger.setName(name);
        Trainpassenger.setIdnumber(idnumber);
        Trainpassenger.setIdtype(idtype);// 1:"二代身份证"; 3:"护照";4:"港澳通行证";5: "台湾通行证";
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
     * 4.1. 查询账户余额
     * 
     * 
     * @time 2014年12月26日10:28:45
     * @author chendong
     */
    private String query_money() {
        String method = "query_money";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        //        String passengerJSON = "{\"passengerid\":\"1\",\"ticket_no\":\"\",\"passengersename\":\"陈栋\",\"passportseno\":\"412823198909298017\",\"passporttypeseid\":\"1\",\"passporttypeseidname\":\"二代身份证\",\"piaotype\":\"1\",\"piaotypename\":\"\",\"zwcode\":\"3\",\"zwname\":\"硬卧\",\"cxin\":\"\",\"price\":\"488.5\"},{\"passengerid\":\"1\",\"ticket_no\":\"\",\"passengersename\":\"王战朝\",\"passportseno\":\"410883199006281010\",\"passporttypeseid\":\"1\",\"passporttypeseidname\":\"二代身份证\",\"piaotype\":\"1\",\"piaotypename\":\"\",\"zwcode\":\"3\",\"zwname\":\"硬卧\",\"cxin\":\"\",\"price\":\"488.5\"}";
        String passengerJSON = "";
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\"}";
        System.out.println(jsonStr);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
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
     */
    private String train_orderMobile(String orderid, String checi, String from_station_code, String from_station_name,
            String to_station_code, String to_station_name, String train_date, List<Trainpassenger> passengers,
            String mobile, String loginpassword, String contactusername, String contacttel, String mailflag,
            String insurtypeid) {
        String method = "train_order";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        //        String passengerJSON = "{\"passengerid\":\"1\",\"ticket_no\":\"\",\"passengersename\":\"陈栋\",\"passportseno\":\"412823198909298017\",\"passporttypeseid\":\"1\",\"passporttypeseidname\":\"二代身份证\",\"piaotype\":\"1\",\"piaotypename\":\"\",\"zwcode\":\"3\",\"zwname\":\"硬卧\",\"cxin\":\"\",\"price\":\"488.5\"},{\"passengerid\":\"1\",\"ticket_no\":\"\",\"passengersename\":\"王战朝\",\"passportseno\":\"410883199006281010\",\"passporttypeseid\":\"1\",\"passporttypeseidname\":\"二代身份证\",\"piaotype\":\"1\",\"piaotypename\":\"\",\"zwcode\":\"3\",\"zwname\":\"硬卧\",\"cxin\":\"\",\"price\":\"488.5\"}";
        String passengerJSON = "";
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
            passengerJSON += "{\"passengerid\":\"" + i + "\",\"ticket_no\":\"\",\"passengersename\":\""
                    + trainpassenger.getName() + "\",\"passportseno\":\"" + trainpassenger.getIdnumber()
                    + "\",\"passporttypeseid\":\"" + passporttypeseid + "\",\"passporttypeseidname\":\""
                    + passporttypeseidname + "\",\"piaotype\":\"" + piaotype + "\",\"piaotypename\":\"" + piaotypename
                    + "\",\"zwcode\":\"" + zwcode + "\",\"zwname\":\"" + zwname + "\",\"cxin\":\"\",\"price\":\""
                    + price + "\"}";
            if (i < passengers.size() - 1) {
                passengerJSON += ",";
            }
        }
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"checi\":\"" + checi
                + "\",\"from_station_code\":\"" + from_station_code + "\",\"from_station_name\":\"" + from_station_name
                + "\",\"to_station_code\":\"" + to_station_code + "\",\"to_station_name\":\"" + to_station_name
                + "\",\"train_date\":\"" + train_date + "\",\"passengers\":[" + passengerJSON
                + "],\"callbackurl\":\"http://\",'mobile':'" + mobile + "','loginpassword':'" + loginpassword
                + "','contactusername':'" + contactusername + "','contacttel':'" + contacttel + "','mailflag':'"
                + mailflag + "','insurtypeid':'" + insurtypeid
                + "','mailname':'wzc','mailtel':'15811073432','mailcode':'5982156','mailaddress':'北京石景山区'}";
        Long temp1 = System.currentTimeMillis();
        System.out.println("test请求开始:" + "jsonStr=" + jsonStr);
        System.out.println("test请求开始:" + trainorderurl);
        String resultString = "";
        resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("test请求结束到开始时间差:" + (System.currentTimeMillis() - temp1));
        return resultString;
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
     */
    private String train_order(String orderid, String checi, String from_station_code, String from_station_name,
            String to_station_code, String to_station_name, String train_date, JSONArray jsonArrayPassengers,
            String LoginUserName, String LoginUserPassword, String cookie) {
        JSONObject jsonObject_jsonStr = new JSONObject();
        String method = "train_order";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        jsonObject_jsonStr.put("passengers", jsonArrayPassengers);
        jsonObject_jsonStr.put("partnerid", partnerid);
        jsonObject_jsonStr.put("method", method);
        jsonObject_jsonStr.put("reqtime", reqtime);
        jsonObject_jsonStr.put("sign", sign);
        jsonObject_jsonStr.put("orderid", orderid);
        jsonObject_jsonStr.put("checi", checi);
        jsonObject_jsonStr.put("from_station_code", from_station_code);
        jsonObject_jsonStr.put("from_station_name", from_station_name);
        jsonObject_jsonStr.put("to_station_code", to_station_code);
        jsonObject_jsonStr.put("to_station_name", to_station_name);
        jsonObject_jsonStr.put("train_date", train_date);
        jsonObject_jsonStr.put("LoginUserName", LoginUserName);
        jsonObject_jsonStr.put("LoginUserPassword", LoginUserPassword);
        jsonObject_jsonStr.put("cookie", cookie);
        jsonObject_jsonStr.put("callbackurl", "http");

        Long temp1 = System.currentTimeMillis();
        String paramContent = "jsonStr=" + jsonObject_jsonStr.toJSONString();
        System.out.println("test请求开始:" + paramContent);
        System.out.println("test请求开始:" + trainorderurl);
        String resultString = "";
        resultString = SendPostandGet.submitPost(trainorderurl, paramContent, "UTF-8").toString();
        System.out.println("test请求结束到开始时间差:" + (System.currentTimeMillis() - temp1));
        return resultString;
    }

    /**
     * 4.6. 火车票确认出票
     * 
     * @param orderid
     * @param transactionid
     * @return
     * @time 2014年12月12日 下午3:38:59
     * @author chendong
     */
    private String train_confirm(String orderid, String transactionid) {
        String method = "train_confirm";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String LoginUserName = "cd198992901";
        String LoginUserPassword = "nicaicai1";
        String cookie = "";
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\",\"LoginUserName\":\"" + LoginUserName + "\",\"LoginUserPassword\":\"" + LoginUserPassword
                + "\",\"cookie\":\"" + cookie + "\"}";
        System.out.println("jsonStr=" + jsonStr);
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        System.out.println("resultString=" + resultString);
        return resultString;
    }

    /**
     * 4.7. 取消火车票订单
     * 
     * @param orderid
     * @param transactionid
     * @return
     * @time 2014年12月12日 下午3:42:08
     * @author chendong
     */
    private String train_cancel(String orderid, String transactionid) {
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
     * 4.8. 查询订单状态
     * 
     * @param orderid
     * @return
     * @time 2014年12月12日 下午3:44:57
     * @author chendong
     */
    private String train_query_status(String orderid) {
        String method = "train_query_status";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\"}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    /**
     * 4.9. 查询订单详情
     * 
     * @param orderid
     * @param transactionid
     * @return
     * @time 2014年12月12日 下午3:45:30
     * @author chendong
     */
    private String train_query_info(String orderid, String transactionid) {
        String method = "train_query_info";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    /**
     * 4.10. 在线退票
     * 
     * @param orderid
     * @param transactionid
     * @param ordernumber
     * @param reqtoken
     * @param callbackurl
     * @param trainorder
     * @return
     * @time 2014年12月12日 下午3:48:55
     * @author chendong
     */
    private String return_ticket(String orderid, String transactionid, String ordernumber, String reqtoken,
            String callbackurl, Trainorder trainorder) {
        String method = "return_ticket";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String passengerJSON = "";
        for (int i = 0; i < trainorder.getPassengers().size(); i++) {
            Trainpassenger trainpassenger = trainorder.getPassengers().get(i);

            passengerJSON += "{\"ticket_no\":\"" + trainpassenger.getTraintickets().get(0).getTicketno()
                    + "\",\"passengername\":\"" + trainpassenger.getName() + "\",\"passportseno\":\""
                    + trainpassenger.getIdnumber() + "\",\"passporttypeseid\":\"1\"}";
            if (i < trainorder.getPassengers().size() - 1) {
                passengerJSON += ",";
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
     * 4.12.请求改签
     * time 2014年12月12日 下午2:46:18
     * @author 路平
     */
    private String train_request_change() {
        String method = "train_request_change";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);

        JSONObject reqobj = new JSONObject();

        reqobj.put("reqtime", reqtime);
        reqobj.put("sign", sign);
        reqobj.put("method", method);
        reqobj.put("partnerid", partnerid);
        reqobj.put("LoginUserName", "tanchue7e9");
        reqobj.put("LoginUserPassword", "asd123456");

        reqobj.put("orderid", "20151105203232034_cd_1446726752039");
        reqobj.put("ordernumber", "EC27229494");
        reqobj.put("transactionid", "T1511052032245806719");
        reqobj.put("change_checi", "K7058");
        reqobj.put("change_datetime", "2015-11-28 14:13:00");
        reqobj.put("change_zwcode", "1");
        reqobj.put("old_zwcode", "1");
        JSONArray ticketinfo = new JSONArray();

        JSONObject a = new JSONObject();
        a.put("passengersename", "黄骅");
        a.put("passporttypeseid", "1");
        a.put("passportseno", "21100419761123631X");
        a.put("piaotype", "1");
        a.put("old_ticket_no", "EC272294941020012");
        ticketinfo.add(a);

        reqobj.put("ticketinfo", ticketinfo);
        reqobj.put("isasync", "Y");
        reqobj.put("reqtoken", System.currentTimeMillis());
        reqobj.put("callbackurl",
                "http://192.168.0.107:9001/cn_interface/CallBackChangeTestServlet?" + reqobj.getLongValue("reqtoken"));

        return SendPostandGet.submitPost(trainorderurl, "jsonStr=" + reqobj.toJSONString(), "UTF-8").toString();
    }

    /**
     * 4.13.取消改签
     * time 2014年12月12日 下午2:46:18
     * @author 路平
     */
    private String train_cancel_change(String orderid, String transactionid) {
        String method = "train_cancel_change";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"tongcheng_train\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    /**
     * 4.13.确认改签
     * time 2014年12月12日 下午2:46:18
     * @author 路平
     */
    private String train_confirm_change(String orderid, String transactionid) {
        String method = "train_confirm_change";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject req = new JSONObject();
        req.put("partnerid", partnerid);
        req.put("method", method);
        req.put("reqtime", reqtime);
        req.put("sign", sign);
        req.put("orderid", orderid);
        req.put("transactionid", transactionid);
        req.put("isasync", "Y");
        req.put("reqtoken", System.currentTimeMillis());
        req.put("callbackurl", "http://localhost:9004/cn_interface/tcCallBack?" + req.getLongValue("reqtoken"));
        return SendPostandGet.submitPost(trainorderurl, "jsonStr=" + req.toJSONString(), "UTF-8").toString();
    }

    /**
     * 4.16.确认出票回调通知
     * time 2014年12月12日 下午2:46:18
     * @author 路平
     */
    private String train_pay_callback(String orderid, String transactionid) {
        String method = "train_pay_callback";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"tongcheng_train\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\"}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    /**
     * 4.10.4.退票回调通知
     * time 2014年12月12日 下午2:46:18
     * @author 路平
     */
    private String train_refun_callback(String orderid, String transactionid, String returntype, String reqtoken,
            String passengerid, String ticket_no, String passengersename, String passportseno, String passporttypeseid,
            String returnsuccess, String returnmoney, String returntime, String returnfailid, String returnfailmsg) {
        String method = "train_refun_callback";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"tongcheng_train\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + orderid + "\",\"transactionid\":\"" + transactionid
                + "\",\"returntype\":\"" + returntype + "\",\"reqtoken\":\"" + reqtoken
                + "\",\"returntickets\":[{\"passengerid\":\"" + passengerid + "\",\"ticket_no\":\"" + ticket_no
                + "\",\"passengersename\":\"" + passengersename + "\",\"passportseno\":\"" + passportseno
                + "\",\"passporttypeseid\":\"" + passporttypeseid + "\",\"returnsuccess\":\"" + returnsuccess
                + "\",\"returnmoney\":\"" + returnmoney + "\",\"returntime\":\"" + returntime
                + "\",\"returnfailid\":\"" + returnfailid + "\",\"returnfailmsg\":\"" + returnfailmsg + "\"}]}";
        String resultString = SendPostandGet.submitPost(trainorderurl, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     * 
     * @time 2014年12月12日 下午2:44:31
     * @author chendong
     */
    public static String getsign(String partnerid, String method, String reqtime, String key) {
        System.out.println("=========key加密前=========");
        System.out.println(key);
        System.out.println("=========key加密后=========");
        key = MD5Util.MD5Encode(key, "UTF-8");
        System.out.println(key);
        String jiamiqian = partnerid + method + reqtime + key;
        System.out.println("=========sign加密前=========");
        System.out.println(jiamiqian);
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
        System.out.println("=========sign加密后=========");
        System.out.println(sign);
        return sign;
    }

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getcurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }

    private void executepool(Thread t1, ExecutorService pool) {
        Long l1 = System.currentTimeMillis();
        String orderid = "TC_20150102_cdtest:" + l1 + ":" + new Random().nextInt(1000000);
        t1 = new TestThread(orderid);
        pool.execute(t1);
    }

}
