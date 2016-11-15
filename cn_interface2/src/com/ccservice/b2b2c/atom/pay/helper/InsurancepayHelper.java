package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.InsuranceMoneyHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.service.IAirService;

public class InsurancepayHelper extends Payhelperframework implements Payhelper {
    public InsurancepayHelper(long orderid) {
        super(orderid);
        this.insuranceorder = Server.getInstance().getAirService().findInsurorder(orderid);
    }

    private IAirService airservice;

    private Insurorder insuranceorder;

    @Override
    public String getHandleName() {
        return InsuranceMoneyHandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        String tempdescrip = "";
        if (insuranceorder.getInsuruserlist() != null) {
            for (int i = 0, size = insuranceorder.getInsuruserlist().size(); i < size; i++) {
                tempdescrip += insuranceorder.getInsuruserlist().get(i).getName() + ",";
            }
        }
        return this.getOrdername() + "(" + tempdescrip + ").支付金额" + insuranceorder.getTotalmoney();
    }

    @Override
    public String getOrdername() {
        return "保险购买" + insuranceorder.getInsuranceType() == null ? "" : insuranceorder.getInsuranceType()
                .getInsurancename();
    }

    @Override
    public String getOrdernumber() {
        return insuranceorder.getOrderno();
    }

    @Override
    public double getOrderprice() {
        return insuranceorder.getTotalmoney().floatValue();
    }

    @Override
    public String getShwourl() {
        //return this.getNotifyurl();
        return "";
    }

    @Override
    public int getTradetype() {
        return 7;
    }

    public IAirService getAirservice() {

        if (airservice == null) {
            airservice = Server.getInstance().getAirService();
        }
        return airservice;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
