package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSupplyMethodUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.GuidUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 途牛请求改签
 * 
 * @time 2015年8月31日 下午8:39:11
 * @author Administrator
 */
public class TuniuReqChangeThread extends TongchengSupplyMethod implements Runnable {

    String partnerid;

    JSONObject reqobj;

    String orderid;

    String ordernumber;

    String transactionid;

    //当前不提供服务
    String code113;

    String code999;

    String datatypeflag;

    //日期格式化
    SimpleDateFormat dateFormat;

    //时分格式化
    SimpleDateFormat shiFenFormat;

    String guid = "";

    boolean McCached = false;

    public TuniuReqChangeThread(String partnerid, JSONObject reqobj, String orderid, String ordernumber,
            String transactionid, String code113, String code999, String datatypeflag, SimpleDateFormat dateFormat,
            SimpleDateFormat shiFenFormat) {
        this.partnerid = partnerid;
        this.reqobj = reqobj;
        this.orderid = orderid;
        this.ordernumber = ordernumber;
        this.transactionid = transactionid;
        this.code113 = code113;
        this.code999 = code999;
        this.datatypeflag = datatypeflag;
        this.dateFormat = dateFormat;
        this.shiFenFormat = shiFenFormat;
    }

    @Override
    public void run() {
        WriteLog.write("途牛请求改签Thread", partnerid + reqobj.toString() + orderid + ordernumber + transactionid + code113
                + code999 + datatypeflag);
        boolean McCached = false;
        guid = GuidUtil.getUuid();
        McCached = setMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        //订单不存在
        if (orders == null || orders.size() != 1) {
            //            retobj.put("code", "402");
            //            retobj.put("msg", "订单不存在");
            //            return retobj.toString();
            tuniuReqChangeCallBack(partnerid, false);
            WriteLog.write("途牛请求改签Thread", "订单不存在,orderid:" + orderid + "--transactionid:" + transactionid);
            return;
        }
        Trainorder order = orders.get(0);
        order.setQunarOrdernumber(orderid);
        //12306订单号
        if (!ordernumber.equals(order.getExtnumber())) {
            //            retobj.put("code", "108");
            //            retobj.put("msg", "取票单号[" + ordernumber + "]不一致");
            //            return retobj.toJSONString();
            WriteLog.write("途牛请求改签Thread", "取票单号不一致,orderid:" + orderid + "--transactionid:" + transactionid
                    + "--ordernumber：" + ordernumber + "--Extnumber" + order.getExtnumber());
            tuniuReqChangeCallBack(partnerid, false);
            return;
        }
        //数据校验
        WriteLog.write("途牛请求改签Thread", "数据校验,orderid:" + orderid + "--reqobj:" + reqobj.toString());
        RequestCheck(order, reqobj, 0);
    }

    /**
     * 改签请求数据校验 
     * @param order 本地订单
     * @param reqobj 请求数据
     * @param changeType 改签类型，0：改签；1：改签退
     */
    public void RequestCheck(Trainorder order, JSONObject reqobj, int changeType) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //改签车票信息
        JSONArray ticketinfo = reqobj.getJSONArray("ticketinfo");
        //改签车票出发时间判断
        String change_datetime = reqobj.getString("change_datetime");
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //FORMAT
            change_datetime = timeFormat.format(timeFormat.parse(change_datetime));
        }
        catch (Exception e) {
            tuniuReqChangeCallBack(partnerid, false);
            WriteLog.write("途牛请求改签Thread", "改签新车票出发时间[" + change_datetime + "]格式错误，应为yyyy-MM-dd HH:mm:ss");
            ExceptionUtil.writelogByException("err_tuniu", e);
        }
        //原票的座位席别编码
        String old_zwcode = reqobj.getString("old_zwcode");
        //改签退，新车票的价格
        float change_price = changeType == 1 ? reqobj.getFloatValue("change_price") : 0;
        //改签新车票的座位席别编码
        String change_zwcode = reqobj.getString("change_zwcode");
        //key：车票号，value：改签乘客信息
        Map<String, JSONObject> reqTickets = new HashMap<String, JSONObject>();
        //下单参数
        JSONObject reqpassengers = new JSONObject();
        String prices = "";
        String zwcodes = "";
        String oldPassengerStr = "";
        String passengerTicketStr = "";
        //循环车票
        for (int i = 0; i < ticketinfo.size(); i++) {
            JSONObject temp = ticketinfo.getJSONObject(i);
            String passengersename = temp.containsKey("passengersename") ? temp.getString("passengersename") : "";
            String passporttypeseid = temp.containsKey("passporttypeseid") ? temp.getString("passporttypeseid") : "";
            String passportseno = temp.containsKey("passportseno") ? temp.getString("passportseno") : "";
            String piaotype = temp.containsKey("piaotype") ? temp.getString("piaotype") : "";
            String old_ticket_no = temp.containsKey("old_ticket_no") ? temp.getString("old_ticket_no") : "";
            if (ElongHotelInterfaceUtil.StringIsNull(passengersename)
                    || ElongHotelInterfaceUtil.StringIsNull(passporttypeseid)
                    || ElongHotelInterfaceUtil.StringIsNull(passportseno)
                    || ElongHotelInterfaceUtil.StringIsNull(piaotype)
                    || ElongHotelInterfaceUtil.StringIsNull(old_ticket_no)) {
                WriteLog.write("途牛请求改签Thread", "参数为空 orderid:" + orderid);
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            if (reqTickets.containsKey(old_ticket_no)) {
                WriteLog.write("途牛请求改签Thread", "old_ticket_no:" + old_ticket_no + "orderid:" + orderid);
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            reqTickets.put(old_ticket_no, temp);
            //拼下单参数
            prices += change_price + "@";
            zwcodes += change_zwcode + "@";
            if ("2".equals(piaotype)) {
                oldPassengerStr += "_ ";
            }
            else {
                oldPassengerStr += passengersename + "," + passporttypeseid + "," + passportseno + "," + piaotype + "_";
            }
            //无座，12306下单参数为硬座
            passengerTicketStr += change_zwcode + ",0," + piaotype + "," + passengersename + "," + passporttypeseid
                    + "," + passportseno + ",,Y_";
        }
        reqpassengers.put("prices", prices.substring(0, prices.length() - 1));
        reqpassengers.put("zwcodes", zwcodes.substring(0, zwcodes.length() - 1));
        reqpassengers.put("oldPassengerStr", oldPassengerStr);
        reqpassengers.put("passengerTicketStr", passengerTicketStr.substring(0, passengerTicketStr.length() - 1));
        //批量改签时，选择的新票座位席别必须一致，并且不能是卧铺。
        //P:特等座，M:一等座，O:二等座，E:特等软座，9:商务座，8：二等软座，7：一等软座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座
        if (ticketinfo.size() > 1) {
            if ("6".equals(old_zwcode) || "4".equals(old_zwcode) || "3".equals(old_zwcode)) {
                WriteLog.write("途牛请求改签Thread", "批量改签时，原票不能是卧铺orderid:" + orderid);
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            if ("6".equals(change_zwcode) || "4".equals(change_zwcode) || "3".equals(change_zwcode)) {
                WriteLog.write("途牛请求改签Thread", "批量改签时，新票不是能卧铺orderid:" + orderid);
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
        }
        WriteLog.write("途牛请求改签Thread", "本地数据效验--orderid:" + orderid + "--reqobj:" + reqobj);
        LocalCheck(order, reqobj, reqpassengers, ticketinfo, reqTickets, change_datetime, change_zwcode, changeType);
    }

    /**
     * 本地数据校验
     * @param order 本地订单
     * @param reqobj 请求JSON
     * @param ticketinfo 请求车票JSON
     * @param reqTickets 请求车票Map
     * @param change_datetime 改签新车票出发String时间
     * @param change_zwcode 改签新车票的座位席别编码
     */
    private void LocalCheck(Trainorder order, JSONObject reqobj, JSONObject reqpassengers, JSONArray ticketinfo,
            Map<String, JSONObject> reqTickets, String change_datetime, String change_zwcode, int changeType) {
        //是否为异步改签>>Y：异步改签；N：同步改签
        String isasync = reqobj.containsKey("isasync") ? reqobj.getString("isasync") : "N";
        //是否为改签变站
        Boolean isTs = reqobj.containsKey("isTs") ? reqobj.getBoolean("isTs") : false;
        //校验结果
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //已出票
        int status = order.getOrderstatus();
        if (status != Trainorder.ISSUED) {
            WriteLog.write("途牛请求改签Thread", "已出票,orderid:" + orderid + "--status:" + order.getOrderstatus());
            tuniuReqChangeCallBack(partnerid, false);
            return;
        }
        long orderId = order.getId();
        //校验现有改签
        JSONObject ChangeCheck = ChangeCheck(orderId);
        //校验失败，返回结果
        if (!ChangeCheck.getBooleanValue("success")) {
            WriteLog.write("途牛请求改签Thread", "效验失败,orderid:" + orderid + "--ChangeCheck:" + ChangeCheck.toString());
            tuniuReqChangeCallBack(partnerid, false);
            return;
        }
        //加载其他字段、乘客
        if (changeType == 0) {
            order = Server.getInstance().getTrainService().findTrainorder(orderId);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
        }
        //原票价总和
        float tcOriginalPrice = 0f;
        //订单对应乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //车票类别 1：成从票；2：儿童票
        List<Integer> piaoTypes = new ArrayList<Integer>();
        //车票座席、用于判断指改签时，座席一致
        List<String> seatTypes = new ArrayList<String>();
        //用于验证，同一订单中相同日期、车次、发站、到站、席别的车票方可批量改签
        List<String> ticketMsgList = new ArrayList<String>();
        //原车票号对应本地车票
        Map<String, Trainticket> oldTicketMap = new HashMap<String, Trainticket>();
        Map<String, Trainpassenger> passengerMap = new HashMap<String, Trainpassenger>();
        //查询所有改签车票
        for (String old_ticket_no : reqTickets.keySet()) {
            Trainticket oldTicket = null;
            Trainpassenger ticketPassenger = null;
            //循环乘客找车票
            out: for (Trainpassenger passenger : passengers) {
                List<Trainticket> traintickets = passenger.getTraintickets();
                if (traintickets == null || traintickets.size() == 0) {
                    continue;
                }
                WriteLog.write("途牛请求改签Thread", "traintickets.size:" + traintickets.size());
                //多张票可能是联程
                for (Trainticket trainticket : traintickets) {
                    //改签票号
                    String tcticketno = trainticket.getTcticketno();
                    WriteLog.write("途牛请求改签Thread", "改签票号,old_ticket_no:" + old_ticket_no + "&改签票号" + tcticketno);
                    if (old_ticket_no.equals(tcticketno)) {
                        WriteLog.write("途牛请求改签Thread", "改签票号,old_ticket_no:" + old_ticket_no + "&改签票号" + tcticketno);
                        tuniuReqChangeCallBack(partnerid, false);
                        return;
                    }
                    //票号
                    String ticketno = trainticket.getTicketno();
                    WriteLog.write("途牛请求改签Thread", "票号,ticketno:" + ticketno);
                    if (old_ticket_no.equals(ticketno)) {
                        if (oldTicket == null) {
                            oldTicket = trainticket;
                            ticketPassenger = passenger;
                        }
                        else {
                            oldTicket = null;
                            ticketPassenger = null;
                            break out;
                        }
                    }
                }
            }
            if (oldTicket == null) {
                WriteLog.write("途牛请求改签Thread", "oldTicket:" + oldTicket);
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            //开车时间
            String departTime = oldTicket.getDeparttime();
            //校验车票状态
            JSONObject TicketCheck = TicketCheck(oldTicket, old_ticket_no, departTime, change_datetime, changeType,
                    isTs);
            //FALSE
            if (!TicketCheck.getBooleanValue("success")) {
                WriteLog.write("途牛请求改签Thread", "校验车票状态,TicketCheck:" + TicketCheck.toString());
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            int tickettype = oldTicket.getTickettype();
            //校验乘客信息
            JSONObject PassengerCheck = PassengerCheck(old_ticket_no, String.valueOf(tickettype),
                    reqTickets.get(old_ticket_no), ticketPassenger);
            WriteLog.write("途牛请求改签Thread", "校验乘客信息,PassengerCheck:" + PassengerCheck.toString());
            //FALSE
            if (!PassengerCheck.getBooleanValue("success")) {
                WriteLog.write("途牛请求改签Thread", "校验乘客信息--FALSE,PassengerCheck:" + PassengerCheck.toString());
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            //车票座席
            String seattype = oldTicket.getSeattype();
            if (!seatTypes.contains(seattype)) {
                seatTypes.add(seattype);
            }
            //日期、车次、发站、到站、席别
            String ticketMsg = departTime + "@" + oldTicket.getTrainno() + "@" + oldTicket.getDeparture() + "@"
                    + oldTicket.getArrival() + "@" + oldTicket.getSeattype();
            if (!ticketMsgList.contains(ticketMsg)) {
                ticketMsgList.add(ticketMsg);
            }
            //后续更新用
            WriteLog.write("途牛请求改签Thread", "后续更新用orderid:" + orderid);
            if (!piaoTypes.contains(tickettype)) {
                piaoTypes.add(tickettype);
            }
            oldTicketMap.put(old_ticket_no, oldTicket);
            passengerMap.put(old_ticket_no, ticketPassenger);
            tcOriginalPrice = ElongHotelInterfaceUtil.floatAdd(tcOriginalPrice, oldTicket.getPrice());
        }
        //批量改签时，原票不能是卧铺
        //同一订单中相同日期、车次、发站、到站、席别的车票方可批量改签
        if (ticketinfo.size() > 1) {
            if (seatTypes.size() == 0 || seatTypes.size() > 1 || ticketMsgList.size() == 0 || ticketMsgList.size() > 1) {
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
            String seatType = seatTypes.get(0);
            if (seatType.contains("卧") && !"硬卧代硬座".equals(seatType) && !"软卧代软座".equals(seatType)
                    && !"软卧代二等座".equals(seatType)) {
                WriteLog.write("途牛请求改签Thread", "seatType:" + seatType);
                tuniuReqChangeCallBack(partnerid, false);
                return;
            }
        }
        //异步改签
        WriteLog.write("途牛请求改签Thread", "改签orderid:" + orderid);
        if ("Y".equals(isasync)) {
            AsyncChange(order, reqobj, piaoTypes, change_zwcode, change_datetime, tcOriginalPrice, oldTicketMap,
                    passengerMap);
            return;
        }
        //同步改签
        else {
            RepOperate(order, oldTicketMap, passengerMap, reqobj, reqTickets, reqpassengers, change_datetime,
                    piaoTypes, changeType, new Trainorderchange()).toJSONString();
            return;

        }
    }

    /**
     * 异步改签
     */
    private void AsyncChange(Trainorder order, JSONObject reqobj, List<Integer> piaoTypes, String change_zwcode,
            String change_datetime, float tcOriginalPrice, Map<String, Trainticket> oldTicketMap,
            Map<String, Trainpassenger> passengerMap) {
        //请求特征值
        String reqtoken = reqobj.getString("reqtoken");
        //改签占座异步回调地址
        String callbackurl = reqobj.getString("callbackurl");
        //淘宝支付号
        String taobaoapplyid = reqobj.getString("apply_id");
        String latest_change_time = reqobj.containsKey("latest_change_time") ? reqobj.getString("latest_change_time")
                : "";
        //配置地址
        String systemSetUrl = getTrainCallBackUrl(order.getAgentid(), 1);
        //非空，取配置
        if (!ElongHotelInterfaceUtil.StringIsNull(systemSetUrl)) {
            callbackurl = systemSetUrl;
        }
        //变站
        boolean isTs = false;
        String to_station_name = reqobj.containsKey("to_station_name") ? reqobj.getString("to_station_name") : "";
        //返回数据
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        retobj.put("reqtoken", reqtoken);
        //订单ID
        long orderId = order.getId();
        change_datetime = reqobj.getString("change_datetime");
        //新增改签
        Trainorderchange trainOrderChange = new Trainorderchange();
        trainOrderChange.setOrderid(orderId);
        trainOrderChange.setTctrainno(reqobj.getString("change_checi"));//改签车次
        trainOrderChange.setTctickettype(piaoTypes.size() == 1 ? piaoTypes.get(0) : 1);//票类型
        trainOrderChange.setTcseattype(change_zwcode);//座席名称，默认为编码
        trainOrderChange.setTcprice(0);//新价格，默认为0
        trainOrderChange.setTccreatetime(ElongHotelInterfaceUtil.getCurrentTime());//改签订单创建时间
        trainOrderChange.setTcdeparttime(change_datetime.substring(0, change_datetime.length() - 3));//出发时间
        trainOrderChange.setTcstatus(Trainorderchange.APPLYCHANGE);//状态
        trainOrderChange.setStatus12306(Trainorderchange.WAITORDER);//12306状态
        trainOrderChange.setTcprocedure(0);//手续费
        trainOrderChange.setTcmemo("改签申请成功");//备注
        trainOrderChange.setTcpaystatus(0);//支付状态
        trainOrderChange.setIsQuestionChange(0);//正常订单
        trainOrderChange.setTcischangerefund(0);//是否是退改签  1:退改签  0:改签
        trainOrderChange.setTcoriginalprice(tcOriginalPrice);//原票价总和
        trainOrderChange.setTcislowchange(0);//是否是低改-->1 低改: 改签后价格<=原价格; 0 高改: 改签后价格>原价格
        trainOrderChange.setRequestIsAsync(1);
        trainOrderChange.setRequestCallBackUrl(callbackurl);
        trainOrderChange.setRequestReqtoken(reqtoken);
        trainOrderChange.setTaobaoapplyid(taobaoapplyid);
        if (!"".equals(latest_change_time)) {
            trainOrderChange.setChangetimeout(Timestamp.valueOf(latest_change_time));
        }
        String stationName = "";//车站
        String tcTicketId = "";//车票ID
        String passengerName = "";//乘客姓名
        String updateTicketId = "";//更新车票
        for (String old_ticket_no : oldTicketMap.keySet()) {
            //更新车票
            Trainticket oldTicket = oldTicketMap.get(old_ticket_no);
            Trainpassenger passenger = passengerMap.get(old_ticket_no);
            tcTicketId += oldTicket.getId() + "@";
            updateTicketId += oldTicket.getId() + ",";
            passengerName += passenger.getName() + "<br/>";
            if (!ElongHotelInterfaceUtil.StringIsNull(to_station_name)
                    && !to_station_name.equals(oldTicket.getArrival())) {
                if (order.getInterfacetype() != null && TrainInterfaceMethod.TAOBAO == order.getInterfacetype()) {
                    isTs = reqobj.containsKey("isTs") ? reqobj.getBoolean("isTs") : false;
                    if (isTs) {
                        stationName = oldTicket.getDeparture() + " - " + to_station_name;
                    }
                    else {
                        stationName = oldTicket.getDeparture() + " - " + oldTicket.getArrival();
                    }
                }
                else {
                    isTs = true;
                    stationName = oldTicket.getDeparture() + " - " + to_station_name;
                }
            }
            else {
                stationName = oldTicket.getDeparture() + " - " + oldTicket.getArrival();
            }
        }
        trainOrderChange.setTcTicketId(tcTicketId);
        trainOrderChange.setStationName(stationName);
        trainOrderChange.setPassengerName(passengerName);
        trainOrderChange.setTcnumber((isTs ? "TS" : "TC") + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
                + orderId);//变更号
        trainOrderChange = Server.getInstance().getTrainService().createTrainorderchange(trainOrderChange);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        //改签ID
        long changeId = trainOrderChange.getId();
        //保存数据失败
        if (changeId <= 0) {
            retobj.put("code", code999);
            retobj.put("msg", "请求改签失败");
        }
        else {
            retobj.put("success", true);
            retobj.put("code", "100");
            retobj.put("msg", "改签请求已接受");
            retobj.put("transactionid", reqobj.getString("transactionid"));
            retobj.put("orderid", order.getQunarOrdernumber());
            retobj.put("help_info", "改签请求已接受");
            //记录日志
            String changeTSFlag = isTs ? "变更到站" : "改签";
            createtrainorderrc(1, "[" + changeTSFlag + " - " + changeId + "]提交" + changeTSFlag + "申请成功", orderId, 0l,
                    Trainticket.APPLYCHANGE, "系统接口");
            //车票ID
            updateTicketId = updateTicketId.substring(0, updateTicketId.length() - 1);
            //更新SQL
            String updateTicketSql = "update T_TRAINTICKET set C_CHANGEID = " + changeId + ", C_STATUS = "
                    + Trainticket.APPLYCHANGE + " where ID in (" + updateTicketId + ") and C_STATUS = "
                    + Trainticket.ISSUED;
            WriteLog.write("途牛请求改签Thread", "更新SQL:" + updateTicketSql);
            //更新车票
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateTicketSql);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
            //队列下单
            activeMQChangeOrder(changeId, 1);
        }
    }

    /**
     * 异步改签MQ处理
     */
    public JSONObject AsyncChangeMQ(Trainorderchange trainOrderChange) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        retobj.put("code", code999);
        retobj.put("msg", "改签占座失败");
        //查询订单
        long changeId = trainOrderChange.getId();
        long orderId = trainOrderChange.getOrderid();
        Trainorder trainOrder = Server.getInstance().getTrainService().findTrainorder(orderId);
        retobj.put("agentId", trainOrder.getAgentid());
        retobj.put("orderid", trainOrder.getQunarOrdernumber());
        retobj.put("transactionid", trainOrder.getOrdernumber());
        //查询车票、乘客
        Map<String, Trainticket> oldTicketMap = new HashMap<String, Trainticket>();
        Map<String, Trainpassenger> passengerMap = new HashMap<String, Trainpassenger>();
        //订单对应乘客
        List<Trainpassenger> passengers = trainOrder.getPassengers();
        //原票座席
        String old_zwcode = "";
        //原票价总和
        float tcOriginalPrice = 0f;
        String change_zwcode = trainOrderChange.getTcseattype();
        //下单参数
        String prices = "";
        String zwcodes = "";
        String oldPassengerStr = "";
        String passengerTicketStr = "";
        String old_to_station_name = "";
        JSONArray tickets = new JSONArray();
        List<Integer> piaoTypes = new ArrayList<Integer>();
        Map<String, JSONObject> reqTickets = new HashMap<String, JSONObject>();
        //循环乘客找车票
        for (Trainpassenger passenger : passengers) {
            List<Trainticket> traintickets = passenger.getTraintickets();
            if (traintickets == null || traintickets.size() == 0) {
                return retobj;
            }
            //多张票可能是联程
            for (Trainticket trainticket : traintickets) {
                //申请改签
                if (trainticket.getChangeid() == changeId) {
                    if (trainticket.getStatus() != Trainticket.APPLYCHANGE) {
                        return retobj;
                    }
                    old_zwcode = trainticket.getSeattype();
                    old_to_station_name = trainticket.getArrival();
                    passengerMap.put(trainticket.getTicketno(), passenger);
                    oldTicketMap.put(trainticket.getTicketno(), trainticket);
                    tcOriginalPrice = ElongHotelInterfaceUtil.floatAdd(tcOriginalPrice, trainticket.getPrice());
                    //改签车票信息
                    JSONObject changeTicket = new JSONObject();
                    changeTicket.put("passengersename", passenger.getName());
                    String idtype12306 = getIdtype12306(passenger.getIdtype());
                    WriteLog.write("TongChengReqChange_idtype12306", passenger.getName() + ":" + idtype12306);
                    changeTicket.put("passporttypeseid", idtype12306);
                    changeTicket.put("passportseno", passenger.getIdnumber());
                    changeTicket.put("piaotype", trainticket.getTickettype());
                    changeTicket.put("old_ticket_no", trainticket.getTicketno());
                    //添加车票信息
                    tickets.add(changeTicket);
                    reqTickets.put(trainticket.getTicketno(), changeTicket);
                    //拼下单参数
                    prices += "0@";
                    zwcodes += change_zwcode + "@";
                    String piaotype = String.valueOf(trainticket.getTickettype());
                    if ("2".equals(piaotype)) {
                        oldPassengerStr += "_ ";
                    }
                    else {
                        oldPassengerStr += passenger.getName() + "," + idtype12306 + "," + passenger.getIdnumber()
                                + "," + piaotype + "_";
                    }
                    //无座，12306下单参数为硬座
                    passengerTicketStr += change_zwcode + ",0," + piaotype + "," + passenger.getName() + ","
                            + idtype12306 + "," + passenger.getIdnumber() + ",,Y_";
                    break;
                }
            }
        }
        if (tcOriginalPrice <= 0 || tcOriginalPrice != trainOrderChange.getTcoriginalprice()) {
            return retobj;
        }
        //拼下单请求
        JSONObject reqobj = new JSONObject();
        reqobj.put("orderid", trainOrder.getQunarOrdernumber());
        reqobj.put("transactionid", trainOrder.getOrdernumber());
        reqobj.put("ordernumber", trainOrder.getExtnumber());
        reqobj.put("change_checi", trainOrderChange.getTctrainno());
        reqobj.put("change_datetime", trainOrderChange.getTcdeparttime() + ":00");
        reqobj.put("change_zwcode", change_zwcode);
        reqobj.put("old_zwcode", old_zwcode);
        reqobj.put("ticketinfo", tickets);
        //新车站
        boolean isTs = false;
        String to_station_name = trainOrderChange.getStationName().split("-")[1].trim();
        if (!to_station_name.equals(old_to_station_name)) {
            isTs = true;
            reqobj.put("to_station_name", to_station_name);
        }
        //乘客信息
        JSONObject reqpassengers = new JSONObject();
        reqpassengers.put("prices", prices.substring(0, prices.length() - 1));
        reqpassengers.put("zwcodes", zwcodes.substring(0, zwcodes.length() - 1));
        reqpassengers.put("oldPassengerStr", oldPassengerStr);
        reqpassengers.put("passengerTicketStr", passengerTicketStr.substring(0, passengerTicketStr.length() - 1));
        //操作记录
        String changeTSFlag = isTs ? "变更到站" : "改签";
        createtrainorderrc(1, "[" + changeTSFlag + " - " + changeId + "]开始异步" + changeTSFlag + "占座", orderId, 0l,
                Trainticket.APPLYROCESSING, "系统接口");
        //请求REP
        JSONObject RepOperate = RepOperate(trainOrder, oldTicketMap, passengerMap, reqobj, reqTickets, reqpassengers,
                trainOrderChange.getTcdeparttime(), piaoTypes, 0, trainOrderChange);
        //相关ID
        RepOperate.put("agentId", trainOrder.getAgentid());
        RepOperate.put("orderid", trainOrder.getQunarOrdernumber());
        RepOperate.put("transactionid", trainOrder.getOrdernumber());
        //返回数据
        return RepOperate;
    }

    /**
     * REP操作
     */
    private JSONObject RepOperate(Trainorder order, Map<String, Trainticket> oldTicketMap,
            Map<String, Trainpassenger> passengerMap, JSONObject reqobj, Map<String, JSONObject> reqTickets,
            JSONObject reqpassengers, String change_datetime, List<Integer> piaoTypes, int changeType,
            Trainorderchange trainOrderChange) {
        int changeMax = 5;

        int changeEnLoginMax = 5;

        try {
            String changeMaxStr = getSystemConfig("changeMax");
            if (!"".equals(changeMaxStr)) {
                changeMax = Integer.valueOf(changeMaxStr);
            }
        }
        catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
        try {
            String changeEnLoginMaxStr = getSystemConfig("changeEnLoginMax");
            if (!"".equals(changeEnLoginMaxStr)) {
                changeEnLoginMax = Integer.valueOf(changeEnLoginMaxStr);
            }
        }
        catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //变站
        String to_station_telecode = "";
        String to_station_name = reqobj.containsKey("to_station_name") ? reqobj.getString("to_station_name") : "";
        //Y:变更到站，N:改签
        String changeTSFlag = !ElongHotelInterfaceUtil.StringIsNull(to_station_name) ? "Y" : "N";
        //请求参数
        reqobj.put("changeTSFlag", changeTSFlag);
        //获取到站简码
        if ("Y".equals(changeTSFlag)) {
            to_station_telecode = getThree(to_station_telecode, to_station_name);
            //简码为空
            if (ElongHotelInterfaceUtil.StringIsNull(to_station_telecode)) {
                retobj.put("code", code999);
                retobj.put("msg", "到达站简码为空。");
                return retobj;
            }
            else {
                reqobj.put("to_station_name", to_station_name);
                reqobj.put("to_station_telecode", to_station_telecode);
            }
        }
        //下单账户
        String createAccount = order.getSupplyaccount();
        if (ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
            retobj.put("code", code999);
            retobj.put("msg", "请求改签失败");
            return retobj;
        }
        long orderId = order.getId();
        int random = Integer.parseInt(Long.toString(orderId));//记录日志
        Customeruser user = getCustomeruserBy12306Account(order, random, true);
        //Cookie为空、已经取消三次，今日不可用
        if (user == null || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //账号系统，以未登录释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            retobj.put("code", code999);
            retobj.put("msg", "请求改签失败");
            return retobj;
        }
        //锁账号
        boolean LockTrue = LockCustomeruser(user);
        //时间
        change_datetime = reqobj.getString("change_datetime");
        //请求数据
        reqobj.put("loginName", user.getLoginname() == null ? "" : user.getLoginname());
        reqobj.put("loginPwd", user.getLogpassword() == null ? "" : user.getLogpassword());
        reqobj.put("orderId", orderId);//用于日志记录
        reqobj.put("cookie", user.getCardnunber());
        reqobj.put("order_date", dateFormat.format(order.getCreatetime()));//查询改签订单用
        reqobj.put("train_date", change_datetime.split(" ")[0]);//乘车日期
        reqobj.put("train_date_time", change_datetime);
        reqobj.put("train_code", reqobj.getString("change_checi"));//车次
        reqobj.put("queryLink", PropertyUtil.getValue("queryTicketLink"));//查询车票链接，防止12306变数据，从cn_interface传入
        reqobj.put("seatTypeOf12306", get12306SeatTypes());//12306座席
        //改签退，验证价格
        if (changeType == 1) {
            reqobj.put("orderType", "1");
        }
        reqobj.put("passengers", reqpassengers.toJSONString());
        //座席转换
        String seatChange = PropertyUtil.getValue("seatChange", "Train.properties");
        //空或true转换
        reqobj.put("seatChange",
                ElongHotelInterfaceUtil.StringIsNull(seatChange) || "true".equalsIgnoreCase(seatChange));
        //记录日志
        long start = System.currentTimeMillis();
        WriteLog.write("12306_4.12.请求改签", orderId + ">>>>>请求REP参数>>>>>" + reqobj.toJSONString());
        //请求REP
        String repUrl = "";
        String repName = "";
        String retdata = "";
        JSONObject repobj = new JSONObject();
        RepServerBean rep = new RepServerBean();
        try {
            //获取REP
            rep = RepServerUtil.getRepServer(user, false);
            //REP地址
            repUrl = rep.getUrl();
            repName = rep.getName();
            if (ElongHotelInterfaceUtil.StringIsNull(repUrl)) {
                //释放账号
                if (LockTrue) {
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                }
                //返回信息
                retobj.put("code", code999);
                retobj.put("msg", "请求改签失败");
                return retobj;
            }
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(reqobj.toJSONString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr;
            int changeNum = 0;
            int changeEnLoginNum = 0;
            do {
                long repstart = System.currentTimeMillis();
                //请求REP
                retdata = SendPostandGet.submitPost(repUrl, param, "UTF-8").toString();
                long repsub = System.currentTimeMillis() - repstart;
                //日志
                WriteLog.write("12306_4.12.每次请求改签", orderId + ">>>>>[第" + (changeNum + 1) + "次请求REP改签]>>>>>[ 消耗时间："
                        + repsub / 1000 + "秒(" + repsub + ") ]>>>>>REP服务器地址>>>>>" + repUrl + "[" + repName
                        + "]>>>>>REP返回数据>>>>>" + retdata);
                /**
                 * 如果不满足以下条件 跳出下单循环：
                 * 1、用户未登录 
                 * 2、存在未完成订单，请先支付
                 * 3、订单填写页
                 * 4、多次打码失败
                 * 5、该用户已在其他地点登录，本次登录已失效! 
                 * 6、订单查询出现问题，请重新查询 
                 */
                if (retdata != null && !retdata.contains("用户未登录") && !retdata.contains("存在未完成订单")
                        && !retdata.contains("订单填写页") && !retdata.contains("多次打码") && !retdata.contains("登录已失效")
                        && !retdata.contains("重新查询") && !retdata.contains("提交用户过多")) {
                    break;
                }
                if (retdata.contains("用户未登录") || retdata.contains("登录已失效")) {
                    //账号系统，以未登录释放账号
                    if (user != null && user.isFromAccountSystem()) {
                        freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree,
                                AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                        try {
                            Thread.sleep(500L);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        user = getCustomeruserBy12306Account(order, changeNum, true);
                    }
                    if (changeEnLoginNum < changeEnLoginMax) {
                        changeMax++;
                        changeEnLoginNum++;
                    }

                }
                //如果超时,立即不再循环
                if (!TrainSupplyMethodUtil.isEnChangeTimeOut(order, 1, trainOrderChange)) {
                    break;
                }
                //存在未完成订单 睡眠1分钟后 重新改签
                if (retdata.contains("存在未完成订单")) {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(6000L);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                changeNum++;
            }
            while (changeNum < changeMax);
            //返回数据
            repobj = JSONObject.parseObject(retdata);
        }
        catch (Exception e) {
            if (ElongHotelInterfaceUtil.StringIsNull(retdata)) {
                retdata = "ERROR：" + e.getMessage();
            }
        }
        finally {
            long sub = System.currentTimeMillis() - start;
            //日志
            WriteLog.write("12306_4.12.请求改签", orderId + ">>>>>[ 消耗时间：" + sub / 1000 + "秒(" + sub
                    + ") ]>>>>>REP服务器地址>>>>>" + repUrl + "[" + repName + "]>>>>>REP返回数据>>>>>" + retdata);
            //释放REP
            //freeRep(rep);
        }
        //返回
        if (repobj == null || !repobj.containsKey("success")) {
            //释放账号
            if (LockTrue) {
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            //RETURN
            retobj.put("code", code999);
            retobj.put("msg", "改签失败");
            retobj.put("help_info", "改签失败");
            return retobj;
        }
        if (!repobj.getBooleanValue("success")) {
            if (LockTrue) {
                //订单号
                String sequence_no = repobj.getString("sequence_no");
                //释放账号
                freeCustomeruser(user, ElongHotelInterfaceUtil.StringIsNull(sequence_no) ? AccountSystem.FreeNoCare
                        : AccountSystem.FreeCurrent, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            //RETURN
            String code = ElongHotelInterfaceUtil.getJsonString(repobj, "code");
            String realmsg = ElongHotelInterfaceUtil.getJsonString(repobj, "msg");
            //Code Is Null
            if (ElongHotelInterfaceUtil.StringIsNull(code)) {
                //虚假msg
                String modifymsg = realmsg;
                //错误转换
                retobj = TrainSupplyMethodUtil.codeMsgTongcheng(repobj, 2, realmsg);
                //登录失败
                if (modifymsg.contains("本次登录已失效")) {
                    //将转换存入DB
                    createModifyMsg(modifymsg, "改签失败", trainOrderChange.getId(), 2);
                }
                retobj.put("help_info", "改签失败");
                //已退票、已出票等
                String flagid = "";
                String flagmsg = "";
                if (modifymsg.contains("已出票")) {
                    flagid = "1005";
                    flagmsg = "已出票，请到车站办理";
                }
                else if (modifymsg.contains("已改签")) {
                    flagid = "1006";
                    flagmsg = "已改签，不能再次改签";
                }
                else if (modifymsg.contains("已退票")) {
                    flagid = "1007";
                    flagmsg = "已退票，无法改签";
                }
                else if (modifymsg.contains("如需办理退票、改签和变更到站等变更业务，请持乘车人身份证件原件到就近车站办理")) {
                    flagid = "1008";
                    flagmsg = "旅游票，请到车站办理";
                }
                //非空
                if (!ElongHotelInterfaceUtil.StringIsNull(flagid) && oldTicketMap.size() == 1) {
                    //新车票
                    JSONArray newtickets = new JSONArray();
                    //座位
                    String zwcode = reqobj.getString("change_zwcode");
                    //循环车票
                    for (String old_ticket_no : oldTicketMap.keySet()) {
                        //本地
                        Trainticket ticket = oldTicketMap.get(old_ticket_no);
                        Trainpassenger passenger = passengerMap.get(old_ticket_no);
                        //车票
                        JSONObject newticket = new JSONObject();
                        newticket.put("price", 0);
                        newticket.put("cxin", "");
                        newticket.put("flagid", flagid);
                        newticket.put("zwcode", zwcode);
                        newticket.put("flagmsg", flagmsg);
                        newticket.put("new_ticket_no", "");
                        newticket.put("zwname", getzwcode(zwcode));
                        newticket.put("old_ticket_no", old_ticket_no);
                        newticket.put("piaotype", ticket.getTickettype());
                        newticket.put("passportseno", passenger.getIdnumber());
                        //ADD
                        newtickets.add(newticket);
                    }
                    //PUT
                    retobj.put("newtickets", newtickets);
                }
            }
            else {
                retobj.put("code", code);
                retobj.put("msg", realmsg);
                retobj.put("help_info", "改签失败");
            }
            return retobj;
        }
        //RETURN
        return parse12306(order, reqobj, repobj, oldTicketMap, passengerMap, reqTickets, piaoTypes, changeType,
                trainOrderChange, changeTSFlag);
    }

    /**
     * 校验现有改签
     */
    @SuppressWarnings("rawtypes")
    private JSONObject ChangeCheck(long orderId) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //查询订单所有改签
        String changeSql = "select C_TCSTATUS, C_TCCREATETIME, C_REQUESTREQTOKEN "
                + "from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + orderId;
        List tempList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
        if (tempList != null && tempList.size() > 0) {
            //取消次数
            int cancelCount = 0;
            //当天日期
            String currentDate = ElongHotelInterfaceUtil.getCurrentDate();
            //循环改签
            for (int i = 0; i < tempList.size(); i++) {
                try {
                    Map tempMap = (Map) tempList.get(i);
                    //时间
                    String tcCreatetime = tempMap.get("C_TCCREATETIME").toString();
                    //状态
                    int tcStatus = Integer.parseInt(tempMap.get("C_TCSTATUS").toString());
                    //逻辑判断
                    if (tcStatus == Trainorderchange.CANTCHANGE) {
                        if (tcCreatetime.startsWith(currentDate)) {
                            cancelCount++;
                        }
                    }
                    else if (tcStatus == Trainorderchange.CHANGEWAITPAY || tcStatus == Trainorderchange.CHANGEPAYING) {
                        retobj.put("code", code113);
                        retobj.put("msg", "存在正在确认改签");
                        return retobj;
                    }
                    else if (tcStatus == Trainorderchange.APPLYCHANGE || tcStatus == Trainorderchange.APPLYROCESSING) {
                        retobj.put("code", code113);
                        retobj.put("msg", "存在正在占座改签");
                        return retobj;
                    }
                    else if (tcStatus != Trainorderchange.FINISHCHANGE && tcStatus != Trainorderchange.FAILCHANGE) {
                        retobj.put("code", code113);
                        retobj.put("msg", "存在未完成改签");
                        return retobj;
                    }
                    if (cancelCount >= 3) {
                        retobj.put("code", 1004);
                        retobj.put("msg", "取消改签次数超过上限，无法继续操作");
                        return retobj;
                    }
                }
                catch (Exception e) {
                }
            }
        }
        //TRUE
        retobj.put("success", true);
        //RETURN
        return retobj;
    }

    /**
     * 校验乘客信息
     */
    private JSONObject PassengerCheck(String old_ticket_no, String ticketType, JSONObject reqTicket,
            Trainpassenger passenger) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //请求数据
        String piaotype = reqTicket.getString("piaotype");
        String passportseno = reqTicket.getString("passportseno");
        String passengersename = reqTicket.getString("passengersename");
        String passporttypeseid = reqTicket.getString("passporttypeseid");
        int tcTypeId = TongChengTrainUtil.tongChengIdTypeToLocal(passporttypeseid);
        //本地数据
        int localType = passenger.getIdtype();
        String localNo = passenger.getIdnumber();
        String localName = passenger.getName();
        //返回信息
        String msg = "";
        if (!piaotype.equals(ticketType)) {
            msg = "票种类别不一致";
        }
        else if (!passengersename.equals(localName)) {
            msg = "乘客姓名不一致";
        }
        else if (localType != tcTypeId) {
            msg = "乘客证件类别不一致";
        }
        else if (!passportseno.equalsIgnoreCase(localNo)) {
            msg = "乘客证件号码不一致";
        }
        if (!"".equals(msg)) {
            retobj.put("code", "108");
            retobj.put("msg", "车票[" + old_ticket_no + "]" + msg);
            return retobj;
        }
        //TRUE
        retobj.put("success", true);
        //RETURN
        return retobj;
    }

    /**
     * 校验车票状态
     */
    private JSONObject TicketCheck(Trainticket oldTicket, String old_ticket_no, String departTime,
            String change_datetime, int changeType, boolean isTs) {
        JSONObject retobj = new JSONObject();
        //DEFAULT
        retobj.put("success", false);
        //车票状态
        int ticketStatus = oldTicket.getStatus();
        //申请改签、改签处理中
        if (ticketStatus == Trainticket.APPLYCHANGE) {
            retobj.put("code", code113);
            retobj.put("msg", "车票[" + old_ticket_no + "]改签处理中");
            return retobj;
        }
        //改签通过
        if (ticketStatus == Trainticket.THOUGHCHANGE) {
            retobj.put("code", code113);
            retobj.put("msg", "车票[" + old_ticket_no + "]改签待支付");
            return retobj;
        }
        //确认改签
        if (ticketStatus == Trainticket.FILLMONEY) {
            retobj.put("code", code113);
            retobj.put("msg", "车票[" + old_ticket_no + "]正在确认改签");
            return retobj;
        }
        //一张车票只能办理一次成功的改签，改签后的新票不能再改签
        if (ticketStatus == Trainticket.FINISHCHANGE) {//改签完成
            retobj.put("code", code113);
            retobj.put("msg", "车票[" + old_ticket_no + "]确认改签完成，不能再次进行改签");
            return retobj;
        }
        //改签退
        WriteLog.write("途牛请求改签Thread", "changeType:" + changeType + "&ticketStatus:" + ticketStatus);
        if (changeType == 1) {
            if (ticketStatus != Trainticket.REFUNDROCESSING) {
                retobj.put("code", code113);
                retobj.put("msg", "车票[" + old_ticket_no + "]状态变化，暂不可改签");
                return retobj;
            }
        }
        else {
            //已出票
            if (ticketStatus != Trainticket.ISSUED) {
                retobj.put("code", code113);
                retobj.put("msg", "车票[" + old_ticket_no + "]状态变化，暂不可改签");
                return retobj;
            }
        }
        boolean timeOut = false;//改签必须不晚于原票开车前2小时10分钟方可进行
        boolean hour48 = false;//开车前48小时以内，只能改签至票面日期当日24:00之间
        boolean tstimeout = false;//变站必须在48小时
        int days48 = 0;
        //开车时间
        try {
            //改签时间限制,单位:分钟
            String ChangeTimeLimit = getSysconfigString("ChangeTimeLimit");
            int ChangeTimeLimitIntValue = Integer.parseInt(ChangeTimeLimit) + 10;
            //时间差=开车时间-当前时间
            long timesub = shiFenFormat.parse(departTime).getTime() - System.currentTimeMillis();
            if (!isTs) {
                //开车前2小时10分内
                if (timesub <= ChangeTimeLimitIntValue * 60 * 1000) {
                    timeOut = true;
                }
                //48小时转毫秒，预留10分钟
                else if (timesub <= 2890 * 60 * 1000) {
                    hour48 = true;
                    String departDate = departTime.split(" ")[0];
                    String changeDate = change_datetime.split(" ")[0];
                    days48 = ElongHotelInterfaceUtil.getSubDays(departDate, changeDate);
                }
            }
            else {
                if (timesub <= 2890 * 60 * 1000) {
                    tstimeout = true;
                }
            }
        }
        catch (Exception e) {
        }
        if (timeOut || tstimeout) {
            retobj.put("code", "1002");
            retobj.put("msg", "距离开车时间太近无法改签");
            return retobj;
        }
        if (hour48 && days48 > 0) {
            retobj.put("code", code999);
            retobj.put("msg", "开车前48小时以内，可改签当前到开车日期当日24:00之间的列车，不办理票面日期次日及以后的改签");
            return retobj;
        }
        //TRUE
        retobj.put("success", true);
        //RETURN
        return retobj;
    }

    /**
     * 解析12306
     * @param order 本地订单
     * @param reqobj 请求数据
     * @param repobj REP返回数据
     * @param oldTicketMap 原车票号对应车票
     * @param reqTickets 原车票号对应请求车票
     * @param trainOrderChange 异步改签订单时ID>0
     */
    private JSONObject parse12306(Trainorder order, JSONObject reqobj, JSONObject repobj,
            Map<String, Trainticket> oldTicketMap, Map<String, Trainpassenger> passengerMap,
            Map<String, JSONObject> reqTickets, List<Integer> piaoTypes, int changeType,
            Trainorderchange trainOrderChange, String changeTSFlag) {
        long orderId = order.getId();
        String sequence_no = repobj.getString("sequence_no");
        //异步改签
        boolean IsAsync = trainOrderChange != null && trainOrderChange.getRequestIsAsync() != null
                && trainOrderChange.getRequestIsAsync().intValue() == 1;
        //解析数据
        try {
            boolean rightOrder = false;
            JSONObject orderDB = new JSONObject();
            String orderHtml = repobj.getString("12306");
            JSONArray orderDBList = JSONObject.parseObject(orderHtml).getJSONObject("data").getJSONArray("orderDBList");
            for (int i = 0; i < orderDBList.size(); i++) {
                JSONObject temp12306 = orderDBList.getJSONObject(i);
                if (sequence_no.equals(temp12306.getString("sequence_no"))) {
                    rightOrder = true;
                    orderDB = temp12306;
                    break;
                }
            }
            if (!rightOrder) {
                throw new Exception("未匹配上12306订单[" + sequence_no + "]");
            }
            //手续费
            float fee = 0f;//改签手续费
            float diffrate = 0f;//差额退款费率
            JSONArray changeFees = repobj.getJSONArray("changeFees");
            Map<String, Float> changeFeeMap = new HashMap<String, Float>();
            for (int i = 0; i < changeFees.size(); i++) {
                JSONObject changeFee = changeFees.getJSONObject(i);
                //费率
                diffrate = changeFee.getFloatValue("return_rate");
                //手续费
                float return_cost = changeFee.getFloatValue("return_cost");
                return_cost = ElongHotelInterfaceUtil.floatMultiply(return_cost, 0.01f);
                fee = ElongHotelInterfaceUtil.floatAdd(fee, return_cost);
                //Map
                changeFeeMap.put(changeFee.getString("ticket_no"), return_cost);
            }
            //百分比费率
            String percentageDiffrate = String.valueOf(diffrate);
            //以.0结束
            if (percentageDiffrate.endsWith(".0")) {
                percentageDiffrate = percentageDiffrate.substring(0, percentageDiffrate.length() - 2);
            }
            //以.00结束
            else if (percentageDiffrate.endsWith(".00")) {
                percentageDiffrate = percentageDiffrate.substring(0, percentageDiffrate.length() - 3);
            }
            percentageDiffrate = percentageDiffrate + "%";
            //小数费率
            float decimalDiffrate = 0f;
            //0
            if (diffrate == 0) {
                decimalDiffrate = 0f;
            }
            //5
            else if (diffrate == 5) {
                decimalDiffrate = 0.05f;
            }
            //10
            else if (diffrate == 10) {
                decimalDiffrate = 0.1f;
            }
            //20
            else if (diffrate == 20) {
                decimalDiffrate = 0.2f;
            }
            else {
                decimalDiffrate = ElongHotelInterfaceUtil.floatMultiply(diffrate, 0.01f);
            }
            //价格等参数
            float oldprice = 0;
            float newprice = 0;
            float pricedifference = 0;
            String seat_type_code = "";
            String seat_type_name = "";
            String start_train_date_page = "";
            //改签退，新车票的价格
            float change_price = changeType == 1 ? reqobj.getFloatValue("change_price") : 0;
            //改签后的新车票信息
            JSONArray newtickets = new JSONArray();
            //车站信息
            JSONObject stationTrainDTO = new JSONObject();
            //12306车票信息
            JSONArray tickets = orderDB.getJSONArray("tickets");
            //key: 本地车票ID，value: 12306车票
            Map<Long, JSONObject> ticketJson = new HashMap<Long, JSONObject>();
            //循环12306车票
            for (int i = 0; i < tickets.size(); i++) {
                JSONObject ticket = tickets.getJSONObject(i);
                //车站信息
                stationTrainDTO = ticket.getJSONObject("stationTrainDTO");
                //乘客信息
                JSONObject passengerDTO = ticket.getJSONObject("passengerDTO");
                //证件号
                String passenger_id_no = passengerDTO.getString("passenger_id_no");
                //票类型
                String ticket_type_code = ticket.getString("ticket_type_code");
                //座席、日期
                seat_type_code = ticket.getString("seat_type_code");
                seat_type_name = ticket.getString("seat_type_name");
                start_train_date_page = ticket.getString("start_train_date_page");
                //原票
                String old_ticket_no = "";
                //根据证件号+票类型确定原票，相同时，随便取一个
                for (String temp_old_ticket_no : reqTickets.keySet()) {
                    JSONObject temp = reqTickets.get(temp_old_ticket_no);
                    //车票类型
                    String piaotype = temp.getString("piaotype");
                    //请求证件
                    String passportseno = temp.getString("passportseno");
                    //证件+票类型一致
                    if (ticket_type_code.equalsIgnoreCase(piaotype) && passenger_id_no.equalsIgnoreCase(passportseno)) {
                        old_ticket_no = temp_old_ticket_no;
                        break;
                    }
                }
                //取到了之后，移除原票，排除相同证件购买儿童票
                reqTickets.remove(old_ticket_no);
                //改签后的新车票信息
                JSONObject newticket = new JSONObject();
                newticket.put("piaotype", ticket_type_code);
                newticket.put("passportseno", passenger_id_no);
                newticket.put("new_ticket_no", ticket.getString("ticket_no"));
                newticket.put("old_ticket_no", old_ticket_no);
                String price = ticket.getString("str_ticket_price_page");
                //改签退，价格发生变化
                if (changeType == 1 && change_price != Float.parseFloat(price)) {
                    //取消订单、无改签退了、暂不处理

                    //释放账号、无改签退了、暂不处理

                    //中断返回
                    JSONObject retobj = new JSONObject();
                    retobj.put("success", false);
                    retobj.put("code", code113);
                    retobj.put("msg", "价格发生变化:" + change_price + "#" + price);
                    return retobj;
                }
                newticket.put("price", price);
                newticket.put("zwcode", seat_type_code);
                newticket.put("zwname", seat_type_name);
                newticket.put("flagid", "100");
                newticket.put("flagmsg", "改签成功");
                newticket.put("cxin", ticket.getString("coach_no") + "车厢," + ticket.getString("seat_name"));
                newtickets.add(newticket);
                //旧车票
                Trainticket oldticket = oldTicketMap.get(old_ticket_no);
                oldprice = ElongHotelInterfaceUtil.floatAdd(oldprice, oldticket.getPrice().floatValue());
                newprice = ElongHotelInterfaceUtil.floatAdd(newprice, Float.parseFloat(price));
                ticketJson.put(oldticket.getId(), ticket);
            }
            //返回数据
            JSONObject retobj = new JSONObject();
            retobj.put("success", true);
            retobj.put("code", "100");
            retobj.put("msg", "改签占座成功");
            retobj.put("transactionid", reqobj.getString("transactionid"));
            retobj.put("refund_online", repobj.getIntValue("refund_online"));
            retobj.put("orderid", order.getQunarOrdernumber());
            retobj.put("newtickets", newtickets);
            //1：表示新票款高于原票款
            //2：表示新票款与原票款相等
            //3：表示新票款低于原票款
            int priceinfotype = 0;
            float totalpricediff = 0f;
            String priceinfo = "";
            pricedifference = ElongHotelInterfaceUtil.floatSubtract(newprice, oldprice);
            if (pricedifference > 0) {
                priceinfotype = 1;
                totalpricediff = oldprice;
                priceinfo = "收取新票款：" + newprice + "元，退还原票款：" + oldprice + "元";
            }
            else if (pricedifference == 0) {
                priceinfotype = 2;
                totalpricediff = 0f;
                priceinfo = "改签票款差价：0.0元";
            }
            else {
                priceinfotype = 3;
                totalpricediff = -pricedifference;
                priceinfo = "票款差额：" + totalpricediff + "元，退票费率：" + percentageDiffrate + "，实际退还票款："
                        + ElongHotelInterfaceUtil.floatSubtract(totalpricediff, fee) + "元";
            }
            retobj.put("fee", fee);
            retobj.put("diffrate", decimalDiffrate);
            retobj.put("totalpricediff", ElongHotelInterfaceUtil.floatSubtract(totalpricediff, fee));
            retobj.put("priceinfotype", priceinfotype);
            try {
                retobj.put("priceinfo", URLEncoder.encode(priceinfo, "UTF-8"));
            }
            catch (Exception e) {
                retobj.put("priceinfo", priceinfo);
            }
            retobj.put("pricedifference", pricedifference);
            retobj.put("help_info", "改签占座成功");
            //车站信息
            if (stationTrainDTO == null) {
                stationTrainDTO = new JSONObject();
            }
            retobj.put("to_station_name", stationTrainDTO.getString("to_station_name"));
            retobj.put("to_station_code", stationTrainDTO.getString("to_station_telecode"));
            retobj.put("from_station_name", stationTrainDTO.getString("from_station_name"));
            retobj.put("from_station_code", stationTrainDTO.getString("from_station_telecode"));
            retobj.put("reqtoken", reqobj.containsKey("reqtoken") ? reqobj.getString("reqtoken") : "");
            //改签ID
            long changeid = 0;
            float procedure = 0f;//改签前退票手续费
            String change_checi = reqobj.getString("change_checi");
            String to_station_name = reqobj.containsKey("to_station_name") ? reqobj.getString("to_station_name") : "";
            if (changeType == 1) {
                String refundTicket = reqobj.getString("refundTicket");
                if (refundTicket != null && refundTicket.contains("不直接退票，获取退票手续费成功")) {
                    procedure = JSONObject.parseObject(refundTicket).getFloatValue("return_cost");
                }
            }
            try {
                //新增改签
                if (!IsAsync) {
                    trainOrderChange = new Trainorderchange();
                    trainOrderChange.setTcnumber(("Y".equals(changeTSFlag) ? "TS" : "TC")
                            + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + orderId);//变更号
                }
                trainOrderChange.setOrderid(orderId);
                trainOrderChange.setTctrainno(change_checi);//改签车次
                trainOrderChange.setTctickettype(piaoTypes.size() == 1 ? piaoTypes.get(0) : 1);//票类型
                trainOrderChange.setTcseattype(seat_type_name);//座席名称
                trainOrderChange.setSeatTypeCode(seat_type_code);//座席编码
                trainOrderChange.setTcprice(newprice);//新价格
                trainOrderChange.setTccreatetime(orderDB.getString("order_date"));
                trainOrderChange.setTcdeparttime(start_train_date_page);//出发时间
                trainOrderChange.setTcstatus(Trainorderchange.THOUGHCHANGE);//状态
                trainOrderChange.setStatus12306(Trainorderchange.ORDEREDWAITPAY);//12306状态
                trainOrderChange.setTcprocedure(pricedifference);//手续费
                trainOrderChange.setChangeProcedure(fee);//改签手续费
                trainOrderChange.setChangeRate(diffrate);
                if (changeType == 1) {
                    trainOrderChange.setTcmemo("自动改签退，改签成功，" + priceinfo);//备注
                }
                else {
                    trainOrderChange.setTcmemo("改签成功，" + priceinfo);//备注
                }
                trainOrderChange.setTcpaystatus(0);//支付状态
                trainOrderChange.setIsQuestionChange(0);//正常订单
                trainOrderChange.setTcischangerefund(changeType);//是否是退改签  1:退改签  0:改签
                trainOrderChange.setTcoriginalprice(oldprice);//原票价总和
                trainOrderChange.setTcislowchange(pricedifference > 0 ? 0 : 1);//是否是低改-->1 低改: 改签后价格<=原价格; 0 高改: 改签后价格>原价格
                trainOrderChange.setIscanrefundonline(repobj.getIntValue("refund_online"));
                String stationName = "";//车站
                String tcTicketId = "";//车票ID
                String passengerName = "";//乘客姓名
                for (String old_ticket_no : oldTicketMap.keySet()) {
                    //更新车票
                    Trainticket oldTicket = oldTicketMap.get(old_ticket_no);
                    Trainpassenger passenger = passengerMap.get(old_ticket_no);
                    tcTicketId += oldTicket.getId() + "@";
                    passengerName += passenger.getName() + "<br/>";
                    if ("Y".equals(changeTSFlag)) {
                        stationName = oldTicket.getDeparture() + " - " + to_station_name;
                    }
                    else {
                        stationName = oldTicket.getDeparture() + " - " + oldTicket.getArrival();
                    }
                }
                trainOrderChange.setTcTicketId(tcTicketId);
                trainOrderChange.setStationName(stationName);
                trainOrderChange.setPassengerName(passengerName);
                trainOrderChange.setToStationName(stationTrainDTO.getString("to_station_name"));
                trainOrderChange.setToStationCode(stationTrainDTO.getString("to_station_telecode"));
                trainOrderChange.setFromStationName(stationTrainDTO.getString("from_station_name"));
                trainOrderChange.setFromStationCode(stationTrainDTO.getString("from_station_telecode"));
                //异步，更新
                if (IsAsync) {
                    Server.getInstance().getTrainService().updateTrainorcerchange(trainOrderChange);
                }
                else {
                    trainOrderChange = Server.getInstance().getTrainService().createTrainorderchange(trainOrderChange);
                }
                //改签ID
                changeid = trainOrderChange.getId();
                if (changeid <= 0 && changeType == 0) {
                    throw new Exception("保存改签订单信息失败");
                }
                //更新车票
                for (String old_ticket_no : oldTicketMap.keySet()) {
                    //更新车票
                    Trainticket oldTicket = oldTicketMap.get(old_ticket_no);
                    JSONObject newTicket = ticketJson.get(oldTicket.getId());
                    //赋值
                    oldTicket.setChangeid(changeid);
                    //改签退，不修改车票状态，还是退票中；设置原票退票手续费
                    if (changeType == 1) {
                        oldTicket.setProcedure(procedure);
                        //改签退票手续费，默认跟直接退票手续费一样
                        oldTicket.setTcProcedure(procedure);
                    }
                    else {
                        oldTicket.setStatus(Trainticket.THOUGHCHANGE);
                    }
                    oldTicket.setState12306(Trainticket.CHANGEDWAITPAY);
                    oldTicket.setTctrainno(change_checi);
                    oldTicket.setTsArrival(to_station_name);
                    oldTicket.setTcseatno(newTicket.getString("seat_name"));
                    oldTicket.setTccoach(newTicket.getString("coach_no"));
                    oldTicket.setTcticketno(newTicket.getString("ticket_no"));
                    oldTicket.setTtcseattype(newTicket.getString("seat_type_name"));
                    oldTicket.setTtcdeparttime(trainOrderChange.getTcdeparttime());
                    oldTicket.setChangeProcedure(changeFeeMap.get(oldTicket.getTcticketno()));
                    oldTicket.setTcPrice(Float.parseFloat(newTicket.getString("str_ticket_price_page")));
                    //更新
                    Server.getInstance().getTrainService().updateTrainticket(oldTicket);
                }
            }
            catch (Exception e) {
            }
            if (changeid <= 0 && changeType == 0) {
                throw new Exception("保存改签订单信息失败");
            }
            //记录日志
            try {
                //创建者
                String createuser = changeType == 1 ? "自动改签退" : "系统接口";
                //日志内容
                String content = "";
                //变更到站
                if ("Y".equals(changeTSFlag)) {
                    content = "[变更到站 - " + changeid + "]<span style='color:red;'>变更到站占座成功</span>，新到站："
                            + to_station_name + "，新车次：" + change_checi + "，发车时间：" + start_train_date_page + "，新票款："
                            + newprice + "元，原票款：" + oldprice + "元，差价手续费：" + fee;
                }
                else {
                    content = "[改签 - " + changeid + "]<span style='color:red;'>改签占座成功</span>，新车次：" + change_checi
                            + "，发车时间：" + start_train_date_page + "，新票款：" + newprice + "元，原票款：" + oldprice + "元，差价手续费："
                            + fee;
                }
                //改签退
                if (changeType == 1) {
                    content += "，原退票手续费：" + procedure + "元";
                }
                //保存日志
                createtrainorderrc(1, content, orderId, 0l, Trainticket.THOUGHCHANGE, createuser);
            }
            catch (Exception e) {
                System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + " 接口改签，记录日志异常，订单ID：" + orderId + "。");
            }
            return retobj;
        }
        catch (Exception e) {
        }
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        retobj.put("code", code113);
        retobj.put("msg", "改签成功，解析新车票信息失败，需确认");
        return retobj;
    }

    private String getThree(String station_code, String station_name) {
        if (ElongHotelInterfaceUtil.StringIsNull(station_code)) {
            try {
                station_code = Train12306StationInfoUtil.getThreeByName(station_name);
            }
            catch (Exception e) {
                station_code = "";
            }
        }
        return station_code;
    }

    /**
     * 创建修改下单MSG的记录表
     * @param oldmsg
     * @param newmsg
     * @param orderid
     * @param type
     */
    private void createModifyMsg(String oldmsg, String newmsg, long orderid, int type) {
        try {
            Timestamp createtime = new Timestamp(System.currentTimeMillis());
            String sql = "INSERT INTO TrainOrderModifyMsg "
                    + "([OldMsg] ,[NewMsg] ,[OrderId] ,[CreateTime] ,[Type] ,[Remark])" + "VALUES " + " ('" + oldmsg
                    + "','" + newmsg + "'," + orderid + ",'" + createtime + "'," + type + ",''  )";
            WriteLog.write("TrainCreateOrder_createModifyMsg", sql);
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断McCached有没有值,有就返回false,没有,添加值并返回true
     * 
     * @param orderid
     * @return
     * @time 2015年8月22日 下午1:47:54
     * @author wcl
     */
    public boolean setMemCachedTrainorder(String orderid, String guid) {
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        WriteLog.write("途牛请求改签Thread", "orderid:" + orderid + "--mccguid:" + mccguid);
        if (mccguid == null || "".equals(mccguid)) {
            OcsMethod.getInstance().add("confirm=" + orderid, guid, 120);
            WriteLog.write("途牛请求改签Thread", "orderid:" + orderid + "--guid:" + guid + "--mccguid" + mccguid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 判断MemCached中有没有数据且数据是否一致, 有:true,没有false
     * 
     * @param orderid
     * @return
     * @time 2015年8月18日 下午6:52:17
     * @author wcl
     */
    public boolean getMemCachedTrainorder(String orderid, String guid) {
        WriteLog.write("途牛请求改签Thread", "orderid" + orderid + "-" + "guid:" + guid);
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        if (mccguid != null || "".equals(mccguid) && OcsMethod.getInstance().get("confirm=" + orderid).equals(guid)) {
            WriteLog.write("途牛请求改签Thread", "orderid" + orderid + "-" + "guid:" + guid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 回调
     * 
     * @param retobj
     * @time 2015年8月22日 下午1:51:22
     * @author wcl
     */
    @SuppressWarnings("rawtypes")
    public void tuniuReqChangeCallBack(String partnerid, boolean existBool) {
        boolean bool = false;
        String url = PropertyUtil.getValue("TuNiu_CallBack_Url", "Train.properties");
        String sql = "SELECT C_CONFIRMCHANGECALLBACKURL,C_REQUESTCHANGECALLBACKURL FROM T_INTERFACEACCOUNT with(nolock) WHERE C_USERNAME='"
                + partnerid + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        Map map = (Map) list.get(0);
        String requestChangeCallBackUrl = map.get("C_REQUESTCHANGECALLBACKURL").toString();
        JSONObject jso = new JSONObject();
        jso.put("method", "train_request_change");
        jso.put("orderid", orderid);
        jso.put("transactionid", transactionid);
        jso.put("agentId", partnerid);
        jso.put("callBackUrl", requestChangeCallBackUrl);
        WriteLog.write("途牛请求改签Thread", "回调jso:" + jso.toString());

        for (int i = 0; i < 6; i++) {
            String resultUrlString = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();

            if (resultUrlString.equals("SUCCESS")) {
                OcsMethod.getInstance().remove("confirm=" + orderid);
                WriteLog.write("途牛请求改签Thread", "第" + i + "回调:" + resultUrlString);
                bool = true;
                break;
            }
            WriteLog.write("途牛请求改签Thread", "第" + i + "回调" + resultUrlString);
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //如果没有回调成功,将订单设置成采购订单
        if (!bool && existBool) {
            try {
                String update = "UPDATE T_TRAINORDERCHANGE SET C_ISQUESTIONCHANGE=3 WHERE C_ORDERID=" + orderid;
                Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
            }
            catch (Exception e) {
                WriteLog.write("err_TuniuReqChangeThread", orderid);
                ExceptionUtil.writelogByException("err_TuniuReqChangeThread", e);
            }
        }

    }

}
