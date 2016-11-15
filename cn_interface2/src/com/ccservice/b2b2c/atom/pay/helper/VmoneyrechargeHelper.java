package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.VmoneyrechargenotifyHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.base.service.IMemberService;

public class VmoneyrechargeHelper extends Payhelperframework implements Payhelper {

    public VmoneyrechargeHelper(long orderid) throws Exception {
        super(orderid);
        rebaterecord = this.getMemberservice().findRebaterecord(orderid);
    }

    private long orderid;

    private IMemberService service;

    private Rebaterecord rebaterecord;

    @Override
    public String getOrderDescription() {
        return "充值信息：" + this.getOrdername();
    }

    @Override
    public String getOrdername() {
        return "虚拟账户充值";
    }

    @Override
    public String getOrdernumber() {
        return "Vmoney" + rebaterecord.getId() + "";
    }

    @Override
    public double getOrderprice() {
        return this.rebaterecord.getRebatemoney();
    }

    private IMemberService getMemberservice() {
        if (this.service != null) {
            return this.service;
        }
        else {
            return service = Server.getInstance().getMemberService();
        }
    }

    @Override
    public int getTradetype() {
        return 6;
    }

    @Override
    public String getShwourl() {
        return "http://www.alhk999.com/cn_home/Orderinfo!orderdetail.jspx?id=" + this.orderid;
    }

    @Override
    public String getHandleName() {
        return VmoneyrechargenotifyHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
