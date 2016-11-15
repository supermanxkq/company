package com.ccservice.b2b2c.atom.pay.helper;

import java.util.List;

import com.ccservice.b2b2c.atom.pay.handle.TrainfillHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.ITrainService;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class Trainpayfillhelper extends Payhelperframework implements Payhelper {

    private ITrainService service;

    private Trainorder train;

    private Trainorderchange change;

    private List<AirticketPaymentrecord> records;

    public Trainpayfillhelper(long changeid) {
        super(changeid);
        this.change = this.getTrainservice().findTrainorcerchange(changeid);
        this.train = this.getTrainservice().findTrainorder(change.getOrderid());
        String sql = "select * from T_AIRTICKETPAYMENTRECORD  where C_YWTYPE=4 and C_STATUS=0 and C_TRADETYPE=2 and C_ORDERID="
                + change.getId();
        records = Server.getInstance().getB2BSystemService().findAllPaymentrecordBySql(sql);
    }

    @Override
    public String getOrderDescription() {
        Trainticket ticket = train.getPassengers().get(0).getTraintickets().get(0);
        StringBuilder sb = new StringBuilder();
        sb.append(this.getOrdername() + "(" + ticket.getDeparture() + "-" + ticket.getArrival() + ")-补款");
        return sb.toString();
    }

    @Override
    public String getOrdername() {
        return this.change.getTcnumber();
    }

    @Override
    public String getOrdernumber() {
        return change.getId() + "";
    }

    @Override
    public double getOrderprice() {
        float money = 0.0f;
        for (AirticketPaymentrecord a : records) {
            money += a.getTradeprice();
        }
        return money;
    }

    private ITrainService getTrainservice() {
        if (this.service != null) {
            return this.service;
        }
        else {
            return service = Server.getInstance().getTrainService();
        }
    }

    @Override
    public int getTradetype() {
        return 2;
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public String getHandleName() {
        return TrainfillHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        if (records != null && records.size() > 0) {
            return records.get(0).getTradeno();
        }
        return null;
    }
}
