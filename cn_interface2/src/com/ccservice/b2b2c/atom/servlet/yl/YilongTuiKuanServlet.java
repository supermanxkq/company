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

public class YilongTuiKuanServlet extends HttpServlet {

    /**
     * 
     */
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
            /**
             * 
            2015年12月10日 16:42:221
            yangtao
             * */
            out = response.getWriter();
            String orderId = request.getParameter("orderId");// 订单号 orderId 必填  订单号 
            String amount = request.getParameter("amount");//退款金额    amount  必填  退款金额    大于0
            String comment = request.getParameter("comment");//退款说明    comment 必填  备注
            String orderItemId = request.getParameter("orderItemId");// 订单项号    orderItemId 必填  订单项号    
            String merchantCode = request.getParameter("merchantCode");//代理商code  merchantCode    必填  由艺龙分配   
            String tradeNo = request.getParameter("tradeNo");//退款流水号   tradeNo 必填  由供应商提供的唯一的流水编号。不允许重复    退款流水号
            String sign = request.getParameter("sign");//  签名  sign    必填  数据签名    参照签名机制
            WriteLog.write("Elong_tuiKuan_ElongPayMessageServlet", orderId + "orderId:" + ":amount:" + amount
                    + ":comment:" + comment + ":orderItemId:" + orderItemId + ":merchantCode:" + merchantCode
                    + ":tradeNo:" + tradeNo + ":sign:" + sign);
            if (ElongHotelInterfaceUtil.StringIsNull(orderId) || ElongHotelInterfaceUtil.StringIsNull(amount)
                    || ElongHotelInterfaceUtil.StringIsNull(comment)
                    || ElongHotelInterfaceUtil.StringIsNull(orderItemId)
                    || ElongHotelInterfaceUtil.StringIsNull(merchantCode)
                    || ElongHotelInterfaceUtil.StringIsNull(tradeNo) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                outResult.put("retcode", "400");
                outResult.put("retdesc", "艺龙系统错误");
            }
            else {
                outResult = new YilongTuiKuanMethod().tuikuan(orderId, amount, comment, orderItemId, merchantCode,
                        tradeNo, sign);
            }
        }
        catch (Exception e) {
        }
        finally {
            WriteLog.write("Elong_艺龙退款_ElongPayMessageServlet", r1 + ":reslut:" + outResult);
            out.print(outResult);
            out.flush();
            out.close();
        }
    }
}
