package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.b2b2c.util.HttpUtils;
import com.pay.config.YeepayConfig;
import com.yeepay.util.DigestUtil;

/**
 * @author wzc 易宝支付
 * 
 */
public class Yeepay extends PaySupport implements Pay {
    public Yeepay(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
    }

    private static final String requesturl = "https://www.yeepay.com/app-merchant-proxy/node";

    public void pay(float factorage) throws Exception {
        WriteLog.write("易宝支付", "易宝支付开始，支付金额：" + (payhelper.getOrderprice() + factorage));
        String keyValue = YeepayConfig.getInstance().getKey();
        String p0_Cmd = "Buy";// 固定值“Buy”。
        String p1_MerId = YeepayConfig.getInstance().getPartnerID();// 商户编号
        String selfOrderid = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();// 商户订单号
        String p2_Order = selfOrderid;// 商户订单号
        String paymoney = payhelper.getOrderprice() + factorage + "";
        String p3_Amt = paymoney;// 消费金额
        String p4_Cur = "CNY";// 交易币种 是 - 固定值 “CNY”。
        // p5_Pid 商品名称 否 // Max(50) // 此参数如用到中文,请注意转码。
        String p5_Pid = payhelper.getOrdername();
        WriteLog.write("易宝支付", p5_Pid);
        String p6_Pcat = "";
        String p7_Pdesc = "";
        // String notify_url = "http://211.103.207.134:8080/cn_interface/YeepayNotifyHandle";
        String notify_url = "http://" + request.getServerName() + ":" + request.getServerPort()
                + "/cn_interface/YeepayNotifyHandle";
        WriteLog.write("易宝支付", notify_url);
        // 接收支付结果地址 Max(200) 交易完成后会将交易结果以HTTP协议的形式请求到该地址上，用request接收结果。
        String p8_Url = notify_url;
        String p9_SAF = "0";
        String pa_MP = "";
        String pd_FrpId = "";
        String pr_NeedResponse = "1";// /需要应答 是 固定值“1”。
        String hmac = DigestUtil.getReqMd5HmacForOnlinePayment(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid,
                p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
        WriteLog.write("易宝支付", hmac);
        response.setContentType("text/html; charset=gbk");
        Traderecord traderecord = Server.getInstance().getB2BSystemService()
                .findTradeByOrderunmber(payhelper.getOrdernumber());
        if (traderecord == null) {
            traderecord = new Traderecord();
            try {
                traderecord.setCreateuser("Yeepay");
                traderecord.setGoodsname(payhelper.getOrdername());
                traderecord.setCode(selfOrderid);// 外部订单号。
                traderecord.setOrdercode(payhelper.getOrdernumber());
                traderecord.setPayname("易宝支付");
                traderecord.setPaytype(0);// 0支付宝 1财付通
                traderecord.setState(0);// 0等待支付1支付成功2支付失败
                traderecord.setTotalfee((int) payhelper.getOrderprice());// 支付金额分为单位
                traderecord.setType(payhelper.getTradetype());// 订单类型
                traderecord.setPaymothed("0");// 支付方式
                traderecord.setBankcode("");// 支付银行
                traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
            }
            catch (Exception e) {
            }
        }
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body onload='document.forms.yeepay.submit()'>");
        out.println("<form name='yeepay' action='https://www.yeepay.com/app-merchant-proxy/node' method='POST' target='_self'>");
        out.println("<input type='hidden' name='p0_Cmd'   value='" + p0_Cmd + "'>");
        out.println("<input type='hidden' name='p1_MerId' value='" + p1_MerId + "'>");
        out.println("<input type='hidden' name='p2_Order' value='" + p2_Order + "'>");
        out.println("<input type='hidden' name='p3_Amt'   value='" + p3_Amt + "'>");
        out.println("<input type='hidden' name='p4_Cur'   value='" + p4_Cur + "'>");
        out.println("<input type='hidden' name='p5_Pid'   value='" + p5_Pid + "'>");
        out.println("<input type='hidden' name='p6_Pcat'  value=''>");
        out.println("<input type='hidden' name='p7_Pdesc' value=''>");
        out.println("<input type='hidden' name='p8_Url'   value='" + p8_Url + "'>");
        out.println("<input type='hidden' name='p9_SAF'   value='0'>");
        out.println("<input type='hidden' name='pa_MP'    value=''>");
        out.println("<input type='hidden' name='pd_FrpId' value=''>");
        out.println("<input type='hidden' name='pr_NeedResponse'  value='1'>");
        out.println("<input type='hidden' name='hmac'     value='" + hmac + "'>");
        out.println("<input type='submit' type='hidden'/>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    private static String requestPayback(Map<String, String> parameterMap) {
        try {
            WriteLog.write("易宝支付", "requestPayback");
            InputStream stream = HttpUtils.URLPost(requesturl, parameterMap);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            StringBuffer s = HttpUtils.getStringBufferFormBufferedReader(in);
            return s.toString();
        }
        catch (IOException e) {
            WriteLog.write("易宝支付", "requestPayback异常：" + e.fillInStackTrace());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param serialnumber
     *            日志记录
     */
    public void createOrderrc(String serialnumber) {
        Traderecord traderecord = new Traderecord();
        try {
            traderecord.setCreateuser("Yeepay");
            traderecord.setGoodsname(payhelper.getOrdername());
            traderecord.setCode(serialnumber);// 外部订单号。
            traderecord.setOrdercode(payhelper.getOrdernumber());
            traderecord.setPayname("易宝");
            traderecord.setPaytype(0);// 0支付宝 1财付通
            traderecord.setState(0);// 0等待支付1支付成功2支付失败
            traderecord.setTotalfee((int) payhelper.getOrderprice());// 支付金额分为单位
            traderecord.setType(payhelper.getTradetype());// 订单类型
            traderecord.setPaymothed("Epos");// 支付方式
            traderecord.setBankcode(request.getParameter("pd_FrpId"));// 支付银行
            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (Exception e) {
            WriteLog.write("易宝支付", "交易记录失败" + e.fillInStackTrace());
        }
        WriteLog.write("易宝支付", "提交易宝支付.银行编码：" + request.getParameter("pd_FrpId") + ";交易流水号：" + serialnumber);
    }

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }
}
