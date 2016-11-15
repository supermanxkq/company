package com.ccservice.b2b2c.atom.pay;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alipay.util.Payment;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.AlipayConfig;

/**
 * @author hanmh 支付宝即时到帐批量退款无密接口
 * 
 */
@SuppressWarnings("serial")
public class AlipayrefundDemo extends RefundSupport implements Refund {
    public AlipayrefundDemo(HttpServletRequest request, HttpServletResponse response, Refundhelper refundhelper) {
        super(request, response, refundhelper);
    }

    public static DateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmmss");

    public static DateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static void main(String[] args) {
        new AlipayrefundDemo(null, null, null).refund();
    }

    @Override
    public void refund() {
        String paygateway = "https://mapi.alipay.com/gateway.do?";
        String service = "refund_fastpay_by_platform_nopwd";// 接口名称
        String partner = "2088701454373226";// 合作者身份ID
        String key = "c9c0w3opz4wdmv6fjbxmtybvlg0t66b6";
        String _input_charset = "UTF-8";
        String notify_url = "www.baidu.com";// "http://" + request.getServerName() + ":" + request.getServerPort()
        //+ "/cn_interface/alipayprofitnotify";// 服务器异步通知页面路径
        // 可空
        String dback_notify_url = "";// 充退通知地址 可空
        String detail_data = getBatch_num1();// 单笔数据集
        Date date = new Date(System.currentTimeMillis());
        //Refundtrade refundtrade = this.createRefundtrade(detail_data, refundhelper.getRefundinfos());
        // 退款批次号，传递的每一个批次号都必须保证唯一性。格式为：退款日期（8位当天日期）+流水号（3～24位，不能接受“000”，但是可以接受英文字符）。
        String batch_no = dateformat.format(date) + "62312954";//batch_no组成格式固定，用于成功通知中ID提取，请勿随意改动。
        String refund_date = timeformat.format(date);// 退款请求时间
        //WriteLog.write("支付宝退款", refundhelper.getOrderid() + "退款数据集:" + detail_data);
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
            WriteLog.write("支付宝退款", "url:" + url);
            try {
                URL neturl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                SAXReader reader = new SAXReader();
                Document document = reader.read(connection.getInputStream());
                Element root = document.getRootElement();
                is_success = root.elementTextTrim("is_success");
                System.out.println(root.elementTextTrim("error"));
                //response.setContentType("text/plain; charset=utf-8");
                //PrintWriter pw = response.getWriter();
                if (is_success.equals("F")) {
                    WriteLog.write("支付宝退款", detail_data + "申请退款失败：" + root.elementTextTrim("error"));
                    //pw.print("申请退款失败" + root.elementTextTrim("error"));
                }
                else {
                    //pw.print("申请退款成功");
                    WriteLog.write("支付宝退款", "退票退款申请成功");
                }
                //pw.flush();
                //pw.close();
            }
            catch (Exception e) {

            }
        }
    }

    @SuppressWarnings("unused")
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return prestr;
    }

    public String getBatch_num1() { // /
        return "2013122935963563^1272.0^机票退款|13947304048^hyccservice@126.com^36.0^退票分润扣款";
    }

    /**
     * 原付款支付宝交易号^退交易金额^退款理由|转出人Email（原收到分润金额的账户）^转出人userId^转入人Email（原付出分润金额的账户）^转入人userId^退款金额^退款理由|转出人Email^转出人userId^转入人Email^转入人userId^退款金额^退款理由
     * 2012120720057239^2.0^机票退款|yihanzhiming@sina.com^hyccservice@126.com^1.0^退票分润扣款	 * 
     * @param royalty
     * @return
     */
    public String getBatch_num(List<Refundinfo> refundinfos) { // /
        // 原付款支付宝交易号^退交易金额^退款理由$被收费人Email（也就是在交易的时候支付宝收取服务费的账户）^被收费人userId^退款金额^退款理由|转出人Email（原收到分润金额的账户）^转出人userId^转入人Email（原付出分润金额的账户）^转入人userId^退款金额^退款理由|转出人Email^转出人userId^转入人Email^转入人userId^退款金额^退款理由
        String royaltysb = "";
        Iterator<Refundinfo> it = refundinfos.iterator();
        String ordernumber = refundhelper.getOrdernumber();
        while (it.hasNext()) {
            Refundinfo refundinfo = it.next();
            royaltysb += refundinfo.getTradeno() + "^" + refundinfo.getRefundprice() + "^退款";
            Map<String, Float> royalty = refundinfo.getRoyalty_parameters();
            if (royalty != null && royalty.size() > 0) {
                Iterator<Map.Entry<String, Float>> agentiterator = royalty.entrySet().iterator();
                for (; agentiterator.hasNext();) {
                    Map.Entry<String, Float> entery = agentiterator.next();
                    String account = entery.getKey();
                    float money = entery.getValue();
                    if (money < 0) {
                        money = 0 - money;
                    }
                    royaltysb += "|" + account + "^^" + AlipayConfig.getInstance().getSellerEmail() + "^^" + money
                            + "^订单" + ordernumber + "退款返还分润";
                }
            }
            if (it.hasNext())
                royaltysb += "#";
        }
        WriteLog.write("支付宝退款", "退款数据集：" + royaltysb);
        return royaltysb.toString();
    }

}
