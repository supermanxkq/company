package com.ccservice.b2b2c.atom.refund.handle;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayUtil;

/**
 * 
 * @author wzc
 * 财付通退款通知接口
 *
 */
public class TenpayrefundnotifyHandle extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        WriteLog.write("Tenpay退款", "財付通退款成功通知");
        String partner = TenpayConfig.getInstance().getPartnerID(); // 支付宝合作伙伴id
        String key = TenpayConfig.getInstance().getKey(); //
        // 获得POST 过来参数设置到新的params中
        Map requestParams = request.getParameterMap();
        SortedMap params = new TreeMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
            WriteLog.write("Tenpay_pay", name + ":" + valueStr);
        }
        String batch_no = request.getParameter("sp_billno");// 批次号
        WriteLog.write("Tenpay退款", "退款通知请求，批次号：" + batch_no);
        String pay_result = request.getParameter("pay_result");// 处理结果详情
        WriteLog.write("Tenpay退款", "处理结果详情：" + pay_result);
        String ordernumber = batch_no.substring(14);
        Refundtrade fefundtrade = Server.getInstance().getMemberService().findRefundtrade(Long.valueOf(ordernumber));
        String mysign = TenpayUtil.createSign(params, key);
        boolean success = false;
        if (mysign.equals(request.getParameter("sign")) && "0".equals(pay_result)) {
            success = true;
        }
        try {
            String handleclass = fefundtrade.getHandleclass();
            RefundHandle refundhandle = (RefundHandle) Class.forName(
                    RefundHandle.class.getPackage().getName() + "." + handleclass).newInstance();
            refundhandle.refundedHandle(success, fefundtrade.getOrderid(), batch_no);
        }
        catch (Exception e) {
            WriteLog.write("Tenpay退款", batch_no + "退款成功 订单状态更改异常" + e.fillInStackTrace());
        }
    }
}
