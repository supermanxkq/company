package com.ccservice.b2b2c.atom.pay;

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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alipay.util.Payment;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Refundtrade;

/**
 *  支付宝程序调用退款
 * 
 */
@SuppressWarnings("serial")
public class AlipayrefundPro {

    public static DateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmmss");

    public static DateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 获取支付信息
     * @return
     */
    public PayEntryInfo findAgentInfo(long agentid) {
        PayEntryInfo info = null;
        String sql = "select  PARTNERID, KEYSTR, SELLEREMAIL, REFUNDKEY, ACCOUNTTYPE, AGENTID,PrivateKey, PublicKey from payinfo with(nolock) where agentid="
                + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        info = new PayEntryInfo();
        if (list.size() == 1) {
            Map map = (Map) list.get(0);
            String pid = map.get("PARTNERID") == null ? "" : map.get("PARTNERID").toString();
            String keystr = map.get("KEYSTR") == null ? "" : map.get("KEYSTR").toString();
            String sellemail = map.get("SELLEREMAIL") == null ? "" : map.get("SELLEREMAIL").toString();
            String agentidstr = map.get("AGENTID") == null ? "" : map.get("AGENTID").toString();
            String privateKey = map.get("PrivateKey") == null ? "" : map.get("PrivateKey").toString();
            String publicKey = map.get("PublicKey") == null ? "" : map.get("PublicKey").toString();
            info.setPid(pid);
            info.setKey(keystr);
            info.setSellemail(sellemail);
            info.setAgentid(Long.valueOf(agentidstr));
            info.setPrivateKey(privateKey);
            info.setPublicKey(publicKey);
        }
        else {
            info = findAgentInfo(46l);
        }
        return info;
    }

    public Refundtrade createRefundtrade(String detail_data, List<Refundinfo> reundinfos, long id, int yewutype,
            String handleClass) {
        Refundtrade refundtrade = new Refundtrade();
        refundtrade.setHandleclass(handleClass);
        refundtrade.setOrderid(id);
        refundtrade.setBtype(yewutype);
        float totalrefundprice = 0;
        for (Refundinfo reundinfo : reundinfos) {
            totalrefundprice += reundinfo.getRefundprice();
        }
        refundtrade.setRefundprice(totalrefundprice);
        refundtrade.setMemo("退款数据集" + detail_data);
        return Server.getInstance().getMemberService().createRefundtrade(refundtrade);
    }

    /**
     * 
     * @param agentid 需要退款平台代理商ID
     * @param notifyurl 退款通知地址
     * @param orderid 退款订单ID 
     * @param ordernumber 接口单号
     * @param refundinfos 退款信息
     * @param yewutype 业务类型 火车票3
     * @param handleClass 退款回调通知类
     * @return
     */
    public String refund(long agentid, String notifyurl, long id, String ordernumber, List<Refundinfo> refundinfos,
            int yewutype, String handleClass) {
        String requestMsg = "";
        String paygateway = "https://mapi.alipay.com/gateway.do?";
        String service = "refund_fastpay_by_platform_nopwd";// 接口名称
        PayEntryInfo pinfo = findAgentInfo(agentid);
        String partner = pinfo.getPid();// 合作者身份ID
        String key = pinfo.getKey();
        String _input_charset = "UTF-8";
        //String notify_url = "http://" + request.getServerName() + ":" + request.getServerPort()
        //+ "/cn_interface/alipayprofitnotify";// 服务器异步通知页面路径
        String notify_url = notifyurl;// 服务器异步通知页面路径
        // 可空
        String dback_notify_url = "";// 充退通知地址 可空
        String detail_data = getBatch_num(refundinfos, pinfo, ordernumber);// 单笔数据集
        Date date = new Date(System.currentTimeMillis());
        Refundtrade refundtrade = this.createRefundtrade(detail_data, refundinfos, id, yewutype, handleClass);
        // 退款批次号，传递的每一个批次号都必须保证唯一性。格式为：退款日期（8位当天日期）+流水号（3～24位，不能接受“000”，但是可以接受英文字符）。
        String batch_no = dateformat.format(date) + refundtrade.getId();//batch_no组成格式固定，用于成功通知中ID提取，请勿随意改动。
        String refund_date = timeformat.format(date);// 退款请求时间
        WriteLog.write("支付宝程序退款", ordernumber + "退款数据集:" + detail_data);
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
            WriteLog.write("支付宝程序退款", "url:" + url);
            try {
                URL neturl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                SAXReader reader = new SAXReader();
                Document document = reader.read(connection.getInputStream());
                Element root = document.getRootElement();
                is_success = root.elementTextTrim("is_success");
                if (is_success.equals("F")) {
                    WriteLog.write("支付宝程序退款", detail_data + "申请退款失败：" + root.elementTextTrim("error"));
                    requestMsg = "申请退款失败" + root.elementTextTrim("error");
                }
                else {
                    requestMsg = "申请退款成功";
                    WriteLog.write("支付宝程序退款", "退票退款申请成功");
                }
            }
            catch (Exception e) {

            }
        }
        return requestMsg;
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

    /**
     * 原付款支付宝交易号^退交易金额^退款理由|转出人Email（原收到分润金额的账户）^转出人userId^转入人Email（原付出分润金额的账户）^转入人userId^退款金额^退款理由|转出人Email^转出人userId^转入人Email^转入人userId^退款金额^退款理由
     * 2012120720057239^2.0^机票退款|yihanzhiming@sina.com^hyccservice@126.com^1.0^退票分润扣款	 * 
     * @param royalty
     * @return
     */
    public String getBatch_num(List<Refundinfo> refundinfos, PayEntryInfo pinfo, String ordernumber) { // /
        // 原付款支付宝交易号^退交易金额^退款理由$被收费人Email（也就是在交易的时候支付宝收取服务费的账户）^被收费人userId^退款金额^退款理由|转出人Email（原收到分润金额的账户）^转出人userId^转入人Email（原付出分润金额的账户）^转入人userId^退款金额^退款理由|转出人Email^转出人userId^转入人Email^转入人userId^退款金额^退款理由
        String royaltysb = "";
        Iterator<Refundinfo> it = refundinfos.iterator();
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
                    royaltysb += "|" + account + "^^" + pinfo.getSellemail() + "^^" + money + "^订单" + ordernumber
                            + "退款";
                }
            }
            if (it.hasNext())
                royaltysb += "#";
        }
        WriteLog.write("支付宝程序退款", "退款数据集：" + royaltysb);
        return royaltysb.toString();
    }

}
