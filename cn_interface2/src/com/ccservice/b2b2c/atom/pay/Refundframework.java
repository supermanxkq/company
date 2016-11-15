package com.ccservice.b2b2c.atom.pay;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.traderecord.Traderecord;

@SuppressWarnings("serial")
public class Refundframework extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        long orderid = 0;
        try {
            orderid = Long.parseLong(new String(request.getParameter("orderid").getBytes("ISO8859-1"), "UTF-8"));
            String helpername = request.getParameter("helpername");
            WriteLog.write("tui_tuikuan", "订单退款:" + orderid + "," + helpername);
            Refundhelper refundhelper = (Refundhelper) Class.forName(
                    Refundhelper.class.getPackage().getName() + "." + helpername).getConstructor(long.class)
                    .newInstance(orderid);
            WriteLog.write("tui_tuikuan", refundhelper.getOrdernumber());
            String where = "WHERE C_ORDERCODE='" + refundhelper.getOrdernumber() + "' AND  C_STATE=1";
            List list = Server.getInstance().getMemberService().findAllTraderecord(where, "ORDER BY ID DESC", -1, 0);
            Refund refund = null;
            if (list != null & list.size() > 0) {
                Traderecord record = (Traderecord) list.get(0);
                String createuser = record.getCreateuser();
                WriteLog.write("tui_tuikuan", createuser);
                if ("chinapnrpay".equals(createuser)) {
                    refund = new Chinapnrrefund(request, response, refundhelper);
                }
                else if ("Billpay".equals(createuser)) {
                    refund = new Billpayrefund(request, response, refundhelper);
                }
                else if ("Yeepay".equals(createuser)) {
                    refund = new YeepayRefund(request, response, refundhelper);
                }
                else if ("TenpayN".equals(createuser)) {
                    refund = new Tenpayrefund(request, response, refundhelper);
                }
                else {
                    refund = new Alipayrefund(request, response, refundhelper);
                }
            }
            else {// 接口订单无此记录，默认一律支付宝退款
                refund = new Alipayrefund(request, response, refundhelper);
            }
            refund.refund();
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("tui_tuikuan", orderid + "退款异常:" + e.fillInStackTrace());
        }

    }
}
