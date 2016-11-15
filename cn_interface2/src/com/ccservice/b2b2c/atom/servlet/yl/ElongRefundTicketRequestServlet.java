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

public class ElongRefundTicketRequestServlet extends HttpServlet {
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
            String merchantId = request.getParameter("merchantId"); //供应商id
            String timeStamp = request.getParameter("timeStamp"); //发送请求的时间戳
            String orderId = request.getParameter("orderId"); //下单订单号
            String orderItemId = request.getParameter("orderItemId"); //票号（票唯一id）
            String Sign = request.getParameter("sign"); //签名
            String paramJson = request.getParameter("paramJson"); //退票json串

            WriteLog.write("Elong_艺龙退票请求_ElongRefundTicketRequestServlet", r1 + ":" + merchantId + ":" + timeStamp
                    + ":" + orderId + ":" + orderItemId + ":" + Sign + ":" + paramJson);
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId)
                    || ElongHotelInterfaceUtil.StringIsNull(orderItemId) || ElongHotelInterfaceUtil.StringIsNull(Sign)
                    || ElongHotelInterfaceUtil.StringIsNull(paramJson)) {
                result.put("retcode", "500");
                result.put("retdesc", "请求参数不全");
            }
            else {
                result = new ElongRefundTicketRequestDisposeMethod().refundTicketRequsetDispose(merchantId, timeStamp,
                        orderId, orderItemId, Sign, paramJson, r1);
            }
        }
        catch (Exception e) {
        }
        finally {
            WriteLog.write("Elong_艺龙退票请求_ElongRefundTicketRequestServlet", r1 + ":reslut:" + result);
            out.print(result);
            out.flush();
            out.close();
        }

    }
}
