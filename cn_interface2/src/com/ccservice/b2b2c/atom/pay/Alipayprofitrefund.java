package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.util.Util;
import com.pay.config.AlipayConfig;

/**
 * @author hanmh 停用
 * 支付宝即时到帐批量退款无密接口
 *
 */
@SuppressWarnings("serial")
public class Alipayprofitrefund extends HttpServlet {
    public static DateFormat dateformat = new SimpleDateFormat("yyyyMMdd");

    public static DateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    Log logger = LogFactory.getLog(Alipayprofitrefund.class);

    Refundhelper refundhelper = null;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String helpername = request.getParameter("helpername");

        try {
            long orderid = Long.parseLong(new String(request.getParameter("orderid").getBytes("ISO8859-1"), "UTF-8"));
            refundhelper = (Refundhelper) Class.forName(Refundhelper.class.getPackage().getName() + "." + helpername)
                    .getConstructor(long.class).newInstance(orderid);

        }
        catch (Exception e1) {
            logger.error("**你所填写的helper类名不存在或不正确。请正确填写:" + helpername, e1.fillInStackTrace());
        }
        String url = "https://mapi.alipay.com/gateway.do";
        String service = "refund_fastpay_by_platform_nopwd";//接口名称		
        String partner = AlipayConfig.getInstance().getPartnerID();//合作者身份ID
        String key = AlipayConfig.getInstance().getKey();

        String _input_charset = "UTF-8";
        String sign_type = "MD5";
        String sign = "";
        String notify_url = "http://" + request.getServerName() + ":" + request.getServerName() + "/alipayprofitnotify";//服务器异步通知页面路径 可空
        String dback_notify_url = "";//充退通知地址 可空
        Date date = new Date(System.currentTimeMillis());
        String batch_no = "";//dateformat.format(date)+refundhelper.getOrdernumber()+"345678";//退款批次号，传递的每一个批次号都必须保证唯一性。格式为：退款日期（8位当天日期）+流水号（3～24位，不能接受“000”，但是可以接受英文字符）。
        String refund_date = timeformat.format(date);//退款请求时间
        String detail_data = "";//getBatch_num(refundhelper.getRoyalty_parameters());//单笔数据集
        String batch_num = detail_data.split("#").length + "";//退款总笔数
        String use_freeze_amount = "N";//是否使用冻结金额退款 可空 默认N
        String return_type = "xml"; //申请结果返回类型 可空 默认XML
        Map<String, String> map = new HashMap<String, String>();
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
        String psb = createLinkString(map);
        psb += key;
        try {
            sign = Util.MD5(psb);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        map.put("sign_type", sign_type);
        map.put("sign", sign);
        psb = createLinkString(map);
        try {
            String is_success = "";
            if (false) {
                //                URL neturl = new URL(url);
                //                HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
                //                connection.setDoInput(true);
                //                connection.setDoOutput(true);
                //                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                //                out.write(psb);
                //                out.flush();
                //                out.close();
                //                SAXReader reader = new SAXReader();
                //                Document document = reader.read(connection.getInputStream());
                //                Element root = document.getRootElement();
                //                is_success = root.elementTextTrim("is_success");
                //                System.out.println(is_success);
                //                if (is_success.equals("F")) {
                //                    System.out.println(root.elementTextTrim("error"));
                //                }
            }
            if (is_success.equals("T")) {

                map = new HashMap<String, String>();
                map.put("service", service);
                map.put("partner", partner);
                map.put("_input_charset", _input_charset);
                map.put("notify_url", notify_url);
                map.put("dback_notify_url", dback_notify_url);
                map.put("batch_no", batch_no);
                map.put("refund_date", refund_date);
                map.put("batch_num", "1");
                //平台退款格式：原付款支付宝交易号^退款金额^退款理由$被收费人Email（也就是在交易的时候支付宝收取服务费的账户）^被收费人userId^退款金额^退款理由
                detail_data = "";//refundhelper.getTradeno()+"^"+refundhelper.getRefundprice()+"^Agreementrefund";
                map.put("batch_num", batch_num);
                map.put("detail_data", detail_data);
                map.put("use_freeze_amount", use_freeze_amount);
                map.put("return_type", return_type);
                psb = createLinkString(map);
                psb += key;
                try {
                    sign = Util.MD5(psb);
                }
                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                map.put("sign_type", sign_type);
                map.put("sign", sign);
                psb = createLinkString(map);
                URL neturlr = new URL(url);
                HttpURLConnection connectionr = (HttpURLConnection) neturlr.openConnection();
                connectionr.setDoInput(true);
                connectionr.setDoOutput(true);
                OutputStreamWriter outr = new OutputStreamWriter(connectionr.getOutputStream(), "UTF-8");
                outr.write(psb.toString());
                outr.flush();
                outr.close();
                SAXReader readerr = new SAXReader();
                Document documentr = readerr.read(connectionr.getInputStream());
                Element rootr = documentr.getRootElement();
                is_success = rootr.elementTextTrim("is_success");
                System.out.println(is_success);
                if (is_success.equals("F")) {
                    System.out.println(rootr.elementTextTrim("error"));
                }
                if (is_success.equals("T")) {
                    System.out.println(rootr.elementTextTrim("error"));
                }
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

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
        return prestr;
    }

    /**
     * 分润方退款格式：原付款支付宝交易号^0^退款理由|转出人Email（原收到分润金额的账户）^转出人userId^转入人Email（原付出分润金额的账户）^转入人userId^退款金额^退款理由
     * @param royalty
     * @return
     */
    public String getBatch_num(Map<Customeragent, Float> royalty) {
        String royaltysb = "";
        if (royalty != null && royalty.size() > 0) {
            Iterator<Map.Entry<Customeragent, Float>> agentiterator = royalty.entrySet().iterator();

            for (; agentiterator.hasNext();) {
                Map.Entry<Customeragent, Float> entery = agentiterator.next();

                Customeragent agent = entery.getKey();
                float money = entery.getValue();

                boolean hascount = (agent.getAlipayaccount() != null && agent.getAlipayaccount() != "") ? true : false;
                if (agent.getId() != 46 && hascount) {
                    if (agentiterator.hasNext()) {

                        royaltysb = "|" + agent.getAlipayaccount() + "^^" + AlipayConfig.getInstance().getSellerEmail()
                                + "^" + AlipayConfig.getInstance().getPartnerID() + "^" + money + "^ticketrefund"
                                + royaltysb;
                    }
                    else {
                        //royaltysb=refundhelper.getTradeno() + "^0^ticketrefund|"
                        //	+ agent.getAlipayaccount() + "^^" +AlipayConfig.sellerEmail + "^"+AlipayConfig.partnerID+"^"+money+"^ticketrefund"+royaltysb;

                    }

                }
            }
            return royaltysb.toString();
        }
        else {
            return null;
        }
    }

}
