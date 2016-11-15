package com.ccservice.b2b2c.atom.pay;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.alipay.util.UtilDate;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profitstate;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayHttpClient;
import com.tenpay.util.TenpayUtil;

/**
 * 
 * @author wzc
 * 财付通分账接口
 */
public class Tenpaydistribute {

    static final long serialVersionUID = 1L;

    static Logger logger = Logger.getLogger(Tenpaydistribute.class.getSimpleName());

    static List<Profitshare> prifitshares;

    public static String distribute(long orderid, int ywtype) {
        WriteLog.write("Tenpaydistribute_distribute", "订单" + orderid + ",业务：" + ywtype + "财付通交易异步分润开始");
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordByBtype(orderid, ywtype);
        String tradeno = "";
        double tradeprice = 0.0f;
        for (AirticketPaymentrecord pr : records) {
            if (pr.getTradetype() == AirticketPaymentrecord.USUAL) {
                tradeno = pr.getTradeno();
                tradeprice = pr.getTradeprice();
                break;
            }
        }
        String paygateway = "https://mch.tenpay.com/cgi-bin/split.cgi"; // 支付接口（不可以修改）
        String service = "3";
        String partner = TenpayConfig.getInstance().getPartnerID();
        //密钥
        String key = TenpayConfig.getInstance().getKey();
        String royalty_parameters = getRoyalty_parameters(orderid, ywtype, tradeno, tradeprice);// 分润参数明细
        WriteLog.write("Tenpaydistribute_distribute", orderid + "分润明细：" + royalty_parameters);
        if (royalty_parameters == null || royalty_parameters.length() == 0) {
            WriteLog.write("Tenpaydistribute_distribute", orderid + "不用分润");
            return "";
        }
        String moneybackstr = royalty_parameters.split("/")[0];
        boolean flag = false;
        if (moneybackstr.length() > 0) {
            //需要先退回分润
            String paygatewayback = "https://mch.tenpay.com/cgi-bin/split_rollback.cgi";//分账回退
            String cmd = "95";// 业务代码, 财付通分账回退款接口填  95  
            String transaction_id = tradeno;//必填  财付通交易号(订单号)
            String sp_billno = UtilDate.getOrderNum(orderid + "");
            String total_fee = (int) (tradeprice * 100) + "";//总金额，以分为单位  需退回的金额
            String return_urlfenzhang = "http://127.0.0.1/";
            /**
             * 退款单ID，生成规则:109＋spid+YYYYMMDD+7位流水号。如果退款单号相同，则视为同一个退款申请。
                                            冻结、分账回退、平台退款接口，如果refund_id一致，系统认为是同个完整退款的不同步骤，退款信息必须完全一致
             */
            String currentime = TenpayUtil.formatDate(new Date());
            String refund_id = "109" + partner + currentime + TenpayUtil.buildRandom(7);
            String bus_type = "97";//业务类型，整数值，代表分账回退处理规则与业务参数编码规则，暂固定为97
            /**
             * 业务参数，特定格式的字符串，格式为退款总额| (账户^退款金额)[|(账户^退款金额)]*
             */
            String bus_args = total_fee + "|" + moneybackstr;
            String version = "4";
            /**
             * 格式为：账户^信息|账户^信息，显示在对账单中。账户必须是bus_args包含的，“信息”字段长度为30字节，多个信息分别为30个字节
             */
            String rollback_desc = TenpayConfig.getInstance().getSellerEmail() + "^" + orderid + "退款分润";//分润退回字符串说明
            SortedMap<String, String> mapsharerefund = new TreeMap<String, String>();
            mapsharerefund.put("paygateway", paygatewayback);
            mapsharerefund.put("cmdno", cmd);
            mapsharerefund.put("version", version);
            mapsharerefund.put("fee_type", "1");
            mapsharerefund.put("bargainor_id", partner);
            mapsharerefund.put("sp_billno", sp_billno);
            mapsharerefund.put("transaction_id", transaction_id);
            mapsharerefund.put("return_url", return_urlfenzhang);
            mapsharerefund.put("total_fee", total_fee);
            mapsharerefund.put("refund_id", refund_id);
            mapsharerefund.put("bus_type", bus_type);
            mapsharerefund.put("bus_args", bus_args);
            mapsharerefund.put("rollback_desc", rollback_desc);
            //通信对象
            TenpayHttpClient httpClient = new TenpayHttpClient();
            //证书必须放在用户下载不到的目录，避免证书被盗取
            //设置CA证书
            httpClient.setCaInfo(new File("d:/cacert.pem"));
            //设置个人(商户)证书
            httpClient.setCertInfo(new File("d:/" + partner + ".pfx"), partner);
            try {
                String urlcontent = TenpayUtil.getRequestURL(mapsharerefund, key);
                WriteLog.write("Tenpaydistribute_distribute", "分账扣回" + urlcontent);
                httpClient.setReqContent(urlcontent);
            }
            catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            Map m = new HashMap();
            if (httpClient.call()) {
                String content = httpClient.getResContent();
                WriteLog.write("Tenpaydistribute_distribute", "分账扣回" + content);
                try {
                    m = TenpayUtil.doParse(content);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                String refundtrade = m.get("refund_id").toString();
                if ("0".equals(m.get("pay_result"))) {//分润扣回成功
                    WriteLog.write("Tenpaydistribute_distribute", "退款交易号：" + refundtrade + ",扣款字符串：" + moneybackstr);
                    flag = true;
                }
                else {
                    flag = false;//财付通分润扣回失败
                    WriteLog.write("Tenpaydistribute_distribute", tradeno + "财付通交易异步扣回失败:"
                            + m.get("pay_info").toString());
                }
            }
        }
        if (flag) {
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
            map.put("sp_billno", UtilDate.getOrderNum(orderid + ""));//商户系统内部的定单号，此参数仅在对账时提供。 
            map.put("transaction_id", trade_no);
            map.put("total_fee", (int) (tradeprice * 100) + "");//
            map.put("bus_args", royalty_parameters.split("/")[1]);
            //通信对象
            TenpayHttpClient httpClient = new TenpayHttpClient();
            //证书必须放在用户下载不到的目录，避免证书被盗取
            //设置CA证书
            httpClient.setCaInfo(new File("d:/cacert.pem"));
            //设置个人(商户)证书
            httpClient.setCertInfo(new File("d:/" + partner + ".pfx"), partner);
            try {
                httpClient.setReqContent(TenpayUtil.getRequestURL(map, key));
            }
            catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            Map m = new HashMap();
            if (httpClient.call()) {
                String content = httpClient.getResContent();
                WriteLog.write("Tenpaydistribute_distribute", content);
                try {
                    m = TenpayUtil.doParse(content);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if ("0".equals(m.get("pay_result"))) {
                    WriteLog.write("Tenpaydistribute_distribute", tradeno + "财付通交易异步分润成功");
                    if (prifitshares != null) {
                        for (Profitshare share : prifitshares) {
                            Server.getInstance()
                                    .getB2BSystemService()
                                    .profitSharesuccess(share.getId(), share.getAccount(), tradeno,
                                            Paymentmethod.EBANKPAY);
                        }
                    }
                }
                else {
                    WriteLog.write("Tenpaydistribute_distribute", tradeno + "财付通交易异步分润失败：" + m.get("pay_info"));
                    if (prifitshares != null) {
                        for (Profitshare share : prifitshares) {
                            Server.getInstance()
                                    .getB2BSystemService()
                                    .profitShareFail(share.getId(), share.getAccount(), tradeno, Paymentmethod.EBANKPAY);
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error(tradeno + "财付通交易异步分润异常", e.fillInStackTrace());
                WriteLog.write("Tenpaydistribute_distribute", tradeno + "财付通交易异步分润异常" + e.toString());
            }
            return "";
        }
        return "";
    }

    /**
     * bus_args 
    * 业务参数
    * 帐号^分帐金额^角色
    * 角色说明:    1:供应商 2:平台服务方 3:资金清算方 4:独立分润方
    * 帐号1^分帐金额1^角色1|帐号2^分帐金额2^角色2|...
     * @return
     */
    private static String getRoyalty_parameters(long orderid, int ywtype, String tradeno, double tradeprice) {
        String royaltysb = "";
        float totalmoney = 0.0f;
        Map<String, String> map = new HashMap<String, String>();
        List<Profitshare> rebates = Server.getInstance().getB2BSystemService().findAllProfitByOid(orderid, ywtype);
        for (Profitshare share : rebates) {
            if ((share.getStatus() != Profitstate.NOSHARE) || share.getPagentid() == 0) {//接口平台不在易订行分润。
                continue;
            }
            boolean shareenable = true;
            String note = "";
            Customeragent agent = (Customeragent) Server.getInstance().getMemberService()
                    .findCustomeragent(share.getPagentid());
            if ((share.getPagentid() == 46 && TenpayConfig.getInstance().selfaccount)) {// 运营商，且不分润,未签约不分润
                shareenable = false;
                Server.getInstance().getB2BSystemService()
                        .profitSharesuccess(share.getId(), agent.getTenpayaccount(), tradeno, Paymentmethod.EBANKPAY);
                continue;
            }
            if (agent == null) {
                shareenable = false;
                share.setNote("未找到此代理");
            }
            else if (agent.getTenpayaccount() == null || agent.getTenpayaccount() == "") {
                shareenable = false;
                share.setNote("账户未维护");
            }
            //            else if (agent.getIstenpaypartner() == null || agent.getIstenpaypartner() != 1) {
            //                shareenable = false;
            //                note = "账户未签约";
            //            }
            else if (agent.getIspartner() == null || agent.getIspartner() != 1) {
                shareenable = false;
                note = "账户未签约";
            }
            /*因接口平台 运营商 留点分润和 手续费分润为同一账户，不再限制分润账户重复。*/
            else if (map.containsKey(agent.getTenpayaccount())) {
                shareenable = false;
                note = "分润账户不可重复";
            }
            share.setAccount(agent.getTenpayaccount());
            if (shareenable) {
                double money = share.getProfit();
                map.put(share.getAccount(), "");
                if (royaltysb.length() > 0) {
                    royaltysb = royaltysb + "|" + share.getAccount() + "^" + (int) (money * 100) + "^3";
                }
                else {
                    royaltysb = share.getAccount() + "^" + (int) (money * 100) + "^1";
                }
                totalmoney += (int) (money * 100);
                prifitshares.add(share);
            }
            if (!shareenable) {
                Server.getInstance().getB2BSystemService()
                        .unableProfitShare(share.getId(), agent.getTenpayaccount(), note, Paymentmethod.EBANKPAY);
            }
        }
        if (royaltysb.length() > 0) {
            String culstr = TenpayConfig.getInstance().getSellerEmail() + "^" + (int) (totalmoney);
            royaltysb = culstr + "/" + royaltysb;
        }
        else {
            royaltysb = "";
        }
        return royaltysb;
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
