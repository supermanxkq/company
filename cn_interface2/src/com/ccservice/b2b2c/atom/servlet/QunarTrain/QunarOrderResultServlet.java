package com.ccservice.b2b2c.atom.servlet.QunarTrain;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.db.DBHelperBespeak;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

public class QunarOrderResultServlet extends HttpServlet {

    private final String logname = "qunar去哪_出票结果接口";

    private final String errorlogname = "qunar去哪_出票结果接口_error";

    private final int random = new Random().nextInt();

    private static String callbackUrl = "http://api.pub.train.qunar.com/api/rob/ProcessRobPurchase.do";

    private static String merchantCode = "hangt";

    private static String key = "0C13D7C3566147EB90D1E273278DCDD9";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        JSONObject obj = new JSONObject();
        PrintWriter out = null;
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        JSONObject resultJson = new JSONObject();
        try {
            out = res.getWriter();

            String orderNo = req.getParameter("orderNo") != null ? req.getParameter("orderNo") : "";
            if (ElongHotelInterfaceUtil.StringIsNull(orderNo)) {
                orderNo = req.getParameter("orderid") != null ? req.getParameter("orderid") : "";
            }
            String opt = req.getParameter("opt") != null ? req.getParameter("opt") : "";
            if (ElongHotelInterfaceUtil.StringIsNull(orderNo)) {
                resultJson.put("success", false);
                resultJson.put("msg", "orderNO为空");
            }
            else {
                Map map = getCallbackMsg(); // 获取key，callbackUrl，merchantCode
                if (ElongHotelInterfaceUtil.StringIsNull(merchantCode)
                        || ElongHotelInterfaceUtil.StringIsNull(callbackUrl)
                        || ElongHotelInterfaceUtil.StringIsNull(key)) {
                    resultJson.put("success", false);
                    resultJson.put("msg", "未能获取merchantCode或callbackUrl");
                    return;
                }
                String getQunarMsg = "";
                if ("PAYING".equals(opt)) {
                    String interfaceNumber = getInterfaceNumber(orderNo);
                    String comment = "支付中,请稍等";
                    String hmac = ElongHotelInterfaceUtil.MD5(key + merchantCode + interfaceNumber + opt + comment)
                            .toUpperCase();
                    String param = "merchantCode=" + merchantCode + "&orderNo=" + interfaceNumber + "&opt=" + opt
                            + "&comment=" + URLEncoder.encode(comment, "UTF-8") + "&HMAC=" + hmac;
                    getQunarMsg = formatResult(param, callbackUrl);
                    resultJson = JSONObject.parseObject(getQunarMsg);
                }
                else if ("CONFIRM".equals(opt)) {
                    String interfaceNumber = getInterfaceNumber(orderNo);
                    String comment = "出票成功";
                    JSONObject resultJsonObject = formatConfirm(orderNo);
                    if (resultJsonObject.isEmpty()) {
                        resultJson.put("success", false);
                        resultJson.put("msg", "未找到订单");
                        return;
                    }
                    JSONObject result = resultJsonObject.getJSONObject("result");
                    JSONObject encodeResult = resultJsonObject.getJSONObject("encodeResult");
                    String hmac = ElongHotelInterfaceUtil.MD5(
                            key + merchantCode + interfaceNumber + opt + result + comment).toUpperCase();
                    String param = "merchantCode=" + merchantCode + "&orderNo=" + interfaceNumber + "&opt=" + opt
                            + "&result=" + encodeResult + "&comment=" + URLEncoder.encode(comment, "UTF-8") + "&HMAC="
                            + hmac;
                    getQunarMsg = formatResult(param, callbackUrl);
                    resultJson = JSONObject.parseObject(getQunarMsg);
                    if (resultJson.containsKey("success") && resultJson.getBooleanValue("success")) {
                        trainorderSuccess(orderNo);
                    }
                }
                else if ("NO_TICKET".equals(opt)) {
                    String comment = "出票失败";
                    JSONObject jsonObject = formatNoticket(orderNo);
                    if (jsonObject.isEmpty()) {
                        resultJson.put("success", false);
                        resultJson.put("msg", "未找到订单");
                        return;
                    }
                    int reason = jsonObject.getIntValue("reason");
                    String interfaceNumber = jsonObject.getString("InterfaceOrderNumber");
                    JSONArray passengerReason = jsonObject.getJSONArray("passengerReason");
                    JSONArray encodePassengerReason = jsonObject.getJSONArray("encodePassengerReason");
                    String hmac = ElongHotelInterfaceUtil.MD5(
                            key + merchantCode + interfaceNumber + opt + reason
                                    + (passengerReason.isEmpty() ? "" : passengerReason.toString()) + comment)
                            .toUpperCase();
                    String param = "merchantCode="
                            + merchantCode
                            + "&orderNo="
                            + interfaceNumber
                            + "&opt="
                            + opt
                            + "&reason="
                            + reason
                            + (encodePassengerReason.isEmpty() ? "" : "&passengerReason="
                                    + encodePassengerReason.toString()) + "&comment="
                            + URLEncoder.encode(comment, "UTF-8") + "&HMAC=" + hmac;
                    getQunarMsg = formatResult(param, callbackUrl);
                    resultJson = JSONObject.parseObject(getQunarMsg);
                }
                else {
                    resultJson.put("success", false);
                    resultJson.put("msg", "opt错误");
                }
            }
        }
        catch (Exception e) {
            WriteLog.write(errorlogname, random + ":" + e);
            resultJson.put("success", false);
            resultJson.put("msg", "系统错误");
        }
        finally {
            WriteLog.write(logname, random + ":" + resultJson.toString());
            out.print(resultJson.toString());
            out.flush();
            out.close();
        }
    }

    /**
     * 通過訂單ID查找接口訂單號
     * 
     * @param id
     * @return
     * @time 2016年7月12日 下午3:37:25
     * @author fiend
     */
    private String getInterfaceNumber(String id) {
        String interfaceNumber = "";
        String sql = "SELECT C_QUNARORDERNUMBER from T_TRAINORDER with (nolock) where ID=" + id;
        try {
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                interfaceNumber = map.get("C_QUNARORDERNUMBER").toString();
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return interfaceNumber;
    }

    /**
     * 拿callback_url和代理商ID
     * 
     * @param orderid
     * @return
     */
    private Map getCallbackMsg() {
        Map map = new HashMap();
        String sql = "SELECT C_KEY,C_PAYCALLBACKURL,C_INTERFACETYPE FROM T_INTERFACEACCOUNT WHERE C_USERNAME ='qunar_bespeak'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    private JSONObject formatNoticket(String id) throws UnsupportedEncodingException {
        JSONObject resultJsonObject = new JSONObject();
        String sql = "exec [sp_TrainOrderBespeak_Select_RefundMsg] @OrderId=" + id;
        DataTable dataTable = DBHelperBespeak.GetDataTable(sql);
        JSONArray passengerReason = new JSONArray();
        JSONArray encodePassengerReason = new JSONArray();
        int reason = 0;
        String InterfaceOrderNumber = "";
        for (DataRow datarow : dataTable.GetRow()) {
            String RefundMsg = datarow.GetColumnString("RefundMsg");
            int TicketType = datarow.GetColumnInt("TicketType");
            int IdType = datarow.GetColumnInt("IdType");
            String IdNumber = datarow.GetColumnString("IdNumber");
            String Name = datarow.GetColumnString("Name");
            InterfaceOrderNumber = datarow.GetColumnString("InterfaceOrderNumber");
            if (RefundMsg.contains("_身份信息涉嫌被他人冒用")) {
                reason = 7;
                String refundMsgName = RefundMsg.split("_身份信息涉嫌被他人冒用")[0];
                if (Name.equals(refundMsgName)) {
                    JSONObject passJsonObject = new JSONObject();
                    JSONObject encodePassJsonObject = new JSONObject();
                    passJsonObject.put("certNo", IdNumber);
                    passJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    passJsonObject.put("name", Name);
                    passJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    passJsonObject.put("reason", reason);
                    passengerReason.add(passJsonObject);
                    encodePassJsonObject.put("certNo", IdNumber);
                    encodePassJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    encodePassJsonObject.put("name", URLEncoder.encode(Name, "UTF-8"));
                    encodePassJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    encodePassJsonObject.put("reason", reason);
                    encodePassengerReason.add(encodePassJsonObject);
                }
            }
            else if (RefundMsg.contains("已订")) {
                reason = 2;
                String refundMsgName = RefundMsg.split("_身份信息涉嫌被他人冒用")[0].split("(二代身份证")[0].split("出票失败，")[1];
                if (Name.equals(refundMsgName)) {
                    JSONObject passJsonObject = new JSONObject();
                    JSONObject encodePassJsonObject = new JSONObject();
                    passJsonObject.put("certNo", IdNumber);
                    passJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    passJsonObject.put("name", Name);
                    passJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    passJsonObject.put("reason", reason);
                    passengerReason.add(passJsonObject);
                    encodePassJsonObject.put("certNo", IdNumber);
                    encodePassJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    encodePassJsonObject.put("name", URLEncoder.encode(Name, "UTF-8"));
                    encodePassJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    encodePassJsonObject.put("reason", reason);
                    encodePassengerReason.add(encodePassJsonObject);
                }
            }
            else if (RefundMsg.contains("添加乘客 未通过身份效验 ")) {
                reason = 6;
                String refundMsgName = RefundMsg.split("添加乘客 未通过身份效验 ")[1];
                if (refundMsgName.contains(Name)) {
                    JSONObject passJsonObject = new JSONObject();
                    JSONObject encodePassJsonObject = new JSONObject();
                    passJsonObject.put("certNo", IdNumber);
                    passJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    passJsonObject.put("name", Name);
                    passJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    passJsonObject.put("reason", reason);
                    passengerReason.add(passJsonObject);
                    encodePassJsonObject.put("certNo", IdNumber);
                    encodePassJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    encodePassJsonObject.put("name", URLEncoder.encode(Name, "UTF-8"));
                    encodePassJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    encodePassJsonObject.put("reason", reason);
                    encodePassengerReason.add(encodePassJsonObject);
                }
            }
            else if (RefundMsg.contains("未通过身份效验")) {
                reason = 6;
                String refundMsgName = RefundMsg.split("未通过身份效验")[0];
                if (refundMsgName.contains(Name)) {
                    JSONObject passJsonObject = new JSONObject();
                    JSONObject encodePassJsonObject = new JSONObject();
                    passJsonObject.put("certNo", IdNumber);
                    passJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    passJsonObject.put("name", Name);
                    passJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    passJsonObject.put("reason", reason);
                    passengerReason.add(passJsonObject);
                    encodePassJsonObject.put("certNo", IdNumber);
                    encodePassJsonObject.put("certType", getIdTypeDB2Qunar(IdType));
                    encodePassJsonObject.put("name", URLEncoder.encode(Name, "UTF-8"));
                    encodePassJsonObject.put("ticketType", getTicketTypeDB2Qunar(TicketType));
                    encodePassJsonObject.put("reason", reason);
                    encodePassengerReason.add(encodePassJsonObject);
                }
            }
            else {
                reason = 0;
            }
        }
        resultJsonObject.put("reason", reason);
        resultJsonObject.put("InterfaceOrderNumber", InterfaceOrderNumber);
        resultJsonObject.put("passengerReason", passengerReason);
        resultJsonObject.put("encodePassengerReason", encodePassengerReason);
        return resultJsonObject;
    }

    private JSONObject formatConfirm(String id) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        JSONObject result = new JSONObject();
        JSONObject encodeResult = new JSONObject();
        JSONArray passengers = new JSONArray();
        JSONArray encodepassengers = new JSONArray();
        String count = "";
        String ticketNo = "";
        String trainNo = "";
        String trainpid = "";
        String passengerName = "";
        String price = "";
        String seatNo = "";
        String seatTypeName = "";

        Map ordermap = new HashMap();
        String osql = "SELECT C_EXTNUMBER,C_TICKETCOUNT FROM T_TRAINORDER WHERE ID=" + id;
        List olist = Server.getInstance().getSystemService().findMapResultBySql(osql, null);
        if (olist.size() > 0) {
            ordermap = (Map) olist.get(0);
            count = ordermap.get("C_TICKETCOUNT") == null ? "" : ordermap.get("C_TICKETCOUNT").toString();
            ticketNo = ordermap.get("C_EXTNUMBER") == null ? "" : ordermap.get("C_EXTNUMBER").toString();
        }
        else {
            return jsonObject;
        }

        Map passengerMap = new HashMap();
        String psql = "SELECT ID,C_NAME,C_IDTYPE FROM T_TRAINPASSENGER WHERE C_ORDERID = " + id;
        List plist = Server.getInstance().getSystemService().findMapResultBySql(psql, null);
        String departure = "";
        String arrival = "";
        String encodedeparture = "";
        String encodearrival = "";
        String departtime = "";
        if (plist.size() > 0) {
            for (int i = 0; i < plist.size(); i++) {
                JSONObject json = new JSONObject();
                JSONObject encodejson = new JSONObject();
                passengerMap = (Map) plist.get(i);
                trainpid = passengerMap.get("ID").toString();
                passengerName = passengerMap.get("C_NAME").toString();
                Map ticketMap = new HashMap();
                String tsql = "SELECT C_PRICE,C_TRAINNO,C_SEATNO,C_COACH,C_SEATTYPE,C_TICKETTYPE,C_DEPARTTIME,C_DEPARTURE,C_ARRIVAL FROM T_TRAINTICKET WHERE C_TRAINPID ='"
                        + trainpid + "'";
                List tlist = Server.getInstance().getSystemService().findMapResultBySql(tsql, null);
                if (tlist.size() > 0) {
                    ticketMap = (Map) tlist.get(0);
                    trainNo = ticketMap.get("C_TRAINNO") == null ? "" : ticketMap.get("C_TRAINNO").toString();
                    departure = ticketMap.get("C_DEPARTURE") == null ? "" : ticketMap.get("C_DEPARTURE").toString();
                    arrival = ticketMap.get("C_ARRIVAL") == null ? "" : ticketMap.get("C_ARRIVAL").toString();
                    encodedeparture = URLEncoder.encode(departure, "UTF-8");
                    encodearrival = URLEncoder.encode(arrival, "UTF-8");
                    departtime = ticketMap.get("C_DEPARTTIME") == null ? "" : ticketMap.get("C_DEPARTTIME").toString();
                    price = ticketMap.get("C_PRICE") == null ? "" : ticketMap.get("C_PRICE").toString();
                    String seatNumber = ticketMap.get("C_SEATNO") == null ? "" : ticketMap.get("C_SEATNO").toString();
                    String seatType = ticketMap.get("C_SEATTYPE") == null ? "" : ticketMap.get("C_SEATTYPE").toString();
                    String coach = ticketMap.get("C_COACH") == null ? "" : ticketMap.get("C_COACH").toString();
                    String ticketType = getTicketTypeDB2Qunar(ticketMap.get("C_TICKETTYPE") == null ? 1 : Integer
                            .valueOf(ticketMap.get("C_TICKETTYPE").toString()));
                    if (seatNumber.contains("上")) {
                        seatTypeName = seatType + "上";
                    }
                    else if (seatNumber.contains("中")) {
                        seatTypeName = seatType + "中";
                    }
                    else if (seatNumber.contains("下")) {
                        seatTypeName = seatType + "下";
                    }
                    else {
                        seatTypeName = seatType;
                    }
                    if (seatNumber.equals("无座")) {
                        seatNo = coach + "车无座";
                        seatTypeName="无座";
                    }else{
                        seatNo = coach + "车" + seatNumber.split("号")[0] + "号";
                    }
                    json.put("ticketNo", ticketNo);
                    json.put("passengerName", passengerName);
                    json.put("price", price);
                    json.put("ticketType", ticketType);
                    json.put("seatTypeName", seatTypeName);
                    json.put("seatNo", seatNo);
                    passengers.add(json);
                    //encode
                    encodejson.put("ticketNo", ticketNo);
                    encodejson.put("passengerName", URLEncoder.encode(passengerName, "UTF-8"));
                    encodejson.put("price", price);
                    encodejson.put("ticketType", ticketType);
                    encodejson.put("seatTypeName", URLEncoder.encode(seatTypeName, "UTF-8"));
                    encodejson.put("seatNo", URLEncoder.encode(seatNo, "UTF-8"));
                    encodepassengers.add(encodejson);
                }
            }
            result.put("count", count);
            result.put("trainNo", trainNo);
            result.put("tickets", passengers);
            result.put("depStation", departure);
            result.put("arrStation", arrival);
            encodeResult.put("count", count);
            encodeResult.put("trainNo", trainNo);
            encodeResult.put("tickets", encodepassengers);
            encodeResult.put("depStation", encodedeparture);
            encodeResult.put("arrStation", encodearrival);
            jsonObject.put("encodeResult", encodeResult);
            jsonObject.put("result", result);
        }
        else {
            return jsonObject;
        }
        return jsonObject;
    }

    /**
     * 拼接返回系统结果
     * 
     * @param prama
     * @param callbackUrl
     * @return
     */
    public String formatResult(String prama, String callbackUrl) {
        WriteLog.write(logname, random + "-->请求去哪儿参数:" + prama + "-->地址:" + callbackUrl);
        JSONObject resultJson = new JSONObject();
        JSONObject istrue = new JSONObject();
        String result = sendQunarMsg(prama, callbackUrl);
        if (ElongHotelInterfaceUtil.StringIsNull(result)) { // 如果没有接收到回应，循环调5次，每次睡10s
            for (int i = 0; i < 5; i++) {
                result = sendQunarMsg(prama, callbackUrl);
                if (!ElongHotelInterfaceUtil.StringIsNull(result)) {
                    break;
                }
                try {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e) {
                }
            }
        }
        if (!ElongHotelInterfaceUtil.StringIsNull(result)) {
            istrue = JSONObject.parseObject(result);
            if (istrue.getBoolean("ret")) {
                resultJson.put("success", true);
            }
            else {
                resultJson.put("success", false);
                resultJson.put("msg", istrue.getString("errMsg"));
            }
        }
        else {
            resultJson.put("success", false);
            resultJson.put("msg", "请求去哪儿无响应");
        }
        return resultJson.toString();
    }

    /**
     * 调去哪儿接口
     * 
     * @param prama
     * @param callbackUrl
     * @return
     */
    public String sendQunarMsg(String prama, String callbackUrl) {
        StringBuffer result = new StringBuffer();
        result = SendPostandGet.submitPost(callbackUrl, prama, "utf-8");
        if (result == null) {
            result = new StringBuffer();
        }
        return result.toString();
    }

    /**
     * DB票类型转qunar票类型
     * 
     * @param dbTicketType
     * @return
     * @time 2016年7月8日 下午4:54:41
     * @author fiend
     */
    private String getTicketTypeDB2Qunar(int dbTicketType) {
        if (dbTicketType == 2) {
            return "0";
        }
        if (dbTicketType == 1) {
            return "1";
        }
        if (dbTicketType == 3) {
            return "2";
        }
        return "1";
    }

    /**
     * DB证件类型转qunar证件类型
     * 
     * @param dbIDType
     * @return
     * @time 2016年7月8日 下午4:54:41
     * @author fiend
     */
    private String getIdTypeDB2Qunar(int dbIDType) {
        if (dbIDType == 3) {
            return "B";
        }
        if (dbIDType == 5) {
            return "G";
        }
        if (dbIDType == 4) {
            return "C";
        }
        return "1";
    }

    public static void main(String[] args) {
        System.out
                .println(SendPostandGet.submitGet(
                        "http://121.41.35.117:19222/cn_interface/qunarOrderResultServlet?orderNo=31750439&opt=PAYING",
                        "UTF-8"));
    }

    private void trainorderSuccess(String orderId) {
        String sql = "UPDATE T_TRAINORDER SET C_ORDERSTATUS=3 WHERE ID=" + orderId;
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String sql1 = "UPDATE T_TRAINTICKET SET C_STATUS=3 WHERE C_TRAINPID IN (SELECT ID FROM T_TRAINPASSENGER WITH (NOLOCK) WHERE C_ORDERID ="
                + orderId + ")";
        Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
    }
}
