package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.redeem.Redeem;

public class Giftpayhelper extends Payhelperframework implements Payhelper {

    private Redeem redeem;

    public Giftpayhelper(long orderid) {
        super(orderid);
        redeem = Server.getInstance().getSystemService().findRedeem(orderid);

    }

    @Override
    public String getHandleName() {
        return Giftpayhelper.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {

        return redeem.getName() + "购买";
    }

    @Override
    public String getOrdername() {

        return redeem.getName();
    }

    @Override
    public String getOrdernumber() {
        return redeem.getId() + "";
    }

    @Override
    public double getOrderprice() {
        return redeem.getGiftprice();

    }

    public static void main(String[] args) {
        System.out.println(Math.floor(9.8));
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

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
