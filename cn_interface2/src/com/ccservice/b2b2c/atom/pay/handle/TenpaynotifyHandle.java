package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.Tenpaydistributeplat;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayUtil;
import com.tenpay.util.XMLUtil;
import com.yeepay.util.HttpsUtil;

/**
 * 
 * @author wzc
 * 财付通支付成功通知
 *
 */
public class TenpaynotifyHandle extends NotifyHandleSupport {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        WriteLog.write("Tenpay_pay", "××财付通支付成功通知:支付成功");
        String partner = TenpayConfig.getInstance().getPartnerID(); // 支付宝合作伙伴id
        String key = TenpayConfig.getInstance().getKey(); //
        String alipayNotifyURL = "https://gw.tenpay.com/gateway/verifynotifyid.xml";
        SortedMap reqmap = new TreeMap();
        reqmap.put("partner", partner);
        reqmap.put("notify_id", request.getParameter("notify_id"));
        reqmap.put("paygateway", alipayNotifyURL);
        //设置请求内容
        String responseTxt = "";
        String trade_state = "";
        String trade_mode = "";
        try {
            responseTxt = (new String(HttpsUtil.post(TenpayUtil.getRequestURL(reqmap, key), "", "gbk")));
            Map map = XMLUtil.doXMLParse(responseTxt);
            responseTxt = map.get("retcode").toString();
            trade_mode = map.get("trade_mode").toString();
            trade_state = map.get("trade_state").toString();
        }
        catch (Exception e) {
            WriteLog.write("Tenpay_pay", e.getMessage() + "");
            e.printStackTrace();
        }
        WriteLog.write("Tenpay_pay", responseTxt);
        SortedMap params = new TreeMap();
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
            WriteLog.write("Tenpay_pay", name + ":" + valueStr);
        }
        String mysign = TenpayUtil.createSign(params, key);
        try {
            response.setContentType("text/plain; charset=utf-8");
            PrintWriter out = response.getWriter();
            String extra_common_param = new String(request.getParameter("attach").getBytes("ISO-8859-1"), "UTF-8");
            if (mysign.equals(request.getParameter("sign")) && "0".equals(responseTxt) && "0".equals(trade_state)
                    && "1".equals(trade_mode)) {
                String get_order = request.getParameter("out_trade_no") == null ? "" : request
                        .getParameter("out_trade_no");
                String trade_no = request.getParameter("transaction_id") == null ? "" : request
                        .getParameter("transaction_id");
                String get_total_fee1 = request.getParameter("total_fee") == null ? "0" : request
                        .getParameter("total_fee");// 支付金额
                float get_total_fee = Float.valueOf(get_total_fee1) / 100;
                String trade_status = request.getParameter("trade_state") == null ? "" : request
                        .getParameter("trade_state");//交易类型
                WriteLog.write("Tenpay_pay", "alipay交易：" + trade_no + ":通知返回信息:" + extra_common_param + ";执行交易成功订单处理;"
                        + get_order + ";" + get_total_fee + ";" + trade_status);
                if (trade_status.equals("0")) {
                    super.orderHandle(extra_common_param, trade_no, Float.valueOf(get_total_fee), Paymentmethod.Tenpay,
                            "");
                    Tenpaydistributeplat.distribute(trade_no, Float.valueOf(get_total_fee), extra_common_param);
                    out.write("success"); // 注意一定要返回给支付宝一个成功的信息(不包含HTML脚本语言)
                    out.flush();
                    out.close();
                }
            }
            else {
                WriteLog.write("Tenpay_pay", "××交易失败");
                out.write("fail");
                out.write(mysign + "-------" + request.getParameter("sign") + "<br>");
                out.flush();
                out.close();
            }
        }
        catch (IOException i) {
            WriteLog.write("Tenpay_pay", "××财付通支付成功IO异常" + i.fillInStackTrace());
        }

    }
}
