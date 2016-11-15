package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.TrainChangenofiryHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.ITrainService;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;

public class TrainChangePayhelper extends Payhelperframework implements Payhelper {

    public TrainChangePayhelper(long changeid) {
        super(changeid);
        try {
            this.change = this.getTrainservice().findTrainOrderChangeById(changeid);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ITrainService service;

    private Trainorderchange change;

    @Override
    public String getOrderDescription() {
        Trainticket ticket = new Trainticket();
        try {
            ticket = change.getTrainpassengers().get(0).getTraintickets().get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "车票信息：" + this.getOrdername() + "(" + ticket.getDeparture() + "-" + ticket.getArrival() + ")";
    }

    @Override
    public String getOrdername() {
        return this.change.getTcnumber();
    }

    @Override
    public String getOrdernumber() {
        return this.change.getTcnumber();
    }

    @Override
    public double getOrderprice() {
        return change.getTcprocedure();
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
        return TrainChangenofiryHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }
}
