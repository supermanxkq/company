package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.InternationalTickethandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;

public class InterNationalTickethelper extends Payhelperframework implements Payhelper {

    private Orderinfo orderinfo;

    public InterNationalTickethelper(long orderid) {
        super(orderid);
        this.orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
    }

    @Override
    public String getHandleName() {
        // TODO Auto-generated method stub
        return InternationalTickethandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        // TODO Auto-generated method stub
        return "国际机票支付！";
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        return "国际机票支付!";
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return orderinfo.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
        Float totalprice = orderinfo.getTotalticketprice() + orderinfo.getTotalfuelfee();
        return totalprice;
    }

    @Override
    public String getShwourl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Orderinfo getOrderinfo() {
        return orderinfo;
    }

    public void setOrderinfo(Orderinfo orderinfo) {
        this.orderinfo = orderinfo;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
