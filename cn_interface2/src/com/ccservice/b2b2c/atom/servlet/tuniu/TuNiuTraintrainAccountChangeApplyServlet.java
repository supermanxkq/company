package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountSrcUtil;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 途牛请求改签接口
 * @time 2015年11月19日 下午5:43:30
 * 朱李旭
 **/
@SuppressWarnings("serial")
public class TuNiuTraintrainAccountChangeApplyServlet extends HttpServlet {

    private final String logname = "tuniu_3_17_3_请求改签接口";

    //当前不提供服务
    private static final String code113 = "113";

    private static final String code999 = "999";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TuNiuDesUtil TuNiuDesUtil = new TuNiuDesUtil();
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        final AsyncContext ctx = req.startAsync();
        int r1 = new Random().nextInt(10000);
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
        BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String reqString = buf.toString();
        WriteLog.write(logname, r1 + "--->" + reqString);

        try {
            //请求json
            JSONObject reqjso = JSONObject.parseObject(reqString);
            String account = tuNiuServletUtil.getParamByJsonStr("account", reqjso);//账号
            String sign = tuNiuServletUtil.getParamByJsonStr("sign", reqjso);//加密结果
            String timestamp = tuNiuServletUtil.getParamByJsonStr("timestamp", reqjso);//请求时间
            String data = tuNiuServletUtil.getParamByJsonStr("data", reqjso);//加密的请求体
            WriteLog.write(logname, r1 + "--->account:" + account + "--->sign:" + sign + "--->timestamp:" + timestamp
                    + "--->data:" + data);
            if ("".equals(account) || "".equals(sign) || "".equals(timestamp) || "".equals(data)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }

            //获取账户信息
            Map map = tuNiuServletUtil.getInterfaceAccount(account);
            WriteLog.write(logname, r1 + "--->map:" + map);
            String agentid = tuNiuServletUtil.getParamByMapStr("C_AGENTID", map);
            String key = tuNiuServletUtil.getParamByMapStr("C_KEY", map);
            String password = tuNiuServletUtil.getParamByMapStr("C_ARG2", map);
            if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                tuNiuServletUtil.respByUserNotExists(ctx, logname);
                return;
            }
            JSONObject object = JSONObject.parseObject(reqString);
            object.put("sign", "");
            //获取key加密
            String localsign = SignUtil.generateSign(object.toString(), key);
            WriteLog.write(logname, r1 + "--->localsign:" + localsign + ";sign:" + sign);
            if (!sign.equalsIgnoreCase(localsign)) {
                tuNiuServletUtil.respBySignatureError(ctx, logname);
                return;
            }
            String paramStr = TuNiuDesUtil.decrypt(data);
            WriteLog.write(logname, r1 + "--->paramStr:" + paramStr);
            JSONObject jsonString = new JSONObject();
            try {
                jsonString = JSONObject.parseObject(paramStr);
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException(logname, e1, r1 + "");
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }

            JSONObject retobj = new JSONObject();
            retobj.put("success", false);
            //接口不接收线上改签请求
            String changeRequestClose = "0";
            //KEY
            String changeRequestCloseKey = "changeRequestClose";
            //内存无数据
            if (!Server.getInstance().getDateHashMap().containsKey(changeRequestCloseKey)) {
                //从配置文件取数据
                changeRequestClose = PropertyUtil.getValue(changeRequestCloseKey);
                //配置文件无数据，设为0
                changeRequestClose = changeRequestClose == null ? "0" : changeRequestClose;
                //配置文件放到内存
                Server.getInstance().getDateHashMap().put(changeRequestCloseKey, changeRequestClose);
            }
            else {
                changeRequestClose = Server.getInstance().getDateHashMap().get(changeRequestCloseKey);
            }
            WriteLog.write(logname, r1 + "--->changeRequestClose:" + changeRequestClose);
            if ("1".equals(changeRequestClose)) {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;

            }
            //请求参数
            String vendorOrderId = jsonString.containsKey("vendorOrderId") ? jsonString.getString("vendorOrderId") : "";//合作伙伴方订单号
            String orderId = jsonString.containsKey("orderId") ? jsonString.getString("orderId") : "";//途牛订单号
            String retoken = jsonString.containsKey("changeId") ? jsonString.getString("changeId") : "";//途牛改签请求流水号（后续改签的反馈取消等依据此流水号）一个订单多张票，每张票支持独立的改签请求
            String ordernumber = jsonString.containsKey("orderNumber") ? jsonString.getString("orderNumber") : "";//取票号
            String change_checi = jsonString.containsKey("changeCheCi") ? jsonString.getString("changeCheCi") : "";//改签新票车次
            String change_datetime = jsonString.containsKey("changeDateTime") ? jsonString.getString("changeDateTime")
                    : "";//改签新票发车时间
            String change_zwcode = jsonString.containsKey("changeZwCode") ? jsonString.getString("changeZwCode") : "";//改签新车票座位编码
            String old_zwcode = jsonString.containsKey("oldZwCode") ? jsonString.getString("oldZwCode") : "";//原车票坐位编码
            Boolean hasSeat = jsonString.containsKey("hasSeat") ? jsonString.getBooleanValue("hasSeat") : false;//改签是否接受无座
            JSONArray oldTicketInfos = jsonString.containsKey("oldTicketInfos") ? jsonString
                    .getJSONArray("oldTicketInfos") : new JSONArray();//原车票信息
            //同程变站标识
            String tongchengTsFlag = "isChangeTo";
            jsonString.put("partnerid", account);
            //变站标识
            boolean isTs = jsonString.containsKey("isTs") ? jsonString.getBooleanValue("isTs") : false;
            //非变站且传了to_station_name字段
            if (!isTs && jsonString.containsKey("to_station_name")) {
                jsonString.remove("to_station_name");
            }
            //变更到站
            String to_station_name = jsonString.containsKey("to_station_name") ? jsonString
                    .getString("to_station_name") : "";
            //验证
            if (ElongHotelInterfaceUtil.StringIsNull(vendorOrderId) || ElongHotelInterfaceUtil.StringIsNull(orderId)
                    || ElongHotelInterfaceUtil.StringIsNull(ordernumber)
                    || ElongHotelInterfaceUtil.StringIsNull(change_checi)
                    || ElongHotelInterfaceUtil.StringIsNull(change_datetime)
                    || ElongHotelInterfaceUtil.StringIsNull(change_zwcode)
                    || ElongHotelInterfaceUtil.StringIsNull(old_zwcode) || oldTicketInfos.size() == 0
                    || (isTs && ElongHotelInterfaceUtil.StringIsNull(to_station_name))) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //改签占座异步回调地址
            String callBackUrl = jsonString.containsKey("callBackUrl") ? jsonString.getString("callBackUrl") : "";
            if (ElongHotelInterfaceUtil.StringIsNull(callBackUrl)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //查询订单
            Trainform trainform = new Trainform();
            trainform.setQunarordernumber(orderId);
            trainform.setOrdernumber(vendorOrderId);
            WriteLog.write(logname, r1 + "--->orderid:" + orderId + ";changeId:" + vendorOrderId);
            List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
            WriteLog.write(logname, r1 + "--->orders:" + orders.size());
            //订单不存在
            if (orders == null || orders.size() != 1) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            Trainorder order = orders.get(0);
            order.setQunarOrdernumber(orderId);
            //12306订单号
            if (!ordernumber.equals(order.getExtnumber())) {
                WriteLog.write(logname,
                        r1 + "--->ordernumber:" + ordernumber + "--->order.getExtnumber():" + order.getExtnumber());
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //数据校验
            RequestCheck(order, jsonString, 0, r1, ctx);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * 改签请求数据校验 
    * @param order 本地订单
    * @param reqobj 请求数据
    * @param changeType 改签类型，0：改签；1：改签退
    */
    public void RequestCheck(Trainorder order, JSONObject reqobj, int changeType, int r1, AsyncContext ctx) {
        WriteLog.write(logname, r1 + "--->changeType:" + changeType + "--->reqobj:" + reqobj.toString());

        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //改签车票信息
        JSONArray oldTicketInfos = reqobj.getJSONArray("oldTicketInfos");
        //改签车票出发时间判断
        String change_datetime = reqobj.getString("changeDateTime");
        //是否接受改签无座  false允许改到无座 ;  true 不该到无座票
        boolean hasSeat = reqobj.getBooleanValue("hasSeat") ? reqobj.getBooleanValue("hasSeat") : false;
        try {
            //FORMAT
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(change_datetime);
        }
        catch (Exception e) {
            retobj.put("code", "108");
            retobj.put("msg", "改签新车票出发时间[" + change_datetime + "]格式错误，应为yyyy-MM-dd HH:mm:ss");
            WriteLog.write(logname, r1 + "--->retobj:" + retobj.toString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        //原票的座位席别编码
        String old_zwcode = reqobj.getString("oldZwCode");
        //改签退，新车票的价格
        float change_price = changeType == 1 ? reqobj.getFloatValue("change_price") : 0;
        //改签新车票的座位席别编码
        String change_zwcode = reqobj.getString("changeZwCode");
        //key：车票号，value：改签乘客信息
        Map<String, JSONObject> reqTickets = new HashMap<String, JSONObject>();
        //下单参数
        JSONObject reqpassengers = new JSONObject();
        String prices = "";
        String zwcodes = "";
        String oldPassengerStr = "";
        String passengerTicketStr = "";
        //循环车票
        for (int i = 0; i < oldTicketInfos.size(); i++) {
            JSONObject temp = oldTicketInfos.getJSONObject(i);
            String passengersename = temp.containsKey("passengerName") ? temp.getString("passengerName") : "";//姓名
            String passporttypeseid = temp.containsKey("passportTypeId") ? temp.getString("passportTypeId") : "";//证件类型
            String passportseno = temp.containsKey("passportNo") ? temp.getString("passportNo") : "";//证件号
            String piaotype = temp.containsKey("piaoType") ? temp.getString("piaoType") : "";//票类型
            String old_ticket_no = temp.containsKey("oldTicketNo") ? temp.getString("oldTicketNo") : "";//原车票号
            WriteLog.write("进入改签请求数据校验", "passengersename" + passengersename + "passporttypeseid" + passporttypeseid
                    + "passportseno" + passportseno + "piaotype" + piaotype + "reqTickets" + reqTickets
                    + "old_ticket_no" + old_ticket_no);
            if (ElongHotelInterfaceUtil.StringIsNull(passengersename)
                    || ElongHotelInterfaceUtil.StringIsNull(passporttypeseid)
                    || ElongHotelInterfaceUtil.StringIsNull(passportseno)
                    || ElongHotelInterfaceUtil.StringIsNull(piaotype)
                    || ElongHotelInterfaceUtil.StringIsNull(old_ticket_no)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            WriteLog.write("进入改签请求数据校验", "__________1");
            if (reqTickets.containsKey(old_ticket_no)) {
                WriteLog.write(logname,
                        r1 + "--->reqTickets.containsKey(old_ticket_no):" + reqTickets.containsKey(old_ticket_no));
                tuNiuServletUtil.respByUnknownError(ctx, logname);
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
        reqpassengers.put("prices", prices.substring(0, prices.length() - 1));//车票价
        reqpassengers.put("zwcodes", zwcodes.substring(0, zwcodes.length() - 1));//原坐席
        reqpassengers.put("oldPassengerStr", oldPassengerStr);
        reqpassengers.put("passengerTicketStr", passengerTicketStr.substring(0, passengerTicketStr.length() - 1));
        //批量改签时，选择的新票座位席别必须一致，并且不能是卧铺。
        //P:特等座，M:一等座，O:二等座，E:特等软座，9:商务座，8：二等软座，7：一等软座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座
        WriteLog.write("请求改签接口十三", "改签乘客信息" + reqpassengers.toString());
        if (oldTicketInfos.size() > 1) {
            //            //不允许改到无座
            //            if(!hasSeat){
            //                if("0".equals(change_zwcode)){
            //                    retobj.put("code", "108");
            //                    retobj.put("msg", "不允许改签到无座");
            //                    return retobj.toString();
            //                }
            //            }
            if ("6".equals(old_zwcode) || "4".equals(old_zwcode) || "3".equals(old_zwcode)) {
                WriteLog.write(logname, r1 + "--->old_zwcode:" + old_zwcode);
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            if ("6".equals(change_zwcode) || "4".equals(change_zwcode) || "3".equals(change_zwcode)) {
                WriteLog.write(logname, r1 + "--->change_zwcode:" + change_zwcode);
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
        }
        LocalCheck(order, reqobj, reqpassengers, oldTicketInfos, reqTickets, change_datetime, change_zwcode,
                changeType, r1, ctx);
    }

    /**
     * 本地数据校验
     * @param order 本地订单
     * @param reqobj 请求JSON
     * @param oldTicketInfos 请求车票JSON
     * @param reqTickets 请求车票Map
     * @param change_datetime 改签新车票出发String时间
     * @param change_zwcode 改签新车票的座位席别编码
     */
    private void LocalCheck(Trainorder order, JSONObject reqobj, JSONObject reqpassengers, JSONArray oldTicketInfos,
            Map<String, JSONObject> reqTickets, String change_datetime, String change_zwcode, int changeType, int r1,
            AsyncContext ctx) {
        //是否为改签变站
        boolean isTs = reqobj.containsKey("isTs") ? reqobj.getBooleanValue("isTs") : false;
        //校验结果
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //已出票
        int status = order.getOrderstatus();
        if (status != Trainorder.ISSUED) {
            WriteLog.write(logname, r1 + "--->status:" + status);
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        long orderId = order.getId();
        //校验现有改签
        JSONObject ChangeCheck = ChangeCheck(orderId);
        //校验失败，返回结果
        if (!ChangeCheck.getBooleanValue("success")) {
            WriteLog.write(logname, r1 + "--->ChangeCheck:" + ChangeCheck.toJSONString());
            JSONObject dataJsonObject = new JSONObject();
            dataJsonObject.put("vendorOrderId", reqobj.getString("vendorOrderId"));
            JSONObject resultJsonObject = new JSONObject();
            resultJsonObject.put("success", false);
            resultJsonObject.put("returnCode", 1702);
            resultJsonObject.put("errorMsg", "取消改签次数超过上限，无法继续操作");
            resultJsonObject.put("data", dataJsonObject);
            tuNiuServletUtil.getResponeOut(ctx, resultJsonObject.toString(), logname);
            return;
        }
        //加载其他字段、乘客
        if (changeType == 0) {
            order = Server.getInstance().getTrainService().findTrainorder(orderId);
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
        WriteLog.write("请求改签接口十四", "oldTicketMap" + oldTicketMap + "passengerMap" + passengerMap);
        for (String old_ticket_no : reqTickets.keySet()) {
            Trainticket oldTicket = null;
            Trainpassenger ticketPassenger = null;
            //循环乘客找车票
            out: for (Trainpassenger passenger : passengers) {
                List<Trainticket> traintickets = passenger.getTraintickets();
                if (traintickets == null || traintickets.size() == 0) {
                    continue;
                }
                //多张票可能是联程
                for (Trainticket trainticket : traintickets) {
                    //改签票号
                    String tcticketno = trainticket.getTcticketno();
                    if (old_ticket_no.equals(tcticketno)) {
                        WriteLog.write(logname, r1 + "--->old_ticket_no:" + old_ticket_no + "--->tcticketno:"
                                + tcticketno);
                        tuNiuServletUtil.respByUnknownError(ctx, logname);
                        return;
                    }
                    //票号
                    String ticketno = trainticket.getTicketno();
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
            WriteLog.write("请求改签接口十五", "oldTicket" + oldTicket);
            if (oldTicket == null) {
                WriteLog.write(logname, r1 + "--->oldTicket:null");
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            //开车时间
            String departTime = oldTicket.getDeparttime();
            //校验车票状态
            JSONObject TicketCheck = TicketCheck(oldTicket, old_ticket_no, departTime, change_datetime, changeType,
                    isTs);
            //FALSE

            if (!TicketCheck.getBooleanValue("success")) {
                WriteLog.write(logname, r1 + "--->TicketCheck:票状态有误");
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            int tickettype = oldTicket.getTickettype();
            //校验乘客信息
            JSONObject PassengerCheck = PassengerCheck(old_ticket_no, String.valueOf(tickettype),
                    reqTickets.get(old_ticket_no), ticketPassenger);
            WriteLog.write("改签15.1", "2" + "PassengerCheck" + PassengerCheck);
            //FALSE
            if (!PassengerCheck.getBooleanValue("success")) {
                WriteLog.write(logname, r1 + "--->PassengerCheck:乘客信息有误");
                tuNiuServletUtil.respByUnknownError(ctx, logname);
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
            if (!piaoTypes.contains(tickettype)) {
                piaoTypes.add(tickettype);
            }
            oldTicketMap.put(old_ticket_no, oldTicket);
            passengerMap.put(old_ticket_no, ticketPassenger);
            tcOriginalPrice = ElongHotelInterfaceUtil.floatAdd(tcOriginalPrice, oldTicket.getPrice());
        }
        WriteLog.write("改签15.3", "oldTicketInfos" + oldTicketInfos.toString());
        //批量改签时，原票不能是卧铺
        //同一订单中相同日期、车次、发站、到站、席别的车票方可批量改签
        if (oldTicketInfos.size() > 1) {
            if (seatTypes.size() == 0 || seatTypes.size() > 1 || ticketMsgList.size() == 0 || ticketMsgList.size() > 1) {
                WriteLog.write(logname, r1 + "--->seatTypes.size():" + seatTypes.size() + "---> ticketMsgList.size():"
                        + ticketMsgList.size());
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            String seatType = seatTypes.get(0);
            WriteLog.write("改签15.2", "22222");
            if (seatType.contains("卧") && !"硬卧代硬座".equals(seatType) && !"软卧代软座".equals(seatType)
                    && !"软卧代二等座".equals(seatType)) {
                retobj.put("code", "108");
                retobj.put("msg", "批量改签时，原票不能是卧铺");
                WriteLog.write(logname, r1 + "--->retobj:" + retobj.toString());
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
        }
        //保存
        saveThirdAccountInfo(order.getId(), reqobj);
        AsyncChange(order, reqobj, piaoTypes, change_zwcode, change_datetime, tcOriginalPrice, oldTicketMap,
                passengerMap, isTs, changeType, r1, ctx);
    }

    /**
     * 异步改签
     */
    private void AsyncChange(Trainorder order, JSONObject reqobj, List<Integer> piaoTypes, String change_zwcode,
            String change_datetime, float tcOriginalPrice, Map<String, Trainticket> oldTicketMap,
            Map<String, Trainpassenger> passengerMap, boolean isTs, int changeType, int r1, AsyncContext ctx) {
        //改签占座异步回调地址
        String callbackurl = reqobj.getString("callBackUrl");
        //变站
        String to_station_name = reqobj.containsKey("toStationName") ? reqobj.getString("toStationName") : "";
        //配置地址
        String systemSetUrl = changeType == 1 ? "" : getTrainCallBackUrl(order.getAgentid(), 1);
        //非空，取配置
        if (!ElongHotelInterfaceUtil.StringIsNull(systemSetUrl)) {
            callbackurl = systemSetUrl;
        }
        //返回数据
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //订单ID
        long orderId = order.getId();
        change_datetime = reqobj.getString("changeDateTime");
        //新增改签
        Trainorderchange trainOrderChange = new Trainorderchange();
        trainOrderChange.setOrderid(orderId);
        trainOrderChange.setOrderAgentId(order.getAgentid());//代理ID
        trainOrderChange.setTctrainno(reqobj.getString("changeCheCi"));//改签车次
        trainOrderChange.setTctickettype(piaoTypes.size() == 1 ? piaoTypes.get(0) : 1);//票类型
        trainOrderChange.setTcseattype(TongchengSupplyMethod.getzwname(tuNiuServletUtil
                .tuniuSeatCode2DBSeatName(change_zwcode)));//座席名称，默认为编码
        trainOrderChange.setSeatTypeCode(TongchengSupplyMethod.getzwname(tuNiuServletUtil
                .tuniuSeatCode2DBSeatName(change_zwcode)));
        trainOrderChange.setTcprice(changeType == 1 ? reqobj.getFloatValue("price") : 0);//新价格
        trainOrderChange.setTccreatetime(ElongHotelInterfaceUtil.getCurrentTime());//改签订单创建时间
        trainOrderChange.setTcdeparttime(change_datetime.substring(0, change_datetime.length() - 3));//出发时间
        trainOrderChange.setTcstatus(Trainorderchange.APPLYCHANGE);//状态
        trainOrderChange.setStatus12306(Trainorderchange.WAITORDER);//12306状态
        trainOrderChange.setTcprocedure(0);//手续费
        trainOrderChange.setTcmemo(changeType == 1 ? reqobj.getString("refundTicket") : "改签申请成功");//备注
        trainOrderChange.setTcpaystatus(0);//支付状态
        trainOrderChange.setIsQuestionChange(0);//正常订单
        trainOrderChange.setTcischangerefund(changeType);//是否是退改签  1:退改签  0:改签
        trainOrderChange.setTcoriginalprice(tcOriginalPrice);//原票价总和
        trainOrderChange.setTcislowchange(0);//是否是低改-->1 低改: 改签后价格<=原价格; 0 高改: 改签后价格>原价格
        trainOrderChange.setRequestIsAsync(1);
        trainOrderChange.setRequestCallBackUrl(callbackurl);
        trainOrderChange.setRequestReqtoken(reqobj.getString("changeId"));
        String stationName = "";//车站
        String tcTicketId = "";//车票ID
        String passengerName = "";//乘客姓名
        String updateTicketId = "";//更新车票
        for (String old_ticket_no : oldTicketMap.keySet()) {
            //更新车票
            WriteLog.write(logname, r1 + "--->oldTicketMap:" + oldTicketMap.toString());
            Trainticket oldTicket = oldTicketMap.get(old_ticket_no);
            Trainpassenger passenger = passengerMap.get(old_ticket_no);
            tcTicketId += oldTicket.getId() + "@";
            updateTicketId += oldTicket.getId() + ",";
            passengerName += passenger.getName() + "<br/>";
            //变更到站
            if (isTs) {
                stationName = oldTicket.getDeparture() + " - " + to_station_name;
            }
            else {
                stationName = oldTicket.getDeparture() + " - " + oldTicket.getArrival();
            }
            WriteLog.write(logname, r1 + "--->stationName:" + stationName);
        }
        trainOrderChange.setTcTicketId(tcTicketId);
        trainOrderChange.setStationName(stationName);
        trainOrderChange.setPassengerName(passengerName);
        trainOrderChange.setTcnumber((isTs ? "TS" : "TC") + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
                + orderId);//变更号
        trainOrderChange = Server.getInstance().getTrainService().createTrainorderchange(trainOrderChange);
        //改签ID
        long changeId = trainOrderChange.getId();

        WriteLog.write(logname, r1 + "--->changeId:" + changeId);
        //保存数据失败
        if (changeId <= 0) {
            tuNiuServletUtil.respByUnknownError(ctx, logname);
        }
        else {
            //记录日志
            String changeTSFlag = isTs ? "变更到站" : "改签";
            createtrainorderrc(1, "[" + changeTSFlag + " - " + changeId + "]提交" + changeTSFlag + "申请成功", orderId, 0l,
                    Trainticket.APPLYCHANGE, changeType == 1 ? "自动改签退" : "系统接口");
            //车票ID
            updateTicketId = updateTicketId.substring(0, updateTicketId.length() - 1);
            //更新SQL
            String updateTicketSql = "update T_TRAINTICKET set C_CHANGEID = " + changeId + ", C_STATUS = "
                    + Trainticket.APPLYCHANGE + " where ID in (" + updateTicketId + ") and C_STATUS = "
                    + Trainticket.ISSUED;
            //改签退
            if (changeType == 1) {
                updateTicketSql = "update T_TRAINTICKET set C_CHANGEID = " + changeId + " where ID in ("
                        + updateTicketId + ") and C_STATUS = " + Trainticket.REFUNDROCESSING;
            }
            //更新车票
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateTicketSql);
            //队列下单
            activeMQChangeOrder(changeId, 1);
            JSONObject dataJsonObject = new JSONObject();
            dataJsonObject.put("vendorOrderId", reqobj.getString("vendorOrderId"));
            tuNiuServletUtil.respBySuccess(ctx, logname, dataJsonObject);
        }
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
            long timesub = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(departTime).getTime()
                    - System.currentTimeMillis();
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
                        retobj.put("code", "1004");
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
        WriteLog.write("校验乘客信息", reqTicket.toString());
        String piaotype = reqTicket.getString("piaoType");
        String passportseno = reqTicket.getString("passportNo");
        String passengersename = reqTicket.getString("passengerName");
        String passporttypeseid = reqTicket.getString("passportTypeId");
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
     * 获取回调地址
     * @param agentId 代理ID
     * @param callBackType 回调类型 1:请求改签; 2:确认改签
     * @return 回调地址
     */
    @SuppressWarnings("rawtypes")
    public String getTrainCallBackUrl(Long agentId, int callBackType) {
        String url = "";
        if (agentId != null && agentId > 0) {
            String columnName = "";
            //请求改签
            if (callBackType == 1) {
                columnName = "C_REQUESTCHANGECALLBACKURL";
            }
            //确认改签
            else if (callBackType == 2) {
                columnName = "C_CONFIRMCHANGECALLBACKURL";
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(columnName)) {
                try {
                    String sql = "SELECT " + columnName + " FROM T_INTERFACEACCOUNT WITH(NOLOCK) "
                            + "WHERE C_AGENTID = " + agentId;
                    List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    if (list != null && list.size() == 1) {
                        Map map = (Map) list.get(0);
                        url = map.get(columnName) == null ? "" : map.get(columnName).toString();
                    }
                }
                catch (Exception e) {
                }
            }
        }
        return url;
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
     * 异步改签扔进队列里处理
     * @param changeId 改签ID
     * @param type 类型，1：请求改签(占座)；2:确认改签(支付)；3：改签支付审核
     */
    public void activeMQChangeOrder(long changeId, int type) {
        if (changeId > 0 && (type == 1 || type == 2 || type == 3)) {
            String QUEUE_NAME = "";
            String MQ_URL = PropertyUtil.getValue("activeMQ_url", "Train.properties");
            //占座
            if (type == 1) {
                QUEUE_NAME = PropertyUtil.getValue("QueueMQ_TrainChange_WaitOrder_ChangeId", "Train.properties");
            }
            //支付
            else if (type == 2) {
                QUEUE_NAME = PropertyUtil.getValue("QueueMQ_TrainChange_ConfirmOrder_ChangeId", "Train.properties");
            }
            //支付审核
            else if (type == 3) {
                QUEUE_NAME = PropertyUtil.getValue("QueueMQ_TrainChange_PayExamine_ChangeId", "Train.properties");
            }
            try {
                ActiveMQUtil.sendMessage(MQ_URL, QUEUE_NAME, String.valueOf(changeId));
            }
            catch (Exception e) {
                WriteLog.write(QUEUE_NAME, e.getMessage() + "---" + ElongHotelInterfaceUtil.errormsg(e));
            }
        }
    }

    /**
     * 保存第三方账号信息
     * @param orderId 订单ID
     * @param json 接口传过来的json
     * @time 2015年10月22日 下午9:55:22
     * @author 王成亮
     */
    public void saveThirdAccountInfo(long orderId, JSONObject json) {
        //cookie
        String cookie = json.getString("cookie");
        //账号名称
        String accountName = getUsername(json);
        //账号密码
        String accontPassword = getUserPassword(json);
        //partnerid
        String partnerid = json.getString("partnerid");
        //逻辑判断
        if (!ElongHotelInterfaceUtil.StringIsNull(cookie)
                || (!ElongHotelInterfaceUtil.StringIsNull(accountName) && !ElongHotelInterfaceUtil
                        .StringIsNull(accontPassword))) {
            addTrainAccountInfo(accountName, accontPassword, partnerid, orderId, cookie);
        }
    }

    /**
     * 将登录用的帐号名密码或Cookie存入数据库
     * 
     * @param username12306  帐号名
     * @param userpassword12306 密码
     * @param partnerid 代理商ID
     * @param trainorderid 订单号ID
     * @param cookie12306 
     * @time 2015年10月23日 下午7:26:00
     * @author Administrator
     */
    private void addTrainAccountInfo(String username12306, String userpassword12306, String partnerid,
            long trainorderid, String cookie12306) {
        TrainAccountSrcUtil.insertData(username12306, userpassword12306, partnerid, trainorderid, cookie12306);
    }

    //获取用户密码
    public String getUserPassword(JSONObject jsonObject) {
        String userpassword = jsonObject.getString("userPassword");
        return userpassword;
    }

    //获取用户名
    public String getUsername(JSONObject jsonObject) {
        String username = jsonObject.getString("userName");
        return username;
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

}
