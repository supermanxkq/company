package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alipay.util.Alipay_fuction;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.AlipayConfig;

/**
 * 支付支付通知接口 可优化，待优化：小寒
 * @author Administrator 
 * 
 */
@SuppressWarnings("serial")
public class AlipayNotifyHandle extends NotifyHandleSupport {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        WriteLog.write("alipayTZ", "××支付宝支付成功通知:支付成功");
        String partner = AlipayConfig.getInstance().getPartnerID(); // 支付宝合作伙伴id
        String privateKey = AlipayConfig.getInstance().getKey(); // 支付宝安全校验码(账户内提取)
        String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?partner=" + partner + "&notify_id="
                + request.getParameter("notify_id");
        // 获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
        String responseTxt = Alipay_fuction.checkurl(alipayNotifyURL);
        Map params = new HashMap();
        // 获得POST 过来参数设置到新的params中
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        String mysign = Alipay_fuction.sign(params, privateKey);
        PrintWriter out = null;
        String result = "success";
        String extra_common_param = "";
        try {
            response.setContentType("text/plain; charset=utf-8");
            out = response.getWriter();
            extra_common_param = new String(request.getParameter("extra_common_param").getBytes("ISO-8859-1"), "UTF-8");
            if (mysign.equals(request.getParameter("sign")) && responseTxt.equals("true")) {
                String get_order = request.getParameter("out_trade_no") == null ? "" : request
                        .getParameter("out_trade_no");
                String trade_no = request.getParameter("trade_no") == null ? "" : request.getParameter("trade_no");
                String get_total_fee = request.getParameter("total_fee") == null ? "" : request
                        .getParameter("total_fee");// 支付金额
                String trade_status = request.getParameter("trade_status") == null ? "" : request
                        .getParameter("trade_status");//交易类型
                String refund_status = request.getParameter("refund_status") == null ? "" : request
                        .getParameter("refund_status");
                //                [2016-06-08 17:03:29.560] 
                //                alipay交易：2016060821001004270200396297:通知返回信息:HTSA201606081700009FgAirnofiryHandle;
                //                执行交易成功订单处理;HTSA20160608170000920160608170128;1780.00;TRADE_SUCCESS;
                WriteLog.write("alipayTZ", extra_common_param + ":alipay交易：" + trade_no + ":通知返回信息:"
                        + extra_common_param + ";执行交易成功订单处理;" + get_order + ";" + get_total_fee + ";" + trade_status
                        + ";" + refund_status);
                if (refund_status != null && "REFUND_SUCCESS".equals(refund_status.toUpperCase())) {// 退款通知，不做处理
                    //                    out.write("success");
                    //                    out.flush();
                    //                    out.close();
                    //                    return;
                }
                if (trade_status.equals("WAIT_BUYER_PAY")) {// 等待买家付款;
                    //                    out.write("success");
                    //                    out.flush();
                    //                    out.close();
                    //                    return;
                }
                else if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {//支付成功
                    super.orderHandle(extra_common_param, trade_no, Float.valueOf(get_total_fee), Paymentmethod.ALIPAY,
                            "");
                    //                    out.write("success"); // 注意一定要返回给支付宝一个成功的信息(不包含HTML脚本语言)
                    //                    out.flush();
                    //                    out.close();
                }
            }
            else {
                logger.error("××交易失败");
                result = "fail";
                //                out.write("fail");
                // 打印，收到消息比对sign的计算结果和传递来的sign是否匹配
                //                out.write(mysign + "-------" + request.getParameter("sign") + "<br>");
                //                out.flush();
                //                out.close();
            }
        }
        catch (IOException i) {
            logger.error("××支付宝支付成功IO异常", i.fillInStackTrace());
        }
        finally {
            WriteLog.write("alipayTZ", extra_common_param + ":alipay交易:" + result);
            out.write(result);// 注意一定要返回给支付宝一个成功的信息(不包含HTML脚本语言)
            out.flush();
            out.close();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doGet(request, response);
    }

}
