package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * Servlet implementation class RequestConfirmChangeServlet
 */
public class RequestConfirmChangeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RequestConfirmChangeServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String responseString = "success";
        PrintWriter out = null;
        try {
            request.setCharacterEncoding("utf-8");
            request.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", "text/html; charset=UTF-8");
            out = response.getWriter();
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String jsonString = buf.toString();
        jsonString = jsonString.substring(jsonString.indexOf("=") + 1, jsonString.length());
        jsonString = URLDecoder.decode(jsonString, "utf-8");
        WriteLog.write("确认改签回调", "回调接收的json数据" + jsonString);

        Trainticket ticket = new Trainticket();
        if (jsonString != null && !"".equals(jsonString)) {
            Trainorder trainorder = new Trainorder();
            JSONObject newConfirmJson = JSONObject.parseObject(jsonString);
            boolean success = newConfirmJson.getBooleanValue("success");
            String reqtoken = newConfirmJson.getString("reqtoken");
            String ticketpricediffchangeserial = newConfirmJson.getString("ticketpricediffchangeserial");
            String oldticketchangeserial = newConfirmJson.getString("oldticketchangeserial");
            String newticketchangeserial = newConfirmJson.getString("newticketchangeserial");
            String msg = newConfirmJson.getString("msg");
            String orderid = newConfirmJson.getString("orderid");
            JSONArray newticketcxins = newConfirmJson.getJSONArray("newticketcxins");
            String sqlselectString = "exec [sp_T_TRAINORDER_Selectid] @C_ORDERNUMBER='" + orderid + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sqlselectString, null);
            long pkid = 0;
            long changeid = 0;
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                pkid = Long.valueOf(map.get("ID").toString());
                if (pkid > 0) {
                    trainorder = Server.getInstance().getTrainService().findTrainorder(pkid);

                    for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                        for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                            for (int i = 0; i < newticketcxins.size(); i++) {
                                JSONObject newticketcxin = newticketcxins.getJSONObject(i);
                                String old_ticket_no = newticketcxin.getString("old_ticket_no");
                                String new_ticket_no = newticketcxin.getString("new_ticket_no");
                                String cxin = newticketcxin.getString("cxin");
                                String tctrainno = trainticket.getTcticketno();
                                String trainno = trainticket.getTicketno();
                                if (trainno != null && old_ticket_no != null & trainno.equals(old_ticket_no)) {
                                    if (tctrainno != null && !tctrainno.equals(new_ticket_no)) {
                                        responseString = "false";
                                        break;
                                    }
                                    else {
                                        if (cxin != null && !"".equals(cxin)) {
                                            trainticket.setTccoach(cxin.split(",")[0]);
                                            trainticket.setTcseatno(cxin.split(",")[1]);
                                            trainticket.setTcticketno(new_ticket_no);
                                            trainticket.setChangeType(1);
                                            ticket = trainticket;
                                        }
                                        else {
                                            responseString = "false";
                                            break;
                                        }
                                        changeid = trainticket.getChangeid();
                                        if (success) {
                                            Server.getInstance().getTrainService().updateTrainticket(trainticket);
                                            String sql_updateTicketString = "exec [sp_T_TRAINTICKET_updatechange] @ID="
                                                    + trainticket.getId() + ",@C_STATUS=17 ";
                                            Server.getInstance().getSystemService()
                                                    .excuteAdvertisementBySql(sql_updateTicketString);
                                            createTrainorderrc(1, trainorder.getId(), trainpassenger.getName() + " "
                                                    + msg, "确认改签接口", 17, trainticket.getId());
                                        }
                                        else {
                                            String sql_updateTicketString = "exec [sp_T_TRAINTICKET_updatechange] @ID="
                                                    + trainticket.getId() + ",@C_STATUS=17 ";
                                            Server.getInstance().getSystemService()
                                                    .excuteAdvertisementBySql(sql_updateTicketString);
                                            createTrainorderrc(1, trainorder.getId(), trainpassenger.getName() + " "
                                                    + msg, "确认改签接口", 17, trainticket.getId());
                                        }

                                    }
                                }

                            }
                        }
                    }

                }

                if (changeid > 0) {
                    Trainorderchange trainorderchange = Server.getInstance().getTrainService()
                            .findTrainorcerchange(changeid);
                    if (success) {
                        try {
                            trainorderchange.setTcstatus(6);
                            Server.getInstance().getTrainService().updateTrainorcerchange(trainorderchange);
                            int isTcislowchange = trainorderchange.getTcislowchange();
                            if (isTcislowchange == 1) {
                                float cjprice = trainorderchange.getTcprocedure();
                                String serverinfo = request.getServerName() + ":" + request.getServerPort();
                                createTrainorderrc(1, trainorder.getId(), "退改签差价" + "<span style='color:red;'>"
                                        + cjprice + "</span>元", "确认改签接口", 1, 0);
                                String iscj = "gq";
                                Customeruser user = new Customeruser();
                                user.setMembername("改签退款接口");
                                user.setId(1000l);
                                Server.getInstance()
                                        .getTrainService()
                                        .ticketRefundLowChange(trainorderchange.getId(), trainorder.getId(),
                                                ticket.getId(), user, serverinfo);
                                createTrainorderrc(1, trainorder.getId(), "<span style='color:red;'>改签完成</span>",
                                        "确认改签接口", 1, 0);
                            }

                        }
                        catch (Exception e) {
                            WriteLog.write("确认改签回调_Exception", orderid + "");
                            ExceptionUtil.writelogByException("确认改签回调_Exception", e);
                            responseString = "false";
                        }
                    }
                    else {
                        trainorderchange.setTcstatus(3);
                        Server.getInstance().getTrainService().updateTrainorcerchange(trainorderchange);
                        if (trainorderchange.getTcislowchange() == 0) {
                            String serverinfo = request.getServerName() + ":" + request.getServerPort();
                            boolean refund = new TrainchangeRefundMethod().refundChange(trainorderchange, ticket,
                                    trainorder, serverinfo);
                            if (!refund) {
                                responseString = "false";
                            }
                        }
                    }
                }
                else {
                    responseString = "false";
                }

            }
        }
        if (out != null) {
            out.print(responseString);
            out.flush();
            out.close();
        }

    }

    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderid);
            rc.setContent(content);
            rc.setStatus(status);// Trainticket.ISSUED
            rc.setCreateuser(createurser);// "12306"
            rc.setTicketid(ticketid);
            rc.setYwtype(yewutype);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
