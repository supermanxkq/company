package com.ccservice.b2b2c.atom.pay;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alipay.util.Payment;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profitstate;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.AlipayConfig;

/**
 * @author hanmenghui 将以即时到账方式完成交易的交易金额，再分润给其他指定的支付宝账户。支持多级分润。 支付宝分润支持同步分润和异步分润。
 *         同步分润：须在支付接口提交分润信息，即在Alipay类中传递分润信息，
 *         客户支付成功后支付宝同时把钱按照所提交的分润信息分润给各支付宝帐号。
 *         异步分润：异步分润区分与同步分润，无需在支付时传递分润信息，而是在接收到支付宝支付成功通知时调用异步分润接口，在分润接口中提交
 *         分润信息进行分润。 易订行当前使用为第二种，即异步分润。
 *         为确保分润金额在退票后能收回，需要求个代理签约支付宝商圈即：Alipaypartner接口。只有代理签约了此接口方可收回代理分润。
 * 
 */
public class Alipaydistribute {
    static final long serialVersionUID = 1L;

    static Logger logger = Logger.getLogger(Alipaydistribute.class.getSimpleName());

    static List<Profitshare> prifitshares;

    public static String distribute(long orderid, int ywtype) {
        WriteLog.write("Alipaydistribute_distribute", "订单" + orderid + ",业务：" + ywtype + "支付宝交易异步分润开始");
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordByBtype(orderid, ywtype);
        String tradeno = "";
        for (AirticketPaymentrecord pr : records) {
            if (pr.getTradetype() == AirticketPaymentrecord.USUAL) {
                tradeno = pr.getTradeno();
                break;
            }
        }
        String paygateway = "https://mapi.alipay.com/gateway.do?"; // 支付接口（不可以修改）
        String service = "distribute_royalty";
        String partner = AlipayConfig.getInstance().getPartnerID();
        String input_charset = AlipayConfig.getInstance().getCharSet();
        // /String sign_type = AlipayConfig.getInstance().getSign_type();
        String out_bill_no = orderid + "";
        String royalty_type = "10";// 分润类型支付宝分润类型。 目前只支持“10”类型
        String royalty_parameters = getRoyalty_parameters(orderid, ywtype, tradeno);// 分润参数明细
        WriteLog.write("Alipaydistribute_distribute", orderid + "分润明细：" + royalty_parameters);
        if (royalty_parameters == null || royalty_parameters.length() == 0) {
            return "";
        }
        // 需要进行分润的支付宝交易号。支付交易成功后由支付宝系统返回。最短16位，最长64位。
        // 说明：trade_no和out_trade_no至少填写一项。
        String trade_no = tradeno;
        // // String out_trade_no = payhelper.getOrdernumber();// 商户网站唯一订单号
        String key = AlipayConfig.getInstance().getKey();
        Map<String, String> map = new HashMap<String, String>();
        map.put("paygateway", paygateway);
        map.put("service", service);
        map.put("_input_charset", input_charset);
        map.put("partner", partner);
        map.put("out_bill_no", out_bill_no);
        map.put("trade_no", trade_no);
        map.put("royalty_type", royalty_type);
        map.put("royalty_parameters", royalty_parameters);
        // map.put("out_trade_no", out_trade_no);
        String psb = Payment.CreateUrl(map, key);
        try {
            URL neturl = new URL(psb);
            HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            SAXReader reader = new SAXReader();
            Document document = reader.read(connection.getInputStream());
            Element root = document.getRootElement();
            String is_success = root.elementTextTrim("is_success");
            for (Profitshare share : prifitshares) {
                if (is_success.equals("T")) {
                    WriteLog.write("Alipaydistribute_distribute", tradeno + "支付宝交易异步分润成功");
                    Server.getInstance().getB2BSystemService()
                            .profitSharesuccess(share.getId(), share.getAccount(), tradeno, Paymentmethod.EBANKPAY);
                }
                else {
                    String error = root.elementTextTrim("error");
                    WriteLog.write("Alipaydistribute_distribute", tradeno + "支付宝交易异步分润失败：" + error);
                    // logger.error(error);
                    Server.getInstance().getB2BSystemService()
                            .profitShareFail(share.getId(), share.getAccount(), tradeno, Paymentmethod.EBANKPAY);
                }
            }
        }
        catch (Exception e) {
            logger.error(tradeno + "支付宝交易异步分润异常", e.fillInStackTrace());
            WriteLog.write("Alipaydistribute_distribute", tradeno + "支付宝交易异步分润异常" + e.toString());
        }
        return "";

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
     */

    private static String getRoyalty_parameters(long orderid, int ywtype, String tradeno) {
        prifitshares = new ArrayList<Profitshare>();
        String royaltysb = "";
        Map<String, String> map = new HashMap<String, String>();
        List<Profitshare> rebates = Server.getInstance().getB2BSystemService().findAllProfitByOid(orderid, ywtype);
        for (Profitshare share : rebates) {
            try {
                if ((share.getStatus() != Profitstate.NOSHARE) || share.getPagentid() == 0) {//接口平台不在易订行分润。
                    continue;
                }
                boolean shareenable = true;
                String note = "";
                Customeragent agent = (Customeragent) Server.getInstance().getMemberService()
                        .findCustomeragent(share.getPagentid());
                if ((share.getPagentid() == 46 && AlipayConfig.getInstance().selfaccount)) {// 运营商，且不分润,未签约不分润
                    shareenable = false;
                    Server.getInstance()
                            .getB2BSystemService()
                            .profitSharesuccess(share.getId(), agent.getAlipayaccount(), tradeno,
                                    Paymentmethod.EBANKPAY);
                    continue;
                }
                if (agent == null) {
                    shareenable = false;
                    share.setNote("未找到此代理");
                }
                else if (agent.getAlipayaccount() == null || agent.getAlipayaccount() == "") {
                    shareenable = false;
                    share.setNote("账户未维护");
                }
                else if (agent.getIspartner() == null || agent.getIspartner() != 1) {
                    shareenable = false;
                    note = "账户未签约";
                }
                /*因接口平台 运营商 留点分润和 手续费分润为同一账户，不再限制分润账户重复。*/
                else if (map.containsKey(agent.getAlipayaccount())) {
                    shareenable = false;
                    note = "分润账户不可重复";
                }
                share.setAccount(agent.getAlipayaccount());
                if (shareenable) {
                    double money = share.getProfit();
                    map.put(share.getAccount(), "");
                    if (royaltysb.length() > 0) {
                        royaltysb = royaltysb + "|" + share.getAccount() + "^" + money + "^" + share.getAccount()
                                + "获得分润";
                    }
                    else {
                        royaltysb = share.getAccount() + "^" + money + "^" + share.getAccount() + "获得分润" + royaltysb;
                    }
                    prifitshares.add(share);
                }
                if (!shareenable) {
                    Server.getInstance().getB2BSystemService()
                            .unableProfitShare(share.getId(), agent.getAlipayaccount(), note, Paymentmethod.EBANKPAY);
                }
            }
            catch (Exception e) {
                WriteLog.write("Alipaydistribute_distribute", "获取分润数据异常：" + e.getMessage());
                logger.error(e.fillInStackTrace());
            }
        }
        return royaltysb;
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

    public static boolean isNotnullorEpt(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }
}