package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.TrainorderTimeUtil;

@SuppressWarnings("serial")
public class MeituanCallBackOrderBespeak extends HttpServlet {

    /**
     * 抢票下单成功回调接口
     * @time 2015年11月3日 16:24:23
     * @author QingXin
     **/
    @SuppressWarnings("rawtypes")
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "";
        PrintWriter out = null;
        try {
            out = res.getWriter();
            String param = req.getParameter("callback");
            if (!ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject json = JSONObject.parseObject(param);
                long orderid = json.containsKey("orderid") ? json.getLongValue("orderid") : 0l;
                String interfacenumber = json.containsKey("interfacenumber") ? json.getString("interfacenumber") : "";
                String AgentId = json.containsKey("AgentId") ? json.getString("AgentId") : "";
                String Msg = json.containsKey("Msg") ? json.getString("Msg") : "";
                WriteLog.write("Q_占座回调", "orderid:" + orderid);
                Map map = new HashMap();
                if (orderid > 0) {
                    map = getcallbackurl(String.valueOf(orderid));
                }
                else {
                    map = getcallbackurlByagentid(String.valueOf(AgentId));
                }
                String url = getValueByMap(map, "C_ZHANZUOHUIDIAO");
                JSONObject jsonstr = new JSONObject();
                String reqtime = getCurrentTime();
                String partnerid = getValueByMap(map, "C_USERNAME");
                String key = getValueByMap(map, "C_KEY");
                String sign = ElongHotelInterfaceUtil.MD5(partnerid + reqtime + ElongHotelInterfaceUtil.MD5(key));
                if (json.getBooleanValue("success")) {
                    Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(orderid);
                    String transactionid = trainorder.getOrdernumber();
                    String checi = "";
                    String from_station_name = "";
                    String to_station_name = "";
                    String train_date = "";
                    String start_time = "";
                    String arrive_time = "";
                    String ordernumber = trainorder.getExtnumber();
                    String runtime = "";
                    JSONArray passengers = new JSONArray();
                    for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                        Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
                        Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                        try {
                            from_station_name = URLDecoder.decode(req.getParameter("fromCity"), "UTF-8");
                            to_station_name = URLDecoder.decode(req.getParameter("toCity"), "UTF-8");
                        }
                        catch (Exception e2) {
                            from_station_name = trainticket.getDeparture();
                            to_station_name = trainticket.getArrival();
                        }
                        train_date = trainticket.getDeparttime().split(" ")[0];
                        start_time = trainticket.getDeparttime() + ":00";
                        runtime = trainticket.getCosttime();
                        try {
                            arrive_time = TrainorderTimeUtil.getArrivalTime(start_time, runtime);
                            trainticket.setDeparture(from_station_name);
                            trainticket.setArrival(to_station_name);
                            Server.getInstance().getTrainService().updateTrainticket(trainticket);
                        }
                        catch (Exception e1) {
                        }
                        WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";from_station_name:" + from_station_name
                                + ";to_station_name:" + to_station_name);
                        checi = trainticket.getTrainno();
                        JSONObject passengerjson = new JSONObject();
                        String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger
                                .getPassengerid();
                        passengerjson.put("passengerid", passengerid);
                        passengerjson.put("ticket_no", trainticket.getTicketno());
                        String name = geturlencode(trainpassenger.getName());
                        passengerjson.put("passengersename", name);
                        passengerjson.put("passportseno", trainpassenger.getIdnumber());
                        passengerjson.put("passporttypeseid",
                                TongchengSupplyMethod.getIdtype12306(trainpassenger.getIdtype()));
                        String passporttypeseidname = geturlencode(trainpassenger.getIdtypestr());
                        passengerjson.put("passporttypeseidname", passporttypeseidname);
                        passengerjson.put("piaotype", trainticket.getTickettype() + "");
                        String piaotypename = geturlencode(trainticket.getTickettypestr() + "票");
                        passengerjson.put("piaotypename", piaotypename);
                        passengerjson.put("zwcode", TongchengSupplyMethod.getzwname(trainticket.getSeattype()));
                        String zwname = geturlencode(trainticket.getSeattype());
                        passengerjson.put("zwname", zwname);
                        try {
                            String cxin = trainticket.getCoach() + "车厢," + trainticket.getSeatno().replace('号', '座');
                            cxin = geturlencode(cxin);
                            long ticketid = trainticket.getId();
                            passengerjson.put("cxin", cxin);
                            passengerjson.put("ticketid", ticketid);
                        }
                        catch (Exception e) {
                        }
                        passengerjson.put("price", trainticket.getPrice() + "");
                        passengerjson.put("reason", trainpassenger.getAduitstatus());
                        passengers.add(passengerjson);
                    }
                    from_station_name = geturlencode(from_station_name);
                    to_station_name = geturlencode(to_station_name);
                    jsonstr.put("from_station_name", from_station_name);
                    jsonstr.put("to_station_name", to_station_name);
                    jsonstr.put("train_date", train_date);
                    jsonstr.put("start_time", start_time);
                    jsonstr.put("arrive_time", arrive_time);
                    jsonstr.put("ordernumber", ordernumber);
                    jsonstr.put("runtime", runtime);
                    jsonstr.put("checi", checi);
                    jsonstr.put("passengers", passengers);
                    jsonstr.put("transactionid", transactionid);
                    jsonstr.put("qorderid", trainorder.getQunarOrdernumber());
                    jsonstr.put("orderamount", trainorder.getOrderprice());
                    jsonstr.put("refund_online", "0");
                    jsonstr.put("reqtime", reqtime);
                    jsonstr.put("sign", sign);
                    jsonstr.put("ordersuccess", true);
                    WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";jsonStr:" + jsonstr.toJSONString());
                    try {
                        for (int i = 0; i <= 5; i++) {
                            result = SendPostandGet.submitPost(url, "data=" + jsonstr.toJSONString(), "UTF-8")
                                    .toString();
                            WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";result:" + result);
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (!jsonObject.getString("isSuccess").equalsIgnoreCase("SUCCESS")) {
                                try {
                                    Thread.sleep(30000l);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                String OldOrderid = transactionid;
                                String NewOrderid = jsonObject.get("NewOrderid").toString();
                                System.out.println("NewOrderid:   " + OldOrderid + "替换成" + NewOrderid);
                                JSONArray jsonArray = jsonObject.getJSONArray("NewPassengerid");
                                trainorder.setQunarOrdernumber(NewOrderid);
                                trainorder.setIsquestionorder(0);
                                //                                Server.getInstance().getTrainService().updateTrainorder(trainorder);
                                String sqlOrder = "update T_TRAINORDER set C_QUNARORDERNUMBER='" + NewOrderid
                                        + "',C_ISQUESTIONORDER=0 where ID =" + trainorder.getId();
                                Server.getInstance().getSystemService().findMapResultBySql(sqlOrder, null);
                                WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";OldOrderid:" + OldOrderid
                                        + ";NewOrderid:" + NewOrderid);
                                for (int k = 0; k < jsonArray.size(); k++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(k);
                                    String oldPassengerid = jsonObject2.get("oldPassengerid").toString();
                                    String newPassengerid = jsonObject2.get("newPassengerid").toString();
                                    String sql = "UPDATE T_TRAINPASSENGER SET C_PASSENGERID=" + newPassengerid
                                            + " WHERE C_ORDERID=" + orderid + " AND C_PASSENGERID=" + oldPassengerid;
                                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                                    WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";oldPassengerid:"
                                            + oldPassengerid + ";newPassengerid:" + newPassengerid);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e) {
                        WriteLog.write("ERROR_抢票占座回调", "orderid:" + orderid + ";Exception=" + e);
                    }
                }
                else {
                    try {
                        jsonstr.put("reqtime", reqtime);
                        jsonstr.put("sign", sign);
                        jsonstr.put("ordersuccess", false);
                        Map msgMap = refuseMsg2Code(Msg);
                        WriteLog.write("Q约票回调错误码", interfacenumber + "--->" + getValueByMap(msgMap, "code") + ":" + Msg);
                        //                        String msg = geturlencode(Msg);
                        jsonstr.put("msg", getValueByMap(msgMap, "msg"));
                        jsonstr.put("code", getValueByMap(msgMap, "code"));
                        jsonstr.put("qorderid", interfacenumber);
                        for (int i = 1; i <= 5; i++) {
                            WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";data=" + jsonstr.toString());
                            result = SendPostandGet.submitPost(url, "data=" + jsonstr.toString(), "UTF-8").toString();
                            WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";result:" + result);
                            JSONObject jsonObject = JSON.parseObject(result);
                            WriteLog.write("Q_抢票占座回调", "orderid:" + orderid + ";result:" + result);
                            if (!jsonObject.getString("isSuccess").equalsIgnoreCase("SUCCESS")) {
                                try {
                                    Thread.sleep(30000l);
                                }
                                catch (InterruptedException e) {
                                }
                            }
                            else {
                                break;
                            }
                        }
                    }
                    catch (Exception e) {
                        WriteLog.write("ERROR_抢票占座回调", "orderid:" + orderid + ";Exception=" + e);
                    }
                }
            }
            else {

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.print(result);
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    private static String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    /**
     * 从map中获取对应的数据
     **/
    private String getValueByMap(Map map, String key) {
        String value = "-1";
        if (map.get(key) != null) {
            try {
                value = map.get(key).toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 根据orderid获取到回调地址
     * C_PAYCALLBACKURL:出票回调
     * C_ZHANZUOHUIDIAO:占座回调
     **/
    public Map getcallbackurl(String orderid) {
        String key = "";
        Map map = new HashMap();
        String sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid;
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + sql_agentid + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 根据orderid获取到回调地址
     * C_PAYCALLBACKURL:出票回调
     * C_ZHANZUOHUIDIAO:占座回调
     **/
    public Map getcallbackurlByagentid(String AgentId) {
        String key = "";
        Map map = new HashMap();
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=" + AgentId;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return (df.format(new Date()));// new Date()为获取当前系统时间
    }

    /**
     * 错误码
     * 
     * @param res
     * @return
     * @time 2015年12月11日 上午10:08:45
     * @author fiend
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map refuseMsg2Code(String res) {
        Map map = new HashMap();
        /* if (res.indexOf("没有余票") > -1) {
             map.put("code", 301);
         }
         else*/if (res.indexOf("其他订单行") > -1 || res.indexOf("本次购票行程冲突") > -1) {
            map.put("code", 310);
        }
        else if (res.indexOf("已订") > -1 || res.indexOf("已购买") > -1) {
            map.put("code", 305);
        }
        else if (res.indexOf("身份信息涉嫌被他人冒用") > -1) {
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
            }
            map.put("code", 315);
        }
        else if (res.indexOf("身份") > -1) {
            map.put("code", 308);
        }
        else if (res.indexOf("限制高消费") > -1) {
            map.put("code", 313);
        }
        else {
            map.put("code", 999);
        }
        map.put("msg", geturlencode(res));
        return map;
    }
}
