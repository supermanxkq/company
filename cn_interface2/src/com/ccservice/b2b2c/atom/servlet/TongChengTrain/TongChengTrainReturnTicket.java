package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 在线退票 method --> return_ticket 12306规定：在12306.cn网站购票，没有换取纸质车票且不晚于开车前2小时的
 * 
 * @author WH
 */
public class TongChengTrainReturnTicket extends TrainSelectLoginWay {

    // REP退票
    private static final String datatypeflag = "100";

    public String returnticket(JSONObject jsonObject, int random) {
        // 请求数据
        String orderid = jsonObject.getString("orderid");// 同程订单号
        String transactionid = jsonObject.getString("transactionid");// 供应商交易单号
        String ordernumber = jsonObject.getString("ordernumber");// 12306订单号
        String reqtoken = jsonObject.getString("reqtoken");// 请求特征
        JSONArray tickets = jsonObject.getJSONArray("tickets");// 车票信息
        String accountId = jsonObject.getString("accountId");
        // 返回
        JSONObject retobj = new JSONObject();
        // 车票长度
        int ticketSize = tickets == null ? 0 : tickets.size();
        // 判断空值
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(ordernumber)
                || ElongHotelInterfaceUtil.StringIsNull(transactionid)
                || ElongHotelInterfaceUtil.StringIsNull(reqtoken) || ticketSize == 0) {
            retobj.put("success", false);
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            return retobj.toJSONString();
        }
        // 退票类型，key：车票号，value：1、线上 2、线下
        Map<String, Integer> refundTypeMap = new HashMap<String, Integer>();
        // key：车票号，value：退票JSON
        Map<String, JSONObject> refundTicketMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < ticketSize; i++) {
            // {“ticket_no”:“E2610890401070051”,“passengername”:“王二”,“passporttypeseid”:1,“passportseno”:“421116198907143795”}
            JSONObject ticket = tickets.getJSONObject(i);
            String ticket_no = ticket.getString("ticket_no");// 车票号
            String passengername = ticket.getString("passengername");// 乘客姓名
            String passporttypeseid = ticket.getString("passporttypeseid");// 证件类型
            String passportseno = ticket.getString("passportseno");// 证件号
            String remark = ticket.getString("remark");// 退票备注：【0：客人线下已退票】，默认为空
            // 判断空值
            if (ElongHotelInterfaceUtil.StringIsNull(ticket_no) || ElongHotelInterfaceUtil.StringIsNull(passengername)
                    || ElongHotelInterfaceUtil.StringIsNull(passporttypeseid)
                    || ElongHotelInterfaceUtil.StringIsNull(passportseno)) {
                retobj.put("success", false);
                retobj.put("code", "107");
                retobj.put("msg", "业务参数缺失");
                return retobj.toJSONString();
            }
            if (refundTicketMap.containsKey(ticket_no)) {
                retobj.put("success", false);
                retobj.put("code", "108");
                retobj.put("msg", "车票信息，票号[" + ticket_no + "]存在重复");
                return retobj.toJSONString();
            }
            refundTicketMap.put(ticket_no, ticket);
            refundTypeMap.put(ticket_no, "0".equals(remark) ? 2 : 1);
        }

        //        testtuniuMethod();//途牛备用流程

        // 查询订单
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        // 订单不存在
        if (orders == null || orders.size() == 0) {
            retobj.put("success", false);
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            return retobj.toJSONString();
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
            retobj.put("success", false);
            retobj.put("code", "108");
            retobj.put("msg", "取票单号[" + ordernumber + "]不一致");
            return retobj.toJSONString();
        }
        // 订单状态
        int status = order.getOrderstatus();
        if (status != Trainorder.ISSUED) {
            retobj.put("success", false);
            retobj.put("code", "112");
            retobj.put("msg", "该订单状态下，不能退票");
            return retobj.toJSONString();
        }
        //保存
        saveThirdAccountInfo(order.getId(), jsonObject);
        // 加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
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
            if (localTicket == null || ticketType == 0) {
                retobj.put("success", false);
                retobj.put("code", "118");
                retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票未找到");
                return retobj.toString();
            }
            // 车票状态
            int ticketStatus = localTicket.getStatus();
            // 改签类型 1:线上改签、2:改签退
            int changeType = localTicket.getChangeType() == null ? 0 : localTicket.getChangeType().intValue();
            // 改签票必须得线上(分析：已线下改签与线上矛盾、改签退不返回票号)
            if (ticketType == 2 && changeType != 1) {
                retobj.put("success", false);
                retobj.put("code", "113");
                retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票状态变化，暂不可退");
                return retobj.toString();
            }
            // 车站改签、必须申请线下退票
            if (ticketStatus == Trainticket.FINISHCHANGE && changeType > 4 && changeType < 12) {
                // 申请线上退票
                if (refundTypeMap.get(tc_ticket_no) == 1) {
                    retobj.put("success", false);
                    retobj.put("code", "113");
                    retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票状态变化，暂不可线上退票");
                    return retobj.toString();
                }
                // 申请线下退票、改签已退款
                if (changeType == 10) {
                    // 车站低改，原票价>改签票价
                    if (localTicket.getPrice().floatValue() > localTicket.getTcnewprice().floatValue()) {
                        if (localTicket.getTcnewprice().floatValue() > 0) {
                            ticketStatus = Trainticket.ISSUED;
                        }
                        else {
                            retobj.put("success", false);
                            retobj.put("code", "113");
                            retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票改签退款中");
                            return retobj.toString();
                        }
                    }
                    else {
                        retobj.put("success", false);
                        retobj.put("code", "113");
                        retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票已改签全退");
                        return retobj.toString();
                    }
                }
                else {
                    retobj.put("success", false);
                    retobj.put("code", "113");
                    retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票改签退款中");
                    return retobj.toString();
                }
            }
            if ((ticketType == 1 && ticketStatus != Trainticket.ISSUED)
                    || (ticketType == 2 && ticketStatus != Trainticket.FINISHCHANGE)) {
                // 所有请求都接收，返回同程接收成功，判断状态 --> 申请退票以前、退款失败以后、非无法退票的TOKEN不一致
                if (ticketStatus < Trainticket.APPLYTREFUND || ticketStatus > Trainticket.REFUNDFAIL
                        || (ticketStatus != Trainticket.NONREFUNDABLE && !reqtoken.equals(localTicket.getInsureno()))) {
                    retobj.put("success", false);
                    retobj.put("code", "118");
                    retobj.put("msg", "票号[" + tc_ticket_no + "]对应的车票状态变化，暂不可退");
                    return retobj.toString();
                }
                else {
                    requestedMap.put(tc_ticket_no, true);
                }
            }
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
                    //格式化
                    SimpleDateFormat shiFenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
                }
                if (timeOut) {
                    retobj.put("success", false);
                    retobj.put("code", "701");
                    retobj.put("msg", "车票[" + tc_ticket_no + "]距离开车时间太近无法退票");
                    return retobj.toJSONString();
                }
            }
            SingleTicketNo = tc_ticket_no;
            refundMap.put(tc_ticket_no, localTicket);
        }
        // 走异步
        boolean AutoRefundOpen = false;
        // 账号
        String cookie = "";
        Customeruser user = new Customeruser();
        // 自动退票、张数为1、线上
        if (AutoRefundOpen && ticketSize == 1 && refundTypeMap.get(SingleTicketNo) == 1) {
            // 下单用户名
            String createAccount = order.getSupplyaccount();
            // 用户名非空
            if (!ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
                // 下单账户
                user = getCustomeruserBy12306Account(order, random, true);
                // 为空
                user = user == null ? new Customeruser() : user;
                // Cookie非空
                if (!ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                    cookie = user.getCardnunber();
                }
                if (order.getOrdertype() == 6 && !ElongHotelInterfaceUtil.StringIsNull(accountId)) {
                    user = getCustomeruserByusernameEncryption(accountId);
                }
            }

        }
        // 不直接退票
        if (ElongHotelInterfaceUtil.StringIsNull(cookie)) {
            // 更新本地为申请
            int SuccessTotal = refundApply(refundMap, requestedMap, refundTicketMap, refundTypeMap, order, reqtoken,
                    random);
            // 返回数据
            if (SuccessTotal != ticketSize) {
                return returnError();
            }
            else {
                return returnOk(orderid, ordernumber, reqtoken, ticketSize, 0);
            }
        }
        /**
         *@author WH
         *====================注意：后续逻辑暂不会走，不要作无用功来修改逻辑！！！====================
         **/
        // 单个车票已请求
        if (requestedMap.containsKey(SingleTicketNo)) {
            return returnOk(orderid, ordernumber, reqtoken, ticketSize, 0);
        }
        // 本地车票
        Trainticket ticket = refundMap.get(SingleTicketNo);
        // 同程信息
        JSONObject tcTicket = refundTicketMap.get(SingleTicketNo);
        // 退票
        String refundResult = refundSingle(ticket, tcTicket, order, reqtoken, user, SingleTicketNo, trainStartTime,
                random);
        // 成功
        if ("ok".equals(refundResult)) {
            // 返回数据
            return returnOk(orderid, ordernumber, reqtoken, ticketSize, 1);
        }
        // 申请
        else if ("apply".equals(refundResult)) {
            // 更新本地为申请
            int SuccessTotal = refundApply(refundMap, requestedMap, refundTicketMap, refundTypeMap, order, reqtoken,
                    random);
            // 返回数据
            if (SuccessTotal != ticketSize) {
                return returnError();
            }
            else {
                return returnOk(orderid, ordernumber, reqtoken, ticketSize, 0);
            }
        }
        else {
            return refundResult;
        }
    }

    //    private void testtuniuMethod() {
    //      //若是途牛,走此线程
    //        if (partneridIsTuniu(partnerid)) {
    //            // 创建一个可重用固定线程数的线程池
    //            ExecutorService pool = Executors.newFixedThreadPool(1);
    //            TuniuReturnTicketThread returnTicket = new TuniuReturnTicketThread(random, ticketSize, partnerid, orderid,
    //                    reqtoken, transactionid, ordernumber, refundTicketMap, refundTypeMap, shiFenFormat, timeFormat);
    //            Thread tuniu = new Thread(returnTicket);
    //            pool.execute(tuniu);
    //            // 关闭线程池
    //            pool.shutdown();
    //            WriteLog.write("途牛确认退票回调Thread", "tongc订单号:" + orderid + "线程关闭");
    //            retobj.put("success", true);
    //            retobj.put("code", "802");
    //            retobj.put("msg", "退票请求已接收");
    //            retobj.put("orderid", orderid);
    //            retobj.put("ordernumber", ordernumber);
    //            return retobj.toJSONString();
    //        }
    //
    //    }

    /**
     * 12306单个退票
     */
    private String refundSingle(Trainticket ticket, JSONObject tcTicket, Trainorder order, String reqtoken,
            Customeruser user, String ticket_no, long trainStartTime, int random) {
        // 格式化
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 订单创建时间，用于12306通过时间查询订单
        String order_date = timeFormat.format(order.getCreatetime()).split(" ")[0];
        // 12306单号
        String sequence_no = order.getExtnumber();
        // 乘客信息
        String passengername = tcTicket.getString("passengername");// 乘客姓名
        // 请求REP参数
        JSONObject req = new JSONObject();
        req.put("ticket_no", ticket_no);
        req.put("order_date", order_date);
        req.put("sequence_no", sequence_no);
        req.put("cookie", user.getCardnunber());
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
            RepServerBean rep = RepServerUtil.getRepServer(user, false);
            url = rep.getUrl();
            // REP地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(url)) {
                throw new Exception("REP地址为空");
            }
            // 请求参数URLEncoder
            String jsonStr = URLEncoder.encode(req.toJSONString(), "UTF-8");
            // 请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
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
            // 接口类型
            int interfaceType = order.getInterfacetype() == null ? 0 : order.getInterfacetype();
            // 循环退票
            for (String ticket_no : refundMap.keySet()) {
                // 本地车票
                Trainticket ticket = refundMap.get(ticket_no);
                // 退票类型，1:线上；2:线下
                int isApplyTicket = refundTypeMap.get(ticket_no);
                // 申请标识，1:接口申请线下退款
                int applyTicketFlag = isApplyTicket == 2 ? 1 : 0;
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
                        + "', C_APPLYTICKETFLAG = " + applyTicketFlag + ", C_INTERFACETYPE = " + interfaceType
                        + " where ID = " + ticket.getId();
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
        }
        return SuccessTotal;
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
     * 退款
     */
    @SuppressWarnings("unused")
    private static void tuikuan(Long trainorderid, Long ticketid, Long agentid) {
        Customeruser customeruser = getcustomeruserbyagentid(agentid);
        Server.getInstance().getTrainService().ticketRefund(trainorderid, ticketid, customeruser, "");
    }

    /**
     * 根据agentid找到Customeruser
     */
    @SuppressWarnings("rawtypes")
    public static Customeruser getcustomeruserbyagentid(Long agentid) {
        List list = Server.getInstance().getMemberService()
                .findAllCustomeruser("where C_AGENTID=" + agentid + " and C_ISADMIN=1", "order by id", -1, 0);
        Customeruser customeruser = new Customeruser();
        if (list.size() > 0) {
            customeruser = (Customeruser) list.get(0);
        }
        return customeruser;
    }

    /**
     * 退票退款
     */
    @SuppressWarnings("unused")
    public void refundprice(Trainorder trainorder, Trainticket ticket, Trainorderchange change, float procedure,
            Long agentid) {
        Customeruser customeruser = getcustomeruserbyagentid(agentid);
        ticket.setStatus(Trainticket.REFUNDIING);
        ticket.setProcedure(procedure);
        Server.getInstance().getTrainService().updateTrainticket(ticket);
        ticket = Server.getInstance().getTrainService().findTrainticket(ticket.getId());
        Trainorderrc rc = new Trainorderrc();
        if (ticket.getTcnewprice() > 0) {
            change = Server.getInstance().getTrainService()
                    .findTrainorcerchange(ticket.getTrainpassenger().getChangeid());
            rc.setOrderid(change.getId());
            rc.setYwtype(2);
        }
        else {
            rc.setOrderid(trainorder.getId());
            rc.setYwtype(1);
        }
        rc.setCreateuser("系统");
        rc.setContent("执行退款，客票退款中。退票手续费:" + procedure);
        rc.setStatus(Trainticket.REFUNDIING);
        rc.setTicketid(ticket.getId());
        Server.getInstance().getTrainService().createTrainorderrc(rc);
        // long quer_id = Long.valueOf(getSystemConfig("qunar_agentid"));
        // 通知去哪儿是否退票
        /**
         * ticket.getIsapplyticket() 区分去哪儿申请的票还是、客人在车站窗口退票的票
         * isapplyticket=1、如果是去哪儿申请的票、不做退款只掉退票接口，告诉去哪儿
         * isapplyticket=2、如果是客人在车站窗口退票的票、由客服和财务审核然后客服手动申请退票然后做退票退款，调用原路退款接口
         */
        // if (quer_id == trainorder.getAgentid() && ticket.getIsapplyticket()
        // == 1) {
        // boolean msg = Server.getInstance().getIQTrainService()
        // .trainRefundresult(trainorder.getQunarOrdernumber(),
        // ticket.getStatus(), 0);
        // if (!msg) {
        // ticket.setStatus(Trainticket.WAITREFUND);
        // Server.getInstance().getTrainService().updateTrainticket(ticket);
        // boolean data = false;
        // HttpServletResponse response = ServletActionContext.getResponse();
        // response.setContentType("text/plain; charset=utf-8");
        // PrintWriter out;
        // try {
        // out = response.getWriter();
        // out.print(data);
        // out.flush();
        // out.close();
        // }
        // catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        // else {
        // this.trainorder =
        // Server.getInstance().getTrainService().findTrainorder(trainorder.getId());
        // for (Trainpassenger p : trainorder.getPassengers()) {
        // for (Trainticket tk : p.getTraintickets()) {
        // tk.setStatus(Trainticket.REFUNDED);
        // Server.getInstance().getTrainService().updateTrainticket(tk);
        // }
        // }
        // }
        //
        // }
        // else
        // if (quer_id == trainorder.getAgentid() && ticket.getIsapplyticket()
        // == 2) {
        // this.trainorder =
        // Server.getInstance().getTrainService().findTrainorder(trainorder.getId());
        float orderprice = 0l;
        for (Trainpassenger p : trainorder.getPassengers()) {
            for (Trainticket tk : p.getTraintickets()) {
                orderprice += tk.getPrice();
            }
        }
        float refundCash = orderprice - procedure;
        // boolean result =
        // Server.getInstance().getIQTrainService().trainRefundPrice(qunarOrdernumber,
        // 3, refundCash);
        // if (result) {
        // ticket.setStatus(Trainticket.REFUNDED);
        // Server.getInstance().getTrainService().updateTrainticket(ticket);
        // for (Trainpassenger p : trainorder.getPassengers()) {
        // for (Trainticket tk : p.getTraintickets()) {
        // tk.setStatus(Trainticket.REFUNDED);
        // Server.getInstance().getTrainService().updateTrainticket(tk);
        // }
        // }
        // }
        // else {
        // ticket.setStatus(Trainticket.WAITREFUND);
        // Server.getInstance().getTrainService().updateTrainticket(ticket);
        // boolean data = false;
        // HttpServletResponse response = ServletActionContext.getResponse();
        // response.setContentType("text/plain; charset=utf-8");
        // PrintWriter out;
        // try {
        // out = response.getWriter();
        // out.print(data);
        // out.flush();
        // out.close();
        // }
        // catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        //
        // }
        // else {
        // String serverinfo = request.getServerName() + ":" +
        // request.getServerPort();
        // Server.getInstance().getTrainService()
        // .ticketRefund(trainorder.getId(), ticket.getId(),
        // this.getLoginUser(), serverinfo);
        // }

    }

    /**
     * 是否是途牛的代理商，是:true,不是:false
     * 
     * @param partnerid
     * @return
     * @time 2015年8月19日 下午2:29:02
     * @author Administrator
     */
    public boolean partneridIsTuniu(String partnerid) {
        String Checkpartnerid = PropertyUtil.getValue("ChecktrainCreateTimeOutpartnerid", "Train.properties");
        if (Checkpartnerid != null && !"".equals(Checkpartnerid)) {
            String[] parterids = Checkpartnerid.split("_");
            for (int i = 0; i < parterids.length; i++) {
                if (partnerid.contains(parterids[i])) {
                    return true;
                }
            }
        }
        return false;
    }

}