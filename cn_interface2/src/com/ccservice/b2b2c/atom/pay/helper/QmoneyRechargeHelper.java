package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.QmoneyRechargeHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.qmoneyrecharge.Qmoneyrecharge;
import com.ccservice.b2b2c.base.service.IMemberService;

public class QmoneyRechargeHelper extends Payhelperframework implements Payhelper {

    public QmoneyRechargeHelper(long orderid) {
        super(orderid);
        this.recharge = this.getMemberservice().findQmoneyrecharge(orderid);
    }

    private IMemberService memberservice;

    private Qmoneyrecharge recharge;

    @Override
    public String getHandleName() {
        return QmoneyRechargeHandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        return this.getOrdername() + ".充值金额" + recharge.getRechmoney();
    }

    @Override
    public String getOrdername() {
        return recharge.getQqnumber() + "充值";
    }

    @Override
    public String getOrdernumber() {
        return recharge.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
        return recharge.getInprice();
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public int getTradetype() {
        return 5;
    }

    public IMemberService getMemberservice() {

        if (memberservice == null) {
            memberservice = Server.getInstance().getMemberService();
        }
        return memberservice;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
