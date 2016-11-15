package com.ccservice.b2b2c.atom.servlet.yl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class ElongTrainOrderCallBackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String merchantCode;

    private Trainorder trainorder;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;

        try {
            out = response.getWriter();
            String result = request.getParameter("result");
            String failReason = request.getParameter("");
            long trainorderid = Long.parseLong(request.getParameter(""));
            this.trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);

            trainOrderCallBackMethod();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.print("");
            out.flush();
            out.close();
        }

    }

    /**
     * 
     * 
     * @time 2015年12月9日 下午3:32:46
     * @author wcl
     */
    public void trainOrderCallBackMethod() {

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        for (int i = 0; i < this.trainorder.getPassengers().size(); i++) {
            Trainpassenger trainpassenger = this.trainorder.getPassengers().get(i);
            for (int j = 0; j < trainpassenger.getTraintickets().size(); j++) {
                Trainticket trainticket = trainpassenger.getTraintickets().get(j);
                JSONObject jsonTrainticket = new JSONObject();
                JSONObject jsonTrainticket2 = new JSONObject();
                jsonTrainticket.put("orderItemId", trainticket.getTicketno());
                jsonTrainticket2.put("orderItemId", trainticket.getTicketno());
                jsonTrainticket.put("seatType", trainticket.getSeattype());
                jsonTrainticket2.put("seatType", trainticket.getSeattype());
                jsonTrainticket.put("seatNo", getURLEncode(trainticket.getSeatno()));
                jsonTrainticket2.put("seatNo", trainticket.getSeatno());
                jsonTrainticket.put("price", trainticket.getPrice());
                jsonTrainticket2.put("price", trainticket.getPrice());
                jsonTrainticket.put("passengerName", getURLEncode(trainpassenger.getName()));
                jsonTrainticket2.put("passengerName", trainpassenger.getName());
                jsonTrainticket.put("certNo", trainpassenger.getIdnumber());
                jsonTrainticket2.put("certNo", trainpassenger.getIdnumber());
                jsonTrainticket.put("ticketType", trainticket.getTickettype());
                jsonTrainticket2.put("ticketType", trainticket.getTickettype());
                jsonArray.add(jsonTrainticket);
                jsonArray2.add(jsonTrainticket);

            }
        }
        jsonObject.put("tickets", jsonArray);
        jsonObject2.put("tickets", jsonArray2);
        jsonObject.put("ticketNo", this.trainorder.getExtnumber());
        jsonObject2.put("ticketNo", this.trainorder.getExtnumber());
        jsonObject.put("orderId", this.trainorder.getQunarOrdernumber());
        jsonObject2.put("orderId", this.trainorder.getQunarOrdernumber());
        jsonObject.put("holdingSeatSuccessTime", "");
        jsonObject2.put("holdingSeatSuccessTime", "");
        jsonObject.put("payTimeDeadLine", "");
        jsonObject2.put("payTimeDeadLine", "");
        String sign = getSignMethod(jsonObject2);
        jsonObject.put("sign", sign);
        String ElongCallBackUrl = "";
        WriteLog.write("", "占座回调>>>>>>:ElongCallBackUrl:" + ElongCallBackUrl + ":" + jsonObject.toString());
        String result = SendPostandGet.submitPost(ElongCallBackUrl, jsonObject.toString(), "UTF-8").toString();
        WriteLog.write("", ">>>--占座回调返回-->>>:" + result);
    }

    private String getSignMethod(JSONObject json) {

        return null;
    }

    private String getURLEncode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }
}
