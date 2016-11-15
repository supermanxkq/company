package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.ali.AliRefundPushMethod;

/**
 * 支付宝退款回调
 */

@SuppressWarnings("serial")
public class TrainOrderMoneyCallback extends HttpServlet {

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
            //数据
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            //操作
            result = new AliRefundPushMethod().operate(buf.toString());
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
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