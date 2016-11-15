package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.Singletrippayhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Singletripapply;

public class Singletrippayhelper extends Payhelperframework implements Payhelper {

    Singletripapply tripapply = null;

    public Singletrippayhelper(long orderid) {
        super(orderid);
        this.tripapply = Server.getInstance().getSingletripService().findSingletripapply(orderid);
    }

    @Override
    public String getHandleName() {
        return Singletrippayhandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        return "行程单批量申请购买";
    }

    @Override
    public String getOrdername() {
        return "行程单购买";
    }

    @Override
    public String getOrdernumber() {
        return tripapply.getId() + "";
    }

    @Override
    public double getOrderprice() {
        return tripapply.getPrice();
    }

    @Override
    public String getShwourl() {
        return null;
    }

    @Override
    public int getTradetype() {
        return 0;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
