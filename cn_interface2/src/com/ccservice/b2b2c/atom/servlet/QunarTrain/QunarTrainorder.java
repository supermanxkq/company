package com.ccservice.b2b2c.atom.servlet.QunarTrain;

import java.util.ArrayList;
import java.util.List;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class QunarTrainorder extends QunarSupplyMethod {

    private static Train train = new Train();

    public String submittrainorder(int r1, String orderNo, String agentid, String trainNo, String from_station_code,
            String from, String to_station_code, String to, String date, String retUrl, String reqtoken,
            JSONArray jsons, String extSeat) {
        float totalprice = 0f;
        Customeruser customeruser = new Customeruser();//用户表
        Trainorder trainorder = new Trainorder();//火车订单表
        JSONObject jsonstr = new JSONObject();
        WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":from_station_code:" + from_station_code + ":to_station_code:"
                + to_station_code);
        WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":from:" + from + ":to:" + to + ":" + to_station_code + ":" + date + ":"
                + trainNo);
        if (from_station_code.equals("") || to_station_code.equals("")) {
            try {
                from_station_code = Train12306StationInfoUtil.getThreeByName(from);
                to_station_code = Train12306StationInfoUtil.getThreeByName(to);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":from_station_code:" + from_station_code);
        }
        WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":from_station_code:" + from_station_code);
        //        //根据出发到达时间获取列车信息
        //        Train traininfo = getTrainbycache(from_station_code, to_station_code, date, trainNo);
        //        int j = 0;
        //        while (traininfo.getTraincode() == null && j < 20) {
        //            try {
        //                Thread.sleep(50L);
        //            }
        //            catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //            j++;
        //            traininfo = getTrainbycache(from_station_code, to_station_code, date, trainNo);
        //        }

        //        if (!ElongHotelInterfaceUtil.StringIsNull(traininfo.getTraincode())) {
        //        String agentid = getqunaragentid();
        if (agentid != null && !agentid.equals("")) {
            trainorder.setAgentid(Long.parseLong(agentid));//代理ID
        }
        if (agentid != null && !agentid.equals("")) {
            trainorder.setCreateuid(Long.parseLong(agentid));//用户ID
        }
        try {
            List<Trainpassenger> passengers = gettrainpassenger(jsons, new Train(), trainNo, to, date, from, extSeat,
                    r1);

            totalprice = gettotalpricebytrainpassenger(passengers);
            trainorder.setPassengers(passengers);
            trainorder.setOrderstatus(Trainorder.WAITPAY);
            trainorder.setQunarOrdernumber(orderNo);
            trainorder.setOrderprice(totalprice);
            trainorder.setAgentprofit(0f);//采购利润
            trainorder.setCommission(0f);
            trainorder.setInterfacetype(TrainInterfaceMethod.QUNAR);
            trainorder.setState12306(Trainorder.WAITORDER);//12306状态--等待下单
            trainorder.setContactuser(reqtoken);//回调时候使用
            trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
            createTrainOrderExtSeat(trainorder.getId(), extSeat);
            try {
                String sql = "INSERT INTO [QunarTrainCallbackInfo] ([OrderId],[CreateOrderCallbackUrl],[Remark])VALUES("
                        + trainorder.getId() + ",'" + retUrl + "','')";
                WriteLog.write("QunarTrainorder_insert_", sql);
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            createtrainorderrc(1, "提交订单成功", trainorder.getId(), 0L, trainorder.getOrderstatus(),
                    customeruser.getMembername());
            activeMQroordering(trainorder.getId());
            jsonstr.put("ret", true);
            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":jsonstr:" + jsonstr.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            jsonstr.put("ret", true);
            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":火车订单为空或数据库连接失败:trainorder:" + jsonstr.toString());
        }
        //        }
        //        else {
        //            jsonstr.put("ret", false);
        //            jsonstr.put("errCode", 111);
        //            jsonstr.put("errMsg", "未查到对应车次信息");
        //            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":获得火车车次:Traincode:" + jsonstr.toString());
        //        }
        return jsonstr.toString();
    }

    private float gettotalpricebytrainpassenger(List<Trainpassenger> passengers) {
        return 0;
    }

    /**
     * 根据火车票订单id获取订单号
     * 
     * @param id
     * @return
     * @time 2015年1月5日 下午10:42:33
     * @author chendong
     */
    //    private String getordernumberbyid(long id) {
    //        String orderid = "";
    //        try {
    //            List list_trainorder = Server.getInstance().getSystemService()
    //                    .findMapResultBySql("SELECT C_ORDERNUMBER FROM T_TRAINORDER WHERE ID=" + id, null);
    //            if (list_trainorder.size() > 0) {
    //                Map maptrainnumber = (Map) list_trainorder.get(0);
    //                Object C_ORDERNUMBER_Object = maptrainnumber.get("C_ORDERNUMBER");
    //                if (C_ORDERNUMBER_Object != null) {
    //                    orderid = C_ORDERNUMBER_Object.toString();
    //                }
    //            }
    //        }
    //        catch (Exception e) {
    //        }
    //        return orderid;
    //    }

    /**
     * 创建火车票的操作记录
     * 
     * @param content
     * @param orderid
     * @param ticketid
     * @param status
     * @time 2014年12月16日 下午4:11:25
     * @author chendong
     */
    public static void createtrainorderrc(int ywtype, String content, Long orderid, Long ticketid, int status,
            String createuser) {
        Trainorderrc rc = new Trainorderrc();
        rc.setContent(content);
        rc.setCreateuser(createuser);//创建者
        rc.setOrderid(orderid);
        rc.setStatus(status);
        rc.setTicketid(ticketid);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 
     * @time 2015年7月10日15:26:47
     * @return luoqingxin
     */
    private List<Trainpassenger> gettrainpassenger(JSONArray jsons, Train traininfo, String checi,
            String to_station_name, String train_date, String from_station_name, String extSeat, int r1) {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        for (int i = 0; i < jsons.size(); i++) {
            Trainpassenger trainpassenger = new Trainpassenger();//订单的人员信息
            Trainticket ticket = new Trainticket();//票
            String name = jsons.getJSONObject(i).getString("name");//乘客姓名
            String certNo = jsons.getJSONObject(i).getString("certNo");//乘客证件号码
            String certType = jsons.getJSONObject(i).getString("certType");//证件类型ID
            String certName = jsons.getJSONObject(i).getString("certName");//证件类型名称 
            String ticketType = jsons.getJSONObject(i).getString("ticketType");//票种ID
            String ticketName = jsons.getJSONObject(i).getString("ticketName");//票种名称
            String seatCode = jsons.getJSONObject(i).getString("seatCode");//座位编码
            if (extSeat != null && !"".equals(extSeat)) {
                seatCode = extSeat(seatCode, extSeat, r1);
            }
            String seatName = seatName(seatCode);//座位名称
            Float price = 0f;//票价
            String ticketPrice = jsons.getJSONObject(i).getString("ticketPrice");//票价

            String passengerStudent = jsons.getJSONObject(i).containsKey("passengerStudentExt") ? passengerStudent = jsons
                    .getJSONObject(i).getString("passengerStudentExt") : "";
            if (passengerStudent != null && !passengerStudent.equals("")) {
                JSONObject passengerStudentExt = JSONObject.parseObject(passengerStudent);//学生票
                //TODO 遍历学生票      
                String province = passengerStudentExt.getString("province") == null ? "" : passengerStudentExt
                        .getString("province");// 省份名称
                String provinceCode = passengerStudentExt.getString("provinceCode") == null ? "" : passengerStudentExt
                        .getString("provinceCode");// 省份编号
                String schoolCode = passengerStudentExt.getString("schoolCode") == null ? "" : passengerStudentExt
                        .getString("schoolCode");// 学校代号
                String schoolName = passengerStudentExt.getString("schoolName") == null ? "" : passengerStudentExt
                        .getString("schoolName");// 学校名称
                String studentNo = passengerStudentExt.getString("studentNo") == null ? "" : passengerStudentExt
                        .getString("studentNo");// 学号
                String schooling = passengerStudentExt.getString("schooling") == null ? "" : passengerStudentExt
                        .getString("schooling");// 学制
                String intendedTime = passengerStudentExt.getString("intendedTime") == null ? "" : passengerStudentExt
                        .getString("intendedTime"); // 入学年份
                String discountSectionBegin = passengerStudentExt.getString("discountSectionBegin") == null ? ""
                        : passengerStudentExt.getString("discountSectionBegin");// 起始名称
                String discountSectionBeginCode = passengerStudentExt.getString("discountSectionBeginCode") == null ? ""
                        : passengerStudentExt.getString("discountSectionBeginCode");// 起始地代号
                String discountSectionEnd = passengerStudentExt.getString("discountSectionEnd") == null ? ""
                        : passengerStudentExt.getString("discountSectionEnd");// 到达名称
                String discountSectionEndCode = passengerStudentExt.getString("discountSectionEndCode") == null ? ""
                        : passengerStudentExt.getString("discountSectionEndCode");// 到达地代号
                List<TrainStudentInfo> trainStudentInfos = new ArrayList<TrainStudentInfo>();
                TrainStudentInfo trainStudentInfo = new TrainStudentInfo();
                if (ticketType.equals("2")) {
                    trainStudentInfo.setStudentcard("");//优惠卡号
                    trainStudentInfo.setClasses(""); //所在班级
                    trainStudentInfo.setDepartment(""); //所在院系
                    trainStudentInfo.setEductionalsystem(schooling); //学制
                    trainStudentInfo.setEntranceyear(intendedTime); //入学年份
                    trainStudentInfo.setFromcity(discountSectionBegin); //出发城市
                    trainStudentInfo.setTocity(discountSectionEnd); //到达城市
                    trainStudentInfo.setSchoolprovince(province); //学校所在省
                    trainStudentInfo.setSchoolname(schoolName); //学校名称
                    trainStudentInfo.setStudentno(studentNo); //学生证号
                    trainStudentInfo.setSchoolnamecode(schoolCode); //学校代号
                    trainStudentInfo.setSchoolprovincecode(provinceCode); //学校所在省代号
                    trainStudentInfo.setFromcitycode(discountSectionBeginCode); //出发城市代号
                    trainStudentInfo.setTocitycode(discountSectionEndCode); //到达城市代号
                    trainStudentInfo.setArg1(0l); //备用参数1
                    trainStudentInfo.setArg2(""); //备用参数2
                    trainStudentInfo.setArg3(0l); //备用参数3
                    trainStudentInfos.add(trainStudentInfo);
                    trainpassenger.setTrainstudentinfos(trainStudentInfos);
                }
            }

            try {
                price = Float.parseFloat(ticketPrice);
                String status = jsons.getJSONObject(i).getString("status");//身份核验状态
                //乘客信息
                if (certNo.length() > 14) {
                    trainpassenger.setBirthday(certNo.substring(6, 14));
                }
                else {
                    trainpassenger.setBirthday("");
                }
                trainpassenger.setName(name);
                trainpassenger.setIdtype(getIdtype12306tolocal(certType));
                trainpassenger.setIdnumber(certNo);
                trainpassenger.setAduitstatus(0);
                trainpassenger.setChangeid(0);
                //票信息
                ticket.setSeq(0);
                ticket.setTrainno(checi);
                ticket.setPrice(Float.valueOf(price));
                ticket.setPayprice(Float.valueOf(price));
                ticket.setSeattype(seatName);
                ticket.setArrival(to_station_name);
                if (traininfo != null && traininfo.getStarttime() != null) {
                    ticket.setDeparttime(train_date + " " + traininfo.getStarttime());
                }
                else {
                    ticket.setDeparttime(train_date + " 00:00");
                }
                if (traininfo != null && traininfo.getEndtime() != null) {
                    ticket.setArrivaltime(traininfo.getEndtime());
                }
                ticket.setDeparture(from_station_name);
                if (traininfo != null && traininfo.getCosttime() != null) {
                    ticket.setCosttime(traininfo.getCosttime());// 历时
                }
                ticket.setDeparture(from_station_name);
                ticket.setStatus(Trainticket.WAITPAY);
                ticket.setInsurprice(0f);//采购支付
                ticket.setInsurorigprice(0f);//保险
                ticket.setInsurenum(0);
                System.out.println("old" + ticketType);
                ticketType = ticketType(ticketType);
                System.out.println("new" + ticketType);
                ticket.setTickettype(Integer.parseInt(ticketType));
                List<Trainticket> tickets = new ArrayList<Trainticket>();
                tickets.add(ticket);
                trainpassenger.setTraintickets(tickets);
                passengers.add(trainpassenger);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return passengers;
    }

    public String seatName(String seatCode) {
        String ticketName = "";
        if (seatCode.equals("0")) {
            ticketName = "无座";
        }
        if (seatCode.equals("1")) {
            ticketName = "硬座";
        }
        if (seatCode.equals("2")) {
            ticketName = "软座";
        }
        if (seatCode.equals("3")) {
            ticketName = "一等软座";
        }
        if (seatCode.equals("4")) {
            ticketName = "二等软座";
        }
        if (seatCode.equals("5")) {
            ticketName = "硬卧";
        }
        if (seatCode.equals("6")) {
            ticketName = "硬卧";
        }
        if (seatCode.equals("7")) {
            ticketName = "硬卧";
        }
        if (seatCode.equals("8")) {
            ticketName = "软卧";
        }
        if (seatCode.equals("9")) {
            ticketName = "软卧";
        }
        if (seatCode.equals("10")) {
            ticketName = "高级软卧";
        }
        if (seatCode.equals("11")) {
            ticketName = "高级软卧";
        }
        if (seatCode.equals("12")) {
            ticketName = "特等座";
        }
        if (seatCode.equals("13")) {
            ticketName = "商务座";
        }
        return ticketName;
    }

    /**
           * 获取实时下单地址
           * @return
           * @time 2015年1月20日 下午10:08:24
           * @author fiend
           */
    public String getSubmitUrl() {
        //        String submittrainorderqunar = PropertyUtil.getValue("submittrainorderqunar", "train.properties");
        return getSystemConfig("submittrainorderqunar");
        //        return "http://localhost:9010/ticket_inter/SubmitTrainorder.jsp?id=";
    }

    /**
     * 调用下单方法,生成线程(给俺去下单) 
     * @return
     * @time 2015年1月21日 上午10:05:25
     * @author fiend
     */
    public void gotoordering(long id) {
        String url = getSubmitUrl() + id;
        try {
            WriteLog.write("QunarTrainorder", "ID:" + id);
            SendPostandGet.submitGet(url);
            WriteLog.write("QunarTrainorder", "ID:" + id);
        }
        catch (Exception e) {
            WriteLog.write("QunarTrainorder", "ID:" + id);
        }
    }

    /**
     * 创建火车票备选坐席
     * @param orderid
     * @param extseats
     */
    private void createTrainOrderExtSeat(long orderid, String extseats) {
        try {
            String sql = "INSERT INTO TrainOrderExtSeat (OrderId ,ExtSeat ,ReMark) VALUES ( " + orderid + ",'"
                    + extseats + "' ,'')";
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", orderid + "");
            ExceptionUtil.writelogByException("Error_MyThreadQunarOrder_createTrainOrderExtSeat", e);
        }
    }

    //映射票种类型
    private String ticketType(String type) {

        if (type.equals("0")) {
            type = "2";
        }
        else if (type.equals("2")) {
            type = "3";
        }
        return type;
    }

    //当无座为主坐席时调用备选坐席
    private String extSeat(String seatCode, String extSeat, int r1) {
        JSONArray extArray = JSONArray.parseArray(extSeat);
        WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", "替换前extArray:" + extArray.toString());
        if (seatCode.equals("0")) {
            WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", "替换前extArray:");
            JSONObject json = new JSONObject();
            for (int i = 0; i < extArray.size(); i++) {
                String seatY = extArray.getJSONObject(i).getString("1");
                String seat2 = extArray.getJSONObject(i).getString("4");
                String seatR = extArray.getJSONObject(i).getString("2");
                WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", "硬座,seatY:" + seatY + "软座,seatR:"
                        + seatR + "二等座,seat2:" + seat2);
                if (!ElongHotelInterfaceUtil.StringIsNull(seatY)) {
                    extArray.remove(i);
                    json.put(seatCode, Double.parseDouble(seatY));
                    extArray.add(json);
                    seatCode = "1";
                    System.out.println(Double.valueOf(seatY));
                    break;
                }
                else if (!ElongHotelInterfaceUtil.StringIsNull(seatR)) {
                    extArray.remove(i);
                    json.put(seatCode, Float.valueOf(seatR));
                    extArray.add(json);
                    seatCode = "2";
                    break;
                }
                else if (!ElongHotelInterfaceUtil.StringIsNull(seat2)) {
                    extArray.remove(i);
                    json.put(seatCode, Float.valueOf(seat2));
                    extArray.add(json);
                    seatCode = "4";
                    break;
                }
            }
        }
        extSeat = extArray.toString();
        //        System.out.println(extSeat);
        WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", "extArray:" + extArray.toString());
        WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", "seatCode:" + seatCode);
        return seatCode;

    }

    public static void main(String[] args) {

        QunarTrainorder quanr = new QunarTrainorder();
        JSONArray extSeatArray = new JSONArray();
        //        JSONObject extSeatJson = new JSONObject();
        //        extSeatJson.put("0", 93);
        JSONObject extSeatJson2 = new JSONObject();
        extSeatJson2.put("1", 14);
        JSONObject extSeatJson3 = new JSONObject();
        extSeatJson3.put("5", 163);
        JSONObject extSeatJson4 = new JSONObject();
        extSeatJson4.put("6", 168);
        JSONObject extSeatJson5 = new JSONObject();
        extSeatJson5.put("7", 174);

        //        extSeatArray.add(extSeatJson);
        extSeatArray.add(extSeatJson2);
        extSeatArray.add(extSeatJson3);
        extSeatArray.add(extSeatJson4);
        extSeatArray.add(extSeatJson5);
        //        extSeatArray.add(extSeatJson6);

        String extSeat = extSeatArray.toString();
        //        String ww = quanr.extSeat("0", extSeat);
        //        System.out.println("最" + ww);
    }
}