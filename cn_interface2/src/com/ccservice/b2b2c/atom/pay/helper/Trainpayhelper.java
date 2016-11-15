package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.TrainnofiryHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.ITrainService;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;

public class Trainpayhelper extends Payhelperframework implements Payhelper {

    public Trainpayhelper(long orderid) {
        super(orderid);
        this.train = this.getTrainservice().findTrainorder(orderid);
    }

    private ITrainService service;

    private Trainorder train;

    @Override
    public String getOrderDescription() {
        Trainticket ticket = train.getPassengers().get(0).getTraintickets().get(0);

        return "车票信息：" + this.getOrdername() + "(" + ticket.getDeparture() + "-" + ticket.getArrival() + ")";
    }

    @Override
    public String getOrdername() {
        return this.train.getOrdernumber();
    }

    @Override
    public String getOrdernumber() {
        return train.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
//        return train.getOrderprice() + train.getCommission();
        return train.getOrderprice() + train.getCommission();
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
        return 3;
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public String getHandleName() {
        return TrainnofiryHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }
}
