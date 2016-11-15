package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alipay.util.Alipay_fuction;
import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * wap支付支付通知接口 wzc
 * @author wzc 
 * <notify>
    <payment_type>8</payment_type>
    <subject>asdfasdfasdf11111</subject>
    <trade_no>2015072000001000780061176327</trade_no>
    <buyer_email>531209940@qq.com</buyer_email>
    <gmt_create>2015-07-20 17:17:07</gmt_create>
    <notify_type>trade_status_sync</notify_type>
    <quantity>1</quantity>
    <out_trade_no>wadfasdfasdfasdf11111</out_trade_no>
    <notify_time>2015-07-20 17:31:48</notify_time>
    <seller_id>2088701454373226</seller_id>
    <trade_status>TRADE_SUCCESS</trade_status>
    <is_total_fee_adjust>N</is_total_fee_adjust>
    <total_fee>0.01</total_fee>
    <gmt_payment>2015-07-20 17:17:08</gmt_payment>
    <seller_email>hyccservice@126.com</seller_email>
    <price>0.01</price>
    <buyer_id>2088702910926783</buyer_id>
    <notify_id>f5eaa0f79e18ea66627ddd1cefc007286c</notify_id>
    <use_coupon>N</use_coupon>
</notify>
 */
@SuppressWarnings("serial")
public class WapAlipayNotifyHandle extends NotifyHandleSupport {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        WriteLog.write("alipayTZ_wap", "××支付宝支付成功通知:支付成功");
        Map params = new HashMap();
        // 获得POST 过来参数设置到新的params中
        Map requestParams = request.getParameterMap();
        String responseTxt = "";
        String notify_data = "";//支付宝返回数据
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            if ("notify_data".equals(name)) {
                try {
                    notify_data = valueStr;
                    Document document = DocumentHelper.parseText(valueStr);
                    Element root = document.getRootElement();
                    String partner = root.elementTextTrim("seller_id");
                    String notify_id = root.elementTextTrim("notify_id");
                    String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?partner=" + partner
                            + "&notify_id=" + notify_id;
                    // 获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
                    responseTxt = Alipay_fuction.checkurl(alipayNotifyURL);
                    WriteLog.write("alipayTZ_wap", "responseTxt:" + responseTxt);
                }
                catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
            params.put(name, valueStr);
        }
        //String mysign = AlipaySubmit.buildRequestMysign(params);
        try {
            response.setContentType("text/plain; charset=utf-8");
            PrintWriter out = response.getWriter();
            WriteLog.write("notify_data", notify_data);
            if (notify_data.length() > 0 && notify_data.contains("trade_status")) {
                Document document = DocumentHelper.parseText(notify_data);
                Element root = document.getRootElement();
                //if (mysign.equals(request.getParameter("sign")) && responseTxt.equals("true")) {
                if ("true".equals(responseTxt)) {
                    String out_trade_no = root.elementTextTrim("out_trade_no");
                    String subject = root.elementTextTrim("subject");
                    String trade_no = root.elementTextTrim("trade_no");
                    String get_total_fee = root.elementTextTrim("total_fee");// 支付金额
                    String trade_status = root.elementTextTrim("trade_status");//交易类型
                    String seller_email = root.elementTextTrim("seller_email");//交易类型
                    WriteLog.write("alipayTZ_wap", "alipay交易：" + trade_no + ":handle:" + subject + ";执行交易成功订单处理;"
                            + out_trade_no + ";" + get_total_fee + ";" + trade_status + ";");
                    if (trade_status.equals("WAIT_BUYER_PAY")) {// 等待买家付款;
                        out.write("success");
                        out.flush();
                        out.close();
                        return;
                    }
                    else if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                        super.orderHandle(subject, trade_no, Float.valueOf(get_total_fee), 15, seller_email);
                        out.write("success"); // 注意一定要返回给支付宝一个成功的信息(不包含HTML脚本语言)
                        out.flush();
                        out.close();
                    }
                }
                else {
                    logger.error("××交易失败");
                    out.write("fail");
                    out.flush();
                    out.close();
                }
            }
            else {
                logger.error("××交易失败");
                out.write("fail");
                out.flush();
                out.close();
            }
        }
        catch (IOException i) {
            logger.error("××支付宝支付成功IO异常", i.fillInStackTrace());
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doGet(request, response);
    }

}
