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

public class YiLongReqChangeServlet extends HttpServlet {
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
        String result = "";
        PrintWriter out = null;
        int r1 = new Random().nextInt(10000000);
        try {
            out = response.getWriter();
            String merchantId = request.getParameter("merchantId");//merchantId 供应商id   是   string  是   分配给艺龙的id
            String timeStamp = request.getParameter("timeStamp");//timeStamp   推送时间戳   是   string  是   发送请求的时间戳
            String orderId = request.getParameter("orderId");// orderId 订单号 是   string  是   下单订单号
            String orderItemId = request.getParameter("orderItemId");//  orderItemId 票号  是   String  是   票号（票唯一id） 
            String type = request.getParameter("type");//  type    操作类型    是   String  是   1：线下退票
            String note = request.getParameter("note");//  note    操作类型    否   String  是   线下退票
            String sign = request.getParameter("sign");// sign    签名  是   string  否   签名
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId)
                    || ElongHotelInterfaceUtil.StringIsNull(orderItemId) || ElongHotelInterfaceUtil.StringIsNull(type)
                    || ElongHotelInterfaceUtil.StringIsNull(note) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                outResult.put("retcode", "400");
                outResult.put("retdesc", "信息不能为空");
                result = outResult.toJSONString();
            }
            else {
                if (type.equals("1") || type != "1") {
                    result = new YiLongReqChange().returnticketYilong(merchantId, timeStamp, orderId, orderItemId,
                            sign, note, type, r1);

                }
                else {
                    outResult.put("retdesc", "不是线下退票类型");
                    result = outResult.toJSONString();
                }
            }
            WriteLog.write("艺龙先下退票_ElongPayMessageServlet", r1 + ":" + ":merchantId:" + merchantId + ":timeStamp:"
                    + timeStamp + ":orderId:" + orderId + ":orderItemId:" + orderItemId + ":type:" + type + ":note:"
                    + note + ":sign:" + sign + "|result" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.print(result);
            out.flush();
            out.close();
        }

    }
}
