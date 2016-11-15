package com.alipay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tenpay.util.MD5Util;

public class WeChatPayment {

    Log log = LogFactory.getLog(WeChatPayment.class);

    public WeChatPayment() {}

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

    public static String Sign(Map<String, String> params, String key) {
        String signStr = getContent_public(params, key);
        System.out.println(signStr);
        String sign = MD5Util.MD5Encode(signStr, "UTF-8").toUpperCase();
        return sign;

    }

    public static String getContent_public(Map params, String privateKey) {
        List keys = new ArrayList((Collection) params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) params.get(key);
            if (value == null || value.length() == 0) {
                continue;
            }
            if (i == keys.size() - 1) {
                prestr = (new StringBuilder(String.valueOf(prestr))).append(key).append("=").append(value).toString();
            }
            else {
                prestr = (new StringBuilder(String.valueOf(prestr))).append(key).append("=").append(value).append("&")
                        .toString();
            }
        }
        return (new StringBuilder(String.valueOf(prestr))).append("&key=" + privateKey).toString();
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
