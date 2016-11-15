package com.ccservice.b2b2c.atom.hotel;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



import org.apache.log4j.Logger;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TaoBaoReqChange;
import com.ccservice.b2b2c.atom.servlet.TaobaoTrainInsure;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainReturnTicket;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.taobao.TaobaoRefund;
import com.ccservice.b2b2c.atom.taobao.thread.MyThreadTaoBaoOrderOfflineAdd;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.b2b2c.util.TimeUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.AgentRefuseChangeParam;
import com.taobao.api.domain.AgentSeatPriceRq;
import com.taobao.api.domain.SessionItem;
import com.taobao.api.domain.TopSessionResult;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.api.request.TmcUserPermitRequest;
import com.taobao.api.request.TrainAgentAutorefundRefundfeeRequest;
import com.taobao.api.request.TrainAgentBookordersGetRequest;
import com.taobao.api.request.TrainAgentBookticketConfirmRequest;
import com.taobao.api.request.TrainAgentChangeAgreeRequest;
import com.taobao.api.request.TrainAgentChangeAgreeRequest.AgentAgreeChangeParam;
import com.taobao.api.request.TrainAgentChangeAgreeRequest.ChangeTicketInfo;
import com.taobao.api.request.TrainAgentChangeGetRequest;
import com.taobao.api.request.TrainAgentChangeRefuseRequest;
import com.taobao.api.request.TrainAgentChangeordersGetRequest;
import com.taobao.api.request.TrainAgentExpressSetRequest;
import com.taobao.api.request.TrainAgentHandleticketConfirmRequest;
import com.taobao.api.request.TrainAgentMobileSendsmsRequest;
import com.taobao.api.request.TrainAgentOrderGetRequest;
import com.taobao.api.request.TrainAgentReturnordersGetRequest;
import com.taobao.api.request.TrainAgentReturnticketConfirmRequest;
import com.taobao.api.request.TrainAgentSessionGetRequest;
import com.taobao.api.request.TrainAgentseatpriceSetRequest;
import com.taobao.api.response.TmcUserPermitResponse;
import com.taobao.api.response.TrainAgentAutorefundRefundfeeResponse;
import com.taobao.api.response.TrainAgentBookordersGetResponse;
import com.taobao.api.response.TrainAgentBookticketConfirmResponse;
import com.taobao.api.response.TrainAgentChangeAgreeResponse;
import com.taobao.api.response.TrainAgentChangeGetResponse;
import com.taobao.api.response.TrainAgentChangeRefuseResponse;
import com.taobao.api.response.TrainAgentChangeordersGetResponse;
import com.taobao.api.response.TrainAgentExpressSetResponse;
import com.taobao.api.response.TrainAgentHandleticketConfirmResponse;
import com.taobao.api.response.TrainAgentMobileSendsmsResponse;
import com.taobao.api.response.TrainAgentOrderGetResponse;
import com.taobao.api.response.TrainAgentReturnordersGetResponse;
import com.taobao.api.response.TrainAgentReturnticketConfirmResponse;
import com.taobao.api.response.TrainAgentSessionGetResponse;
import com.taobao.api.response.TrainAgentseatpriceSetResponse;

/**
 * 淘宝TCP链接消息解析，出票退票 回调方法 "TaobaoHotelInterfaceUtil_TaobaoAgentid",
 * "Train.properties"
 * 
 * @author Administrator
 *
 */
public class TaobaoHotelInterfaceUtil extends TongchengSupplyMethod {
    Logger logger = Logger.getLogger("TaobaoTrainOrder");

    // //====================================正式START=================================================
    public static String sessionKey = "61012085cc794df214c162323bbf1ebcdc208a261ef5d592508852773";

    public static String url = "http://gw.api.taobao.com/router/rest";

    public static String jiantingurl = "ws://mc.api.taobao.com/";

    // String orderurl;
    public static String appkey = "23112675";

    public static String appSecret = "e5caeb2211b563cc468167887aae5430";

    public static Long agentid = 2508852773L;

    // ====================================正式END=================================================

    // ====================================测试START=================================================
     

//     public static String sessionKey="6102112a2f7b41f9ce6510c4488c134dc9d6707f35cf3a22067074684";
    //    public static String sessionKey = "6101d20802423fc08cc556ed9d5b1785c4d2dacbc84536f2067074684";
//    PUBLIC STATIC STRING SESSIONKEY = "6101A2245D9F5A066863137FB7899E1330A20A71AEF10A62067074684";
//        PUBLIC STATIC STRING URL = "HTTP://GW.API.TBSANDBOX.COM/ROUTER/REST";
//        PUBLIC STATIC STRING JIANTINGURL = "WS://MC.API.TBSANDBOX.COM";
//        PUBLIC STATIC STRING APPKEY = "4272";
//        PUBLIC STATIC STRING APPSECRET = "0EBBCCCFEE18D7AD1AEBC5B135FFA906";
//        public static Long agentid = 3662263834L;
        
//        public static String sessionKey = "61006114f2d540fba81106881cfef468be93cb3b0023d4e2067074684";
//
//        public static String url = "http://gw.api.tbsandbox.com/router/rest";
//
//        public static String jiantingurl = "ws://mc.api.tbsandbox.com";
//
//        public static String appkey = "690047";
//
//        public static String appSecret = "4cc8b1ae51a91e9bf3bdf6bd99277ff7";
//
//        public static Long agentid = Long.valueOf(3662263834L);
    // ====================================测试END=================================================
    public String exceptionS = "";// 异常信息捕获

    static TaobaoHotelInterfaceUtil tbiu;

    public TaobaoHotelInterfaceUtil gettbiu() {
        if (tbiu == null) {
            return new TaobaoHotelInterfaceUtil();
        }

        return tbiu;
    }

    public void settbiu(TaobaoHotelInterfaceUtil tbius) {
        tbiu = tbius;
    }

    public Map taobaoDrawerNotice() {
        try {
            return null;
        }
        catch (Exception e) {
            return null;
        }

    }

    /**
     * 写日志
     * 
     * @param trainorderId
     * @param content
     * @param createuser
     * @param status
     */
    private void createTrainorderrc(Long trainorderId, String content, String createuser, int status) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderId);
            rc.setContent(content);
            rc.setStatus(status);
            rc.setCreateuser(createuser);
            rc.setYwtype(1);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            WriteLog.write("操作记录失败", trainorderId + ":content:" + content);
        }
    }

    /**
     * 根据订单号获取入参
     * 
     * @param orderid
     * @param failmsg
     * @return
     * @time 2015年5月18日 下午5:41:30
     * @author fiend
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map tainOrderid(String orderid, String failmsg) {
        StringBuffer subbuf = new StringBuffer();
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(Long.parseLong(orderid));
        if (failmsg == null || failmsg.equals("")) {
            trainorder = TaobaoTrainInsure.getTaobaoTrainInsure().insuranceSynchronous(trainorder);
        }
        try {
            Map mp = new HashMap();
            mp.put("orderId", trainorder.getId());
            // mp.put("alipayTradeNo", trainorder.getSupplytradeno());
            mp.put("main_order_id", trainorder.getQunarOrdernumber());
            //代购
            if (trainorder.getOrdertype() == 1 || trainorder.getOrdertype() == 0) {
                mp.put("order_type", 0);
            }
            //托管
            else if (trainorder.getOrdertype() == 4) {
                mp.put("order_type", 1);
            }
            else if (trainorder.getOrdertype() == 5) {
                mp.put("order_type", 3);
            }
            if (failmsg == null || failmsg.equals("")) {
                mp.put("status", true);
                mp.put("failMsg", "no");
                try {
                    mp.put("alipaytradeno", trainorder.getSupplytradeno());
                    mp.put("supplyaccount", trainorder.getAutounionpayurlsecond());// 添加支付账户  
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                mp.put("status", false);
                mp.put("failMsg", failmsg);
            }
            // TODO fiend2zhengju 这里看 票里如何拼的数据
            StringBuffer sb = new StringBuffer();
            int num = 0;
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger tp = trainorder.getPassengers().get(i);
                for (int y = 0; y < tp.getTraintickets().size(); y++) {
                    Trainticket tk = tp.getTraintickets().get(y);
                    if (y == 0) {
                        if (trainorder.getOrdertype() == 5) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String costtime = tk.getCosttime();
                            String[] costString = costtime.split(":");
                            long hours = Long.parseLong(costString[0]);
                            long min = Long.parseLong(costString[1]);
                            long costLongtime = (hours * 60 + min) * 60 * 1000;
                            long depLongtime = df.parse(tk.getDeparttime()).getTime();
                            long arriveLongtime = depLongtime + costLongtime;
                            mp.put("arriveDate", df1.format(new Date(arriveLongtime)));
                            mp.put("depDate", df1.format(df.parse(tk.getDeparttime())));
                            mp.put("from_station_name", tk.getDeparture());
                            mp.put("to_station_name", tk.getArrival());
                        }
                        else {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            mp.put("depDate", df.format(df.parse(tk.getDeparttime())));
                        }
                    }

                    num = num + 1;
                    // 火车票订单id，单价，坐席，座位号，车次，乘车人姓名，证件名称，证件号码，保单号，保单价格
                    int x = (int) (tk.getPrice() * 100);
                    sb.append(tk.getInterfaceticketno() + ";");
                    sb.append(x + ";");
                    sb.append(CackBackSuccessseao(tk.getSeattype(), tk.getSeatno()) + ";");
                    if (tk.getSeatno() == null) {
                        sb.append("no" + ";");
                    }
                    else {
                        if ("无座".equals(tk.getSeatno())) {
                            sb.append("无座_" + tk.getCoach() + "车厢;");
                        }
                        else {
                            sb.append(tk.getSeattype() + "_" + tk.getCoach() + "_" + tk.getSeatno().toString() + ";");
                        }
                    }
                    sb.append(tk.getTrainno() + ";");
                    sb.append(tp.getName().toString() + ";");
                    String certTypeValue = tp.getIdtypestr().toString();
                    sb.append(IDnumberType(certTypeValue) + ";");
                    sb.append(tp.getIdnumber() + ";");
                    if (tk.getRealinsureno() == null || "".equals(tk.getRealinsureno())) {
                        sb.append("0;");
                        sb.append("0");
                    }
                    else {
                        sb.append(tk.getRealinsureno() + ";");
                        sb.append((int) (tk.getInsurorigprice() * 100));
                    }
                    if (trainorder.getPassengers().size() > i + 1) {
                        sb.append(",");
                    }

                    String suborderid = tk.getInterfaceticketno();
                    subbuf.append(suborderid);
                    subbuf.append(",");
                }
            }
            String temp = subbuf.toString();
            if (temp.endsWith(",")) {
                temp = temp.substring(0, temp.length() - 1);
            }
            String subOrderId = getSubOrderIdMethod(orderid);
            WriteLog.write("淘宝要的下单失败的乘客的子订单", "DB>>>>>>>>>subOrderId:" + subOrderId + "------------>>ALLSuborderId:"
                    + temp);
            if (!ElongHotelInterfaceUtil.StringIsNull(subOrderId)) {
                mp.put("subOrderId", subOrderId);
            }
            else {
                mp.put("subOrderId", temp);
            }

            mp.put("tickets", sb);

            if (trainorder.getExtnumber() == null) {
                mp.put("ticket12306Id", "no");
            }
            else {
                mp.put("ticket12306Id", trainorder.getExtnumber().toString());
            }
            try {
                if (trainorder.getEnrefundable() == 1) {
                    mp.put("canChange", false);
                }
                else {
                    mp.put("canChange", true);
                }
            }
            catch (Exception e) {
                WriteLog.write("TAINORDERID_CANCHANGE_ERROR", orderid);
                ExceptionUtil.writelogByException("TAINORDERID_CANCHANGE_ERROR", e);
            }
            mp.put("ticketNum", num);// 火车票
            return mp;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.exceptionS = e.toString();
            return null;
        }

    }

    /**
     * 根据订单号获取入参
     * 
     * @param orderid
     * @param failmsg
     * @return
     * @time 2016年1月12日 下午5:41:30
     * @author guozhengju
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map trainOfflineOrderid(String orderid, String issuccess, int failmsg) {
        String sql = "SELECT * FROM TrainOrderOffline WHERE ID=" + orderid;
        List list = getSystemServiceOldDB().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map trainorder = (Map) list.get(0);
            try {
                Map mp = new HashMap();
                mp.put("main_order_id", trainorder.get("OrderNumberOnline").toString());
                String ticketpassengersql = "SELECT P.Name,P.IdType,P.IdNumber,T.sealPrice,T.realSeat,T.Coach,T.SeatNo,T.TrainNo,T.DepartTime,T.TicketType,T.ticketNo,T.subOrderId,P.IdType FROM TrainTicketOffline  T left join TrainPassengerOffline P on T.TrainPid=P.Id WHERE  T.OrderId="
                        + orderid;
                List ticketpassengerlist = getSystemServiceOldDB().findMapResultBySql(ticketpassengersql, null);
                if (issuccess != null && "success".equals(issuccess)) {
                    mp.put("status", true);
                    mp.put("failMsg", "no");
                    StringBuffer sb = new StringBuffer();
                    //
                    if (ticketpassengerlist.size() > 0) {
                        String ticketnos = "";
                        for (int i = 0; i < ticketpassengerlist.size(); i++) {
                            Map mapall = (Map) ticketpassengerlist.get(i);
                            if (i == 0) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                mp.put("depDate", df.format(df.parse(mapall.get("DepartTime").toString())));
                            }
                            int x = (int) (Double.parseDouble(mapall.get("sealPrice").toString()) * 100);
                            sb.append(mapall.get("subOrderId").toString() + ";");
                            sb.append(x + ";");
                            sb.append(callBackSuccessseao(mapall.get("realSeat").toString()) + ";");
                            if (mapall.get("SeatNo").toString() == null) {
                                sb.append("no" + ";");
                            }
                            else {
                                if ("无座".equals(mapall.get("SeatNo").toString())) {
                                    sb.append("无座_" + mapall.get("Coach").toString() + "车厢;");
                                }
                                else {
                                    String tickettype = mapall.get("TicketType").toString();
                                    if ("0".equals(tickettype)) {
                                        tickettype = "1";
                                    }
                                    else if ("1".equals(tickettype)) {
                                        tickettype = "0";
                                    }
                                    sb.append(tickettype + "_" + mapall.get("Coach").toString() + "_"
                                            + mapall.get("SeatNo").toString() + ";");
                                }
                            }
                            sb.append(mapall.get("TrainNo").toString() + ";");
                            sb.append(mapall.get("Name").toString() + ";");
                            String idType = mapall.get("IdType").toString();
                            if ("3".equals(idType)) {
                                idType = "1";
                            }
                            else if ("1".equals(idType)) {
                                idType = "0";
                            }
                            sb.append(idType + ";");
                            sb.append(mapall.get("IdNumber").toString() + ";");
                            sb.append("0;");
                            sb.append("0,");
                            ticketnos += mapall.get("ticketNo").toString() + ";";
                        }
                        mp.put("ticketnum", ticketpassengerlist.size());
                        if (!"".equals(ticketnos)) {
                            mp.put("ticket12306Id", ticketnos.substring(0, ticketnos.length() - 1));// ticketno
                        }
                        else {
                            mp.put("ticket12306Id", "0");// ticketnos
                        }
                    }
                    if (!"".equals(sb)) {
                        // System.out.println("-------------"+sb.substring(0,
                        // sb.length()-2));
                        mp.put("tickets", sb.substring(0, sb.length() - 1));
                        WriteLog.write("淘宝线下出票成功回调tickets", "tickets=" + sb.toString());
                    }
                    else {
                        mp.put("tickets", "0");
                    }
                }
                else {
                    Map mapall = (Map) ticketpassengerlist.get(0);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    mp.put("depDate", df.format(df.parse(mapall.get("DepartTime").toString())));
                    mp.put("ticketnum", ticketpassengerlist.size());
                    mp.put("tickets", "0");
                    mp.put("status", false);
                    mp.put("failMsg", failmsg);
                    mp.put("ticket12306Id", "0");
                }

                WriteLog.write(
                        "ＴＡＯＢＡＯ出票参数",
                        "ticket12306Id=" + mp.get("ticket12306Id").toString() + "--depDate="
                                + mp.get("depDate").toString() + "--failMsg=" + mp.get("failMsg").toString()
                                + "--main_order_id=" + (mp.get("main_order_id").toString()) + "--status=="
                                + mp.get("status").toString() + "--ticketnum=="
                                + Long.parseLong(mp.get("ticketnum").toString()) + "--tickets=="
                                + mp.get("tickets").toString());
                return mp;
            }
            catch (Exception e) {
                e.printStackTrace();
                this.exceptionS = e.toString();
                return null;
            }
        }
        else {
            return null;
        }

    }

    private String getSubOrderIdMethod(String orderid) {
        String subOrderId = "";
        String sql = "select * from TaobaoAddSubOrderId with(nolock) where orderId=" + Long.parseLong(orderid);

        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(list.size() - 1);
            subOrderId = map.get("SubOrderId").toString();
        }
        return subOrderId;

    }

    /**
     * 回调淘宝出票接口获取返回json格式数据
     * 
     * @param sessionKey
     *            web里有
     * @param url
     *            web里有
     * @param appkey
     *            web里有
     * @param appSecret
     *            web里有
     * @param OrderId
     *            订单ID
     * @param AgentId
     *            web里有
     * @param params
     *            this.tainOrderid返回值
     * @return
     * @time 2015年4月1日 下午1:27:34
     * @author liangwei
     */
    public String taobaoDrawer(Map params) {
        int irandom = (int) (Math.random() * 100000);
        try {
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentBookticketConfirmRequest req = new TrainAgentBookticketConfirmRequest();
            TrainAgentBookticketConfirmResponse res;
            // params.get("alipayTradeNo") == null ||
            if (params.get("ticket12306Id") == null || "no".equals(params.get("ticket12306Id").toString())
                    || "".equals(params.get("ticket12306Id").toString())
                    || !Boolean.parseBoolean(params.get("status").toString())) {
                req.setAlipayTradeNo("0");
                req.setTicket12306Id("0");// 12306订单号
            }
            else {
                req.setAlipayTradeNo("0");// params.get("alipayTradeNo").toString()
                try {
                    WriteLog.write("淘宝出票交易流水号", irandom + ":" + params.get("main_order_id").toString() + "--->"
                            + params.get("alipaytradeno") + params.get("subOrderId").toString());
                    if (params.get("alipaytradeno") != null && !"".equals(params.get("alipaytradeno"))
                            && isAlipayno(params.get("alipaytradeno").toString())) {
                        // TODO fiend 2 郭征举 这个如果没有交易号 或者不符合格式 不往下走了，直接返回失败，记录log
                        // ，失败原因是交易号有误
                        req.setAlipayTradeNo(params.get("alipaytradeno").toString());// 交易流水号
                        req.setAlipayAccount(params.get("supplyaccount").toString());
                    }
                    else {
                        createtrainorderrc(1, "淘宝改签交易非法流水号--取消回调", Long.parseLong(params.get("orderId").toString()),
                                1l, 1, "淘宝回调系统");
                        WriteLog.write("淘宝改签交易非法流水号",
                                "订单号:" + params.get("main_order_id").toString() + "--->" + params.get("alipaytradeno"));
                        return "交易号错误";
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                req.setTicket12306Id(params.get("ticket12306Id").toString());
            }
            req.setAgentId(Long.valueOf(agentid));
            req.setDepDate(params.get("depDate").toString());// 发车时间
            req.setFailMsg(params.get("failMsg").toString());// 失败原因
            req.setMainOrderId(Long.parseLong(params.get("main_order_id").toString()));// 淘宝的订单号
            req.setStatus(Boolean.parseBoolean(params.get("status").toString()));// 是否出票成功
            req.setTicketNum(Long.valueOf(Long.parseLong(params.get("ticketNum").toString())));// 这个订单里有几张票
            req.setTickets(params.get("tickets").toString());// 票的详细信息
            req.setCanChange(Boolean.valueOf(params.get("canChange") == null ? "true" : params.get("canChange")
                    .toString()));// 是否可以改签
            // req.setSubOrderId(params.get("subOrderId").toString());
            String ordertype = "1";
            try {
                if (params.containsKey("order_type")) {
                    ordertype = params.get("order_type").toString();
                    req.setOrderType(Long.valueOf(params.get("order_type").toString()));
                }
            }
            catch (Exception e) {
                WriteLog.write("303_TAOBAO_TRAINORDER_CALLBACK_ERROR", irandom + ":"
                        + params.get("main_order_id").toString() + "--->"
                        + net.sf.json.JSONObject.fromObject(req).toString());
                ExceptionUtil.writelogByException("303_TAOBAO_TRAINORDER_CALLBACK_ERROR", e);
            }
            if ("3".equals(ordertype)) {
                req.setArriveDate(params.get("arriveDate").toString());//到达时间
                req.setFromStationName(params.get("from_station_name").toString());//出发站
                req.setToStationName(params.get("to_station_name").toString());//到达站
            }
            // res = client.execute(req);
            WriteLog.write("303_TAOBAO_TRAINORDER_CALLBACK", irandom + ":" + params.get("main_order_id").toString()
                    + "--->" + net.sf.json.JSONObject.fromObject(req).toString());
            res = client.execute(req, sessionKey);
            WriteLog.write("303_TAOBAO_TRAINORDER_CALLBACK", irandom + ":" + params.get("main_order_id").toString()
                    + "--->" + res.getBody());
            if (res.getBody() == null) {
                WriteLog.write("303_TAOBAO_TRAINORDER_CALLBACK", irandom + ":" + params.get("main_order_id").toString()
                        + "--->" + "淘宝回调返回错误：" + res.getErrorCode() + ":" + res.getMsg());
                return this.exceptionS = res.getErrorCode() + ":" + res.getMsg();
            }
            else if (res.getIsSuccess()) {
                return "SUCCESS";
            }
            else {
                WriteLog.write("303_TAOBAO_TRAINORDER_CALLBACK", irandom + ":" + params.get("main_order_id").toString()
                        + "--->" + res.getBody());
                return "error";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("303_TAOBAO_TRAINORDER_CALLBACK", irandom + ":" + "淘宝回调请求异常：" + e.toString());
            this.exceptionS = e.toString();
            return "";
        }

    }

    /**
     *
     * top获取改签订单详情
     * 
     * @param sub_biz_order_id
     * @return
     * @time 2015年4月20日 下午7:12:08
     * @author liangwei
     */
    public String taobaoQuerChangOrder() {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentChangeordersGetRequest req = new TrainAgentChangeordersGetRequest();
        TrainAgentChangeordersGetResponse res;
        req.setAgentId(agentid);
        try {
            res = client.execute(req, sessionKey);
            WriteLog.write("403_TAOBAO_CHANGSBYLIST", "ApplyCount:" + res.getApplyCount());
            WriteLog.write("403_TAOBAO_CHANGSBYLIST", "Msg:" + res.getMsg());
            WriteLog.write("403_TAOBAO_CHANGSBYLIST", "ApplyIds:" + res.getApplyIds());
            if (res.getApplyIds() != null && !"".equals(res.getApplyIds())) {
                for (int i = 0; i < res.getApplyIds().split(",").length; i++) {
                    String changedetail = QuerChangOrderage(Long.valueOf(res.getApplyIds().split(",")[i]));
                    WriteLog.write("403_TAOBAO_CHANGSBYLIST_CHANGEDETAILS", res.getApplyIds().split(",")[i] + "--->"
                            + changedetail);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.exceptionS = e.toString();
        }
        return null;
    }

    /**
     * TOP获取订单详情
     * 
     * @param url
     * @param appkey
     * @param appSecret
     * @param tbOrderId
     * @param agentid
     * @return
     */

    public String taobaoQuerOrder(String tbOrderId) {
        WriteLog.write("taobaoQuerOrder", tbOrderId + ":" + agentid + url + appkey + appSecret + "       sessionKey"
                + sessionKey);
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentOrderGetRequest req = new TrainAgentOrderGetRequest();
        req.setMainOrderId(Long.parseLong(tbOrderId));
        req.setAgentId(agentid);
        TrainAgentOrderGetResponse res;
        try {
            res = client.execute(req, sessionKey);
            WriteLog.write("taobaoQuerOrder", tbOrderId + "msg:" + res.getBody());
            return res.getBody();
        }
        catch (ApiException e) {
            e.printStackTrace();
            this.exceptionS = e.toString();
            WriteLog.write("taobaoQuerOrder", tbOrderId + "异常");
            ExceptionUtil.writelogByException("taobaoQuerOrder", e);
            // return null;
        }
        return null;
    }

    /**
     * TOP获取保证下单接口
     * 
     * @param url
     * @param appkey
     * @param appSecret
     * @param tbOrderId
     * @param agentid
     * @return
     */

    public boolean taobaoHandleOrder(String tbOrderId) {
        try {
            WriteLog.write("保证出票  ", tbOrderId + ":" + agentid + url + appkey + appSecret + "       sessionKey"
                    + sessionKey);
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentHandleticketConfirmRequest req = new TrainAgentHandleticketConfirmRequest();
            req.setMainOrderId(Long.parseLong(tbOrderId));
            req.setTimestamp(System.currentTimeMillis());
            req.setExtendParams("");
            req.setSellerId(agentid);
            TrainAgentHandleticketConfirmResponse res;
            res = client.execute(req, sessionKey);
            WriteLog.write("保证出票  ", tbOrderId + ":msg:" + res.getBody());
            return isHandled(res.getBody());
        }
        catch (ApiException e) {
            e.printStackTrace();
            WriteLog.write("保证出票  ", tbOrderId + ":msg:" + e.getMessage());
            this.exceptionS = e.toString();
        }
        return true;
    }

    /**
     * 解析保证下单返回结果，返回是否保证成功
     * 
     * @param resbody
     * @return
     * @author fiend
     */
    private boolean isHandled(String resbody) {
        try {
            return JSONObject.parseObject(resbody).getJSONObject("train_agent_handleticket_confirm_response")
                    .getBoolean("is_success");
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void TaoBaoShou() {
        try {
            WriteLog.write("淘宝TMC链接词", "授權開始");
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TmcUserPermitRequest req = new TmcUserPermitRequest();
            req.setTopics("taobao_train_TradeNotify");

            TmcUserPermitResponse response = client.execute(req, sessionKey);

            WriteLog.write("淘宝TMC链接词", "授權结束" + response.getBody() + "}}");

        }
        catch (Exception e) {
            WriteLog.write("淘宝TMC链接词", "授權異常" + e.toString() + "}}" + e.getMessage());
        }

    }

    /**
     * 监听淘宝接口
     * 
     * @param sessionKey
     * @param url
     * @param appkey
     * @param appSecret
     * @return
     */
    public void taobaoOuTtick() {
        // getGroup();
        // addGroup("fiend");
        try {
            Date d = new Date();
            System.out.println("淘宝监听启动");
            TmcClient client = new TmcClient(jiantingurl, appkey, appSecret, "default");
            WriteLog.write("淘宝TMC链接词", "jiantingurl" + jiantingurl + "appkey:" + appkey + "appSecret" + appSecret
                    + "淘宝监听启动" + d);
            client.setMessageHandler(new MessageHandler() {
                public void onMessage(Message message, MessageStatus status) {

                    try {
                        WriteLog.write("淘宝TMC链接词", "淘宝TMC获取记录成功" + message.getContent());
                        String exption = tbiu.AnalysisOuTtick(message.getContent());
                        WriteLog.write("淘宝TMC链接词", "处理完成exption" + exption);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        WriteLog.write("淘宝TMC链接词", "处理完成异常情况:" + e.getLocalizedMessage() + ":" + e.toString() + ":"
                                + message.getContent());
                        status.fail();// 回滚

                    }

                }
            });
            try {
                client.connect();
            }
            catch (Exception e) {
                e.printStackTrace();
                WriteLog.write("淘宝TMC链接异常", "错误信息" + e.toString());

            }
        }
        catch (Exception e) {
            System.out.println(9999);
            e.printStackTrace();
        }
    }

    private String contents = "";// 防止连点重复

    /**
     * 解析taobaoOuTtick获取到的json格式数据返回淘宝orderid
     * 
     * @param content
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unused")
    public String AnalysisOuTtick(String content) {
        WriteLog.write("TaobaoHotelInterfaceUtil_AnalysisOuTtick", content);
        try {
            boolean resultbool = false;//收单放单
            if (content != null && !content.trim().equals("")) {
                if (content.equals(contents)) {
                    return "";
                }
                else {
                    contents = content;
                }
                JSONObject json = JSONObject.parseObject(content);
                if (json.get("msg_type") == null) {
                    return "";
                }
                try {
                    boolean decideToShouDan = getdecideToShouDan(json);// 是否需要收单
                    if (decideToShouDan) {
                        String shoudan = json.containsKey("shoudan") ? json.getString("shoudan") : "";
                        if (!"true".equals(shoudan)) {
                            // 收单
                            JSONObject jsonObject = JSONObject.parseObject(content);
                            jsonObject.put("shoudan", "true");
                            String msgorder = jsonObject.toJSONString();
                            String msgtype = json.getString("msg_type");
                            long type = 0;
                            String reqtoken = "";
                            if (msgtype.equals("1")) {
                                type = 1;
                                reqtoken = json.getString("main_biz_order_id");
                            }
                            else if (msgtype.equals("4")) {
                                reqtoken = json.getString("sub_biz_order_id");
                                type = 4;
                            }
                            resultbool = true;
                            //                            saveOrderInfoShouDan(msgorder, type, reqtoken);
                            //                            return "";
                        }
                    }
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (json.get("msg_type").equals("1")) { // 出票
                    String taobao_order_no = json.getString("main_biz_order_id");
                    // if (taobaoHandleOrder(taobao_order_no)) {
                    String ordermes = this.taobaoQuerOrder(taobao_order_no);
                    JSONObject orderme = JSONObject.parseObject(ordermes);// 获取订单详情
                    WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":orderme--->" + orderme);
                    String sql = "SELECT ID,ordertype,C_ORDERSTATUS,C_CREATETIME FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='"
                            + taobao_order_no + "'";
                    List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":list.size=" + list.size());
                    if (list.size() < 1) {
                        // if (!isCanCreate(orderme)) {
                        // this.exceptionS = "订单重复进入";
                        // return null;
                        // }
                        boolean isNeedStandingSeat = false;
                        String standingSeatprice = "";
                        WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":orderme.size()=" + orderme.size());
                        if (orderme != null) {
                            JSONObject orderDetails = new JSONObject();
                            // orderDetails =
                            // orderDetails.parseObject(orderme);
                            orderDetails = orderme.getJSONObject("train_agent_order_get_response");
                            String ttp_order_id = orderDetails.getString("ttp_order_id");
                            int order_type = orderDetails.getIntValue("order_type");
                            WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":orderDetails--->" + orderDetails
                                    + ";orderDetails.get(\"is_success\").toString()="
                                    + orderDetails.get("is_success").toString());
                            if (Boolean.parseBoolean(orderDetails.get("is_success").toString()) == true)// 结果成功返回
                            {
                                JSONObject jb = new JSONObject();
                                JSONObject jb2 = new JSONObject();
                                jb = orderDetails;// 获取json投标
                                jb2 = jb.getJSONObject("tickets");// 获取票字段
                                Trainorder trainorder = new Trainorder();// 创建订单
                                JSONArray TrainTickets = new JSONArray();
                                TrainTickets = jb2.getJSONArray("to_agent_ticket_info");// 获取票的字段转换数组

                                List<Trainpassenger> trainplist = new ArrayList<Trainpassenger>();
                                WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":TrainTickets.size()="
                                        + TrainTickets.size());
                                for (int i = 0; i < TrainTickets.size(); i++) {
                                    List<Trainticket> Trainticklist = new ArrayList<Trainticket>();
                                    Trainticket trainticket = new Trainticket();// 票
                                    Trainpassenger ts = new Trainpassenger();// 客

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");

                                    Date to = df.parse(TrainTickets.getJSONObject(i).getString("to_time").toString());
                                    Date from = df.parse(TrainTickets.getJSONObject(i).getString("from_time")
                                            .toString());
                                    long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
                                    long nh = 1000 * 60 * 60;// 一小时的毫秒数
                                    long nm = 1000 * 60;// 一分钟的毫秒数
                                    long ls = to.getTime() - from.getTime();
                                    String riqi = "";

                                    long hour = ls / nh;// 计算差多少小时
                                    long min = ls % nh / nm;// 计算差多少分钟
                                    riqi = hour + ":" + min;

                                    trainticket.setStatus(Trainticket.WAITISSUE);
                                    trainticket.setCosttime(riqi);
                                    trainticket.setArrivaltime(dfs.format(to));// 新增

                                    trainticket.setDeparttime(df.format(from));
                                    trainticket.setDeparture(TrainTickets.getJSONObject(i).getString("from_station"));
                                    trainticket.setArrival(TrainTickets.getJSONObject(i).getString("to_station"));// 到达

                                    trainticket.setPrice(Float.parseFloat(TrainTickets.getJSONObject(i).getString(
                                            "ticket_price")) / 100);
                                    trainticket.setPayprice(Float.parseFloat(TrainTickets.getJSONObject(i).getString(
                                            "ticket_price")) / 100);
                                    // 淘寶票的id
                                    trainticket.setInterfaceticketno(TrainTickets.getJSONObject(i).getString(
                                            "sub_order_id"));
                                    trainticket.setTrainno(TrainTickets.getJSONObject(i).getString("train_num"));
                                    trainticket.setSeattype(getseattypes(TrainTickets.getJSONObject(i)
                                            .getString("seat"), TrainTickets.getJSONObject(i).getString("train_num")));
                                    WriteLog.write("301_TAOBAO_ORDER", taobao_order_no
                                            + ":TrainTickets.getJSONObject(i).get(\"seat\")--->"
                                            + TrainTickets.getJSONObject(i).get("seat"));
                                    if (TrainTickets.getJSONObject(i).get("seat") != null
                                            && "17".equals(TrainTickets.getJSONObject(i).getString("seat"))) {
                                        isNeedStandingSeat = true;
                                        standingSeatprice = trainticket.getPrice() + "";
                                    }
                                    trainticket.setTickettype(changeTicketTypeTaobao2DB(TrainTickets.getJSONObject(i)
                                            .getString("passenger_type")));
                                    trainticket.setInsurorigprice(Float.parseFloat(TrainTickets.getJSONObject(i)
                                            .getString("insurance_price")) / 100);
                                    Trainpassenger trainpassenger = new Trainpassenger();
                                    trainpassenger.setBirthday(TrainTickets.getJSONObject(i).getString("birthday"));
                                    trainpassenger.setIdnumber(TrainTickets.getJSONObject(i).getString(
                                            "certificate_num"));
                                    String certTypeValue = TrainTickets.getJSONObject(i).getString("certificate_type");
                                    WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ";certTypeValue-->"
                                            + certTypeValue);
                                    if ("0".equals(certTypeValue)) {
                                        certTypeValue = "1";
                                    }
                                    else if ("1".equals(certTypeValue)) {
                                        certTypeValue = "3";
                                    }
                                    trainpassenger.setIdtype(Integer.parseInt(certTypeValue));
                                    trainpassenger.setName(TrainTickets.getJSONObject(i).getString("passenger_name"));
                                    Trainticklist.add(trainticket);
                                    trainpassenger.setTraintickets(Trainticklist);
                                    try {
                                        WriteLog.write("301_TAOBAO_ORDER", taobao_order_no
                                                + ";trainticket.getTickettype()-->" + trainticket.getTickettype());
                                        // 解析淘宝学生票信息
                                        if (3 == trainticket.getTickettype()) {
                                            JSONObject jostudentinfo = TrainTickets.getJSONObject(i).getJSONObject(
                                                    "student_info");
                                            List<TrainStudentInfo> trainstudentinfos = new ArrayList<TrainStudentInfo>();
                                            TrainStudentInfo trainstudentinfo = new TrainStudentInfo();
                                            trainstudentinfo.setClasses(jostudentinfo.getString("classes") == null ? ""
                                                    : jostudentinfo.getString("classes"));
                                            trainstudentinfo
                                                    .setDepartment(jostudentinfo.getString("depart_ment") == null ? ""
                                                            : jostudentinfo.getString("depart_ment"));
                                            trainstudentinfo.setEductionalsystem(jostudentinfo
                                                    .getString("eductional_system") == null ? "" : jostudentinfo
                                                    .getString("eductional_system"));
                                            trainstudentinfo
                                                    .setEntranceyear(jostudentinfo.getString("entrance_year") == null ? ""
                                                            : jostudentinfo.getString("entrance_year"));
                                            trainstudentinfo
                                                    .setFromcity(jostudentinfo.getString("from_city") == null ? ""
                                                            : jostudentinfo.getString("from_city"));
                                            trainstudentinfo
                                                    .setSchoolname(jostudentinfo.getString("school_name") == null ? ""
                                                            : jostudentinfo.getString("school_name"));
                                            trainstudentinfo.setSchoolprovince(jostudentinfo
                                                    .getString("school_province") == null ? "" : jostudentinfo
                                                    .getString("school_province"));
                                            trainstudentinfo
                                                    .setStudentcard(jostudentinfo.getString("card") == null ? ""
                                                            : jostudentinfo.getString("card"));
                                            trainstudentinfo
                                                    .setStudentno(jostudentinfo.getString("student_no") == null ? ""
                                                            : jostudentinfo.getString("student_no"));
                                            trainstudentinfo.setTocity(jostudentinfo.getString("to_city") == null ? ""
                                                    : jostudentinfo.getString("to_city"));
                                            trainstudentinfo.setSchoolnamecode("");
                                            trainstudentinfo.setSchoolprovincecode("");
                                            trainstudentinfo.setFromcitycode("");
                                            trainstudentinfo.setTocitycode("");
                                            trainstudentinfo.setArg1(0l);
                                            trainstudentinfo.setArg2("");
                                            trainstudentinfo.setArg3(0l);
                                            trainstudentinfos.add(trainstudentinfo);
                                            trainpassenger.setTrainstudentinfos(trainstudentinfos);
                                        }
                                    }
                                    catch (Exception e) {
                                        ExceptionUtil.writelogByException("STUDENTS_ERROR", e);
                                        WriteLog.write("301_TAOBAO_ORDER", taobao_order_no
                                                + ";TrainTickets.getJSONObject(i).getJSONObject(\"student_info\")-->"
                                                + TrainTickets.getJSONObject(i).getJSONObject("student_info"));
                                    }
                                    trainplist.add(trainpassenger);

                                }
                                trainorder.setPassengers(trainplist);
                                trainorder.setOrderprice(Float.parseFloat(jb.get("total_price").toString()) / 100);
                                trainorder.setContacttel(jb.getString("telephone"));
                                trainorder.setAgentcontact("1");
                                trainorder.setAgentcontacttel("");
                                trainorder.setTaobaosendid(json.getString("user_id"));// 卖家ID
                                trainorder.setInsureadreess(jb.getString("address"));// 保险邮寄地址
                                trainorder.setContactuser(jb.getString("relation_name"));
                                trainorder.setQunarOrdernumber(jb.getString("main_order_id"));
                                trainorder.setOrderstatus(Trainorder.WAITPAY);
                                trainorder.setState12306(Trainorder.WAITORDER);
                                trainorder.setAgentid(getTaobaoAgetid());
                                trainorder.setInterfacetype(TrainInterfaceMethod.TAOBAO);
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                trainorder.setCreatetime(ts);
                                trainorder.setOrdertimeout(ts.valueOf(jb.getString("latest_issue_time").toString()));
                                trainorder.setCreateuser("淘宝网");
                                WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ";order_type=" + order_type);
                                if (order_type == 1) {
                                    trainorder.setOrdertype(4);
                                }
                                else if (order_type == 2) {// 线下票
                                    trainorder.setOrdertype(2);
                                }
                                else {
                                    trainorder.setOrdertype(1);
                                }
                                WriteLog.write("TaobaoHotelInterfaceUtil_AnalysisOuTtick", "jb=" + jb
                                        + ";ttp_order_id=" + ttp_order_id + ";isNeedStandingSeat=" + isNeedStandingSeat
                                        + ";standingSeatprice=" + standingSeatprice + ";taobao_order_no="
                                        + taobao_order_no);
                                newhandleTaoBaoOrder(trainorder, jb, ttp_order_id, order_type, isNeedStandingSeat,
                                        standingSeatprice, taobao_order_no, resultbool);
                                /*
                                 * trainorder =
                                 * Server.getInstance().getTrainService
                                 * ().createTrainorder(trainorder);
                                 * createTaoBaoTrainOrderNumberChange
                                 * (trainorder.getId(),
                                 * jb.getString("main_order_id"), ttp_order_id);
                                 * if (order_type == 1) {
                                 * createTaoBaoSession(trainorder); } if
                                 * (isNeedStandingSeat) {
                                 * createTrainOrderExtSeat(trainorder.getId(),
                                 * "[{\"0\":" + standingSeatprice + "}]"); }
                                 * WriteLog.write( "301_TAOBAO_ORDER",
                                 * taobao_order_no + ":" +
                                 * trainorder.getQunarOrdernumber() + ":" +
                                 * trainorder.getId()); // if (getNowTime(new
                                 * Date())) { //TODO fiend2zhengju 这里原本是发订单MQ
                                 * 如果是线下票 走征举逻辑
                                 * activeMQroordering(trainorder.getId()); //
                                 * saveTrainOrder(TrainPassenger, //
                                 * TrainTickets); // 代理商出票保存数据和回调 // }
                                 */}
                            else // 结果是不返回
                            {
                                WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":订单详情没有成功返回");

                                this.exceptionS = "淘宝订单详情查询为false";
                            }
                        }
                        else {
                            this.exceptionS = "淘宝订单详情查询为空误";
                            WriteLog.write("301_TAOBAO_ORDER", taobao_order_no
                                    + ":数据无结果exceptionS：淘宝订单详情查询为空误。orderme:" + orderme + ":");
                        }
                    }
                    else {
                        if (!savePushOrder(orderme, list, taobao_order_no)) {
                            this.exceptionS = "淘宝出票订单号重复";
                            WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":淘宝出票订单号重复:list.size:" + list.size()
                                    + ":taobao_order_no" + taobao_order_no);
                        }
                    }
                    // }
                    // else {
                    // this.exceptionS = "淘宝出票订单保证下单失败";
                    // WriteLog.write("301_TAOBAO_ORDER", taobao_order_no +
                    // ":淘宝出票订单保证下单失败:" + content);
                    // }
                }
                else if (json.get("msg_type").equals("2"))// 退款
                {
                    try {
                        WriteLog.write("淘宝退款订单详情false", "淘宝不支持退款:" + json.toJSONString());
                    }
                    catch (Exception e) {
                        WriteLog.write("淘宝退款订单详情false", "淘宝不支持退款:ERROE");
                        e.printStackTrace();
                    }
                    this.exceptionS = "msg_type=2";
                }
                else if (json.get("msg_type").equals("3") || json.get("msg_type").equals("6"))// 退票
                {
                    WriteLog.write("201_TAOBAO_REFUND", "msg_type:" + json.get("main_biz_order_id"));

                    String subminid = json.get("sub_biz_order_id").toString();
                    String orderme = this.taobaoQuerOrder(json.get("main_biz_order_id").toString());

                    JSONObject orderDetails = new JSONObject();
                    orderDetails = orderDetails.parseObject(orderme);
                    orderDetails = orderDetails.getJSONObject("train_agent_order_get_response");
                    String taobao_order_no = orderDetails.getString("main_order_id");
                    //TODO FIEND TMS3
                    JSONObject isTomas3JsonObject = isTomas3(orderDetails);
                    if (isTomas3JsonObject.containsKey("isTomas3") && isTomas3JsonObject.getBooleanValue("isTomas3")) {
                        insertTomas3(taobao_order_no, isTomas3JsonObject.getString("tradeNo"), subminid,
                                json.getString("user_id"));
                        return "";
                    }
                    if (orderDetails != null) {
                        String sql = "SELECT ID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='" + taobao_order_no + "'";
                        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                        if (list.size() == 1) // 查询订单，如果订单数小于大于核实数据库订单
                        {
                            Map map = (Map) list.get(0);
                            long orderid = Long.valueOf(map.get("ID").toString());
                            Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderid);
                            JSONObject ticke = new JSONObject();
                            JSONArray tickes = new JSONArray();
                            ticke = orderDetails.getJSONObject("tickets");
                            tickes = ticke.getJSONArray("to_agent_ticket_info"); // 获取票字段

                            JSONObject jbs = new JSONObject();// 要传入的订单信息
                            JSONArray jbas = new JSONArray();// 要传入的票组

                            jbs.put("orderid", orderDetails.get("main_order_id"));// 淘宝主ID
                            jbs.put("transactionid", order.getOrdernumber());// 本地id
                            jbs.put("ordernumber", order// 12306订单
                                    .getExtnumber());
                            jbs.put("reqtoken", orderDetails.get("main_order_id"));

                            for (int i = 0; i < order.getPassengers().size(); i++)// 循环订单乘客
                            {
                                // 循环订单乘客里的票
                                for (int x = 0; x < order.getPassengers().get(i).getTraintickets().size(); x++) {

                                    for (int y = 0; y < tickes.size(); y++)// 循环淘宝乘客里的票
                                    {
                                        int ticketstatusdb = order.getPassengers().get(i).getTraintickets().get(x)
                                                .getStatus();
                                        if (order.getPassengers().get(i).getTraintickets().get(x)
                                                .getInterfaceticketno().equals(subminid)
                                                && tickes.getJSONObject(y).getString("sub_order_id").equals(subminid)
                                                && ((ticketstatusdb == 3) || (ticketstatusdb == 17) || (ticketstatusdb == 7))) {
                                            JSONObject onticke = new JSONObject();
                                            if (order.getPassengers().get(i).getTraintickets().get(x).getStatus() == 17) {
                                                onticke.put("ticket_no", order.getPassengers().get(i).getTraintickets()
                                                        .get(x).getTcticketno());
                                            }
                                            else {
                                                onticke.put("ticket_no", order.getPassengers().get(i).getTraintickets()
                                                        .get(x).getTicketno());
                                            }
                                            onticke.put("passengername",
                                                    tickes.getJSONObject(y).getString("passenger_name"));
                                            onticke.put("sub_order_id",
                                                    tickes.getJSONObject(y).getString("sub_order_id"));
                                            onticke.put("passportseno",
                                                    tickes.getJSONObject(y).getString("certificate_num"));
                                            if (json.get("msg_type").equals("6")) {
                                                // TODO gzj 这里加淘宝新加的参数
                                                onticke.put("remark", 0);
                                            }
                                            else {
                                                onticke.put("remark", tickes.getJSONObject(y).getString(""));
                                            }
                                            onticke.put("passporttypeseid", order.getPassengers().get(i)
                                                    .getTraintickets().get(x).getSeatno());

                                            jbas.add(onticke);
                                        }
                                    }

                                }
                            }

                            if (jbas.size() > 0) {
                                jbs.put("tickets", jbas);// 退票的字段拼接完成
                                int r1 = new Random().nextInt(10000000);

                                String result = new TongChengTrainReturnTicket().returnticket(jbs, r1);
                                if (result != null && result.contains("距离开车时间太近无法退票")) {
                                    for (int i = 0; i < jbs.getJSONArray("tickets").size(); i++) {
                                        JSONObject jsonobject = new JSONObject();
                                        jsonobject.put("agree_return", false);
                                        jsonobject.put("main_order_id", jbs.getString("orderid"));
                                        jsonobject.put("mainBizOrderId", jbs.getString("orderid"));
                                        jsonobject.put("refuse_return_reason", "2");
                                        jsonobject.put("refund_fee", "0");
                                        jsonobject.put("sub_biz_order_id", jbs.getJSONArray("tickets").getJSONObject(i)
                                                .getString("sub_order_id"));
                                        jsonobject.put("buyerid", json.getString("user_id"));
                                        WriteLog.write("201_TAOBAO_REFUND_回调无法退票", jbs.getJSONArray("tickets")
                                                .getJSONObject(i).getString("sub_order_id")
                                                + "@" + jsonobject.toJSONString());
                                        String taobao_result = taobaoDrawerNotice(jsonobject);
                                        WriteLog.write("201_TAOBAO_REFUND_回调无法退票", jbs.getJSONArray("tickets")
                                                .getJSONObject(i).getString("sub_order_id")
                                                + "@" + taobao_result);
                                        boolean isTaobaoRefundSuccess = false;
                                        try {
                                            isTaobaoRefundSuccess = JSONObject.parseObject(taobao_result)
                                                    .getJSONObject("train_agent_returnticket_confirm_response")
                                                    .getBooleanValue("is_success");
                                        }
                                        catch (Exception e) {
                                            isTaobaoRefundSuccess = false;
                                            ExceptionUtil.writelogByException("ISTAOBAOREFUNDSUCCESS_ERROR", e);
                                        }
                                        if (!isTaobaoRefundSuccess) {
                                            WriteLog.write("201_TAOBAO_REFUND_强制修改票状态",
                                                    "orderid" + jbs.getString("orderid") + "@@sub_biz_order_id"
                                                            + jsonobject.getString("sub_biz_order_id"));
                                            taobaoTuipiaoByTimeout(jbs.getString("orderid"),
                                                    jsonobject.getString("sub_biz_order_id"));
                                        }
                                    }
                                }
                                WriteLog.write("201_TAOBAO_REFUND", json.get("main_biz_order_id")
                                        + ":退票流程结束返回result =:" + result);
                            }
                            else {
                                this.exceptionS = "找不到退票票信息";
                                WriteLog.write("201_TAOBAO_REFUND", json.get("main_biz_order_id")
                                        + ":找不到退票订单 jbas.size() =:" + jbas.size() + "信息" + jbs.toJSONString());
                                // 找不到退票的信息
                            }

                        }
                        else {
                            this.exceptionS = "找不到退票订单";
                            WriteLog.write("201_TAOBAO_REFUND", json.get("main_biz_order_id") + ":找不到退票订单list.size="
                                    + list.size());
                        }
                    }
                    else// 查询异常
                    {
                        WriteLog.write("201_TAOBAO_REFUND", json.get("main_biz_order_id") + ":淘宝退票订单详情为空orderme"
                                + orderme);
                        this.exceptionS = "退票没有对应的订单";
                    }

                }
                else if (json.get("msg_type").equals("4"))// 改签
                {
                    WriteLog.write("101_TAOBAO_CHANGE", "进入改签--->" + json.get("sub_biz_order_id"));
                    // String orderme = this.taobaoQuerChangOrder();//获取订单详情list
                    // WriteLog.write("淘宝改签", "订单详情获取结束--->orderme " + orderme);
                    if (json != null) {

                        // order =
                        // orderDetails.getJSONObject("train_agent_changeorders_get_response");
                        TaobaoOrderChanginId(Long.parseLong(json.get("sub_biz_order_id").toString()));
                        WriteLog.write("101_TAOBAO_CHANGE", "改签信息处理完成--->" + json.get("sub_biz_order_id"));

                    }
                    else// 查询异常
                    {
                        WriteLog.write("101_TAOBAO_CHANGE", "订单数组详情获取空--->");
                        this.exceptionS = "没有对应的订单";
                    }

                }
                else if (json.get("msg_type").equals("tri_short_message")) {// 获取手机小号消息
                    // 消息
                    String msg_content = json.getString("msg_content");
                    // 手机号
                    String mobile_number = json.getString("mobile_number");
                    WriteLog.write("阿里小号代理对接数据", "msg_content:" + msg_content + ":mobile_number:" + mobile_number);
                    String sql = "INSERT INTO [TaobaoMobileMsgContent]([Content],[MobileNumber],[Remark])VALUES('"
                            + msg_content + "','" + mobile_number + "','')";
                    WriteLog.write("阿里小号代理对接数据", "sql:" + sql);
                    try {
                        String systemdburlString = PropertyUtil.getValue("TongCheng_Service_Url",
                                "train.tongcheng.properties");
                        WriteLog.write("阿里小号代理对接数据", "systemdburlString:" + systemdburlString);
                        getTrainServiceOldDB(systemdburlString).findMapResultBySql(sql, null);
                    }
                    catch (Exception e) {
                        ExceptionUtil.writelogByException("阿里小号代理对接数据_Err", e);
                    }

                }
                else if (json.get("msg_type").equals("5")) {// 退款
                    WriteLog.write("105_TAOBAO_RefundPrice", "进入退款--->" + json.toString());
                    if (json != null) {
                        json.put("SellerId", agentid);
                        new TaobaoRefund().operate(json);
                    }
                    else// 查询异常
                    {
                        WriteLog.write("105_TAOBAO_RefundPrice", "订单数组详情获取空--->");
                        this.exceptionS = "没有对应的订单";
                    }
                }
            }
        }
        catch (Exception e) {
            Date dt = new Date();
            e.printStackTrace();
            ExceptionUtil.writelogByException("淘宝流程异常紧急需要处理", e);
            WriteLog.write("淘宝流程异常紧急需要处理" + dt, e.toString() + "   " + e.getMessage() + "  " + e.hashCode());
            this.exceptionS = e.toString();
        }
        return null;

    }

    /**
     * @TODO taobao 二次分单
     * <p>
     * @param orderme
     * @param list
     * @param taobao_order_no
     * @return
     * <p>
     * @time:2016年5月6日  上午11:17:05
     * <p>
     * @author  fengfh
     */
    private boolean savePushOrder(JSONObject orderme, List list, String taobao_order_no) {
        String log = "501_taobao_二次分单";
        boolean isOrderTypeChange = false;
        try {
            if (orderme != null) {
                JSONObject orderDetails = new JSONObject();
                orderDetails = orderme.getJSONObject("train_agent_order_get_response");
                int order_type = orderDetails.getIntValue("order_type"); //当前推送的类型
                if (order_type == 0) {
                    order_type = 1;
                }
                else if (order_type == 1) {
                    order_type = 4;
                }
                Map map = (Map) list.get(0);
                int ordertype = (Integer) map.get("ordertype");//订单类型
                int orderStatus = (Integer) map.get("C_ORDERSTATUS");//訂單狀態
                long orderId = Long.valueOf(map.get("ID").toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String orderTimeOut = orderDetails.get("latest_issue_time").toString();
                String createTime = map.get("C_CREATETIME").toString().substring(0, 10);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String todayDate = format.format(new Date());
                if (order_type != ordertype) {
                    WriteLog.write(log, orderId + "--->order_type--->" + order_type + ";ordertype--->" + ordertype);
                    WriteLog.write(log, orderId + "--->list.size()--->" + list.size() + ";orderStatus--->"
                            + orderStatus + ";todayDate--->" + todayDate + ";createTime--->" + createTime);
                    if (list.size() == 1 && orderStatus == 8 && todayDate.equals(createTime)) {
                        String queryProc = "exec [dbo].[trainOrder_queryFenDan] @orderNumber='" + taobao_order_no + "'";
                        List resultList = null;
                        try {
                            resultList = Server.getInstance().getSystemService().findMapResultBySql(queryProc, null);
                        }
                        catch (Exception e) {
                            WriteLog.write(log + "_Exception", "查询是否存在订单号");
                            ExceptionUtil.writelogByException(log + "_exception", e);
                        }
                        //如果存在就跳过
                        if (resultList.size() < 1) {
                            WriteLog.write("start_" + log, "需要修改订单");
                            int excuteInsertRsInt = 0;
                            String insertProc = "exec [dbo].[trainOrder_insertFenDan] @orderNumber='" + taobao_order_no
                                    + "'";
                            try {
                                excuteInsertRsInt = Server.getInstance().getSystemService().excuteGiftBySql(insertProc);
                            }
                            catch (Exception e) {
                                WriteLog.write(log + "_Exception", "插入订单号");
                                ExceptionUtil.writelogByException(log + "_exception", e);
                            }
                            int excuteSaveRsInt = 0;
                            if (!(excuteInsertRsInt > 0)) {
                                WriteLog.write("fail_" + log, "执行存单sql");
                            }
                            else {
                                String updateProc = "exec [dbo].[trainOrder_partPropertyInit] @orderNumber='"
                                        + taobao_order_no + "',@ordertype=" + order_type + ",@ORDERTIMEOUT='"
                                        + orderTimeOut + "' ";
                                WriteLog.write(log, orderId + "--->" + updateProc);
                                try {
                                    excuteSaveRsInt = Server.getInstance().getSystemService()
                                            .excuteGiftBySql(updateProc);
                                }
                                catch (Exception e) {
                                    WriteLog.write(log + "_Exception", orderId + "--->" + "初始化订单信息异常");
                                    ExceptionUtil.writelogByException(log + "_Exception", e);
                                }
                            }
                            if (excuteSaveRsInt > 0) {
                                isOrderTypeChange = true;
                                createTrainorderrc(orderId, "二次分单", "淘宝二次分单", 1);
                                activeMQroordering(orderId);
                            }
                            else {
                                WriteLog.write(log + "_ERROR", orderId + "--->" + "初始化订单信息失败");
                            }
                        }
                    }
                    else {
                        WriteLog.write(log + "_error", taobao_order_no + ":orderStatus=" + orderStatus + ";todayDate="
                                + todayDate + ";createTime=" + createTime);
                    }
                }
            }
        }
        catch (Exception e) {
            WriteLog.write(log + "_EXCEPTION", orderme == null ? "" : orderme.toString());
            ExceptionUtil.writelogByException(log + "_EXCEPTION", e);
        }
        return isOrderTypeChange;
    }

    /**
     * 拉单时判断系统是否收单
     * 
     * @return
     * @time 2015年11月25日 上午12:12:14
     * @author fiend
     */
    private boolean getdecideToShouDanByOrderList() {
        boolean decideToShouDan = false;
        // 如果收单状态满足 ，非收单时间 收单标识开始
        // 出票改签夜间单处理
        String shouDanflag = getShouDanflag();
        if (!getNowTime(new Date()) || "1".equals(shouDanflag)) {
            decideToShouDan = true;
        }
        try {
            System.out.println(TimeUtil.longToString(System.currentTimeMillis(), TimeUtil.yyyyMMddHHmmssSSS)
                    + "=shouDanflag:" + shouDanflag + ":" + ":decideToShouDan:" + decideToShouDan);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return decideToShouDan;
    }

    /**
     * 
     * @return
     * @time 2015年11月25日 上午12:12:14
     * @author chendong
     * @param json
     */
    private boolean getdecideToShouDan(JSONObject json) {
        boolean decideToShouDan = false;
        // 出票或者改签
        boolean orderstateflag = json.get("msg_type").equals("4") || json.get("msg_type").equals("1");
        // 如果收单状态满足 ，非收单时间 收单标识开始
        // 出票改签夜间单处理
        String shouDanflag = getShouDanflag();
        // String shouDanAllflag = SystemPropetyUtil.getValue("shouDanAllflag");
        if (orderstateflag && (!getNowTime(new Date()) || ("1".equals(shouDanflag)))) {
            decideToShouDan = true;
        }
        try {
            System.out.println(TimeUtil.longToString(System.currentTimeMillis(), TimeUtil.yyyyMMddHHmmssSSS)
                    + "=shouDanflag:" + shouDanflag + ":" + ":decideToShouDan:" + decideToShouDan);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return decideToShouDan;
    }

    /**
     * 获取是否收单标示
     * 
     * @return
     * @time 2015年11月23日 上午10:11:30
     * @author chendong
     */
    private String getShouDanflag() {
        // String shouDanflag = SystemPropetyUtil.getValue("shouDanflag");//老方法
        String shouDanflag = "0";
        // String shouDanflag = SystemPropetyUtil.getValue("shouDanflag");
        String AcquiringflagMsg = "";
        try {
            // String sysflag = getSysconfigString("sysflag");//系统标识 系统标识 1 同程 2
            // 空铁
            String quiringOrderkey = "quiringOrderkey_2_57";// 空铁_淘宝的agentid=57
            if ("".equals(OcsMethod.getInstance().get(quiringOrderkey))) {
            }
            else {
                AcquiringflagMsg = OcsMethod.getInstance().get(quiringOrderkey);// 系统整体收单标识
                if ("success".equals(AcquiringflagMsg)) {
                    shouDanflag = "1";
                }
            }
        }
        catch (Exception e) {
            logger.error("allAcquiringflag:" + e.fillInStackTrace());
        }
        return shouDanflag;
    }

    /**
     * 淘宝退票队列获取退票进行处理
     * 
     * @param subminid
     * @author fiend
     */
    private void taobaoTuipiao(String subminid, int IsApplyTicket) {
        String logname = "203_TAOBAO_REFUNDBYLIST";
        if (IsApplyTicket == 2) {
            logname = "203_TAOBAO_REFUNDBYLIST_OFFLINE";
        }
        WriteLog.write(logname, subminid);
        //如果是线下 C_APPLYTICKETFLAG = 2
        String sql = "";
        if (IsApplyTicket == 1) {
            sql = "UPDATE T_TRAINTICKET SET C_STATUS=" + Trainticket.APPLYTREFUND + ",C_ISAPPLYTICKET=" + IsApplyTicket
                    + ",C_INSURENO='" + subminid + "',C_REFUNDREQUESTTIME = '"
                    + ElongHotelInterfaceUtil.getCurrentTime() + "' WHERE C_INTERFACETICKETNO ='" + subminid
                    + "' AND C_STATUS IN (" + Trainticket.ISSUED + "," + Trainticket.NONREFUNDABLE + ","
                    + Trainticket.FINISHCHANGE + "," + Trainticket.CANTCHANGE + ")";
        }
        else if (IsApplyTicket == 2) {
            sql = "UPDATE T_TRAINTICKET SET C_STATUS=" + Trainticket.APPLYTREFUND + ",C_ISAPPLYTICKET=" + IsApplyTicket
                    + ",C_INSURENO='" + subminid + "',C_REFUNDREQUESTTIME = '"
                    + ElongHotelInterfaceUtil.getCurrentTime() + "',C_APPLYTICKETFLAG = 1, C_INTERFACETYPE = "
                    + TrainInterfaceMethod.TAOBAO + " WHERE C_INTERFACETICKETNO ='" + subminid + "' AND C_STATUS IN ("
                    + Trainticket.ISSUED + "," + Trainticket.NONREFUNDABLE + "," + Trainticket.FINISHCHANGE + ","
                    + Trainticket.CANTCHANGE + ")";
        }

        try {
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            WriteLog.write(logname, subminid + "---true---" + sql);
        }
        catch (Exception e) {
            WriteLog.write(logname, subminid + "---error---" + sql);
            ExceptionUtil.writelogByException(logname + "_ERROR", e);
            e.printStackTrace();
        }
    }

    /**
     * 淘宝退票队列获取退票进行处理
     * 
     * @param subminid
     * @author fiend
     */
    private void taobaoTuipiaoByTimeout(String subminid, String interfaceticketno) {
        WriteLog.write("203_TAOBAO_REFUNDBYLIST", subminid);
        String sql = "UPDATE T_TRAINTICKET SET C_STATUS=" + Trainticket.APPLYTREFUND
                + ",C_ISAPPLYTICKET=1,C_INSURENO='" + subminid + "',C_REFUNDREQUESTTIME = '"
                + ElongHotelInterfaceUtil.getCurrentTime() + "' WHERE C_INTERFACETICKETNO ='" + interfaceticketno
                + "' AND C_STATUS IN (" + Trainticket.ISSUED + "," + Trainticket.NONREFUNDABLE + ","
                + Trainticket.FINISHCHANGE + "," + Trainticket.CANTCHANGE + ")";
        try {
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            WriteLog.write("203_TAOBAO_REFUNDBYLIST", subminid + "---true---" + sql);
        }
        catch (Exception e) {
            WriteLog.write("203_TAOBAO_REFUNDBYLIST", subminid + "---error---" + sql);
            ExceptionUtil.writelogByException("203_TAOBAO_REFUNDBYLIST_ERROR", e);
            e.printStackTrace();
        }
    }

    /**
     * 确认改签下单 list版
     * 
     * @param trainOrderChangeResult
     * @return
     * @time 2015年4月23日 上午9:36:42
     * @author Administrator
     */
    public void TaobaoOrderChangin(JSONObject jsonObject) {
        try {
            WriteLog.write("淘宝改签", "改签下单");
            int r1 = new Random().nextInt(10000000);
            String[] st;
            String applyid = jsonObject.getString("apply_ids");
            int applucout = jsonObject.getInteger("apply_count");
            TaobaoHotelInterfaceUtil tbiu = new TaobaoHotelInterfaceUtil();
            WriteLog.write("淘宝改签", "改签下单申请单号处理applyid  " + applyid);
            if (applucout > 1) {
                st = applyid.split(",");
            }
            else {
                st = new String[1];
                st[0] = applyid;
            }
            if (st != null) {

                for (int i = 0; i < st.length; i++) {
                    WriteLog.write("淘宝改签", "改签下单申请单号详情处理applyid  " + st[i]);
                    String ordermsg = tbiu.QuerChangOrderage(Long.parseLong(st[i]));// 订单详情
                    WriteLog.write("淘宝改签", "改签下单申请单号详情处理结束ordermsg  " + ordermsg);
                    if (ordermsg != null) {
                        tbiu.ChangOrderSet(ordermsg);
                        // if (e == null) {//改签回调失败失败
                        // WriteLog.write("淘宝改签", "推送mq失败  " + e);
                        // }
                        // else if (e.length() > 10) {//改签下单成功推mq出票
                        // JSONObject j3 = new JSONObject();
                        // WriteLog.write("淘宝改签", "推送mq  " + e);
                        // TrainpayMqMSGUtil t = new
                        // TrainpayMqMSGUtil("TB_Change_Order");
                        // t.sendTBChangeOrderMQmsg(j3.parseObject(e));//推mq
                        //
                        // }

                    }
                }

            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("淘宝改签", e);
        }

    }

    /**
     * 正式使用版
     * 
     * @param subid
     */
    public void TaobaoOrderChanginId(long subid) {
        try {
            WriteLog.write("101_TAOBAO_CHANGE", "改签下单subid" + subid);
            int r1 = new Random().nextInt(10000000);
            String ordermsg = tbiu.QuerChangOrderage(subid);// 从淘宝获取订单详情第三步
            WriteLog.write("101_TAOBAO_CHANGE", subid + ":改签详情:" + ordermsg);
            if (ordermsg != null) {
                tbiu.ChangOrderSet(ordermsg);
            }
            else {
                WriteLog.write("101_TAOBAO_CHANGE", "订单在淘宝订单详情里获取不到数据，淘宝ID:" + subid);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("101_TAOBAO_CHANGE_ERROR", "订单在淘宝订单详情里获取不到数据，淘宝ID:" + subid);
            ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_ERROR", e);
        }
    }

    /**
     * 淘宝改签数据拼接
     * 
     * @param json
     * @return
     * @time 2015年4月22日 上午11:13:07
     * @author Administrator
     */

    public void ChangOrderSet(String json) {
        // TODO json 是淘宝的订单详情
        // {"train_agent_change_get_response":{"apply_id":3596361284,"from_station_name":"赣州","from_time":"2015-07-08 23:35:00","latest_change_time":"2015-07-01 13:30:17","main_biz_order_id":1108171704848492,"seat_name":1,"status":2,"tickets":{"change_ticket_info":[{"change_fee":0,"sub_biz_order_id":1108171704848492}]},"to_station_name":"鹰潭","to_time":"2015-07-09 06:40:00","total_change_fee":0,"train_num":"K794\/K795","request_id":"101yjq9r6k3w7"}}
        // TODO 后续逻辑是将淘宝的订单详情 封装成同程格式的请求
        JSONObject orderjson = new JSONObject();
        JSONArray tickjsons = new JSONArray();
        TaoBaoReqChange tbr = new TaoBaoReqChange();
        JSONObject retobj = new JSONObject();
        JSONObject jb = JSONObject.parseObject(json);
        JSONObject taobaoorder = new JSONObject();
        taobaoorder = jb.getJSONObject("train_agent_change_get_response");
        JSONArray taobaotick = new JSONArray();
        taobaotick = taobaoorder.getJSONObject("tickets").getJSONArray("change_ticket_info");
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(taobaoorder.getString("main_biz_order_id"));
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        if (orders == null || orders.size() != 1) {
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            return;
        }

        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orders.get(0).getId());
        List<Trainticket> ltt = new ArrayList<Trainticket>();
        String old_zwcode = "0";
        String old_to_station_name = "";
        for (int i = 0; i < order.getPassengers().size(); i++) {
            for (int y = 0; y < order.getPassengers().get(i).getTraintickets().size(); y++) {
                for (int x = 0; x < taobaotick.size(); x++) {
                    if (order.getPassengers().get(i).getTraintickets().get(y).getInterfaceticketno()
                    // 遍历内部和淘宝的订单票乘客
                            .equals(taobaotick.getJSONObject(x).getString("sub_biz_order_id"))) {// 票信息
                        Trainticket tk = order.getPassengers().get(i).getTraintickets().get(y);
                        Trainpassenger tp = order.getPassengers().get(i);
                        JSONObject oldtick = new JSONObject();
                        oldtick.put("passengersename", tp.getName());
                        oldtick.put("passporttypeseid", TongChengTrainUtil.localIdTypeToTongCheng(tp.getIdtype()));
                        oldtick.put("passportseno", tp.getIdnumber());
                        oldtick.put("piaotype", tk.getTickettype());
                        oldtick.put("interfacechangfee", taobaotick.getJSONObject(x).getString("change_fee"));
                        oldtick.put("ticketid", tk.getId());
                        oldtick.put("old_ticket_no", tk.getTicketno());
                        // oldtick.put("interfaceticketno",
                        // taobaotick.getJSONObject(x).getString("sub_biz_order_id"));
                        oldtick.put("interfaceticketno", taobaotick.getJSONObject(x).getString("sub_biz_order_id"));
                        old_zwcode = TongchengSupplyMethod.getzwname(tk.getSeattype());
                        old_to_station_name = tk.getArrival();
                        tickjsons.add(oldtick);
                        ltt.add(tk);
                    }
                }
            }
        }
        orderjson.put("orderid", taobaoorder.getString("main_biz_order_id"));
        orderjson.put("transactionid", order.getOrdernumber());
        orderjson.put("ordernumber", order.getExtnumber());
        orderjson.put("change_checi", taobaoorder.getString("train_num"));
        orderjson.put("change_datetime", taobaoorder.getString("from_time"));
        // orderjson.put("change_zwcode",
        // getseatypesea(taobaoorder.getString("seat_name")));
        // orderjson.put("change_taobaozwcode",
        // taobaoorder.getString("seat_name"));
        orderjson.put("change_zwcode",
                getseatypesea(taobaoorder.getString("seat_name"), taobaoorder.getString("train_num")));
        orderjson.put("change_taobaozwcode", taobaoorder.getString("seat_name"));
        orderjson.put("old_zwcode", old_zwcode);
        orderjson.put("ticketinfo", tickjsons);
        orderjson.put("latest_change_time", taobaoorder.getString("latest_change_time"));
        orderjson.put("apply_id", taobaoorder.getString("apply_id"));
        orderjson.put("reqtoken", taobaoorder.getString("request_id"));
        orderjson.put("isasync", "Y");
        String callbackUrl = PropertyUtil.getValue("TaoBao_Change_CallBack_Url", "Train.properties");
        orderjson.put("callbackurl", callbackUrl);
        // 改签还是变站 1是变站，0是改签
        int ists = taobaotick.getJSONObject(0).containsKey("extend_param") ? taobaotick.getJSONObject(0).getIntValue(
                "extend_param") : 0;
        // 到站
        String to_station_name = taobaoorder.getString("to_station_name");
        // 变更到站
        if (ists == 1) {
            orderjson.put("to_station_name", to_station_name);
            orderjson.put("isTs", true);// 与to_station_name成对存在
            // 日志
            WriteLog.write("101_TAOBAO_CHANGE_STATION_ERROR", "变更到站:" + old_to_station_name + ":" + to_station_name);
        }
        // TODO 封装结束
        String type = tbr.operate(orderjson);// TODO 请求同程改签
        // type是请求同程改签 返回的结果
        JSONObject jsontype = JSONObject.parseObject(type);
        // TODO 这里对 jsontype 进行判断是否成功接收改签请求
        WriteLog.write("淘宝改签", jsontype.toString());
        if (jsontype.getBoolean("success")) {
            WriteLog.write("TaobaoHotelInterfaceUtil_ChangOrderSet", taobaoorder.getString("main_biz_order_id")
                    + "异步改签提交成功");
        }
        else {
            // 这里代表请求改签失败了 无须等待回调后处理 所以不用改签
            // TODO 回调的时候 也会有改签失败的可能 所以这里的逻辑需要复制一份到回调里面
            JSONObject j = new JSONObject();
            j.put("mainbizorderid", order.getQunarOrdernumber());
            j.put("applyid", taobaoorder.get("apply_id").toString());
            j.put("orderidme", order.getId());
            // REP返回MSG
            String msg = ElongHotelInterfaceUtil.getJsonString(jsontype, "msg");
            j.put("errorcode", msg2TaoBaoError(msg));
            String result = CommitChangOrderageOver(j);
            WriteLog.write("TaobaoHotelInterfaceUtil_ChangOrderSet_ERROR", taobaoorder.getString("main_biz_order_id")
                    + ":异步改签提交失败--->" + result);
        }
    }

    /**
     * 将同程接口返回的MSG转换为淘宝的错误类型
     * 
     * @param msgs
     * @return
     * @author fiend
     */
    public static String msg2TaoBaoError(String msgs) {
        if (msgs.contains("已无余票")) {
            return "1";
        }
        else if (msgs.contains("已出票")) {
            return "2";
        }
        else if (msgs.contains("已退票")) {
            return "3";
        }
        else if (msgs.contains("身份验证未通过")) {
            return "4";
        }
        else if (msgs.contains("已定车票")) {
            return "5";
        }
        else if (msgs.contains("新票出票超时")) {
            return "6";
        }
        else if (msgs.contains("价格不符")) {
            return "7";
        }
        else if (msgs.contains("发车时间不符")) {
            return "8";
        }
        else if (msgs.contains("车次未找到")) {
            return "9";
        }
        else if (msgs.contains("网络繁忙或系统故障")) {
            return "10";
        }
        else if (msgs.contains("学生票信息错误")) {
            return "11";
        }
        else if (msgs.contains("被限制消费")) {
            return "12";
        }
        else if (msgs.contains("无座票")) {
            return "13";
        }
        else if (msgs.contains("与本次购票行程冲突")) {
            return "14";
        }
        else if (msgs.contains("预售期不符")) {
            return "15";
        }
        else if (msgs.contains("用户12306账号登录失败")) {
            return "16";
        }
        else if (msgs.contains("乘客信息有误")) {
            return "17";
        }
        else if (msgs.contains("乘客已改签")) {
            return "18";
        }
        else if (msgs.contains("代理账号问题")) {
            return "19";
        }
        else if (msgs.contains("非法席别")) {
            return "20";
        }
        else if (msgs.contains("发车前45分钟以内")) {
            return "21";
        }
        else if (msgs.contains("您申请的新票到站不符合")) {
            return "22";
        }
        else if (msgs.contains("车次停运")) {
            return "23";
        }
        else {
            return "0";
        }
    }

    /**
     * 淘宝改签订单查询
     * 
     * @param applyId
     * @return
     * @time 2015年4月20日 下午8:14:39
     * @author liangwei
     */
    public String QuerChangOrderage(long applyId) {
        try {
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentChangeGetRequest req = new TrainAgentChangeGetRequest();
            TrainAgentChangeGetResponse res;
            req.setAgentId(agentid);
            req.setApplyId(applyId);
            res = client.execute(req, sessionKey);
            return res.getBody();
        }
        catch (Exception e) {
            WriteLog.write("QUERCHANGORDERAGE_ERROR", applyId + "");
            ExceptionUtil.writelogByException("QUERCHANGORDERAGE_ERROR", e);
            return null;
        }

    }

    /**
     * 改签回调成
     * 
     * @param applyId
     * @return
     * @time 2015年4月22日 上午11:49:10
     * @author Administrator
     */
    public String CommitChangOrderage(JSONObject json, JSONArray jsonarry) {
        int irandom = (int) (Math.random() * 100000);
        try {
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":改签成功回调开始--->" + json.toJSONString());
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":改签成功回调开始--->" + jsonarry.toJSONString());
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentChangeAgreeRequest req = new TrainAgentChangeAgreeRequest();
            TrainAgentChangeAgreeResponse res;
            AgentAgreeChangeParam param = new AgentAgreeChangeParam();
            //新增参数   支付账号    changealipayaccount 
            String changealipayaccount = json.getString("changealipayaccount");
            param.setAlipayAccount(changealipayaccount);
            param.setApplyId(json.getLong("applyid"));
            param.setMainBizOrderId(Long.parseLong(json.getString("mainbizorderid")));
            // param.setSellerId(agentid);
            param.setSellerId(json.getLong("sellerid"));
            JSONArray jsa = new JSONArray();
            jsa = json.getJSONArray("cgja");
            ArrayList<ChangeTicketInfo> cti = new ArrayList<ChangeTicketInfo>();
            for (int i = 0; i < jsa.size(); i++) {
                ChangeTicketInfo tickes = new ChangeTicketInfo();
                tickes.setChangeFee(jsa.getJSONObject(i).getLong("changfee"));
                tickes.setHandingFee(jsa.getJSONObject(i).getLong("handing_fee"));
                tickes.setChooseSeat(jsa.getJSONObject(i).getString("chooseseat"));
                if (jsonarry.size() > 0) {
                    String exparam = "{";
                    for (int y = 0; y < jsonarry.size(); y++) {
                        if (y > 0) {
                            exparam += ",";
                        }
                        if (jsonarry.getJSONObject(y).getString("Interfaceticketno") != null
                                && jsa.getJSONObject(i).getLong("subbizorderid") != null
                                && jsonarry.getJSONObject(y).getString("Interfaceticketno")
                                        .equals(jsa.getJSONObject(i).getLong("subbizorderid") + "")) {
                            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":抓到了改签投保-------"
                                    + jsonarry.getJSONObject(y).getString("baodanhao"));
                            exparam += "\"insurance_code\":\"" + jsonarry.getJSONObject(y).getString("baodanhao")
                                    + "\"";
                        }
                    }
                    exparam += "}";
                    tickes.setExtendParam(exparam);
                }
                else {
                    tickes.setExtendParam("{}");
                }

                tickes.setRemark("demo");
                tickes.setRealSeat(jsa.getJSONObject(i).getLong("realseat"));
                tickes.setSubBizOrderId(jsa.getJSONObject(i).getLong("subbizorderid"));
                try {
                    tickes.setCanChange(json.get("refund_online") == null || json.getInteger("refund_online") == 0 ? true
                            : false);
                }
                catch (Exception e) {
                    tickes.setCanChange(true);
                }
                cti.add(tickes);
            }
            param.setExtendParam("{}");
            param.setRemark("demo");
            param.setTickets(cti);
            try {
                if (json.containsKey("changealipaytradeno") && !"".equals(json.getString("changealipaytradeno").trim())
                        && isAlipayno(json.getString("changealipaytradeno").trim())) {
                    param.setAlipayTradeNo(json.getString("changealipaytradeno"));
                }
                else {
                    createtrainorderrc(1, "淘宝改签交易非法流水号--取消回调", Long.parseLong(json.getString("mainbizorderid")), 1l, 1,
                            "系统");
                    // createtrainorderrc(int ywtype, String content, Long
                    // orderid, Long ticketid, int status, String createuser)
                    WriteLog.write("淘宝改签交易非法流水号", param.getApplyId() + "--->" + json.getString("changealipaytradeno"));
                    return "交易号错误";
                }
                // param.setAlipayTradeNo(json.getString("changealipaytradeno")
                // == null ? "" : json
                // .getString("changealipaytradeno"));
                // TODO fiend 2 郭征举 这个如果没有交易号 或者不符合格式 不往下走了，直接返回失败，记录log
                // ，失败原因是交易号有误
                WriteLog.write("淘宝改签交易流水号", param.getApplyId() + "--->" + json.getString("changealipaytradeno"));
            }
            catch (Exception e) {
                WriteLog.write("淘宝改签交易流水号_ERROR", param.getApplyId() + "");
                ExceptionUtil.writelogByException("淘宝改签交易流水号_ERROR", e);
            }
            req.setParam(param);
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":tickets:" + param.getTickets().toString());
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":SellerId:" + param.getSellerId());
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":MainBizOrderId:" + param.getMainBizOrderId());
            res = client.execute(req, sessionKey);
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE", irandom + ":" + res.getBody());
            if (res.getErrorCode() != null) {
                String content = "回调淘宝失败";

                createtrainorderrc(1, content, json.getLong("orderidme"), 0l, Trainticket.THOUGHCHANGE, "系统接口");

                return res.getMsg();
            }
            else {
                WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE",
                        irandom + ":body:" + res.getBody() + ">code：" + res.getErrorCode() + ">msg." + res.getMsg());
                String content = "回调淘宝成功";
                createtrainorderrc(1, content, json.getLong("orderidme"), 0l, Trainticket.THOUGHCHANGE, "系统接口");
            }
            return res.getBody();
        }
        catch (Exception e) {
            String content = "回调淘宝失败";
            createtrainorderrc(1, content, json.getLong("orderidme"), 0l, Trainticket.THOUGHCHANGE, "系统接口");
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_TRUE",
                    irandom + ":改签成功回调异常--->" + e.getMessage() + "   " + e.hashCode() + "  " + e.getLocalizedMessage());
            ExceptionUtil.writelogByException("", e);
            return e.toString();
        }

    }

    /**
     * 验证是否有交易号 且格式符不符合 guozhengju
     * 
     * @param payno
     * @return
     */
    public boolean isAlipayno(String payno) {
        String patterner = "";
        boolean result = true;
        String sql = "SELECT * FROM AlipayNoPattern";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);

        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            String status = map.get("status").toString();
            if ("1".equals(status)) {
                patterner = map.get("pattern").toString();
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patterner);
                java.util.regex.Matcher match = pattern.matcher(payno);
                if (match.matches()) {
                    result = true;
                }
                else {
                    result = false;
                }
            }
            else {
                result = true;
            }

        }
        WriteLog.write("201_TAOBAO交易流水号是否合法", "payno:" + payno + ";result:" + result);
        return result;
    }

    /**
     * 改签回调失败
     * 
     * @param order
     * @param apply_id
     * @param ltt
     * @return
     * @time 2015年4月22日 下午1:52:41
     * @author Administrator
     */

    public String CommitChangOrderageOver(JSONObject json) {
        int irandom = (int) (Math.random() * 100000);
        try {
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_FALSE", irandom + ":改签失败回调开始--->json" + json);
            Long x = 0L;
            if (json.get("errorcode") == null || json.get("errorcode").equals("")) {
                x = 0L;
            }
            else {
                x = json.getLong("errorcode");
            }
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentChangeRefuseRequest req = new TrainAgentChangeRefuseRequest();
            TrainAgentChangeRefuseResponse res;

            AgentRefuseChangeParam param = new AgentRefuseChangeParam();
            param.setApplyId(json.getLong("applyid"));
            System.out.println("agentid------" + agentid);
            param.setSellerId(agentid);
            param.setExtendParam("{}");
            param.setRefuseType(x);
            param.setRemark("demo");
            param.setMainBizOrderId(Long.parseLong(json.getString("mainbizorderid")));
            req.setParam(param);
            System.out.println("sessionKey------" + sessionKey);
            res = client.execute(req, sessionKey);
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_FALSE", irandom + ":改签失败回调结束--->body:" + res.getBody()
                    + ">code：" + res.getErrorCode() + ">msg." + res.getMsg());

            if (res.getErrorCode() != null) {
                String content = "无法改签_淘宝回调失败";
                createtrainorderrc(1, content, json.getLong("orderidme"), 0l, Trainticket.THOUGHCHANGE, "系统接口");

                return null;
            }
            String content = "无法改签_淘宝回调成功";
            createtrainorderrc(1, content, json.getLong("orderidme"), 0l, Trainticket.THOUGHCHANGE, "系统接口");
            return "SUCCESS";
        }
        catch (Exception e) {
            String content = "无法改签_回调淘宝失败_异常";
            createtrainorderrc(1, content, json.getLong("orderidme"), 0l, Trainticket.THOUGHCHANGE, "系统接口");
            WriteLog.write("103_TAOBAO_CHANGE_CALLBACK_FALSE",
                    irandom + ":改签失败回调异常--->" + e.getMessage() + "   " + e.hashCode() + "  " + e.getLocalizedMessage());

            e.printStackTrace();
            return null;
        }

    }

    private boolean isRealEnnull(String str) {
        if (str == null) {
            return false;
        }
        if ("".equals(str)) {
            return false;
        }
        return true;
    }

    public String IDnumberType(String val) {

        if (val.equals("二代身份证")) {
            return "0";
        }
        else if (val.equals("一代身份证")) {
            return "0";
        }
        else if (val.equals("护照")) {
            return "1";
        }
        else if (val.equals("港澳通行证")) {
            return "4";
        }
        else if (val.equals("台湾通行证")) {
            return "5";
        }

        return "-1";
    }

    /**
     * 坐席转换 数字转汉字
     * 
     * @param str_st
     * @return
     */
    public static String getseattypes(String str_st, String traino) {
        String i_st = "";
        if (str_st.equals("-1"))
            return "非法值";
        if (str_st.equals("17")) {// 站票
            return "无座";
            /*if (traino.startsWith("D")) {
                i_st = "二等座";
            }
            else if (traino.startsWith("C")) {
                i_st = "软座";
            }
            else {
                i_st = "硬座";
            }*/
        }
        else if (str_st.equals("1")) {
            i_st = "硬座";
        }
        else if (str_st.equals("5")) {
            i_st = "软座";
        }
        else if (str_st.equals("13")) {
            i_st = "一等座";
        }
        else if (str_st.equals("14")) {
            i_st = "二等座";
        }
        else if (str_st.equals("2")) {
            i_st = "硬卧";
        }
        else if (str_st.equals("3")) {
            i_st = "硬卧";
        }
        else if (str_st.equals("4")) {
            i_st = "硬卧";
        }
        else if (str_st.equals("6")) {
            i_st = "软卧";
        }
        else if (str_st.equals("7")) {
            i_st = "软卧";
        }
        else if (str_st.equals("8")) {
            i_st = "软卧";
        }
        else if (str_st.equals("9")) {
            i_st = "商务座";
        }
        else if (str_st.equals("10")) {
            i_st = "一等包座";
        }
        else if (str_st.equals("11")) {
            i_st = "观光座";
        }
        else if (str_st.equals("12")) {
            i_st = "特等座";
        }
        else if (str_st.equals("15")) {
            i_st = "高级软卧";
        }
        else if (str_st.equals("16")) {
            i_st = "高级软卧";
        }
        else {
            i_st = "其它";
        }
        return i_st;
    }

    /**
     * 淘宝改签 zwcode转换
     * 
     * @param str_st
     * @return
     */
    public static String getseatypesea(String str_st, String traino) {
        String i_st = "";
        if (str_st.equals("-1"))
            return "-1";
        if (str_st.equals("17")) {//
            // i_st = "17";
            if (traino.startsWith("D")) {
                i_st = "O";
            }
            else if (traino.startsWith("C")) {
                i_st = "2";
            }
            else {
                i_st = "1";
            }
        }
        else if (str_st.equals("1")) {
            i_st = "1";
        }
        else if (str_st.equals("5")) {
            i_st = "2";
        }
        else if (str_st.equals("13")) {
            i_st = "M";
        }
        else if (str_st.equals("14")) {
            i_st = "O";
        }
        else if (str_st.equals("2")) {
            i_st = "3";
        }
        else if (str_st.equals("3")) {
            i_st = "3";
        }
        else if (str_st.equals("4")) {
            i_st = "3";
        }
        else if (str_st.equals("6")) {
            i_st = "4";
        }
        else if (str_st.equals("7")) {
            i_st = "4";
        }
        else if (str_st.equals("8")) {
            i_st = "4";
        }
        else if (str_st.equals("9")) {
            i_st = "9";
        }
        else if (str_st.equals("10")) {
            i_st = "M";
        }
        else if (str_st.equals("11")) {
            i_st = "1";
        }
        else if (str_st.equals("12")) {
            i_st = "P";
        }
        else if (str_st.equals("15")) {
            i_st = "6";
        }
        else if (str_st.equals("16")) {
            i_st = "6";
        }
        else {
            i_st = "1";
        }
        return i_st;
    }

    /**
     * 坐席转换 汉字转数字
     * 
     * @param str_st
     * @return
     */
    public static String getseattype(String str_st) {
        String i_st = "";
        if (str_st.equals("非法值"))
            return "-1";
        if (str_st.equals("站票")) {//
            i_st = "17";
        }
        else if (str_st.equals("硬座")) {
            i_st = "1";
        }
        else if (str_st.equals("软座")) {
            i_st = "5";
        }
        else if (str_st.equals("一等软座")) {
            i_st = "13";
        }
        else if (str_st.equals("二等软座")) {
            i_st = "14";
        }
        else if (str_st.equals("硬卧")) {
            i_st = "2";
        }
        else if (str_st.equals("硬卧")) {
            i_st = "3";
        }
        else if (str_st.equals("硬卧")) {
            i_st = "4";
        }
        else if (str_st.equals("软卧上")) {
            i_st = "6";
        }
        else if (str_st.equals("软卧中")) {
            i_st = "7";
        }
        else if (str_st.equals("软卧下")) {
            i_st = "8";
        }
        else if (str_st.equals("商务座")) {
            i_st = "9";
        }
        else if (str_st.equals("一等包座")) {
            i_st = "10";
        }
        else if (str_st.equals("观光座")) {
            i_st = "11";
        }
        else if (str_st.equals("特等座")) {
            i_st = "12";
        }
        else if (str_st.equals("高级软卧上")) {
            i_st = "15";
        }
        else if (str_st.equals("高级软卧下")) {
            i_st = "16";
        }
        else if (str_st.equals("动卧")) {
            i_st = "19";
        }
        else {
            i_st = "18";
        }
        return i_st;
    }

    /**
     * 供应商收单处理 收单模式 保存数据库
     */
    public void saveOrderInfoShouDan(String ordermsg, long msgtype, String key) {
        String c_agentid = PropertyUtil.getValue("TaobaoHotelInterfaceUtil_TaobaoAgentid", "Train.properties");
        try {
            String countsql = "select count(id) from T_TRAINORDERMSG where C_KEY='" + key + "' and C_MSGTYPE="
                    + msgtype;
            int c1 = Server.getInstance().getSystemService().countGiftBySql(countsql);
            WriteLog.write("t火车票接口_4.5收单", ordermsg);
            if (c1 <= 0) {
                String msgsql = "insert into T_TRAINORDERMSG(C_MSG,C_TIME,C_STATE,C_USERID,C_KEY,C_MSGTYPE,C_INTERFACETYPE,c_agentid) values('"
                        + ordermsg
                        + "','"
                        + new Timestamp(System.currentTimeMillis())
                        + "',1,0,'"
                        + key
                        + "',"
                        + msgtype + ",2," + c_agentid + ")";
                WriteLog.write("t火车票接口_4.5收单", msgsql);
                int i1 = Server.getInstance().getSystemService().excuteGiftBySql(msgsql);
            }
            else {
                WriteLog.write("t火车票接口_4.5收单", "已存在：" + key);
            }
        }
        catch (Exception e) {
            logger.error("tongchengtrainorder_updateExtordercreatetime", e.fillInStackTrace());
        }
    }

    /**
     * 淘宝回调坐席编码转换
     * 
     * @param str_st
     * @param str_no
     * @return
     */
    public static String CackBackSuccessseao(String str_st, String str_no) {
        if (str_no == null) {
            str_no = "";
        }
        if (str_st == null) {
            str_st = "";
        }
        String i_st = "";
        if (str_st.equals("非法值"))
            return "-1";
        if (str_no.contains("无座")) {//
            i_st = "17";
        }
        else if (str_st.equals("硬座") || str_st.equals("硬卧代硬座")) {
            i_st = "1";
        }
        else if (str_st.equals("软座") || str_st.equals("软卧代软座")) {
            i_st = "5";
        }
        else if (str_st.equals("一等软座") || str_st.equals("一等座")) {
            i_st = "13";
        }
        else if (str_st.equals("二等软座") || str_st.equals("二等座") || str_st.equals("软卧代二等座")) {
            i_st = "14";
        }
        else if (str_st.equals("硬卧")) {
            if (str_no.contains("上")) {
                i_st = "2";
            }
            else if (str_no.contains("中")) {
                i_st = "3";
            }
            else if (str_no.contains("下")) {
                i_st = "4";
            }
            else {
                i_st = "16";
            }

        }
        else if (str_st.equals("软卧")  ) {
            if (str_no.contains("上")) {
                i_st = "6";
            }
            else if (str_no.contains("中")) {
                i_st = "7";
            }
            else if (str_no.contains("下")) {
                i_st = "8";
            }
            else {
                i_st = "8";
            }
        }
        else if (str_st.equals("动卧")) {
            i_st = "19";
        }
        else if (str_st.equals("商务座")) {
            i_st = "9";
        }
        else if (str_st.equals("一等包座")) {
            i_st = "10";
        }
        else if (str_st.equals("观光座")) {
            i_st = "11";
        }
        else if (str_st.equals("特等座")) {
            i_st = "12";
        }
        else if (str_st.equals("高级软卧") || str_st.equals("高级动卧")) {
            if (str_no.contains("上")) {
                i_st = "15";
            }
            else if (str_no.contains("下")) {
                i_st = "16";
            }
            else {
                i_st = "16";
            }
        }

        else {
            i_st = "18";
        }
        return i_st;
    }

    /**
     * 退票回调
     * 
     * @param jsonString
     * @return
     */
    public String taobaoDrawerNotice(JSONObject jsonString) {
        try {

            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentReturnticketConfirmRequest req = new TrainAgentReturnticketConfirmRequest();
            TrainAgentReturnticketConfirmResponse res;
            req.setAgentId(agentid);
            req.setAgreeReturn(Boolean.parseBoolean(jsonString.get("agree_return").toString()));// Boolean 是 是否同意退票
            req.setMainBizOrderId(Long.parseLong(jsonString.get("main_order_id").toString()));// 订单id
            req.setRefuseReturnReason(jsonString.get("refuse_return_reason").toString());
            req.setRefundFee(Long.parseLong(jsonString.get("refund_fee").toString()));// 退票金额
            req.setSubBizOrderId(Long.parseLong(jsonString.get("sub_biz_order_id").toString()));// 火车票 id
            req.setBuyerId(Long.parseLong(jsonString.get("buyerid").toString()));
            Date d = new Date();
            req.setTimestamp(d.getTime());
            res = client.execute(req, sessionKey);
            // res=client.execute(req,sessionKey);//正式上线请求
            WriteLog.write("203_TAOBAO_refund_callback",
                    jsonString.get("main_order_id").toString() + "--->" + res.getBody());
            if (res.getBody() == null) {
                // createTrainorderrc(1, trainorder.getId(), "回调淘宝失败",
                // "淘宝退票退款接口", Trainticket.REFUNDFALSE, ticket.getId());
                WriteLog.write("淘宝退票", jsonString.get("mainBizOrderId") + "淘宝退票失败回调失败:" + res.getErrorCode() + ":"
                        + res.getMsg());
                return null;
            }
            // createTrainorderrc(1, trainorder.getId(), "淘宝退票回调成功", "淘宝退票退款接口",
            // Trainticket.REFUNDED12306, ticket.getId());
            WriteLog.write("淘宝退票", res.getBody());
            return res.getBody();
        }
        catch (ApiException e) {
            // createTrainorderrc(1, trainorder.getId(), "淘宝退票回调淘宝失败",
            // "淘宝退票退款接口", Trainticket.REFUNDFALSE, ticket.getId());
            WriteLog.write("淘宝退票:",
                    jsonString.get("mainBizOrderId") + "淘宝退票失败回调失败:" + e.getErrCode() + ":" + e.getErrMsg());
            return null;
        }

    }

    /**
     * 写操作记录
     * 
     * @param ywtype
     * @param content
     * @param orderid
     * @param ticketid
     * @param status
     * @param createuser
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
     * taobao拉单
     * 
     * @time 2015年12月2日 上午11:06:16
     * @author fiend
     */
    public void bookorders() {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentBookordersGetRequest req = new TrainAgentBookordersGetRequest();
        req.setAgentId(agentid);
        TrainAgentBookordersGetResponse res;
        boolean result = false;
        try {
            try {
                // 是否需要收单
                if (getdecideToShouDanByOrderList()) {
                    //                    return;
                    result = true;
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            res = client.execute(req, sessionKey);
            WriteLog.write("303_TAOBAO_ORDERSBYLIST", "订单池总数--->" + res.getOrderCount());
            WriteLog.write("303_TAOBAO_ORDERSBYLIST", "订单池错误--->" + res.getErrorCode());
            WriteLog.write("303_TAOBAO_ORDERSBYLIST", "订单池订单IDS--->" + res.getOrderIds());
            try {
                Thread.sleep(10000L);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            if (getNowTime(new Date())) {
                if (res.getOrderIds() != null && !"".equals(res.getOrderIds())) {
                    for (int ii = 0; ii < res.getOrderIds().split(",").length; ii++) {
                        String taobao_order_no = res.getOrderIds().split(",")[ii];
                        String ordermes = this.taobaoQuerOrder(taobao_order_no);
                        WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDERDETAILS", taobao_order_no + "--->" + ordermes);
                        JSONObject orderme = JSONObject.parseObject(ordermes);// 获取订单详情
                        WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no + ":orderme:" + orderme);
                        String sql = "SELECT ID,ordertype,C_ORDERSTATUS,C_CREATETIME FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                                + taobao_order_no + "'";
                        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                        WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no + ":list.size=" + list.size());
                        long tomeoutbydeptime = 0;
                        if (list.size() < 1) {
                            boolean isNeedStandingSeat = false;
                            String standingSeatprice = "";
                            WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no + ":orderme=" + orderme);
                            if (orderme != null) {
                                JSONObject orderDetails = new JSONObject();
                                // orderDetails =
                                // orderDetails.parseObject(orderme);
                                orderDetails = orderme.getJSONObject("train_agent_order_get_response");
                                String ttp_order_id = orderDetails.getString("ttp_order_id");
                                int order_type = orderDetails.getIntValue("order_type");
                                WriteLog.write(
                                        "303_TAOBAO_ORDERSBYLIST_ORDER",
                                        taobao_order_no + ":orderDetails:" + orderDetails
                                                + ";orderDetails.get(\"is_success\")-->"
                                                + orderDetails.get("is_success"));
                                if (Boolean.parseBoolean(orderDetails.get("is_success").toString()) == true)// 结果成功返回
                                {
                                    JSONObject jb = new JSONObject();
                                    JSONObject jb2 = new JSONObject();
                                    jb = orderDetails;// 获取json投标
                                    jb2 = jb.getJSONObject("tickets");// 获取票字段
                                    Trainorder trainorder = new Trainorder();// 创建订单
                                    JSONArray TrainTickets = new JSONArray();
                                    TrainTickets = jb2.getJSONArray("to_agent_ticket_info");// 获取票的字段转换数组

                                    List<Trainpassenger> trainplist = new ArrayList<Trainpassenger>();
                                    WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                            + ":TrainTickets.size()=" + TrainTickets.size());
                                    for (int i = 0; i < TrainTickets.size(); i++) {
                                        List<Trainticket> Trainticklist = new ArrayList<Trainticket>();
                                        Trainticket trainticket = new Trainticket();// 票
                                        Trainpassenger ts = new Trainpassenger();// 客
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");

                                        Date to = df.parse(TrainTickets.getJSONObject(i).getString("to_time")
                                                .toString());
                                        Date from = df.parse(TrainTickets.getJSONObject(i).getString("from_time")
                                                .toString());
                                        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
                                        long nh = 1000 * 60 * 60;// 一小时的毫秒数
                                        long nm = 1000 * 60;// 一分钟的毫秒数
                                        long ls = to.getTime() - from.getTime();
                                        tomeoutbydeptime = from.getTime() - 3 * nh;
                                        String riqi = "";

                                        long hour = ls / nh;// 计算差多少小时
                                        long min = ls % nh / nm;// 计算差多少分钟
                                        riqi = hour + ":" + min;

                                        trainticket.setStatus(Trainticket.WAITISSUE);
                                        trainticket.setCosttime(riqi);
                                        trainticket.setArrivaltime(dfs.format(to));// 新增

                                        trainticket.setDeparttime(df.format(from));
                                        trainticket.setDeparture(TrainTickets.getJSONObject(i)
                                                .getString("from_station"));
                                        trainticket.setArrival(TrainTickets.getJSONObject(i).getString("to_station"));// 到达

                                        trainticket.setPrice(Float.parseFloat(TrainTickets.getJSONObject(i).getString(
                                                "ticket_price")) / 100);
                                        trainticket.setPayprice(Float.parseFloat(TrainTickets.getJSONObject(i)
                                                .getString("ticket_price")) / 100);
                                        // 淘寶票的id
                                        trainticket.setInterfaceticketno(TrainTickets.getJSONObject(i).getString(
                                                "sub_order_id"));
                                        trainticket.setTrainno(TrainTickets.getJSONObject(i).getString("train_num"));
                                        trainticket.setSeattype(getseattypes(
                                                TrainTickets.getJSONObject(i).getString("seat"), TrainTickets
                                                        .getJSONObject(i).getString("train_num")));
                                        WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                                + ":TrainTickets.getJSONObject(i).getString(\"seat\")="
                                                + TrainTickets.getJSONObject(i).getString("seat"));
                                        if (TrainTickets.getJSONObject(i).get("seat") != null
                                                && "17".equals(TrainTickets.getJSONObject(i).getString("seat"))) {
                                            isNeedStandingSeat = true;
                                            standingSeatprice = trainticket.getPrice() + "";
                                        }
                                        trainticket.setTickettype(changeTicketTypeTaobao2DB(TrainTickets.getJSONObject(
                                                i).getString("passenger_type")));

                                        trainticket.setInsurorigprice(Float.parseFloat(TrainTickets.getJSONObject(i)
                                                .getString("insurance_price")) / 100);

                                        Trainpassenger trainpassenger = new Trainpassenger();
                                        trainpassenger.setBirthday(TrainTickets.getJSONObject(i).getString("birthday"));
                                        trainpassenger.setIdnumber(TrainTickets.getJSONObject(i).getString(
                                                "certificate_num"));
                                        String certTypeValue = TrainTickets.getJSONObject(i).getString(
                                                "certificate_type");
                                        WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                                + ":certTypeValue=" + certTypeValue);
                                        if ("0".equals(certTypeValue)) {
                                            certTypeValue = "1";
                                        }
                                        else if ("1".equals(certTypeValue)) {
                                            certTypeValue = "3";
                                        }
                                        trainpassenger.setIdtype(Integer.parseInt(certTypeValue));
                                        trainpassenger.setName(TrainTickets.getJSONObject(i)
                                                .getString("passenger_name"));
                                        Trainticklist.add(trainticket);
                                        trainpassenger.setTraintickets(Trainticklist);
                                        try {
                                            // 解析淘宝学生票信息
                                            WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                                    + ":trainticket.getTickettype()=" + trainticket.getTickettype());
                                            if (3 == trainticket.getTickettype()) {
                                                JSONObject jostudentinfo = TrainTickets.getJSONObject(i).getJSONObject(
                                                        "student_info");
                                                List<TrainStudentInfo> trainstudentinfos = new ArrayList<TrainStudentInfo>();
                                                TrainStudentInfo trainstudentinfo = new TrainStudentInfo();
                                                trainstudentinfo
                                                        .setClasses(jostudentinfo.getString("classes") == null ? ""
                                                                : jostudentinfo.getString("classes"));
                                                trainstudentinfo
                                                        .setDepartment(jostudentinfo.getString("depart_ment") == null ? ""
                                                                : jostudentinfo.getString("depart_ment"));
                                                trainstudentinfo.setEductionalsystem(jostudentinfo
                                                        .getString("eductional_system") == null ? "" : jostudentinfo
                                                        .getString("eductional_system"));
                                                trainstudentinfo.setEntranceyear(jostudentinfo
                                                        .getString("entrance_year") == null ? "" : jostudentinfo
                                                        .getString("entrance_year"));
                                                trainstudentinfo
                                                        .setFromcity(jostudentinfo.getString("from_city") == null ? ""
                                                                : jostudentinfo.getString("from_city"));
                                                trainstudentinfo
                                                        .setSchoolname(jostudentinfo.getString("school_name") == null ? ""
                                                                : jostudentinfo.getString("school_name"));
                                                trainstudentinfo.setSchoolprovince(jostudentinfo
                                                        .getString("school_province") == null ? "" : jostudentinfo
                                                        .getString("school_province"));
                                                trainstudentinfo
                                                        .setStudentcard(jostudentinfo.getString("card") == null ? ""
                                                                : jostudentinfo.getString("card"));
                                                trainstudentinfo
                                                        .setStudentno(jostudentinfo.getString("student_no") == null ? ""
                                                                : jostudentinfo.getString("student_no"));
                                                trainstudentinfo
                                                        .setTocity(jostudentinfo.getString("to_city") == null ? ""
                                                                : jostudentinfo.getString("to_city"));
                                                trainstudentinfo.setSchoolnamecode("");
                                                trainstudentinfo.setSchoolprovincecode("");
                                                trainstudentinfo.setFromcitycode("");
                                                trainstudentinfo.setTocitycode("");
                                                trainstudentinfo.setArg1(0l);
                                                trainstudentinfo.setArg2("");
                                                trainstudentinfo.setArg3(0l);
                                                trainstudentinfos.add(trainstudentinfo);
                                                trainpassenger.setTrainstudentinfos(trainstudentinfos);
                                            }
                                        }
                                        catch (Exception e) {
                                            WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                                    + ":解析学生票_exception");
                                        }
                                        trainplist.add(trainpassenger);

                                    }
                                    trainorder.setPassengers(trainplist);
                                    trainorder.setOrderprice(Float.parseFloat(jb.get("total_price").toString()) / 100);
                                    trainorder.setContacttel(jb.getString("telephone"));
                                    trainorder.setAgentcontact("1");
                                    trainorder.setAgentcontacttel("");
                                    trainorder.setTaobaosendid("0");
                                    trainorder.setInsureadreess(jb.getString("address"));// 保险邮寄地址
                                    trainorder.setContactuser(jb.getString("relation_name"));
                                    trainorder.setQunarOrdernumber(jb.getString("main_order_id"));
                                    trainorder.setOrderstatus(Trainorder.WAITPAY);
                                    trainorder.setState12306(Trainorder.WAITORDER);
                                    trainorder.setAgentid(getTaobaoAgetid());
                                    trainorder.setInterfacetype(TrainInterfaceMethod.TAOBAO);
                                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                                    trainorder.setCreatetime(ts);
                                    String latestissuetime = "";
                                    Timestamp timeoutime;
                                    WriteLog.write(
                                            "303_TAOBAO_ORDERSBYLIST_ORDER",
                                            taobao_order_no + ":jb.getString(\"latest_issue_time\")"
                                                    + jb.getString("latest_issue_time"));
                                    if (jb.getString("latest_issue_time") != null
                                            && jb.getString("latest_issue_time") != "") {
                                        latestissuetime = jb.getString("latest_issue_time");
                                    }
                                    else {
                                        latestissuetime = TimeUtil.longToString(tomeoutbydeptime,
                                                TimeUtil.yyyyMMddHHmmssSSS);
                                        WriteLog.write("TAOBAO没有最迟出票时间", trainorder.getQunarOrdernumber()
                                                + "------>计算出来的超时时间:" + latestissuetime);
                                    }
                                    timeoutime = ts.valueOf(latestissuetime);
                                    trainorder.setOrdertimeout(timeoutime);
                                    trainorder.setCreateuser("淘宝网");
                                    WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no + ":order_type="
                                            + order_type);
                                    if (order_type == 1) {
                                        trainorder.setOrdertype(4);
                                    }
                                    else if (order_type == 2) {// 线下票
                                        trainorder.setOrdertype(2);
                                    }
                                    else {
                                        trainorder.setOrdertype(1);
                                    }
                                    WriteLog.write("TaobaoHotelInterfaceUtil_AnalysisOuTtick", "jb=" + jb
                                            + ";ttp_order_id=" + ttp_order_id + ";isNeedStandingSeat="
                                            + isNeedStandingSeat + ";standingSeatprice=" + standingSeatprice
                                            + ";taobao_order_no=" + taobao_order_no);
                                    newhandleTaoBaoOrder(trainorder, jb, ttp_order_id, order_type, isNeedStandingSeat,
                                            standingSeatprice, taobao_order_no, result);
                                    /*
                                     * trainorder =
                                     * Server.getInstance().getTrainService
                                     * ().createTrainorder(trainorder);
                                     * createTaoBaoTrainOrderNumberChange
                                     * (trainorder.getId(),
                                     * jb.getString("main_order_id"),
                                     * ttp_order_id); if (order_type == 1) {
                                     * createTaoBaoSession(trainorder); } if
                                     * (isNeedStandingSeat) {
                                     * createTrainOrderExtSeat
                                     * (trainorder.getId(), "[{\"0\":" +
                                     * standingSeatprice + "}]"); } if
                                     * (isNeedStandingSeat) {
                                     * createTrainOrderExtSeat
                                     * (trainorder.getId(), "[{\"0\":" +
                                     * standingSeatprice + "}]"); }
                                     * WriteLog.write(
                                     * "303_TAOBAO_ORDERSBYLIST_ORDER",
                                     * taobao_order_no + ":" +
                                     * trainorder.getQunarOrdernumber() + ":" +
                                     * trainorder.getId()); // if
                                     * (getNowTime(new Date())) { //TODO
                                     * fiend2zhengju 这里原本是发订单MQ 如果是线下票 走征举逻辑
                                     * activeMQroordering(trainorder.getId());
                                     * // saveTrainOrder(TrainPassenger, //
                                     * TrainTickets); // 代理商出票保存数据和回调 // }
                                     */}
                                else // 结果是不返回
                                {
                                    WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no + ":未获取到订单详情");

                                    this.exceptionS = "淘宝订单详情查询为false";
                                }
                            }
                            else {
                                this.exceptionS = "淘宝订单详情查询为空误";
                                WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                        + ":数据无结果exceptionS：淘宝订单详情查询为空误。orderme:" + orderme + ":");
                            }
                        }
                        else {
                            if (!savePushOrder(orderme, list, taobao_order_no)) {
                                this.exceptionS = "淘宝出票订单号重复";
                                WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", taobao_order_no
                                        + ":淘宝出票订单号重复：淘宝出票订单号重复list.size:" + list.size() + ":taobao_order_no"
                                        + taobao_order_no);
                            }
                        }
                    }
                }
            }

        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("303_TAOBAO_ORDERSBYLIST_ORDER_ERROR", e);
        }
    }

    /**
     * 获取对应系统的ITrainService
     * 
     * @param systemdburlString
     * @return
     */
    private ISystemService getSystemServiceOldDB() {
        String systemdburlString = PropertyUtil.getValue("offlineservice", "Train.properties");
        // String
        // systemdburlString="http://121.40.241.126:9001/cn_service/service/";
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ISystemService) factory.create(ISystemService.class,
                    systemdburlString + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // private DB

    /**
     * 淘宝线下退款回调
     * 
     * @param main_biz_order_id
     *            主订单id
     * @param ttp_id
     * @param refund_type
     *            退款类型1.改签 2.退票
     * @param sub_order_id
     *            子订单号（票号）
     * @param seller_id
     *            代理商id
     * @param trade_no
     *            支付宝交易号 
     * @param user_id
     *            买家id
     * @param refund_fee
     *            退款金额
     * @param apply_id
     *            退款单号
     * @param agree_refund
     *            1:同意退款
     * @param refuse_reason
     *            拒绝退款原因
     * @return
     */
    public String refundfee(String main_biz_order_id, String ttp_id, int refund_type, String sub_order_id,
            String seller_id, String trade_no, String user_id, String refund_fee, String apply_id, int agree_refund,
            int refuse_reason) {
        WriteLog.write("TaobaoHotelInterfaceUtil_refundfee", "main_biz_order_id:" + main_biz_order_id + "--->ttp_id:"
                + ttp_id + "--->refund_type:" + refund_type + "--->sub_order_id:" + sub_order_id + "--->seller_id:"
                + seller_id + "--->trade_no:" + trade_no + "--->user_id:" + user_id + "--->refund_fee:" + refund_fee
                + "--->apply_id:" + apply_id + "--->agree_refund:" + agree_refund + "--->refuse_reason:"
                + refuse_reason);
        String resultString = "-1";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentAutorefundRefundfeeRequest trainAgentAutorefundRefundfeeRequest = new TrainAgentAutorefundRefundfeeRequest();
        TrainAgentAutorefundRefundfeeResponse res;
        trainAgentAutorefundRefundfeeRequest.setApplyId(Long.valueOf(apply_id));
        trainAgentAutorefundRefundfeeRequest.setMainBizOrderId(Long.valueOf(main_biz_order_id));
        trainAgentAutorefundRefundfeeRequest.setSellerId(Long.valueOf(seller_id));
        trainAgentAutorefundRefundfeeRequest.setTtpId(Long.valueOf(ttp_id));
        trainAgentAutorefundRefundfeeRequest.setUserId(Long.valueOf(user_id));
        trainAgentAutorefundRefundfeeRequest.setTradeNo(trade_no);
        trainAgentAutorefundRefundfeeRequest.setTimestamp(System.currentTimeMillis());
        if (1 == agree_refund) {// 同意退款
            trainAgentAutorefundRefundfeeRequest.setAgreeRefund(true);
            trainAgentAutorefundRefundfeeRequest.setRefundFee(Long.valueOf(refund_fee));
            trainAgentAutorefundRefundfeeRequest.setRefundType(Long.valueOf(refund_type));
            trainAgentAutorefundRefundfeeRequest.setRefuseReason("");
            trainAgentAutorefundRefundfeeRequest.setSubOrderId(0l);
        }
        else {
            trainAgentAutorefundRefundfeeRequest.setAgreeRefund(false);
            trainAgentAutorefundRefundfeeRequest.setRefundFee(0l);
            trainAgentAutorefundRefundfeeRequest.setRefundType(0l);
            trainAgentAutorefundRefundfeeRequest.setRefuseReason(refuseReason(refuse_reason));
            trainAgentAutorefundRefundfeeRequest.setSubOrderId(0l);
        }
        try {
            res = client.execute(trainAgentAutorefundRefundfeeRequest, sessionKey);
            WriteLog.write("403_TAOBAO_REFUNDFEE_CALLBACK", apply_id + "--->" + res.getBody());
            if (res.getResul().getSuccess()) {
                resultString = "SUCCESS";
            }
            else {
                resultString = res.getResul().getResultMsg();
            }
        }
        catch (ApiException e) {
            WriteLog.write("ERROR_TaobaoHotelInterfaceUtil_refundfee", apply_id);
            ExceptionUtil.writelogByException("ERROR_TaobaoHotelInterfaceUtil_refundfee", e);
            resultString = e.getErrMsg();
        }
        catch (Exception e) {
            WriteLog.write("ERROR_TaobaoHotelInterfaceUtil_refundfee", apply_id);
            ExceptionUtil.writelogByException("ERROR_TaobaoHotelInterfaceUtil_refundfee", e);
            resultString = "调用淘宝退款,Exception";
        }
        WriteLog.write("403_TAOBAO_REFUNDFEE_CALLBACK", apply_id + "--->" + resultString);
        return resultString;
    }

    /**
     * 1、已经退款 2、无退款 3、金额不符 4、退款处理中
     */
    private String refuseReason(int refuse_reason) {
        // 无退款
        if (refuse_reason == 1) {
            return "2";
        }
        // 已退款
        else if (refuse_reason == 2) {
            return "1";
        }
        // 退款处理中
        else if (refuse_reason == 3) {
            return "4";
        }
        // 退款金额不一致
        else if (refuse_reason == 4) {
            return "3";
        }
        else {
            return "2";
        }
    }

    /**
     * 去待处理退票池里抓取退票
     * 
     * @author fiend
     */
    public void returnOrdersByList(int offline) {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentReturnordersGetRequest req = new TrainAgentReturnordersGetRequest();
        req.setAgentId(agentid);
        req.setOffline(Long.valueOf(offline));
        TrainAgentReturnordersGetResponse res;
        try {
            res = client.execute(req, sessionKey);
            System.out.println(res.getOrderCount());
            System.out.println(res.getErrorCode());
            System.out.println(res.getOrderIds());
            String orderidstr = res.getOrderIds();
            String ocsRefundKey = "taobao_refunds";
            if (offline == 1) {
                ocsRefundKey = "taobao_refunds_offline";
            }
            String str = OcsMethod.getInstance().get(ocsRefundKey);
            try {
                if (str != null && !"".equals(str)) {
                    boolean isreplace = OcsMethod.getInstance().replace(ocsRefundKey, res.getOrderCount() + "");
                    System.out.println("replace--->" + str + "--->" + res.getOrderCount() + "--->" + isreplace);
                }
                else {
                    boolean isadd = OcsMethod.getInstance().add(ocsRefundKey, res.getOrderCount() + "");
                    System.out.println("add--->" + ocsRefundKey + "--->" + res.getOrderCount() + "--->" + isadd);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            String[] orderids = orderidstr.split(",");
            for (int i = 0; i < orderids.length; i++) {
                // TODO gzj 这里加淘宝新加的参数
                int IsApplyTicket = 1;
                if (offline == 1) {
                    IsApplyTicket = 2;
                }
                taobaoTuipiao(orderids[i], IsApplyTicket);
            }
        }
        catch (ApiException e) {
            ExceptionUtil.writelogByException("RETURNORDERSBYLIST_ERROR", e);
        }
    }

    /**
     * 上传卧铺价格
     * 
     * @param traincode
     *            车次
     * @param tostationname
     *            到达站名字
     * @param seatprice
     *            坐席价格
     * @param fromstationname
     *            出发站名字
     * @param depdate
     *            发车时间
     * @param seattype
     *            坐席类型
     * @author fiend
     */
    public static void TrainAgentseatpriceSet(String traincode, String tostationname, String seatprice,
            String fromstationname, String depdate, String seattype) {
        WriteLog.write("2.3.12_淘宝同步卧铺价格", traincode + "@" + tostationname + "@" + seatprice + "@" + fromstationname
                + "@" + depdate + "@" + seattype);
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentseatpriceSetRequest req = new TrainAgentseatpriceSetRequest();
        AgentSeatPriceRq agentseatpricerq = new AgentSeatPriceRq();
        agentseatpricerq.setAgentId(agentid);
        agentseatpricerq.setAppKey(appkey);
        agentseatpricerq.setDepDate(depdate.substring(0, 10));
        agentseatpricerq.setFromStationName(fromstationname);
        agentseatpricerq.setMethod("");
        agentseatpricerq.setSeatPrice(seatprice);
        agentseatpricerq.setSeatType(seattype);
        try {
            String sessionUid = PropertyUtil.getValue("TaobaoSessionUid", "Train.properties");
            agentseatpricerq.setSessionNick("空铁无忧网票务");
            agentseatpricerq.setSessionUid(Long.valueOf(sessionUid));
            // agentseatpricerq.setSessionNick("trainseller04");
            // agentseatpricerq.setSessionUid(3662263834L);
        }
        catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
        agentseatpricerq.setToStationName(tostationname);
        agentseatpricerq.setTrainCode(traincode);
        agentseatpricerq.setV("");
        try {
            WriteLog.write("2.3.12_淘宝同步卧铺价格", agentseatpricerq.toString());
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        req.setAgentSeatPriceRq(agentseatpricerq);
        TrainAgentseatpriceSetResponse res;
        try {
            res = client.execute(req, sessionKey);
            WriteLog.write("2.3.12_淘宝同步卧铺价格", res.getBody());
        }
        catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public void ladan(String taobao_order_no) throws ParseException {
        if (taobaoHandleOrder(taobao_order_no)) {
            String ordermes = this.taobaoQuerOrder(taobao_order_no);
            JSONObject orderme = JSONObject.parseObject(ordermes);// 获取订单详情
            WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":orderme--->" + orderme);
            String sql = "SELECT ID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='" + taobao_order_no + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":list.size=" + list.size());
            if (list.size() < 1) {
                boolean isNeedStandingSeat = false;
                String standingSeatprice = "";
                if (orderme != null) {
                    JSONObject orderDetails = new JSONObject();
                    // orderDetails = orderDetails.parseObject(orderme);
                    orderDetails = orderme.getJSONObject("train_agent_order_get_response");
                    WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":orderDetails--->" + orderDetails);
                    if (Boolean.parseBoolean(orderDetails.get("is_success").toString()) == true)// 结果成功返回
                    {
                        String ttp_order_id = orderDetails.getString("ttp_order_id");
                        int order_type = orderDetails.getIntValue("order_type");
                        JSONObject jb = new JSONObject();
                        JSONObject jb2 = new JSONObject();
                        jb = orderDetails;// 获取json投标
                        jb2 = jb.getJSONObject("tickets");// 获取票字段
                        Trainorder trainorder = new Trainorder();// 创建订单
                        JSONArray TrainTickets = new JSONArray();
                        TrainTickets = jb2.getJSONArray("to_agent_ticket_info");// 获取票的字段转换数组

                        List<Trainpassenger> trainplist = new ArrayList<Trainpassenger>();
                        for (int i = 0; i < TrainTickets.size(); i++) {
                            List<Trainticket> Trainticklist = new ArrayList<Trainticket>();
                            Trainticket trainticket = new Trainticket();// 票
                            Trainpassenger ts = new Trainpassenger();// 客

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");

                            Date to = df.parse(TrainTickets.getJSONObject(i).getString("to_time").toString());
                            Date from = df.parse(TrainTickets.getJSONObject(i).getString("from_time").toString());
                            long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
                            long nh = 1000 * 60 * 60;// 一小时的毫秒数
                            long nm = 1000 * 60;// 一分钟的毫秒数
                            long ls = to.getTime() - from.getTime();
                            String riqi = "";

                            long hour = ls / nh;// 计算差多少小时
                            long min = ls % nh / nm;// 计算差多少分钟
                            riqi = hour + ":" + min;

                            trainticket.setStatus(Trainticket.WAITISSUE);
                            trainticket.setCosttime(riqi);
                            trainticket.setArrivaltime(dfs.format(to));// 新增

                            trainticket.setDeparttime(df.format(from));
                            trainticket.setDeparture(TrainTickets.getJSONObject(i).getString("from_station"));
                            trainticket.setArrival(TrainTickets.getJSONObject(i).getString("to_station"));// 到达

                            trainticket.setPrice(Float.parseFloat(TrainTickets.getJSONObject(i).getString(
                                    "ticket_price")) / 100);
                            trainticket.setPayprice(Float.parseFloat(TrainTickets.getJSONObject(i).getString(
                                    "ticket_price")) / 100);
                            // 淘寶票的id
                            trainticket.setInterfaceticketno(TrainTickets.getJSONObject(i).getString("sub_order_id"));
                            trainticket.setTrainno(TrainTickets.getJSONObject(i).getString("train_num"));
                            trainticket.setSeattype(getseattypes(TrainTickets.getJSONObject(i).getString("seat"),
                                    TrainTickets.getJSONObject(i).getString("train_num")));
                            if (TrainTickets.getJSONObject(i).get("seat") != null
                                    && "17".equals(TrainTickets.getJSONObject(i).getString("seat"))) {
                                isNeedStandingSeat = true;
                                standingSeatprice = trainticket.getPrice() + "";
                            }
                            trainticket.setTickettype(changeTicketTypeTaobao2DB(TrainTickets.getJSONObject(i)
                                    .getString("passenger_type")));

                            trainticket.setInsurorigprice(Float.parseFloat(TrainTickets.getJSONObject(i).getString(
                                    "insurance_price")) / 100);

                            Trainpassenger trainpassenger = new Trainpassenger();
                            trainpassenger.setBirthday(TrainTickets.getJSONObject(i).getString("birthday"));
                            trainpassenger.setIdnumber(TrainTickets.getJSONObject(i).getString("certificate_num"));
                            String certTypeValue = TrainTickets.getJSONObject(i).getString("certificate_type");
                            if ("0".equals(certTypeValue)) {
                                certTypeValue = "1";
                            }
                            else if ("1".equals(certTypeValue)) {
                                certTypeValue = "3";
                            }
                            trainpassenger.setIdtype(Integer.parseInt(certTypeValue));
                            trainpassenger.setName(TrainTickets.getJSONObject(i).getString("passenger_name"));
                            Trainticklist.add(trainticket);
                            trainpassenger.setTraintickets(Trainticklist);
                            try {
                                // 解析淘宝学生票信息
                                if (3 == trainticket.getTickettype()) {
                                    JSONObject jostudentinfo = TrainTickets.getJSONObject(i).getJSONObject(
                                            "student_info");
                                    List<TrainStudentInfo> trainstudentinfos = new ArrayList<TrainStudentInfo>();
                                    TrainStudentInfo trainstudentinfo = new TrainStudentInfo();
                                    trainstudentinfo.setClasses(jostudentinfo.getString("classes") == null ? ""
                                            : jostudentinfo.getString("classes"));
                                    trainstudentinfo.setDepartment(jostudentinfo.getString("depart_ment") == null ? ""
                                            : jostudentinfo.getString("depart_ment"));
                                    trainstudentinfo
                                            .setEductionalsystem(jostudentinfo.getString("eductional_system") == null ? ""
                                                    : jostudentinfo.getString("eductional_system"));
                                    trainstudentinfo
                                            .setEntranceyear(jostudentinfo.getString("entrance_year") == null ? ""
                                                    : jostudentinfo.getString("entrance_year"));
                                    trainstudentinfo.setFromcity(jostudentinfo.getString("from_city") == null ? ""
                                            : jostudentinfo.getString("from_city"));
                                    trainstudentinfo.setSchoolname(jostudentinfo.getString("school_name") == null ? ""
                                            : jostudentinfo.getString("school_name"));
                                    trainstudentinfo
                                            .setSchoolprovince(jostudentinfo.getString("school_province") == null ? ""
                                                    : jostudentinfo.getString("school_province"));
                                    trainstudentinfo.setStudentcard(jostudentinfo.getString("card") == null ? ""
                                            : jostudentinfo.getString("card"));
                                    trainstudentinfo.setStudentno(jostudentinfo.getString("student_no") == null ? ""
                                            : jostudentinfo.getString("student_no"));
                                    trainstudentinfo.setTocity(jostudentinfo.getString("to_city") == null ? ""
                                            : jostudentinfo.getString("to_city"));
                                    trainstudentinfo.setSchoolnamecode("");
                                    trainstudentinfo.setSchoolprovincecode("");
                                    trainstudentinfo.setFromcitycode("");
                                    trainstudentinfo.setTocitycode("");
                                    trainstudentinfo.setArg1(0l);
                                    trainstudentinfo.setArg2("");
                                    trainstudentinfo.setArg3(0l);
                                    trainstudentinfos.add(trainstudentinfo);
                                    trainpassenger.setTrainstudentinfos(trainstudentinfos);
                                }
                            }
                            catch (Exception e) {
                                ExceptionUtil.writelogByException("STUDENTS_ERROR", e);
                            }
                            trainplist.add(trainpassenger);

                        }
                        trainorder.setPassengers(trainplist);
                        trainorder.setOrderprice(Float.parseFloat(jb.get("total_price").toString()) / 100);
                        trainorder.setContacttel(jb.getString("telephone"));
                        trainorder.setAgentcontact("1");
                        trainorder.setAgentcontacttel("");
                        // trainorder.setTaobaosendid(json.getString("user_id"));//卖家ID
                        trainorder.setInsureadreess(jb.getString("address"));// 保险邮寄地址
                        trainorder.setContactuser(jb.getString("relation_name"));
                        trainorder.setQunarOrdernumber(jb.getString("main_order_id"));
                        trainorder.setOrderstatus(Trainorder.WAITPAY);
                        trainorder.setState12306(Trainorder.WAITORDER);
                        trainorder.setAgentid(getTaobaoAgetid());
                        trainorder.setInterfacetype(TrainInterfaceMethod.TAOBAO);
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        trainorder.setCreatetime(ts);
                        trainorder.setOrdertimeout(ts.valueOf(jb.getString("latest_issue_time").toString()));
                        trainorder.setCreateuser("淘宝网");
                        if (order_type == 1) {
                            trainorder.setOrdertype(4);
                        }
                        else if (order_type == 2) {// 线下票
                            trainorder.setOrdertype(2);
                        }
                        else {
                            trainorder.setOrdertype(1);
                        }
                        WriteLog.write("TaobaoHotelInterfaceUtil_AnalysisOuTtick", "jb=" + jb + ";ttp_order_id="
                                + ttp_order_id + ";isNeedStandingSeat=" + isNeedStandingSeat + ";standingSeatprice="
                                + standingSeatprice + ";taobao_order_no=" + taobao_order_no);
                        newhandleTaoBaoOrder(trainorder, jb, ttp_order_id, order_type, isNeedStandingSeat,
                                standingSeatprice, taobao_order_no, false);
                        /*
                         * trainorder =
                         * Server.getInstance().getTrainService().createTrainorder
                         * (trainorder);
                         * createTaoBaoTrainOrderNumberChange(trainorder
                         * .getId(), jb.getString("main_order_id"),
                         * ttp_order_id); if (order_type == 1) {
                         * createTaoBaoSession(trainorder); } if
                         * (isNeedStandingSeat) {
                         * createTrainOrderExtSeat(trainorder.getId(),
                         * "[{\"0\":" + standingSeatprice + "}]"); }
                         * WriteLog.write("301_TAOBAO_ORDER", taobao_order_no +
                         * ":" + trainorder.getQunarOrdernumber() + ":" +
                         * trainorder.getId()); // if (getNowTime(new Date())) {
                         * //TODO fiend2zhengju 这里原本是发订单MQ 如果是线下票 走征举逻辑
                         * activeMQroordering(trainorder.getId()); //
                         * saveTrainOrder(TrainPassenger, TrainTickets); //
                         * 代理商出票保存数据和回调 // }
                         */}
                    else // 结果是不返回
                    {
                        WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":订单详情没有成功返回");

                        this.exceptionS = "淘宝订单详情查询为false";
                    }
                }
                else {
                    this.exceptionS = "淘宝订单详情查询为空误";
                    WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":数据无结果exceptionS：淘宝订单详情查询为空误。orderme:"
                            + orderme + ":");

                }
            }
            else {
                this.exceptionS = "淘宝出票订单号重复";
                WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":淘宝出票订单号重复:list.size:" + list.size()
                        + ":taobao_order_no" + taobao_order_no);
            }
        }
        else {
            this.exceptionS = "淘宝出票订单保证下单失败";
            WriteLog.write("301_TAOBAO_ORDER", taobao_order_no + ":淘宝出票订单保证下单失败:");
        }
    }

    /**
     * 创建订单成功后 存储新旧订单号
     * 
     * @param id
     * @param oldinterfaceordernumber
     * @param newinterfaceordernumber
     * @time 2015年9月11日 下午3:23:34
     * @author fiend
     */
    private void createTaoBaoTrainOrderNumberChange(long id, String oldinterfaceordernumber,
            String newinterfaceordernumber) {
        try {
            String insertsql = "INSERT INTO TaoBaoTrainOrderNumberChange ([OrderId],[OldInterfaceOrderNumber],[NewInterfaceOrderNumber],[Remark])  VALUES("
                    + id + ",'" + oldinterfaceordernumber + "','" + newinterfaceordernumber + "','')";
            WriteLog.write("createTaoBaoTrainOrderNumberChange", insertsql);
            Server.getInstance().getSystemService().findMapResultBySql(insertsql, null);
            WriteLog.write("createTaoBaoTrainOrderNumberChange", insertsql + "--->true");
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_createTaoBaoTrainOrderNumberChange", e);
        }
    }

    private String createTaoBaoSession(Trainorder trainorder) {
        String returnMsg = "";
        TrainAgentSessionGetRequest req = new TrainAgentSessionGetRequest();
        req.setAgentId(agentid);
        req.setMainBizOrderId(Long.valueOf(trainorder.getQunarOrdernumber()));
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentSessionGetResponse res;
        try {
            res = client.execute(req, sessionKey);
            returnMsg = res.getBody();
            WriteLog.write("311_TAOBAO_Session", trainorder.getId() + ":" + trainorder.getQunarOrdernumber() + "--->"
                    + res.getBody());
            if (res.getBody() == null) {
                WriteLog.write("311_TAOBAO_Session_fail", trainorder.getId() + ":" + trainorder.getQunarOrdernumber()
                        + "--->" + "淘宝获取session：" + res.getErrorCode() + ":" + res.getMsg());
            }
            else {
                TopSessionResult session_info = res.getSessionInfo();
                List<SessionItem> list = session_info == null ? new ArrayList<SessionItem>() : session_info.getItems();
                String cookie = "";
                for (int i = 0; list != null && i < list.size(); i++) {
                    String name = list.get(i).getName();
                    String value = list.get(i).getValue();
                    cookie += name + "=" + value + "; ";
                }
                if (cookie != null && !"".equals(cookie)) {
                    cookie += "current_captcha_type=Z";
                    WriteLog.write("311_TAOBAO_Session", trainorder.getId() + ":" + trainorder.getQunarOrdernumber()
                            + "--->" + cookie);
                    insertTrainAccountSrc(trainorder, cookie, session_info.getServerIp());
                }
                else {
                    WriteLog.write("311_TAOBAO_Session", trainorder.getId() + ":" + trainorder.getQunarOrdernumber()
                            + "--->" + cookie);
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("311_TAOBAO_Session_error", trainorder.getId() + "");
            ExceptionUtil.writelogByException("311_TAOBAO_Session_error", e);
        }
        return returnMsg;
        // return ElongHotelInterfaceUtil.StringIsNull(returnMsg) ? "" :
        // returnMsg;
    }

    private void insertTrainAccountSrc(Trainorder trainorder, String cookie, String IP12306) {
        String sql = "[dbo].[sp_TrainAccountSrc_Insert_CookieAndIP] @Agentid = " + trainorder.getAgentid()
                + ", @TrainOrderId = " + trainorder.getId() + ", @Cookie = '" + cookie + "', @IP12306 = '" + IP12306
                + "'";
        try {
            WriteLog.write("insertTrainAccountSrc", sql);
            // String systemdburlString =
            // PropertyUtil.getValue("TongCheng_Service_Url",
            // "train.tongcheng.properties");
            // WriteLog.write("311_TAOBAO_Session", "systemdburlString:" +
            // systemdburlString);
            // getTrainServiceOldDB(systemdburlString).findMapResultByProcedure(sql);
            // new DBHelper().GetResultSet(sql, null);
            Server.getInstance().getSystemService().findMapResultByProcedure(sql);
        }
        catch (Exception e) {
            WriteLog.write("insertTrainAccountSrc_error", sql);
            ExceptionUtil.writelogByException("insertTrainAccountSrc_error", e);
        }
    }

    /**
     * 获取对应系统的ITrainService
     * 
     * @param systemdburlString
     * @return
     */
    public ISystemService getTrainServiceOldDB(String systemdburlString) {
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ISystemService) factory.create(ISystemService.class,
                    systemdburlString + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @time 2015年11月16日 下午2:13:31
     * @author chendong
     * @throws ApiException
     */
    public String Sendsms(String mobileNumber) throws ApiException {
        String resuleString = "false";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentMobileSendsmsRequest req = new TrainAgentMobileSendsmsRequest();
        req.setAgentId(agentid);
        req.setMobileNumber(mobileNumber);
        req.setMsgType(1L);
        TrainAgentMobileSendsmsResponse rsp = client.execute(req);
        rsp.getMsg();

        // #TODO mark#chendong
        // 这个body是调用淘宝返回的数据需要按照文档解析下格式获取到发送结果并返回
        // 淘宝接口文档地址http://open.taobao.com/doc2/apiDetail?spm=0.0.0.0.SltJzm&apiId=25890&scopeId=11180
        System.out.println(rsp.getBody());
        WriteLog.write("TrainAgentMobileSendsmsResponse", "rsp:" + rsp);
        WriteLog.write("TrainAgentMobileSendsmsResponse", "rsp:" + JSONObject.toJSONString(rsp));
        if (rsp.getIsSuccess().booleanValue() == true) {
            resuleString = "true";
        }
        return resuleString;
    }

    public String freshCookie(JSONObject json) {
        String returnMsg = "";
        WriteLog.write("freshCookie", json.toString());
        try {
            Trainorder trainorder = new Trainorder();
            trainorder.setId(json.getLongValue("orderid"));
            trainorder.setQunarOrdernumber(json.getString("interfaceOrderNumber"));
            trainorder.setAgentid(json.getLongValue("agentid"));
            returnMsg = createTaoBaoSession(trainorder);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("error_freshCookie", e);
        }
        return returnMsg;
    }

    /**
     * 新锁单逻辑
     * 
     * @param json
     * @return
     * @time 2015年11月20日 下午12:35:21
     * @author fiend
     */
    public JSONObject taobaoHandleOrderByJsp(String jsonStr) {
        JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json = JSONObject.parseObject(jsonStr);
        }
        catch (Exception e) {
            jsonObject.put("success", false);
            jsonObject.put("msg", "请求有误:>>>" + jsonStr + "<<<");
            return jsonObject;
        }
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        String taobao_order_no = json.containsKey("taobao_order") ? json.getString("taobao_order") : "";
        if ("".equals(taobao_order_no)) {
            jsonObject.put("success", false);
            jsonObject.put("msg", "淘宝订单号为空！");
            return jsonObject;
        }
        if ("".equals(orderid)) {
            jsonObject.put("success", false);
            jsonObject.put("msg", "订单ID为空！");
            return jsonObject;
        }
        if (taobaoHandleOrder(taobao_order_no)) {
            try {
                Server.getInstance().getSystemService()
                        .findMapResultByProcedure("[sp_Trainorder_Handle_Update_TimeOut] @OrderId=" + orderid);
                jsonObject.put("success", true);
                jsonObject.put("msg", "淘宝订单锁单成功！");
            }
            catch (Exception e) {
                WriteLog.write("error_taobaoHandleOrderByJsp", orderid);
                ExceptionUtil.writelogByException("error_taobaoHandleOrderByJsp", e);
                jsonObject.put("success", true);
                jsonObject.put("msg", "淘宝订单锁单成功,存儲失敗!!!");
            }
            return jsonObject;
        }
        jsonObject.put("success", false);
        jsonObject.put("msg", "淘宝订单锁单失败！");
        return jsonObject;
    }

    /**
     * 线下票快递回填
     * 
     * @param main_biz_order_id
     * @param express_id
     * @return
     * @throws Exception
     * @time 2016年1月12日 上午10:49:16
     * @author fiend
     */
    public JSONObject expressCallBack(Map map) throws Exception {
        JSONObject jsonObject = new JSONObject();

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
        TrainAgentExpressSetRequest trainAgentExpressSetRequest = new TrainAgentExpressSetRequest();
        TrainAgentExpressSetResponse res = new TrainAgentExpressSetResponse();
        trainAgentExpressSetRequest.setAgentId(agentid);
        trainAgentExpressSetRequest.setMainOrderId(Long.valueOf(map.get("orderid").toString()));
        trainAgentExpressSetRequest.setExpressId(map.get("express").toString());
        trainAgentExpressSetRequest.setMobile(map.get("mobile").toString());
        trainAgentExpressSetRequest.setAddr(map.get("add").toString());
        trainAgentExpressSetRequest.setExpressName(map.get("empressname").toString());
        res = client.execute(trainAgentExpressSetRequest, sessionKey);
        WriteLog.write("淘宝线下_邮寄回调信息",
                "main_biz_order_id:" + map.get("orderid").toString() + ",express_id:" + map.get("express").toString()
                        + ",mobile:" + map.get("mobile").toString() + ",address:" + map.get("add").toString()
                        + ",empressname:" + map.get("empressname").toString() + ",是否成功:" + res.getIsSuccess()
                        + ";body:" + res.getBody());
        boolean isSuccess = res.getIsSuccess();
        jsonObject.put("isSuccess", isSuccess);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!isSuccess) {
            jsonObject.put("errorMsgCode", res.getErrorMsgCode());
            jsonObject.put("errorMsg", res.getErrorMsg());
            jsonObject.put("extendParams", res.getExtendParams());
        }
        return jsonObject;
    }

    /**
     * 淘宝线下火车票出票回调
     * 
     * @param params
     *            guozhengju 2016-01-12
     * @return
     */
    public String taobaoOfflineDrawer(Map params, String order_id) {
        int irandom = (int) (Math.random() * 100000);
        WriteLog.write("3010_TAOBAO_TRAINORDEROFFLINE_CALLBACK_线下火车票出票回调", irandom + ":"
                + params.get("main_order_id").toString());
        try {
            TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
            TrainAgentBookticketConfirmRequest req = new TrainAgentBookticketConfirmRequest();
            TrainAgentBookticketConfirmResponse res;
            req.setCanChange(false);
            req.setTicket12306Id(params.get("ticket12306Id").toString());
            req.setAgentId(agentid);
            req.setDepDate(params.get("depDate").toString());// 发车时间
            req.setFailMsg(params.get("failMsg").toString());// 失败原因
            req.setMainOrderId(Long.parseLong(params.get("main_order_id").toString()));// 淘宝的订单号
            req.setStatus(Boolean.parseBoolean(params.get("status").toString()));// 是否出票成功
            req.setTicketNum(Long.valueOf(Long.parseLong(params.get("ticketnum").toString())));// 这个订单里有几张票
            req.setTickets(params.get("tickets").toString());// 票的详细信息
            req.setOrderType(2l);
            // req.setAlipayTradeNo("0");
            // req.setSubOrderId("0");
            res = client.execute(req, sessionKey);
            WriteLog.write("3031_TAOBAO_TRAINORDEROFFLINE_线下火车票出票回调", irandom + ":"
                    + params.get("main_order_id").toString() + "--->"
                    + net.sf.json.JSONObject.fromObject(req).toString());
            WriteLog.write("3031_TAOBAO_TRAINORDEROFFLINE_线下火车票出票回调", "res.getBody()=" + res.getBody());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            if (res.getBody() == null) {
                WriteLog.write(
                        "303_TAOBAO_TRAINORDEROFFLINE_CALLBACK_线下火车票出票回调",
                        irandom + ":" + params.get("main_order_id").toString() + "--->" + "线下淘宝回调返回错误："
                                + res.getErrorCode() + ":" + res.getMsg());
                return this.exceptionS = res.getErrorCode() + ":" + res.getMsg();
            }
            else if (res.getIsSuccess()) {
                String insert1 = "INSERT TrainOrderOfflineRecord(FKTrainOrderOfflineId,ProviderAgentid,DistributionTime,DealResult,RefundReasonStr) "
                        + " VALUES("
                        + order_id
                        + ",0,'"
                        + sdf.format(date)
                        + "',13,'"
                        + "--------出票(拒单)回调成功！--------')";
                WriteLog.write("淘宝线下票__根据出票返回信息添加操作记录", "updatesql1:" + insert1);
                getSystemServiceOldDB().findMapResultBySql(insert1, null);
                return "SUCCESS";
            }
            else if (res.getBody().contains("sub_msg")) {
                String insert1 = "INSERT TrainOrderOfflineRecord(FKTrainOrderOfflineId,ProviderAgentid,DistributionTime,DealResult,RefundReasonStr) "
                        + " VALUES("
                        + order_id
                        + ",0,'"
                        + sdf.format(date)
                        + "',113,'---出票(拒单)回调成功！---原因:"
                        + res.getSubMsg() + "')";
                WriteLog.write("淘宝线下票__根据出票返回信息添加操作记录", "updatesql1:" + insert1);
                getSystemServiceOldDB().findMapResultBySql(insert1, null);
                WriteLog.write("303_TAOBAO_TRAINORDEROFFLINE_CALLBACK_线下火车票出票回调",
                        irandom + ":" + params.get("main_order_id").toString() + "--->" + res.getBody());
                return "error";
            }
            else {
                WriteLog.write("303_TAOBAO_TRAINORDEROFFLINE_CALLBACK_线下火车票出票回调",
                        irandom + ":" + params.get("main_order_id").toString() + "--->" + res.getBody());
                return "error";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("303_TAOBAO_TRAINORDEROFFLINE_CALLBACK", irandom + ":" + "淘宝回调请求异常：" + e.toString());
            this.exceptionS = e.toString();
            return "";
        }

    }

    /**
     * 新加处理淘宝线下火车票逻辑，用于区分线上/下逻辑 guozhengju 2016-01-12
     * 
     * @param trainorder
     * @param jb
     * @param ttp_order_id
     * @param order_type
     * @param isNeedStandingSeat
     * @param standingSeatprice
     * @param taobao_order_no
     */
    public void newhandleTaoBaoOrder(Trainorder trainorder, JSONObject jb, String ttp_order_id, int order_type,
            boolean isNeedStandingSeat, String standingSeatprice, String taobao_order_no, boolean result) {
        WriteLog.write("taobao_handleTaoBaoOrder_start", "otder_type" + order_type + ";isNeedStandingSeat-->"
                + isNeedStandingSeat + ";taobao_order_no-->" + taobao_order_no);
        // 订单类型为2走线下
        if (order_type == 2) {
            WriteLog.write("TAOBAO_线下火车票_Add_ordertype=2", "进入到order_type=2;taobao_order_no=" + taobao_order_no
                    + ";order_type=" + order_type + ";jb=" + jb);
            new MyThreadTaoBaoOrderOfflineAdd(jb).start();
            // t1.start();
            // t1.stop();
            // 收件人和电话
        }
        else {
            WriteLog.write("TAOBAOTrainOffline_Add_ordertype=1", "进入到order_type=1;taobao_order_no=" + taobao_order_no
                    + ";order_type=" + order_type);
            try {
                trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
            }
            catch (Exception e) {
                WriteLog.write("taobao_handleTaoBaoOrder_exception",
                        "创建订单 trainorder=" + trainorder + ";trianorder.getId()-->" + trainorder.getId()
                                + ";trainorder.get--->" + trainorder.getOrdernumber());
            }
            createTaoBaoTrainOrderNumberChange(trainorder.getId(), jb.getString("main_order_id"), ttp_order_id);
            if (order_type == 1) {
                createTaoBaoSession(trainorder);
            }
            WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", "taobao_order_no--->" + taobao_order_no
                    + ";QunarOrdernumber--->" + trainorder.getQunarOrdernumber() + ";trainorder.getOrdernumber()--->"
                    + trainorder.getOrdernumber() + ";trainorder.getId()--->" + trainorder.getId()
                    + ";taobao_order_no--->" + taobao_order_no + ";isNeedStandingSeat--->" + isNeedStandingSeat);
            if (isNeedStandingSeat) {
                out: for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                    for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                        WriteLog.write("303_TAOBAO_ORDERSBYLIST_ORDER", "taobao_order_no--->" + taobao_order_no
                                + ";trainticket.getDeparture()-->" + trainticket.getDeparture()
                                + ";trainticket.getArrival()-->" + trainticket.getArrival()
                                + ";trainticket.getTrainno()-->" + trainticket.getTrainno()
                                + "trainticket.getDeparttime()-->" + trainticket.getDeparttime()
                                + ";trainticket.getPrice()--->" + trainticket.getPrice()
                                + ";trainticket.getSeattype()-->" + trainticket.getSeattype());
                        if (trainticket.getSeattype().contains("无座")) {
                            new TaobaoHotelInterfaceUtilNoSeatThread(result, trainticket, trainorder).start();
                            break out;
                        }
                    }
                }
                // createTrainOrderExtSeat(trainorder.getId(), "[{\"0\":" +
                // standingSeatprice + "}]");
            }
            else {
                newactiveMQroordering(result, trainorder);
            }
            // if (getNowTime(new Date())) {
            // TODO fiend2zhengju 这里原本是发订单MQ 如果是线下票 走征举逻辑
            // saveTrainOrder(TrainPassenger,
            // TrainTickets);
            // 代理商出票保存数据和回调
            //
        }
    }

    public static String callBackSuccessseao(String str_st) {
        if (str_st != null && !"".equals(str_st) && !"null".equals(str_st)) {
            String returnresult = "";
            if ("硬座".equals(str_st)) {
                returnresult = "1";
            }
            else if ("硬卧上".equals(str_st)) {
                returnresult = "2";
            }
            else if ("硬卧中".equals(str_st)) {
                returnresult = "3";
            }
            else if ("硬卧下".equals(str_st)) {
                returnresult = "4";
            }
            else if ("软座".equals(str_st)) {
                returnresult = "5";
            }
            else if ("软卧上".equals(str_st)) {
                returnresult = "6";
            }
            else if ("软卧中".equals(str_st)) {
                returnresult = "7";
            }
            else if ("软卧下".equals(str_st)) {
                returnresult = "8";
            }
            else if ("商务座".equals(str_st)) {
                returnresult = "9";
            }
            else if ("观光座".equals(str_st)) {
                returnresult = "10";
            }
            else if ("一等包座".equals(str_st)) {
                returnresult = "11";
            }
            else if ("特等座".equals(str_st)) {
                returnresult = "12";
            }
            else if ("一等座".equals(str_st)) {
                returnresult = "13";
            }
            else if ("二等座".equals(str_st)) {
                returnresult = "14";
            }
            else if ("高级软卧上".equals(str_st)) {
                returnresult = "15";
            }
            else if ("高级软卧下".equals(str_st)) {
                returnresult = "16";
            }
            else if ("动卧".equals(str_st)) {
                returnresult = "19";
            }
            return returnresult;
        }
        else {
            return "1";
        }
    }

    /**
     * 判断是否是托马斯3期，如果是，顺带把交易号拿到
     * 
     * @param orderdetail
     * @return
     * @time 2016年8月26日 上午10:43:27
     * @author fiend
     */
    private JSONObject isTomas3(JSONObject orderdetail) {
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("isTomas3", false);
        try {
            if (orderdetail.containsKey("extendParams")) {
                String extendParams = orderdetail.getString("extendParams");
                JSONObject extendParamsJsonObject = JSONObject.parseObject(extendParams);
                if (extendParamsJsonObject.containsKey("tradeNo")
                        && extendParamsJsonObject.getString("tradeNo").length() > 5) {
                    resultJsonObject.put("isTomas3", false);
                    resultJsonObject.put("tradeNo", extendParamsJsonObject.getString("tradeNo"));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resultJsonObject;
    }

    /**
     * 托马斯3期退票插入DB
     * 
     * @param taobaoOrderNumber
     * @param tradeNo
     * @param taobaoTicketNumber
     * @param user_id
     * @time 2016年8月26日 下午1:19:38
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private void insertTomas3(String taobaoOrderNumber, String tradeNo, String taobaoTicketNumber, String user_id) {
        String sql = " [sp_TaoBaoRefund_insert] @TaobaoOrderNumber='" + taobaoOrderNumber + "',@TradeNo='" + tradeNo
                + "',@TaobaoTicketNumber='" + taobaoTicketNumber + "',@user_id='" + user_id + "'";
        try {
            List list = Server.getInstance().getSystemService().findMapResultByProcedure("");
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                WriteLog.write("insertTomas3_" + map.get("result").toString(), sql);
            }
            else {
                WriteLog.write("insertTomas3", sql + "--->返回list的size为0");
            }
        }
        catch (Exception e) {
            WriteLog.write("insertTomas3_Exception", sql);
            ExceptionUtil.writelogByException("insertTomas3_Exception", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map tainOrderidTaoBao(String orderid, String failmsg) {
        StringBuffer subbuf = new StringBuffer();
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(Long.parseLong(orderid));
        if (failmsg == null || failmsg.equals("")) {
            trainorder = TaobaoTrainInsure.getTaobaoTrainInsure().insuranceSynchronous(trainorder);
        }
        try {
            Map mp = new HashMap();
            mp.put("orderId", trainorder.getId());
            // mp.put("alipayTradeNo", trainorder.getSupplytradeno());
            mp.put("main_order_id", trainorder.getQunarOrdernumber());
            //抢票
            mp.put("order_type", 3);
            StringBuffer sb = new StringBuffer();
            int num = 0;
            mp.put("status", false);
            mp.put("failMsg", failmsg);
            try {
                mp.put("alipaytradeno", trainorder.getSupplytradeno());
                mp.put("supplyaccount", trainorder.getAutounionpayurlsecond());// 添加支付账户   
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger tp = trainorder.getPassengers().get(i);
                for (int y = 0; y < tp.getTraintickets().size(); y++) {
                    Trainticket tk = tp.getTraintickets().get(y);
                    if (y == 0) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String costtime = tk.getCosttime();
                        String[] costString = costtime.split(":");
                        long hours = Long.parseLong(costString[0]);
                        long min = Long.parseLong(costString[1]);
                        long costLongtime = (hours * 60 + min) * 60 * 1000;
                        long depLongtime = df.parse(tk.getDeparttime()).getTime();
                        long arriveLongtime = depLongtime + costLongtime;
                        mp.put("arriveDate", df1.format(new Date(arriveLongtime)));
                        mp.put("depDate", df1.format(df.parse(tk.getDeparttime())));
                        mp.put("from_station_name", tk.getDeparture());
                        mp.put("to_station_name", tk.getArrival());
                    }
                    num = num + 1;
                    // 火车票订单id，单价，坐席，座位号，车次，乘车人姓名，证件名称，证件号码，保单号，保单价格
                    int x = (int) (tk.getPrice() * 100);
                    sb.append(tk.getInterfaceticketno() + ";");
                    sb.append(x + ";");
                    sb.append(CackBackSuccessseao(tk.getSeattype(), tk.getSeatno()) + ";");
                    if (tk.getSeatno() == null) {
                        sb.append("no" + ";");
                    }
                    else {
                        if ("无座".equals(tk.getSeatno())) {
                            sb.append("无座_" + tk.getCoach() + "车厢;");
                        }
                        else {
                            sb.append(tk.getSeattype() + "_" + tk.getCoach() + "_" + tk.getSeatno().toString() + ";");
                        }
                    }
                    sb.append(tk.getTrainno() + ";");
                    sb.append(tp.getName().toString() + ";");
                    String certTypeValue = tp.getIdtypestr().toString();
                    sb.append(IDnumberType(certTypeValue) + ";");
                    sb.append(tp.getIdnumber() + ";");
                    if (tk.getRealinsureno() == null || "".equals(tk.getRealinsureno())) {
                        sb.append("0;");
                        sb.append("0");
                    }
                    else {
                        sb.append(tk.getRealinsureno() + ";");
                        sb.append((int) (tk.getInsurorigprice() * 100));
                    }
                    if (trainorder.getPassengers().size() > i + 1) {
                        sb.append(",");
                    }

                    String suborderid = tk.getInterfaceticketno();
                    subbuf.append(suborderid);
                    subbuf.append(",");
                }
            }
            String temp = subbuf.toString();
            if (temp.endsWith(",")) {
                temp = temp.substring(0, temp.length() - 1);
            }
            String subOrderId = getSubOrderIdMethod(orderid);
            WriteLog.write("淘宝抢票下单1", "DB>>>>>>>>>subOrderId:" + subOrderId + "------------>>ALLSuborderId:" + temp);
            if (!ElongHotelInterfaceUtil.StringIsNull(subOrderId)) {
                mp.put("subOrderId", subOrderId);
            }
            else {
                mp.put("subOrderId", temp);
            }
            mp.put("tickets", sb);
            if (trainorder.getExtnumber() == null) {
                mp.put("ticket12306Id", "no");
            }
            else {
                mp.put("ticket12306Id", trainorder.getExtnumber().toString());
            }
            try {
                if (trainorder.getEnrefundable() == 1) {
                    mp.put("canChange", false);
                }
                else {
                    mp.put("canChange", true);
                }
            }
            catch (Exception e) {
                WriteLog.write("TAINORDERID_CANCHANGE_ERROR", orderid);
                ExceptionUtil.writelogByException("TAINORDERID_CANCHANGE_ERROR", e);
            }
            mp.put("ticketNum", num);// 火车票
            WriteLog.write("淘宝抢票下单1", "数据拼参-------------------->" + mp);
            return mp;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.exceptionS = e.toString();
            return null;
        }

    }

    public boolean getNowTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dateBefor = df.parse("06:02:00");
            Date dateAfter = df.parse("23:00:00");
            Date time = df.parse(df.format(date));
            if (time.after(dateBefor) && time.before(dateAfter)) {
                return true;
            }
        }
        catch (Exception e) {
            WriteLog.write("InterfaceTimeRuleUtil_error", "时间异常");
            ExceptionUtil.writelogByException("InterfaceTimeRuleUtil_error", e);
        }
        return false;//现在24小时,以后有需要再改为FALSE
    }
    
}
