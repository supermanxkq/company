package com.ccservice.b2b2c.atom.servlet.yl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class ElongCancelOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=utf-8");
        int r1 = new Random().nextInt(10000000);
        JSONObject result = new JSONObject();
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String merchantId = request.getParameter("merchantId");
            String timeStamp = request.getParameter("timeStamp");
            String orderId = request.getParameter("orderId");
            String sign = request.getParameter("sign");

            WriteLog.write("Elong_取消订单_ElongCancelOrderServlet", r1 + ":" + merchantId + ":" + timeStamp + ":"
                    + orderId + ":" + sign);
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                result.put("retcode", "400");
                result.put("retdesc", "艺龙系统错误");
            }
            else {
                result = new ElongCancelOrderDisposeMethod().CancelOrderDispose(merchantId, orderId, timeStamp, sign,
                        r1);
            }
        }
        catch (Exception e) {
        }
        finally {
            WriteLog.write("Elong_取消订单_ElongCancelOrderServlet", r1 + ":reslut:" + result);
            out.print(result);
            out.flush();
            out.close();
        }
    }
}
