package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.Visaorderhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.visaorder.Visaorder;

public class Visaorderhepler extends Payhelperframework implements Payhelper {

    private Visaorder visaorder;

    public Visaorderhepler(long orderid) {
        super(orderid);
        this.visaorder = Server.getInstance().getVisaService().findVisaorder(orderid);
        System.out.println(visaorder.toString());
    }

    @Override
    public String getHandleName() {
        return Visaorderhandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        return "签证支付";
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        return "签证支付";
    }

    @Override
    public String getOrdernumber() {

        return visaorder.getOrderid();
    }

    @Override
    public double getOrderprice() {

        return Float.parseFloat(visaorder.getTotalprice() + "");
    }

    @Override
    public String getShwourl() {
        return null;
    }

    @Override
    public int getTradetype() {
        return 6;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
