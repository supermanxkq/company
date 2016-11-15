package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.Singletrippayhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Singletripapply;

public class SingletripHelper extends Payhelperframework implements Payhelper {

    private Singletripapply singletripapply;

    public SingletripHelper(long orderid) {
        super(orderid);
        singletripapply = Server.getInstance().getSingleTripInService().findSingletripapply(Long.valueOf(orderid));
    }

    @Override
    public String getHandleName() {
        return Singletrippayhandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        return "行程单购买";
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        return "行程单";
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return singletripapply.getId() + "";
    }

    @Override
    public double getOrderprice() {

        return (float) singletripapply.getApplycount();
    }

    /* 8:短信充值
     * (non-Javadoc)
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getTradetype()
     */
    @Override
    public int getTradetype() {
        return 8;
    }

    @Override
    public String getShwourl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
