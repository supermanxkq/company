package com.ccservice.b2b2c.atom.pay;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alipay.util.Payment;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.pay.config.AlipayConfig;

/**
 * @author hanmenghui 支付圈签约查询接口
 */
@SuppressWarnings("serial")
public class Alipaypartnercheck {

    public static boolean partnercheck(String user_email) {
        String paygateway = "https://mapi.alipay.com/gateway.do?";
        String service = "query_customer_protocol";
        String partner = AlipayConfig.getInstance().getPartnerID();// 合作者身份ID
        String key = AlipayConfig.getInstance().getKey();
        String _input_charset = "UTF-8";
        String biz_type = "10004";// 10004：机票代扣款业务
        Map<String, String> map = new HashMap<String, String>();
        map.put("paygateway", paygateway);
        map.put("biz_type", biz_type);
        map.put("_input_charset", _input_charset);
        map.put("partner", partner);
        map.put("service", service);
        map.put("user_email", user_email);
        String url = Payment.CreateUrl(map, key);
        try {
            URL neturl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            SAXReader reader = new SAXReader();
            Document document = reader.read(connection.getInputStream());
            Element root = document.getRootElement();
            String is_success = root.elementTextTrim("is_success");
            if (is_success.equals("T")) {
                String charge_agent = root.elementTextTrim("charge_agent");
                String refund_charge = root.elementTextTrim("refund_charge");
                WriteLog.write("支付宝签约", user_email + ":" + refund_charge);
                if ("T".equals(refund_charge)) {
                    return true;
                }
                return false;
            }
            else {
                String error = root.elementTextTrim("error");
                WriteLog.write("支付宝签约", user_email + ":" + error);
                return false;

            }
        }
        catch (Exception e) {

        }
        return false;
    }

    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";
        try {
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = params.get(key);
                if (value.length() == 0) {
                    continue;
                }
                if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                    prestr = prestr + key + "=" + value;
                }
                else {
                    prestr = prestr + key + "=" + value + "&";
                }
            }
            System.out.print("拼接后的字符串：" + prestr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return prestr;
    }

}
