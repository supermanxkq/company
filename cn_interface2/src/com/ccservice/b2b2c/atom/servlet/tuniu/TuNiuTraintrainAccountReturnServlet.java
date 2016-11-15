package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 途牛退票接口
 * 朱李旭
 */
@SuppressWarnings("serial")
public class TuNiuTraintrainAccountReturnServlet extends HttpServlet {
    
    private final String logname = "tuniu_3_5_3_退票接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int random = (int) (Math.random() * 1000000);
        TuNiuDesUtil TuNiuDesUtil = new TuNiuDesUtil();
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        final AsyncContext ctx = req.startAsync();
        ctx.setTimeout(50000L);
        //监听
        ctx.addListener(new AsyncListener() {
            public void onTimeout(AsyncEvent event) throws IOException {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }

            public void onError(AsyncEvent event) throws IOException {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }

            public void onComplete(AsyncEvent event) throws IOException {
            }

            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });
        TongchengSupplyMethod TongchengSupplyMethod = new TongchengSupplyMethod();
        BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String reqString = buf.toString();
        WriteLog.write(logname, random + "--->" + reqString);
        try {
            if (reqString == null || "".equals(reqString)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //请求json
            JSONObject reqjso = JSONObject.parseObject(reqString);
            String account = tuNiuServletUtil.getParamByJsonStr("account", reqjso);//账号
            String sign = tuNiuServletUtil.getParamByJsonStr("sign", reqjso);//加密结果
            String timestamp = tuNiuServletUtil.getParamByJsonStr("timestamp", reqjso);//请求时间
            String data = tuNiuServletUtil.getParamByJsonStr("data", reqjso);//加密的请求体
            WriteLog.write(logname, random + "--->account--->" + account + "--->sign--->" + sign + "--->timestamp--->"
                    + timestamp + "--->data--->" + data);
            if ("".equals(account) || "".equals(sign) || "".equals(timestamp) || "".equals(data)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //获取账户信息
            Map map = tuNiuServletUtil.getInterfaceAccount(account);
            String agentid = tuNiuServletUtil.getParamByMapStr("C_AGENTID", map);
            String key = tuNiuServletUtil.getParamByMapStr("C_KEY", map);
            String password = tuNiuServletUtil.getParamByMapStr("C_ARG2", map);
            String interfacetype = tuNiuServletUtil.getParamByMapStr("C_INTERFACETYPE", map);
            WriteLog.write(logname, random + "--->agentid--->" + agentid + "--->key--->" + key + "--->password--->"
                    + password + "--->interfacetype--->" + interfacetype);
            if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                tuNiuServletUtil.respByUserNotExists(ctx, logname);
                return;
            }
            JSONObject object = JSONObject.parseObject(reqString);
            object.put("sign", "");
            //获取key加密
            String localsign = SignUtil.generateSign(object.toString(), key);
            WriteLog.write(logname, random + "--->localsign:" + localsign + ";sign:" + sign);
            if (!sign.equalsIgnoreCase(localsign)) {
                tuNiuServletUtil.respBySignatureError(ctx, logname);
                return;
            }
            String paramStr = TuNiuDesUtil.decrypt(data);
            WriteLog.write(logname, random + "--->data:" + paramStr);
            JSONObject jsonString = new JSONObject();
            try {
                jsonString = JSONObject.parseObject(paramStr);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logname, e, random + "");
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            JSONObject retobj = new JSONObject();
            String vendorOrderId = tuNiuServletUtil.getParamByJsonStr("vendorOrderId", jsonString);//合作伙伴方订单号
            String orderId = tuNiuServletUtil.getParamByJsonStr("orderId", jsonString);//途牛订单号
            String orderNumber = tuNiuServletUtil.getParamByJsonStr("orderNumber", jsonString);//取票单号
            String callBackUrl = tuNiuServletUtil.getParamByJsonStr("callBackUrl", jsonString);//回调地址
            String tickets = tuNiuServletUtil.getParamByJsonStr("tickets", jsonString);//车票信息
            String refundId = tuNiuServletUtil.getParamByJsonStr("refundId", jsonString);//退票请求流水号
            WriteLog.write(logname, random + "--->vendorOrderId--->" + vendorOrderId + "--->orderId--->" + orderId
                    + "--->orderNumber--->" + orderNumber + "--->callBackUrl--->" + callBackUrl + "--->tickets--->"
                    + tickets + "--->refundId--->" + refundId);
            if ("".equals(orderId) || "".equals(orderNumber) || "".equals(callBackUrl) || "".equals(tickets)
                    || "".equals(refundId)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            JSONArray ticketarray = JSONObject.parseArray(tickets);
            int ticketSize = ticketarray == null ? 0 : ticketarray.size();
            WriteLog.write(logname, random + "--->ticketSize--->" + ticketSize);
            if (ElongHotelInterfaceUtil.StringIsNull(vendorOrderId) || ElongHotelInterfaceUtil.StringIsNull(orderId)
                    || ElongHotelInterfaceUtil.StringIsNull(orderNumber)
                    || ElongHotelInterfaceUtil.StringIsNull(callBackUrl) || ticketSize == 0) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            Map<String, Integer> refundTypeMap = new HashMap<String, Integer>();//退票类型  线上  线下

            Map<String, JSONObject> refundTicketMap = new HashMap<String, JSONObject>();// key：车票号，value：退票JSON
            for (int i = 0; i < ticketSize; i++) {
                JSONObject ticket = ticketarray.getJSONObject(i);
                String ticketNo = ticket.getString("ticketNo");// 车票号
                String passengerName = ticket.getString("passengerName");// 乘客姓名
                String passportTypeId = ticket.getString("passportTypeId");// 证件类型
                String passportNo = ticket.getString("passportNo");// 证件号
                WriteLog.write(logname, random + "--->ticketNo--->" + ticketNo + "--->passengerName--->"
                        + passengerName + "--->passportTypeId--->" + passportTypeId + "--->passportNo--->" + passportNo);
                if (ElongHotelInterfaceUtil.StringIsNull(ticketNo)
                        || ElongHotelInterfaceUtil.StringIsNull(passengerName)
                        || ElongHotelInterfaceUtil.StringIsNull(passportTypeId)
                        || ElongHotelInterfaceUtil.StringIsNull(passportNo)) {
                    tuNiuServletUtil.respByParamError(ctx, logname);
                    return;
                }
                if (refundTicketMap.containsKey(ticketNo)) {
                    WriteLog.write(logname, random + "--->车票信息，票号[" + ticketNo + "]存在重复");
                    JSONObject json = new JSONObject();
                    json.put("vendorOrderId", vendorOrderId);
                    json.put("orderNumber", orderNumber);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", "true");
                    jsonObject.put("returnCode", "140002");
                    jsonObject.put("errorMsg", "退票失败");
                    jsonObject.put("data", json);
                    WriteLog.write(logname, random + "--->" + jsonObject.toString());
                    tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                    return;
                }

                refundTicketMap.put(ticketNo, ticket);
                refundTypeMap.put(ticketNo, 1);

            }
            // 查询订单
            Trainform trainform = new Trainform();
            trainform.setQunarordernumber(orderId);
            trainform.setOrdernumber(vendorOrderId);
            List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
            WriteLog.write(logname, random + "--->orders--->" + orders.size());
            Trainorder order = new Trainorder();
            if (orders.size() > 0) {
                order = orders.get(0);
            }
            if (order.getOrderstatus() == Trainorder.ISSUED) {
                // 继续取第一个
                order = orders.get(0);
            }
            else if (orders.size() == 2) {
                order = orders.get(1);
            }
            WriteLog.write(logname, random + "--->" + order.getExtnumber() + "--->" + order.getOrderstatus());
            // 12306订单号
            if (!orderNumber.equals(order.getExtnumber())) {
                WriteLog.write(logname, random + "--->取票单号[" + orderNumber + "]不一致");
                JSONObject json = new JSONObject();
                json.put("vendorOrderId", vendorOrderId);
                json.put("orderNumber", orderNumber);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", "true");
                jsonObject.put("returnCode", "140002");
                jsonObject.put("errorMsg", "退票失败");
                jsonObject.put("data", json);
                WriteLog.write(logname, random + "--->" + jsonObject.toString());
                tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                return;
            }
            // 订单状态
            int status = order.getOrderstatus();
            if (status != Trainorder.ISSUED) {
                WriteLog.write(logname, random + "--->该订单状态下，不能退票");
                JSONObject json = new JSONObject();
                json.put("vendorOrderId", vendorOrderId);
                json.put("orderNumber", orderNumber);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", "true");
                jsonObject.put("returnCode", "140002");
                jsonObject.put("errorMsg", "退票失败");
                jsonObject.put("data", json);
                WriteLog.write(logname, random + "--->" + jsonObject.toString());
                tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                return;
            }
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
            Map<String, Trainticket> refundMap = new HashMap<String, Trainticket>();// 订单不存在
            if (orders == null || orders.size() == 0) {
                WriteLog.write(logname, random + "--->订单不存在");
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            // 验证车票，KEY：途牛票号
            for (String tn_ticket_no : refundTicketMap.keySet()) {

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
                        if (tn_ticket_no.equals(ticketno) || tn_ticket_no.equals(tcticketno)) {
                            if (localTicket == null) {
                                if (tn_ticket_no.equals(ticketno)) {
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
                WriteLog.write(logname, random + "--->ticketType--->" + ticketType + "车票是否通过验证");
                if (localTicket == null || ticketType == 0) {
                    WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票未找到");
                    JSONObject json = new JSONObject();
                    json.put("vendorOrderId", vendorOrderId);
                    json.put("orderNumber", orderNumber);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", "true");
                    jsonObject.put("returnCode", "140002");
                    jsonObject.put("errorMsg", "退票失败");
                    jsonObject.put("data", json);
                    WriteLog.write(logname, random + "--->" + jsonObject.toString());
                    tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                    return;
                }
                // 车票状态
                int ticketStatus = localTicket.getStatus();
                // 改签类型 1:线上改签、2:改签退
                int changeType = localTicket.getChangeType() == null ? 0 : localTicket.getChangeType().intValue();
                // 改签票必须得线上(分析：已线下改签与线上矛盾、改签退不返回票号)
                WriteLog.write(logname, random + "--->ticketStatus--->" + ticketStatus + "--->changeType--->"
                        + changeType);
                if (ticketType == 2 && changeType != 1) {
                    WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票状态变化，暂不可退");
                    JSONObject json = new JSONObject();
                    json.put("vendorOrderId", vendorOrderId);
                    json.put("orderNumber", orderNumber);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", "true");
                    jsonObject.put("returnCode", "140002");
                    jsonObject.put("errorMsg", "退票失败");
                    jsonObject.put("data", json);
                    WriteLog.write(logname, random + "--->" + jsonObject.toString());
                    tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                    return;
                }
                // 车站改签、必须申请线下退票
                if (ticketStatus == Trainticket.FINISHCHANGE && changeType > 4 && changeType < 12) {
                    // 申请线上退票
                    if (refundTypeMap.get(tn_ticket_no) == 1) {
                        WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票状态变化，暂不可线上退票");
                        JSONObject json = new JSONObject();
                        json.put("vendorOrderId", vendorOrderId);
                        json.put("orderNumber", orderNumber);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("success", "true");
                        jsonObject.put("returnCode", "140002");
                        jsonObject.put("errorMsg", "退票失败");
                        jsonObject.put("data", json);
                        WriteLog.write(logname, random + "--->" + jsonObject.toString());
                        tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                        return;
                    }
                    // 申请线下退票、改签已退款
                    if (changeType == 10) {
                        // 车站低改，原票价>改签票价
                        if (localTicket.getPrice().floatValue() > localTicket.getTcnewprice().floatValue()) {
                            if (localTicket.getTcnewprice().floatValue() > 0) {
                                ticketStatus = Trainticket.ISSUED;
                            }
                            else {
                                WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票改签退款中");
                                JSONObject json = new JSONObject();
                                json.put("vendorOrderId", vendorOrderId);
                                json.put("orderNumber", orderNumber);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("success", "true");
                                jsonObject.put("returnCode", "140002");
                                jsonObject.put("errorMsg", "退票失败");
                                jsonObject.put("data", json);
                                WriteLog.write(logname, random + "--->" + jsonObject.toString());
                                tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                                return;
                            }
                        }
                        else {
                            WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票已改签全退");
                            JSONObject json = new JSONObject();
                            json.put("vendorOrderId", vendorOrderId);
                            json.put("orderNumber", orderNumber);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("success", "true");
                            jsonObject.put("returnCode", "140002");
                            jsonObject.put("errorMsg", "退票失败");
                            jsonObject.put("data", json);
                            WriteLog.write(logname, random + "--->" + jsonObject.toString());
                            tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                            return;
                        }
                    }
                    else {
                        WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票改签退款中");
                        JSONObject json = new JSONObject();
                        json.put("vendorOrderId", vendorOrderId);
                        json.put("orderNumber", orderNumber);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("success", "true");
                        jsonObject.put("returnCode", "140002");
                        jsonObject.put("errorMsg", "退票失败");
                        jsonObject.put("data", json);
                        WriteLog.write(logname, random + "--->" + jsonObject.toString());
                        tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                        return;
                    }
                }
                if ((ticketType == 1 && ticketStatus != Trainticket.ISSUED)
                        || (ticketType == 2 && ticketStatus != Trainticket.FINISHCHANGE)) {
                    // 所有请求都接收，返回途牛接收成功，判断状态 --> 申请退票以前、退款失败以后、非无法退票的TOKEN不一致
                    if (ticketStatus < Trainticket.APPLYTREFUND || ticketStatus > Trainticket.REFUNDFAIL) {
                        WriteLog.write(logname, random + "--->票号[" + tn_ticket_no + "]对应的车票状态变化，暂不可退");
                        JSONObject json = new JSONObject();
                        json.put("vendorOrderId", vendorOrderId);
                        json.put("orderNumber", orderNumber);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("success", "true");
                        jsonObject.put("returnCode", "140002");
                        jsonObject.put("errorMsg", "退票失败");
                        jsonObject.put("data", json);
                        WriteLog.write(logname, random + "--->" + jsonObject.toString());
                        tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                        return;
                    }
                    else {
                        requestedMap.put(tn_ticket_no, true);
                    }
                }
                // 线上退票
                if (refundTypeMap.get(tn_ticket_no) == 1) {
                    // 退票必须不晚于原票开车前2小时方可进行
                    boolean timeOut = false;
                    // 开车String时间
                    String departTime = ticketType == 1 ? localTicket.getDeparttime() : localTicket.getTtcdeparttime();
                    PublicComponent PublicComponent = new PublicComponent();
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
                        ExceptionUtil.writelogByException(logname + "_Exception", e, random + "");
                    }
                    if (timeOut) {
                        WriteLog.write(logname, random + "--->车票[" + tn_ticket_no + "]距离开车时间太近无法退票");
                        JSONObject json = new JSONObject();
                        json.put("vendorOrderId", vendorOrderId);
                        json.put("orderNumber", orderNumber);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("success", "true");
                        jsonObject.put("returnCode", "140000");
                        jsonObject.put("errorMsg", "距离开车时间太近无法退票");
                        jsonObject.put("data", json);
                        WriteLog.write(logname, random + "--->" + jsonObject.toString());
                        tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                        return;
                    }
                }
                SingleTicketNo = tn_ticket_no;
                refundMap.put(tn_ticket_no, localTicket);
            }
            // 走异步
            boolean AutoRefundOpen = false;
            // 账号
            String cookie = "";
            Customeruser user = new Customeruser();
            // 自动退票、张数为1、线上
            WriteLog.write(logname, random + "--->ticketSize" + ticketSize + "refundTypeMap.get(SingleTicketNo)"
                    + refundTypeMap.get(SingleTicketNo));
            if (AutoRefundOpen && ticketSize == 1 && refundTypeMap.get(SingleTicketNo) == 1) {
                // 下单用户名
                String createAccount = order.getSupplyaccount();
                // 用户名非空
                if (!ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
                    // 下单账户
                    user = TongchengSupplyMethod.getCustomeruserBy12306Account(order, random, true);
                    // 为空
                    user = user == null ? new Customeruser() : user;
                    // Cookie非空
                    if (!ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                        cookie = user.getCardnunber();
                    }
                }
            }
            WriteLog.write(logname, random + "--->cookie" + cookie);
            // 不直接退票
            if (ElongHotelInterfaceUtil.StringIsNull(cookie)) {
                // 更新本地为申请
                int SuccessTotal = refundApply(refundMap, requestedMap, refundTicketMap, refundTypeMap, order, refundId);
                // 返回数据
                WriteLog.write(logname, random + "--->SuccessTotal" + SuccessTotal);
                if (SuccessTotal != ticketSize) {
                    JSONObject json = new JSONObject();
                    json.put("vendorOrderId", vendorOrderId);
                    json.put("orderNumber", orderNumber);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", "true");
                    jsonObject.put("returnCode", "140002");
                    jsonObject.put("errorMsg", "退票失败");
                    jsonObject.put("data", json);
                    WriteLog.write(logname, random + "--->" + jsonObject.toString());
                    tuNiuServletUtil.getResponeOut(ctx, jsonObject.toString(), logname);
                    return;
                }
                else {
                    JSONObject json = new JSONObject();
                    json.put("vendorOrderId", vendorOrderId);
                    json.put("orderNumber", orderNumber);
                    tuNiuServletUtil.respBySuccess(ctx, logname, json);
                    return;
                }
            }
            // 单个车票已请求
//            if (requestedMap.containsKey(SingleTicketNo)) {
//                JSONObject json = new JSONObject();
//                json.put("vendorOrderId", vendorOrderId);
//                json.put("orderNumber", orderNumber);
//                tuNiuServletUtil.respBySuccess(ctx, logname, json);
//                return;
//            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logname, e, random + "");
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
    }

    //    /**
    //     * 12306单个退票
    //     */
    //    private String refundSingle(Trainticket ticket, JSONObject tnTicket, Trainorder order, Customeruser user,
    //            String ticket_no, long trainStartTime, int random) {
    //        // 格式化
    //        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //        // 订单创建时间，用于12306通过时间查询订单
    //        String order_date = timeFormat.format(order.getCreatetime()).split(" ")[0];
    //        // 12306单号
    //        String sequence_no = order.getExtnumber();
    //        // 乘客信息
    //        String passengername = tnTicket.getString("passengerName");// 乘客姓名
    //        // 请求REP参数 
    //        JSONObject req = new JSONObject();
    //        req.put("ticket_no", ticket_no);
    //        req.put("order_date", order_date);
    //        req.put("sequence_no", sequence_no);
    //        req.put("cookie", user.getCardnunber());
    //        req.put("cookie",
    //                "JSESSIONID=0A01D957608EF34159572856169EB25D58A8943CD5; BIGipServerotn=1473839370.24610.0000; current_captcha_type=Z");
    //        // 用于查订单
    //        req.put("passenger_name", passengername);
    //        //退票时间限制,单位:分钟
    //        String RefundTimeLimit = getSysconfigString("RefundTimeLimit");
    //        int RefundTimeLimitIntValue = Integer.parseInt(RefundTimeLimit);
    //        req.put("RefundTimeLimit", RefundTimeLimitIntValue);
    //        req.put("refundType", PropertyUtil.getValue("refundType"));
    //        req.put("refundTime", RefundTimeLimitIntValue + 60);//退票时间+1小时内，直接退
    //
    //        /******************* 时间差判断，24、48小时临界点，半小时内(暂定)直接退 *******************/
    //        boolean refundFlag = false;
    //        // 发车时间
    //        if (trainStartTime > 0) {
    //            // 开车时间-当前时间
    //            long timesub = trainStartTime - System.currentTimeMillis();
    //            // 24H@48H@30
    //            String[] refundTimeDifference = PropertyUtil.getValue("refundTimeDifference").split("@");
    //            // 长度
    //            int refundTimeDifferenceLen = refundTimeDifference.length;
    //            // 时间差Long值
    //            long diffTime = Long.parseLong(refundTimeDifference[refundTimeDifferenceLen - 1]) * 60 * 1000;
    //            // 循环判断
    //            for (int i = 0; i < refundTimeDifferenceLen - 1; i++) {
    //                String TempTime = refundTimeDifference[i];
    //                // 结尾
    //                String TimeType = TempTime.substring(TempTime.length() - 1);
    //                // 时间String值
    //                TempTime = TempTime.substring(0, TempTime.length() - 1);
    //                // 时间Long值
    //                long TempLongTime = Long.parseLong(TempTime);
    //                // 分钟
    //                if ("M".equalsIgnoreCase(TimeType)) {
    //                    TempLongTime = TempLongTime * 60 * 1000;
    //                }
    //                // 小时
    //                else if ("H".equalsIgnoreCase(TimeType)) {
    //                    TempLongTime = TempLongTime * 60 * 60 * 1000;
    //                }
    //                // 天
    //                else if ("D".equalsIgnoreCase(TimeType)) {
    //                    TempLongTime = TempLongTime * 24 * 60 * 60 * 1000;
    //                }
    //                if (timesub > TempLongTime && timesub < TempLongTime + diffTime) {
    //                    refundFlag = true;
    //                    break;
    //                }
    //            }
    //        }
    //        else {
    //            refundFlag = true;
    //        }
    //        req.put("refundFlag", refundFlag);
    //        /******************* 时间差判断，24、48小时临界点，半小时内(暂定)直接退 *******************/
    //
    //        // REP
    //        String url = "";
    //        String retdata = "";
    //        JSONObject obj12306 = new JSONObject();
    //        try {
    //            RepServerBean rep = RepServerUtil.getRepServer(user, false);
    //            url = rep.getUrl();
    //            // REP地址为空
    //            if (ElongHotelInterfaceUtil.StringIsNull(url)) {
    //                throw new Exception("REP地址为空");
    //            }
    //            // 请求参数URLEncoder
    //            String jsonStr = URLEncoder.encode(req.toJSONString(), "UTF-8");
    //            // 请求参数
    //            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
    //            // 请求REP
    //            retdata = SendPostandGet.submitPost(url, param, "UTF-8").toString();
    //            // 返回数据
    //            obj12306 = JSONObject.parseObject(retdata);
    //        }
    //        catch (Exception e) {
    //        }
    //        finally {
    //            if (obj12306 == null) {
    //                obj12306 = new JSONObject();
    //            }
    //            WriteLog.write("t途牛火车票接口_4.10在线退票", random + ">>>>>>" + order.getId() + ">>>>>REP服务器地址>>>>>" + url
    //                    + ">>>>>REP返回>>>>>" + retdata);
    //        }
    //        // 解析REP返回数据
    //        String code = obj12306.getString("code");
    //        // 成功
    //        if (obj12306.getBooleanValue("success")) {
    //            refundSuccess(order, obj12306, ticket, tnTicket, ticket_no);
    //            // 返回数据
    //            return "ok";
    //        }
    //        // 不可退票标示
    //        else if (!ElongHotelInterfaceUtil.StringIsNull(code)) {
    //            JSONObject retobj = new JSONObject();
    //            retobj.put("success", false);
    //            retobj.put("code", code);
    //            retobj.put("msg", obj12306.getString("msg"));
    //            return retobj.toJSONString();
    //        }
    //        return "apply";
    //    }

    /**
    * 单个退票成功
    */
    //    private void refundSuccess(Trainorder order, JSONObject obj12306, Trainticket ticket, JSONObject tnTicket,
    //            String ticket_no) {
    //        try {
    //            // 车票票款
    //            float ticket_price = obj12306.getFloatValue("ticket_price");
    //            // 应退票款
    //            float return_price = obj12306.getFloatValue("return_price");
    //            // 退票费
    //            float return_cost = obj12306.getFloatValue("return_cost");
    //            // 更新车票
    //            ticket.setRefundType(1);
    //            ticket.setIsapplyticket(1);
    //            ticket.setProcedure(return_cost);
    //            ticket.setStatus(Trainticket.WAITREFUND);
    //            ticket.setState12306(Trainticket.REFUNDED12306);
    //            ticket.setRefundRequestTime(ElongHotelInterfaceUtil.getCurrentTime());
    //            ticket.setRefundsuccesstime(ticket.getRefundRequestTime());
    //            Server.getInstance().getTrainService().updateTrainticket(ticket);
    //            // 乘客信息
    //            String passportseno = tnTicket.getString("passportseno");// 证件号
    //            String passengername = tnTicket.getString("passengername");// 乘客姓名
    //            // 票类型
    //            String ticketType = getTicketType(ticket);// 票类型
    //                                                      // 1:成人票，2:儿童票，3:学生票，4:残军票
    //            // 日志内容
    //            String logContent = "乘客[" + passengername + "][" + passportseno + "][" + ticketType + "]申请退票，票号："
    //                    + ticket_no + "，12306<span style='color:red;'>退票成功</span>，等待退款，车票票款：" + ticket_price + "，应退票款："
    //                    + return_price + "，退票费：" + return_cost;
    //            // 保存日志
    //            createtrainorderrc(1, logContent, order.getId(), ticket.getId(), Trainticket.WAITREFUND);
    //        }
    //        catch (Exception e) {
    //            String log = "订单[" + order.getId() + "]>>>接口退票，更新车票状态为已退票-等待退款异常，请求已接收: " + e.getMessage();
    //
    //        }
    //    }

    /**
     * 根据sysconfig的name获得value
     * 内存中
     * @param name
     * @return
     */
    protected String getSysconfigString(String name) {
        String result = "-1";
        try {
            if (Server.getInstance().getDateHashMap().get(name) == null) {
                List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                        .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
                if (sysoconfigs.size() > 0) {
                    WriteLog.write("TongchengSupplyMethod_getcustomeruser", "" + sysoconfigs.size());
                    result = sysoconfigs.get(0).getValue() != null ? sysoconfigs.get(0).getValue() : "-1";
                    Server.getInstance().getDateHashMap().put(name, result);
                }
            }
            else {
                result = Server.getInstance().getDateHashMap().get(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
            String refundId) {
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
                        + Trainticket.APPLYTREFUND + ", C_ISAPPLYTICKET = " + isApplyTicket
                        + ", C_REFUNDREQUESTTIME = '" + ElongHotelInterfaceUtil.getCurrentTime()
                        + "', C_APPLYTICKETFLAG = " + applyTicketFlag + ", C_INTERFACETYPE = " + interfaceType
                        + ",C_INSURENO='" + refundId + "' where ID = " + ticket.getId();
                // 更新成功
                if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) == 1) {
                    SuccessTotal++;
                    JSONObject tnTicket = refundTicketMap.get(ticket_no);
                    // 乘客信息
                    String passportseno = tnTicket.getString("passportNo");// 证件号
                    String passengername = tnTicket.getString("passengerName");// 乘客姓名
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
            WriteLog.write("途牛新版退票接口接口", log);
            ExceptionUtil.writelogByException("途牛新版退票接口接口_ERROR", e);
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
    public static void createtrainorderrc(int ywtype, String content, Long orderid, Long ticketid, int status) {
        Trainorderrc rc = new Trainorderrc();
        rc.setContent(content);
        rc.setCreateuser("系统接口");// 创建者
        rc.setOrderid(orderid);
        rc.setStatus(status);
        rc.setTicketid(ticketid);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

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
        rc.setCreateuser(createuser);// 创建者
        rc.setOrderid(orderid);
        rc.setStatus(status);
        rc.setTicketid(ticketid);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 请求REP账号公共参数
     */
    public String CommonAccountInfo(Customeruser user, RepServerBean rep) {
        try {
            JSONObject obj = new JSONObject();
            //REP相关
            obj.put("repType", rep.getType());
            obj.put("serverIp", rep.getServerIp());
            obj.put("serverPort", rep.getServerPort());
            obj.put("serverPassword", rep.getServerPassword());
            //账号相关
            obj.put("loginName", user.getLoginname());
            obj.put("loginPwd", user.getLogpassword());
            obj.put("login12306Ip", user.getPostalcode());
            return URLEncoder.encode(obj.toString(), "UTF-8");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 请求REP账号公共参数，注意必须要有CommonAccountInfo方法的参数
     */
    public String JoinCommonAccountInfo(Customeruser user, RepServerBean rep) {
        return "&accountInfo=" + CommonAccountInfo(user, rep);
    }

}
