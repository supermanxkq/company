package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.billpay.pki.Pkipair;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.gp.BillPayGpChangePay;
import com.ccservice.b2b2c.atom.pay.gp.BillPayTicketRefundOrder;
import com.ccservice.b2b2c.atom.pay.gp.BillpayGpTicket;
import com.ccservice.b2b2c.atom.pay.gp.BillpayGpWapChangePay;
import com.ccservice.b2b2c.atom.pay.gp.BillpayGpWapTicket;
import com.ccservice.b2b2c.atom.pay.gp.BillpayRefundRefuseOrder;
import com.ccservice.b2b2c.atom.pay.gp.BillpayWithHoldMoney;
import com.ccservice.b2b2c.atom.pay.gp.GpConfimOrder;
import com.ccservice.b2b2c.atom.pay.gp.GpWapAlipay;
import com.ccservice.b2b2c.atom.pay.gp.GpWapChangeAlipay;
import com.ccservice.b2b2c.atom.pay.gp.YeePosWithHoldMoney;
import com.ccservice.b2b2c.atom.pay.test.BillpayAndShareProfitTest;

/**
 * 生成支付链接工具类
 * @author wzc
 *
 */
public class PayUtilHttp extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String resultmsg = "";
        PrintWriter out = res.getWriter();
        String param = "";
        out = res.getWriter();
        int ind = new Random().nextInt(100000);
        try {
            param = req.getParameter("jsonStr");
            WriteLog.write("GpPayUtilHttp", ind + ":request:" + param);
            if (!StringIsNull(param)) {
                JSONObject msg = JSONObject.parseObject(param);
                String cmd = msg.getString("cmd");//
                if ("GetPayUrl".equals(cmd)) {//获取支付链接
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    long orderid = msg.getLongValue("orderid");//Gp订单ID
                    resultmsg = new BillpayGpTicket(orderid, orderNumber).pay(msg, Money, GoodDesc, ind);
                }
                else if ("RefuseOrder".equals(cmd)) {//拒单退款
                    long orderid = msg.getLongValue("orderid");//订单ID
                    resultmsg = new BillpayRefundRefuseOrder().refund(orderid);
                }
                else if ("ConfirmOrder".equals(cmd)) {//确认出票 创建分润记录
                    resultmsg = new GpConfimOrder().ConfirmOrder(msg, ind);
                }
                else if ("WithHoldMoneyUrl".equals(cmd)) {//代扣款获取支付链接
                    Double ServMoney = 0d;
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    resultmsg = new BillpayWithHoldMoney(orderNumber).pay(msg, Money, GoodDesc, ServMoney, ind);
                }
                else if ("PayTest".equals(cmd)) {//支付链接测试
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    resultmsg = new BillpayAndShareProfitTest().pay(orderNumber, Money, GoodDesc, "46");
                }
                else if ("signTest".equals(cmd)) {
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    resultmsg = new Pkipair().signMsg(GoodDesc, "46");
                }
                else if ("refundTicket".equals(cmd)) {//退票退款
                    resultmsg = new BillPayTicketRefundOrder().TicketRefund(msg, ind);
                }
                else if ("GetChangePayUrl".equals(cmd)) {//快钱机票改签支付
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    long orderid = msg.getLongValue("orderid");//Gp订单ID
                    resultmsg = new BillPayGpChangePay(orderid, orderNumber).pay(msg, Money, GoodDesc, ind);
                }
                else if ("Wappay".equalsIgnoreCase(cmd)) {//wap快钱端手机支付
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    long orderid = msg.getLongValue("orderid");//Gp订单ID
                    resultmsg = new BillpayGpWapTicket(orderid, orderNumber).pay(msg, Money, GoodDesc, ind);
                }
                else if ("WapChangePay".equals(cmd)) {//改签快钱wap钱支付
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    long orderid = msg.getLongValue("orderid");//Gp订单ID
                    resultmsg = new BillpayGpWapChangePay(orderid, orderNumber).pay(msg, Money, GoodDesc, ind);

                }
                else if ("GpWapAlipay".equals(cmd)) {//Gp非公务票业务支付宝
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    long orderid = msg.getLongValue("orderid");//Gp订单ID
                    resultmsg = new GpWapAlipay(orderid, orderNumber).pay(msg, Money, GoodDesc, ind);
                }
                else if ("GpWapChangeAlipay".equalsIgnoreCase(cmd)) {//Gp非公务票改签获取支付链接
                    String orderNumber = msg.getString("orderNumber");//改签订单号
                    Double Money = msg.getDouble("Money");//金额
                    String GoodDesc = msg.getString("GoodDesc");//商品描述
                    long orderid = msg.getLongValue("orderid");//Gp订单ID
                    resultmsg = new GpWapChangeAlipay(orderid, orderNumber).pay(msg, Money, GoodDesc, ind);
                }
                else if ("YeePosCreateWithHoldMoney".equals(cmd)) {//yeepos创建订单接口
                    String orderNumber = msg.getString("orderNumber");//订单号
                    Double Money = msg.getDouble("Money");//金额
                    resultmsg = new YeePosWithHoldMoney(orderNumber).pay(msg, Money, ind);
                }
            }
            else {
                resultmsg = "提交参数无效";
            }
            WriteLog.write("GpPayUtilHttp", ind + ":response:" + resultmsg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        out.print(resultmsg);
        out.flush();
        out.close();
    }

    /**字符串是否为空*/
    public static boolean StringIsNull(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }
}
