package com.ccservice.jl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.huamin.WriteLog;

public class JlOrderStatus extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        InputStream in = request.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String str = "";
        StringBuffer sb = new StringBuffer("");
        StringBuffer sbt = new StringBuffer();
        sbt.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        WriteLog.write("捷旅订单日志", "接收到的请求信息：" + sb.toString());
        Document doc = DocumentHelper.createDocument();
        if (!"".equals(sb.toString())) {
            try {
                doc = DocumentHelper.parseText(sb.toString());
            }
            catch (DocumentException e1) {
                e1.printStackTrace();
            }
            Element root = doc.getRootElement();
            String customercd = root.elementText("customercd");//客户编号 
            String authno = root.elementText("authno");//授权码 
            String businesstype = root.elementText("businesstype");//业务类型
            String orderid = root.elementText("orderid");//捷旅订单 ID
            String ordercd = root.elementText("ordercd");//捷旅订单编号
            String customerordercd = root.elementText("customerordercd");//同行订单编号
            String hotelconfirmfaxsent = root.elementText("hotelconfirmfaxsent");//已发送酒店
            String orderstatus = root.elementText("orderstatus");//订单状态
            String paystatus = root.elementText("paystatus");//付款状态 
            String receivestatus = root.elementText("receivestatus");//收款状态
            if (ordercd != null && !"".equals(ordercd)) {
                if (customerordercd != null && !"".equals(customerordercd)) {
                    List<Hotelorder> hotelorders = Server.getInstance().getHotelService()
                            .findAllHotelorder("where C_ORDERID='" + customerordercd + "'", "", -1, 0);
                    if (hotelorders.size() == 1) {
                        Hotelorder order = hotelorders.get(0);
                        if (orderstatus != null && !"".equals(orderstatus)) {
                            if (!(order.getOutorderstate().longValue() + "").equals(orderstatus)) {
                                if (hotelconfirmfaxsent != null && !"".equals(hotelconfirmfaxsent)) {
                                    Hotelorderrc rc = new Hotelorderrc();
                                    rc.setContent("供应商：" + hotelconfirmfaxsent);
                                    rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                                    rc.setOrderid(order.getOrderid());
                                    rc.setHandleuser(order.getMemberid().toString());
                                    rc.setLanguage(0);
                                    try {
                                        Server.getInstance().getHotelService().createHotelorderrc(rc);
                                    }
                                    catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            order.setOutorderstate(Long.valueOf(orderstatus));
                            Server.getInstance().getHotelService().updateHotelorderIgnoreNull(order);
                            sbt.append("<order>");
                            sbt.append("<customercd>" + customercd + "</customercd>");
                            sbt.append("<authno>" + authno + "</authno>");
                            sbt.append("<businesstype>orderstatus</businesstype>");
                            sbt.append("<result>1</result>");
                            sbt.append("<error></error>");
                            sbt.append("<orderid>" + orderid + "</orderid>");
                            sbt.append("<ordercd>" + ordercd + "</ordercd>");
                            sbt.append("<customerordercd>" + customerordercd + "</customerordercd>");
                            sbt.append("</order>");
                            //成功
                        }
                        else {
                            //更新失败
                            sbt.append("<order>");
                            sbt.append("<customercd>" + customercd + "</customercd>");
                            sbt.append("<authno>" + authno + "</authno>");
                            sbt.append("<businesstype>orderstatus</businesstype>");
                            sbt.append("<result>8</result>");
                            sbt.append("<error></error>");
                            sbt.append("<orderid>" + orderid + "</orderid>");
                            sbt.append("<ordercd>" + ordercd + "</ordercd>");
                            sbt.append("<customerordercd>" + customerordercd + "</customerordercd>");
                            sbt.append("</order>");
                        }
                    }
                    else {
                        //更新失败
                        sbt.append("<order>");
                        sbt.append("<customercd>" + customercd + "</customercd>");
                        sbt.append("<authno>" + authno + "</authno>");
                        sbt.append("<businesstype>orderstatus</businesstype>");
                        sbt.append("<result>8</result>");
                        sbt.append("<error></error>");
                        sbt.append("<orderid>" + orderid + "</orderid>");
                        sbt.append("<ordercd>" + ordercd + "</ordercd>");
                        sbt.append("<customerordercd>" + customerordercd + "</customerordercd>");
                        sbt.append("</order>");
                    }
                }
            }
            try {
                WriteLog.write("捷旅订单日志", "订单状态通知返回信息：" + sbt.toString());
                out.println(sbt.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                WriteLog.write("捷旅订单日志", "订单状态通知返回信息：" + sbt.toString());
                out.println(sbt.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
