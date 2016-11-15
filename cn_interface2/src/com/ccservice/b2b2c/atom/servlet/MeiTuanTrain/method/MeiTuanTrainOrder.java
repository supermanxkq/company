package com.ccservice.b2b2c.atom.servlet.MeiTuanTrain.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Exception.TrainOrderException;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.AirUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MeiTuanTrainOrder extends TongchengSupplyMethod {
    private final String logname = "MeiTuan_先占座后支付_提交订单";

    public String submitOrder(JSONObject jsonObject) {
        Customeruser customeruser = new Customeruser();// 用户表
        String result = "";
        JSONObject obj = new JSONObject();
        String code = "";
        String msg = "";
        WriteLog.write(logname, "json值：" + jsonObject);
        try {
            String orderId = jsonObject.getString("orderId");//改签Id
            String train_code = jsonObject.getString("train_code");//车次号
            String start_time = jsonObject.getString("start_time");//发车时间
            String arrive_time = jsonObject.getString("arrive_time");//到达时间
            String from_station_code = jsonObject.getString("from_station_code");//出发城市CODE
            String from_station_name = jsonObject.getString("from_station_name");//出发城市
            String to_station_code = jsonObject.getString("to_station_code");//到大城市code
            String to_station_name = jsonObject.getString("to_station_name");//到达城市
            String callback_url = jsonObject.getString("callback_url");//回调地址
            String req_token = jsonObject.getString("req_token");//标识
            String passengers = jsonObject.getString("passengers");//乘客
            String reqtime = jsonObject.getString("reqtime");//请求时间
            String partnerid = jsonObject.getString("partnerid");//识别id
            String sign = jsonObject.getString("sign");//签名
            if (ElongHotelInterfaceUtil.StringIsNull(orderId) || ElongHotelInterfaceUtil.StringIsNull(train_code)
                    || ElongHotelInterfaceUtil.StringIsNull(start_time)
                    || ElongHotelInterfaceUtil.StringIsNull(arrive_time)
                    || ElongHotelInterfaceUtil.StringIsNull(from_station_code)
                    || ElongHotelInterfaceUtil.StringIsNull(from_station_name)
                    || ElongHotelInterfaceUtil.StringIsNull(to_station_code)
                    || ElongHotelInterfaceUtil.StringIsNull(to_station_name)
                    || ElongHotelInterfaceUtil.StringIsNull(callback_url)
                    || ElongHotelInterfaceUtil.StringIsNull(req_token)
                    || ElongHotelInterfaceUtil.StringIsNull(passengers)
                    || ElongHotelInterfaceUtil.StringIsNull(reqtime) || ElongHotelInterfaceUtil.StringIsNull(partnerid)
                    || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "请求参数缺失");
                return obj.toString();
            }
            String reqSign = "";
            //       String reqSign=getHoldSeatReqToken(orderId,train_code,from_station_code,to_station_code,start_time,List<Trainticket>);
            WriteLog.write(logname, "签名验证：reqSign=" + reqSign + ",sign=" + sign);
            if (!reqSign.equals(sign)) {
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "账号验证失败");
                return obj.toString();
            }
            else {
                String agentid = gettongchengagentid(partnerid);
                Trainorder trainorder = new Trainorder();
                trainorder.setQunarOrdernumber(orderId);
                trainorder.setAgentid(Long.parseLong(agentid));
                trainorder.setOrderprice((float) 0);
                trainorder.setAgentprofit(0f);// 采购利润
                trainorder.setCommission(0f);
                trainorder.setSupplyprice((float) 0);
                trainorder.setCreateuser("接口");
                trainorder.setPaymethod(4);
                trainorder.setContactuser(req_token);
                Train traininfo = new Train();
                traininfo.setStarttime(start_time);
                traininfo.setEndtime(arrive_time);
                JSONArray jsons = JSONArray.parseArray(passengers);
                WriteLog.write(logname, "乘客传入信息：" + jsons);
                List<Trainpassenger> trainpassengers = gettrainpassenger(jsons, traininfo, train_code, to_station_name,
                        start_time, from_station_name);
                WriteLog.write(logname, "乘客拼接信息：" + trainpassengers);
                trainorder.setPassengers(trainpassengers);
                try {
                    trainorder.setState12306(Trainorder.WAITORDER);// 12306状态--等待下单
                    boolean isTimeoutOrder = false;
                    try {
                        String sql = "SELECT C_ORDERNUMBER FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                                + orderId + "' AND C_AGENTID=" + agentid;
                        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                        WriteLog.write(logname, "已创建订单信息：" + list);
                        if (list.size() > 0) {
                            Map map = (Map) list.get(0);
                            String ordernumberString = map.get("C_ORDERNUMBER").toString();
                            orderId = ordernumberString;
                            isTimeoutOrder = true;
                            WriteLog.write(logname, "已创建订单号：" + orderId);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    boolean createSuccess = false;//插入数据库异常标识     默认为false    当数据库插入订单发生异常时为   true
                    if (!isTimeoutOrder) {//当前订单不存在时    创建订单
                        trainorder.setContactuser(req_token);// 回调时候使用
                        try {
                            trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                            createSuccess = true;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (createSuccess) {//判断之后   下面的情况是创建订单成功
                            createTrainOrderExtSeat(trainorder, jsonObject);
                            orderId = getordernumberbyid(trainorder.getId());
                            createtrainorderrc(1, "提交订单成功", trainorder.getId(), 0L, trainorder.getOrderstatus(),
                                    customeruser.getMembername());
                            obj.put("success", true);
                            obj.put("code", "100");
                            obj.put("msg", "创建订单成功");
                            return obj.toString();
                        }
                        else {
                            WriteLog.write("闩锁问题订单", "使用方订单号 " + orderId);
                            obj.put("success", false);
                            obj.put("code", "999");
                            obj.put("msg", "订单创建失败");
                            result = obj.toString();
                        }
                    }
                    else {//当订单已存在 
                        obj.put("success", true);
                        obj.put("code", "100");
                        obj.put("msg", "创建订单成功");
                        return obj.toString();
                    }
                }
                catch (Exception e) {
                    obj.put("success", false);
                    obj.put("code", "113");
                    obj.put("msg", "当前时间不提供服务");
                    return obj.toString();
                }
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        catch (TrainOrderException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 把json里的对象转换成我们自己的 List<Trainpassenger>
     * 
     * @param jsons
     * @param traininfo
     * @param checi
     * @param to_station_name
     * @param train_date
     * @param from_station_name
     * @return
     * @time 2015年1月15日 上午9:35:03
     * @author chendong
     * @throws TrainOrderException 
     */
    private List<Trainpassenger> gettrainpassenger(JSONArray jsons, Train traininfo, String checi,
            String to_station_name, String train_date, String from_station_name) throws TrainOrderException {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        for (int i = 0; i < jsons.size(); i++) {
            Trainpassenger trainpassenger = new Trainpassenger();// 订单的人员信息
            Trainticket ticket = new Trainticket();// 票
            String passengersename = jsons.getJSONObject(i).getString("passenger_name");// 乘客姓名
            String passportseno = jsons.getJSONObject(i).getString("certificate_no");// 乘客证件号码
            String passporttypeseid = jsons.getJSONObject(i).getString("certificate_type");// 证件类型ID
            String piaotype = jsons.getJSONObject(i).getString("ticket_type");// 票种ID
            String zwcode = jsons.getJSONObject(i).getString("seat_type");// 座位编码
            String zwname = getzwcode(zwcode);
            WriteLog.write(logname, "坐席名称转换：code=" + zwcode + "----->zwname=" + zwname);
            if (zwname == null) {
                throw new TrainOrderException("103 err|zwname is null");
            }
            zwname = zwname.replace("上", "").replace("中", "").replace("下", "");
            String price = jsons.getJSONObject(i).getString("price");// 票价
            trainpassenger.setIdentitystatusid(0);
            // if (price != null && !"".equals(price)) {
            // totalprice += Float.valueOf(price);
            // }
            passportseno = AirUtil.ToDBC(passportseno);
            // 乘客信息
            if (passportseno.length() > 14) {
                trainpassenger.setBirthday(passportseno.substring(6, 14));
            }
            else {
                trainpassenger.setBirthday("");
            }
            trainpassenger.setName(passengersename);
            trainpassenger.setIdtype(getIdtype12306tolocal(passporttypeseid));
            trainpassenger.setIdnumber(passportseno);
            trainpassenger.setAduitstatus(0);
            trainpassenger.setChangeid(0);
            // 票信息
            ticket.setTrainno(checi);
            ticket.setPrice(Float.valueOf(price));
            ticket.setPayprice(Float.valueOf(price));
            // ticket.setCoach(cxin);
            // ticket.setSeatno(zwcode);
            if ("无座".equals(zwname)) {
                if (checi.startsWith("D")) {
                    zwname = "二等座";
                }
                else if (checi.startsWith("C")) {
                    zwname = "软座";
                }
                else {
                    zwname = "硬座";
                }
                ticket.setSeattype(zwname);
            }
            else {
                ticket.setSeattype(zwname);
            }
            ticket.setSeattype(zwname);
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
            ticket.setStatus(Trainticket.WAITPAY);
            ticket.setInsurprice(0f);// 采购支付
            ticket.setInsurorigprice(0f);// 保险
            ticket.setInsurenum(0);
            piaotype = (piaotype == null || "".equals(piaotype)) ? "1" : piaotype;
            ticket.setTickettype(Integer.parseInt(piaotype));
            List<Trainticket> tickets = new ArrayList<Trainticket>();
            tickets.add(ticket);
            trainpassenger.setTraintickets(tickets);
            JSONObject JSONObjecttrainpassenger = jsons.getJSONObject(i);
            int tickettype = ticket.getTickettype();// 1:成人票，2:儿童票，3:学生票，4:残军票
            trainpassenger = setStudentInfo(trainpassenger, i, JSONObjecttrainpassenger, tickettype);
            passengers.add(trainpassenger);
        }
        return passengers;
    }

    /**
     * 设置学生票信息
     * @return
     * @time 2016年1月19日 上午11:19:55
     * @author chendong
     * @param i 
     * @param trainpassenger 
     * @param jSONObjecttrainpassenger 
     * @param tickettype 
     */
    private Trainpassenger setStudentInfo(Trainpassenger trainpassenger, int i, JSONObject jSONObjecttrainpassenger,
            int tickettype) {
        try {
            //TODO 学生票
            List<TrainStudentInfo> trainStudentInfos = new ArrayList<TrainStudentInfo>();
            TrainStudentInfo trainStudentInfo = new TrainStudentInfo();
            String province_name = jSONObjecttrainpassenger.getString("province_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("province_name");// 省份名称
            String province_code = jSONObjecttrainpassenger.getString("province_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("province_code");// 省份编号
            String school_code = jSONObjecttrainpassenger.getString("school_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("school_code");// 学校代号
            String school_name = jSONObjecttrainpassenger.getString("school_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("school_name");// 学校名称
            String student_no = jSONObjecttrainpassenger.getString("student_no") == null ? ""
                    : jSONObjecttrainpassenger.getString("student_no");// 学号
            String school_system = jSONObjecttrainpassenger.getString("school_system") == null ? ""
                    : jSONObjecttrainpassenger.getString("school_system");// 学制
            String enter_year = jSONObjecttrainpassenger.getString("enter_year") == null ? ""
                    : jSONObjecttrainpassenger.getString("enter_year"); // 入学年份
            String preference_from_station_name = jSONObjecttrainpassenger.getString("preference_from_station_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_from_station_name");// 起始名称
            String preference_from_station_code = jSONObjecttrainpassenger.getString("preference_from_station_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_from_station_code");// 起始地代号
            String preference_to_station_name = jSONObjecttrainpassenger.getString("preference_to_station_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_to_station_name");// 到达名称
            String preference_to_station_code = jSONObjecttrainpassenger.getString("preference_to_station_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_to_station_code");// 到达地代号
            if (3 == tickettype) {
                trainStudentInfo.setStudentcard("");//优惠卡号
                trainStudentInfo.setClasses(""); //所在班级
                trainStudentInfo.setDepartment(""); //所在院系
                trainStudentInfo.setEductionalsystem(school_system); //学制
                trainStudentInfo.setEntranceyear(enter_year); //入学年份
                trainStudentInfo.setFromcity(preference_from_station_name); //出发城市
                trainStudentInfo.setTocity(preference_to_station_name); //到达城市
                trainStudentInfo.setSchoolprovince(province_name); //学校所在省
                trainStudentInfo.setSchoolname(school_name); //学校名称
                trainStudentInfo.setStudentno(student_no); //学生证号
                trainStudentInfo.setSchoolnamecode(school_code); //学校代号
                trainStudentInfo.setSchoolprovincecode(province_code); //学校所在省代号
                trainStudentInfo.setFromcitycode(preference_from_station_code); //出发城市代号
                trainStudentInfo.setTocitycode(preference_to_station_code); //到达城市代号
                trainStudentInfo.setArg1(0l); //备用参数1
                trainStudentInfo.setArg2(""); //备用参数2
                trainStudentInfo.setArg3(0l); //备用参数3
                trainStudentInfos.add(trainStudentInfo);
                trainpassenger.setTrainstudentinfos(trainStudentInfos);
            }
        }
        catch (Exception e) {
            WriteLog.write(logname, trainpassenger + ":" + i + ":" + jSONObjecttrainpassenger + ":" + tickettype);
        }
        return trainpassenger;
    }

    /**
     * 是否接受站票
     * @param jsonObject2 
     * @param trainorder 
     * 
     * @param orderid
     * @param acceptStand 
     * @param ticketPrice  1 代表接受站票
     */
    public void createTrainOrderExtSeat(Trainorder trainorder, JSONObject jsonObject1) {
        try {
            long orderid = trainorder.getId();
            boolean isNeedStandingSeat = false;
            if (jsonObject1.containsKey("is_ accept _ standing")) {
                isNeedStandingSeat = jsonObject1.getBooleanValue("is_ accept _ standing");
            }
            else if (jsonObject1.containsKey("is_accept_standing")) {
                isNeedStandingSeat = jsonObject1.getBooleanValue("is_accept_standing");
            }
            else if (jsonObject1.containsKey("hasseat")) {
                isNeedStandingSeat = !jsonObject1.getBooleanValue("hasseat");
            }

            //闪联接受无座票会传无座
            String zwName = "";
            for (int i = 0; i < jsonObject1.getJSONArray("passengers").size(); i++) {
                zwName = jsonObject1.getJSONArray("passengers").getJSONObject(i).getString("zwname");// 座位名称;
                if ("无座".equals(zwName))
                    break;
            }
            Float ticketPrice = trainorder.getOrderprice();
            WriteLog.write("createTrainOrderExtSeat", orderid + ":" + (isNeedStandingSeat || "无座".equals(zwName)));
            if (isNeedStandingSeat || "无座".equals(zwName)) {
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("0", ticketPrice);
                    jsonArray.add(jsonObject);
                    String extseats = jsonArray.toJSONString();//[{"5":389},{"6":428}]
                    String sql = "INSERT INTO TrainOrderExtSeat (OrderId ,ExtSeat ,ReMark) VALUES ( " + orderid + ",'"
                            + extseats + "' ,'')";
                    WriteLog.write("createTrainOrderExtSeat", orderid + ":sql:" + sql);
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", orderid + "");
                    ExceptionUtil.writelogByException("Error_MyThreadQunarOrder_createTrainOrderExtSeat", e);
                }
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * 根据火车票订单id获取订单号
     * 
     * @param id
     * @return
     * @time 2015年1月5日 下午10:42:33
     * @author chendong
     */
    private String getordernumberbyid(long id) {
        String orderid = "";
        try {
            List list_trainorder = Server.getInstance().getSystemService()
                    .findMapResultBySql("SELECT C_ORDERNUMBER FROM T_TRAINORDER WHERE ID=" + id, null);
            if (list_trainorder.size() > 0) {
                Map maptrainnumber = (Map) list_trainorder.get(0);
                Object C_ORDERNUMBER_Object = maptrainnumber.get("C_ORDERNUMBER");
                if (C_ORDERNUMBER_Object != null) {
                    orderid = C_ORDERNUMBER_Object.toString();
                }
            }
        }
        catch (Exception e) {
            WriteLog.write(logname, "Tongchengtrainorder_getordernumberbyid:" + id + e.fillInStackTrace());
        }
        return orderid;
    }

}
