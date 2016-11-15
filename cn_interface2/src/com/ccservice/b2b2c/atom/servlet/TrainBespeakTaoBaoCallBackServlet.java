package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;

/**
 * Servlet implementation class TrainBespeakTaoBaoSuodan
 */
public class TrainBespeakTaoBaoCallBackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TaobaoHotelInterfaceUtil tbiu;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrainBespeakTaoBaoCallBackServlet() {
        super();
        tbiu = new TaobaoHotelInterfaceUtil();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String orderId = request.getParameter("orderid");
        String refuseMsg = request.getParameter("refuseMsg");
        String method = request.getParameter("method");
        WriteLog.write("淘宝抢票回调1", "数据------>订单号：" + orderId + "method=" + method + "原因：" + refuseMsg);
        String result = "";
        Map ma = null;
        String obj = "";
        int i = 0;
        boolean suodan = false;
        JSONObject exception = new JSONObject();
        if (method.equals("suodan")) {
            suodan = tbiu.taobaoHandleOrder(orderId);
//            suodan=true;
            WriteLog.write("淘宝抢票下单1", "淘宝锁单：" + suodan);
            if (suodan) {//锁单成功
                result = "success";
            }
            else {
                result = "false";
            }
        }
        else if (method.equals("callbackMsg")) {
            WriteLog.write("淘宝抢票回调1", "失败原因：refuseMsg=" + refuseMsg);
            ma = tbiu.tainOrderidTaoBao(orderId, refuseMsg);
            WriteLog.write("淘宝抢票回调1", "请求淘宝参数值:" + ma);
            if (ma != null) {
                do {
                    WriteLog.write("淘宝抢票回调1", "请求淘宝返回值:" + obj);
                    obj = tbiu.taobaoDrawer(ma);
                    WriteLog.write("淘宝抢票回调1", "请求淘宝返回值:" + obj);
                    if (!"SUCCESS".equals(obj)) {
                        try {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException e) {
                        }
                    }
                    i++;
                }
                while (!obj.equals("SUCCESS") && i < 5);
                if (obj.equals("") || obj == null) {
                    result = "error";
                }
                else if (obj.equals("SUCCESS")) {
                    result = "success";
                }
                else {
                    exception.put("success", false);
                    exception.put("msg", "淘宝接口请求失败，session过期或参数不匹配，错误信息：" + tbiu.exceptionS);
                    result = exception.toString();
                }
            }
            else {
                exception.put("success", false);
                exception.put("msg", "淘宝接口拼参请求失败，错误信息：" + tbiu.exceptionS);
                result = exception.toString();
            }
        }
        WriteLog.write("淘宝抢票回调1", "返回信息:" + result);
        PrintWriter out = null;
        out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }
}
