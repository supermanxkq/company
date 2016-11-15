package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.GuidUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 途牛在线退票Thread
 * 
 * @time 2015年8月26日 上午12:09:39
 * @author Administrator
 */
public class TuniuReturnTicketThread extends TongchengSupplyMethod implements Runnable {
    int random;

    int ticketSize;

    String datatypeflag;

    String orderid;

    String reqtoken;

    String transactionid;

    String ordernumber;

    SimpleDateFormat shiFenFormat;

    SimpleDateFormat timeFormat;

    Map<String, Integer> refundTypeMap;

    Map<String, JSONObject> refundTicketMap;

    public TuniuReturnTicketThread(int random, int ticketSize, String datatypeflag, String orderid, String reqtoken,
            String transactionid, String ordernumber, Map<String, JSONObject> refundTicketMap,
            Map<String, Integer> refundTypeMap, SimpleDateFormat shiFenFormat, SimpleDateFormat timeFormat) {

        this.random = random;
        this.ticketSize = ticketSize;
        this.datatypeflag = datatypeflag;
        this.orderid = orderid;
        this.reqtoken = reqtoken;
        this.transactionid = transactionid;
        this.ordernumber = ordernumber;
        this.refundTicketMap = refundTicketMap;
        this.refundTypeMap = refundTypeMap;
        this.shiFenFormat = shiFenFormat;
        this.timeFormat = timeFormat;
    }

    @Override
    public void run() {
        WriteLog.write("途牛确认退票回调Thread", datatypeflag + orderid + reqtoken + transactionid + ordernumber);
        boolean McCached = false;
        String guid = GuidUtil.getUuid();
        McCached = setMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        // 查询订单
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        // 订单不存在
        if (orders == null || orders.size() == 0) {
            //TODO AAA
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "订单不存在");
            trainorderCallBackInfo(null, orderid);
            return;
        }

        Trainorder order = new Trainorder();
        if (orders.size() > 0) {
            order = orders.get(0);
        }
        if (order.getOrderstatus() == Trainorder.ISSUED) {
            // 继续取第一个
        }
        else if (orders.size() == 2) {
            order = orders.get(1);
        }
        // 12306订单号
        if (!ordernumber.equals(order.getExtnumber())) {
            WriteLog.write("途牛确认退票回调Thread",
                    "12306订单号---ordernumber:" + ordernumber + "——Extnumber:" + order.getExtnumber());
            trainorderCallBackInfo(order, orderid);
            return;
        }
        // 订单状态
        int status = order.getOrderstatus();
        WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "订单状态---status:" + status);
        if (status != Trainorder.ISSUED) {
            trainorderCallBackInfo(order, orderid);
            return;
        }
        // 加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        // 获取订单对应乘客
        List<Trainpassenger> passengers = order.getPassengers();
        // 单个车票号，暂时只能直接退一张
        String SingleTicketNo = "";
        // 发车Long时间
        long trainStartTime = 0;
        // 已申请过退票
        Map<String, Boolean> requestedMap = new HashMap<String, Boolean>();
        // 退票集合
        Map<String, Trainticket> refundMap = new HashMap<String, Trainticket>();
        // 验证车票，KEY：同程票号
        for (String tc_ticket_no : refundTicketMap.keySet()) {
            int ticketType = 0;// 1：正常；2：改签
            Trainticket localTicket = null;
            // 循环乘客找车票
            out: for (Trainpassenger passenger : passengers) {
                List<Trainticket> traintickets = passenger.getTraintickets();
                if (traintickets == null || traintickets.size() == 0) {
                    continue;
                }
                // 多张票可能是联程
                for (Trainticket trainticket : traintickets) {
                    String ticketno = trainticket.getTicketno();// 票号
                    String tcticketno = trainticket.getTcticketno();// 改签票号
                    WriteLog.write("途牛确认退票回调Thread", "票号:" + ticketno + "&改签票号:" + tcticketno + "&tc_ticket_no:"
                            + tc_ticket_no);
                    if (tc_ticket_no.equals(ticketno) || tc_ticket_no.equals(tcticketno)) {
                        if (localTicket == null) {
                            if (tc_ticket_no.equals(ticketno)) {
                                ticketType = 1;
                            }
                            else {
                                ticketType = 2;
                            }
                            localTicket = trainticket;
                        }
                        else {
                            ticketType = 0;
                            localTicket = null;
                            break out;
                        }
                    }
                }
            }
            WriteLog.write("途牛确认退票回调Thread", "localTicket:" + localTicket + "——ticketType:" + ticketType);
            if (localTicket == null || ticketType == 0) {
                WriteLog.write("途牛确认退票回调Thread", "票号[" + tc_ticket_no + "]对应的车票未找到");
                trainorderCallBackInfo(order, orderid);
                return;
            }
            // 车票状态
            int ticketStatus = localTicket.getStatus();

            // 改签类型 1:线上改签、2:改签退
            int changeType = localTicket.getChangeType() == null ? 0 : localTicket.getChangeType().intValue();
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "车票状态---status:" + ticketStatus + "&ticketType:"
                    + ticketType + "&changeType:" + changeType);
            // 改签票必须得线上(分析：已线下改签与线上矛盾、改签退不返回票号)
            if (ticketType == 2 && changeType != 1) {
                WriteLog.write("途牛确认退票回调Thread", "票号[" + tc_ticket_no + "]对应的车票状态变化，暂不可退");
                trainorderCallBackInfo(order, orderid);
                return;
            }
            WriteLog.write("途牛确认退票回调Thread", "车站改签、必须申请线下退票" + ticketStatus + "&" + changeType);
            // 车站改签、必须申请线下退票
            if (ticketStatus == Trainticket.FINISHCHANGE && changeType > 4 && changeType < 12) {
                // 申请线上退票
                if (refundTypeMap.get(tc_ticket_no) == 1) {
                    WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "票号[" + tc_ticket_no + "]对应的车票状态变化，暂不可线上退票"
                            + refundTypeMap.get(tc_ticket_no));
                    trainorderCallBackInfo(order, orderid);
                    return;
                }
                // 申请线下退票、改签已退款
                if (changeType == 10) {
                    WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "申请线下退票" + changeType + ":" + changeType);
                    // 车站低改，原票价>改签票价
                    if (localTicket.getPrice().floatValue() > localTicket.getTcnewprice().floatValue()) {
                        if (localTicket.getTcnewprice().floatValue() > 0) {
                            ticketStatus = Trainticket.ISSUED;
                        }
                        else {
                            WriteLog.write("途牛确认退票回调Thread", "票号[" + tc_ticket_no + "]对应的车票改签退款中");
                            trainorderCallBackInfo(order, orderid);
                            return;
                        }
                    }
                    else {
                        WriteLog.write("途牛确认退票回调Thread", "票号[" + tc_ticket_no + "]对应的车票已改签全退");
                        trainorderCallBackInfo(order, orderid);
                        return;
                    }
                }
                else {
                    WriteLog.write("途牛确认退票回调Thread", "票号[" + tc_ticket_no + "]对应的车票改签退款中");
                    trainorderCallBackInfo(order, orderid);
                    return;
                }
            }
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "申请线下退票,ticketType:" + ticketType + ":ticketStatus"
                    + ticketStatus);
            if ((ticketType == 1 && ticketStatus != Trainticket.ISSUED)
                    || (ticketType == 2 && ticketStatus != Trainticket.FINISHCHANGE)) {
                // 所有请求都接收，返回同程接收成功，判断状态 --> 申请退票以前、退款失败以后
                if (ticketStatus < Trainticket.APPLYTREFUND || ticketStatus > Trainticket.REFUNDFAIL) {
                    WriteLog.write("途牛确认退票回调Thread", "票号[" + tc_ticket_no + "]对应的车票状态变化，暂不可退");
                    trainorderCallBackInfo(order, orderid);
                    return;
                }
                else {
                    requestedMap.put(tc_ticket_no, true);
                }
            }
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "线上退票,refundTypeMap.get(tc_ticket_no):"
                    + refundTypeMap.get(tc_ticket_no));
            // 线上退票
            if (refundTypeMap.get(tc_ticket_no) == 1) {
                // 退票必须不晚于原票开车前2小时方可进行
                boolean timeOut = false;
                // 开车String时间
                String departTime = ticketType == 1 ? localTicket.getDeparttime() : localTicket.getTtcdeparttime();
                try {
                    //退票时间限制,单位:分钟
                    String RefundTimeLimit = getSysconfigString("RefundTimeLimit");
                    int RefundTimeLimitIntValue = Integer.parseInt(RefundTimeLimit);
                    // 开车Long时间
                    trainStartTime = shiFenFormat.parse(departTime).getTime();
                    // 开车时间-当前时间
                    long timesub = trainStartTime - System.currentTimeMillis();
                    // 开车前2小时以内
                    if (RefundTimeLimitIntValue > 0 && timesub <= RefundTimeLimitIntValue * 60 * 1000) {
                        timeOut = true;
                    }
                }
                catch (Exception e) {
                    String log = "订单[" + order.getId() + "]>>>接口退票，车票[" + tc_ticket_no + "]发车时间[" + departTime + "]错误。";
                    WriteLog.write("t同程火车票接口_4.10在线退票", random + ":result:" + log);
                    ExceptionUtil.writelogByException("err_tuniu", e);
                }
                if (timeOut) {
                    WriteLog.write("途牛确认退票回调Thread", "车票[" + tc_ticket_no + "]距离开车时间太近无法退票");
                    trainorderCallBackInfo(order, orderid);
                    return;
                }
            }
            SingleTicketNo = tc_ticket_no;
            refundMap.put(tc_ticket_no, localTicket);
        }
        // 走异步
        boolean AutoRefundOpen = false;
        // 账号
        Customeruser user = new Customeruser();
        // 自动退票、张数为1、线上
        if (AutoRefundOpen && ticketSize == 1 && refundTypeMap.get(SingleTicketNo) == 1) {
            // 下单用户名
            String createAccount = order.getSupplyaccount();
            // 用户名非空
            if (!ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
                // 下单账户
                user = getCustomeruserBy12306Account(order, random, true);
            }
        }
        // 不直接退票
        if (user == null || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            // 更新本地为申请
            int SuccessTotal = refundApply(refundMap, requestedMap, refundTicketMap, refundTypeMap, order, reqtoken,
                    random);
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "不直接退票,更新本地为申请,SuccessTotal:" + SuccessTotal);
            // 返回数据
            if (SuccessTotal != ticketSize) {
                WriteLog.write("途牛确认退票回调Thread", "退票请求失败,orderid:" + orderid);
                trainorderCallBackInfo(order, orderid);
                return;
                //                return returnError();
            }
            else {
                WriteLog.write("途牛确认退票回调Thread", "退票请求已接收，正在处理,orderid:" + orderid);
                return;
                //                return returnOk(orderid, ordernumber, reqtoken, ticketSize, 0);
            }
        }
        WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "单个车票已请求,SingleTicketNo:" + SingleTicketNo);
        // 单个车票已请求
        if (requestedMap.containsKey(SingleTicketNo)) {
            trainorderCallBackInfo(order, orderid);
            return;
            //            return returnOk(orderid, ordernumber, reqtoken, ticketSize, 0);
        }
        // 本地车票
        Trainticket ticket = refundMap.get(SingleTicketNo);
        // 同程信息
        JSONObject tcTicket = refundTicketMap.get(SingleTicketNo);
        // 退票
        String refundResult = refundSingle(ticket, tcTicket, order, reqtoken, user, SingleTicketNo, trainStartTime,
                random);
        WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "退票refundResult:" + refundResult);
        // 成功
        if ("ok".equals(refundResult)) {
            // 返回数据
            returnOk(orderid, ordernumber, reqtoken, ticketSize, 1);
        }
        // 退票
        else if ("apply".equals(refundResult)) {
            // 更新本地为申请
            int SuccessTotal = refundApply(refundMap, requestedMap, refundTicketMap, refundTypeMap, order, reqtoken,
                    random);
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "更新本地为申请SuccessTotal:" + SuccessTotal);
            // 返回数据
            if (SuccessTotal != ticketSize) {
                WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "退票请求失败");
                trainorderCallBackInfo(order, orderid);
                return;
                //                returnError();
            }
            else {
                WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "退票请求已接收，正在处理");
                return;
                //                                                return returnOk(orderid, ordernumber, reqtoken, ticketSize, 0);
            }
        }
        else {
            trainorderCallBackInfo(order, orderid);
            return;
            //                         refundResult;
        }

    }

    /**
     * 退票申请，更新本地车票，记录日志
     * 
     * @param refundMap
     *            退票集合，key：车票号，value：对应本地车票
     * @param requestedMap
     *            已申请退票集合
     * @param refundTicketMap
     *            key：车票号，value：退票JSON，乘客信息等
     * @param refundTypeMap
     *            退票类型，key：车票号，value：1、线上 2、线下
     */
    private int refundApply(Map<String, Trainticket> refundMap, Map<String, Boolean> requestedMap,
            Map<String, JSONObject> refundTicketMap, Map<String, Integer> refundTypeMap, Trainorder order,
            String reqtoken, int random) {
        // 成功个数
        int SuccessTotal = 0;
        try {
            // 循环退票
            for (String ticket_no : refundMap.keySet()) {
                // 本地车票
                Trainticket ticket = refundMap.get(ticket_no);
                // 退票类型，1:线上；2:线下
                int isApplyTicket = refundTypeMap.get(ticket_no);
                // 已申请退票
                if (requestedMap.containsKey(ticket_no)) {
                    // 非无法退票
                    if (ticket.getStatus() != Trainticket.NONREFUNDABLE) {
                        SuccessTotal++;
                        continue;
                    }
                }
                String updateSql = "update T_TRAINTICKET set C_ORDERID = " + order.getId() + ", C_STATUS = "
                        + Trainticket.APPLYTREFUND + ", C_INSURENO = '" + reqtoken + "', C_ISAPPLYTICKET = "
                        + isApplyTicket + ", C_REFUNDREQUESTTIME = '" + ElongHotelInterfaceUtil.getCurrentTime()
                        + "' where ID = " + ticket.getId();
                // 更新成功
                if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) == 1) {
                    SuccessTotal++;
                    // 同程信息
                    JSONObject tcTicket = refundTicketMap.get(ticket_no);
                    // 乘客信息
                    String passportseno = tcTicket.getString("passportseno");// 证件号
                    String passengername = tcTicket.getString("passengername");// 乘客姓名
                    // 日志内容
                    String ticketType = getTicketType(ticket);// 票类型 1:成人票，2:儿童票，3:学生票，4:残军票
                    String logContent = "乘客[" + passengername + "][" + passportseno + "][" + ticketType + "]申请"
                            + (isApplyTicket == 2 ? "<span style='color:red;'>线下</span>" : "") + "退票，票号：" + ticket_no;
                    // 日志
                    createtrainorderrc(1, logContent, order.getId(), ticket.getId(), Trainticket.APPLYTREFUND);
                }
            }
        }
        catch (Exception e) {
            String log = "订单[" + order.getId() + "]>>>接口退票，更新车票状态为申请退票异常，请求已接收: " + ElongHotelInterfaceUtil.errormsg(e);
            WriteLog.write("t同程火车票接口_4.10在线退票", random + ">>>" + log);
            ExceptionUtil.writelogByException("err_tuniu", e);
        }
        return SuccessTotal;
    }

    /**
     * 票类型
     */
    private String getTicketType(Trainticket ticket) {
        // 票类型
        int tickettype = ticket.getTickettype();
        // 判断类型
        if (tickettype == 1) {
            return "成人票";
        }
        if (tickettype == 2) {
            return "儿童票";
        }
        if (tickettype == 3) {
            return "学生票";
        }
        if (tickettype == 4) {
            return "残军票";
        }
        return "票类型待定：" + tickettype;
    }

    /**
     * 返回同程失败信息
     */
    private String returnError() {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        retobj.put("code", "113");// 当前时间不提供服务
        retobj.put("msg", "退票请求失败");
        return retobj.toJSONString();
    }

    /**
     * 返回同程成功信息
     * 
     * @param SuccessSize
     *            12306退票成功个数
     */
    private String returnOk(String orderid, String ordernumber, String reqtoken, int ticketSize, int SuccessSize) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", true);
        retobj.put("code", "802");
        retobj.put("msg", "退票请求已接收");
        retobj.put("orderid", orderid);
        retobj.put("ordernumber", ordernumber);
        if (SuccessSize > 0 && SuccessSize == ticketSize) {
            // retobj.put("tooltip", "退票成功，等待退款");
            retobj.put("tooltip", "退票请求已接收，正在处理");
        }
        else {
            retobj.put("tooltip", "退票请求已接收，正在处理");
        }
        retobj.put("reqtoken", reqtoken);
        // RETURN
        return retobj.toJSONString();
    }

    /**
     * 12306单个退票
     */
    private String refundSingle(Trainticket ticket, JSONObject tcTicket, Trainorder order, String reqtoken,
            Customeruser user, String ticket_no, long trainStartTime, int random) {
        String cookie = user.getCardnunber();
        // 订单创建时间，用于12306通过时间查询订单
        String order_date = timeFormat.format(order.getCreatetime()).split(" ")[0];
        // 12306单号
        String sequence_no = order.getExtnumber();
        // 乘客信息
        String passengername = tcTicket.getString("passengername");// 乘客姓名
        // 请求REP参数
        JSONObject req = new JSONObject();
        req.put("cookie", cookie);
        req.put("ticket_no", ticket_no);
        req.put("order_date", order_date);
        req.put("sequence_no", sequence_no);
        // 用于查订单
        req.put("passenger_name", passengername);
        //退票时间限制,单位:分钟
        String RefundTimeLimit = getSysconfigString("RefundTimeLimit");
        int RefundTimeLimitIntValue = Integer.parseInt(RefundTimeLimit);
        req.put("RefundTimeLimit", RefundTimeLimitIntValue);
        req.put("refundType", PropertyUtil.getValue("refundType"));
        req.put("refundTime", RefundTimeLimitIntValue + 60);//退票时间+1小时内，直接退

        /******************* 时间差判断，24、48小时临界点，半小时内(暂定)直接退 *******************/
        boolean refundFlag = false;
        // 发车时间
        if (trainStartTime > 0) {
            // 开车时间-当前时间
            long timesub = trainStartTime - System.currentTimeMillis();
            // 24H@48H@30
            String[] refundTimeDifference = PropertyUtil.getValue("refundTimeDifference").split("@");
            // 长度
            int refundTimeDifferenceLen = refundTimeDifference.length;
            // 时间差Long值
            long diffTime = Long.parseLong(refundTimeDifference[refundTimeDifferenceLen - 1]) * 60 * 1000;
            // 循环判断
            for (int i = 0; i < refundTimeDifferenceLen - 1; i++) {
                String TempTime = refundTimeDifference[i];
                // 结尾
                String TimeType = TempTime.substring(TempTime.length() - 1);
                // 时间String值
                TempTime = TempTime.substring(0, TempTime.length() - 1);
                // 时间Long值
                long TempLongTime = Long.parseLong(TempTime);
                // 分钟
                if ("M".equalsIgnoreCase(TimeType)) {
                    TempLongTime = TempLongTime * 60 * 1000;
                }
                // 小时
                else if ("H".equalsIgnoreCase(TimeType)) {
                    TempLongTime = TempLongTime * 60 * 60 * 1000;
                }
                // 天
                else if ("D".equalsIgnoreCase(TimeType)) {
                    TempLongTime = TempLongTime * 24 * 60 * 60 * 1000;
                }
                if (timesub > TempLongTime && timesub < TempLongTime + diffTime) {
                    refundFlag = true;
                    break;
                }
            }
        }
        else {
            refundFlag = true;
        }
        req.put("refundFlag", refundFlag);
        /******************* 时间差判断，24、48小时临界点，半小时内(暂定)直接退 *******************/

        // REP
        String url = "";
        String retdata = "";
        JSONObject obj12306 = new JSONObject();
        try {
            // REP地址
            url = RepServerUtil.getRepServer(user, false).getUrl();
            // REP地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(url)) {
                throw new Exception("REP地址为空");
            }
            // 请求参数URLEncoder
            String jsonStr = URLEncoder.encode(req.toJSONString(), "UTF-8");
            // 请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr;
            // 请求REP
            retdata = SendPostandGet.submitPost(url, param, "UTF-8").toString();
            // 返回数据
            obj12306 = JSONObject.parseObject(retdata);
        }
        catch (Exception e) {
        }
        finally {
            if (obj12306 == null) {
                obj12306 = new JSONObject();
            }
            WriteLog.write("t同程火车票接口_4.10在线退票", random + ">>>>>>" + order.getId() + ">>>>>REP服务器地址>>>>>" + url
                    + ">>>>>REP返回>>>>>" + retdata);
        }
        // 解析REP返回数据
        String code = obj12306.getString("code");
        // 成功
        if (obj12306.getBooleanValue("success")) {
            refundSuccess(order, obj12306, ticket, tcTicket, ticket_no, reqtoken, random);
            // 返回数据
            return "ok";
        }
        // 不可退票标示
        else if (!ElongHotelInterfaceUtil.StringIsNull(code)) {
            JSONObject retobj = new JSONObject();
            retobj.put("success", false);
            retobj.put("code", code);
            retobj.put("msg", obj12306.getString("msg"));
            return retobj.toJSONString();
        }
        return "apply";
    }

    /**
     * 单个退票成功
     */
    private void refundSuccess(Trainorder order, JSONObject obj12306, Trainticket ticket, JSONObject tcTicket,
            String ticket_no, String reqtoken, int random) {
        try {
            // 车票票款
            float ticket_price = obj12306.getFloatValue("ticket_price");
            // 应退票款
            float return_price = obj12306.getFloatValue("return_price");
            // 退票费
            float return_cost = obj12306.getFloatValue("return_cost");
            // 更新车票
            ticket.setRefundType(1);
            ticket.setIsapplyticket(1);
            ticket.setInsureno(reqtoken);
            ticket.setProcedure(return_cost);
            ticket.setStatus(Trainticket.WAITREFUND);
            ticket.setState12306(Trainticket.REFUNDED12306);
            ticket.setRefundRequestTime(ElongHotelInterfaceUtil.getCurrentTime());
            ticket.setRefundsuccesstime(ticket.getRefundRequestTime());
            Server.getInstance().getTrainService().updateTrainticket(ticket);
            // 乘客信息
            String passportseno = tcTicket.getString("passportseno");// 证件号
            String passengername = tcTicket.getString("passengername");// 乘客姓名
            // 票类型
            String ticketType = getTicketType(ticket);// 票类型
                                                      // 1:成人票，2:儿童票，3:学生票，4:残军票
            // 日志内容
            String logContent = "乘客[" + passengername + "][" + passportseno + "][" + ticketType + "]申请退票，票号："
                    + ticket_no + "，12306<span style='color:red;'>退票成功</span>，等待退款，车票票款：" + ticket_price + "，应退票款："
                    + return_price + "，退票费：" + return_cost;
            // 保存日志
            createtrainorderrc(1, logContent, order.getId(), ticket.getId(), Trainticket.WAITREFUND);
        }
        catch (Exception e) {
            String log = "订单[" + order.getId() + "]>>>接口退票，更新车票状态为已退票-等待退款异常，请求已接收: " + e.getMessage();
            WriteLog.write("t同程火车票接口_4.10在线退票", random + ">>>" + log);
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
        WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "--mccguid:" + mccguid);
        if (mccguid == null || "".equals(mccguid)) {
            OcsMethod.getInstance().add("confirm=" + orderid, guid, 120);
            WriteLog.write("途牛确认退票回调Thread", "orderid:" + orderid + "--guid:" + guid + "--mccguid" + mccguid);
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
        WriteLog.write("途牛确认退票回调Thread", "orderid" + orderid + "-" + "guid:" + guid);
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        if (mccguid != null || "".equals(mccguid) && OcsMethod.getInstance().get("confirm=" + orderid).equals(guid)) {
            WriteLog.write("途牛确认退票回调Thread", "orderid" + orderid + "-" + "guid:" + guid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 回调
     * 
     * @param jso
     * @time 2015年8月22日 下午1:51:22
     * @author wcl
     */
    public void TuniuReturnTicketCallBack(String trainorderid, Trainorder trainorder, Trainticket trainticket,
            Trainpassenger trainpassenger) {
        //        String url = "http://121.199.25.199:35216/tcTrainCallBack";
        boolean bool = false;
        String url = PropertyUtil.getValue("TuNiu_CallBack_Url", "Train.properties");
        JSONObject jso = new JSONObject();
        jso.put("method", "train_refund_callback");
        jso.put("apiorderid", trainorder.getQunarOrdernumber());
        jso.put("returntype", "0");
        jso.put("reqtoken", trainticket.getInsureno());
        jso.put("trainorderid", trainorder.getExtnumber());
        jso.put("returnstate", false);
        jso.put("returnmoney", "");
        String returnmsg = "";
        try {
            returnmsg = URLEncoder.encode(returnmsg, "utf-8");
        }
        catch (UnsupportedEncodingException e2) {
            WriteLog.write("err_TuniuReturnTicketThread", trainorderid);
            ExceptionUtil.writelogByException("err_TuniuReturnTicketThread", e2);
        }
        jso.put("returnmsg", returnmsg);
        jso.put("ticketid", "");
        jso.put("ticket_no", trainticket.getTicketno());
        try {
            jso.put("passengername", URLEncoder.encode(trainpassenger.getName(), "utf-8"));
        }
        catch (UnsupportedEncodingException e1) {
            WriteLog.write("err_TuniuReturnTicketThread", trainorderid);
            ExceptionUtil.writelogByException("err_TuniuReturnTicketThread", e1);
        }
        jso.put("passporttypeseid", getIdtype12306(trainpassenger.getIdtype()));
        jso.put("passportseno", trainpassenger.getIdnumber());
        jso.put("returnsuccess", false);
        jso.put("returntime", "");
        jso.put("returnfailid", 0);
        String returnfailmsg = "";
        try {
            returnfailmsg = URLEncoder.encode(returnfailmsg, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            WriteLog.write("err_TuniuReturnTicketThread", trainorderid);
            ExceptionUtil.writelogByException("err_TuniuReturnTicketThread", e);
        }
        jso.put("returnfailmsg", returnfailmsg);
        for (int i = 0; i < 6; i++) {
            String resultUrlString = "";
            try {
                System.out.println(trainorder.getId() + ":订单号:" + trainorder.getOrdernumber() + "地址:" + url + "-请求参数:"
                        + jso.toString());
                resultUrlString = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
                WriteLog.write("途牛确认退票回调Thread", trainorder.getId() + ":订单号:" + trainorder.getOrdernumber() + "地址:"
                        + url + "-请求参数:" + jso.toString());
            }
            catch (Exception e1) {
                WriteLog.write("err_TuniuReturnTicketThread", trainorderid);
                ExceptionUtil.writelogByException("err_TuniuReturnTicketThread", e1);
            }
            if (resultUrlString.equals("success")) {
                OcsMethod.getInstance().remove("confirm=" + orderid);
                WriteLog.write("途牛确认退票回调Thread", "第" + i + "回调:" + resultUrlString);
                bool = true;
                break;
            }
            WriteLog.write("途牛确认退票回调Thread", "第" + i + "回调" + resultUrlString);
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                WriteLog.write("err_TuniuReturnTicketThread", trainorderid);
                ExceptionUtil.writelogByException("err_TuniuReturnTicketThread", e);
            }
        }
        if (!bool) {
            try {
                String upate = "update T_TRAINORDER set C_ISQUESTIONORDER=3 where C_ORDERNUMBER='" + trainorderid + "'";
                Server.getInstance().getSystemService().excuteAdvertisementBySql(upate);
            }
            catch (Exception e) {
                WriteLog.write("err_TuniuReturnTicketThread", trainorderid);
                ExceptionUtil.writelogByException("err_TuniuReturnTicketThread", e);
            }
        }
    }

    public void trainorderCallBackInfo(Trainorder order, String orderid) {
        if (order != null) {
            List<Trainpassenger> trainpassengers = order.getPassengers();
            for (int i = 0; i < trainpassengers.size(); i++) {
                Trainpassenger trainpassenger = trainpassengers.get(i);
                TuniuReturnTicketCallBack(orderid, order, trainpassenger.getTraintickets().get(0), trainpassenger);
            }
        }
        else {
            String url = PropertyUtil.getValue("TuNiu_CallBack_Url", "Train.properties");
            JSONObject jso = new JSONObject();
            jso.put("method", "train_refund_callback");
            jso.put("apiorderid", orderid);
            jso.put("returntype", "0");
            jso.put("reqtoken", reqtoken);
            jso.put("trainorderid", ordernumber);
            jso.put("returnstate", false);
            for (int i = 0; i < 6; i++) {
                String resultUrlString = "";
                try {
                    System.out.println(orderid + ":订单号:" + ordernumber + "地址:" + url + "-请求参数:" + jso.toString());
                    resultUrlString = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (resultUrlString.equals("success")) {
                    OcsMethod.getInstance().remove("confirm=" + orderid);
                    WriteLog.write("途牛确认退票回调Thread", "第" + i + "回调:" + resultUrlString);
                    break;
                }
                WriteLog.write("途牛确认退票回调Thread", "第" + i + "回调" + resultUrlString);
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String resultUrlString = "";
        String url = "http://test.test123.com:29004/cn_interface/tcTrainCallBack";
        while (true) {
            try {
                resultUrlString = SendPostandGet.submitGet(url, "UTF-8").toString();
                WriteLog.write("测试数据", "resultUrlString:" + resultUrlString);
                if (resultUrlString != null && resultUrlString.contains("请求参数为空")) {
                    break;
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("测试数据", e);
            }
            try {
                Thread.sleep(3000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
