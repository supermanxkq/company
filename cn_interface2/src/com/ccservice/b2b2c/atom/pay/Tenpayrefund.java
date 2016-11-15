package com.ccservice.b2b2c.atom.pay;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.IB2BSystemService;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayHttpClient;
import com.tenpay.util.TenpayUtil;

/**
 * 
 * @author wzc
 * 财付通退款接口
 *
 */
public class Tenpayrefund extends RefundSupport implements Refund {
    public static DateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmmss");

    public static DateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public Tenpayrefund(HttpServletRequest request, HttpServletResponse response, Refundhelper refundhelper) {
        super(request, response, refundhelper);
    }

    @Override
    public void refund() {
        String partner = TenpayConfig.getInstance().getPartnerID();// 合作者身份ID
        String key = TenpayConfig.getInstance().getKey();
        Refundinfo refundinfo = refundhelper.getRefundinfos().get(0);// 财付通仅支持单笔退款。
        /**
         * 交易號/退款金額/總收款金額/收款字符串/收款字符串退回#交易號/退款金額/總收款金額/收款字符串/收款字符串退回/退款说明
         * 46165165165465165/50000/2000/865845971^1000|865845972^1000/865845971^返还分润|865845972^退款
         */
        String DivDetails = getBatch_num(refundinfo);
        WriteLog.write("Tenpay退款", "分账回str," + DivDetails);
        Refundtrade refundtrade = this.createRefundtrade(DivDetails, refundhelper.getRefundinfos());
        String tradenum = DivDetails.split("/")[0];//退款交易号
        String refundprice = DivDetails.split("/")[1];//退款金额
        String moneyback = DivDetails.split("/")[2];//需收回金额
        String moneybackstr = DivDetails.split("/")[3];//收回金额字符串
        String moneybackstrdesc = DivDetails.split("/")[4];//收回金额字符串说明
        String renddesc = DivDetails.split("/")[5];//退款说明
        String fee_type = "1";//RMB人民币
        boolean flag = true;//表示分润扣回是否成功
        String sql = "select * from T_AIRTICKETPAYMENTRECORD where C_TRADENO='" + tradenum
                + "' and C_TRADENO is not null";
        double totalmoneyr = 0.0f;
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordBySql(sql);
        if (records != null && records.size() > 0) {
            totalmoneyr = records.get(0).getTradeprice();
        }
        try {
            int floatmoneyback = Integer.valueOf(moneyback);
            WriteLog.write("Tenpay退款", "分账回str," + floatmoneyback);
            if (floatmoneyback > 0) {//需要先退回分润
                String paygateway = "https://mch.tenpay.com/cgi-bin/split_rollback.cgi";//分账回退
                String cmd = "95";// 业务代码, 财付通分账回退款接口填  95  
                String transaction_id = tradenum;//必填  财付通交易号(订单号)
                if (totalmoneyr <= 0) {
                    WriteLog.write("Tenpay退款", "分账回退金额不合法," + transaction_id);
                }
                Date date = new Date(System.currentTimeMillis());
                String sp_billno = dateformat.format(date) + refundtrade.getId();//batch_no组成格式固定，用于成功通知中ID提取，请勿随意改动。
                String return_urlfenzhang = "http://127.0.0.1/";
                String total_fee = (int) (totalmoneyr * 100) + "";//总金额，以分为单位  需退回的金额
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
                String bus_args = moneyback + "|" + moneybackstr;
                String version = "4";
                /**
                 * 格式为：账户^信息|账户^信息，显示在对账单中。账户必须是bus_args包含的，“信息”字段长度为30字节，多个信息分别为30个字节
                 */
                String rollback_desc = moneybackstrdesc;//分润退回字符串说明
                SortedMap<String, String> mapsharerefund = new TreeMap<String, String>();
                mapsharerefund.put("paygateway", paygateway);
                mapsharerefund.put("cmdno", cmd);
                mapsharerefund.put("version", version);
                mapsharerefund.put("fee_type", fee_type);
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
                    String url = TenpayUtil.getRequestURL(mapsharerefund, key);
                    httpClient.setReqContent(url);
                    WriteLog.write("Tenpay退款", url);
                }
                catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                Map m = new HashMap();
                if (httpClient.call()) {
                    String content = httpClient.getResContent();
                    WriteLog.write("Tenpay退款", "分账扣回" + content);
                    try {
                        m = TenpayUtil.doParse(content);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    String tradeno = m.get("refund_id").toString();
                    IB2BSystemService service = Server.getInstance().getB2BSystemService();
                    if ("0".equals(m.get("pay_result"))) {//分润扣回成功
                        List<Profitshare> shares = service.findProfitRefundByOid(refundtrade.getOrderid(),
                                refundtrade.getBtype());
                        for (Profitshare share : shares) {
                            int pmethod = Paymentmethod.EBANKPAY;
                            service.profitRefundsuccess(share.getId(), pmethod);
                            if (TenpayConfig.getInstance().selfaccount && share.getPagentid() == 46) {
                                service.profitRefundsuccess(share.getId(), Paymentmethod.EBANKPAY);
                            }
                        }
                    }
                    else {
                        flag = false;//财付通分润扣回失败
                        WriteLog.write("Tenpay退款", tradeno + "财付通交易异步扣回失败:" + m.get("pay_info").toString());
                    }
                }
            }
            //平台进行退款操作
            if (flag) {
                String cmdno = "93";
                String refundgateway = "https://mch.tenpay.com/cgi-bin/refund_b2c_split.cgi";
                String bargainor_id = partner;//商户号
                String transaction_id = tradenum;//财付通订单号
                Date date = new Date(System.currentTimeMillis());
                String sp_billno = dateformat.format(date) + refundtrade.getId();//batch_no组成格式固定，用于成功通知中ID提取，请勿随意改动。
                //String return_url = "http://211.103.207.134:8080/cn_interface/TenpayrefundnotifyHandle";//后台调用，填写为http://127.0.0.1/
                String return_url = "http://" + request.getServerName() + ":" + request.getServerPort()
                        + "/cn_interface/TenpayrefundnotifyHandle";//后台调用，填写为http://127.0.0.1/
                String currentime = TenpayUtil.formatDate(new Date());
                /**
                 * 退款单ID，生成规则:109＋spid+YYYYMMDD+7位流水号。如果退款单号相同，则视为同一个退款申请。
                冻结、分账回退、平台退款接口，如果refund_id一致，系统认为是同个完整退款的不同步骤，退款信息必须完全一致。
                 */
                String refund_id = "109" + partner + currentime + TenpayUtil.buildRandom(7);
                String refund_fee = (int) (totalmoneyr * 100) + "";//退款金额
                String version = "4";
                String refund_desc = renddesc;
                SortedMap<String, String> refundmap = new TreeMap<String, String>();
                refundmap.put("paygateway", refundgateway);
                refundmap.put("cmdno", cmdno);
                refundmap.put("version", version);
                refundmap.put("fee_type", fee_type);
                refundmap.put("bargainor_id", bargainor_id); //商户号
                refundmap.put("sp_billno", sp_billno); //商家订单号
                refundmap.put("transaction_id", transaction_id); //财付通交易单号    
                refundmap.put("return_url", return_url); //后台系统调用，必现填写为http://127.0.0.1/
                refundmap.put("total_fee", refund_fee); //商品金额,以分为单位
                //退款ID，同个ID财付通认为是同一笔退款,格式为109+10位商户号+8位日期+7位序列号
                refundmap.put("refund_id", refund_id);
                refundmap.put("refund_fee", refundprice);
                refundmap.put("refund_desc", refund_desc);//退款说明
                //通信对象
                TenpayHttpClient httpClient = new TenpayHttpClient();
                //设置CA证书
                httpClient.setCaInfo(new File("d:/cacert.pem"));
                //设置个人(商户)证书
                httpClient.setCertInfo(new File("d:/" + partner + ".pfx"), partner);
                try {
                    String url = TenpayUtil.getRequestURL(refundmap, key);
                    httpClient.setReqContent(url);
                    WriteLog.write("Tenpay退款", "订单退款" + url);
                }
                catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                Map m = new HashMap();
                if (httpClient.call()) {
                    String content = httpClient.getResContent();
                    WriteLog.write("Tenpay退款", "订单退款" + content);
                    try {
                        m = TenpayUtil.doParse(content);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ("0".equals(m.get("pay_result"))) {
                        WriteLog.write("Tenpay退款", "财付通订单申请退款成功:" + refund_id);
                    }
                    else {
                        WriteLog.write("Tenpay退款", "财付通订单申请退款失败:" + m.get("pay_info").toString());
                    }
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("Tenpay退款", "退款异常：" + e.fillInStackTrace());
            e.printStackTrace();
        }

    }

    /**
     * 交易號/退款金額/總收款金額/收款字符串/收款字符串退回#交易號/退款金額/總收款金額/收款字符串/收款字符串退回/退款说明
     * 46165165165465165/50000.0/2000.0/865845971^1000.0|865845972^1000.0/865845971^订单退款返还分润|865845972^订单退款返还分润
     */
    public String getBatch_num(Refundinfo refundinfo) { // /
        String royaltysb = "";
        String ordernumber = refundhelper.getOrdernumber();
        float remoneytotal = 0.0f;
        String monybackstr = "";
        String monybackstrdesc = "";
        String refunddesc = "";
        royaltysb += refundinfo.getTradeno() + "/" + (int) (refundinfo.getRefundprice() * 100) + "/";
        refunddesc = ordernumber + "订单退款";
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
                monybackstr += account + "^" + (int) (money * 100) + "|";
                monybackstrdesc += account + "^" + ordernumber + "返还分润|";
                remoneytotal += money * 100;
            }
        }
        if (!"".equals(monybackstr) && monybackstr.length() > 0) {
            monybackstr = monybackstr.substring(0, monybackstr.length() - 1);
            int refundmoney = (int) (refundinfo.getRefundprice() * 100) - (int) remoneytotal;
            monybackstr = TenpayConfig.getInstance().getSellerEmail() + "^" + refundmoney + "|" + monybackstr + "";
        }
        else {
            int refundmoney = (int) (refundinfo.getRefundprice() * 100) - (int) remoneytotal;
            monybackstr = TenpayConfig.getInstance().getSellerEmail() + "^" + refundmoney;
        }

        if (!"".equals(monybackstrdesc) && monybackstrdesc.length() > 0) {
            monybackstrdesc = monybackstrdesc.substring(0, monybackstrdesc.length() - 1);
            monybackstrdesc = TenpayConfig.getInstance().getSellerEmail() + "^" + ordernumber + "返还分润|"
                    + monybackstrdesc;
        }
        else {
            monybackstrdesc = TenpayConfig.getInstance().getSellerEmail() + "^" + ordernumber + "退款";
        }
        royaltysb += (int) (refundinfo.getRefundprice() * 100) + "/" + monybackstr + "/" + monybackstrdesc + "/"
                + refunddesc;
        WriteLog.write("Tenpay退款", "退款数据集：" + royaltysb);
        return royaltysb.toString();
    }

}
