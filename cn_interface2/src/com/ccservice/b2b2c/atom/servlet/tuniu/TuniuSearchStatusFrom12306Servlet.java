package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.util.DesUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

@SuppressWarnings("serial")
public class TuniuSearchStatusFrom12306Servlet extends HttpServlet {

    private final String logname = "tuniu途牛_1_0_0_7_出票站信息";

    private final String errorlogname = "tuniu途牛_1_0_0_7_出票站信息_error";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int random = new Random().nextInt();
        TongchengSupplyMethod tongchengSupplyMethod = new TongchengSupplyMethod();
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        Customeruser user = new Customeruser();
        JSONObject jsonObject = new JSONObject();
        try {
            out = resp.getWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }

            String param = buf.toString();
            JSONObject decryptJson = new JSONObject();
            WriteLog.write(logname, random + "-->请求参数:" + param);
            // 解密
            try {
                decryptJson = JSONObject.parseObject(param);
                if (decryptJson.containsKey("data") && !decryptJson.getString("data").isEmpty()) {
                    String data = decryptJson.getString("data");
                    data = TuNiuDesUtil.decrypt(data);
                    JSONObject dataJsonObject = JSONObject.parseObject(data);
                    decryptJson.put("data", dataJsonObject);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(errorlogname, e);
                jsonObject.put("success", false);
                jsonObject.put("returnCode", "231001");
                jsonObject.put("errorMsg", "解密错误");
                jsonObject.put("data", "");
                return;
            }
            WriteLog.write(logname, random + "-->请求参数(解密后):" + decryptJson.toString());
            if (!ElongHotelInterfaceUtil.StringIsNull(decryptJson.toString())) {
                JSONObject dateReqJsonObject = decryptJson.getJSONObject("data");
                String cookie = "";
                String Loginname = "";
                String Logpassword = "";
                try {
                    Loginname = dateReqJsonObject.containsKey("userName") ? dateReqJsonObject.getString("userName")
                            : "";
                    Logpassword = dateReqJsonObject.containsKey("password") ? dateReqJsonObject.getString("password")
                            : "";
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException(errorlogname, e);
                }
                // 请求数据
                String orderId = dateReqJsonObject.getString("orderId");// 途牛订单号
                String vendorOrderId = dateReqJsonObject.getString("vendorOrderId");// 供应商交易单号
                String orderNumber = dateReqJsonObject.getString("orderNumber");// 12306订单号
                JSONArray ticket = dateReqJsonObject.containsKey("ticket") ? dateReqJsonObject.getJSONArray("ticket")
                        : new JSONArray();// 车票信息
                if (ticket != null && ticket.size() > 0) {
                    for (int i = 0; i < ticket.size(); i++) {
                        JSONObject ticketJsonObject = ticket.getJSONObject(i);
                        ticketJsonObject.put("passengerName",
                                getPassengerNameByTicketNo(ticketJsonObject.getString("ticketNo")));
                    }
                }
                else {
                    ticket = getPassengerNameAndTicketNoByOrder(orderId, vendorOrderId);
                }
                JSONObject send2RepJson = new JSONObject();
                send2RepJson.put("ticket", ticket);
                send2RepJson.put("orderNumber", orderNumber);
                WriteLog.write(logname, random + "-->send2RepJson:" + send2RepJson.toString());
                if (!ElongHotelInterfaceUtil.StringIsNull(Loginname)
                        && !ElongHotelInterfaceUtil.StringIsNull(Logpassword)) {
                    try {
                        Logpassword = DesUtil.decrypt(Logpassword, "A1B2C3D4E5F60708");
                        WriteLog.write(logname, random + "-->Logpassword:" + Logpassword);
                    }
                    catch (Exception e) {
                    }
                    user = tongchengSupplyMethod.GetCustomerAccount(Loginname, Logpassword);
                }
                else {
                    String account = getTrainorderId(orderId, vendorOrderId);
                    user = tongchengSupplyMethod.GetUserFromAccountSystem(AccountSystem.LoginNameAccount, account,
                            !AccountSystem.waitWhenNoAccount, AccountSystem.NullMap);
                }
                cookie = user == null ? null : user.getCardnunber();
                WriteLog.write(logname, random + "-->cookie:" + cookie);
                if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {
                    String repUrl = RepServerUtil.getRepServer(user, false).getUrl();
                    // 发送调用newTrainInit请求 1007
                    String paramContent = "";
                    paramContent = "jsonFromTuniu=" + send2RepJson + "&cookie=" + cookie + "&datatypeflag=1007";
                    if (!ElongHotelInterfaceUtil.StringIsNull(repUrl)) {
                        WriteLog.write(logname, random + "--->" + repUrl + "?" + paramContent);
                        String repResult = SendPostandGet.submitPostTimeOut(repUrl, paramContent, "utf-8", 60000)
                                .toString();
                        WriteLog.write(logname, random + "--->" + repResult);
                        JSONObject dataJsonObject = new JSONObject();
                        dataJsonObject.put("orderId", orderId);
                        dataJsonObject.put("orderNumber", orderNumber);
                        dataJsonObject.put("vendorOrderId", vendorOrderId);
                        JSONObject repResultJsonObject = new JSONObject();
                        try {
                            repResultJsonObject = JSONObject.parseObject(repResult);
                        }
                        catch (Exception e1) {
                        }
                        if (!ElongHotelInterfaceUtil.StringIsNull(repResult) && repResultJsonObject != null) {
                            JSONArray ticketJsonArray = new JSONArray();
                            if (repResultJsonObject.containsKey("ticket")) {
                                try {
                                    ticketJsonArray = repResultJsonObject.getJSONArray("ticket");
                                    for (int i = 0; i < ticketJsonArray.size(); i++) {
                                        JSONObject statusJsonObject = ticketJsonArray.getJSONObject(i).getJSONObject(
                                                "statusInfo");
                                        statusJsonObject.put(
                                                "status",
                                                getStatusName(statusJsonObject.getString("status_code"),
                                                        statusJsonObject.getString("status")));
                                        statusJsonObject.remove("status_code");
                                        statusJsonObject.put("channel",
                                                officeNameFormat(statusJsonObject.getString("channel")));
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            dataJsonObject.put("ticket",
                                    repResultJsonObject.containsKey("ticket") ? repResultJsonObject.get("ticket")
                                            : new JSONArray());
                        }
                        else {
                            dataJsonObject.put("ticket", new JSONArray());
                        }
                        WriteLog.write(logname, random + "--->" + dataJsonObject.toString());
                        jsonObject.put("success", true);
                        jsonObject.put("returnCode", "231000");
                        jsonObject.put("errorMsg", "");
                        jsonObject.put("data", dataJsonObject);
                        return;
                    }
                }
                else {
                    jsonObject.put("success", false);
                    jsonObject.put("returnCode", "231001");
                    jsonObject.put("errorMsg", "查询信息失败");
                    jsonObject.put("data", "");
                    return;
                }
            }
            else {
                jsonObject.put("success", false);
                jsonObject.put("returnCode", "231001");
                jsonObject.put("errorMsg", "param error");
                jsonObject.put("data", "");
                return;
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(errorlogname, e);
            jsonObject.put("success", false);
            jsonObject.put("returnCode", "231001");
            jsonObject.put("errorMsg", "查询信息失败");
            jsonObject.put("data", "");
            return;
        }
        finally {
            //释放账号
            tongchengSupplyMethod.FreeUserFromAccountSystem(user, AccountSystem.FreeNoCare, AccountSystem.OneFree,
                    AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
            //记录日志
            WriteLog.write(logname, random + "--->" + "返回参数:" + jsonObject.toString());
            out.print(jsonObject.toString());
            out.flush();
            out.close();
        }
    }

    /**
     * 通过订单号获取12306账号
     * 
     * @param interfaceNumber
     * @param orderNumber
     * @return
     * @time 2016年6月15日 下午3:21:29
     * @author fiend
     */
    private String getTrainorderId(String interfaceNumber, String orderNumber) {
        String account = "";
        String sql = "SELECT C_SUPPLYACCOUNT FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER ='"
                + interfaceNumber + "' AND C_ORDERNUMBER='" + orderNumber + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            account = map.get("C_SUPPLYACCOUNT").toString().split("/")[0];
        }
        return account;
    }

    /**
     * 通过12306票号获取乘客姓名
     * 
     * @param ticketNo
     * @return
     * @time 2016年6月15日 下午3:22:22
     * @author fiend
     */
    private String getPassengerNameByTicketNo(String ticketNo) {
        String passengerName = "";
        String sql = "SELECT C_NAME FROM T_TRAINPASSENGER WITH (NOLOCK) WHERE ID =(SELECT C_TRAINPID FROM T_TRAINTICKET WITH (NOLOCK) WHERE C_TICKETNO='"
                + ticketNo + "')";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            try {
                passengerName = URLEncoder.encode(map.get("C_NAME").toString(), "UTF-8");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return passengerName;
    }

    /**
     * 通过订单获取订单下所有乘客和票号
     * 
     * @param interfaceNumber
     * @param orderNumber
     * @return
     * @time 2016年6月15日 下午3:22:38
     * @author fiend
     */
    private JSONArray getPassengerNameAndTicketNoByOrder(String interfaceNumber, String orderNumber) {
        JSONArray ticket = new JSONArray();
        String sql = "SELECT p.C_NAME,t.C_TICKETNO FROM T_TRAINTICKET  t WITH(NOLOCK) JOIN T_TRAINPASSENGER p WITH (NOLOCK) ON p.ID=t.C_TRAINPID"
                + " WHERE p.C_ORDERID =(SELECT TOP 1 ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                + interfaceNumber + "' AND C_ORDERNUMBER='" + orderNumber + "')";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            Map map = (Map) list.get(i);
            try {
                String passengerName = URLEncoder.encode(map.get("C_NAME").toString(), "UTF-8");
                String ticketNo = map.get("C_TICKETNO").toString();
                jsonObject.put("passengerName", passengerName);
                jsonObject.put("ticketNo", ticketNo);
                ticket.add(jsonObject);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ticket;
    }

    /**
     *  获取12306票状态
     *  依赖12306的JS，如果有变动，此方法需要修改
     *  依赖：https://kyfw.12306.cn/otn/resources/merged/myOrderDetail_js.js?scriptVersion=1.8956
     *          中的_getStatusName: function(j, g)方法 j 是statusCode，g 是status
     *  版本号：1.8956
     * @param statusCode
     * @param status
     * @return
     * @time 2016年6月20日 下午1:36:40
     * @author fiend
     */
    private String getStatusName(String statusCode, String status) {
        String statusName = "";
        if (statusCode.equalsIgnoreCase("a")) {
            statusName = "支付成功";
        }
        else if (statusCode.equalsIgnoreCase("b")) {
            statusName = "制票成功";
        }
        else if (statusCode.equalsIgnoreCase("c")) {
            statusName = "办理退票";
        }
        else if (statusCode.equalsIgnoreCase("d")) {
            statusName = "办理改签";
        }
        else if (statusCode.equalsIgnoreCase("f")) {
            statusName = "改签成功";
        }
        else if (statusCode.equalsIgnoreCase("l")) {
            statusName = "检票进站";
        }
        else if (statusCode.equalsIgnoreCase("m")) {
            statusName = "检票出站";
        }
        else if (statusCode.equalsIgnoreCase("p")) {
            statusName = "已变更到站";
        }
        else if (statusCode.equalsIgnoreCase("r")) {
            statusName = "变更到站票";
        }
        else if (statusCode.equalsIgnoreCase("q")) {
            statusName = "办理改签";
        }
        else {
            statusName = status;
        }
        return statusName;
    }

    /**
     *  获取12306票的操作人
     *  依赖12306的JS，如果有变动，此方法需要修改
     *  依赖：https://kyfw.12306.cn/otn/resources/merged/myOrderDetail_js.js?scriptVersion=1.8956
     *          中的_officeNameFormat: function(h)方法 h 是office_name
     *  版本号：1.8956
     * @param office_name
     * @return
     * @time 2016年6月20日 下午1:36:40
     * @author fiend
     */
    private String officeNameFormat(String office_name) {
        String officeName = office_name;
        if (officeName.indexOf("网售") > 0) {
            return "互联网/手机端";
        }
        else {
            return officeName;
        }
    }
}