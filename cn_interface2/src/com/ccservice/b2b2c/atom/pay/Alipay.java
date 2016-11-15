package com.ccservice.b2b2c.atom.pay;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.alipay.util.Alipay_fuction;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.b2b2c.util.AirUtil;
import com.pay.config.AlipayConfig;

/**
 * Servlet implementation class for Servlet: Alipay 支付宝支付类
 * 
 */
public class Alipay extends PaySupport implements Pay {
    public static final String payment_type = "1";

    public static final String paygateway = "https://mapi.alipay.com/gateway.do?"; // 支付接口（不可以修改）

    public static final String service = "create_direct_pay_by_user";// 快速付款交易服务（不可以修改）

    public Alipay(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
    }

    static final long serialVersionUID = 1L;

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public void pay(float factorage) throws Exception {
        factorage = 0;//这里要写死，是王战朝说的，我也不知道为什么，反正就是得写死、chendong 2016年4月21日18:31:42
        WriteLog.write("alipay_pay", payhelper.getOrdernumber() + "支付宝交易记录:交易开始");
        String input_charset = AlipayConfig.getInstance().getCharSet();
        String seller_email = AlipayConfig.getInstance().getSellerEmail();
        String partner = AlipayConfig.getInstance().getPartnerID();
        String key = AlipayConfig.getInstance().getKey();
        String ServerName = request.getServerName();
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
        String notify_url = "http://" + ServerName + ":" + request.getServerPort() + "/cn_interface/AlipayNotifyHandle";
        WriteLog.write("alipay_pay", "localhost:" + notify_url);
        String return_url = payhelper.getReturnurl();
        String show_url = "";// 商品详情地址
        if (payhelper.getShwourl() != null && payhelper.getShwourl().length() > 0) {
            show_url = payhelper.getShwourl();
        }
        // 调取支付宝工具类生成订单号
        com.alipay.util.UtilDate utilDate = new com.alipay.util.UtilDate();
        // 请与贵网站生成唯一订单号匹配-调取支付宝工具类生成订单号
        Traderecord traderecord = Server.getInstance().getB2BSystemService()
                .findTradeByOrderunmber(payhelper.getOrdernumber());
        String out_trade_no = utilDate.getOrderNum(payhelper.getOrdernumber());
        WriteLog.write("alipay_pay", "支付宝外部交易号：" + out_trade_no);
        if (traderecord != null) {
            out_trade_no = traderecord.getCode();
        }
        // 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = payhelper.getOrdername();
        // 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
        String body = payhelper.getOrderDescription();
        // 订单总金额，显示在支付宝收银台里的“应付总额”里
        String total_fee = String.valueOf(payhelper.getOrderprice() + factorage);
        /*
         * 以下两个参数paymethod和defaultbank可以选择不使用，如果不使用需要注销，并在Payment类的方法中也要注销
         * 默认支付方式，四个值可选：bankPay(网银); cartoon(卡通); directPay(余额); CASH(网点支付)
         * 大额信用卡CREDITCARD
         */
        String paymethod = "directPay";
        if (request.getParameter("paymethod") != null) {
            paymethod = new String(request.getParameter("paymethod").getBytes(), "UTF-8");
        }
        String defaultbank = "";
        // 信用卡
        if (paymethod.equals("creditcard")) {
            paymethod = "bankPay";// 取消信用卡支付。
        }
        if (request.getParameter("pay_bank") != null) {
            defaultbank = new String(request.getParameter("pay_bank").getBytes(), "UTF-8");
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
        if (AlipayConfig.getInstance().getAntiphishing().equals("1")) {
            exter_invoke_ip = AirUtil.getBrowserIp(request);// "10.5.20.4";
            // //用户在外部系统创建交易时，由外部系统记录的用户IP地址
            try {
                anti_phishing_key = new Alipay_fuction().parseAlipayTimestampResultXml(new Alipay_fuction()
                        .creatteInvokeUrl());
            }
            catch (DocumentException e) {
                e.printStackTrace();
            } // 通过时间戳查询接口（见文档4）获取的加密支付宝系统时间戳，有效时间：30秒。
        }
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
        String royalty_type = "10";
        // 订单号和HandleName以Fg分隔开。
        String extra_common_param = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();
        if (traderecord == null) {
            traderecord = new Traderecord();
            try {
                traderecord.setCreateuser("alipay");
                traderecord.setGoodsname(payhelper.getOrdername());
                traderecord.setCode(out_trade_no);// 外部订单号。
                traderecord.setOrdercode(payhelper.getOrdernumber());
                traderecord.setPayname("支付宝");
                traderecord.setPaytype(0);// 0支付宝 1财付通
                traderecord.setState(0);// 0等待支付1支付成功2支付失败
                traderecord.setTotalfee((int) payhelper.getOrderprice());// 支付金额分为单位
                traderecord.setType(payhelper.getTradetype());// 订单类型
                traderecord.setPaymothed(paymethod);// 支付方式
                traderecord.setBankcode(defaultbank);// 支付银行
                traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
            }
            catch (Exception e) {
                logger.info("交易记录失败", e.fillInStackTrace());
            }
        }
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
        String ItemUrl_Get = com.alipay.util.Payment.CreateUrl(params, key);
        WriteLog.write("alipay_pay", ItemUrl_Get);
        response.sendRedirect(ItemUrl_Get);

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
            System.out.println(agentroya.size());
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

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }

}