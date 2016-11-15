package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profittype;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.ben.Smschargerecord;

public class SMSRechargeHandle implements PayHandle {

    private Smschargerecord smschargerecord;

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        WriteLog.write("alipay_tz_smscz", ordernumber + "充值成功");
        try {
            WriteLog.write("alipay_tz_smscz", "短信" + ordernumber + "充值成功！交易号：" + tradeno);
            synchronized (this) {
                WriteLog.write("alipay_tz_smscz", "进入加锁：" + sdf.format(new Date(System.currentTimeMillis())));
                smschargerecord = Server.getInstance().getSmschargeService()
                        .findSmsById(Long.valueOf(ordernumber.substring(3)));
                WriteLog.write("alipay_tz_smscz", "充值状态：" + smschargerecord.getStatus());
                if (smschargerecord.getStatus() == 0) {
                    AirticketPaymentrecord payment = new AirticketPaymentrecord();
                    payment.setOrderid(smschargerecord.getId());
                    payment.setYwtype(5);
                    payment.setTradeprice(smschargerecord.getChargemoney());
                    payment.setPaymethod(Paymentmethod.EBANKPAY);
                    payment.setStatus(1);
                    payment.setTradeno(tradeno);
                    payment.setTradetype(AirticketPaymentrecord.USUAL);
                    payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                    Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                    String sql = "";
                    if (smschargerecord.getDnsid().longValue() == smschargerecord.getBuyagentid().longValue()) {
                        sql = "UPDATE T_SMSCHARGERECORD SET C_STATUS=2,C_TRADENUM='" + tradeno
                                + "',C_SMSCOUNT=ISNULL(C_SMSCOUNT,0)+" + smschargerecord.getChargecount()
                                + ",C_PAYMETHOD=1 WHERE ID=" + smschargerecord.getId();
                    }
                    else {
                        sql = "UPDATE T_SMSCHARGERECORD SET C_STATUS=1,C_TRADENUM='" + tradeno
                                + "',C_SMSCOUNT=ISNULL(C_SMSCOUNT,0)+" + smschargerecord.getChargecount()
                                + ",C_PAYMETHOD=1 WHERE ID=" + smschargerecord.getId();
                    }
                    sql += ";UPDATE T_CUSTOMERAGENT SET C_SMSCOUNT=ISNULL(C_SMSCOUNT,0)+"
                            + smschargerecord.getChargecount() + " WHERE ID= " + smschargerecord.getBuyagentid();
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    WriteLog.write("alipay_tz_smscz", sql);
                    boolean flag = false;
                    if (smschargerecord.getDnsid() != null
                            && smschargerecord.getBuyagentid().longValue() != smschargerecord.getDnsid().longValue()
                            && smschargerecord.getSmspaytype() == 1) {
                        String sqltemp = "UPDATE T_CUSTOMERAGENT SET C_SMSCOUNT=ISNULL(C_SMSCOUNT,0)-"
                                + smschargerecord.getChargecount() + " WHERE ID=" + smschargerecord.getDnsid();
                        flag = true;
                        List<Profitshare> list = new ArrayList<Profitshare>();
                        Profitshare share = new Profitshare();
                        share.setTicketid(smschargerecord.getId());
                        share.setBtype(5);
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMaximumFractionDigits(2);
                        double money = smschargerecord.getChargemoney() * (1 - 0.005);
                        share.setProfit(Double.valueOf(nf.format(money)));
                        share.setPagentid(smschargerecord.getDnsid());
                        share.setPtype(Profittype.PROFITSHARE);
                        list.add(share);
                        Server.getInstance().getB2BSystemService().createProfitshares(list);
                        WriteLog.write("alipay_tz_smscz", sqltemp);
                        Server.getInstance().getSystemService().findMapResultBySql(sqltemp, null);
                        Server.getInstance().getB2BSystemService().shareProfit(smschargerecord.getId(), 5);
                    }
                    if (flag) {
                        smschargerecord.setBuyagentid(smschargerecord.getDnsid());
                        smschargerecord.setStatus(-1);
                        Server.getInstance().getVIPService().createsmschargerecord(smschargerecord);
                    }
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("alipay_tz_smscz", "支付成功交易信息修改失败，异常信息:" + e.getMessage());

        }
    }
}
