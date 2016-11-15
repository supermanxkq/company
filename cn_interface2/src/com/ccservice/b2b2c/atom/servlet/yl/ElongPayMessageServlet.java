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

public class ElongPayMessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        JSONObject outResult = new JSONObject();
        PrintWriter out = null;
        int r1 = new Random().nextInt(10000000);
        try {
            out = response.getWriter();
            String merchantId = request.getParameter("merchantId");
            String timeStamp = request.getParameter("timeStamp");
            String orderId = request.getParameter("orderId");
            String ticketPrice = request.getParameter("ticketPrice");
            String result = request.getParameter("result");
            String sign = request.getParameter("sign");
            WriteLog.write("Elong_艺龙支付通知_ElongPayMessageServlet", r1 + ":" + ":merchantId:" + merchantId
                    + ":timeStamp:" + timeStamp + ":orderId:" + orderId + ":ticketPrice:" + ticketPrice + ":result:"
                    + result + ":sign:" + sign);
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId)
                    || ElongHotelInterfaceUtil.StringIsNull(ticketPrice)
                    || ElongHotelInterfaceUtil.StringIsNull(result) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                outResult.put("retcode", "400");
                outResult.put("retdesc", "艺龙系统错误");
            }
            else {
                outResult = new ElongPayMessageDisposeMethod().payMsgDisposeMethod(result, orderId, merchantId,
                        timeStamp, ticketPrice, sign, r1);
            }
        }
        catch (Exception e) {
        }
        finally {
            WriteLog.write("Elong_艺龙支付通知_ElongPayMessageServlet", r1 + ":reslut:" + outResult);
            out.print(outResult);
            out.flush();
            out.close();
        }
    }

}
