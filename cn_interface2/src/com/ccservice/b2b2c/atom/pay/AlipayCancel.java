package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import antlr.Utils;

import com.alipay.util.Alipay_fuction;
import com.alipay.util.Md5Encrypt;
import com.pay.config.AlipayConfig;
import com.yeepay.util.HttpUtil;

/**
 * 
 * 
 * @time 2014年9月23日 下午2:35:37
 * @author wzc
 * 支付宝取消交易接口
 */
public class AlipayCancel {

    public static String paygateway = "https://mapi.alipay.com/gateway.do?";

    public static void AlipayCancelData(String out_order_no) throws IOException {
        AlipayConfig config = AlipayConfig.getInstance();
        String service = "close_trade";
        String partner = config.getPartnerID();
        String _input_charset = config.getCharSet();
        String trade_role = "B";
        Map<String, String> params = new HashMap<String, String>();
        params.put("_input_charset", _input_charset);
        params.put("out_order_no", out_order_no);
        params.put("partner", partner);
        params.put("service", service);
        params.put("trade_role", trade_role);
        params.put("paygateway", paygateway);
        getContent_public(params, config.getKey());
        String ItemUrl_Get = CreateUrl(params, config.getKey());
        System.out.println(ItemUrl_Get);
        System.out.println(HttpUtil.URLGet(ItemUrl_Get, ""));
    }

    public static String CreateUrl(Map<String, String> params, String key) {
        String sign_type = AlipayConfig.getInstance().getSign_type();
        StringBuilder parameter = new StringBuilder(params.remove("paygateway"));
        System.out.println(getContent_public(params, key));
        String sign = Md5Encrypt.md5(getContent_public(params, key));
        String input_charset = params.remove("_input_charset");
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
        return (new StringBuilder(String.valueOf(prestr))).append(privateKey).toString();
    }
}
