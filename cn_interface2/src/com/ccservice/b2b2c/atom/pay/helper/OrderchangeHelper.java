package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.Orderchangehandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Orderchange;

public class OrderchangeHelper extends Payhelperframework implements Payhelper {

    private Orderchange orderchange;

    public OrderchangeHelper(long orderid) {
        super(orderid);
        orderchange = Server.getInstance().getB2BAirticketService().findOrderchange(orderid);
    }

    @Override
    public String getHandleName() {
        return Orderchangehandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        return "机票并更手续费支付";
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        return "订单变更";
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return orderchange.getId() + "";
    }

    @Override
    public double getOrderprice() {

        return (float) orderchange.getRefundprice();
    }

    /* 8:短信充值
     * (non-Javadoc)
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getTradetype()
     */
    @Override
    public int getTradetype() {
        return 9;
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
