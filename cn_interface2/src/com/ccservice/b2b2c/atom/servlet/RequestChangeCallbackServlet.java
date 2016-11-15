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
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class RequestChangeCallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RequestChangeCallbackServlet() {
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
        WriteLog.write("请求改签回调", "回调接收的json数据" + jsonString);
        try {
            if (jsonString != null && !"".equals(jsonString)) {
                JSONObject jbJsonObject = JSONObject.parseObject(jsonString);
                JSONArray newtickets = new JSONArray();
                boolean success = jbJsonObject.getBooleanValue("success");
                String orderid = jbJsonObject.getString("orderid");
                String msg = jbJsonObject.getString("msg");
                String sqlselectString = "exec [sp_T_TRAINORDER_Selectid] @C_ORDERNUMBER='" + orderid + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sqlselectString, null);
                long pkid = 0;
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    pkid = Long.valueOf(map.get("ID").toString());
                    if (pkid > 0) {
                        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(pkid);
                        String reqtoken = jbJsonObject.getString("reqtoken");
                        float pricedifference = jbJsonObject.getFloatValue("pricedifference");
                        int priceinfotype = jbJsonObject.getIntValue("priceinfotype");//1 表示新票款高于原票款，2 表示新票款与原票款相等， 3 表示新票款低于原票款。
                        String to_station_name = jbJsonObject.getString("to_station_name");
                        String help_info = jbJsonObject.getString("help_info");
                        String transactionid = jbJsonObject.getString("transactionid");
                        String priceinfo = jbJsonObject.getString("priceinfo");
                        float diffrate = jbJsonObject.getFloatValue("diffrate");
                        float fee = jbJsonObject.getFloatValue("fee");
                        String from_station_name = jbJsonObject.getString("from_station_name");
                        String checi = jbJsonObject.getString("checi");
                        String arrive_time = jbJsonObject.getString("arrive_time");
                        String train_date = jbJsonObject.getString("train_date");
                        String start_time = jbJsonObject.getString("start_time");
                        newtickets = jbJsonObject.getJSONArray("newtickets");
                        if (success) {
                            long changeid = 0;
                            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                                    if (newtickets != null && newtickets.size() > 0) {
                                        for (int i = 0; i < newtickets.size(); i++) {
                                            JSONObject newticket = newtickets.getJSONObject(i);
                                            String zwname = newticket.getString("zwname");
                                            String new_ticket_no = newticket.getString("new_ticket_no");
                                            String price = newticket.getString("price");
                                            String piaotype = newticket.getString("piaotype");
                                            String zwcode = newticket.getString("zwcode");
                                            String passportseno = newticket.getString("passportseno");
                                            String old_ticket_no = newticket.getString("old_ticket_no");
                                            String flagmsg = newticket.getString("flagmsg");
                                            String flagid = newticket.getString("flagid");
                                            String cxin = newticket.getString("cxin");
                                            if (passportseno.equals(trainpassenger.getIdnumber())
                                                    && trainticket.getStatus() == Trainticket.APPLYCHANGE) {
                                                trainticket.setChangeProcedure(fee);
                                                if (cxin != null && !"".equals(cxin) && price != null
                                                        && !"".equals(price)) {
                                                    trainticket.setTccoach(cxin.split(",")[0]);
                                                    trainticket.setTcPrice(Float.valueOf(price));
                                                    trainticket.setTcnewprice(Float.valueOf(price));
                                                    trainticket.setTcseatno(cxin.split(",")[1]);
                                                    trainticket.setTcticketno(new_ticket_no);
                                                    trainticket.setChangeType(1);
                                                }
                                                trainticket.setTctrainno(checi);
                                                trainticket.setTtcdeparttime(train_date + " " + start_time);
                                                trainticket.setTtcseattype(zwname);
                                                trainticket.setStatus(Trainticket.THOUGHCHANGE);
                                                changeid = trainticket.getChangeid();
                                                Server.getInstance().getTrainService().updateTrainticket(trainticket);
                                                //                                            String sql_updateTicketString = "exec [sp_T_TRAINTICKET_updatechange] @ID="
                                                //                                                    + trainticket.getId() + ",@C_STATUS=15 ";
                                                //                                            Server.getInstance().getSystemService()
                                                //                                                    .excuteAdvertisementBySql(sql_updateTicketString);
                                                if (checi != null) {
                                                    createTrainorderrc(1, trainorder.getId(), trainpassenger.getName()
                                                            + "申请改签占座成功" + "新车次：" + checi + "发车时间：" + train_date + " "
                                                            + start_time + "" + priceinfo, "系统接口", 1, 0);
                                                }
                                                else {
                                                    createTrainorderrc(1, trainorder.getId(), trainpassenger.getName()
                                                            + "申请改签占座成功", "系统接口", 1, 0);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (changeid > 0) {
                                Trainorderchange trainorderchange = Server.getInstance().getTrainService()
                                        .findTrainorcerchange(changeid);
                                int tcislowchange = 1;
                                if (priceinfotype == 3 || priceinfotype == 2) {
                                    tcislowchange = 1;
                                }
                                else {
                                    tcislowchange = 0;
                                }
                                trainorderchange.setTcislowchange(tcislowchange); //是否是低改
                                trainorderchange.setTcstatus(4);
                                trainorderchange.setTcprocedure(pricedifference);
                                trainorderchange.setChangeRate(diffrate);
                                trainorderchange.setChangeProcedure(fee);
                                Server.getInstance().getTrainService().updateTrainorcerchange(trainorderchange);
                            }
                            else {
                                responseString = "false";
                            }
                        }
                        else {
                            String sql = "UPDATE T_TRAINORDERCHANGE set C_TCSTATUS=10 where C_REQUESTREQTOKEN='"
                                    + reqtoken
                                    + "' and C_TCSTATUS=1 ;update T_TRAINTICKET set C_STATUS=3 where C_CHANGEID in ( select ID from T_TRAINORDERCHANGE WITH (NOLOCK) where C_REQUESTREQTOKEN='"
                                    + reqtoken + "') and C_STATUS=12";
                            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                            createTrainorderrc(1, trainorder.getId(), reqtoken + "申请改签占座失败", "系统接口", 1, 0);
                        }
                    }
                    else {
                        responseString = "false";
                    }
                }
                else {
                    responseString = "false";
                }
            }
            else {
                responseString = "false";
            }
        }
        catch (Exception e) {
            responseString = "false";
        }
        finally {
            if (out != null) {
                out.print(responseString);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 书写操作记录
     * 
     * @param trainorderid
     * @param content
     * @param createurser
     * @time 2015年1月21日 下午7:05:04
     * @author fiend
     */
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
