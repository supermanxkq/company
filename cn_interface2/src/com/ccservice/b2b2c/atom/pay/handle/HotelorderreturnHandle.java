package com.ccservice.b2b2c.atom.pay.handle;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelinvoice.HotelInvoice;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * 
 * @author 王战朝
 * 
 */
public class HotelorderreturnHandle implements PayHandle {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    //判断是否是易订行
    public String getIsYdx() {
        List<Sysconfig> isydxs = Server.getInstance().getSystemService()
                .findAllSysconfig("where c_name='isydx'", "", -1, 0);
        if (isydxs != null && isydxs.size() == 1) {
            Sysconfig isydx = isydxs.get(0);
            return isydx.getValue();
        }
        return "";
    }

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        logger.info("××酒店交易成功订单处理：" + ordernumber);
        ISystemService service = Server.getInstance().getSystemService();
        try {
            logger.info("订单" + ordernumber + "交易成功！");
            List<Hotelorder> hotelorders = Server.getInstance().getHotelService()
                    .findAllHotelorder(" where " + Hotelorder.COL_orderid + "='" + ordernumber + "'", "", -1, 0);
            Integer stateflag = 0;
            Hotelorder hotelorder = null;
            if (hotelorders.size() > 0) {
                hotelorder = hotelorders.get(0);
                if (hotelorder.getPaystate() == 0) {
                    //如果未支付的话，更新订单状态
                    if (hotelorder.getState() == 1) {//等待确认
                        stateflag = 2;//支付成功，等待确认
                    }
                    else if (hotelorder.getState() == 4) {//预订成功，等待支付
                        stateflag = 5;//支付成功，待安排房间
                    }
                    else if (hotelorder.getState() == 7) {//预订失败支付
                        stateflag = 88;//问题订单
                    }
                    String sql = "update T_HOTELORDER set C_STATE=" + stateflag
                            + ",C_PAYSTATE=1,C_PAYMENT=1,C_TRADENUM='" + tradeno + "' where C_ORDERID='" + ordernumber
                            + "'";
                    logger.info("修改订单：" + sql);
                    service.findMapResultBySql(sql, null);
                    //发票
                    Long InvoiceId = hotelorder.getInvoiceid();
                    if (InvoiceId != null && InvoiceId.longValue() > 0) {
                        HotelInvoice hotelInvoice = Server.getInstance().getHotelService().findHotelInvoice(InvoiceId);
                        if (hotelInvoice != null) {
                            //税费
                            double moneytax = 0d;
                            if (hotelInvoice.getAddmoneyflag() != null
                                    && hotelInvoice.getAddmoneyflag().intValue() == 1) {
                                moneytax = new BigDecimal(String.valueOf(hotelInvoice.getMoneytax())).doubleValue();
                            }
                            //快递费
                            double postprice = 0d;
                            if (hotelInvoice.getPosttype() != null && hotelInvoice.getPosttype().longValue() == 1) {
                                postprice = hotelInvoice.getPostprice().doubleValue();
                            }
                            if (moneytax > 0 || postprice > 0) {
                                hotelInvoice.setPaystate(1);//已支付
                                hotelInvoice.setPaymethod(1l);//网上支付
                                hotelInvoice.setTradenum(tradeno);
                                Server.getInstance().getHotelService().updateHotelInvoiceIgnoreNull(hotelInvoice);
                            }
                        }
                    }
                    //接口订单
                    if (hotelorder.getOrdertype().equals("1")) {
                        hotelorder = Server.getInstance().getHotelService().findHotelorder(hotelorder.getId());
                        if (hotelorder.getCustomerconfig() != null) {
                            if (hotelorder.getCustomerconfig() == 1) {//支付到易订行
                                Server.getInstance().getHotelOrderInterface()
                                        .synchupdatehotelorderingnull(hotelorder, null, null, 1l);
                            }
                            else if (hotelorder.getCustomerconfig() == 2) {//不支付到易订行
                                System.out.println("支付供应到易订行……");
                                String isydx = getIsYdx();
                                if (isydx.equals("2")) {
                                    hotelorder.setPayoffer(1l);//需支付供应
                                    Server.getInstance().getHotelService().updateHotelorderIgnoreNull(hotelorder);
                                }
                                else if (isydx.equals("1")) {
                                    hotelorder.setPayoffer(2l);// 支付供应完成
                                    hotelorder.setOutorderstate(3l);// 支付供应完成
                                    System.out.println("支付成功，同步客户服务器……");
                                    Server.getInstance().getHotelService().updateHotelorderIgnoreNull(hotelorder);
                                    hotelorder = Server.getInstance().getHotelService()
                                            .findHotelorder(hotelorder.getId());
                                    Server.getInstance().getHotelOrderInterface()
                                            .synchotelorder(hotelorder, null, null, 2l);
                                }
                            }
                        }
                    }
                    String tsql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + ordernumber + "'";
                    service.findMapResultBySql(tsql, null);
                    logger.info("修改交易记录" + tsql);
                    try {
                        if (hotelorders.size() > 0) {
                            //创建支付记录
                            AirticketPaymentrecord payment = new AirticketPaymentrecord();
                            payment.setOrderid(hotelorder.getId());
                            payment.setYwtype(2);
                            payment.setTradeprice(payprice);
                            payment.setPaymethod(Paymentmethod.EBANKPAY);
                            payment.setStatus(1);
                            payment.setTradetype(AirticketPaymentrecord.USUAL);
                            payment.setTradeno(tradeno);
                            payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                            Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                            //分润
                            String sqlprofit = "UPDATE T_PROFITSHARE SET C_TRADENO='" + tradeno
                                    + "',C_PMETHOD=1 WHERE C_TICKETID=" + hotelorder.getId() + " AND C_BTYPE=2";
                            service.findMapResultBySql(sqlprofit, null);
                            //日志
                            Hotelorderrc rc = new Hotelorderrc();
                            rc.setOrderid(hotelorder.getOrderid());
                            rc.setHandleuser(hotelorder.getMemberid() + "");
                            rc.setState(stateflag);
                            rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                            rc.setContent("客户完成支付，外部交易号：" + tradeno + "，支付金额：" + payprice);//勿改日志内容
                            Server.getInstance().getHotelService().createHotelorderrc(rc);
                        }
                    }
                    catch (SQLException ex) {
                        logger.info("支付成功后发送短信失败，异常信息:" + ex.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            logger.info("支付成功交易信息修改失败，异常信息:" + e.getMessage());
            String sql = "update T_HOTELORDER set C_PAYSTATE=1 where C_ORDERID='" + ordernumber + "'";
            service.findMapResultBySql(sql, null);
            String tsql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                    + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + ordernumber + "'";
            service.findMapResultBySql(tsql, null);
            e.printStackTrace();
        }
    }
}
