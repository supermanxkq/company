package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.io.SAXReader;

import com.alipay.util.Payment;
import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 
 * @author wzc
 * 要回分润退款的钱(手工操作)
 *
 */
public class RefundShareMoney {
    public static DateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmmss");

    public static DateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    public static void main(String[] args) {
        new RefundShareMoney().refund();
    }
    
    public void refund() {
        String paygateway = "https://mapi.alipay.com/gateway.do?";
        String service = "refund_fastpay_by_platform_nopwd";// 接口名称
        String partner ="2088701454373226";// 合作者身份ID
        String key = "c9c0w3opz4wdmv6fjbxmtybvlg0t66b6";
        String _input_charset = "UTF-8";
        String notify_url = "http://211.103.207.133:8080/cn_interface/alipayprofitnotify";// 服务器异步通知页面路径
        // 可空
        String dback_notify_url = "";// 充退通知地址 可空
        String detail_data = getBatch_num();// 单笔数据集
        Date date = new Date(System.currentTimeMillis());
        // 退款批次号，传递的每一个批次号都必须保证唯一性。格式为：退款日期（8位当天日期）+流水号（3～24位，不能接受“000”，但是可以接受英文字符）。
        String batch_no = dateformat.format(date);//batch_no组成格式固定，用于成功通知中ID提取，请勿随意改动。
        String refund_date = timeformat.format(date);// 退款请求时间
        String batch_num = detail_data.split("#").length + "";// 退款总笔数
        String use_freeze_amount = "N";// 是否使用冻结金额退款 可空 默认N
        String return_type = "xml"; // 申请结果返回类型 可空 默认XML
        Map<String, String> map = new HashMap<String, String>();
        String is_success = "";
        if (detail_data != null && detail_data.length() > 0) {
            map.put("paygateway", paygateway);
            map.put("service", service);
            map.put("partner", partner);
            map.put("_input_charset", _input_charset);
            map.put("notify_url", notify_url);
            map.put("dback_notify_url", dback_notify_url);
            map.put("batch_no", batch_no);
            map.put("refund_date", refund_date);
            map.put("batch_num", batch_num);
            map.put("detail_data", detail_data);
            map.put("use_freeze_amount", use_freeze_amount);
            map.put("return_type", return_type);
            String url = Payment.CreateUrl(map, key);
            WriteLog.write("detail_data", "url:" + url);
            try {
                URL neturl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                SAXReader reader = new SAXReader();
                InputStream input = connection.getInputStream();
                InputStreamReader in = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(in);
                String brstr = "";
                while ((brstr = br.readLine()) != null) {
                    WriteLog.write("detail_data", "返回信息" + brstr);
                    System.out.println(brstr);
                }
            }
            catch (Exception e) {

            }
        }
    }

    public String getBatch_num() { // /
        //String royaltysb = tradenum+"^726.0^退款|15900856299@163.com^^hyccservice@126.com^^2.0^"+pnr+"退废票返还分润|lyl1560@126.com^^hyccservice@126.com^^2.0^"+pnr+"退废票返还分润";
        String royaltysb="2014041661192679^1220^A117740退款";
        WriteLog.write("detail_data", "退款数据集：" + royaltysb);
        return royaltysb.toString();
    }
}
