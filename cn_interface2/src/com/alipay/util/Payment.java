package com.alipay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pay.config.AlipayConfig;

public class Payment {

    Log log = LogFactory.getLog(Payment.class);

    public Payment() {
    }

    public static String CreateUrlfen(String paygateway, String service, String sign_type, String out_trade_no,
            String input_charset, String partner, String key, String show_url, String body, String total_fee,
            String payment_type, String seller_email, String subject, String notify_url, String return_url,
            String paymethod, String defaultbank, String royalty_type, String royalty_parameters, String buyer_email,
            String extra_common_param) {
        Map params = new HashMap();
        params.put("service", service);
        params.put("partner", partner);
        params.put("subject", subject);
        params.put("body", body);
        params.put("out_trade_no", out_trade_no);
        params.put("total_fee", total_fee);
        params.put("show_url", show_url);
        params.put("payment_type", payment_type);
        params.put("seller_email", seller_email);
        if (isNotnullorEpt(return_url)) {
            params.put("return_url", return_url);
        }
        if (isNotnullorEpt(notify_url)) {
            params.put("notify_url", notify_url);
        }
        params.put("paymethod", paymethod);
        params.put("defaultbank", defaultbank);
        params.put("_input_charset", input_charset);
        params.put("royalty_type", royalty_type);
        params.put("royalty_parameters", royalty_parameters);
        params.put("buyer_email", buyer_email);
        params.put("extra_common_param", extra_common_param);
        Alipay_fuction af = new Alipay_fuction();
        String sign = Md5Encrypt.md5(Alipay_fuction.getContent_public(params, key));
        String parameter = "";
        parameter = (new StringBuilder(String.valueOf(parameter))).append(paygateway).toString();
        List keys = new ArrayList((Collection) params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            try {
                parameter = (new StringBuilder(String.valueOf(parameter))).append(keys.get(i)).append("=")
                        .append(URLEncoder.encode((String) params.get(keys.get(i)), input_charset)).append("&")
                        .toString();
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        parameter = (new StringBuilder(String.valueOf(parameter))).append("sign=").append(sign).append("&sign_type=")
                .append(sign_type).toString();
        return parameter;
    }

    public static String CreateUrl(Map<String, String> params, String key) {
        String input_charset = params.get("_input_charset");
        String sign_type = AlipayConfig.getInstance().getSign_type();
        StringBuilder parameter = new StringBuilder(params.remove("paygateway"));
        String sign = Md5Encrypt.md5(Alipay_fuction.getContent_public(params, key));
        for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
            String paraname = iterator.next();
            String paraval = params.get(paraname);
            if (paraval == null || paraval.length() == 0) {
                continue;
            }
            parameter.append(paraname + "=");
            try {
                parameter.append(URLEncoder.encode(paraval, input_charset));
            }
            catch (UnsupportedEncodingException e) {

                System.out.println("编码出错：");
                e.printStackTrace();
            }
            parameter.append("&");
        }
        parameter.append("sign=" + sign + "&sign_type=" + sign_type);
        return parameter.toString();

    }

    /**
     * 退款的协议
     */
    public static String CreateUrltui(String paygateway, String input_charset, String service, String partner,
            String sign_type, String batch_no, String refund_date, String batch_num, String detail_data,
            String notify_url, String key) {

        Map params = new HashMap();
        params.put("_input_charset", input_charset);
        params.put("service", service);
        params.put("partner", partner);
        params.put("batch_no", batch_no);
        params.put("refund_date", refund_date);
        params.put("batch_num", batch_num);
        params.put("detail_data", detail_data);
        params.put("notify_url", notify_url);

        String prestr = "";

        prestr = prestr + key;
        // System.out.println("prestr=" + prestr);

        String sign = Md5Encrypt.md5(Alipay_fuction.getContent_public(params, key));

        String parameter = "";
        parameter = parameter + paygateway;

        List keys = new ArrayList(params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String value = (String) params.get(keys.get(i));
            if (value == null || value.trim().length() == 0) {
                continue;
            }
            try {
                parameter = parameter + keys.get(i) + "=" + URLEncoder.encode(value, input_charset) + "&";
            }
            catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        }

        parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

        return parameter;

    }

    /**
     * 签约支付圈
     * 
     * @param paygateway
     * @param input_charset
     * @param service
     * @param partner
     * @param sign_type
     * @param email
     * @param key
     * @return
     */
    public static String CreateUrlqian(String paygateway, String input_charset, String service, String partner,
            String sign_type, String email, String key) {

        Map params = new HashMap();
        params.put("_input_charset", input_charset);
        params.put("service", service);
        params.put("partner", partner);
        params.put("email", email);

        String prestr = "";

        prestr = prestr + key;
        // System.out.println("prestr=" + prestr);

        String sign = com.alipay.util.Md5Encrypt.md5(Alipay_fuction.getContent_public(params, key));

        String parameter = "";
        parameter = parameter + paygateway;

        List keys = new ArrayList(params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String value = (String) params.get(keys.get(i));
            if (value == null || value.trim().length() == 0) {
                continue;
            }
            try {
                parameter = parameter + keys.get(i) + "=" + URLEncoder.encode(value, input_charset) + "&";
            }
            catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        }

        parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

        return parameter;

    }

    public static boolean isNotnullorEpt(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

}
