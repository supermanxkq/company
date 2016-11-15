package com.ccservice.b2b2c.atom.servlet.MeiTuanChange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method.GetReqTokenByResignId;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;

@SuppressWarnings("serial")
public class MeiTuanRequestChangeServlet extends HttpServlet {

    private final String logname = "meituan美团_申请改签";

    private final String errorlogname = "meituan美团_申请改签_error";

    private final int r1 = new Random().nextInt(10000000);

    private TongChengReqChange tongChengReqChange;

    @Override
    public void init() throws ServletException {
        super.init();
        tongChengReqChange = new TongChengReqChange();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        String result = "";
        String param = "";
        try {
            out = resp.getWriter();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buffer = new StringBuffer(1024);
            if ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            param = buffer.toString();
            bufferedReader.close();
            WriteLog.write(logname, r1 + "-->请求参数:" + param);
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "传入的json为空对象");
                result = obj.toString();
            }
            else {
                try {
                    JSONObject json = JSONObject.parseObject(param);
                    String partnerid = json.containsKey("partnerid") ? json.getString("partnerid") : "";
                    String key = getKeyByPartnerid(partnerid);
                    //请求时间
                    String reqtime = json.containsKey("reqtime") ? json.getString("reqtime") : "";
                    //数字签名
                    String sign = json.containsKey("sign") ? json.getString("sign") : "";
                    //请求方法
                    String method = json.containsKey("method") ? json.getString("method") : "";
                    WriteLog.write(logname, r1 + ":Key:" + key);
                    key = ElongHotelInterfaceUtil.MD5(key);
                    WriteLog.write(logname, r1 + ":Key1:" + key);
                    String signflag = partnerid + GetReqTokenByResignId.Method.RESIGN_TICKET + reqtime + key;
                    WriteLog.write(logname, r1 + ":signflag:" + signflag);
                    signflag = ElongHotelInterfaceUtil.MD5(signflag);
                    WriteLog.write(logname, r1 + ":signflag1:" + signflag + "method:" + method);
                    if (signflag.equalsIgnoreCase(sign)) {
                        String from_station_code = json.containsKey("fromStationCode") ? json
                                .getString("fromStationCode") : ""; //出发站简码
                        String from_station_name = json.containsKey("fromStationName") ? json
                                .getString("fromStationName") : ""; //出发站名
                        String to_station_code = json.containsKey("toStationCode") ? json.getString("toStationCode")
                                : ""; //到达站简码
                        String to_station_name = json.containsKey("toStationName") ? json.getString("toStationName")
                                : ""; //到达站名
                        String toStationChange = json.containsKey("toStationChange") ? json
                                .getString("toStationChange") : ""; //是否变站  0不变站，默认不变站
                        String orderid = json.containsKey("orderId") ? json.getString("orderId") : ""; //使用方订单号
                        String transactionid = json.containsKey("agentOrderId") ? json.getString("agentOrderId") : ""; //交易单号
                        WriteLog.write(logname, r1 + "-->请求参数:" + "orderid-->" + orderid + "transactionid-->"
                                + transactionid);
                        Trainform trainform = new Trainform();
                        trainform.setQunarordernumber(orderid);
                        trainform.setOrdernumber(transactionid);
                        List<Trainorder> orders = Server.getInstance().getTrainService()
                                .findAllTrainorder(trainform, null);
                        WriteLog.write(logname, r1 + "-->请求参数:" + "orders.size()" + orders.size());
                        if (orders == null || orders.size() != 1) {
                            JSONObject returnJson = new JSONObject();
                            returnJson.put("code", "402");
                            returnJson.put("msg", "订单不存在");
                            returnJson.put("success", false);
                            result = returnJson.toString();
                        }
                        else {
                            Trainorder trainorder = Server.getInstance().getTrainService()
                                    .findTrainorder(orders.get(0).getId());
                            String ordernumber = trainorder.getExtnumber();
                            List<Trainpassenger> trainpassengers = trainorder.getPassengers();
                            String change_checi = json.containsKey("trainCode") ? json.getString("trainCode") : ""; //车次
                            String change_datetime = json.containsKey("trainDate") ? json.getString("trainDate") : "";
                            change_datetime += " 00:00:00"; //出发日期，美团传进来没有时分秒，我们接口需要
                            JSONArray tickets = json.containsKey("tickets") ? json.getJSONArray("tickets")
                                    : new JSONArray(); //改签车票信息
                            String ticketNo = tickets.getJSONObject(0).getString("ticketNo");
                            String change_zwcode = tickets.getJSONObject(0).getString("seatType");
                            String htchange_zwcode = PublicMTMethod.getMTcode(change_zwcode);
                            String old_zwcode = getOld_zwcode(getOldSeatType(ticketNo));
                            String callbackurl = json.containsKey("callBackURL") ? json.getString("callBackURL") : "";
                            String reqtoken = json.containsKey("resignId") ? json.getString("resignId") : "";
                            JSONArray ticketinfo = new JSONArray();
                            for (int i = 0; i < tickets.size(); i++) {
                                JSONObject ticket = new JSONObject();
                                long ticketId = tickets.getJSONObject(i).getLong("ticketId");
                                String passengersename = tickets.getJSONObject(i).getString("passengerName");
                                String passportseno = tickets.getJSONObject(i).getString("certificateNo");
                                int passporttypeseid = 2;
                                String old_ticket_no = tickets.getJSONObject(i).getString("ticketNo");
                                int piaotype = changePiaoType(tickets.getJSONObject(i).getInteger("ticketType"));
                                for (int j = 0; j < trainpassengers.size(); j++) {
                                    Trainpassenger trainpassenger = trainpassengers.get(j);
                                    String name = trainpassenger.getName();
                                    String Idnumber = trainpassenger.getIdnumber();
                                    Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                                    if (passengersename.equals(name)
                                            && passportseno.toUpperCase().equals(Idnumber.toUpperCase())
                                            && piaotype == trainticket.getTickettype()) {
                                        passporttypeseid = trainpassenger.getIdtype();
                                        WriteLog.write(logname, r1 + "-->请求参数:" + "passporttypeseid-->"
                                                + passporttypeseid + "trainticket-->" + trainticket.getId()
                                                + "ticketId-->>" + ticketId);
                                        updateTicketById(trainticket.getId(), ticketId + "");
                                    }
                                }
                                ticket.put("passengersename", passengersename);
                                ticket.put("passportseno", passportseno);
                                ticket.put("passporttypeseid", passporttypeseid);
                                ticket.put("old_ticket_no", old_ticket_no);
                                ticket.put("piaotype", piaotype);
                                WriteLog.write(logname, r1 + "-->请求参数:" + "ticket--->" + ticket.toString());
                                ticketinfo.add(ticket);
                            }
                            int toStationChangeInt = 0;//0不变站，默认不变站
                            try {
                                toStationChangeInt = Integer.valueOf(toStationChange);
                            }
                            catch (Exception e) {
                            }
                            boolean isTs = toStationChangeInt == 1;
                            //                            boolean isTs = isStationChange(ticketNo, to_station_name);
                            JSONObject jsonStr = new JSONObject();
                            jsonStr.put("from_station_code", from_station_code);
                            jsonStr.put("from_station_name", from_station_name);
                            jsonStr.put("to_station_code", to_station_code);
                            jsonStr.put("to_station_name", to_station_name);
                            jsonStr.put("orderid", orderid);
                            jsonStr.put("transactionid", transactionid);
                            jsonStr.put("ordernumber", ordernumber);
                            jsonStr.put("change_checi", change_checi);
                            jsonStr.put("change_datetime", change_datetime);
                            jsonStr.put("change_zwcode", htchange_zwcode);
                            jsonStr.put("old_zwcode", old_zwcode);
                            jsonStr.put("ticketinfo", ticketinfo);
                            jsonStr.put("isasync", "Y");
                            jsonStr.put("callbackurl", callbackurl);
                            jsonStr.put("reqtoken", reqtoken);
                            jsonStr.put("isTs", isTs);
                            WriteLog.write(logname, r1 + "-->请求参数:--->" + jsonStr.toJSONString());
                            result = tongChengReqChange.operate(jsonStr); //调用我们的接口
                            JSONObject resJon = new JSONObject();
                            resJon = JSONObject.parseObject(result);
                            WriteLog.write(logname, r1 + "-->resJon:" + resJon);
                            String msg = resJon.containsKey("msg") ? resJon.getString("msg") : "";
                            if (resJon.containsKey("success") && resJon.getBoolean("success")) {
                                JSONObject returnJson = new JSONObject();
                                returnJson.put("msg", msg);
                                returnJson.put("code", "802");
                                returnJson.put("success", true);
                                result = returnJson.toString();
                            }
                            else {
                                JSONObject returnJson = new JSONObject();
                                returnJson.put("msg", msg);
                                returnJson.put("code", "803");
                                returnJson.put("success", false);
                                result = returnJson.toString();
                            }
                        }
                    }
                    else {
                        WriteLog.write(logname, r1 + ":jsonStr:" + json);
                        JSONObject obj = new JSONObject();
                        obj.put("success", false);
                        obj.put("code", "105");
                        obj.put("msg", "签名错误");
                        result = obj.toString();
                    }
                }
                catch (Exception e) {
                    WriteLog.write(errorlogname, r1 + ":error:" + e);
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    obj.put("code", "113");
                    obj.put("msg", "系统错误");
                    result = obj.toString();
                }
            }
        }
        catch (Exception e) {
            WriteLog.write(errorlogname, r1 + ":error:" + e);
            JSONObject obj = new JSONObject();
            obj.put("success", false);
            obj.put("code", "113");
            obj.put("msg", "系统错误");
            result = obj.toString();
        }
        finally {
            if (out != null) {
                WriteLog.write(logname, r1 + ":reslut:" + result);
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 转换车票类型
     * @param piaotype
     * @return
     */
    public int changePiaoType(int piaotype) {
        if (piaotype == 3) { //儿童
            return 2;
        }
        else if (piaotype == 2) {//学生
            return 3;
        }
        else {
            return 1;
        }

    }

    public String getOld_zwcode(String oldSeatType) {
        if ("商务座".equals(oldSeatType)) {
            return "9";
        }
        else if ("特等座".equals(oldSeatType)) {
            return "P";
        }
        else if ("一等座".equals(oldSeatType)) {
            return "M";
        }
        else if ("二等座".equals(oldSeatType)) {
            return "O";
        }
        else if ("高级软卧".equals(oldSeatType)) {
            return "6";
        }
        else if ("软卧".equals(oldSeatType)) {
            return "4";
        }
        else if ("硬卧".equals(oldSeatType)) {
            return "3";
        }
        else if ("软座".equals(oldSeatType)) {
            return "2";
        }
        else if ("硬座".equals(oldSeatType)) {
            return "1";
        }
        else if ("一等软座".equals(oldSeatType)) {
            return "7";
        }
        else if ("二等软座".equals(oldSeatType)) {
            return "8";
        }
        else if ("动卧".equals(oldSeatType)) {
            return "F";
        }
        else if ("高级动卧".equals(oldSeatType)) {
            return "A";
        }
        return "";
    }

    /**
     * 拿老座位席别
     * @param ticketNo
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getOldSeatType(String ticketNo) {
        String seatType = "";
        String sql = "SELECT C_SEATTYPE FROM T_TRAINTICKET WITH (NOLOCK) WHERE C_TICKETNO = '" + ticketNo + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            seatType = map.get("C_SEATTYPE") != null ? map.get("C_SEATTYPE").toString() : "";
        }
        return seatType;
    }

    /**
     * 获取证件类型
     * @param idnumber
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getIdtypeByIdnumber(String idnumber) {
        String idType = "";
        String sql = "SELECT C_IDTYPE FROM T_TRAINPASSENGER WITH (NOLOCK) WHERE C_IDNUMBER = '" + idnumber + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            idType = map.get("C_IDTYPE") != null ? map.get("C_IDTYPE").toString() : "";
        }
        return idType;
    }

    /**
     * 是否变站
     * @param ticketNo
     * @param toStationName
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean isStationChange(String ticketNo, String toStationName) {
        boolean isTs = false;
        String sql = "SELECT C_ARRIVAL FROM T_TRAINTICKET WITH (NOLOCK) WHERE C_TICKETNO = '" + ticketNo + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            String oldToStation = map.get("C_ARRIVAL") != null ? map.get("C_ARRIVAL").toString() : "";
            if (!toStationName.equals(oldToStation)) {
                isTs = true;
            }
        }
        return isTs;
    }

    /**
     * 获取美团KEY
     * @param partnerid
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getKeyByPartnerid(String partnerid) {
        String key = "";
        String sql = "SELECT C_KEY FROM T_INTERFACEACCOUNT WHERE C_USERNAME = '" + partnerid + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            key = map.get("C_KEY") != null ? map.get("C_KEY").toString() : "";
        }
        return key;
    }

    /**
     * 获取交易单号或12306订单号
     * @param orderid
     * @param i
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getExtnumber(String orderid, String transactionid) {
        String extnumber = "";
        String sql = "SELECT C_EXTNUMBER FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER = '" + orderid
                + "' AND C_ORDERNUMBER ='" + transactionid + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            extnumber = map.get("C_EXTNUMBER") != null ? map.get("C_EXTNUMBER").toString() : "";
        }
        return extnumber;
    }

    /**
     * 根据票的id  更新C_INTERFACETICKETNO
     * @param id
     * @param interfaceTicketno
     */
    public static void updateTicketById(long id, String interfaceTicketno) {
        try {
            String sql = " update T_TRAINTICKET set C_INTERFACETICKETNO = '" + interfaceTicketno + "' where ID=" + id;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
        }
    }

    public static void main(String[] args) {

        //	    String orderid="2179218834105344";
        String transactionid = "T160810DE7F12CA0409604E0B0938C0312847DF09F6";
        Trainform trainform = new Trainform();
        //        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(orders.get(0).getId());

        System.out.println(trainorder.getExtnumber());
    }
}
