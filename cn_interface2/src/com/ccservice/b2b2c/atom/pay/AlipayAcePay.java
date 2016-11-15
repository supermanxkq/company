package com.ccservice.b2b2c.atom.pay;

import java.io.StringReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alipay.util.AlipaySubmit;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.handle.PayHandle;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.AlipayConfig;

/**
 * 
 * @author wzc
 * 支付宝代扣接口
 *
 */
public class AlipayAcePay extends PaySupport {

    private String payacount = "";

    public AlipayAcePay(Payhelper payhelper) {
        super(payhelper);
    }

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

    public String pay(String payacc) throws Exception {
        String result = "";
        String host = "";
        payacount = payacc;
        try {
            String localhost = InetAddress.getLocalHost().toString().split("/")[1];
            host = localhost;
            WriteLog.write("alipay_ace", "serverhost:" + host);
        }
        catch (Exception e) {

        }
        if (payacount != null && !"".equals(payacount)) {
            WriteLog.write("alipay_ace", "localhost:" + host + ":account:" + payacount);
            String notify_url = "http://127.0.0.1/cn_interface/AlipayNotifyHandle";
            WriteLog.write("alipay_ace", "localhost:" + notify_url);
            // 调取支付宝工具类生成订单号
            com.alipay.util.UtilDate utilDate = new com.alipay.util.UtilDate();
            //支付通知信息
            String extra_common_param = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();

            //商户网站唯一订单号。支付宝合作商户网站唯一订单号（确保在商户系统中唯一）
            String out_trade_no = payhelper.getOrdernumber() + "Lb" + extra_common_param;
            //金额（须在签约时设定的金额范围内。如果签约时设定的是固定金额，那么须与之一致。）
            String amount = String.valueOf(payhelper.getOrderprice());
            //支付标题（摘要，即对订单的描述。）
            String subject = payhelper.getOrdername();
            //转出支付宝账号
            String trans_account_out = payacount;
            //选填参数
            //代扣模式
            String charge_type = "trade";
            //提成类型
            String royalty_type = "";
            //提成信息集
            String royalty_parameters = payacount + "^" + AlipayConfig.getInstance().getSellerEmail() + "^" + amount
                    + "^" + subject + "ACE付款";
            //操作员ID
            String operator_id = "";
            //把请求参数打包成数组
            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("out_order_no", out_trade_no);
            sParaTemp.put("amount", amount);
            sParaTemp.put("subject", subject);
            sParaTemp.put("trans_account_in", AlipayConfig.getInstance().getSellerEmail());
            sParaTemp.put("trans_account_out", trans_account_out);
            sParaTemp.put("charge_type", charge_type);
            sParaTemp.put("royalty_type", royalty_type);
            sParaTemp.put("royalty_parameters", royalty_parameters);
            sParaTemp.put("operator_id", operator_id);
            //增加基本配置
            sParaTemp.put("service", "cae_charge_agent");
            sParaTemp.put("partner", AlipayConfig.getInstance().getPartnerID());
            sParaTemp.put("notify_url", notify_url);
            sParaTemp.put("_input_charset", AlipayConfig.getInstance().getCharSet());
            sParaTemp.put("type_code", AlipayConfig.getInstance().getPartnerID() + "1000310004");
            String sHtmlText = AlipaySubmit.sendPostInfo(sParaTemp, ALIPAY_GATEWAY_NEW);
            WriteLog.write("alipay_ace", "请求返回结果:" + sHtmlText);

            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(sHtmlText));
            Element root = document.getRootElement();
            String is_success = root.elementTextTrim("is_success");
            if (is_success.equals("F")) {
                WriteLog.write("alipay_acetz",
                        "Ace自动支付失败：" + payhelper.getOrdernumber() + ":" + root.elementTextTrim("error"));
                result = "F," + root.elementTextTrim("error");
            }
            else {
                Element response = root.element("response");
                Element order = response.element("order");
                String out_order_no = order.elementText("out_order_no");
                String status = order.elementText("status");
                String trade_no = order.elementText("trade_no");
                WriteLog.write("alipay_acetz", "Ace自动支付成功：" + trade_no + "：" + out_order_no + ":" + status);
                if ("TRADE_SUCCESS".equalsIgnoreCase(status)) {
                    try {
                        String[] infos = out_order_no.split("Lb")[1].split("Fg");// 支付时传入参数规范
                        String ordernumber = infos[0];// 获取订单号
                        String handleName = infos[1];// 获取handle类名
                        PayHandle payhandle = (PayHandle) Class.forName(
                                PayHandle.class.getPackage().getName() + "." + handleName).newInstance();
                        payhandle.orderHandle(ordernumber, trade_no, Float.valueOf(amount), Paymentmethod.ACEpay, "");
                    }
                    catch (Exception e) {
                        WriteLog.write("alipay_ace", e.fillInStackTrace() + "");
                    }
                    result = "S," + trade_no;
                }
            }
        }
        else {
            result = "F,请提供支付宝账号";
        }
        return result;
    }
}
