package com.ccservice.b2b2c.atom.pay.helper;

import java.text.DecimalFormat;

import com.ccservice.b2b2c.atom.pay.handle.MobilRechargeHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.recharge.Recharge;
import com.ccservice.b2b2c.base.service.IMemberService;

public class MobileRechargeHelper extends Payhelperframework implements Payhelper {

    public MobileRechargeHelper(long orderid) {
        super(orderid);
        this.recharge = this.getMemberservice().findRecharge(orderid);
    }

    private IMemberService memberservice;

    private Recharge recharge;

    @Override
    public String getHandleName() {
        return MobilRechargeHandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        return this.getOrdername() + ".充值金额" + this.recharge.getRechmoney();
    }

    @Override
    public String getOrdername() {
        return this.recharge.getPhonenumber() + "充值";
    }

    @Override
    public String getOrdernumber() {
        return this.recharge.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
        return Double.parseDouble(new DecimalFormat("######0.00").format(this.recharge.getRechmoney()));//(double) this.recharge.getRechmoney();
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public int getTradetype() {
        return 4;
    }

    public IMemberService getMemberservice() {

        if (memberservice == null) {
            memberservice = Server.getInstance().getMemberService();
        }
        return memberservice;
    }

    @Override
    public String getTradeno() {
        return null;
    }
}
