package com.ccservice.b2b2c.atom.pay.test;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.alipay.util.Alipay_fuction;
import com.alipay.util.Md5Encrypt;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.pay.config.AlipayConfig;

/**
 * Servlet implementation class for Servlet: Alipay 支付宝支付类
 * 
 */
public class alipaytest {
    public static final String payment_type = "1";

    public static final String paygateway = "https://mapi.alipay.com/gateway.do?"; // 支付接口（不可以修改）

    public static final String service = "create_direct_pay_by_user";// 快速付款交易服务（不可以修改）

    static final long serialVersionUID = 1L;

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public static void main(String[] args) throws Exception {
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            new alipaytest().pay("ceshiorder" + i + "tt" + r.nextInt(10000));
        }
    }

    public void pay(String ordernumberd) throws Exception {
        WriteLog.write("alipay_pay", ordernumberd + "支付宝交易记录:交易开始");
        String input_charset = AlipayConfig.getInstance().getCharSet();
        String seller_email = AlipayConfig.getInstance().getSellerEmail();
        String partner = AlipayConfig.getInstance().getPartnerID();
        String key = AlipayConfig.getInstance().getKey();
        String ServerName = "www.baidu.com";
        String host = "";
        try {
            String localhost = InetAddress.getLocalHost().toString().split("/")[1];
            //String serverhost = InetAddress.getByName(request.getServerName()).toString().split("/")[1];
            host = localhost;
            WriteLog.write("alipay_pay", "serverhost:" + host);
        }
        catch (Exception e) {
            this.logger.error("获取服务器IP异常", e.fillInStackTrace());
        }
        WriteLog.write("alipay_pay", "localhost:" + ServerName);
        String notify_url = "http://www.baidu.com/cn_interface/AlipayNotifyHandle";
        WriteLog.write("alipay_pay", "localhost:" + notify_url);
        String return_url = "";
        String show_url = "";// 商品详情地址
        // 调取支付宝工具类生成订单号
        com.alipay.util.UtilDate utilDate = new com.alipay.util.UtilDate();
        // 请与贵网站生成唯一订单号匹配-调取支付宝工具类生成订单号
        String out_trade_no = utilDate.getOrderNum(ordernumberd);
        WriteLog.write("alipay_pay", "支付宝外部交易号：" + out_trade_no);
        // 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = ordernumberd + "wsd";
        // 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
        String body = "sdfasdfsdf";
        // 订单总金额，显示在支付宝收银台里的“应付总额”里
        String total_fee = "0.1";
        /*
         * 以下两个参数paymethod和defaultbank可以选择不使用，如果不使用需要注销，并在Payment类的方法中也要注销
         * 默认支付方式，四个值可选：bankPay(网银); cartoon(卡通); directPay(余额); CASH(网点支付)
         * 大额信用卡CREDITCARD
         */
        String paymethod = "directPay";
        String defaultbank = "";
        // 信用卡
        if (paymethod.equals("creditcard")) {
            paymethod = "bankPay";// 取消信用卡支付。
        }
        // 网银
        if (paymethod.equals("directPay")) {
            defaultbank = null;
        }
        // 扩展功能参数——防钓鱼功能控制参数
        // 使用一下参数需要导入dom4j包，以及打开alipay_fuction类中的parseAlipayTimestampResultXml方法
        // 若使用此参数，功能自动打开，若要关闭需要联系签约客户经理进行关闭。
        String exter_invoke_ip = "";
        String anti_phishing_key = "";
        //        if (AlipayConfig.getInstance().getAntiphishing().equals("1")) {
        //            exter_invoke_ip = "211.103.207.130";// "10.5.20.4";
        //            // //用户在外部系统创建交易时，由外部系统记录的用户IP地址
        //            try {
        //                anti_phishing_key = new Alipay_fuction().parseAlipayTimestampResultXml(new Alipay_fuction()
        //                        .creatteInvokeUrl());
        //            }
        //            catch (DocumentException e) {
        //                e.printStackTrace();
        //            } // 通过时间戳查询接口（见文档4）获取的加密支付宝系统时间戳，有效时间：30秒。
        //        }
        // 只有credit_card_pay=Y时为显示;其它情况视为不显示。
        // 说明：用于决定大额信用卡网关是否出现在收银台上（默认关闭）。
        String credit_card_pay = "N";
        // 只有
        // credit_card_default_display=N为不显示;其它情况视为显示。
        // 说明：用于决定用户没有使用过大额信用卡网关付款成功或使用招行信用卡网关付款成功的请况下，是否默认显示大额信用卡网关。如果想默认某个信用卡网银可以增加在参数defaultbank中传入银行列表的银行简码

        String credit_card_default_display = "N";
        if (paymethod.equals("creditcard")) {
            credit_card_pay = "Y";
            credit_card_default_display = "Y";
        }

        // 扩展功能参数——其他
        // String buyer_email="a@b.c";
        // 买家账户，例如：alipay@alipay.com
        // 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
        String royalty_type = "0.01";
        // 订单号和HandleName以Fg分隔开。
        String extra_common_param = ordernumberd + "Fg";
        // 支付订单记录

        // GET方式提交支付请求
        Map<String, String> params = new HashMap<String, String>();
        params.put("paygateway", paygateway);
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
        if (isNotnullorEpt(defaultbank)) {
            params.put("defaultbank", defaultbank);
        }
        params.put("_input_charset", input_charset);
        params.put("extra_common_param", extra_common_param);
        params.put("credit_card_pay", credit_card_pay);
        params.put("credit_card_default_display", credit_card_default_display);
        String ItemUrl_Get = CreateUrl(params, key);
        String msg = "{'payCallBackFlag':'0','Ordertype':'1','OrderID':'" + ordernumberd + "','Amount':'" + total_fee
                + "','PayParam':'','PayUrl':'" + ItemUrl_Get + "','WNumber':'" + searWnumber(ItemUrl_Get) + "'}";
        WriteLog.write("payurl", msg);
        System.out.println(msg);
    }

    /**
     *交通获取交易金额 
     * @param args
     * @time 2014年12月4日 下午8:01:13
     * @author wzc
     */
    public static String searWnumber(String params) {
        String retrunval = "";
        try {
            Pattern p = Pattern.compile("ord_id_ext=W\\d*");
            Matcher m = p.matcher(params);
            retrunval = "";
            if (m.find()) {
                String count = m.group();
                retrunval = count.replace("ord_id_ext=", "");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if ("".equals(retrunval)) {
            retrunval = searWnumberTradeNum(params);
        }
        return retrunval;
    }

    /**
     *交通获取交易金额 
     * @param args
     * @time 2014年12月4日 下午8:01:13
     * @author wzc
     */
    public static String searWnumberTradeNum(String params) {
        String retrunval = "";
        try {
            String[] param = params.split("&");
            for (String str : param) {
                if (str.contains("out_trade_no")) {
                    retrunval = str.replaceAll("out_trade_no=", "");
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return retrunval;
    }

    public static String CreateUrl(Map<String, String> params, String key) {
        String input_charset = params.get("_input_charset");
        String sign_type = "MD5";
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

    /*
     * (non-Javadoc)
     * 
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getRoyalty_parameters()
     * 
     * 代码格式： 收款方Email_1^金额1^备注1|收款方Email_1^收款方Email_2^金额2^备注2 功能效果：
     * 买家付出了交易金额100元，同时刻，金额1给了收款方Email_1，收款方Email_1把获得的金额中的金额2给了收款方Email_2，
     * seller_email获得剩下的金额（剩下的金额可以为0块钱）。 金额计算规则： 买家交易金额=金额1
     * +seller_email收款金额+支付宝手续费 收款方Email_1实际获得金额=金额1-金额2（金额1必须大于等于金额2）
     * 收款方Email_2实际获得金额=金额2 seller_email收款金额实际获得金额=买家交易金额-金额1-支付宝手续费
     * 
     * 
     */
    public String getRoyalty_parameters(Map<Customeragent, Float> agentroya) {

        if (agentroya != null && agentroya.size() > 0) {
            Iterator<Map.Entry<Customeragent, Float>> agentiterator = agentroya.entrySet().iterator();
            String royaltysb = "";

            Map<String, String> map = new HashMap<String, String>();
            for (; agentiterator.hasNext();) {
                Map.Entry<Customeragent, Float> entery = agentiterator.next();
                Customeragent agent = entery.getKey();
                if (1 != agent.getIspartner()) {
                    continue;
                }
                float money = entery.getValue();
                boolean hascount = (agent.getAlipayaccount() != null && !"".equals(agent.getAlipayaccount())) ? true
                        : false;

                if (agent.getId() != 46 && hascount) {
                    if (map.containsKey(agent.getAlipayaccount())) {
                        continue;
                    }
                    map.put(agent.getAlipayaccount(), "");
                    if (agentiterator.hasNext()) {
                        royaltysb = "|" + agent.getAlipayaccount() + "^" + money + "^" + agent.getCode() + "获得分润"
                                + royaltysb;
                    }
                    else {
                        royaltysb = agent.getAlipayaccount() + "^" + money + "^" + agent.getCode() + "获得分润" + royaltysb;
                    }

                }
            }
            return royaltysb.toString();
        }
        else {
            return null;
        }
        // return null;getre
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