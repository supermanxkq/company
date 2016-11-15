package com.ccservice.b2b2c.atom.servlet.tq;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 114退票回调接收
 * @author wzc
 *
 */
public class TrainRefundTicketTqServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        String param = req.getParameter("data");
        WriteLog.write("TrainInsure_tuibao", param);
        if (param != null && !"".equals(param)) {//退票数据
            JSONObject refundobj = JSONObject.parseObject(param);
            String apiorderid = refundobj.getString("apiorderid");//接口单号
            JSONArray returnticketsary = refundobj.getJSONArray("returntickets");//退票数组
            Trainform form = new Trainform();
            form.setQunarordernumber(apiorderid);
            List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(form, null);
            if (orders.size() > 0) {
                Trainorder order = Server.getInstance().getTrainService().findTrainorder(orders.get(0).getId());
                for (int i = 0; i < returnticketsary.size(); i++) {
                    JSONObject retundticketobj = returnticketsary.getJSONObject(i);
                    String ticket_no = retundticketobj.getString("ticket_no");
                    boolean returnsuccess = retundticketobj.getBooleanValue("returnsuccess");//是否退票成功
                    if (returnsuccess) {//退票成功
                        for (Trainpassenger trainpassenger : order.getPassengers()) {
                            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                                if (ticket_no.equals(trainticket.getTicketno())
                                        && trainticket.getInsurorigprice() != null
                                                & trainticket.getInsurorigprice() > 0) {
                                    if (trainticket.getRealinsureno() != null
                                            && !"".equalsIgnoreCase(trainticket.getRealinsureno())) {
                                        String url = PropertyUtil.getValue("mqurl", "tqTrain.properties");
                                        JSONObject jsoseng = new JSONObject();
                                        jsoseng.put("type", "2");
                                        jsoseng.put("orderid", order.getId());
                                        jsoseng.put("policyno", trainticket.getRealinsureno());
                                        WriteLog.write("TrainInsure_tuibao", jsoseng.toString());
                                        ActiveMQUtil.sendMessage(url, "TrainInsure", jsoseng.toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            out.print("success");
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
