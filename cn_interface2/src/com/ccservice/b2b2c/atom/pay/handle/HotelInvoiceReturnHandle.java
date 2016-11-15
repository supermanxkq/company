package com.ccservice.b2b2c.atom.pay.handle;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelinvoice.HotelInvoice;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * 酒店补开发票
 * @author WH
 */
public class HotelInvoiceReturnHandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @SuppressWarnings("unchecked")
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        logger.info("××酒店订单补开发票交易成功订单处理：" + ordernumber);
        try {
            long InvoiceId = Long.parseLong(ordernumber.split("-")[1]);//HOTELINVOICE-ID
            HotelInvoice hotelInvoice = Server.getInstance().getHotelService().findHotelInvoice(InvoiceId);
            if (hotelInvoice != null) {
                //税费
                double moneytax = 0d;
                if (hotelInvoice.getAddmoneyflag() != null && hotelInvoice.getAddmoneyflag().intValue() == 1) {
                    moneytax = new BigDecimal(String.valueOf(hotelInvoice.getMoneytax())).doubleValue();
                }
                //快递费
                double postprice = 0d;
                if (hotelInvoice.getPosttype() != null && hotelInvoice.getPosttype().longValue() == 1) {
                    postprice = hotelInvoice.getPostprice().doubleValue();
                }
                if (moneytax > 0 || postprice > 0) {
                    //更新
                    hotelInvoice.setPaystate(1);//已支付
                    hotelInvoice.setPaymethod(1l);//网上支付
                    hotelInvoice.setTradenum(tradeno);
                    Server.getInstance().getHotelService().updateHotelInvoiceIgnoreNull(hotelInvoice);
                    //交易记录
                    String tsql = "UPDATE T_TRADERECORD SET C_STATE = 1 , C_MODIFYTIME = '"
                            + new Timestamp(System.currentTimeMillis()) + "' " + "WHERE C_TYPE = 2 AND C_ORDERCODE = '"
                            + ordernumber + "'";
                    Server.getInstance().getSystemService().findMapResultBySql(tsql, null);
                    Hotelorder hotelorder = Server.getInstance().getHotelService()
                            .findHotelorder(hotelInvoice.getOrderid().longValue());
                    if (hotelorder != null) {
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
                        //日志
                        logger.info("订单" + hotelorder.getOrderid() + "补开发票交易成功！");
                        Hotelorderrc rz = new Hotelorderrc();
                        rz.setOrderid(hotelorder.getOrderid());
                        rz.setHandleuser(hotelorder.getMemberid() + "");
                        rz.setState(1);
                        rz.setCreatetime(new Timestamp(System.currentTimeMillis()));
                        rz.setContent("完成补开发票，外部交易号：" + tradeno + "，支付金额：" + payprice);
                        Server.getInstance().getHotelService().createHotelorderrc(rz);
                        //接口订单
                        Double customerconfig = hotelorder.getCustomerconfig();
                        if ("1".equals(hotelorder.getOrdertype()) && customerconfig != null) {
                            //支付到易订行
                            if (customerconfig.doubleValue() == 1) {
                                Server.getInstance().getHotelOrderInterface()
                                        .updateHotelInvoicePay(hotelorder, hotelInvoice, 0);
                            }
                            //不支付到易订行
                            else if (customerconfig.doubleValue() == 2) {
                                String isydx = getIsYdx();
                                //易订行
                                if ("1".equals(isydx)) {
                                    hotelInvoice.setPayoffer(2);//支付供应完成
                                    Server.getInstance().getHotelService().updateHotelInvoiceIgnoreNull(hotelInvoice);
                                    //同步
                                    Server.getInstance().getHotelOrderInterface()
                                            .syncHotelInvoicePay(hotelorder, hotelInvoice, 1);
                                }
                                //非易订行
                                else if ("2".equals(isydx)) {
                                    hotelInvoice.setPayoffer(1);//需支付易订行
                                    Server.getInstance().getHotelService().updateHotelInvoiceIgnoreNull(hotelInvoice);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.info("支付成功交易信息修改失败，异常信息:" + e.getMessage());
            e.printStackTrace();
        }
    }

    //判断是否是易订行
    @SuppressWarnings("unchecked")
    private String getIsYdx() {
        List<Sysconfig> isydxs = Server.getInstance().getSystemService()
                .findAllSysconfig("where c_name='isydx'", "", -1, 0);
        if (isydxs != null && isydxs.size() == 1) {
            Sysconfig isydx = isydxs.get(0);
            return isydx.getValue();
        }
        return "";
    }

}
