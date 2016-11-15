package com.ccservice.b2b2c.atom.servlet.yl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 艺龙 订单推送方法
 * 
 * @time 2015年12月8日 上午11:36:31
 * @author chendong
 */
public class YiLongTrainOrderMethod extends TrainSelectLoginWay {
    public static void main(String[] args) {
        //        JSONArray jsonArray = new JSONArray();
        //        JSONObject jsonObject = new JSONObject();
        //        jsonObject.put("0", 10);
        //        jsonArray.add(jsonObject);
        //        System.out.println(jsonArray.toJSONString());
        //        JSONObject jsonObject = JSONObject.parseObject("[ss,ss]");
        YiLongTrainOrderMethod YiLongTrainOrderMethod = new YiLongTrainOrderMethod();
        System.out.println(YiLongTrainOrderMethod.getzwnameByYlseatType("15"));
    }

    /**
     * 
     * @param paramJson
     * @param type  9 YILONG1   先占座模式[艺龙] 10 YILONG2   先支付模式就是黄牛模式[艺龙]
     * @time 2015年12月8日 下午12:10:39
     * @author chendong
     * @param merchantId 
     * @return 
     */
    public String createYlTrainOrder(String paramJson, int interfacetype, String merchantId) {
        String result = "";
        JSONObject jsonObjectresult = new JSONObject();

        if (paramJson == null) {
            jsonObjectresult.put("retcode", "401");
            jsonObjectresult.put("retdesc", "paramJson err");
        }
        else if (merchantId == null) {
            jsonObjectresult.put("retcode", "401");
            jsonObjectresult.put("retdesc", "merchantId err");
        }
        else {
            JSONObject jsonObject = JSONObject.parseObject(paramJson);
            String orderId = jsonObject.getString("orderId");//订单号
            Trainorder trainorder = new Trainorder();
            trainorder.setInterfacetype(interfacetype);// 接口类型
            trainorder.setOrdertype(1);
            String agentid = gettongchengagentid(merchantId);
            trainorder.setAgentid(Long.parseLong(agentid));// 代理ID
            trainorder.setOrderstatus(Trainorder.WAITPAY);
            trainorder.setAgentprofit(0f);// 采购利润
            trainorder.setCommission(0f);
            trainorder.setCreateuser("接口");
            trainorder.setPaymethod(4);
            String acceptStand = jsonObject.getString("acceptStand");//1 代表接受站票
            String contactMobile = jsonObject.getString("contactMobile");//联系人手机号
            trainorder.setContacttel(contactMobile);
            String contactName = jsonObject.getString("contactName");//联系人姓名
            trainorder.setContactuser(contactName);
            //String orderDate = jsonObject.getString("orderDate");//下单时间
            if ((jsonObject.getString("orderDate") != null) || (!("".equals(jsonObject.getString("orderDate"))))) {
                Timestamp orderDate = Timestamp.valueOf(jsonObject.getString("orderDate"));
                trainorder.setCreatetime(orderDate);
            }
            trainorder.setQunarOrdernumber(orderId);
            Float ticketPrice = jsonObject.getFloat("ticketPrice");//票价
            List<Trainpassenger> passengers = gettrainpassenger(jsonObject);
            trainorder.setPassengers(passengers);
            Float orderprice = passengers.size() * ticketPrice;

            trainorder.setOrderprice(orderprice);
            trainorder.setSupplyprice(orderprice);
            trainorder.setState12306(1);//12306状态--等待下单
            trainorder.setChangesupplytradeno("");
            String sqlTemp = "SELECT top 1 tor.ID ID from T_TRAINORDER as tor with(nolock) "
                    + "where tor.C_QUNARORDERNUMBER='" + orderId + "'";
            WriteLog.write("艺龙下单log记不记", "paramJson：" + paramJson + "|interfacetype" + interfacetype + "|merchantId:"
                    + merchantId);
            List list1 = Server.getInstance().getSystemService().findMapResultBySql(sqlTemp, null);

            WriteLog.write("艺龙下单log记不记", sqlTemp + "sqlTemp" + "list1.size()" + list1.size());
            if (list1.size() > 0) {

                WriteLog.write("艺龙下单log记不记", "list1" + list1);
                if (interfacetype == 10) {
                    jsonObjectresult.put("retcode", "0");
                    jsonObjectresult.put("retdesc", "成功");
                }
                else {
                    jsonObjectresult.put("retcode", "200");
                    jsonObjectresult.put("retdesc", "成功");
                }
            }
            else {
                trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                if (trainorder.getId() > 0) {
                    WriteLog.write("艺龙下单log记不记", "进插入了！！！" + "trainorder：" + trainorder);
                    insertYLSeatCode(trainorder, jsonObject);
                    createTrainOrderExtSeat(trainorder.getId(), acceptStand, ticketPrice);
                    toActiveMQroordering(passengers, trainorder);
                    //                    activeMQroordering(trainorder.getId());
                    jsonObjectresult.put("retcode", "0");
                    jsonObjectresult.put("retdesc", "成功");
                    if (interfacetype == 10) {
                        jsonObjectresult.put("retcode", "0");
                        jsonObjectresult.put("retdesc", "成功");
                    }
                    else {
                        jsonObjectresult.put("retcode", "200");
                        jsonObjectresult.put("retdesc", "成功");
                    }
                }
                else {
                    jsonObjectresult.put("retcode", "400");
                    jsonObjectresult.put("retdesc", "系统错误");
                }
            }
            result = jsonObjectresult.toJSONString();
        }
        WriteLog.write("艺龙下单log记不记", "最后返回值" + result);
        return result;

    }

    private void toActiveMQroordering(List<Trainpassenger> passengers, Trainorder trainorder) {
        boolean isStandingSeat = false;
        out: for (Trainpassenger trainpassenger : passengers) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                if (trainticket.getSeattype().contains("无座")) {
                    new YiLongTrainOrderMethodNoSeat(trainticket, trainorder).start();
                    isStandingSeat = true;
                    break out;
                }
            }
        }
        if (!isStandingSeat) {
            activeMQroordering(trainorder.getId());
        }
    }

    /**
     * 根据艺龙传过来的json获取 乘客的对象list
     * @param jsonObject
     * @return
     * @time 2015年12月8日 下午1:27:08
     * @author chendong
     */
    private List<Trainpassenger> gettrainpassenger(JSONObject jsonObject) {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();

        String dptStation = jsonObject.getString("arrStation");//"北京南", 始发站名
        String arrStation = jsonObject.getString("dptStation");//到达站名
        //坐席类型 0 站票 1 硬座  2 软座  3 硬卧  4 软卧  5 高级软卧  6 一等软座  7 二等软座  8 商务座  
        //        9 一等座  10  二等座  11  特等座  12  观光座  13  特等软座  14  一人软包  15  动软 16 高级动软
        String seatType = jsonObject.getString("seatType");
        String zwname = getzwnameByYlseatType(seatType);
        String trainEndTime = jsonObject.getString("trainEndTime");//到站时间    "trainEndTime": "2014-08-09 16:17:00",        到站时间
        String trainNo = jsonObject.getString("trainNo");//车次
        String trainStartTime = jsonObject.getString("trainStartTime");//发车时间
        Float ticketPrice = jsonObject.getFloat("ticketPrice");//票价
        JSONArray passengersJsonArray = jsonObject.getJSONArray("passengers");//乘客信息列表
        for (int i = 0; i < passengersJsonArray.size(); i++) {
            JSONObject passenger = passengersJsonArray.getJSONObject(i);
            //    "certNo": "370724198704210751",     证件号
            //    "certType": "1",                      证件类型
            //    "name": "张玉朋",                    姓名
            //    "orderItemId": "20140807212402186",  票item号
            //    "ticketType": "2"                      票类型
            String certNo = passenger.getString("certNo");//证件号
            String certType = passenger.getString("certType");//证件类型 1 身份证 C 港澳通行证 G 台湾通行证 B 护照

            String name = passenger.getString("name");//姓名
            String orderItemId = passenger.getString("orderItemId");//票item号
            String ticketType = passenger.getString("ticketType");//票类型  票种类型  0 儿童票 1 成人票 2 学生票

            Trainpassenger trainpassenger = new Trainpassenger();
            Trainticket ticket = new Trainticket();// 票
            trainpassenger.setName(name);
            trainpassenger.setIdnumber(certNo);

            if (certNo.length() > 14) {
                trainpassenger.setBirthday(certNo.substring(6, 14));
            }
            else {
                trainpassenger.setBirthday("");
            }
            trainpassenger.setIdtype(getIdtype12306ByYilongticketType(certType));
            trainpassenger.setAduitstatus(0);
            trainpassenger.setChangeid(0);
            trainpassenger.setPassengerid(orderItemId);

            // 票信息
            ticket.setTrainno(trainNo);//车次
            ticket.setPrice(ticketPrice);
            ticket.setPayprice(ticketPrice);

            ticket.setSeattype(zwname);
            ticket.setArrival(dptStation);// 到达站名称
            ticket.setTcseatno("0");

            if (trainStartTime != null) {
                ticket.setDeparttime(trainStartTime);//出发日期
            }
            if (trainEndTime != null) {
                ticket.setArrivaltime(trainEndTime);//到站时间
            }
            ticket.setDeparture(arrStation);
            ticket.setCosttime("");// 历时
            ticket.setStatus(Trainticket.WAITPAY);
            ticket.setInsurprice(0f);// 采购支付
            ticket.setInsurorigprice(0f);// 保险
            ticket.setInsurenum(0);
            Integer piaotype = getPiaoTypeByticketType(ticketType);
            ticket.setTickettype(piaotype);//1:成人票，2:儿童票，3:学生票，4:残军票
            List<Trainticket> tickets = new ArrayList<Trainticket>();
            tickets.add(ticket);
            trainpassenger.setTraintickets(tickets);
            passengers.add(trainpassenger);
        }
        return passengers;
    }

    /**
     * 
     * @param ticketType 票类型  票种类型  0 儿童票 1 成人票 2 学生票
     * @time 2015年12月8日 下午2:03:53
     * @author chendong
     * @return //1:成人票，2:儿童票，3:学生票，4:残军票
     */
    private Integer getPiaoTypeByticketType(String ticketType) {
        Integer piaotype = 1;
        if ("0".equals(ticketType)) {
            piaotype = 2;
        }
        else if ("1".equals(ticketType)) {
            piaotype = 1;

        }
        else if ("2".equals(ticketType)) {
            piaotype = 3;
        }
        return piaotype;
    }

    /**
    * 根据艺龙座位type 返回座位name
    * @time 2015年12月8日 下午1:44:19
    * @author chendong
     * @param seatType 
     * //坐席类型 0 站票 1 硬座  2 软座  3 硬卧  4 软卧  5 高级软卧  6 一等软座  7 二等软座  8 商务座  
                 9 一等座  10  二等座  11  特等座  12  观光座  13  特等软座  14  一人软包  15  动软  16 高级动软
     * @return 
    */
    private String getzwnameByYlseatType(String seatType) {
        String str = "";
        if ("15".equals(seatType)) {
            str = "软卧";
        }
        else if ("16".equals(seatType)) {
            str = "高级软卧";
        }
        else if ("14".equals(seatType)) {
            str = "一人软包";
        }
        else if ("13".equals(seatType)) {
            str = "特等软座";
        }
        else if ("12".equals(seatType)) {
            str = "观光座";
        }
        else if ("11".equals(seatType)) {
            str = "特等座";
        }
        else if ("10".equals(seatType)) {
            str = "二等座";
        }
        else if ("9".equals(seatType)) {
            str = "一等座";
        }
        else if ("8".equals(seatType)) {
            str = "商务座";
        }
        else if ("7".equals(seatType)) {
            str = "二等软座";
        }
        else if ("6".equals(seatType)) {
            str = "一等软座";
        }
        else if ("5".equals(seatType)) {
            str = "高级软卧";
        }
        else if ("4".equals(seatType)) {
            str = "软卧";
        }
        else if ("3".equals(seatType)) {
            str = "硬卧";
        }
        else if ("2".equals(seatType)) {
            str = "软座";
        }
        else if ("1".equals(seatType)) {
            str = "硬座";
        }
        else if ("0".equals(seatType)) {
            str = "无座";
        }
        return str;
    }

    /**
     * 
     * @param ticketType 1 身份证 C 港澳通行证 G 台湾通行证 B 护照
     * @return
     * @time 2015年12月8日 下午1:35:54
     * @author chendong
     */
    private int getIdtype12306ByYilongticketType(String certType) {
        if ("1".equals(certType)) {
            return 1;
        }
        else if ("B".equals(certType)) {
            return 3;
        }
        else if ("C".equals(certType)) {
            return 4;
        }
        else if ("G".equals(certType)) {
            return 5;
        }
        else {
            return 0;
        }
    }

    /**
     * 是否接受站票
     * 
     * @param orderid
     * @param acceptStand 
     * @param ticketPrice  1 代表接受站票
     */
    private void createTrainOrderExtSeat(long orderid, String acceptStand, Float ticketPrice) {
        if ("1".equals(acceptStand)) {
            try {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("0", ticketPrice);
                jsonArray.add(jsonObject);
                String extseats = jsonArray.toJSONString();//[{"5":389},{"6":428}]
                String sql = "INSERT INTO TrainOrderExtSeat (OrderId ,ExtSeat ,ReMark) VALUES ( " + orderid + ",'"
                        + extseats + "' ,'')";
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
            catch (Exception e) {
                WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", orderid + "");
                ExceptionUtil.writelogByException("Error_MyThreadQunarOrder_createTrainOrderExtSeat", e);
            }
        }
    }

    //{"acceptStand":"1", //1 代表接受站票
    // "arrStation": "北京南",                        始发站名
    // "contactMobile": "13787484435",             联系人手机号
    // "contactName": "自动化",                    联系人姓名
    // "dptStation": "杭州",                          到达站名
    // "orderDate": "2014-08-07 10:52:05",            下单时间
    // "orderId": "20140807212402185",              订单号
    // "passengers": [                               乘客信息列表
    //  {
    //    "certNo": "370724198704210751",     证件号
    //    "certType": "1",                      证件类型
    //    "name": "张玉朋",                    姓名
    //    "orderItemId": "20140807212402186",  票item号
    //    "ticketType": "2"                      票类型
    //  },
    //  {
    //    "certNo": "46000319871214184X",
    //    "certType": "1",
    //    "name": "李月华",
    //    "orderItemId": "20140807212402187",
    //    "ticketType": "2"
    //  }
    //  ],
    // "seatType": "10",                             坐席类型
    // "ticketPrice": 629,                            票价
    // "trainEndTime": "2014-08-09 16:17:00",        到站时间
    // "trainNo": "G41",                             车次
    // "trainStartTime": "2014-08-09 09:33:00"        发车时间
    //}

    /**
     * 将艺龙的坐席存入DB
     * 
     * @param trainorder
     * @param jsonObject
     * @time 2016年6月21日 下午5:54:28
     * @author fiend
     */
    private void insertYLSeatCode(Trainorder trainorder, JSONObject jsonObject) {
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            JSONArray passengersJsonArray = jsonObject.getJSONArray("passengers");//乘客信息列表
            for (int i = 0; i < passengersJsonArray.size(); i++) {
                JSONObject passenger = passengersJsonArray.getJSONObject(i);
                if (trainpassenger.getPassengerid().equals(passenger.getString("orderItemId"))) {
                    String sql = " sp_YiLongTicketSeatCode_Insert @TicketId="
                            + trainpassenger.getTraintickets().get(0).getId() + " ,@SeatCode='"
                            + jsonObject.getString("seatType") + "'";
                    WriteLog.write("YiLongTrainOrderMethod_insertYLSeatCode", trainorder.getId() + "--->" + sql);
                    try {
                        Server.getInstance().getSystemService().findMapResultByProcedure(sql);
                    }
                    catch (Exception e) {
                        ExceptionUtil.writelogByException("YiLongTrainOrderMethod_insertYLSeatCode_error", e);
                    }
                }
            }
        }
    }
}
