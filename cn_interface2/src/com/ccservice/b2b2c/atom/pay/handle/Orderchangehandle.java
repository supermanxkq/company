package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * 
 * @author wzc
 *
 */
public class Orderchangehandle implements PayHandle {

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        WriteLog.write("alipay机票变更支付通知", ordernumber + ",交易号：" + tradeno + ",价格:" + payprice);
        String seachsql = "SELECT ISNULL(C_CHANGESTATE,0) C_CHANGESTATE FROM T_ORDERCHANGE WHERE ID=" + ordernumber;
        List list = Server.getInstance().getSystemService().findMapResultBySql(seachsql, null);
        try {
            if (list != null && list.size() > 0) {
                Map map = (Map) list.get(0);
                long state = Long.valueOf(map.get("C_CHANGESTATE").toString());
                //10无法变更   11无法变更，已退款   9 变更完毕
                if (state != 9 && state != 10 && state != 11) {
                    String sql = "UPDATE T_ORDERCHANGE SET C_CHANGESTATE=8 WHERE ID=" + ordernumber;
                    AirticketPaymentrecord payment = new AirticketPaymentrecord();
                    payment.setOrderid(Long.valueOf(ordernumber));
                    payment.setYwtype(7);
                    payment.setTradeprice(payprice);
                    payment.setPaymethod(Paymentmethod.EBANKPAY);
                    payment.setStatus(1);
                    payment.setSelleremail(selleremail);
                    payment.setTradeno(tradeno);
                    payment.setTradetype(AirticketPaymentrecord.USUAL);
                    payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                    Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);

                    String updateSql = "update T_AIRTICKETPAYMENTRECORD set GusPayMethod=" + paytype
                            + " where C_ORDERID=" + ordernumber + " and C_YWTYPE=7";
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    Server.getInstance().getSystemService().findMapResultBySql(updateSql, null);
                    Orderinforc rc = new Orderinforc();
                    rc.setOrderinfoid(0l);
                    rc.setChangeid(Long.valueOf(ordernumber));
                    rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    rc.setCustomeruserid(0l);
                    rc.setContent("变更手续费支付成功，外部交易号：" + tradeno);
                    rc.setState(0);
                    try {
                        Server.getInstance().getAirService().createOrderinforc(rc);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Server.getInstance().getRateService().orderchangenotify(Long.valueOf(ordernumber),
                            "变更手续费支付成功，外部交易号：" + tradeno);
                }
                else {
                    WriteLog.write("alipay机票变更支付通知", "非可支付状态：" + state);
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("alipay机票变更支付通知", "" + e.fillInStackTrace());
            WriteLog.write("alipay机票变更支付通知", "" + e.getMessage());
        }
    }
}
