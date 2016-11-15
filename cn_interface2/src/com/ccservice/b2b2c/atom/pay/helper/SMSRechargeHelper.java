package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.SMSRechargeHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Smschargerecord;

public class SMSRechargeHelper extends Payhelperframework implements Payhelper {

    private Smschargerecord smschargerecord;

    public SMSRechargeHelper(long orderid) {
        super(orderid);
        smschargerecord = Server.getInstance().getSmschargeService().findSmsById(orderid);
    }

    @Override
    public String getHandleName() {
        return SMSRechargeHandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {

        return "短信充值";
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        return "短信充值";
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return "SMS" + smschargerecord.getId();
    }

    @Override
    public double getOrderprice() {

        return smschargerecord.getChargemoney();
    }

    /* 8:短信充值
     * (non-Javadoc)
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getTradetype()
     */
    @Override
    public int getTradetype() {
        return 5;
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
