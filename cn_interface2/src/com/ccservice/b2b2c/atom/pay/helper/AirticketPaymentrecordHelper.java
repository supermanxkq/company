package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.AirticketPaymentrecordHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class AirticketPaymentrecordHelper extends Payhelperframework implements Payhelper {
    private AirticketPaymentrecord subsidy;

    private Orderinfo orderinfo;

    public AirticketPaymentrecordHelper(long orderid) throws Exception {
        super(orderid);
        this.subsidy = Server.getInstance().getB2BAirticketService().findAirticketPaymentrecord(orderid);
        this.orderinfo = Server.getInstance().getAirService().findOrderinfo(subsidy.getOrderid());
    }

    @Override
    public String getOrderDescription() {
        return "补款订单：" + orderinfo.getOrdernumber() + ";补款金额：" + subsidy.getTradeprice();
    }

    @Override
    public String getOrdername() {
        return "机票差价补款";
    }

    @Override
    public String getOrdernumber() {
        return subsidy.getId() + "";
    }

    @Override
    public double getOrderprice() {
        return this.subsidy.getTradeprice();
    }

    @Override
    public int getTradetype() {

        return 0;
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public String getHandleName() {
        return AirticketPaymentrecordHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        return "";
    }

}
