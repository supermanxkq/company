package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class AirticketPaymentrecordHandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        logger.info(tradeno + "订单补款支付成功：" + ordernumber);
        AirticketPaymentrecord payment = new AirticketPaymentrecord();
        payment.setId(Long.valueOf(ordernumber));
        payment.setTradeprice(payprice);
        payment.setYwtype(1);
        payment.setTradetime(new Timestamp(System.currentTimeMillis()));
        payment.setStatus(1);
        payment.setPaymethod(Paymentmethod.EBANKPAY);
        payment.setTradeno(tradeno);
        Server.getInstance().getB2BAirticketService().updateAirticketPaymentrecord(payment);
        payment = Server.getInstance().getB2BAirticketService().findAirticketPaymentrecord(payment.getId());
        Server.getInstance().getB2BAirticketService().updatePassengersubidyByOid(payment.getOrderid());

    }

}
