package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 途牛催退款接口
 * @author fantao
 */
@SuppressWarnings("serial")
public class TuNiuTrainRefundNoticeServlet extends HttpServlet {

    private final String logname = "tuniu途牛_催退款接口";

    private final String errorlogname = "tuniu途牛_催退款接口_error";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int random = new Random().nextInt();
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        JSONObject rusultJsonObject = new JSONObject();
        try {
            out = resp.getWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }

            String param = buf.toString();
            JSONObject paramoJsonObject = new JSONObject();
            WriteLog.write(logname, random + "-->请求参数:" + param);
            // 解密
            try {
                paramoJsonObject = JSONObject.parseObject(param);
                if (paramoJsonObject.containsKey("data") && !paramoJsonObject.getString("data").isEmpty()) {
                    String data = paramoJsonObject.getString("data");
                    data = TuNiuDesUtil.decrypt(data);
                    JSONObject dataJsonObject = JSONObject.parseObject(data);
                    paramoJsonObject.put("data", dataJsonObject);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(errorlogname, e);
                rusultJsonObject.put("success", false);
                rusultJsonObject.put("returnCode", "2001");
                rusultJsonObject.put("errorMsg", "解密错误");
                rusultJsonObject.put("data", "");
                return;
            }
            WriteLog.write(logname, random + "请求参数(解密后):" + paramoJsonObject.toString());
            if (!ElongHotelInterfaceUtil.StringIsNull(paramoJsonObject.toString())) {
                JSONObject dataJson = paramoJsonObject.getJSONObject("data");
                JSONObject data = getRefundMsg(dataJson.toString());
                if (!ElongHotelInterfaceUtil.StringIsNull(data.toString())) {
                    WriteLog.write(logname, random + "--->" + data.toString());
                    rusultJsonObject.put("success", true);
                    rusultJsonObject.put("returnCode", "231000");
                    rusultJsonObject.put("errorMsg", "");
                    rusultJsonObject.put("data", data);
                    return;
                }
                else {
                    rusultJsonObject.put("success", false);
                    rusultJsonObject.put("returnCode", "2001");
                    rusultJsonObject.put("errorMsg", "催退款失败");
                    rusultJsonObject.put("data", "");
                    return;
                }

            }
            else {
                rusultJsonObject.put("success", false);
                rusultJsonObject.put("returnCode", "2001");
                rusultJsonObject.put("errorMsg", "param error");
                rusultJsonObject.put("data", "");
                return;
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(errorlogname, e);
            rusultJsonObject.put("success", false);
            rusultJsonObject.put("returnCode", "2001");
            rusultJsonObject.put("errorMsg", "催退款失败");
            rusultJsonObject.put("data", "");
            return;
        }
        finally {
            //记录日志
            WriteLog.write(logname, random + "--->" + "返回参数:" + rusultJsonObject.toString());
            out.print(rusultJsonObject.toString());
            out.flush();
            out.close();
        }
    }

    public JSONObject getRefundMsg(String ticketsMsg) {
        JSONObject data = new JSONObject();

        try {
            JSONObject ticketsMsgJsonObject = JSONObject.parseObject(ticketsMsg);
            String vendorOrderId = ticketsMsgJsonObject.containsKey("vendorOrderId") ? ticketsMsgJsonObject
                    .getString("vendorOrderId") : "";
            String orderId = ticketsMsgJsonObject.containsKey("orderId") ? ticketsMsgJsonObject.getString("orderId")
                    : "";
            String orderNumber = ticketsMsgJsonObject.containsKey("orderNumber") ? ticketsMsgJsonObject
                    .getString("orderNumber") : "";
            JSONArray tickets = ticketsMsgJsonObject.containsKey("tickets") ? ticketsMsgJsonObject
                    .getJSONArray("tickets") : new JSONArray();

            JSONArray ticketsjArray = new JSONArray();
            if (tickets.size() == 0) {
                tickets = getPassengerNameAndTicketNoByOrder(orderId, vendorOrderId);
            }
            if (tickets.size() > 0) {
                for (int i = 0; i < tickets.size(); i++) {
                    String ticket = tickets.get(i).toString();
                    String dbStr = getMsgFromDB(ticket); // 从DB获取信息
                    if (!ElongHotelInterfaceUtil.StringIsNull(dbStr)) {
                        JSONObject dbjObject = new JSONObject();
                        try {
                            dbjObject = JSONObject.parseObject(dbStr);
                            String ticketNo = ticket;
                            String passengerName = dbjObject.containsKey("passengerName") ? dbjObject
                                    .getString("passengerName") : "";
                            String passportTypeld = dbjObject.containsKey("passengerTypeId") ? dbjObject
                                    .getString("passengerTypeId") : "";
                            String passportseNo = dbjObject.containsKey("passportseNo") ? dbjObject
                                    .getString("passportseNo") : "";
                            String returnStatus = "";
                            String returnSuccess = "";
                            if (dbjObject.getString("status").equals("8") || dbjObject.getString("status").equals("9")
                                    || dbjObject.getString("status").equals("10")) {
                                returnSuccess = "true";
                                if (dbjObject.getString("status").equals("8")
                                        || dbjObject.getString("status").equals("9")) {
                                    returnStatus = "1";
                                }
                                else {
                                    returnStatus = "2";
                                }
                            }
                            else {
                                returnSuccess = "false";
                                returnStatus = "3";
                            }

                            String returnMoney = dbjObject.containsKey("returnMoney") ? dbjObject
                                    .getString("returnMoney") : "";
                            String returnTime = dbjObject.containsKey("returnTime") ? dbjObject.getString("returnTime")
                                    : "";
                            String returnMsg = "";
                            if (returnStatus.equals("1")) {
                                returnMsg = "退款处理中";
                            }
                            else if (returnStatus.equals("2")) {
                                returnMsg = "已退款";
                            }
                            else if (returnStatus.equals("3")) {
                                returnMsg = "退款处理失败";
                            }
                            else {
                                returnStatus = "4";
                                returnSuccess = "false";
                                returnMsg = "获取信息失败";
                            }
                            JSONObject ticketjObject = new JSONObject();
                            ticketjObject.put("ticketNo", ticketNo);
                            ticketjObject.put("passengerName", passengerName);
                            ticketjObject.put("passportTypeld", passportTypeld);
                            ticketjObject.put("passportseNo", passportseNo);
                            ticketjObject.put("returnSuccess", returnSuccess);
                            ticketjObject.put("returnMoney", returnMoney);
                            ticketjObject.put("returnTime", returnTime);
                            ticketjObject.put("returnFailld", "");
                            ticketjObject.put("returnFailMsg", "");
                            ticketjObject.put("returnStatus", returnStatus);
                            ticketjObject.put("returnMsg", returnMsg);
                            ticketsjArray.add(ticketjObject);
                        }
                        catch (Exception e) {
                            return data;
                        }
                    }
                    else {
                        return data;
                    }
                }
                data.put("vendorOrderId", vendorOrderId);
                data.put("orderId", orderId);
                data.put("orderNumber", orderNumber);
                data.put("tickets", ticketsjArray);
                return data;
            }
            else {
                data.put("vendorOrderId", vendorOrderId);
                data.put("orderId", orderId);
                data.put("orderNumber", orderNumber);
                data.put("tickets", ticketsjArray);
            }
        }
        catch (Exception e) {
            return data;
        }
        return data;
    }

    /**
     * 通过订单获取订单下所有票号
     * 
     * @param interfaceNumber
     * @param orderNumber
     * @return
     * @time 2016年6月15日 下午3:22:38
     * @author fiend
     */
    private JSONArray getPassengerNameAndTicketNoByOrder(String interfaceNumber, String orderNumber) {
        JSONArray ticket = new JSONArray();
        String sql = "SELECT C_TICKETNO FROM T_TRAINTICKET WHERE C_TRAINPID="
                + "(SELECT ID FROM T_TRAINPASSENGER WHERE C_ORDERID = (SELECT ID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER = '"
                + interfaceNumber + "' AND C_ORDERNUMBER = '" + orderNumber + "'))";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            try {
                String ticketNo = map.get("C_TICKETNO").toString();
                ticket.add(ticketNo);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ticket;
    }

    public String getMsgFromDB(String ticketNo) {
        Map map = null;
        String passengerName = "";
        String passengerTypeId = "";
        String passportseNo = "";
        String returnMoney = "";
        String status = "";
        String returnTime = "";
        //
        String passengerSql = "SELECT C_NAME, C_IDTYPE, C_IDNUMBER FROM T_TRAINPASSENGER WITH (NOLOCK) WHERE ID =(SELECT C_TRAINPID FROM T_TRAINTICKET WITH (NOLOCK) WHERE C_TICKETNO='"
                + ticketNo + "')";
        List passengerList = Server.getInstance().getSystemService().findMapResultBySql(passengerSql, null);
        if (passengerList.size() > 0) {
            map = (Map) passengerList.get(0);
            passengerName = map.get("C_NAME") != null ? map.get("C_NAME").toString() : "";
            passengerTypeId = map.get("C_IDTYPE") != null ? map.get("C_IDTYPE").toString() : "";
            passportseNo = map.get("C_IDNUMBER") != null ? map.get("C_IDNUMBER").toString() : "";
        }
        // 状态 /票价 /退票手续费/退票申请时间/退票成功时间
        String ticketSql = "SELECT C_STATUS, C_PRICE, C_PROCEDURE, C_REFUNDREQUESTTIME, C_REFUNDSUCCESSTIME FROM T_TRAINTICKET WHERE C_TICKETNO='"
                + ticketNo + "'";
        List ticketList = Server.getInstance().getSystemService().findMapResultBySql(ticketSql, null);
        if (ticketList.size() > 0) {
            map = (Map) ticketList.get(0);
            try {
                float price = map.get("C_PRICE") != null ? Float.parseFloat(map.get("C_PRICE").toString()) : 0;
                float procedure = map.get("C_PROCEDURE") != null ? Float.parseFloat(map.get("C_PROCEDURE").toString())
                        : 0;
                returnMoney = (price - procedure) + "";
                status = map.get("C_STATUS").toString().trim();
                if (map.get("C_REFUNDSUCCESSTIME") != null) {
                    returnTime = map.get("C_REFUNDSUCCESSTIME").toString();
                }
                else if (map.get("C_REFUNDREQUESTTIME") != null) {
                    returnTime = map.get("C_REFUNDREQUESTTIME").toString();
                }
            }
            catch (Exception e) {
                return "";
            }
        }
        JSONObject result = new JSONObject();
        if (!ElongHotelInterfaceUtil.StringIsNull(passengerName)
                && !ElongHotelInterfaceUtil.StringIsNull(passengerTypeId)
                && !ElongHotelInterfaceUtil.StringIsNull(passportseNo)) {
            result.put("passengerName", passengerName);
            result.put("passengerTypeId", passengerTypeId);
            result.put("passportseNo", passportseNo);
            result.put("returnMoney", returnMoney);
            result.put("status", status);
            result.put("returnTime", returnTime);
        }
        return result.toString();
    }
}
