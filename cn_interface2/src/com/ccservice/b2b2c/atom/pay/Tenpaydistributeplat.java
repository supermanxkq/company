package com.ccservice.b2b2c.atom.pay;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayHttpClient;
import com.tenpay.util.TenpayUtil;

/**
 * 
 * @author wzc
 * 财付通支付后直接分账到平台财付通账号
 */
public class Tenpaydistributeplat {

    static final long serialVersionUID = 1L;

    static Logger logger = Logger.getLogger(Tenpaydistribute.class.getSimpleName());

    public static void main(String[] args) {
        distribute("1219817301201407080699286793", 3544f, "DLQA1407221904379");
    }

    public static String distribute(String tradeno, float tradeprice, String extra_common_param) {
        WriteLog.write("Tenpaydistribute_plat", "订单" + extra_common_param + ",财付通交易异步分润开始");
        String paygateway = "https://mch.tenpay.com/cgi-bin/split.cgi"; // 支付接口（不可以修改）
        String service = "3";
        String partner = TenpayConfig.getInstance().getPartnerID();
        //密钥
        String key = TenpayConfig.getInstance().getKey();
        String out_bill_no = extra_common_param.length() > 25 ? extra_common_param.substring(0, 20)
                : extra_common_param + "";
        String royalty_parameters = "1779015454^" + (int) (tradeprice * 100) + "^1";
        WriteLog.write("Tenpaydistribute_plat", extra_common_param + "分润明细：" + royalty_parameters);
        if (royalty_parameters == null || royalty_parameters.length() == 0) {
            return "";
        }
        String trade_no = tradeno;
        SortedMap<String, String> map = new TreeMap<String, String>();
        map.put("paygateway", paygateway);
        map.put("cmdno", service);//业务代码, 财付通分账接口填  3
        map.put("bargainor_id", partner);//商家的商户号,由腾讯公司唯一分配
        map.put("fee_type", "1");//现金支付币种，目前只支持人民币，码编请参见附件中的
        map.put("return_url", "http://127.0.0.1/");
        map.put("bus_type", "97");//业务类型，整数值，代表分账处理规则与业务参数编码规则，暂固定为97
        map.put("bus_desc", "");
        map.put("version", "4");
        map.put("sp_billno", out_bill_no);//商户系统内部的定单号，此参数仅在对账时提供。 
        map.put("transaction_id", trade_no);
        map.put("total_fee", (int) (tradeprice * 100) + "");//
        map.put("bus_args", royalty_parameters);
        //通信对象
        TenpayHttpClient httpClient = new TenpayHttpClient();
        //证书必须放在用户下载不到的目录，避免证书被盗取
        //设置CA证书
        httpClient.setCaInfo(new File("d:/cacert.pem"));
        //设置个人(商户)证书
        httpClient.setCertInfo(new File("d:/" + partner + ".pfx"), partner);
        try {
            String content = TenpayUtil.getRequestURL(map, key);
            WriteLog.write("Tenpaydistribute_plat", content);
            httpClient.setReqContent(content);
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        Map m = new HashMap();
        if (httpClient.call()) {
            String content = httpClient.getResContent();
            WriteLog.write("Tenpaydistribute_plat", content);
            try {
                m = TenpayUtil.doParse(content);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if ("0".equals(m.get("pay_result"))) {
                WriteLog.write("Tenpaydistribute_plat", tradeno + "财付通交易异步分润成功");
            }
            else {
                WriteLog.write("Tenpaydistribute_plat", tradeno + "财付通交易异步分润失败：" + m.get("pay_info"));
            }
        }
        catch (Exception e) {
            logger.error(tradeno + "财付通交易异步分润异常", e.fillInStackTrace());
            WriteLog.write("Tenpaydistribute_plat", tradeno + "财付通交易异步分润异常" + e.toString());
        }
        return "";

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
