package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.gp.GpUtil;
import com.ccservice.b2b2c.atom.pay.gp.certificate.GpCertificationMqUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * 代收款处理类
 * @author wzc
 *
 */
public class GpAirWithHoldHandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /**
     * 代收款处理类
     */
    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        WriteLog.write("alipay_daishou", "××交易成功订单处理:" + ordernumber + ":" + tradeno);
        try {
            ISystemService service = Server.getInstance().getSystemService();
            Orderinfo order = (Orderinfo) (Server.getInstance().getAirService()
                    .findAllOrderinfo("WHERE C_ORDERNUMBER='" + ordernumber.trim() + "'", " order by id asc ", -1, 0)
                    .get(0));
            GpCertificationMqUtil.sendMq(order.getId());
            WriteLog.write("alipay_daishou", order.getOrdernumber() + "{Paystatus:" + order.getPaystatus()
                    + ";Orderstatus:" + order.getOrderstatus() + "}");
            long daishoustate = -1;
            String selectsql = "SELECT DaiShouState FROM GpAgencyProfitRecord WITH(NOLOCK) WHERE OrderId="
                    + order.getId();
            List list = service.findMapResultBySql(selectsql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                daishoustate = Long.parseLong(map.get("DaiShouState").toString());
            }
            order.setTradeno(tradeno);
            if (order.getOrderstatus() == 3 && daishoustate != 1) {//订单已经出票 更新支付信息
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setTradetype(5);
                payment.setTradeno(tradeno);
                payment.setSelleremail(selleremail);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                double sxf = payprice - getAccountorderprice(order);
                try {
                    WriteLog.write("alipay_daishou", "订单" + ordernumber + "交易成功！");
                    String updateSql = "UPDATE GpAgencyProfitRecord SET ActualMoney=" + payprice + ",ServFee=" + sxf
                            + ",DaiShouState=1 WHERE OrderId=" + order.getId();
                    service.findMapResultBySql(updateSql, null);
                    String sql = "UPDATE T_ORDERINFO SET C_CURRPLATFEE=0,C_TRADENO='" + tradeno
                            + "',C_PAYMETHOD=1,C_TOTALPAYPRICE=" + sxf + ",C_PAYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERNUMBER='" + ordernumber.trim()
                            + "'";
                    WriteLog.write("alipay_daishou", "修改订单:" + sql);
                    service.findMapResultBySql(sql, null);
                    String tsql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + ordernumber + "'";
                    service.findMapResultBySql(tsql, null);
                    createOrderrc(order);
                    WriteLog.write("alipay_daishou", "修改交易记录" + tsql);
                    WriteLog.write("alipay_daishou", "修改交易记录" + "执行自动支付");
                    try {
                        String updateSqlrecord = "update T_AIRTICKETPAYMENTRECORD set GusPayMethod=" + paytype
                                + " where C_ORDERID=" + order.getId() + " and C_YWTYPE=1";
                        Server.getInstance().getSystemService().findMapResultBySql(updateSqlrecord, null);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //接口发送支付信息
                    String paypriceStr = formatMoney(payprice);
                    if (paytype == 17) {
                        String rate = PropertyUtil.getValue("yeePosRate", "GpAir.properties");
                        double servMoney = Math.ceil(payprice * Double.parseDouble(rate) * 10d) / 10;
                        paypriceStr = formatMoney(payprice - servMoney);
                        new GpUtil().shareGpProfit(order.getId(), 1, paypriceStr);
                    }
                    else {
                        new GpUtil().shareGpProfit(order.getId(), 0, "0");
                    }
                    GpInterFaceMethod.sendDayShouPayInfo(order.getId(), order.getOrdernumber(), tradeno, paypriceStr);

                }
                catch (Exception e) {
                    logger.error("订单号:" + ordernumber + "交易号:" + tradeno + "支付成功交易信息修改失败，异常信息:" + e.getMessage());
                }
            }
            else if (order.getOrderstatus() == 6) {
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setTradetype(5);
                payment.setTradeno(tradeno);
                payment.setSelleremail(selleremail);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                createOrderrc(order);
                WriteLog.write("alipay_daishou", order.getOrdernumber() + "此订单已取消");
            }
            else if (order.getOrderstatus() == 27) {
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setTradetype(5);
                payment.setTradeno(tradeno);
                payment.setSelleremail(selleremail);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                String sql = "UPDATE T_ORDERINFO SET C_TRADENO='" + tradeno
                        + "',C_PAYSTATUS=1,C_PAYMETHOD=1,C_PAYTIME='" + new Timestamp(System.currentTimeMillis())
                        + "' WHERE C_ORDERNUMBER='" + ordernumber.trim() + "'";
                WriteLog.write("alipay_daishou", "修改订单:" + sql);
                service.findMapResultBySql(sql, null);
            }
            else {
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setSelleremail(selleremail);
                payment.setTradetype(5);
                payment.setTradeno(tradeno);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                WriteLog.write("alipay_daishou", order.getOrdernumber() + "此订单非待支付状态不做处理:" + order.getOrderstatus());
            }
        }
        catch (Exception ex) {
            WriteLog.write("EX", "支付通知异常:" + ex.getMessage());
            logger.error("订单号:" + ordernumber + "交易号:" + tradeno + "支付成功交易信息修改失败，异常信息:" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    public String formatMoney(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    private float getAccountorderprice(Orderinfo order) {
        float totalprice = 0;
        try {
            totalprice = order.getTotalticketprice() + order.getTotalairportfee() + order.getTotalfuelfee();
            if (order.getTotalanjian() != null) {
                totalprice += order.getTotalanjian();
            }
            if (order.getTotalotherfee() != null) {
                totalprice += order.getTotalotherfee();
            }
            if (order.getTotalinsurprice() != null) {
                totalprice += order.getTotalinsurprice();
            }
            if (order.getTotalgeneralprice() != 0) {
                totalprice += order.getTotalgeneralprice();
            }
            if (order.getTotalbusinessprice() != 0) {
                totalprice += order.getTotalbusinessprice();
            }
        }
        catch (Exception e) {

        }
        return totalprice;
    }

    @SuppressWarnings("unused")
    private void createOrderrc(Orderinfo order) {
        Orderinforc rc = new Orderinforc();
        rc.setOrderinfoid(order.getId());
        rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
        rc.setCustomeruserid(order.getSaleagentid());
        rc.setContent("订票方完成支付，外部交易号：" + order.getTradeno());
        rc.setState(888);
        try {
            Server.getInstance().getAirService().createOrderinforc(rc);
        }
        catch (SQLException e) {
            logger.error(e);
        }
    }
}
