package com.ccservice.b2b2c.atom.service12306.offlineRefund.servlet;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.TrainOfflineRefundMethod;

@SuppressWarnings("serial")
public class AliRefundPriceServlet extends HttpServlet {

    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //UTF8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html; charset=UTF-8");
        //异步
        final AsyncContext ctx = request.startAsync();
        //结果
        String result = "";
        try {
            //请求数据
            String orderId = request.getParameter("orderId");
            String busType = request.getParameter("busType");
            String payTradeNos = request.getParameter("payTradeNos");
            String refundPrice = request.getParameter("refundPrice");
            String specialFlag = request.getParameter("specialFlag");
            boolean newModel = "1".equals(busType) || "2".equals(busType);
            String passengers = request.getParameter("passengers");//退款乘客
            //JSON
            JSONObject reqdata = new JSONObject();
            reqdata.put("newModel", newModel);
            reqdata.put("payTradeNos", payTradeNos);
            reqdata.put("busType", newModel ? busType : "");
            reqdata.put("orderId", newModel ? Long.parseLong(orderId) : 0);
            //退款乘客
            reqdata.put(
                    "passengers",
                    ElongHotelInterfaceUtil.StringIsNull(passengers) ? new JSONArray() : JSONArray
                            .parseArray(new String(passengers.getBytes("ISO8859-1"), "UTF-8")));
            //特殊标识
            reqdata.put("specialFlag",
                    ElongHotelInterfaceUtil.StringIsNull(specialFlag) ? 0 : Integer.parseInt(specialFlag));
            //退款合计
            reqdata.put("aliTotalRefund",
                    ElongHotelInterfaceUtil.StringIsNull(refundPrice) ? 0 : Float.parseFloat(refundPrice));
            //操作处理
            result = new TrainOfflineRefundMethod().operate(reqdata).toString();
        }
        catch (Exception e) {
            //打印异常
            e.printStackTrace();
            //异常结果
            result = ElongHotelInterfaceUtil.errormsg(e);
        }
        //输出结果
        try {
            ctx.getResponse().getWriter().write(result);
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
        }
        //响应结果
        try {
            ctx.complete();
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
        }
    }
}