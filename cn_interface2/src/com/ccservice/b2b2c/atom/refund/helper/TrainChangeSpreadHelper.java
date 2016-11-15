package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.TrainChangeSpreadRefundHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class TrainChangeSpreadHelper extends Refundframework implements Refundhelper {

    AirticketPaymentrecord record;

    Trainorder trainorder;

    Trainorderchange change;

    public TrainChangeSpreadHelper(long orderid) {
        super(orderid);
        this.record = Server.getInstance().getTrainService().findTrainChangeSpreadrecord(orderid);
        this.change = Server.getInstance().getTrainService().findTrainorcerchange(orderid);
        this.trainorder = Server.getInstance().getTrainService().findTrainorderPayinfo(change.getOrderid());

    }

    @Override
    public long getOrderid() {
        return record.getId();
    }

    @Override
    public Class getProfitHandle() {
        return TrainChangeSpreadRefundHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRoyalty_parameters(null);

        refundinfo.setTradeno(trainorder.getTradeno());
        refundinfo.setRefundprice((float) record.getTradeprice());
        refundinfos.add(refundinfo);
        return refundinfos;
    }

    @Override
    public String getOldOrdId() {
        return this.trainorder.getOrdernumber();
    }

    @Override
    public String getOrdernumber() {
        return this.trainorder.getOrdernumber();
    }

    @Override
    public int getTradetype() {
        return 3;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
