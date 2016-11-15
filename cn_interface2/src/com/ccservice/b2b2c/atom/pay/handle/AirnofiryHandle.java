package com.ccservice.b2b2c.atom.pay.handle;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.service.IRateService;

public class AirnofiryHandle implements PayHandle {
    public static void main(String[] args) {
        try {
            try {
                ISystemService service = Server.getInstance().getSystemService();
                Orderinfo order = (Orderinfo) (Server.getInstance().getAirService()
                        .findAllOrderinfo("WHERE C_ORDERNUMBER='ZHTA201606172008501'", " order by id asc ", -1, 0)
                        .get(0));
                String autoPayResult = new AirnofiryHandle().getAutoPayRateService().AutoPay(order);// 订单自动支付
                System.out.println(autoPayResult);
            }
            catch (Exception e) {
                logger.error("自动支付异常", e);
            }

            //            AirticketPaymentrecord payment = new AirticketPaymentrecord();
            //            :? 订单号:A419910交易号:2016032800001000620089950789
            //            支付成功交易信息修改失败，异常信息:com.caucho.hessian.io.HessianFieldException: 
            //                java.lang.Throwable.cause: java.util.HashMap ({vendorCode=2627, 
            //                SQLState=23000, cause=java.sql.SQLException: 违反了 PRIMARY KEY 
            //                约束 'PK_T_AIRTICKETPAYMENTRECORD'。不能在对象 'dbo.T_AIRTICKETPAYMENTRECORD' 中插入重复键。, detailMessage=  
            //                }
            //                    --- The error occurred in com/ccservice/b2b2c/ben/AirticketPaymentrecord.xml.  
            //                    --- The error occurred while applying a parameter map.  
            //                    --- Check the createAirticketPaymentrecord-InlineParameterMap.  
            //                    --- Check the statement (update failed).  
            //                    --- Cause: java.sql.SQLException: 违反了 PRIMARY KEY 约束
            //            'PK_T_AIRTICKETPAYMENTRECORD'。不能在对象 'dbo.T_AIRTICKETPAYMENTRECORD' 中插入重复键。,
            //            next=null, stackTrace=[Ljava.lang.StackTraceElement;@213d0a7}) cannot be assigned to java.lang.Throwable
            //            payment.setOrderid(409910);
            //            payment.setYwtype(1);
            //            payment.setTradeprice(1);
            //            payment.setPaymethod(Paymentmethod.EBANKPAY);
            //            payment.setStatus(1);
            //            payment.setTradetype(AirticketPaymentrecord.USUAL);
            //            payment.setTradeno("2016032800001000620089950789");
            //            payment.setTradetime(new Timestamp(System.currentTimeMillis()));
            //            Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("111");
        }
    }

    private final static Logger logger = Logger.getLogger(AirnofiryHandle.class.getSimpleName());

    /**
     * 机票支付成功后需要处理的内容
     */
    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        WriteLog.write("alipay_TZ", "××交易成功订单处理:" + ordernumber);
        //        [2016-03-28 16:59:49.795] ××交易成功订单处理：A419894
        //        [2016-03-28 16:59:49.810] A419894{Paystatus:0;Orderstatus:1}
        try {
            ISystemService service = Server.getInstance().getSystemService();
            Orderinfo order = (Orderinfo) (Server.getInstance().getAirService()
                    .findAllOrderinfo("WHERE C_ORDERNUMBER='" + ordernumber.trim() + "'", " order by id asc ", -1, 0)
                    .get(0));
            WriteLog.write("alipay_TZ", order.getOrdernumber() + ":{Paystatus:" + order.getPaystatus()
                    + ";Orderstatus:" + order.getOrderstatus() + "}");
            //如果订单的支付状态是未支付且是等待支付状态的时候才执行下面的方法
            order.setTradeno(tradeno);
            if (order.getPaystatus() == 0 && order.getOrderstatus() == 1) {
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setTradetype(AirticketPaymentrecord.USUAL);
                payment.setTradeno(tradeno);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                double sxf = payprice - getAccountorderprice(order);
                try {
                    WriteLog.write("alipay_TZ", "订单" + ordernumber + "交易成功！");
                    String sql = "UPDATE T_ORDERINFO SET C_TRADENO='" + tradeno
                            + "', C_ORDERSTATUS=2,C_PAYSTATUS=1,C_PAYMETHOD=1,C_TOTALPAYPRICE=" + sxf + ",C_PAYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERNUMBER='"
                            + ordernumber.trim() + "'";
                    WriteLog.write("alipay_TZ", "修改订单:" + sql);
                    service.findMapResultBySql(sql, null);
                    String tsql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + ordernumber + "'";
                    service.findMapResultBySql(tsql, null);
                    createOrderrc(order);
                    WriteLog.write("alipay_TZ", "修改交易记录" + tsql);
                    WriteLog.write("alipay_TZ", "修改交易记录" + "执行自动支付");
                    try {
                        String autoPayResult = getAutoPayRateService().AutoPay(order);// 订单自动支付
                        createOrderrc(order, "autopay_result:" + autoPayResult);
                    }
                    catch (Exception e) {
                        logger.error("自动支付异常", e);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    logger.error("订单号:" + ordernumber + "交易号:" + tradeno + "支付成功交易信息修改失败，异常信息:" + e.fillInStackTrace());
                }
                //创建机票分润信息
                Server.getInstance().getB2BAirticketService().createAirtciektProfitshare(order.getId(), 1);
                //创建保险分润信息
                createAirtciektProfitshare(order.getId(), tradeno);
            }
            else if (order.getOrderstatus() == 6) {
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setTradetype(AirticketPaymentrecord.USUAL);
                payment.setTradeno(tradeno);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                createOrderrc(order);
                WriteLog.write("alipay_TZ", order.getOrdernumber() + "此订单已取消");
            }
            else if (order.getOrderstatus() == 27) {
                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(order.getId());
                payment.setYwtype(1);
                payment.setTradeprice(payprice);
                payment.setPaymethod(Paymentmethod.EBANKPAY);
                payment.setStatus(1);
                payment.setTradetype(AirticketPaymentrecord.USUAL);
                payment.setTradeno(tradeno);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                String sql = "UPDATE T_ORDERINFO SET C_ORDERSTATUS=2,C_TRADENO='" + tradeno
                        + "',C_PAYSTATUS=1,C_PAYMETHOD=1,C_PAYTIME='" + new Timestamp(System.currentTimeMillis())
                        + "' WHERE C_ORDERNUMBER='" + ordernumber.trim() + "'";
                WriteLog.write("alipay_TZ", "修改订单:" + sql);
                service.findMapResultBySql(sql, null);
            }
            else {
                //                AirticketPaymentrecord payment = new AirticketPaymentrecord();
                //                payment.setOrderid(order.getId());
                //                payment.setYwtype(1);
                //                payment.setTradeprice(payprice);
                //                payment.setPaymethod(Paymentmethod.EBANKPAY);
                //                payment.setStatus(1);
                //                payment.setTradetype(AirticketPaymentrecord.USUAL);
                //                payment.setTradeno(tradeno);
                //                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                //                Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                WriteLog.write("alipay_TZ", order.getOrdernumber() + ":此订单非待支付状态不做处理:" + order.getOrderstatus());
            }
        }
        catch (Exception ex) {
            WriteLog.write("EX", "支付通知异常:" + ex.getMessage());
            logger.error("订单号:" + ordernumber + "交易号:" + tradeno + "支付成功交易信息修改失败，异常信息:", ex);
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @return
     * @time 2016年5月11日 下午6:03:49
     * @author chendong
     */
    public IRateService getAutoPayRateService() {
        IRateService iRateService = Server.getInstance().getRateService();
        //http://www.yeebooking.com:49010
        String hthyzrateurl_rtpat = getSysConfigByProcedure("hthyzrateurl_AutoPay");
        WriteLog.write("getZratebyRTandPAT", ":" + hthyzrateurl_rtpat);
        try {
            iRateService = (IRateService) new HessianProxyFactory().create(IRateService.class, hthyzrateurl_rtpat
                    + IRateService.class.getSimpleName());
        }
        catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        return iRateService;
    }

    /**
     * 根据存储过程获取信息
     * @time 2016年3月16日 上午10:17:23
     * @author chendong
     */
    public String getSysConfigByProcedure(String name) {
        //        String procedure = "[dbo].[sp_T_SysConfig_selectByName] @name = N'IsCreateInterPnr'";
        String procedure = "[dbo].[sp_T_SysConfig_selectByName] @name = N'" + name + "'";
        List list = Server.getInstance().getSystemService().findMapResultByProcedure(procedure);
        String value = "";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            value = map.get("value") == null ? "" : map.get("value").toString();
        }
        return value;
    }

    /**
     * 创建保险分润信息
     * @param airorderid 机票订单id
     */
    private void createAirtciektProfitshare(long airorderid, String tradeno) {
        try {
            String sqls = "SELECT ID,C_TOTALMONEY FROM T_INSURORDER WHERE ID IN(SELECT C_ORDERID FROM T_INSURUSER WHERE C_TICKETORDERID="
                    + airorderid + ") ORDER BY ID DESC";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sqls, null);
            WriteLog.write("alipay_TZ", "createAirtciektProfitshare:sqls:" + sqls);
            WriteLog.write("alipay_TZ", "createAirtciektProfitshare:list.size():" + list.size());
            if (list.size() > 0) {
                Map m = (Map) list.get(0);
                String id = m.get("ID").toString();
                String C_TOTALMONEY = m.get("C_TOTALMONEY").toString();
                WriteLog.write("alipay_TZ", "T_INSURORDER=id:" + id);
                String updatesql = "update T_INSURORDER set C_PAYMETHOD=1,C_PAYSTATUS=1,C_LIUSHUINO='" + tradeno
                        + "' where ID=" + id;
                WriteLog.write("alipay_TZ", "updatetradeno:" + updatesql);
                Server.getInstance().getSystemService().findMapResultBySql(updatesql, null);
                creteinsAirticketPaymentrecord(id, C_TOTALMONEY, tradeno);
                if (id != null) {
                    long lid = Long.parseLong(id);
                    //创建保险分润信息
                    Server.getInstance().getB2BAirticketService().createAirtciektProfitshare(lid, 6);
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 创建订单资金流动记录
     * @param s_id 保险订单
     * @param s_payprice 保险总金额
     * @param tradeno 交易号
     */
    private void creteinsAirticketPaymentrecord(String s_id, String s_payprice, String tradeno) {
        try {
            Long id = Long.parseLong(s_id);
            Float payprice = Float.parseFloat(s_payprice);
            AirticketPaymentrecord payment = new AirticketPaymentrecord();
            payment.setOrderid(id);
            payment.setYwtype(6);
            payment.setTradeprice(payprice);
            payment.setPaymethod(Paymentmethod.EBANKPAY);
            payment.setStatus(1);
            payment.setTradetype(AirticketPaymentrecord.USUAL);
            payment.setTradeno(tradeno);
            payment.setTradetime(new Timestamp(System.currentTimeMillis()));
            Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
        }
        catch (Exception e) {
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

    private void createOrderrc(Orderinfo order, String content) {
        Orderinforc rc = new Orderinforc();
        rc.setOrderinfoid(order.getId());
        rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
        rc.setCustomeruserid(order.getSaleagentid());
        rc.setContent(content);
        rc.setState(2);
        try {
            Server.getInstance().getAirService().createOrderinforc(rc);
        }
        catch (SQLException e) {
            logger.error(e);
        }
    }

}
